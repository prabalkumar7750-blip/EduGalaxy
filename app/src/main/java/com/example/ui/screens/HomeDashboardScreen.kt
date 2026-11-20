package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.Announcement
import com.example.data.model.EducationCatalog
import com.example.data.model.EducationTier
import com.example.data.model.SubjectMetadata
import com.example.data.model.UserProfile
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.EduGalaxyBrandLogo
import com.example.ui.components.GlassCard
import com.example.ui.components.Subject3DIconBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    profile: UserProfile?,
    announcements: List<Announcement>,
    reducedMotion: Boolean,
    onOpenSearch: () -> Unit,
    onOpenClassSelection: () -> Unit,
    onSelectTier: (EducationTier) -> Unit,
    onOpenSubjectDashboard: (SubjectMetadata) -> Unit,
    onOpenAllSubjects: () -> Unit,
    onOpenVideosTab: () -> Unit,
    onOpenQuestions: (String?) -> Unit,
    onOpenTestsTab: () -> Unit,
    onOpenDownloadsTab: () -> Unit,
    onOpenAiTutor: () -> Unit,
    onOpenProgressDashboard: () -> Unit,
    onOpenUniversityDirectory: () -> Unit,
    onOpenProfile: () -> Unit
) {
    var showNotificationsSheet by remember { mutableStateOf(false) }

    if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            containerColor = Color(0xFF0E1538)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Notifications & Updates",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                announcements.forEach { item ->
                    GlassCard(borderColor = NeonCyan.copy(alpha = 0.5f)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(item.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                            Text(item.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(item.message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_dashboard_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. TOP BRAND HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    EduGalaxyBrandLogo(size = 42.dp, animated = !reducedMotion)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "EduGalaxy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Learn • Practice • Excel",
                            style = MaterialTheme.typography.labelSmall,
                            color = GalaxyGold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onOpenSearch,
                        modifier = Modifier.testTag("home_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Global Search",
                            tint = NeonCyan
                        )
                    }
                    IconButton(
                        onClick = { showNotificationsSheet = true },
                        modifier = Modifier.testTag("home_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (announcements.isNotEmpty()) {
                                    Badge(containerColor = NeonRose, contentColor = Color.White) {
                                        Text("${announcements.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = TextPrimary
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = NeonPurple.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, GalaxyGold.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .clickable(onClick = onOpenProfile)
                            .testTag("home_profile_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = (profile?.displayName ?: "Student").split(" ").first(),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Student Level",
                                tint = GalaxyGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. HERO BANNER (Dream Big... Prepare Better...)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonCyan.copy(alpha = 0.65f),
                glowColor = NeonPurple.copy(alpha = 0.28f),
                cornerRadius = 22.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(NeonBlue.copy(alpha = 0.35f), Color.Transparent),
                                center = Offset(size.width * 0.85f, size.height * 0.25f),
                                radius = size.width * 0.45f
                            ),
                            radius = size.width * 0.45f,
                            center = Offset(size.width * 0.85f, size.height * 0.25f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = NeonPink.copy(alpha = 0.22f),
                                    border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        text = "Your Success, Our Mission!",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Dream Big... Prepare Better...",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = GalaxyGold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Complete Study Material from Class 1 to PG & Exams!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NeonCyan.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.55f)),
                                modifier = Modifier.clickable(onClick = onOpenAiTutor)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = "EduGalaxy AI", tint = NeonCyan)
                                    Spacer(Modifier.height(2.dp))
                                    Text("AI Tutor", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                                }
                            }
                        }

                        // 5 Feature Checkmarks from Blueprint/Mockup
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(
                                "Animated Video Lectures (2D/3D & AI Voice)",
                                "Most Important & Expected Practice Questions",
                                "Mock Tests & Instant Performance Analytics",
                                "Free Offline Study Notes & Formula Sheets"
                            ).forEach { bullet ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(bullet, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onOpenAiTutor,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = GalaxyDeepNavy),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("hero_ask_ai_button")
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Ask EduGalaxy AI", fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, GalaxyGold.copy(alpha = 0.6f)),
                                modifier = Modifier.clickable(onClick = onOpenClassSelection)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = profile?.selectedLevel ?: "Class 10",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = GalaxyGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. STUDENT GAMIFICATION & DAILY GOAL STRIP
        item {
            val goal = (profile?.dailyGoalMinutes ?: 45L).coerceAtLeast(1L)
            val mins = profile?.dailyStudyMinutes ?: 0L
            val pct = (mins.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = GalaxyGold.copy(alpha = 0.45f),
                onClick = onOpenProgressDashboard
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedProgressRing(
                            progress = pct,
                            color = NeonCyan,
                            size = 58.dp,
                            strokeWidth = 6.dp,
                            label = "${(pct * 100).toInt()}%"
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Daily Goal: $mins / $goal min", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(
                                "${profile?.selectedLevel ?: "Class 10"} • ${profile?.selectedUniversity ?: "CBSE"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Streak", tint = AmberGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${profile?.streakDays ?: 1}d Streak", style = MaterialTheme.typography.labelLarge, color = AmberGold)
                        }
                        Text("${profile?.xp ?: 50} XP • ${profile?.coins ?: 50} Coins", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                    }
                }
            }
        }

        // 4. SELECT YOUR CLASS
        item {
            SectionHeader(
                icon = Icons.Filled.School,
                title = "Select Your Class & Goal",
                iconTint = GalaxyGold,
                actionText = "View All",
                onAction = onOpenClassSelection
            )
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(EducationCatalog.educationTiers) { tier ->
                    GlassCard(
                        modifier = Modifier
                            .width(142.dp)
                            .testTag("class_tier_${tier.id}"),
                        borderColor = tier.accentColor.copy(alpha = 0.75f),
                        glowColor = tier.accentColor.copy(alpha = 0.24f),
                        onClick = { onSelectTier(tier) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = tier.accentColor.copy(alpha = 0.22f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.School, contentDescription = tier.title, tint = tier.accentColor)
                                }
                            }
                            Text(tier.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1)
                            Text(tier.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Surface(
                                shape = CircleShape,
                                color = tier.accentColor.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    tier.badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tier.accentColor,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. SELECT YOUR SUBJECT (2-row responsive neon cards)
        item {
            SectionHeader(
                icon = Icons.Filled.Description,
                title = "Select Your Subject",
                iconTint = NeonCyan,
                actionText = "All ${EducationCatalog.subjects.size}",
                onAction = onOpenAllSubjects
            )
            Spacer(Modifier.height(10.dp))
            val featuredSubjects = EducationCatalog.subjects.take(12)
            val topRow = featuredSubjects.filterIndexed { index, _ -> index % 2 == 0 }
            val bottomRow = featuredSubjects.filterIndexed { index, _ -> index % 2 == 1 }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(topRow.indices.toList()) { colIndex ->
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SubjectTileCard(
                            subject = topRow[colIndex],
                            onClick = { onOpenSubjectDashboard(topRow[colIndex]) }
                        )
                        if (colIndex < bottomRow.size) {
                            SubjectTileCard(
                                subject = bottomRow[colIndex],
                                onClick = { onOpenSubjectDashboard(bottomRow[colIndex]) }
                            )
                        }
                    }
                }
            }
        }

        // 6. POWERFUL FEATURES
        item {
            SectionHeader(
                icon = Icons.Filled.Star,
                title = "Powerful Features",
                iconTint = GalaxyGold,
                actionText = null,
                onAction = null
            )
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    QuickFeaturePill(
                        title = "Animated Videos",
                        cta = "Watch Now",
                        icon = Icons.Filled.PlayCircle,
                        accent = NeonRose,
                        onClick = onOpenVideosTab
                    )
                }
                item {
                    QuickFeaturePill(
                        title = "Important Questions",
                        cta = "Practice Now",
                        icon = Icons.Filled.Quiz,
                        accent = NeonGreen,
                        onClick = { onOpenQuestions(null) }
                    )
                }
                item {
                    QuickFeaturePill(
                        title = "Mock Tests",
                        cta = "Attempt Now",
                        icon = Icons.Filled.EmojiEvents,
                        accent = NeonBlue,
                        onClick = onOpenTestsTab
                    )
                }
                item {
                    QuickFeaturePill(
                        title = "AI Study Tutor",
                        cta = "Ask Doubt",
                        icon = Icons.Filled.AutoAwesome,
                        accent = NeonCyan,
                        onClick = onOpenAiTutor
                    )
                }
                item {
                    QuickFeaturePill(
                        title = "Universities",
                        cta = "Explore",
                        icon = Icons.Filled.AccountBalance,
                        accent = AmberGold,
                        onClick = onOpenUniversityDirectory
                    )
                }
            }
        }

        // 7. TRACK PROGRESS & FREE PDF NOTES BANNERS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("banner_track_progress"),
                    borderColor = NeonPurple.copy(alpha = 0.7f),
                    glowColor = NeonPurple.copy(alpha = 0.25f),
                    onClick = onOpenProgressDashboard
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = GalaxyGold)
                        Text("Track Your Progress", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("XP, Streaks & Mastery", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("View Dashboard ->", style = MaterialTheme.typography.labelLarge, color = GalaxyGold)
                    }
                }
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("banner_free_pdfs"),
                    borderColor = NeonGreen.copy(alpha = 0.7f),
                    glowColor = NeonGreen.copy(alpha = 0.25f),
                    onClick = onOpenDownloadsTab
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = NeonGreen)
                        Text("Free PDF Notes", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("Download Offline Study", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("Download Now ->", style = MaterialTheme.typography.labelLarge, color = NeonGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    actionText: String?,
    onAction: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText, color = NeonCyan, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.width(2.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SubjectTileCard(
    subject: SubjectMetadata,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .width(126.dp)
            .testTag("subject_card_${subject.id}"),
        borderColor = subject.accentColor.copy(alpha = 0.65f),
        glowColor = subject.accentColor.copy(alpha = 0.22f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Subject3DIconBadge(subjectName = subject.name, accentColor = subject.accentColor, size = 44.dp)
            Text(
                text = subject.name,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickFeaturePill(
    title: String,
    cta: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.width(156.dp),
        borderColor = accent.copy(alpha = 0.7f),
        glowColor = accent.copy(alpha = 0.20f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.22f))
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.labelLarge, color = TextPrimary, maxLines = 1)
                Text("$cta ->", style = MaterialTheme.typography.labelSmall, color = accent)
            }
        }
    }
}
