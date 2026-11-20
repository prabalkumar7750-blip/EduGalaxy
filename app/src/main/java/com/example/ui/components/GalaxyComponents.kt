package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyGlassCard
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.GalaxyMidnight
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class GalaxyStar(val xFrac: Float, val yFrac: Float, val radius: Float, val phase: Float, val color: Color)

@Composable
fun GalaxyStarfieldBackground(
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val stars = remember {
        listOf(
            GalaxyStar(0.12f, 0.08f, 2.2f, 0.0f, Color.White),
            GalaxyStar(0.28f, 0.14f, 1.6f, 1.2f, NeonCyan),
            GalaxyStar(0.75f, 0.06f, 2.5f, 2.4f, GalaxyGold),
            GalaxyStar(0.88f, 0.18f, 1.8f, 0.8f, Color.White),
            GalaxyStar(0.45f, 0.25f, 1.4f, 3.1f, NeonPurple),
            GalaxyStar(0.18f, 0.42f, 2.0f, 1.8f, Color.White),
            GalaxyStar(0.82f, 0.49f, 1.7f, 2.1f, NeonCyan),
            GalaxyStar(0.62f, 0.65f, 2.1f, 0.4f, Color.White),
            GalaxyStar(0.10f, 0.78f, 1.8f, 2.7f, GalaxyGold),
            GalaxyStar(0.91f, 0.85f, 2.4f, 1.5f, Color.White),
            GalaxyStar(0.38f, 0.91f, 1.6f, 0.9f, NeonCyan),
            GalaxyStar(0.55f, 0.36f, 1.5f, 2.9f, Color.White)
        )
    }

    val pulse = if (reducedMotion) {
        0.5f
    } else {
        val transition = rememberInfiniteTransition(label = "galaxy_stars")
        val anim by transition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "star_phase"
        )
        anim
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GalaxyDeepNavy, GalaxyMidnight, Color(0xFF0A0D28))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Nebula glow accents
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.20f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.12f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonBlue.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.12f, size.height * 0.38f),
                    radius = size.width * 0.5f
                ),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.12f, size.height * 0.38f)
            )
            stars.forEach { star ->
                val alpha = if (reducedMotion) 0.7f else (0.35f + 0.55f * (0.5f + 0.5f * sin(pulse + star.phase)))
                drawCircle(
                    color = star.color.copy(alpha = alpha.coerceIn(0.2f, 0.95f)),
                    radius = star.radius * density,
                    center = Offset(size.width * star.xFrac, size.height * star.yFrac)
                )
            }
        }
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = NeonCyan.copy(alpha = 0.4f),
    glowColor: Color = NeonBlue.copy(alpha = 0.14f),
    cornerRadius: Dp = 18.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Surface(
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, NeonPurple.copy(alpha = 0.35f))
                ),
                shape = shape
            ),
        shape = shape,
        color = GalaxyGlassCard,
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(glowColor, Color.Transparent)
                )
            )
        ) {
            content()
        }
    }
}

@Composable
fun EduGalaxyBrandLogo(
    size: Dp = 44.dp,
    animated: Boolean = true
) {
    val orbitAngle = if (animated) {
        val inf = rememberInfiniteTransition(label = "logo_orbit")
        val a by inf.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
            label = "orbit"
        )
        a
    } else 45f

    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Glow aura
        drawCircle(
            brush = Brush.radialGradient(
                listOf(NeonCyan.copy(alpha = 0.35f), Color.Transparent)
            ),
            radius = w * 0.55f
        )
        // Open book wings
        val leftBook = Path().apply {
            moveTo(w * 0.49f, h * 0.55f)
            lineTo(w * 0.16f, h * 0.48f)
            lineTo(w * 0.16f, h * 0.78f)
            lineTo(w * 0.49f, h * 0.84f)
            close()
        }
        val rightBook = Path().apply {
            moveTo(w * 0.51f, h * 0.55f)
            lineTo(w * 0.84f, h * 0.48f)
            lineTo(w * 0.84f, h * 0.78f)
            lineTo(w * 0.51f, h * 0.84f)
            close()
        }
        drawPath(leftBook, color = Color(0xFFE0F7FA))
        drawPath(rightBook, color = Color.White)

        // Graduation cap
        val cap = Path().apply {
            moveTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.32f)
            lineTo(w * 0.50f, h * 0.46f)
            lineTo(w * 0.22f, h * 0.32f)
            close()
        }
        drawPath(cap, color = NeonCyan)

        // Orbital gold ring
        drawArc(
            color = GalaxyGold,
            startAngle = -35f,
            sweepAngle = 215f,
            useCenter = false,
            style = Stroke(width = w * 0.06f, cap = StrokeCap.Round),
            topLeft = Offset(w * 0.10f, h * 0.12f),
            size = androidx.compose.ui.geometry.Size(w * 0.80f, h * 0.65f)
        )
        // Orbiting star dot
        val rad = orbitAngle * PI / 180.0
        val starX = w * 0.5f + (w * 0.40f) * cos(rad).toFloat()
        val starY = h * 0.44f + (h * 0.26f) * sin(rad).toFloat()
        drawCircle(color = GalaxyGold, radius = w * 0.055f, center = Offset(starX, starY))
    }
}

@Composable
fun AnimatedProgressRing(
    progress: Float,
    color: Color = NeonCyan,
    size: Dp = 76.dp,
    strokeWidth: Dp = 7.dp,
    label: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "ring_progress"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            drawArc(
                color = Color.White.copy(alpha = 0.10f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(color, NeonPurple, GalaxyGold, color)),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

fun subjectIconFor(subjectName: String): ImageVector = when {
    subjectName.contains("Math", true) || subjectName.contains("Quant", true) -> Icons.Filled.Calculate
    subjectName.contains("Physics", true) || subjectName.contains("Science", true) -> Icons.Filled.Science
    subjectName.contains("Chem", true) -> Icons.Filled.Science
    subjectName.contains("Bio", true) || subjectName.contains("Botany", true) -> Icons.Filled.Biotech
    subjectName.contains("Computer", true) || subjectName.contains("Data", true) -> Icons.Filled.Computer
    subjectName.contains("Program", true) -> Icons.Filled.Code
    subjectName.contains("AI", true) || subjectName.contains("Reason", true) -> Icons.Filled.Psychology
    subjectName.contains("Econ", true) || subjectName.contains("Account", true) || subjectName.contains("Stat", true) -> Icons.Filled.ShowChart
    subjectName.contains("History", true) || subjectName.contains("Civics", true) -> Icons.Filled.HistoryEdu
    subjectName.contains("Geo", true) || subjectName.contains("Social", true) -> Icons.Filled.Public
    subjectName.contains("Hindi", true) || subjectName.contains("Sanskrit", true) || subjectName.contains("English", true) -> Icons.Filled.Translate
    subjectName.contains("GK", true) -> Icons.Filled.EmojiEvents
    else -> Icons.Filled.Book
}

@Composable
fun Subject3DIconBadge(
    subjectName: String,
    accentColor: Color,
    size: Dp = 40.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(accentColor.copy(alpha = 0.85f), accentColor.copy(alpha = 0.28f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Icon(
            imageVector = subjectIconFor(subjectName),
            contentDescription = subjectName,
            tint = Color.White,
            modifier = Modifier.size(size * 0.56f)
        )
    }
}

@Composable
fun BilingualToggleChip(
    selectedLanguage: String,
    onSelect: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("English", "Hindi", "Bilingual").forEach { lang ->
            val selected = selectedLanguage == lang
            Surface(
                shape = CircleShape,
                color = if (selected) NeonCyan.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, if (selected) NeonCyan else Color.White.copy(alpha = 0.16f)),
                modifier = Modifier.clickable { onSelect(lang) }
            ) {
                Text(
                    text = if (lang == "Hindi") "हिन्दी" else lang,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) NeonCyan else TextSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyGalaxyState(
    title: String,
    subtitle: String,
    icon: ImageVector = Icons.Filled.Lightbulb
) {
    GlassCard(
        modifier = Modifier.padding(vertical = 8.dp),
        borderColor = NeonPurple.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = GalaxyGold, modifier = Modifier.size(40.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}
