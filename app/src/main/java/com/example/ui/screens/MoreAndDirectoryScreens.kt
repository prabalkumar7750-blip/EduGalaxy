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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AdminAuditLog
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
import com.example.data.model.UserProfile
import com.example.data.model.VideoLesson
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.BilingualToggleChip
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
fun MoreHubScreen(
    profile: UserProfile?,
    isAdmin: Boolean,
    onOpenAiTutor: () -> Unit,
    onOpenQuestions: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenUniversities: () -> Unit,
    onOpenClasses: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("more_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Explore EduGalaxy", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Text("AI Tutor, Directory, Progress, Profile & Tools", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        item {
            HubActionCard("EduGalaxy AI Tutor", "Ask doubts, solve math, Hindi & English voice tutor", Icons.Filled.AutoAwesome, NeonCyan, onOpenAiTutor)
        }
        item {
            HubActionCard("Most Important Questions", "Chapter-wise MCQs, Conceptual & AI Practice", Icons.Filled.Quiz, NeonGreen, onOpenQuestions)
        }
        item {
            HubActionCard("Progress & Leaderboard", "XP, Coins, Streaks, Badges & Subject Mastery", Icons.Filled.EmojiEvents, GalaxyGold, onOpenProgress)
        }
        item {
            HubActionCard("Indian University & College Directory", "IITs, NITs, IIITs, Central, State & Open Universities", Icons.Filled.AccountBalance, NeonBlue, onOpenUniversities)
        }
        item {
            HubActionCard("Academic Directory (Class 1–PG & Exams)", "Switch Class, Stream, Degree, or Competitive Exam", Icons.Filled.School, NeonPurple, onOpenClasses)
        }
        item {
            HubActionCard("Profile & Accessibility Settings", "${profile?.displayName ?: "Student"} • Language, Goals & Reduced Motion", Icons.Filled.Person, AmberGold, onOpenProfile)
        }
        if (isAdmin) {
            item {
                HubActionCard("Admin CMS & Moderation Panel", "Manage Questions, Videos, PDFs, Tests, Reports & Audit Logs", Icons.Filled.AdminPanelSettings, NeonRose, onOpenAdmin)
            }
        }
    }
}

@Composable
private fun HubActionCard(title: String, subtitle: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accent.copy(alpha = 0.65f),
        glowColor = accent.copy(alpha = 0.16f),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = accent.copy(alpha = 0.2f), modifier = Modifier.size(46.dp)) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressAndLeaderboardScreen(
    profile: UserProfile?,
    progressList: List<ChapterProgress>,
    testAttempts: List<TestAttempt>,
    leaderboard: List<LeaderboardEntry>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val xp = profile?.xp ?: 50L
    val levelNum = (xp / 200L) + 1L
    val levelPct = ((xp % 200L).toFloat() / 200f).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Student Progress & Mastery", subtitle = "Gamified Learning Analytics", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GlassCard(borderColor = GalaxyGold, glowColor = NeonPurple.copy(alpha = 0.25f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AnimatedProgressRing(progress = levelPct, color = GalaxyGold, size = 84.dp, label = "Lv $levelNum")
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("$xp XP • ${profile?.coins ?: 50} Study Coins", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
                            Text("Streak: ${profile?.streakDays ?: 1} Days • Chapters: ${progressList.size}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            Text("Tests Completed: ${testAttempts.size}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                        }
                    }
                }
            }

            item {
                Text("Achievement Badges", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val badges = (profile?.badges ?: listOf("galaxy_pioneer")).distinct()
                    badges.forEach { badge ->
                        Surface(
                            shape = CircleShape,
                            color = GalaxyGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, GalaxyGold.copy(alpha = 0.6f))
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = GalaxyGold, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(badge.replace("_", " ").uppercase(), style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                            }
                        }
                    }
                }
            }

            item {
                GlassCard(borderColor = NeonGreen.copy(alpha = 0.55f)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Verified, contentDescription = null, tint = NeonGreen)
                            Spacer(Modifier.width(8.dp))
                            Text("Learning Completion Certificate", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }
                        Text(
                            text = "Awarded to ${profile?.displayName ?: "Student"} for active mastery in ${profile?.selectedLevel ?: "Class 10"} (${profile?.selectedUniversity ?: "CBSE"}).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Leaderboard, contentDescription = null, tint = NeonCyan)
                    Spacer(Modifier.width(8.dp))
                    Text("Live EduGalaxy Leaderboard", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                }
            }

            if (leaderboard.isEmpty()) {
                item {
                    EmptyGalaxyState(title = "Leaderboard syncing...", subtitle = "Complete tests or lessons to climb the EduGalaxy ranks!")
                }
            } else {
                items(leaderboard.take(15).mapIndexed { i, e -> i + 1 to e }) { (rank, entry) ->
                    GlassCard(borderColor = if (rank == 1) GalaxyGold else NeonCyan.copy(alpha = 0.35f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("#$rank", style = MaterialTheme.typography.titleMedium, color = if (rank <= 3) GalaxyGold else NeonCyan, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(entry.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                    Text("${entry.selectedLevel} • ${entry.streakDays}d streak", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }
                            Text("${entry.xp} XP", style = MaterialTheme.typography.titleMedium, color = GalaxyGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UniversityDirectoryScreen(
    universities: List<UniversityInfo>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val uriHandler = LocalUriHandler.current
    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }

    val filtered = remember(universities, search, category) {
        universities.filter { u ->
            (category == "All" || u.category.equals(category, true)) &&
                (search.isBlank() || u.name.contains(search, true) || u.location.contains(search, true) || u.courses.any { it.contains(search, true) })
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Indian University Directory", subtitle = "Verified portals, courses, results & admit card links", onBack = onBack)
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search IIT, NIT, DU, BHU, IGNOU...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NeonCyan) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(EducationCatalog.universityCategories) { cat ->
                    FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) })
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.id }) { uni ->
                GlassCard(borderColor = NeonBlue.copy(alpha = 0.6f)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Surface(shape = CircleShape, color = NeonBlue.copy(alpha = 0.2f)) {
                                Text(uni.category, style = MaterialTheme.typography.labelSmall, color = NeonCyan, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                            Text(uni.location, style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                        }
                        Text(uni.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("Courses: ${uni.courses.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("Departments: ${uni.departments.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("Exam Info: ${uni.examInfo}", style = MaterialTheme.typography.bodyMedium, color = NeonCyan)
                        uni.notices.forEach { notice ->
                            Text("Notice: $notice", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { runCatching { uriHandler.openUri(uni.websiteUrl) } }) {
                                Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Website")
                            }
                            OutlinedButton(onClick = { runCatching { uriHandler.openUri(uni.resultsUrl) } }) {
                                Text("Results")
                            }
                            OutlinedButton(onClick = { runCatching { uriHandler.openUri(uni.admitCardUrl) } }) {
                                Text("Admit Card")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSettingsScreen(
    profile: UserProfile?,
    bookmarks: List<UserBookmark>,
    onUpdatePreferences: (language: String?, dailyGoal: Long?, reducedMotion: Boolean?) -> Unit,
    onChangeClass: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var goalMinutes by remember(profile?.dailyGoalMinutes) { mutableFloatStateOf((profile?.dailyGoalMinutes ?: 45L).toFloat()) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopScreenBar(title = "Profile & Settings", subtitle = profile?.email ?: "", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GlassCard(borderColor = NeonCyan.copy(alpha = 0.6f)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(profile?.displayName ?: "EduGalaxy Student", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                        Text("Role: ${profile?.role?.uppercase() ?: "STUDENT"}", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                        Text("Current Level: ${profile?.selectedLevel ?: "Class 10"} • ${profile?.selectedStream ?: "General"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("Board/University: ${profile?.selectedUniversity ?: "CBSE"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        OutlinedButton(onClick = onChangeClass) {
                            Text("Change Class / Course / Exam")
                        }
                    }
                }
            }

            item {
                GlassCard(borderColor = NeonPurple.copy(alpha = 0.55f)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Learning & Accessibility Preferences", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("Preferred Explanation Language", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        BilingualToggleChip(
                            selectedLanguage = profile?.preferredLanguage ?: "Bilingual",
                            onSelect = { onUpdatePreferences(it, null, null) }
                        )
                        Text("Daily Study Goal: ${goalMinutes.toInt()} minutes", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Slider(
                            value = goalMinutes,
                            onValueChange = { goalMinutes = it },
                            onValueChangeFinished = { onUpdatePreferences(null, goalMinutes.toLong(), null) },
                            valueRange = 15f..180f
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Reduced Motion (Accessibility)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                Text("Minimizes starfield and particle animations", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                            Switch(
                                checked = profile?.reducedMotion ?: false,
                                onCheckedChange = { onUpdatePreferences(null, null, it) }
                            )
                        }
                    }
                }
            }

            item {
                Text("Saved Bookmarks (${bookmarks.size})", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
            }
            items(bookmarks, key = { it.id }) { bm ->
                GlassCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${bm.itemType.uppercase()} • ${bm.subject}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                        Text(bm.title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    }
                }
            }

            item {
                Button(
                    onClick = onSignOut,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRose, contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_out_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    questions: List<QuestionItem>,
    reports: List<StudentReport>,
    auditLogs: List<AdminAuditLog>,
    onSaveQuestion: (QuestionItem) -> Unit,
    onDeleteQuestion: (String) -> Unit,
    onPublishVideo: (VideoLesson) -> Unit,
    onPublishPdf: (PdfMaterial) -> Unit,
    onPublishTest: (MockTest) -> Unit,
    onBroadcastAnnouncement: (String, String, String) -> Unit,
    onResolveReport: (String, String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var tabIdx by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().testTag("admin_dashboard_screen")) {
        TopScreenBar(title = "Admin CMS & Analytics", subtitle = "Role-Protected Content & Audit Management", onBack = onBack)
        TabRow(selectedTabIndex = tabIdx, containerColor = Color.Transparent, contentColor = NeonCyan) {
            Tab(selected = tabIdx == 0, onClick = { tabIdx = 0 }, text = { Text("Questions") })
            Tab(selected = tabIdx == 1, onClick = { tabIdx = 1 }, text = { Text("Publish") })
            Tab(selected = tabIdx == 2, onClick = { tabIdx = 2 }, text = { Text("Reports (${reports.size})") })
            Tab(selected = tabIdx == 3, onClick = { tabIdx = 3 }, text = { Text("Audit Log") })
        }

        when (tabIdx) {
            0 -> {
                var subject by remember { mutableStateOf("Mathematics") }
                var level by remember { mutableStateOf("Class 10") }
                var chapter by remember { mutableStateOf("Quadratic Equations") }
                var statement by remember { mutableStateOf("") }
                var answer by remember { mutableStateOf("") }
                var explanationEn by remember { mutableStateOf("") }
                var explanationHi by remember { mutableStateOf("") }

                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        GlassCard(borderColor = NeonCyan) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Question to Bank", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Class / Exam Level") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = chapter, onValueChange = { chapter = it }, label = { Text("Chapter") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = statement, onValueChange = { statement = it }, label = { Text("Question Statement") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Correct Answer") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = explanationEn, onValueChange = { explanationEn = it }, label = { Text("English Explanation") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = explanationHi, onValueChange = { explanationHi = it }, label = { Text("Hindi Explanation") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = {
                                        if (statement.isNotBlank() && answer.isNotBlank()) {
                                            onSaveQuestion(
                                                QuestionItem(
                                                    subject = subject,
                                                    academicLevel = level,
                                                    chapter = chapter,
                                                    topic = chapter,
                                                    questionType = "Short Answer",
                                                    difficulty = "Medium",
                                                    marks = 3,
                                                    year = "Admin Curated",
                                                    source = "EduGalaxy Admin Bank",
                                                    statement = statement,
                                                    options = listOf(answer, "Option B", "Option C", "Option D"),
                                                    correctAnswer = answer,
                                                    explanationEn = explanationEn.ifBlank { "Correct answer: $answer" },
                                                    explanationHi = explanationHi.ifBlank { "सही उत्तर: $answer" },
                                                    published = true
                                                )
                                            )
                                            statement = ""; answer = ""; explanationEn = ""; explanationHi = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = GalaxyDeepNavy)
                                ) { Text("Publish Question", fontWeight = FontWeight.Bold) }
                            }
                        }
                    }

                    items(questions.filter { it.createdBy != "catalog" }, key = { it.id }) { q ->
                        GlassCard {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${q.subject} • ${q.academicLevel}", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                                    Text(q.statement, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                }
                                IconButton(onClick = { onDeleteQuestion(q.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = NeonRose)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                var annTitle by remember { mutableStateOf("") }
                var annMessage by remember { mutableStateOf("") }
                var resTitle by remember { mutableStateOf("") }
                var resSubject by remember { mutableStateOf("Physics") }
                var resSummary by remember { mutableStateOf("") }

                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        GlassCard(borderColor = GalaxyGold) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Broadcast Notification", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
                                OutlinedTextField(value = annTitle, onValueChange = { annTitle = it }, label = { Text("Announcement Title") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = annMessage, onValueChange = { annMessage = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = {
                                        if (annTitle.isNotBlank() && annMessage.isNotBlank()) {
                                            onBroadcastAnnouncement(annTitle, annMessage, "Admin Update")
                                            annTitle = ""; annMessage = ""
                                        }
                                    }
                                ) { Text("Send Announcement") }
                            }
                        }
                    }

                    item {
                        GlassCard(borderColor = NeonPurple) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Quick Publish Video / PDF / Mock Test", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                                OutlinedTextField(value = resTitle, onValueChange = { resTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = resSubject, onValueChange = { resSubject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = resSummary, onValueChange = { resSummary = it }, label = { Text("Summary / Key Points") }, modifier = Modifier.fillMaxWidth())
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        if (resTitle.isNotBlank()) {
                                            onPublishVideo(
                                                VideoLesson(
                                                    title = resTitle, subject = resSubject, academicLevel = "Class 12",
                                                    chapter = resTitle, lessonMode = "2D Animated", durationMinutes = 12,
                                                    summary = resSummary.ifBlank { resTitle }, keyConcepts = listOf(resSubject),
                                                    captionsEn = listOf(resSummary.ifBlank { resTitle }), captionsHi = listOf(resTitle), published = true
                                                )
                                            )
                                            resTitle = ""; resSummary = ""
                                        }
                                    }) { Text("+ Video") }
                                    Button(onClick = {
                                        if (resTitle.isNotBlank()) {
                                            onPublishPdf(
                                                PdfMaterial(
                                                    title = resTitle, category = "Revision Notes", subject = resSubject,
                                                    academicLevel = "Class 12", chapter = resTitle, summary = resSummary.ifBlank { resTitle },
                                                    contentSections = listOf(resSummary.ifBlank { resTitle }), published = true
                                                )
                                            )
                                            resTitle = ""; resSummary = ""
                                        }
                                    }) { Text("+ PDF") }
                                    Button(onClick = {
                                        if (resTitle.isNotBlank()) {
                                            onPublishTest(
                                                MockTest(
                                                    title = resTitle, category = "Daily Mock Test", subject = resSubject,
                                                    academicLevel = "Class 12", durationMinutes = 15, negativeMarking = 0.25,
                                                    questionCount = 4, totalMarks = 16, questionIds = listOf("q_math_quad_1", "q_phy_gauss_1"), published = true
                                                )
                                            )
                                            resTitle = ""; resSummary = ""
                                        }
                                    }) { Text("+ Test") }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (reports.isEmpty()) {
                        item { EmptyGalaxyState("No student reports", "Reported questions or content issues appear here.") }
                    } else {
                        items(reports, key = { it.id }) { rep ->
                            GlassCard {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("${rep.targetType} • ${rep.status.uppercase()}", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                                    Text(rep.reason, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                    if (rep.status == "open") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(onClick = { onResolveReport(rep.id, "resolved") }) { Text("Resolve", color = NeonGreen) }
                                            OutlinedButton(onClick = { onResolveReport(rep.id, "dismissed") }) { Text("Dismiss", color = TextSecondary) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (auditLogs.isEmpty()) {
                        item { EmptyGalaxyState("Audit log empty", "Admin content actions are logged immutably here.") }
                    } else {
                        items(auditLogs, key = { it.id }) { log ->
                            GlassCard {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${log.action} • ${log.targetCollection}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                                    Text(log.summary, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                    Text("By: ${log.adminEmail}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
