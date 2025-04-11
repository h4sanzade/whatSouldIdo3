package com.materialdesign.whatsouldido

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.json.JSONObject
import android.content.Context
import org.json.JSONException

class StatisticsActivity : AppCompatActivity() {

    lateinit var mainLayout: ConstraintLayout
    lateinit var statsContainer: LinearLayout
    lateinit var themeManager: ThemeManager
    val suggestionCounts = mutableMapOf<String, Int>()
    val categoryUsage = mutableMapOf<String, Int>()
    // Add reference to suggestionManager that is used in StatisticsActivityViews.kt
    val suggestionManager = SuggestionManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        themeManager = ThemeManager()
        themeManager.loadThemeSettings(this)

        initViews()
        loadData()
        updateTheme()
        displayStatistics()
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.statsMainLayout)
        statsContainer = findViewById(R.id.statsContainer)
    }

    private fun loadData() {
        loadCounts()
        calculateCategoryUsage()
    }

    private fun loadCounts() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val countsJson = sharedPrefs.getString("counts", null)
        suggestionCounts.clear()

        if (countsJson != null) {
            try {
                val jsonObj = JSONObject(countsJson)
                val keys = jsonObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    suggestionCounts[key] = jsonObj.getInt(key)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateCategoryUsage() {
        categoryUsage.clear()

        suggestionManager.loadSuggestions(this)

        // Her bir öneri için kategori kullanımını hesapla
        for ((suggestion, count) in suggestionCounts) {
            val category = suggestionManager.getCategoryForSuggestion(suggestion)
            if (category != null) {
                val categoryName = category.name
                categoryUsage[categoryName] = categoryUsage.getOrDefault(categoryName, 0) + count
            }
        }
    }

    private fun updateTheme() {
        // Arka plan rengini ayarla
        val backgroundColor = if (themeManager.isDarkMode) {
            ContextCompat.getColor(this, R.color.darkStart)
        } else {
            ContextCompat.getColor(this, R.color.lightStart)
        }
        mainLayout.setBackgroundColor(backgroundColor)
    }

    private fun displayStatistics() {
        // Mevcut istatistik görünümlerini temizle
        statsContainer.removeAllViews()

        // Kategori kullanım istatistiklerini göster
        addCategoryStatistics()

        // En çok kullanılan önerileri göster
        addTopSuggestionsStatistics()

        // Genel istatistikleri göster
        addGeneralStatistics()
    }
}