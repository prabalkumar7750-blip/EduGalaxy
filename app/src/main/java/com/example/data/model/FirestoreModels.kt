package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Student",
    val email: String = "",
    val photoUrl: String? = null,
    val role: String = "student",
    val selectedLevel: String = "Class 10",
    val selectedStream: String = "General",
    val selectedCourse: String = "School (Class 1–12)",
    val selectedUniversity: String = "CBSE / NCERT",
    val preferredLanguage: String = "Bilingual",
    val xp: Long = 0L,
    val coins: Long = 50L,
    val streakDays: Long = 1L,
    val dailyStudyMinutes: Long = 0L,
    val dailyGoalMinutes: Long = 45L,
    val completedChaptersCount: Long = 0L,
    val reducedMotion: Boolean = false,
    val badges: List<String> = listOf("galaxy_pioneer"),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "displayName" to displayName.take(80).ifBlank { "Student" },
        "email" to email.take(120),
        "photoUrl" to photoUrl?.take(500),
        "role" to if (role == "admin") "admin" else "student",
        "selectedLevel" to selectedLevel.take(60),
        "selectedStream" to selectedStream.take(60),
        "selectedCourse" to selectedCourse.take(80),
        "selectedUniversity" to selectedUniversity.take(120),
        "preferredLanguage" to if (preferredLanguage in listOf("English", "Hindi", "Bilingual")) preferredLanguage else "Bilingual",
        "xp" to xp.coerceAtLeast(0L),
        "coins" to coins.coerceAtLeast(0L),
        "streakDays" to streakDays.coerceAtLeast(0L),
        "dailyStudyMinutes" to dailyStudyMinutes.coerceIn(0L, 1440L),
        "dailyGoalMinutes" to dailyGoalMinutes.coerceIn(5L, 600L),
        "completedChaptersCount" to completedChaptersCount.coerceAtLeast(0L),
        "reducedMotion" to reducedMotion,
        "badges" to badges.take(40),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    ).filterValues { it != null }.mapValues { it.value!! }
}

@Keep
@IgnoreExtraProperties
data class UserBookmark(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemType: String = "question",
    val itemId: String = "",
    val title: String = "",
    val subject: String = "",
    val subtitle: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "itemType" to itemType,
        "itemId" to itemId,
        "title" to title.take(200),
        "subject" to subject.take(80),
        "subtitle" to subtitle.take(200),
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class UserDownload(
    @DocumentId val id: String = "",
    val userId: String = "",
    val pdfId: String = "",
    val title: String = "",
    val category: String = "",
    val subject: String = "",
    val localFileName: String = "",
    val sizeKb: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "pdfId" to pdfId,
        "title" to title.take(200),
        "category" to category.take(80),
        "subject" to subject.take(80),
        "localFileName" to localFileName.take(200),
        "sizeKb" to sizeKb.coerceAtLeast(0L),
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class TestAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val testId: String = "",
    val testTitle: String = "",
    val subject: String = "",
    val totalQuestions: Long = 1L,
    val attempted: Long = 0L,
    val correct: Long = 0L,
    val incorrect: Long = 0L,
    val unanswered: Long = 0L,
    val score: Double = 0.0,
    val maxScore: Double = 1.0,
    val accuracyPercent: Double = 0.0,
    val timeTakenSeconds: Long = 0L,
    val weakTopics: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "testId" to testId,
        "testTitle" to testTitle.take(200),
        "subject" to subject.take(80),
        "totalQuestions" to totalQuestions.coerceAtLeast(1L),
        "attempted" to attempted.coerceIn(0L, totalQuestions),
        "correct" to correct.coerceIn(0L, totalQuestions),
        "incorrect" to incorrect.coerceIn(0L, totalQuestions),
        "unanswered" to unanswered.coerceIn(0L, totalQuestions),
        "score" to score,
        "maxScore" to maxScore.coerceAtLeast(1.0),
        "accuracyPercent" to accuracyPercent.coerceIn(0.0, 100.0),
        "timeTakenSeconds" to timeTakenSeconds.coerceIn(0L, 86400L),
        "weakTopics" to weakTopics.take(20),
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class ChapterProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val chapterId: String = "",
    val chapterTitle: String = "",
    val progressPercent: Long = 0L,
    val videoCompleted: Boolean = false,
    val notesRead: Boolean = false,
    val questionsPracticed: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "subjectId" to subjectId,
        "subjectName" to subjectName.take(80),
        "chapterId" to chapterId,
        "chapterTitle" to chapterTitle.take(200),
        "progressPercent" to progressPercent.coerceIn(0L, 100L),
        "videoCompleted" to videoCompleted,
        "notesRead" to notesRead,
        "questionsPracticed" to questionsPracticed.coerceAtLeast(0L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class AiSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val academicLevel: String = "",
    val language: String = "English",
    val prompt: String = "",
    val response: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "subject" to subject.take(80),
        "academicLevel" to academicLevel.take(80),
        "language" to language.take(40),
        "prompt" to prompt.take(2000),
        "response" to response.take(15000),
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val selectedLevel: String = "",
    val xp: Long = 0L,
    val streakDays: Long = 0L,
    val publicOptIn: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuestionItem(
    @DocumentId val id: String = "",
    val subject: String = "",
    val academicLevel: String = "",
    val chapter: String = "",
    val topic: String = "",
    val questionType: String = "MCQ",
    val difficulty: String = "Medium",
    val marks: Long = 4L,
    val year: String = "Practice",
    val source: String = "EduGalaxy Concept Bank",
    val statement: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanationEn: String = "",
    val explanationHi: String = "",
    val aiGenerated: Boolean = false,
    val repeatedTag: Boolean = false,
    val published: Boolean = true,
    val createdBy: String = "catalog",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(adminUid: String): Map<String, Any> = mapOf(
        "subject" to subject.take(80),
        "academicLevel" to academicLevel.take(80),
        "chapter" to chapter.take(150),
        "topic" to topic.take(150),
        "questionType" to questionType.take(60),
        "difficulty" to if (difficulty in listOf("Easy", "Medium", "Hard")) difficulty else "Medium",
        "marks" to marks.coerceIn(1L, 100L),
        "year" to year.take(40),
        "source" to source.take(160),
        "statement" to statement.take(2000),
        "options" to options.take(6),
        "correctAnswer" to correctAnswer.take(500),
        "explanationEn" to explanationEn.take(4000),
        "explanationHi" to explanationHi.take(4000),
        "aiGenerated" to aiGenerated,
        "repeatedTag" to repeatedTag,
        "published" to published,
        "createdBy" to adminUid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class VideoLesson(
    @DocumentId val id: String = "",
    val title: String = "",
    val subject: String = "",
    val academicLevel: String = "",
    val chapter: String = "",
    val lessonMode: String = "2D Animated",
    val durationMinutes: Long = 10L,
    val language: String = "Bilingual",
    val instructor: String = "EduGalaxy Studio",
    val summary: String = "",
    val keyConcepts: List<String> = emptyList(),
    val captionsEn: List<String> = emptyList(),
    val captionsHi: List<String> = emptyList(),
    val published: Boolean = true,
    val createdBy: String = "catalog",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(adminUid: String): Map<String, Any> = mapOf(
        "title" to title.take(200),
        "subject" to subject.take(80),
        "academicLevel" to academicLevel.take(80),
        "chapter" to chapter.take(150),
        "lessonMode" to if (lessonMode in listOf("2D Animated", "3D Animated", "Whiteboard", "AI Voice")) lessonMode else "2D Animated",
        "durationMinutes" to durationMinutes.coerceIn(1L, 600L),
        "language" to language.take(40),
        "instructor" to instructor.take(100),
        "summary" to summary.take(2000),
        "keyConcepts" to keyConcepts.take(15),
        "captionsEn" to captionsEn.take(25),
        "captionsHi" to captionsHi.take(25),
        "published" to published,
        "createdBy" to adminUid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class PdfMaterial(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "Revision Notes",
    val subject: String = "",
    val academicLevel: String = "",
    val chapter: String = "",
    val source: String = "EduGalaxy Open Notes",
    val pagesCount: Long = 10L,
    val summary: String = "",
    val contentSections: List<String> = emptyList(),
    val published: Boolean = true,
    val createdBy: String = "catalog",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(adminUid: String): Map<String, Any> = mapOf(
        "title" to title.take(200),
        "category" to category.take(80),
        "subject" to subject.take(80),
        "academicLevel" to academicLevel.take(80),
        "chapter" to chapter.take(150),
        "source" to source.take(160),
        "pagesCount" to pagesCount.coerceIn(1L, 2000L),
        "summary" to summary.take(2000),
        "contentSections" to contentSections.take(30),
        "published" to published,
        "createdBy" to adminUid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class MockTest(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "Chapter-wise Test",
    val subject: String = "",
    val academicLevel: String = "",
    val durationMinutes: Long = 15L,
    val negativeMarking: Double = 0.25,
    val questionCount: Long = 5L,
    val totalMarks: Long = 20L,
    val questionIds: List<String> = emptyList(),
    val published: Boolean = true,
    val createdBy: String = "catalog",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(adminUid: String): Map<String, Any> = mapOf(
        "title" to title.take(200),
        "category" to category.take(80),
        "subject" to subject.take(80),
        "academicLevel" to academicLevel.take(80),
        "durationMinutes" to durationMinutes.coerceIn(1L, 300L),
        "negativeMarking" to negativeMarking.coerceIn(0.0, 5.0),
        "questionCount" to questionCount.coerceIn(1L, 200L),
        "totalMarks" to totalMarks.coerceIn(1L, 1000L),
        "questionIds" to questionIds.take(100),
        "published" to published,
        "createdBy" to adminUid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class UniversityInfo(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "IIT",
    val location: String = "",
    val websiteUrl: String = "",
    val resultsUrl: String = "",
    val admitCardUrl: String = "",
    val courses: List<String> = emptyList(),
    val departments: List<String> = emptyList(),
    val examInfo: String = "",
    val notices: List<String> = emptyList(),
    val published: Boolean = true,
    val createdBy: String = "catalog",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(adminUid: String): Map<String, Any> = mapOf(
        "name" to name.take(160),
        "category" to category.take(80),
        "location" to location.take(120),
        "websiteUrl" to websiteUrl.take(300),
        "resultsUrl" to resultsUrl.take(300),
        "admitCardUrl" to admitCardUrl.take(300),
        "courses" to courses.take(30),
        "departments" to departments.take(30),
        "examInfo" to examInfo.take(1500),
        "notices" to notices.take(15),
        "published" to published,
        "createdBy" to adminUid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val title: String = "",
    val message: String = "",
    val category: String = "Update",
    val published: Boolean = true,
    val createdBy: String = "catalog",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(adminUid: String): Map<String, Any> = mapOf(
        "title" to title.take(150),
        "message" to message.take(1000),
        "category" to category.take(60),
        "published" to published,
        "createdBy" to adminUid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class StudentReport(
    @DocumentId val id: String = "",
    val userId: String = "",
    val targetType: String = "question",
    val targetId: String = "",
    val reason: String = "",
    val status: String = "open",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AdminAuditLog(
    @DocumentId val id: String = "",
    val adminId: String = "",
    val adminEmail: String = "",
    val action: String = "",
    val targetCollection: String = "",
    val targetId: String = "",
    val summary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
