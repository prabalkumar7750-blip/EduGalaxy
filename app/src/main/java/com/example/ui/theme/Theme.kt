package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val EduGalaxyColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = GalaxyDeepNavy,
    primaryContainer = Color(0xFF0E3A7A),
    onPrimaryContainer = TextPrimary,
    secondary = NeonViolet,
    onSecondary = TextPrimary,
    secondaryContainer = Color(0xFF3B1A6E),
    onSecondaryContainer = TextPrimary,
    tertiary = GalaxyGold,
    onTertiary = GalaxyDeepNavy,
    tertiaryContainer = Color(0xFF473308),
    onTertiaryContainer = GalaxyGold,
    background = GalaxyDeepNavy,
    onBackground = TextPrimary,
    surface = GalaxySurface,
    onSurface = TextPrimary,
    surfaceVariant = GalaxySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = PurpleBorder,
    error = NeonRose,
    onError = Color.White,
)

private val EduGalaxyShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun EduGalaxyTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = EduGalaxyColorScheme,
        typography = EduGalaxyTypography,
        shapes = EduGalaxyShapes,
        content = content
    )
}
