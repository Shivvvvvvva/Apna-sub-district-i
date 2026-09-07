package com.example.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode(val titleMr: String, val titleHi: String, val titleEn: String) {
    SYSTEM("सिस्टम प्रमाणे", "सिस्टम के अनुसार", "System Default"),
    LIGHT("☀️ प्रकाश (Light)", "☀️ लाइट मोड", "☀️ Light Mode"),
    DARK("🌙 गडद (Dark)", "🌙 डार्क मोड", "🌙 Dark Mode")
}

object ThemeManager {
    private const val PREFS_NAME = "gav_bus_theme_prefs"
    private const val KEY_THEME_MODE = "saved_theme_mode"

    var currentThemeMode by mutableStateOf(ThemeMode.SYSTEM)
    var isDarkThemeActive by mutableStateOf(false)

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        currentThemeMode = try {
            ThemeMode.valueOf(saved)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(context: Context, mode: ThemeMode) {
        currentThemeMode = mode
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun toggleDarkMode(context: Context) {
        val newMode = if (isDarkThemeActive) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(context, newMode)
    }
}
