package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BrandAccent = Color(0xFF3B82F6)
val BrandAccentSecondary = Color(0xFF6366F1)
val BrandGood = Color(0xFF22C55E)
val BrandDanger = Color(0xFFEF4444)
val BrandWarning = Color(0xFFF59E0B)

// Dark Theme Colors (Student OS Dark)
val DarkBackground = Color(0xFF07101D)
val DarkSurface = Color(0xFF0D1929)
val DarkSurfaceVariant = Color(0xFF101F33)
val DarkOutline = Color(0xFF1D3048)
val DarkText = Color(0xFFEEF5FF)
val DarkMuted = Color(0xFF91A2B8)

// Light Theme Colors
val LightBackground = Color(0xFFF4F7FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF3F6FB)
val LightOutline = Color(0xFFDCE5F0)
val LightText = Color(0xFF14213D)
val LightMuted = Color(0xFF607087)

private val DarkColorScheme = darkColorScheme(
    primary = BrandAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF142945),
    onPrimaryContainer = Color(0xFF8FC0FF),
    secondary = BrandAccentSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF233554),
    onSecondaryContainer = Color(0xFFD4E2FF),
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkMuted,
    outline = DarkOutline,
    outlineVariant = Color(0xFF24456C),
    error = BrandDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF4F46E5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF3730A3),
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightMuted,
    outline = LightOutline,
    outlineVariant = Color(0xFFCBD5E1),
    error = BrandDanger,
    onError = Color.White
)

@Composable
fun StudyTrackerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
