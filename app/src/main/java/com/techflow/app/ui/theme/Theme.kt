package com.techflow.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Esquema de colores oscuro personalizado para TechFlow
private val DarkColorScheme = darkColorScheme(
    primary = TechBlueLight,
    onPrimary = BackgroundDark,
    primaryContainer = TechBlueDark,
    onPrimaryContainer = TechBlueSurface,
    secondary = TechGreenLight,
    tertiary = TechBlueLight,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = TechRedLight,
    errorContainer = TechRed,
    onErrorContainer = TechRedLight
)

// Esquema de colores claro personalizado para TechFlow
// Azul tecnológico como primary - profesional y moderno
private val LightColorScheme = lightColorScheme(
    primary = TechBlue,
    onPrimary = SurfaceLight,
    primaryContainer = TechBlueSurface,
    onPrimaryContainer = TechBlueDark,
    secondary = TechGreen,
    secondaryContainer = TechGreenLight,
    tertiary = TechBlueLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = TechRed,
    errorContainer = TechRedLight,
    onErrorContainer = TechRed
)

// TechFlowTheme - tema global de la app que aplica los colores y tipografía
// Se usa en MainActivity envolviendo toda la UI
// Soporta modo claro/oscuro automáticamente según la configuración del dispositivo
@Composable
fun TechFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic Color usa los colores del wallpaper del usuario (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
