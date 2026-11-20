package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MockTest
import com.example.data.model.TestAttempt
import com.example.ui.ActiveExamSession
import com.example.ui.ExamResultSummary
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.EmptyGalaxyState
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MockTestScreen(
    mockTests: List<MockTest>,
    pastAttempts: List<TestAttempt>,
    activeSession: ActiveExamSession?,
    initialSubject: String? = null,
    preferredLanguage: String,
    onStartTest: (MockTest) -> Unit,
    onSelectAnswer: (Int, String) -> Unit,
    onNavigateQuestion: (Int) -> Unit,
    onToggleReview: (Int) -> Unit,
    onSubmitTest: () -> Unit,
    onExitSession: () -> Unit
) {
    if (activeSession != null) {
        if (activeSession.finishedResult != null) {
            ExamResultDashboard(
                result = activeSession.finishedResult,
                preferredLanguage = preferredLanguage,
                onDone = onExitSession
            )
        } else {
            ActiveExamInterface(
                session = activeSession,
                onSelectAnswer = onSelectAnswer,
                onNavigateQuestion = onNavigateQuestion,
                onToggleReview = onToggleReview,
                onSubmitTest = onSubmitTest,
                onExitSession = onExitSession
            )
        }
        return
    }

    var categoryFilter by remember { mutableStateOf("All") }
    val filteredTests = remember(mockTests, categoryFilter, initialSubject) {
        mockTests.filter { t ->
            (categoryFilter == "All" || t.category.contains(categoryFilter, true)) &&
                (initialSubject == null || t.subject.equals(initialSubject, true))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mock_tests_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Mock Tests & Online Exams", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Text("Timed exams, negative marking & instant topic analytics", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("All", "Daily", "Weekly", "Subject-wise", "Full-Length")) { cat ->
                    FilterChip(selected = categoryFilter == cat, onClick = { categoryFilter = cat }, label = { Text(cat) })
                }
            }
        }

        items(filteredTests, key = { it.id }) { test ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonBlue.copy(alpha = 0.65f),
                glowColor = NeonBlue.copy(alpha = 0.20f)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = NeonPurple.copy(alpha = 0.25f)) {
                            Text(test.category, style = MaterialTheme.typography.labelSmall, color = NeonCyan, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                        Text("${test.academicLevel} • ${test.subject}", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                    }
                    Text(test.title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Text(
                        text = "${test.questionCount} Questions • ${test.durationMinutes} min • ${test.totalMarks} Marks • Negative: -${test.negativeMarking}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Button(
                        onClick = { onStartTest(test) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = GalaxyDeepNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("start_test_${test.id}")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Start Exam", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text("Recent Test Attempts (${pastAttempts.size})", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }

        if (pastAttempts.isEmpty()) {
            item {
                EmptyGalaxyState(
                    title = "No tests attempted yet",
                    subtitle = "Start any mock test above to unlock instant scorecards and topic analytics.",
                    icon = Icons.Filled.EmojiEvents
                )
            }
        } else {
            items(pastAttempts.sortedByDescending { it.createdAt?.seconds ?: 0L }, key = { it.id }) { att ->
                GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GalaxyGold.copy(alpha = 0.45f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(att.testTitle, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("${att.correct}/${att.totalQuestions} Correct • Accuracy ${att.accuracyPercent.toInt()}%", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Text(
                            text = "${"%.1f".format(att.score)}/${att.maxScore.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = GalaxyGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveExamInterface(
    session: ActiveExamSession,
    onSelectAnswer: (Int, String) -> Unit,
    onNavigateQuestion: (Int) -> Unit,
    onToggleReview: (Int) -> Unit,
    onSubmitTest: () -> Unit,
    onExitSession: () -> Unit
) {
    BackHandler(onBack = onExitSession)
    var showPalette by remember { mutableStateOf(false) }
    var confirmSubmit by remember { mutableStateOf(false) }

    val mins = session.remainingSeconds / 60
    val secs = session.remainingSeconds % 60
    val timeText = "%02d:%02d".format(mins, secs)

    val q = session.questions.getOrNull(session.currentIndex)
    val selectedOpt = session.answers[session.currentIndex]
    val isMarked = session.markedForReview.contains(session.currentIndex)

    if (showPalette) {
        AlertDialog(
            onDismissRequest = { showPalette = false },
            containerColor = Color(0xFF0E1538),
            title = { Text("Question Palette", color = TextPrimary) },
            text = {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    session.questions.indices.forEach { idx ->
                        val answered = session.answers.containsKey(idx)
                        val marked = session.markedForReview.contains(idx)
                        val bg = when {
                            marked -> AmberGold
                            answered -> NeonGreen
                            else -> Color.White.copy(alpha = 0.12f)
                        }
                        Surface(
                            shape = CircleShape,
                            color = bg,
                            border = if (idx == session.currentIndex) BorderStroke(2.dp, NeonCyan) else null,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onNavigateQuestion(idx); showPalette = false }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${idx + 1}",
                                    color = if (answered || marked) GalaxyDeepNavy else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPalette = false }) { Text("Close", color = NeonCyan) }
            }
        )
    }

    if (confirmSubmit) {
        AlertDialog(
            onDismissRequest = { confirmSubmit = false },
            containerColor = Color(0xFF0E1538),
            title = { Text("Submit Mock Test?", color = TextPrimary) },
            text = {
                Text(
                    "Answered ${session.answers.size} of ${session.questions.size} questions. Marked for review: ${session.markedForReview.size}.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { confirmSubmit = false; onSubmitTest() }) {
                    Text("Submit Now", color = NeonGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmSubmit = false }) { Text("Continue", color = TextSecondary) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("active_exam_interface"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Exam Header & Live Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(session.test.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("Q ${session.currentIndex + 1} of ${session.questions.size}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Surface(
                shape = CircleShape,
                color = if (session.remainingSeconds < 60) NeonRose.copy(alpha = 0.25f) else NeonCyan.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, if (session.remainingSeconds < 60) NeonRose else NeonCyan)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = "Timer", tint = if (session.remainingSeconds < 60) NeonRose else NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(timeText, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                }
            }
            IconButton(onClick = { showPalette = true }) {
                Icon(Icons.Filled.GridView, contentDescription = "Question Palette", tint = GalaxyGold)
            }
        }

        if (q != null) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonCyan.copy(alpha = 0.55f)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("${q.subject} • ${q.topic}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                                Text("+${q.marks} / -${session.test.negativeMarking}", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                            }
                            Text(q.statement, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }
                    }
                }
                items(q.options) { opt ->
                    val selected = selectedOpt == opt
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) NeonCyan.copy(alpha = 0.24f) else Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(1.5.dp, if (selected) NeonCyan else Color.White.copy(alpha = 0.16f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAnswer(session.currentIndex, opt) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (selected) NeonCyan else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(opt, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onNavigateQuestion((session.currentIndex - 1).coerceAtLeast(0)) },
                enabled = session.currentIndex > 0,
                modifier = Modifier.weight(1f)
            ) { Text("Prev") }

            OutlinedButton(
                onClick = { onToggleReview(session.currentIndex) },
                border = BorderStroke(1.dp, if (isMarked) AmberGold else Color.White.copy(alpha = 0.3f)),
                modifier = Modifier.weight(1.2f)
            ) {
                Icon(Icons.Filled.Bookmark, contentDescription = null, tint = if (isMarked) AmberGold else TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isMarked) "Marked" else "Review", color = if (isMarked) AmberGold else TextPrimary)
            }

            if (session.currentIndex < session.questions.lastIndex) {
                Button(
                    onClick = { onNavigateQuestion(session.currentIndex + 1) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue, contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) { Text("Next") }
            } else {
                Button(
                    onClick = { confirmSubmit = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = GalaxyDeepNavy),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("submit_exam_button")
                ) { Text("Submit", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun ExamResultDashboard(
    result: ExamResultSummary,
    preferredLanguage: String,
    onDone: () -> Unit
) {
    BackHandler(onBack = onDone)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("exam_result_dashboard"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = GalaxyGold,
                glowColor = NeonPurple.copy(alpha = 0.25f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Exam Result & Analytics", style = MaterialTheme.typography.headlineMedium, color = GalaxyGold)
                    Text(result.test.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    AnimatedProgressRing(
                        progress = (result.accuracyPercent / 100.0).toFloat().coerceIn(0f, 1f),
                        color = NeonGreen,
                        size = 104.dp,
                        strokeWidth = 10.dp,
                        label = "${result.accuracyPercent.toInt()}%"
                    )
                    Text("Score: ${"%.2f".format(result.score)} / ${result.maxScore.toInt()}", style = MaterialTheme.typography.titleLarge, color = NeonCyan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatMini("Correct", "${result.correct}", NeonGreen)
                        StatMini("Wrong", "${result.incorrect}", NeonRose)
                        StatMini("Skipped", "${result.unanswered}", AmberGold)
                        StatMini("Time", "${result.timeTakenSeconds}s", NeonCyan)
                    }
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonCyan.copy(alpha = 0.45f)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Topic-wise Performance", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    result.topicStats.forEach { (topic, pair) ->
                        val ratio = if (pair.second > 0) pair.first.toFloat() / pair.second.toFloat() else 0f
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(topic, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                Text("${pair.first}/${pair.second}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                            }
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(CircleShape),
                                color = if (ratio >= 0.7f) NeonGreen else AmberGold,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Detailed Solutions", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }

        items(result.questions.indices.toList()) { idx ->
            val q = result.questions[idx]
            val userAns = result.userAnswers[idx]
            val isCorrect = userAns == q.correctAnswer
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isCorrect) NeonGreen.copy(alpha = 0.55f) else NeonRose.copy(alpha = 0.55f)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Q${idx + 1}: ${q.statement}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("Your Answer: ${userAns ?: "Unanswered"}", style = MaterialTheme.typography.bodyMedium, color = if (isCorrect) NeonGreen else NeonRose)
                    Text("Correct Answer: ${q.correctAnswer}", style = MaterialTheme.typography.labelLarge, color = NeonGreen)
                    if (preferredLanguage != "Hindi") {
                        Text(q.explanationEn, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                    if (preferredLanguage != "English" && q.explanationHi.isNotBlank()) {
                        Text(q.explanationHi, style = MaterialTheme.typography.bodyMedium, color = GalaxyGold)
                    }
                }
            }
        }

        item {
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = GalaxyDeepNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Mock Tests", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatMini(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
