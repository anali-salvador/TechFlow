package com.techflow.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val AppDarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnSurfaceLight,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = OnSurfaceLight,
    secondary = PrimaryBlue,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorRed,
    errorContainer = ErrorRed
)

@Composable
fun TechFlowTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Disabled to enforce brand identity (fondo siempre negro)
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography = Typography,
        content = content
    )
}
