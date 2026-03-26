package com.example.collegeschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NatkBlueLight,
    onPrimary = NatkTextDark,
    secondary = NatkAccent,
    background = NatkBlueDark,
    onBackground = NatkTextLight,
    surface = Color(0xFF123A68),
    onSurface = NatkTextLight
)

private val LightColorScheme = lightColorScheme(
    primary = NatkBlue,
    onPrimary = NatkTextLight,
    secondary = NatkAccent,
    background = NatkBackground,
    onBackground = NatkTextDark,
    surface = NatkSurface,
    onSurface = NatkTextDark
)

@Composable
fun CollegeScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}