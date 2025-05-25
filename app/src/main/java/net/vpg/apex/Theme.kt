package net.vpg.apex

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Custom colors for app
object ApexColors {
    // Primary brand colors
    val GreenPrimary = Color(0xFF1DB954)
    val GreenLight = Color(0xFF1ED760)
    val GreenDark = Color(0xFF1AA34A)

    // Background colors
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

private val ApexTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    )
)

// Custom shapes
private val ApexShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

@Composable
fun ApexTheme(content: @Composable () -> Unit) = MaterialTheme(
    colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
    typography = ApexTypography,
    shapes = ApexShapes,
    content = content
)