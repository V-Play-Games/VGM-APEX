package net.vpg.apex

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ApexTheme(content: @Composable () -> Unit) = MaterialTheme(
    colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
    content = content
)

// Custom colours for app
object ApexColors {
    // Primary brand colours
    val GreenPrimary = Color(0xFF1DB954)
    val GreenLight = Color(0xFF1ED760)
    val GreenDark = Color(0xFF1AA34A)

    // Background colours
    val BackgroundDark = Color(0xFF121212)
    val BackgroundLight = Color(0xFFF5F5F5)
    val SurfaceDark = Color(0xFF242424)
    val SurfaceLight = Color(0xFFFFFFFF)
    val CardDark = Color(0xFF2A2A2A)
    val CardLight = Color(0xFFEAEAEA)

    // Text colors
    val TextPrimaryDark = Color.White
    val TextSecondaryDark = Color(0xFFB3B3B3)
    val TextPrimaryLight = Color(0xFF121212)
    val TextSecondaryLight = Color(0xFF535353)
}

private val DarkColors = darkColorScheme(
    primary = ApexColors.GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = ApexColors.GreenDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF1ED760),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1AA34A),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFB3B3B3),
    onTertiary = Color.Black,
    background = ApexColors.BackgroundDark,
    onBackground = ApexColors.TextPrimaryDark,
    surface = ApexColors.SurfaceDark,
    onSurface = ApexColors.TextPrimaryDark,
    surfaceVariant = ApexColors.CardDark,
    onSurfaceVariant = ApexColors.TextSecondaryDark,
    error = Color(0xFFE57373),
    onError = Color.Black
)

private val LightColors = lightColorScheme(
    primary = ApexColors.GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = ApexColors.GreenLight,
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF1ED760),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1AA34A),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF535353),
    onTertiary = Color.White,
    background = ApexColors.BackgroundLight,
    onBackground = ApexColors.TextPrimaryLight,
    surface = ApexColors.SurfaceLight,
    onSurface = ApexColors.TextPrimaryLight,
    surfaceVariant = ApexColors.CardLight,
    onSurfaceVariant = ApexColors.TextSecondaryLight,
    error = Color(0xFFB00020),
    onError = Color.White
)