package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.EducationCatalog
import com.example.data.model.QuestionItem
import com.example.data.model.UserBookmark
import com.example.ui.components.BilingualToggleChip
import com.example.ui.components.EmptyGalaxyState
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuestionsBankScreen(
    questions: List<QuestionItem>,
    bookmarks: List<UserBookmark>,
    initialSubject: String? = null,
    academicLevel: String,
    preferredLanguage: String,
    isGeneratingAiQuestion: Boolean,
    onPracticeAnswered: (QuestionItem) -> Unit,
    onToggleBookmark: (QuestionItem) -> Unit,
    onGenerateAiQuestion: (subject: String, level: String, chapter: String, difficulty: String) -> Unit,
    onAskAiExplain: (QuestionItem) -> Unit,
    onReportQuestion: (QuestionItem, String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var subjectFilter by remember(initialSubject) { mutableStateOf(initialSubject ?: "All") }
    var difficultyFilter by remember { mutableStateOf("All") }
    var typeFilter by remember { mutableStateOf("All") }
    var repeatedOnly by remember { mutableStateOf(false) }
    var langMode by remember(preferredLanguage) { mutableStateOf(preferredLanguage) }

    val selectedAnswers = remember { mutableStateMapOf<String, String>() }
    val expandedSolutions = remember { mutableStateMapOf<String, Boolean>() }
    var reportingQuestion by remember { mutableStateOf<QuestionItem?>(null) }
    var reportReason by remember { mutableStateOf("") }

    val bookmarkedIds = remember(bookmarks) {
        bookmarks.filter { it.itemType == "question" }.map { it.itemId }.toSet()
    }

    val filteredQuestions = remember(questions, subjectFilter, difficultyFilter, typeFilter, repeatedOnly) {
        questions.filter { q ->
            (subjectFilter == "All" || q.subject.equals(subjectFilter, true)) &&
                (difficultyFilter == "All" || q.difficulty.equals(difficultyFilter, true)) &&
                (typeFilter == "All" || q.questionType.contains(typeFilter, true)) &&
                (!repeatedOnly || q.repeatedTag)
        }
    }

    if (reportingQuestion != null) {
        AlertDialog(
            onDismissRequest = { reportingQuestion = null },
            containerColor = Color(0xFF10183B),
            title = { Text("Report Question Issue", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    label = { Text("Describe correction or issue") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        reportingQuestion?.let { onReportQuestion(it, reportReason) }
                        reportReason = ""
                        reportingQuestion = null
                    }
                ) { Text("Submit", color = NeonCyan) }
            },
            dismissButton = {
                TextButton(onClick = { reportingQuestion = null }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Important Questions Bank", subtitle = "Concept, Practice & Bilingual Solutions", onBack = onBack)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BilingualToggleChip(selectedLanguage = langMode, onSelect = { langMode = it })
                Button(
                    onClick = {
                        val sub = if (subjectFilter == "All") "Mathematics" else subjectFilter
                        val ch = EducationCatalog.subjects.find { it.name.equals(sub, true) }?.chapters?.firstOrNull()?.title ?: "Core Concepts"
                        val diff = if (difficultyFilter == "All") "Medium" else difficultyFilter
                        onGenerateAiQuestion(sub, academicLevel, ch, diff)
                    },
                    enabled = !isGeneratingAiQuestion,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("generate_ai_question_button")
                ) {
                    if (isGeneratingAiQuestion) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text("AI Practice", style = MaterialTheme.typography.labelLarge)
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val subjects = listOf("All") + EducationCatalog.subjects.take(12).map { it.name }
                items(subjects) { sub ->
                    FilterChip(selected = subjectFilter == sub, onClick = { subjectFilter = sub }, label = { Text(sub) })
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("All", "Easy", "Medium", "Hard")) { diff ->
                    FilterChip(selected = difficultyFilter == diff, onClick = { difficultyFilter = diff }, label = { Text(diff) })
                }
                item {
                    FilterChip(
                        selected = repeatedOnly,
                        onClick = { repeatedOnly = !repeatedOnly },
                        label = { Text("Most Repeated") },
                        leadingIcon = { Icon(Icons.Filled.Repeat, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
                items(listOf("MCQ", "Numerical", "Short Answer", "Conceptual")) { t ->
                    FilterChip(
                        selected = typeFilter == t,
                        onClick = { typeFilter = if (typeFilter == t) "All" else t },
                        label = { Text(t) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredQuestions.isEmpty()) {
                item {
                    EmptyGalaxyState(
                        title = "No questions match those filters",
                        subtitle = "Reset filters or tap 'AI Practice' to generate a fresh practice question for this subject."
                    )
                }
            } else {
                items(filteredQuestions, key = { it.id }) { q ->
                    val safeId = q.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
                    val isBookmarked = bookmarkedIds.contains(safeId)
                    val chosen = selectedAnswers[q.id]
                    val showSolution = expandedSolutions[q.id] == true || chosen != null

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (q.aiGenerated) NeonPurple.copy(alpha = 0.7f) else NeonCyan.copy(alpha = 0.45f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                                    TagBadge(q.subject, NeonCyan)
                                    TagBadge(q.difficulty, when (q.difficulty) {
                                        "Easy" -> NeonGreen
                                        "Hard" -> NeonRose
                                        else -> AmberGold
                                    })
                                    if (q.aiGenerated) {
                                        TagBadge("AI Practice", NeonPurple)
                                    } else if (q.repeatedTag) {
                                        TagBadge("Repeated", GalaxyGold)
                                    }
                                }
                                Row {
                                    IconButton(onClick = { onToggleBookmark(q) }) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                            contentDescription = "Save Question",
                                            tint = if (isBookmarked) GalaxyGold else TextSecondary
                                        )
                                    }
                                    IconButton(onClick = { reportingQuestion = q }) {
                                        Icon(Icons.Filled.Flag, contentDescription = "Report Issue", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Text("${q.academicLevel} • ${q.chapter} • ${q.marks} Marks (${q.source})", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(q.statement, style = MaterialTheme.typography.titleMedium, color = TextPrimary)

                            if (q.options.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    q.options.forEach { opt ->
                                        val isSelected = chosen == opt
                                        val isCorrect = opt == q.correctAnswer
                                        val bg = when {
                                            chosen == null -> Color.White.copy(alpha = 0.05f)
                                            isCorrect -> NeonGreen.copy(alpha = 0.22f)
                                            isSelected -> NeonRose.copy(alpha = 0.22f)
                                            else -> Color.White.copy(alpha = 0.03f)
                                        }
                                        val border = when {
                                            chosen == null -> Color.White.copy(alpha = 0.16f)
                                            isCorrect -> NeonGreen
                                            isSelected -> NeonRose
                                            else -> Color.White.copy(alpha = 0.10f)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = bg,
                                            border = BorderStroke(1.dp, border),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = chosen == null) {
                                                    selectedAnswers[q.id] = opt
                                                    onPracticeAnswered(q)
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(opt, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                                                if (chosen != null && isCorrect) {
                                                    Icon(Icons.Filled.CheckCircle, contentDescription = "Correct", tint = NeonGreen, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { expandedSolutions[q.id] = !showSolution },
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                                ) {
                                    Text(if (showSolution) "Hide Solution" else "View Solution", color = NeonCyan)
                                }
                                OutlinedButton(
                                    onClick = { onAskAiExplain(q) },
                                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.7f))
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Ask AI", color = Color(0xFFD8B4FE))
                                }
                            }

                            AnimatedVisibility(visible = showSolution) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GalaxyDeepNavy.copy(alpha = 0.75f),
                                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Answer: ${q.correctAnswer}", style = MaterialTheme.typography.labelLarge, color = NeonGreen)
                                        if (langMode == "English" || langMode == "Bilingual") {
                                            Text("English: ${q.explanationEn}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                        }
                                        if ((langMode == "Hindi" || langMode == "Bilingual") && q.explanationHi.isNotBlank()) {
                                            Text("हिन्दी: ${q.explanationHi}", style = MaterialTheme.typography.bodyMedium, color = GalaxyGold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagBadge(text: String, color: Color) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
