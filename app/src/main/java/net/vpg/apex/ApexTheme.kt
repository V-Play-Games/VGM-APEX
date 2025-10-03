package net.vpg.apex

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import net.vpg.apex.core.ThemeMode
import net.vpg.apex.core.asStateValue
import net.vpg.apex.core.di.rememberSettings

@Composable
fun ApexTheme(content: @Composable () -> Unit) {
    val settings = rememberSettings()
    val themeMode = settings.theme.asStateValue()
    val accentColor = settings.accentColor.asStateValue()

    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark)
        StandardDarkColorScheme.copy(
            primary = Color(accentColor.primary),
            primaryContainer = Color(accentColor.dark),
            secondary = Color(accentColor.light),
            secondaryContainer = Color(accentColor.dark),
        )
    else
        StandardLightColorScheme.copy(
            primary = Color(accentColor.primary),
            primaryContainer = Color(accentColor.light),
            secondary = Color(accentColor.light),
            secondaryContainer = Color(accentColor.dark),
        )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// Custom colours for app
object ApexColors {
    // Background colours
    val BackgroundDark = Color(0xFF121212)
    val BackgroundLight = Color(0xFFF5F5F5)
    val SurfaceDark = Color(0xFF242424)
    val SurfaceLight = Color.White
    val CardDark = Color(0xFF2A2A2A)
    val CardLight = Color(0xFFEAEAEA)

    // Text colors
    val TextPrimaryDark = Color.White
    val TextSecondaryDark = Color(0xFFB3B3B3)
    val TextPrimaryLight = Color(0xFF121212)
    val TextSecondaryLight = Color(0xFF535353)
}

private val StandardDarkColorScheme = darkColorScheme(
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    onSecondary = Color.Black,
    onSecondaryContainer = Color.White,
    tertiary = ApexColors.TextSecondaryDark,
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

private val StandardLightColorScheme = lightColorScheme(
    onPrimary = Color.White,
    onPrimaryContainer = Color.Black,
    onSecondary = Color.Black,
    onSecondaryContainer = Color.White,
    tertiary = ApexColors.TextSecondaryLight,
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
