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

private val LightColorScheme =
  lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    primaryContainer = SaffronContainer,
    onPrimaryContainer = OnSaffronContainer,
    secondary = MaroonSecondary,
    onSecondary = Color.White,
    secondaryContainer = MaroonContainer,
    onSecondaryContainer = OnMaroonContainer,
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = OnEmeraldContainer,
    background = WarmParchment,
    onBackground = HighContrastText,
    surface = WarmSurface,
    onSurface = HighContrastText,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = SubtitleText,
    outline = BorderColor,
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFFF8A50),
    onPrimary = Color(0xFF5D1700),
    primaryContainer = Color(0xFF862700),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFFFB4A9),
    onSecondary = Color(0xFF680003),
    secondaryContainer = Color(0xFF930006),
    onSecondaryContainer = Color(0xFFFFDAD4),
    tertiary = Color(0xFF86DDA0),
    onTertiary = Color(0xFF00391A),
    tertiaryContainer = Color(0xFF00522B),
    onTertiaryContainer = Color(0xFF98F7BD),
    background = Color(0xFF141210),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF1F1C18),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF2C2824),
    onSurfaceVariant = Color(0xFFD4C4B8),
    outline = Color(0xFF4E453E),
  )

@Composable
fun MyApplicationTheme(
  themeMode: ThemeMode = ThemeManager.currentThemeMode,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val systemDark = isSystemInDarkTheme()
  val darkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> systemDark
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }
  ThemeManager.isDarkThemeActive = darkTheme

  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

