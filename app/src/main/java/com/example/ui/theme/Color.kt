package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Rural MSRTC Saffron Orange, Deep Maroon, and Emerald Green Palette
val SaffronPrimary = Color(0xFFC2410C)
val SaffronDark = Color(0xFF9A3412)
val SaffronLight = Color(0xFFEA580C)
val SaffronContainer = Color(0xFFFFEDD5)
val OnSaffronContainer = Color(0xFF7C2D12)

val MaroonSecondary = Color(0xFF991B1B)
val MaroonDark = Color(0xFF7F1D1D)
val MaroonContainer = Color(0xFFFEE2E2)
val OnMaroonContainer = Color(0xFF450A0A)

val EmeraldGreen = Color(0xFF15803D)
val EmeraldLight = Color(0xFF16A34A)
val EmeraldContainer = Color(0xFFDCFCE7)
val OnEmeraldContainer = Color(0xFF14532D)

// Soft Pastel Sky Blue Theme (Requested by user)
val SkyBlueBackground = Color(0xFFB5D0F8)
val SkyBlueSurface = Color(0xFFFFFFFF)
val SkyBlueSurfaceVariant = Color(0xFFE8F1FC)
val SkyBlueBorder = Color(0xFF9EC1F2)

val WarmParchment = SkyBlueBackground
val WarmSurface = SkyBlueSurface
val WarmSurfaceVariant = SkyBlueSurfaceVariant
val HighContrastText = Color(0xFF18181B)
val SubtitleText = Color(0xFF374151)
val BorderColor = SkyBlueBorder

val GoldenYellow = Color(0xFFD97706)
val GoldenContainer = Color(0xFFFEF3C7)

// Dynamic Theme-Aware Color Accessors
@androidx.compose.runtime.Composable
fun appSurfaceColor(): Color = if (ThemeManager.isDarkThemeActive) Color(0xFF1F1C18) else Color.White

@androidx.compose.runtime.Composable
fun appBackgroundColor(): Color = if (ThemeManager.isDarkThemeActive) Color(0xFF141210) else SkyBlueBackground

@androidx.compose.runtime.Composable
fun appSurfaceVariantColor(): Color = if (ThemeManager.isDarkThemeActive) Color(0xFF2C2824) else SkyBlueSurfaceVariant

@androidx.compose.runtime.Composable
fun appTextColor(): Color = if (ThemeManager.isDarkThemeActive) Color(0xFFEDE0D4) else HighContrastText

@androidx.compose.runtime.Composable
fun appSubtitleColor(): Color = if (ThemeManager.isDarkThemeActive) Color(0xFFB0A49B) else SubtitleText

@androidx.compose.runtime.Composable
fun appBorderColor(): Color = if (ThemeManager.isDarkThemeActive) Color(0xFF3E3730) else SkyBlueBorder


