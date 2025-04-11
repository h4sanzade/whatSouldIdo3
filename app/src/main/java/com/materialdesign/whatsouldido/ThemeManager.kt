package com.materialdesign.whatsouldido

import android.content.Context
import android.content.res.Configuration
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

        // Sistem temasına göre karanlık mod durumunu belirle
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

        // Tema değişikliğini kaydet
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("theme", theme)
        editor.apply()

        // Karanlık mod durumunu güncelle
        isDarkMode = when(theme) {
            "system" -> isSystemInDarkMode(context)
            "dark" -> true
            "light" -> false
            "custom" -> isSystemInDarkMode(context)
            else -> isSystemInDarkMode(context)
        }
    }

    fun updateBackground(context: Context, mainLayout: ConstraintLayout, textView: TextView, countTextView: TextView) {
        // Tema renklerini ayarla
        val backgroundColor = if (isDarkMode) {
            ContextCompat.getColor(context, R.color.darkStart)
            textView.setTextColor(ContextCompat.getColor(context, R.color.white))
            countTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            ContextCompat.getColor(context, R.color.lightStart)
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
            countTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        mainLayout.setBackgroundColor(backgroundColor)
    }
}