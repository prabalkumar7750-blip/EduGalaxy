package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.ChapterProgress
import com.example.data.model.EducationCatalog
import com.example.data.model.EducationTier
import com.example.data.model.MockTest
import com.example.data.model.PdfMaterial
import com.example.data.model.QuestionItem
import com.example.data.model.SubjectMetadata
import com.example.data.model.UniversityInfo
import com.example.data.model.VideoLesson
import com.example.ui.components.EmptyGalaxyState
import com.example.ui.components.GlassCard
import com.example.ui.components.Subject3DIconBadge
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassSelectionScreen(
    currentLevel: String,
    currentStream: String,
    initialTierId: String? = null,
    onSelectAcademicPath: (level: String, stream: String, course: String, university: String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var selectedTier by remember(initialTierId) {
        mutableStateOf(
            EducationCatalog.educationTiers.find { it.id == initialTierId }
                ?: EducationCatalog.educationTiers.first()
        )
    }
    var chosenStream by remember { mutableStateOf(currentStream.ifBlank { "General" }) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Academic Directory", subtitle = "Class 1 to PG & Competitive Exams", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EducationCatalog.educationTiers) { tier ->
                        val active = tier.id == selectedTier.id
                        FilterChip(
                            selected = active,
                            onClick = { selectedTier = tier },
                            label = { Text(tier.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tier.accentColor.copy(alpha = 0.24f),
                                selectedLabelColor = tier.accentColor
                            )
                        )
                    }
                }
            }

            if (selectedTier.streams.isNotEmpty()) {
                item {
                    GlassCard(borderColor = NeonPurple.copy(alpha = 0.55f)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Select Class 11–12 Stream", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                selectedTier.streams.forEach { stream ->
                                    FilterChip(
                                        selected = chosenStream == stream,
                                        onClick = { chosenStream = stream },
                                        label = { Text(stream) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "${selectedTier.title} — ${selectedTier.subtitle}",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }

            items(selectedTier.levels) { level ->
                val isCurrent = level.equals(currentLevel, ignoreCase = true)
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("select_level_$level"),
                    borderColor = if (isCurrent) NeonCyan else selectedTier.accentColor.copy(alpha = 0.45f),
                    onClick = {
                        val stream = if (selectedTier.streams.isNotEmpty()) chosenStream else "General"
                        val univ = when (selectedTier.id) {
                            "college", "postgrad" -> "University / AICTE"
                            "competitive" -> "National Testing Agency / UPSC / SSC"
                            else -> "CBSE / State Board / NCERT"
                        }
                        onSelectAcademicPath(level, stream, selectedTier.title, univ)
                        onBack()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.School, contentDescription = null, tint = selectedTier.accentColor)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(level, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                Text(selectedTier.badge, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                        if (isCurrent) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = NeonCyan)
                        } else {
                            Text("Select", style = MaterialTheme.typography.labelLarge, color = selectedTier.accentColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllSubjectsScreen(
    onOpenSubject: (SubjectMetadata) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var categoryFilter by remember { mutableStateOf("All") }
    val filtered = remember(categoryFilter) {
        if (categoryFilter == "All") EducationCatalog.subjects
        else EducationCatalog.subjects.filter { it.category.equals(categoryFilter, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Complete Subject Library", subtitle = "School, College & Competitive Subjects", onBack = onBack)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "School", "College", "Competitive")) { cat ->
                FilterChip(
                    selected = categoryFilter == cat,
                    onClick = { categoryFilter = cat },
                    label = { Text(cat) }
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { subject ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = subject.accentColor.copy(alpha = 0.6f),
                    onClick = { onOpenSubject(subject) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Subject3DIconBadge(subjectName = subject.name, accentColor = subject.accentColor, size = 48.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(subject.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(subject.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("${subject.category} • ${subject.chapters.size} Chapters", style = MaterialTheme.typography.labelSmall, color = subject.accentColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectDashboardScreen(
    subject: SubjectMetadata,
    questions: List<QuestionItem>,
    videos: List<VideoLesson>,
    pdfs: List<PdfMaterial>,
    progressList: List<ChapterProgress>,
    onOpenQuestionsForSubject: (String) -> Unit,
    onOpenVideosForSubject: (String) -> Unit,
    onOpenPdfsForSubject: (String) -> Unit,
    onOpenTestsForSubject: (String) -> Unit,
    onAskAiForSubject: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val subQuestions = remember(questions, subject) { questions.filter { it.subject.equals(subject.name, true) } }
    val subVideos = remember(videos, subject) { videos.filter { it.subject.equals(subject.name, true) } }
    val subPdfs = remember(pdfs, subject) { pdfs.filter { it.subject.equals(subject.name, true) } }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = subject.name, subtitle = "${subject.category} Subject Dashboard", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GlassCard(borderColor = subject.accentColor, glowColor = subject.accentColor.copy(alpha = 0.25f)) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Subject3DIconBadge(subject.name, subject.accentColor, size = 56.dp)
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(subject.name, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                                Text(subject.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            SubjectActionStat("${subQuestions.size} Qs", Icons.Filled.Quiz, NeonGreen, Modifier.weight(1f)) {
                                onOpenQuestionsForSubject(subject.name)
                            }
                            SubjectActionStat("${subVideos.size} Videos", Icons.Filled.PlayCircle, NeonCyan, Modifier.weight(1f)) {
                                onOpenVideosForSubject(subject.name)
                            }
                            SubjectActionStat("${subPdfs.size} PDFs", Icons.Filled.PictureAsPdf, GalaxyGold, Modifier.weight(1f)) {
                                onOpenPdfsForSubject(subject.name)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeonPurple.copy(alpha = 0.24f),
                                border = BorderStroke(1.dp, NeonPurple),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenTestsForSubject(subject.name) }
                            ) {
                                Text(
                                    "Practice Test",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeonCyan.copy(alpha = 0.20f),
                                border = BorderStroke(1.dp, NeonCyan),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onAskAiForSubject(subject.name) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Ask AI", style = MaterialTheme.typography.labelLarge, color = NeonCyan)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("Chapters & Topics", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }

            items(subject.chapters) { chapter ->
                val chProgress = progressList.find { it.chapterTitle.equals(chapter.title, true) }
                val pct = ((chProgress?.progressPercent ?: 0L).toFloat() / 100f).coerceIn(0f, 1f)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = subject.accentColor.copy(alpha = 0.45f),
                    onClick = { onOpenQuestionsForSubject(subject.name) }
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(chapter.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                            Text("${chapter.estimatedMinutes} min", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                        }
                        Text(
                            text = "Topics: ${chapter.topics.joinToString(" • ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        LinearProgressIndicator(
                            progress = { pct },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = subject.accentColor,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                        Text("${(pct * 100).toInt()}% Completed", style = MaterialTheme.typography.labelSmall, color = subject.accentColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectActionStat(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, maxLines = 1)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalSearchScreen(
    recentSearches: List<String>,
    questions: List<QuestionItem>,
    videos: List<VideoLesson>,
    pdfs: List<PdfMaterial>,
    mockTests: List<MockTest>,
    universities: List<UniversityInfo>,
    onRecordSearch: (String) -> Unit,
    onOpenSubject: (SubjectMetadata) -> Unit,
    onOpenQuestions: () -> Unit,
    onOpenVideos: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenTests: () -> Unit,
    onOpenUniversities: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var query by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") }

    val q = query.trim()
    val matchedSubjects = remember(q) {
        if (q.isEmpty()) emptyList()
        else EducationCatalog.subjects.filter {
            it.name.contains(q, true) || it.chapters.any { ch -> ch.title.contains(q, true) }
        }
    }
    val matchedQuestions = remember(q, questions) {
        if (q.isEmpty()) emptyList()
        else questions.filter { it.statement.contains(q, true) || it.subject.contains(q, true) || it.chapter.contains(q, true) }
    }
    val matchedVideos = remember(q, videos) {
        if (q.isEmpty()) emptyList()
        else videos.filter { it.title.contains(q, true) || it.subject.contains(q, true) }
    }
    val matchedPdfs = remember(q, pdfs) {
        if (q.isEmpty()) emptyList()
        else pdfs.filter { it.title.contains(q, true) || it.subject.contains(q, true) }
    }
    val matchedUniversities = remember(q, universities) {
        if (q.isEmpty()) emptyList()
        else universities.filter { it.name.contains(q, true) || it.category.contains(q, true) || it.courses.any { c -> c.contains(q, true) } }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Smart Global Search", subtitle = "Classes, Subjects, Questions, PDFs & Universities", onBack = onBack)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search Physics, IIT Delhi, Quadratic, DSA...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NeonCyan) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        TextButton(onClick = { onRecordSearch(query); query = "" }) {
                            Text("Clear", color = NeonCyan)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input")
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("All", "Subjects", "Questions", "Videos", "PDFs", "Universities")) { chip ->
                    FilterChip(selected = filterType == chip, onClick = { filterType = chip }, label = { Text(chip) })
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (q.isEmpty()) {
                item {
                    Text("Recent & Suggested Searches", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentSearches.forEach { term ->
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable { query = term }
                            ) {
                                Text(term, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                            }
                        }
                    }
                }
            } else {
                if ((filterType == "All" || filterType == "Subjects") && matchedSubjects.isNotEmpty()) {
                    item { Text("Subjects (${matchedSubjects.size})", style = MaterialTheme.typography.titleMedium, color = NeonCyan) }
                    items(matchedSubjects) { sub ->
                        SearchResultCard(sub.name, "${sub.category} • ${sub.chapters.size} Chapters", "Subject") {
                            onRecordSearch(q); onOpenSubject(sub)
                        }
                    }
                }
                if ((filterType == "All" || filterType == "Questions") && matchedQuestions.isNotEmpty()) {
                    item { Text("Questions (${matchedQuestions.size})", style = MaterialTheme.typography.titleMedium, color = NeonGreen) }
                    items(matchedQuestions) { item ->
                        SearchResultCard(item.statement, "${item.subject} • ${item.chapter}", item.questionType) {
                            onRecordSearch(q); onOpenQuestions()
                        }
                    }
                }
                if ((filterType == "All" || filterType == "Videos") && matchedVideos.isNotEmpty()) {
                    item { Text("Video Lessons (${matchedVideos.size})", style = MaterialTheme.typography.titleMedium, color = NeonPurple) }
                    items(matchedVideos) { vid ->
                        SearchResultCard(vid.title, "${vid.subject} • ${vid.lessonMode}", "Video") {
                            onRecordSearch(q); onOpenVideos()
                        }
                    }
                }
                if ((filterType == "All" || filterType == "PDFs") && matchedPdfs.isNotEmpty()) {
                    item { Text("PDF Study Material (${matchedPdfs.size})", style = MaterialTheme.typography.titleMedium, color = GalaxyGold) }
                    items(matchedPdfs) { pdf ->
                        SearchResultCard(pdf.title, "${pdf.category} • ${pdf.subject}", "PDF") {
                            onRecordSearch(q); onOpenDownloads()
                        }
                    }
                }
                if ((filterType == "All" || filterType == "Universities") && matchedUniversities.isNotEmpty()) {
                    item { Text("Universities (${matchedUniversities.size})", style = MaterialTheme.typography.titleMedium, color = NeonCyan) }
                    items(matchedUniversities) { uni ->
                        SearchResultCard(uni.name, "${uni.category} • ${uni.location}", "University") {
                            onRecordSearch(q); onOpenUniversities()
                        }
                    }
                }
                if (matchedSubjects.isEmpty() && matchedQuestions.isEmpty() && matchedVideos.isEmpty() && matchedPdfs.isEmpty() && matchedUniversities.isEmpty()) {
                    item {
                        EmptyGalaxyState(
                            title = "No matching items for \"$q\"",
                            subtitle = "Try searching another subject, chapter, exam, or university name."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(title: String, subtitle: String, tag: String, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 2)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 1)
            }
            Spacer(Modifier.width(8.dp))
            Surface(shape = CircleShape, color = NeonCyan.copy(alpha = 0.16f)) {
                Text(tag, style = MaterialTheme.typography.labelSmall, color = NeonCyan, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun TopScreenBar(title: String, subtitle: String? = null, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("top_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}
