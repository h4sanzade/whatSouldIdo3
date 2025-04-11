package com.materialdesign.whatsouldido

import android.graphics.Color
import android.os.Bundle
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

class StatisticsActivity : AppCompatActivity() {
    lateinit var mainLayout: ConstraintLayout
    lateinit var statsContainer: LinearLayout
    lateinit var themeManager: ThemeManager
    val suggestionCounts = mutableMapOf<String, Int>()
    val categoryUsage = mutableMapOf<String, Int>()
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

        for ((suggestion, count) in suggestionCounts) {
            val category = suggestionManager.getCategoryForSuggestion(suggestion)
            if (category != null) {
                val categoryName = category.name
                categoryUsage[categoryName] = categoryUsage.getOrDefault(categoryName, 0) + count
            }
        }
    }

    private fun updateTheme() {
        val backgroundColor = if (themeManager.isDarkMode) {
            ContextCompat.getColor(this, R.color.darkStart)
        } else {
            ContextCompat.getColor(this, R.color.lightStart)
        }
        mainLayout.setBackgroundColor(backgroundColor)
    }

    private fun displayStatistics() {
        statsContainer.removeAllViews()
        addCategoryStatistics()
        addTopSuggestionsStatistics()
        addGeneralStatistics()
    }

    // StatisticsActivityViews.kt functions
    fun addCategoryStatistics() {
        if (categoryUsage.isEmpty()) return

        val titleView = createSectionTitleView("Kategori İstatistikleri")
        statsContainer.addView(titleView)

        val cardView = CardView(this).apply {
            radius = dpToPx(8f)
            elevation = dpToPx(4f)
            setContentPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 16)
            }
        }

        val tableLayout = TableLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isStretchAllColumns = true
        }

        val headerRow = TableRow(this)
        val categoryHeader = createTableHeaderTextView("Kategori")
        val countHeader = createTableHeaderTextView("Kullanım")

        headerRow.addView(categoryHeader)
        headerRow.addView(countHeader)
        tableLayout.addView(headerRow)

        val sortedCategories = categoryUsage.entries.sortedByDescending { it.value }
        for ((category, count) in sortedCategories) {
            val dataRow = TableRow(this)
            val categoryView = createTableCellTextView(category)
            val countView = createTableCellTextView(count.toString())

            dataRow.addView(categoryView)
            dataRow.addView(countView)
            tableLayout.addView(dataRow)
        }

        cardView.addView(tableLayout)
        statsContainer.addView(cardView)
    }

    fun addTopSuggestionsStatistics() {
        if (suggestionCounts.isEmpty()) return

        val titleView = createSectionTitleView("En Çok Kullanılan Öneriler")
        statsContainer.addView(titleView)

        val cardView = CardView(this).apply {
            radius = dpToPx(8f)
            elevation = dpToPx(4f)
            setContentPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 16)
            }
        }

        val tableLayout = TableLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isStretchAllColumns = true
        }

        val headerRow = TableRow(this)
        val suggestionHeader = createTableHeaderTextView("Öneri")
        val countHeader = createTableHeaderTextView("Sayı")

        headerRow.addView(suggestionHeader)
        headerRow.addView(countHeader)
        tableLayout.addView(headerRow)

        val topSuggestions = suggestionCounts.entries
            .sortedByDescending { it.value }
            .take(10)

        for ((suggestion, count) in topSuggestions) {
            val dataRow = TableRow(this)
            val suggestionView = createTableCellTextView(suggestion)
            val countView = createTableCellTextView(count.toString())

            dataRow.addView(suggestionView)
            dataRow.addView(countView)
            tableLayout.addView(dataRow)
        }

        cardView.addView(tableLayout)
        statsContainer.addView(cardView)
    }

    fun addGeneralStatistics() {
        val titleView = createSectionTitleView("Genel İstatistikler")
        statsContainer.addView(titleView)

        val cardView = CardView(this).apply {
            radius = dpToPx(8f)
            elevation = dpToPx(4f)
            setContentPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 16)
            }
        }

        val statsLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 8)
        }

        val totalSuggestions = suggestionManager.suggestionsList.size
        val totalSuggestionsView = createStatItemView("Toplam Öneri Sayısı", totalSuggestions.toString())
        statsLayout.addView(totalSuggestionsView)

        val totalUsage = suggestionCounts.values.sum()
        val totalUsageView = createStatItemView("Toplam Kullanım", totalUsage.toString())
        statsLayout.addView(totalUsageView)

        val averageUsage = if (suggestionCounts.isNotEmpty()) {
            String.format("%.2f", totalUsage.toFloat() / suggestionCounts.size)
        } else {
            "0"
        }
        val averageUsageView = createStatItemView("Ortalama Kullanım", averageUsage)
        statsLayout.addView(averageUsageView)

        val mostUsedSuggestion = suggestionCounts.entries.maxByOrNull { it.value }
        if (mostUsedSuggestion != null) {
            val mostUsedView = createStatItemView(
                "En Çok Kullanılan Öneri",
                "${mostUsedSuggestion.key} (${mostUsedSuggestion.value} kez)"
            )
            statsLayout.addView(mostUsedView)
        }

        cardView.addView(statsLayout)
        statsContainer.addView(cardView)
    }

    private fun createSectionTitleView(title: String): TextView {
        return TextView(this).apply {
            text = title
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 24, 16, 8)
            }
            setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        }
    }

    private fun createTableHeaderTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(8, 16, 8, 16)
            setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        }
    }

    private fun createTableCellTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            gravity = if (text.matches(Regex("\\d+"))) Gravity.CENTER else Gravity.START
            setPadding(8, 12, 8, 12)
            setTextColor(if (themeManager.isDarkMode) Color.LTGRAY else Color.DKGRAY)
        }
    }

    private fun createStatItemView(label: String, value: String): LinearLayout {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        val labelView = TextView(this).apply {
            text = "$label: "
            setTypeface(null, Typeface.BOLD)
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        }

        val valueView = TextView(this).apply {
            text = value
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setTextColor(if (themeManager.isDarkMode) Color.LTGRAY else Color.DKGRAY)
        }

        itemLayout.addView(labelView)
        itemLayout.addView(valueView)

        return itemLayout
    }

    private fun Context.dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }
}