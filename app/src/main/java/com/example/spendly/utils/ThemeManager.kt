package com.example.spendly.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREFS_NAME = "spendly_theme_prefs"
    private const val KEY_THEME_MODE = "key_theme_mode"
    private const val KEY_ACCENT_COLOR = "key_accent_color"

    const val MODE_SYSTEM = 0
    const val MODE_LIGHT = 1
    const val MODE_DARK = 2

    const val ACCENT_PURPLE = "#7C4DFF"
    const val ACCENT_BLUE = "#2980B9"
    const val ACCENT_GREEN = "#2ECC71"
    const val ACCENT_ORANGE = "#E67E22"

    fun initTheme(context: Context) {
        val mode = getThemeMode(context)
        applyThemeMode(mode)
    }

    fun getThemeMode(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM)
    }

    fun setThemeMode(context: Context, mode: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_THEME_MODE, mode)
            .apply()
        applyThemeMode(mode)
    }

    fun getAccentColor(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACCENT_COLOR, ACCENT_PURPLE) ?: ACCENT_PURPLE
    }

    fun setAccentColor(context: Context, colorHex: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ACCENT_COLOR, colorHex)
            .apply()
    }

    private fun applyThemeMode(mode: Int) {
        val nightMode = when (mode) {
            MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}