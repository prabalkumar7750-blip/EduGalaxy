package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.core.OperationType
import com.example.core.handleFirestoreError
import com.example.data.model.AdminAuditLog
import com.example.data.model.AiSession
import com.example.data.model.Announcement
import com.example.data.model.ChapterProgress
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MockTest
import com.example.data.model.PdfMaterial
import com.example.data.model.QuestionItem
import com.example.data.model.StudentReport
import com.example.data.model.TestAttempt
import com.example.data.model.UniversityInfo
import com.example.data.model.UserBookmark
import com.example.data.model.UserDownload
import com.example.data.model.UserProfile
import com.example.data.model.VideoLesson
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EduFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun <T> Flow<T>.handleSnapshotErrors(op: OperationType, path: String, expectedUid: String? = null): Flow<T> =
        this.catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val exceptionToLog = firestoreError ?: (error as? Exception)
            if (exceptionToLog != null && (expectedUid == null || auth.currentUser?.uid == expectedUid)) {
                handleFirestoreError(exceptionToLog, op, path)
            }
            throw error
        }

    // --- USER PROFILE & INITIALIZATION ---

    suspend fun ensureUserProfile(): Result<UserProfile> {
        val user = auth.currentUser ?: return Result.failure(IllegalStateException("Not signed in"))
        val uid = user.uid
        val path = "users/$uid"
        return try {
            val docRef = db.collection("users").document(uid)
            val snap = docRef.get().await()
            val isBootstrappedAdmin = user.email == "prabalkumar7750@gmail.com" && user.isEmailVerified
            if (snap.exists()) {
                val existing = snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    ?: UserProfile(userId = uid, displayName = user.displayName ?: "Student", email = user.email ?: "")
                // Sync leaderboard entry cleanly
                syncLeaderboardEntry(existing)
                Result.success(existing)
            } else {
                val initialProfile = UserProfile(
                    userId = uid,
                    displayName = (user.displayName ?: "EduGalaxy Learner").take(80),
                    email = (user.email ?: "$uid@edugalaxy.app").take(120),
                    photoUrl = user.photoUrl?.toString()?.take(500),
                    role = if (isBootstrappedAdmin) "admin" else "student",
                    selectedLevel = "Class 10",
                    selectedStream = "General",
                    selectedCourse = "School (Class 1–12)",
                    selectedUniversity = "CBSE / NCERT",
                    preferredLanguage = "Bilingual",
                    xp = 50L,
                    coins = 50L,
                    streakDays = 1L,
                    dailyStudyMinutes = 0L,
                    dailyGoalMinutes = 45L,
                    completedChaptersCount = 0L,
                    reducedMotion = false,
                    badges = listOf("galaxy_pioneer")
                )
                docRef.set(initialProfile.toCreateMap()).await()
                syncLeaderboardEntry(initialProfile)
                Result.success(initialProfile)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { it.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.GET, path, userId)
        )
    }

    suspend fun updateAcademicSelection(
        level: String,
        stream: String,
        course: String,
        university: String
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "users/$uid"
        return try {
            db.collection("users").document(uid).update(
                mapOf(
                    "selectedLevel" to level.take(60),
                    "selectedStream" to stream.take(60),
                    "selectedCourse" to course.take(80),
                    "selectedUniversity" to university.take(120),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            db.collection("leaderboard").document(uid).update(
                mapOf(
                    "selectedLevel" to level.take(60),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateUserPreferences(
        preferredLanguage: String? = null,
        dailyGoalMinutes: Long? = null,
        reducedMotion: Boolean? = null
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "users/$uid"
        return try {
            val updates = mutableMapOf<String, Any>("updatedAt" to FieldValue.serverTimestamp())
            preferredLanguage?.let {
                if (it in listOf("English", "Hindi", "Bilingual")) updates["preferredLanguage"] = it
            }
            dailyGoalMinutes?.let { updates["dailyGoalMinutes"] = it.coerceIn(5L, 600L) }
            reducedMotion?.let { updates["reducedMotion"] = it }
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun awardLearningReward(
        xpDelta: Long,
        coinsDelta: Long,
        studyMinutesDelta: Long = 0L,
        chaptersCompletedDelta: Long = 0L,
        newBadge: String? = null
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "users/$uid"
        return try {
            val docRef = db.collection("users").document(uid)
            val snap = docRef.get().await()
            val profile = snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: return Result.failure(IllegalStateException("Profile not found"))
            val updatedXp = (profile.xp + xpDelta).coerceAtLeast(0L)
            val updatedCoins = (profile.coins + coinsDelta).coerceAtLeast(0L)
            val updatedMinutes = (profile.dailyStudyMinutes + studyMinutesDelta).coerceIn(0L, 1440L)
            val updatedChapters = (profile.completedChaptersCount + chaptersCompletedDelta).coerceAtLeast(0L)
            val updatedBadges = if (newBadge != null && !profile.badges.contains(newBadge) && profile.badges.size < 40) {
                profile.badges + newBadge
            } else {
                profile.badges
            }
            docRef.update(
                mapOf(
                    "xp" to updatedXp,
                    "coins" to updatedCoins,
                    "dailyStudyMinutes" to updatedMinutes,
                    "completedChaptersCount" to updatedChapters,
                    "badges" to updatedBadges,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            db.collection("leaderboard").document(uid).update(
                mapOf(
                    "xp" to updatedXp,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    private suspend fun syncLeaderboardEntry(profile: UserProfile) {
        val lbRef = db.collection("leaderboard").document(profile.userId)
        val snap = lbRef.get().await()
        if (snap.exists()) {
            lbRef.update(
                mapOf(
                    "displayName" to profile.displayName.take(80),
                    "selectedLevel" to profile.selectedLevel.take(60),
                    "xp" to profile.xp,
                    "streakDays" to profile.streakDays,
                    "publicOptIn" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            lbRef.set(
                mapOf(
                    "userId" to profile.userId,
                    "displayName" to profile.displayName.take(80),
                    "selectedLevel" to profile.selectedLevel.take(60),
                    "xp" to profile.xp,
                    "streakDays" to profile.streakDays,
                    "publicOptIn" to true,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    // --- USER SUBCOLLECTIONS ---

    fun observeBookmarks(userId: String): Flow<List<UserBookmark>> = flow {
        val path = "users/$userId/bookmarks"
        emitAll(
            db.collection("users").document(userId).collection("bookmarks")
                .snapshots()
                .map { it.toObjects(UserBookmark::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, path, userId)
        )
    }

    suspend fun toggleBookmark(
        itemType: String,
        itemId: String,
        title: String,
        subject: String,
        subtitle: String
    ): Result<Boolean> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val safeDocId = "${itemType}_${itemId}".replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
        val path = "users/$uid/bookmarks/$safeDocId"
        return try {
            val docRef = db.collection("users").document(uid).collection("bookmarks").document(safeDocId)
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.delete().await()
                Result.success(false)
            } else {
                val bookmark = UserBookmark(
                    userId = uid,
                    itemType = itemType,
                    itemId = itemId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120),
                    title = title,
                    subject = subject,
                    subtitle = subtitle
                )
                docRef.set(bookmark.toCreateMap()).await()
                Result.success(true)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeDownloads(userId: String): Flow<List<UserDownload>> = flow {
        val path = "users/$userId/downloads"
        emitAll(
            db.collection("users").document(userId).collection("downloads")
                .snapshots()
                .map { it.toObjects(UserDownload::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, path, userId)
        )
    }

    suspend fun recordDownload(download: UserDownload): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val safeDocId = download.pdfId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
        val path = "users/$uid/downloads/$safeDocId"
        return try {
            val docRef = db.collection("users").document(uid).collection("downloads").document(safeDocId)
            docRef.set(download.copy(userId = uid, pdfId = safeDocId).toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteDownloadRecord(downloadId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "users/$uid/downloads/$downloadId"
        return try {
            db.collection("users").document(uid).collection("downloads").document(downloadId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    fun observeTestAttempts(userId: String): Flow<List<TestAttempt>> = flow {
        val path = "users/$userId/test_attempts"
        emitAll(
            db.collection("users").document(userId).collection("test_attempts")
                .snapshots()
                .map { it.toObjects(TestAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, path, userId)
        )
    }

    suspend fun saveTestAttempt(attempt: TestAttempt): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val attemptId = "att_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val path = "users/$uid/test_attempts/$attemptId"
        return try {
            db.collection("users").document(uid).collection("test_attempts").document(attemptId)
                .set(attempt.copy(userId = uid).toCreateMap())
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeChapterProgress(userId: String): Flow<List<ChapterProgress>> = flow {
        val path = "users/$userId/progress"
        emitAll(
            db.collection("users").document(userId).collection("progress")
                .snapshots()
                .map { it.toObjects(ChapterProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, path, userId)
        )
    }

    suspend fun upsertChapterProgress(
        subjectId: String,
        subjectName: String,
        chapterId: String,
        chapterTitle: String,
        videoCompleted: Boolean? = null,
        notesRead: Boolean? = null,
        questionsPracticedDelta: Long = 0L
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val safeDocId = chapterId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
        val path = "users/$uid/progress/$safeDocId"
        return try {
            val docRef = db.collection("users").document(uid).collection("progress").document(safeDocId)
            val snap = docRef.get().await()
            if (snap.exists()) {
                val cur = snap.toObject(ChapterProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)!!
                val newVideo = videoCompleted ?: cur.videoCompleted
                val newNotes = notesRead ?: cur.notesRead
                val newQ = (cur.questionsPracticed + questionsPracticedDelta).coerceAtLeast(0L)
                val pct = ((if (newVideo) 35 else 0) + (if (newNotes) 35 else 0) + if (newQ >= 2) 30 else (newQ * 15).toInt()).coerceIn(0, 100).toLong()
                docRef.update(
                    mapOf(
                        "videoCompleted" to newVideo,
                        "notesRead" to newNotes,
                        "questionsPracticed" to newQ,
                        "progressPercent" to pct,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val newVideo = videoCompleted ?: false
                val newNotes = notesRead ?: false
                val newQ = questionsPracticedDelta.coerceAtLeast(0L)
                val pct = ((if (newVideo) 35 else 0) + (if (newNotes) 35 else 0) + if (newQ >= 2) 30 else (newQ * 15).toInt()).coerceIn(0, 100).toLong()
                val model = ChapterProgress(
                    userId = uid,
                    subjectId = subjectId,
                    subjectName = subjectName,
                    chapterId = safeDocId,
                    chapterTitle = chapterTitle,
                    progressPercent = pct,
                    videoCompleted = newVideo,
                    notesRead = newNotes,
                    questionsPracticed = newQ
                )
                docRef.set(model.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeAiSessions(userId: String): Flow<List<AiSession>> = flow {
        val path = "users/$userId/ai_sessions"
        emitAll(
            db.collection("users").document(userId).collection("ai_sessions")
                .snapshots()
                .map { it.toObjects(AiSession::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, path, userId)
        )
    }

    suspend fun saveAiSession(
        subject: String,
        academicLevel: String,
        language: String,
        prompt: String,
        response: String
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val sessionId = "ai_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val path = "users/$uid/ai_sessions/$sessionId"
        return try {
            val session = AiSession(
                userId = uid,
                subject = subject,
                academicLevel = academicLevel,
                language = language,
                prompt = prompt,
                response = response
            )
            db.collection("users").document(uid).collection("ai_sessions").document(sessionId)
                .set(session.toCreateMap())
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // --- SHARED CATALOG & LEADERBOARD FLOWS ---

    fun observeLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("leaderboard")
                .whereEqualTo("publicOptIn", true)
                .snapshots()
                .map { snap ->
                    snap.toObjects(LeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.xp }
                }
                .handleSnapshotErrors(OperationType.LIST, "leaderboard", uid)
        )
    }

    fun observePublishedQuestions(): Flow<List<QuestionItem>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("questions")
                .whereEqualTo("published", true)
                .snapshots()
                .map { it.toObjects(QuestionItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "questions", uid)
        )
    }

    fun observePublishedVideos(): Flow<List<VideoLesson>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("videos")
                .whereEqualTo("published", true)
                .snapshots()
                .map { it.toObjects(VideoLesson::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "videos", uid)
        )
    }

    fun observePublishedPdfs(): Flow<List<PdfMaterial>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("pdf_materials")
                .whereEqualTo("published", true)
                .snapshots()
                .map { it.toObjects(PdfMaterial::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "pdf_materials", uid)
        )
    }

    fun observePublishedMockTests(): Flow<List<MockTest>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("mock_tests")
                .whereEqualTo("published", true)
                .snapshots()
                .map { it.toObjects(MockTest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "mock_tests", uid)
        )
    }

    fun observePublishedUniversities(): Flow<List<UniversityInfo>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("universities")
                .whereEqualTo("published", true)
                .snapshots()
                .map { it.toObjects(UniversityInfo::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "universities", uid)
        )
    }

    fun observePublishedAnnouncements(): Flow<List<Announcement>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("announcements")
                .whereEqualTo("published", true)
                .snapshots()
                .map { it.toObjects(Announcement::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "announcements", uid)
        )
    }

    // --- STUDENT REPORTS ---

    suspend fun submitReport(targetType: String, targetId: String, reason: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val reportId = "rep_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val path = "student_reports/$reportId"
        return try {
            require(reason.trim().length >= 3) { "Please describe the issue (at least 3 characters)." }
            db.collection("student_reports").document(reportId).set(
                mapOf(
                    "userId" to uid,
                    "targetType" to targetType.take(60),
                    "targetId" to targetId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128),
                    "reason" to reason.trim().take(500),
                    "status" to "open",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // --- ADMIN OPERATIONS (Role-Gated by Firestore Rules) ---

    fun observeAdminReports(): Flow<List<StudentReport>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("student_reports")
                .snapshots()
                .map { it.toObjects(StudentReport::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "student_reports", uid)
        )
    }

    fun observeAdminAuditLogs(): Flow<List<AdminAuditLog>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("admin_audit_logs")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .snapshots()
                .map { it.toObjects(AdminAuditLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .handleSnapshotErrors(OperationType.LIST, "admin_audit_logs", uid)
        )
    }

    private suspend fun writeAuditLog(action: String, collection: String, targetId: String, summary: String) {
        val user = auth.currentUser ?: return
        val logId = "log_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        db.collection("admin_audit_logs").document(logId).set(
            mapOf(
                "adminId" to user.uid,
                "adminEmail" to (user.email ?: "admin@edugalaxy.app").take(120),
                "action" to action.take(80),
                "targetCollection" to collection.take(80),
                "targetId" to targetId.take(128),
                "summary" to summary.take(500),
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun adminSaveQuestion(question: QuestionItem): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val docId = question.id.ifBlank { "q_${UUID.randomUUID().toString().replace("-", "").take(14)}" }
        val path = "questions/$docId"
        return try {
            val existing = db.collection("questions").document(docId).get().await()
            if (existing.exists()) {
                db.collection("questions").document(docId).update(
                    mapOf(
                        "subject" to question.subject.take(80),
                        "academicLevel" to question.academicLevel.take(80),
                        "chapter" to question.chapter.take(150),
                        "topic" to question.topic.take(150),
                        "questionType" to question.questionType.take(60),
                        "difficulty" to question.difficulty,
                        "marks" to question.marks,
                        "year" to question.year.take(40),
                        "source" to question.source.take(160),
                        "statement" to question.statement.take(2000),
                        "options" to question.options.take(6),
                        "correctAnswer" to question.correctAnswer.take(500),
                        "explanationEn" to question.explanationEn.take(4000),
                        "explanationHi" to question.explanationHi.take(4000),
                        "aiGenerated" to question.aiGenerated,
                        "repeatedTag" to question.repeatedTag,
                        "published" to question.published,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                writeAuditLog("UPDATE_QUESTION", "questions", docId, "Updated ${question.subject} question")
            } else {
                db.collection("questions").document(docId).set(question.toCreateMap(uid)).await()
                writeAuditLog("CREATE_QUESTION", "questions", docId, "Created ${question.subject} question")
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun adminDeleteQuestion(questionId: String): Result<Unit> {
        val path = "questions/$questionId"
        return try {
            db.collection("questions").document(questionId).delete().await()
            writeAuditLog("DELETE_QUESTION", "questions", questionId, "Deleted question $questionId")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun adminCreateVideo(video: VideoLesson): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val docId = "vid_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val path = "videos/$docId"
        return try {
            db.collection("videos").document(docId).set(video.toCreateMap(uid)).await()
            writeAuditLog("CREATE_VIDEO", "videos", docId, "Published video: ${video.title}")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminCreatePdf(pdf: PdfMaterial): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val docId = "pdf_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val path = "pdf_materials/$docId"
        return try {
            db.collection("pdf_materials").document(docId).set(pdf.toCreateMap(uid)).await()
            writeAuditLog("CREATE_PDF", "pdf_materials", docId, "Published PDF: ${pdf.title}")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminCreateMockTest(test: MockTest): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val docId = "test_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val path = "mock_tests/$docId"
        return try {
            db.collection("mock_tests").document(docId).set(test.toCreateMap(uid)).await()
            writeAuditLog("CREATE_MOCK_TEST", "mock_tests", docId, "Created mock test: ${test.title}")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminCreateAnnouncement(announcement: Announcement): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val docId = "ann_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val path = "announcements/$docId"
        return try {
            db.collection("announcements").document(docId).set(announcement.toCreateMap(uid)).await()
            writeAuditLog("CREATE_ANNOUNCEMENT", "announcements", docId, "Broadcast: ${announcement.title}")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminResolveReport(reportId: String, newStatus: String): Result<Unit> {
        val path = "student_reports/$reportId"
        return try {
            db.collection("student_reports").document(reportId).update(
                mapOf("status" to newStatus, "updatedAt" to FieldValue.serverTimestamp())
            ).await()
            writeAuditLog("RESOLVE_REPORT", "student_reports", reportId, "Marked report $newStatus")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }
}
