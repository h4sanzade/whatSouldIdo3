package com.materialdesign.whatsouldido

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.json.JSONObject
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import org.json.JSONException
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var statsContainer: LinearLayout
    private lateinit var themeManager: ThemeManager
    private val suggestionCounts = mutableMapOf<String, Int>()
    private val categoryUsage = mutableMapOf<String, Int>()

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

        val suggestionManager = SuggestionManager()
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