package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SentinelDarkColorScheme = darkColorScheme(
    primary = CyberCyanPrimary,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = CyberPurpleSecondary,
    onSecondary = Color.White,
    tertiary = CyberAmberWarning,
    onTertiary = Color.Black,
    error = CyberCrimsonAlert,
    onError = Color.White,
    background = CyberNavyDark,
    onBackground = CyberTextPrimary,
    surface = CyberSurfaceDark,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve our cyber defense palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SentinelDarkColorScheme,
        typography = Typography,
        content = content
    )
}
