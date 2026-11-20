package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserBookmark
import com.example.data.model.VideoLesson
import com.example.ui.components.BilingualToggleChip
import com.example.ui.components.GlassCard
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VideoLearningScreen(
    videos: List<VideoLesson>,
    bookmarks: List<UserBookmark>,
    initialSubject: String? = null,
    preferredLanguage: String,
    reducedMotion: Boolean,
    onToggleBookmark: (VideoLesson) -> Unit,
    onMarkCompleted: (VideoLesson) -> Unit
) {
    val context = LocalContext.current
    var modeFilter by remember { mutableStateOf("All") }
    var subjectFilter by remember(initialSubject) { mutableStateOf(initialSubject ?: "All") }
    var activeLesson by remember(videos) { mutableStateOf(videos.firstOrNull()) }

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.15f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showCaptions by remember { mutableStateOf(true) }
    var captionLang by remember(preferredLanguage) { mutableStateOf(if (preferredLanguage == "Hindi") "Hindi" else "English") }

    // Android TextToSpeech for real AI Voice Explanation narration
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        instance
    }
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    LaunchedEffect(isPlaying, playbackSpeed, activeLesson?.id) {
        while (isPlaying) {
            delay((250f / playbackSpeed).toLong().coerceAtLeast(80L))
            progress += 0.012f
            if (progress >= 1f) {
                progress = 1f
                isPlaying = false
                activeLesson?.let { onMarkCompleted(it) }
            }
        }
    }

    val bookmarkedIds = remember(bookmarks) {
        bookmarks.filter { it.itemType == "video" }.map { it.itemId }.toSet()
    }

    val filteredVideos = remember(videos, modeFilter, subjectFilter) {
        videos.filter { v ->
            (modeFilter == "All" || v.lessonMode == modeFilter) &&
                (subjectFilter == "All" || v.subject.equals(subjectFilter, true))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("video_learning_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Animated Video Studio", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Text("2D/3D Lessons, Whiteboard & AI Voice Narration", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }

        // ACTIVE ANIMATED LESSON PLAYER
        activeLesson?.let { lesson ->
            val captions = if (captionLang == "Hindi" && lesson.captionsHi.isNotEmpty()) lesson.captionsHi else lesson.captionsEn
            val captionIdx = ((progress * captions.size).toInt()).coerceIn(0, (captions.size - 1).coerceAtLeast(0))
            val currentCaption = captions.getOrNull(captionIdx) ?: lesson.summary

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan.copy(alpha = 0.7f),
                    glowColor = NeonPurple.copy(alpha = 0.25f)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = CircleShape, color = NeonPurple.copy(alpha = 0.25f)) {
                                Text(lesson.lessonMode, style = MaterialTheme.typography.labelSmall, color = NeonCyan, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                            Text("${lesson.subject} • ${lesson.durationMinutes} min", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                        }

                        // Interactive Canvas Educational Visual Simulation
                        AnimatedConceptStage(
                            subject = lesson.subject,
                            mode = lesson.lessonMode,
                            progress = progress,
                            isPlaying = isPlaying && !reducedMotion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )

                        if (showCaptions) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GalaxyDeepNavy.copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentCaption,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Text(lesson.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("Instructor: ${lesson.instructor} • Chapter: ${lesson.chapter}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                        Slider(
                            value = progress,
                            onValueChange = { progress = it },
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { isPlaying = !isPlaying },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = GalaxyDeepNavy),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("video_play_pause_button")
                                ) {
                                    Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = "Play or Pause")
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (isPlaying) "Pause" else "Play", fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (ttsReady) {
                                            tts.language = if (captionLang == "Hindi") Locale("hi", "IN") else Locale.US
                                            tts.setSpeechRate(playbackSpeed)
                                            tts.speak(currentCaption, TextToSpeech.QUEUE_FLUSH, null, "lesson_tts")
                                        }
                                    }
                                ) {
                                    Icon(Icons.Filled.RecordVoiceOver, contentDescription = "AI Voice Narration", tint = GalaxyGold)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.08f),
                                    modifier = Modifier.clickable {
                                        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                                        val next = (speeds.indexOf(playbackSpeed) + 1) % speeds.size
                                        playbackSpeed = speeds[next]
                                    }
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Speed, contentDescription = "Speed", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("${playbackSpeed}x", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                    }
                                }
                                IconButton(onClick = { showCaptions = !showCaptions }) {
                                    Icon(Icons.Filled.Subtitles, contentDescription = "Subtitles", tint = if (showCaptions) NeonCyan else TextSecondary)
                                }
                                IconButton(onClick = { onMarkCompleted(lesson) }) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = "Complete Lesson", tint = NeonGreen)
                                }
                            }
                        }

                        BilingualToggleChip(selectedLanguage = captionLang, onSelect = {
                            captionLang = if (it == "Hindi") "Hindi" else "English"
                        })
                    }
                }
            }
        }

        // MODE & SUBJECT FILTERS
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("All", "2D Animated", "3D Animated", "Whiteboard", "AI Voice")) { mode ->
                    FilterChip(selected = modeFilter == mode, onClick = { modeFilter = mode }, label = { Text(mode) })
                }
            }
        }

        item {
            Text("Continue Watching & Chapters (${filteredVideos.size})", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }

        items(filteredVideos, key = { it.id }) { video ->
            val isSaved = bookmarkedIds.contains(video.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_"))
            val isActive = activeLesson?.id == video.id
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isActive) NeonCyan else NeonPurple.copy(alpha = 0.45f),
                onClick = {
                    activeLesson = video
                    progress = 0.05f
                    isPlaying = true
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.18f))
                    ) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = video.title, tint = NeonCyan, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(video.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("${video.subject} • ${video.lessonMode} • ${video.durationMinutes} min", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("Concepts: ${video.keyConcepts.joinToString(", ")}", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                    }
                    IconButton(onClick = { onToggleBookmark(video) }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark Video",
                            tint = if (isSaved) GalaxyGold else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedConceptStage(
    subject: String,
    mode: String,
    progress: Float,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val phase = if (isPlaying) {
        val inf = rememberInfiniteTransition(label = "lesson_canvas")
        val p by inf.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
            label = "wave"
        )
        p
    } else progress * (2f * PI).toFloat()

    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(Color(0xFF071130), Color(0xFF1A0F3D)))
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Grid lines
            val step = w / 10f
            for (i in 1..9) {
                drawLine(Color.White.copy(alpha = 0.06f), Offset(i * step, 0f), Offset(i * step, h), 1f)
            }
            for (j in 1..4) {
                val y = j * (h / 5f)
                drawLine(Color.White.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y), 1f)
            }

            when {
                subject.contains("Math", true) -> {
                    // Animated Parabola & Shifting Tangent
                    val path = Path()
                    val shift = sin(phase) * 0.14f
                    for (px in 0..80) {
                        val xNorm = (px / 80f) * 2f - 1f
                        val yNorm = (xNorm * xNorm) * 0.7f - 0.35f + shift
                        val sx = (px / 80f) * w
                        val sy = h * 0.55f - yNorm * (h * 0.45f)
                        if (px == 0) path.moveTo(sx, sy) else path.lineTo(sx, sy)
                    }
                    drawPath(path, color = NeonCyan, style = Stroke(width = 5f))
                    drawLine(GalaxyGold.copy(alpha = 0.7f), Offset(0f, h * 0.55f), Offset(w, h * 0.55f), 2f)
                }
                subject.contains("Physics", true) -> {
                    // 3D-style Radiating Electric Flux & Gaussian Ring
                    val cx = w * 0.5f
                    val cy = h * 0.5f
                    val pulseR = h * (0.28f + 0.06f * sin(phase))
                    drawCircle(color = NeonPurple.copy(alpha = 0.35f), radius = pulseR, center = Offset(cx, cy), style = Stroke(4f))
                    drawCircle(color = GalaxyGold, radius = 16f, center = Offset(cx, cy))
                    for (k in 0 until 8) {
                        val ang = (k * 45f) * PI / 180.0 + phase * 0.15f
                        val ex = cx + cos(ang).toFloat() * (h * 0.42f)
                        val ey = cy + sin(ang).toFloat() * (h * 0.42f)
                        drawLine(NeonCyan, Offset(cx, cy), Offset(ex, ey), strokeWidth = 3f)
                    }
                }
                else -> {
                    // Graph / Neural Network Nodes & Animated Signal Pulse
                    val nodes = listOf(
                        Offset(w * 0.2f, h * 0.3f),
                        Offset(w * 0.2f, h * 0.7f),
                        Offset(w * 0.5f, h * 0.25f),
                        Offset(w * 0.5f, h * 0.5f),
                        Offset(w * 0.5f, h * 0.75f),
                        Offset(w * 0.8f, h * 0.5f)
                    )
                    for (i in 0..1) {
                        for (j in 2..4) {
                            drawLine(NeonCyan.copy(alpha = 0.45f), nodes[i], nodes[j], 2.5f)
                        }
                    }
                    for (j in 2..4) {
                        drawLine(GalaxyGold.copy(alpha = 0.65f), nodes[j], nodes[5], 3f)
                    }
                    nodes.forEachIndexed { idx, pt ->
                        val r = 12f + 4f * sin(phase + idx)
                        drawCircle(color = if (idx == 5) NeonGreen else NeonCyan, radius = r, center = pt)
                    }
                }
            }
        }
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.55f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Text(
                text = "$mode • ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
