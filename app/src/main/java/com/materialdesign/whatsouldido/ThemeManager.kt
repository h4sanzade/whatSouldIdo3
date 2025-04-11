package com.materialdesign.whatsouldido

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class ThemeManager {
    var currentTheme: String = "system"
    var isDarkMode: Boolean = false

    fun loadThemeSettings(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        currentTheme = sharedPrefs.getString("theme", "system") ?: "system"
        isDarkMode = when(currentTheme) {
            "system" -> isSystemInDarkMode(context)
            "dark" -> true
            "light" -> false
            "custom" -> isSystemInDarkMode(context)
            else -> isSystemInDarkMode(context)
        }
    }

    private fun isSystemInDarkMode(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun setTheme(theme: String, context: Context) {
        currentTheme = theme
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("theme", theme)
        editor.apply()
        isDarkMode = when(theme) {
            "system" -> isSystemInDarkMode(context)
            "dark" -> true
            "light" -> false
            "custom" -> isSystemInDarkMode(context)
            else -> isSystemInDarkMode(context)
        }
    }

    fun updateBackground(context: Context, mainLayout: ConstraintLayout, textView: TextView, countTextView: TextView) {
        val backgroundColor = if (isDarkMode) {
            ContextCompat.getColor(context, R.color.darkStart)
        } else {
            ContextCompat.getColor(context, R.color.lightStart)
        }

        mainLayout.setBackgroundColor(backgroundColor)
        textView.setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
        countTextView.setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
    }
}