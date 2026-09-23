package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = GreenForestPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainerLight,
    onPrimaryContainer = GreenOnContainer,
    secondary = GreenSageSecondary,
    onSecondary = Color.White,
    secondaryContainer = GreenSageContainer,
    onSecondaryContainer = GreenOnContainer,
    tertiary = GreenEmeraldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GreenEmeraldContainer,
    onTertiaryContainer = GreenOnContainer,
    background = WhiteMist,
    surface = WhitePure,
    surfaceVariant = GreenSurfaceMint,
    onBackground = TextDeepSlate,
    onSurface = TextDeepSlate,
    onSurfaceVariant = TextMuted,
    outline = GreenBorderCrisp,
    outlineVariant = GrayBorderLight
)

private val DarkColorScheme = lightColorScheme(
    // Retain serene white & green theme consistently
    primary = GreenForestPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainerLight,
    onPrimaryContainer = GreenOnContainer,
    secondary = GreenSageSecondary,
    onSecondary = Color.White,
    secondaryContainer = GreenSageContainer,
    onSecondaryContainer = GreenOnContainer,
    tertiary = GreenEmeraldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GreenEmeraldContainer,
    onTertiaryContainer = GreenOnContainer,
    background = WhiteMist,
    surface = WhitePure,
    surfaceVariant = GreenSurfaceMint,
    onBackground = TextDeepSlate,
    onSurface = TextDeepSlate,
    onSurfaceVariant = TextMuted,
    outline = GreenBorderCrisp,
    outlineVariant = GrayBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to light & bright theme as requested
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
