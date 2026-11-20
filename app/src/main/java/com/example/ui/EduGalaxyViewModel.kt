package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminAuditLog
import com.example.data.model.AiSession
import com.example.data.model.Announcement
import com.example.data.model.ChapterProgress
import com.example.data.model.EducationCatalog
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
import com.example.data.repository.EduAiRepository
import com.example.data.repository.EduFirestoreRepository
import com.example.data.repository.OfflinePdfManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiChatState {
    data object Idle : AiChatState
    data object Loading : AiChatState
    data class Streaming(val text: String) : AiChatState
    data class Success(val text: String) : AiChatState
    data class Error(val message: String) : AiChatState
}

data class ActiveExamSession(
    val test: MockTest,
    val questions: List<QuestionItem>,
    val currentIndex: Int = 0,
    val answers: Map<Int, String> = emptyMap(),
    val markedForReview: Set<Int> = emptySet(),
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val finishedResult: ExamResultSummary? = null
)

data class ExamResultSummary(
    val test: MockTest,
    val totalQuestions: Int,
    val attempted: Int,
    val correct: Int,
    val incorrect: Int,
    val unanswered: Int,
    val score: Double,
    val maxScore: Double,
    val accuracyPercent: Double,
    val timeTakenSeconds: Long,
    val topicStats: Map<String, Pair<Int, Int>>, // topic -> (correct, total)
    val questions: List<QuestionItem>,
    val userAnswers: Map<Int, String>
)

private const val TAG = "EduGalaxyVM"
private const val STOP_TIMEOUT_MS = 5000L

class EduGalaxyViewModel(
    private val firestoreRepo: EduFirestoreRepository,
    private val aiRepo: EduAiRepository,
    private val pdfManager: OfflinePdfManager,
    private val currentUserId: String
) : ViewModel() {

    private fun <T> Flow<T>.toSafeUiState(
        fallbackError: String,
        initial: UiState<T> = UiState.Loading
    ): StateFlow<UiState<T>> = this
        .map<T, UiState<T>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, fallbackError, rootError)
            emit(UiState.Error(rootError.localizedMessage ?: fallbackError))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = initial
        )

    val userProfileState: StateFlow<UiState<UserProfile?>> =
        firestoreRepo.observeUserProfile(currentUserId)
            .toSafeUiState("Failed to load student profile")

    val bookmarksState: StateFlow<UiState<List<UserBookmark>>> =
        firestoreRepo.observeBookmarks(currentUserId)
            .toSafeUiState("Failed to load bookmarks")

    val downloadsState: StateFlow<UiState<List<UserDownload>>> =
        firestoreRepo.observeDownloads(currentUserId)
            .toSafeUiState("Failed to load downloads")

    val testAttemptsState: StateFlow<UiState<List<TestAttempt>>> =
        firestoreRepo.observeTestAttempts(currentUserId)
            .toSafeUiState("Failed to load test attempts")

    val chapterProgressState: StateFlow<UiState<List<ChapterProgress>>> =
        firestoreRepo.observeChapterProgress(currentUserId)
            .toSafeUiState("Failed to load progress")

    val aiSessionsState: StateFlow<UiState<List<AiSession>>> =
        firestoreRepo.observeAiSessions(currentUserId)
            .toSafeUiState("Failed to load AI sessions")

    val leaderboardState: StateFlow<UiState<List<LeaderboardEntry>>> =
        firestoreRepo.observeLeaderboard()
            .toSafeUiState("Failed to load leaderboard")

    // Catalog flows combine starter verified catalog with live admin-published Firestore items
    private val _generatedQuestions = MutableStateFlow<List<QuestionItem>>(emptyList())

    val allQuestions: StateFlow<List<QuestionItem>> = combine(
        firestoreRepo.observePublishedQuestions().catch { emit(emptyList()) },
        _generatedQuestions
    ) { remote, aiLocal ->
        (aiLocal + remote + EducationCatalog.starterQuestions).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), EducationCatalog.starterQuestions)

    val allVideos: StateFlow<List<VideoLesson>> =
        firestoreRepo.observePublishedVideos()
            .catch { emit(emptyList()) }
            .map { remote -> (remote + EducationCatalog.starterVideos).distinctBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), EducationCatalog.starterVideos)

    val allPdfs: StateFlow<List<PdfMaterial>> =
        firestoreRepo.observePublishedPdfs()
            .catch { emit(emptyList()) }
            .map { remote -> (remote + EducationCatalog.starterPdfs).distinctBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), EducationCatalog.starterPdfs)

    val allMockTests: StateFlow<List<MockTest>> =
        firestoreRepo.observePublishedMockTests()
            .catch { emit(emptyList()) }
            .map { remote -> (remote + EducationCatalog.starterMockTests).distinctBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), EducationCatalog.starterMockTests)

    val allUniversities: StateFlow<List<UniversityInfo>> =
        firestoreRepo.observePublishedUniversities()
            .catch { emit(emptyList()) }
            .map { remote -> (remote + EducationCatalog.starterUniversities).distinctBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), EducationCatalog.starterUniversities)

    val announcements: StateFlow<List<Announcement>> =
        firestoreRepo.observePublishedAnnouncements()
            .catch { emit(emptyList()) }
            .map { remote -> (remote + EducationCatalog.starterAnnouncements).distinctBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), EducationCatalog.starterAnnouncements)

    val adminReports: StateFlow<List<StudentReport>> =
        firestoreRepo.observeAdminReports()
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val adminAuditLogs: StateFlow<List<AdminAuditLog>> =
        firestoreRepo.observeAdminAuditLogs()
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    // Transient UI & Feature State
    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(listOf("Quadratic Equations", "IIT Delhi", "Gauss Law", "DSA Trees"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _recentlyViewedPdfIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyViewedPdfIds: StateFlow<List<String>> = _recentlyViewedPdfIds.asStateFlow()

    private val _aiChatState = MutableStateFlow<AiChatState>(AiChatState.Idle)
    val aiChatState: StateFlow<AiChatState> = _aiChatState.asStateFlow()

    private val _aiQuestionGenLoading = MutableStateFlow(false)
    val aiQuestionGenLoading: StateFlow<Boolean> = _aiQuestionGenLoading.asStateFlow()

    private val _activeExam = MutableStateFlow<ActiveExamSession?>(null)
    val activeExam: StateFlow<ActiveExamSession?> = _activeExam.asStateFlow()

    private var timerJob: Job? = null
    private var aiJob: Job? = null

    init {
        viewModelScope.launch {
            firestoreRepo.ensureUserProfile().onFailure { e ->
                _statusBanner.value = e.localizedMessage ?: "Could not sync profile."
            }
        }
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun recordSearchQuery(query: String) {
        val clean = query.trim()
        if (clean.isNotEmpty()) {
            _recentSearches.value = (listOf(clean) + _recentSearches.value).distinct().take(8)
        }
    }

    fun updateAcademicSelection(level: String, stream: String, course: String, university: String) {
        viewModelScope.launch {
            firestoreRepo.updateAcademicSelection(level, stream, course, university)
                .onSuccess { _statusBanner.value = "Learning path updated to $level" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Failed to update level" }
        }
    }

    fun updatePreferences(language: String? = null, dailyGoal: Long? = null, reducedMotion: Boolean? = null) {
        viewModelScope.launch {
            firestoreRepo.updateUserPreferences(language, dailyGoal, reducedMotion)
                .onSuccess { _statusBanner.value = "Preferences saved" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Failed to save preferences" }
        }
    }

    fun toggleBookmark(itemType: String, itemId: String, title: String, subject: String, subtitle: String) {
        viewModelScope.launch {
            firestoreRepo.toggleBookmark(itemType, itemId, title, subject, subtitle)
                .onSuccess { saved ->
                    _statusBanner.value = if (saved) "Saved to Bookmarks" else "Removed from Bookmarks"
                }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Bookmark action failed" }
        }
    }

    fun markVideoCompleted(lesson: VideoLesson) {
        viewModelScope.launch {
            firestoreRepo.upsertChapterProgress(
                subjectId = lesson.subject.lowercase().replace(" ", "_"),
                subjectName = lesson.subject,
                chapterId = "${lesson.subject}_${lesson.chapter}".lowercase().replace(Regex("[^a-z0-9_]"), "_"),
                chapterTitle = lesson.chapter,
                videoCompleted = true
            )
            firestoreRepo.awardLearningReward(
                xpDelta = 30L,
                coinsDelta = 10L,
                studyMinutesDelta = lesson.durationMinutes.coerceAtLeast(5L),
                newBadge = "video_explorer"
            )
            _statusBanner.value = "Lesson completed! +30 XP & +10 Coins"
        }
    }

    fun recordQuestionPracticed(question: QuestionItem) {
        viewModelScope.launch {
            firestoreRepo.upsertChapterProgress(
                subjectId = question.subject.lowercase().replace(" ", "_"),
                subjectName = question.subject,
                chapterId = "${question.subject}_${question.chapter}".lowercase().replace(Regex("[^a-z0-9_]"), "_"),
                chapterTitle = question.chapter,
                questionsPracticedDelta = 1L
            )
            firestoreRepo.awardLearningReward(xpDelta = 10L, coinsDelta = 5L, studyMinutesDelta = 2L)
        }
    }

    fun markPdfViewed(pdf: PdfMaterial) {
        _recentlyViewedPdfIds.value = (listOf(pdf.id) + _recentlyViewedPdfIds.value).distinct().take(10)
        viewModelScope.launch {
            firestoreRepo.upsertChapterProgress(
                subjectId = pdf.subject.lowercase().replace(" ", "_"),
                subjectName = pdf.subject,
                chapterId = "${pdf.subject}_${pdf.chapter}".lowercase().replace(Regex("[^a-z0-9_]"), "_"),
                chapterTitle = pdf.chapter,
                notesRead = true
            )
        }
    }

    fun downloadPdfOffline(pdf: PdfMaterial) {
        viewModelScope.launch {
            try {
                val (fileName, sizeKb) = pdfManager.saveMaterialOffline(pdf)
                val record = UserDownload(
                    userId = currentUserId,
                    pdfId = pdf.id,
                    title = pdf.title,
                    category = pdf.category,
                    subject = pdf.subject,
                    localFileName = fileName,
                    sizeKb = sizeKb
                )
                firestoreRepo.recordDownload(record)
                    .onSuccess {
                        firestoreRepo.awardLearningReward(xpDelta = 15L, coinsDelta = 5L, newBadge = "offline_scholar")
                        _statusBanner.value = "Saved offline ($sizeKb KB) • +15 XP"
                    }
                    .onFailure { _statusBanner.value = it.localizedMessage ?: "Download sync failed" }
            } catch (e: Exception) {
                _statusBanner.value = e.localizedMessage ?: "Could not save offline file"
            }
        }
    }

    fun readOfflineFile(fileName: String): String? = pdfManager.readOfflineMaterial(fileName)

    fun deleteOfflineDownload(download: UserDownload) {
        viewModelScope.launch {
            pdfManager.deleteOfflineMaterial(download.localFileName)
            firestoreRepo.deleteDownloadRecord(download.id)
                .onSuccess { _statusBanner.value = "Removed from offline library" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Delete failed" }
        }
    }

    // --- AI TUTOR & PRACTICE GENERATION ---

    fun askEduGalaxyAi(prompt: String, subject: String, academicLevel: String, language: String) {
        val clean = prompt.trim()
        if (clean.isEmpty()) return
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _aiChatState.value = AiChatState.Loading
            var finalOutput = ""
            try {
                aiRepo.streamTutorResponse(clean, academicLevel, subject, language).collect { partial ->
                    finalOutput = partial
                    _aiChatState.value = AiChatState.Streaming(partial)
                }
                _aiChatState.value = AiChatState.Success(finalOutput)
                firestoreRepo.saveAiSession(subject, academicLevel, language, clean, finalOutput)
                firestoreRepo.awardLearningReward(xpDelta = 15L, coinsDelta = 5L, studyMinutesDelta = 3L, newBadge = "ai_curious")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Tutor failed", e)
                _aiChatState.value = AiChatState.Error(e.localizedMessage ?: "EduGalaxy AI failed to respond.")
            }
        }
    }

    fun generateAiPracticeQuestion(subject: String, academicLevel: String, chapter: String, difficulty: String) {
        viewModelScope.launch {
            _aiQuestionGenLoading.value = true
            aiRepo.generatePracticeQuestion(subject, academicLevel, chapter, difficulty)
                .onSuccess { generated ->
                    _generatedQuestions.value = listOf(generated) + _generatedQuestions.value
                    _statusBanner.value = "AI Practice Question added (clearly labeled AI Practice)"
                }
                .onFailure { e ->
                    _statusBanner.value = e.localizedMessage ?: "AI Question generation failed"
                }
            _aiQuestionGenLoading.value = false
        }
    }

    // --- MOCK TEST ENGINE ---

    fun startMockTest(test: MockTest) {
        timerJob?.cancel()
        val bank = allQuestions.value
        val matched = test.questionIds.mapNotNull { qid -> bank.find { it.id == qid } }
            .ifEmpty { bank.filter { it.subject.equals(test.subject, ignoreCase = true) }.take(test.questionCount.toInt()) }
            .ifEmpty { bank.take(4) }
        val totalSecs = (test.durationMinutes * 60).toInt().coerceAtLeast(60)
        _activeExam.value = ActiveExamSession(
            test = test,
            questions = matched,
            currentIndex = 0,
            remainingSeconds = totalSecs,
            totalSeconds = totalSecs
        )
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val cur = _activeExam.value ?: break
                if (cur.finishedResult != null) break
                val next = cur.remainingSeconds - 1
                if (next <= 0) {
                    submitMockTest()
                    break
                } else {
                    _activeExam.value = cur.copy(remainingSeconds = next)
                }
            }
        }
    }

    fun selectExamAnswer(questionIdx: Int, answer: String) {
        val cur = _activeExam.value ?: return
        if (cur.finishedResult != null) return
        _activeExam.value = cur.copy(answers = cur.answers + (questionIdx to answer))
    }

    fun navigateExamQuestion(index: Int) {
        val cur = _activeExam.value ?: return
        if (index in cur.questions.indices) {
            _activeExam.value = cur.copy(currentIndex = index)
        }
    }

    fun toggleMarkForReview(index: Int) {
        val cur = _activeExam.value ?: return
        val updated = if (cur.markedForReview.contains(index)) cur.markedForReview - index else cur.markedForReview + index
        _activeExam.value = cur.copy(markedForReview = updated)
    }

    fun submitMockTest() {
        timerJob?.cancel()
        val cur = _activeExam.value ?: return
        if (cur.finishedResult != null) return

        var correct = 0
        var incorrect = 0
        var score = 0.0
        var maxScore = 0.0
        val topicMap = mutableMapOf<String, Pair<Int, Int>>()

        cur.questions.forEachIndexed { index, q ->
            val qMarks = q.marks.toDouble().coerceAtLeast(1.0)
            maxScore += qMarks
            val chosen = cur.answers[index]
            val prevPair = topicMap[q.topic] ?: (0 to 0)
            if (chosen != null) {
                if (chosen == q.correctAnswer) {
                    correct++
                    score += qMarks
                    topicMap[q.topic] = (prevPair.first + 1) to (prevPair.second + 1)
                } else {
                    incorrect++
                    score -= cur.test.negativeMarking
                    topicMap[q.topic] = prevPair.first to (prevPair.second + 1)
                }
            } else {
                topicMap[q.topic] = prevPair.first to (prevPair.second + 1)
            }
        }

        val total = cur.questions.size.coerceAtLeast(1)
        val attempted = correct + incorrect
        val unanswered = (total - attempted).coerceAtLeast(0)
        val accuracy = if (attempted > 0) (correct.toDouble() / attempted.toDouble()) * 100.0 else 0.0
        val timeTaken = (cur.totalSeconds - cur.remainingSeconds).coerceAtLeast(1).toLong()
        val weakTopics = topicMap.filter { (_, pair) -> pair.first < pair.second }.keys.toList()

        val summary = ExamResultSummary(
            test = cur.test,
            totalQuestions = total,
            attempted = attempted,
            correct = correct,
            incorrect = incorrect,
            unanswered = unanswered,
            score = score,
            maxScore = maxScore.coerceAtLeast(1.0),
            accuracyPercent = accuracy,
            timeTakenSeconds = timeTaken,
            topicStats = topicMap,
            questions = cur.questions,
            userAnswers = cur.answers
        )
        _activeExam.value = cur.copy(finishedResult = summary)

        viewModelScope.launch {
            firestoreRepo.saveTestAttempt(
                TestAttempt(
                    userId = currentUserId,
                    testId = cur.test.id,
                    testTitle = cur.test.title,
                    subject = cur.test.subject,
                    totalQuestions = total.toLong(),
                    attempted = attempted.toLong(),
                    correct = correct.toLong(),
                    incorrect = incorrect.toLong(),
                    unanswered = unanswered.toLong(),
                    score = score,
                    maxScore = maxScore.coerceAtLeast(1.0),
                    accuracyPercent = accuracy,
                    timeTakenSeconds = timeTaken,
                    weakTopics = weakTopics
                )
            )
            val xpEarned = (correct * 25L + 20L).coerceAtLeast(20L)
            firestoreRepo.awardLearningReward(
                xpDelta = xpEarned,
                coinsDelta = (correct * 10L + 10L),
                studyMinutesDelta = (timeTaken / 60L).coerceAtLeast(2L),
                chaptersCompletedDelta = if (accuracy >= 70.0) 1L else 0L,
                newBadge = if (accuracy >= 80.0) "exam_ace" else "test_taker"
            )
        }
    }

    fun exitExamSession() {
        timerJob?.cancel()
        _activeExam.value = null
    }

    // --- REPORTS & ADMIN ACTIONS ---

    fun submitContentReport(targetType: String, targetId: String, reason: String) {
        viewModelScope.launch {
            firestoreRepo.submitReport(targetType, targetId, reason)
                .onSuccess { _statusBanner.value = "Report submitted for admin review" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Failed to submit report" }
        }
    }

    fun adminSaveQuestion(item: QuestionItem) {
        viewModelScope.launch {
            firestoreRepo.adminSaveQuestion(item)
                .onSuccess { _statusBanner.value = "Question saved to Firestore" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Admin write failed" }
        }
    }

    fun adminDeleteQuestion(id: String) {
        viewModelScope.launch {
            firestoreRepo.adminDeleteQuestion(id)
                .onSuccess { _statusBanner.value = "Question deleted" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Delete failed" }
        }
    }

    fun adminPublishVideo(video: VideoLesson) {
        viewModelScope.launch {
            firestoreRepo.adminCreateVideo(video)
                .onSuccess { _statusBanner.value = "Video lesson published" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Publish failed" }
        }
    }

    fun adminPublishPdf(pdf: PdfMaterial) {
        viewModelScope.launch {
            firestoreRepo.adminCreatePdf(pdf)
                .onSuccess { _statusBanner.value = "Study PDF published" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Publish failed" }
        }
    }

    fun adminPublishTest(test: MockTest) {
        viewModelScope.launch {
            firestoreRepo.adminCreateMockTest(test)
                .onSuccess { _statusBanner.value = "Mock test published" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Publish failed" }
        }
    }

    fun adminBroadcastAnnouncement(title: String, message: String, category: String) {
        viewModelScope.launch {
            firestoreRepo.adminCreateAnnouncement(
                Announcement(title = title, message = message, category = category, published = true)
            )
                .onSuccess { _statusBanner.value = "Notification broadcast sent" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Broadcast failed" }
        }
    }

    fun adminResolveReport(reportId: String, status: String) {
        viewModelScope.launch {
            firestoreRepo.adminResolveReport(reportId, status)
                .onSuccess { _statusBanner.value = "Report marked $status" }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Action failed" }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiJob?.cancel()
    }
}
