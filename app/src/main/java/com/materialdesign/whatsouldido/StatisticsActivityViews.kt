package com.materialdesign.whatsouldido

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView

/**
 * StatisticsActivity's view creation functions
 */
fun StatisticsActivity.addCategoryStatistics() {
    if (categoryUsage.isEmpty()) return

    // Title
    val titleView = TextView(this).apply {
        text = "Kategori İstatistikleri"
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
    statsContainer.addView(titleView)

    // Category usage card
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

    // Category table
    val tableLayout = TableLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setColumnStretchable(0, true)
        setColumnStretchable(1, true)
    }

    // Table header
    val headerRow = TableRow(this).apply {
        layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val categoryHeader = TextView(this).apply {
        text = "Kategori"
        textSize = 16f
        setTypeface(null, Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(8, 16, 8, 16)
        setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
    }

    val countHeader = TextView(this).apply {
        text = "Kullanım"
        textSize = 16f
        setTypeface(null, Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(8, 16, 8, 16)
        setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
    }

    headerRow.addView(categoryHeader)
    headerRow.addView(countHeader)
    tableLayout.addView(headerRow)

    // Category data
    val sortedCategories = categoryUsage.entries.sortedByDescending { it.value }
    for ((category, count) in sortedCategories) {
        val dataRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val categoryView = TextView(this).apply {
            text = category
            textSize = 14f
            gravity = Gravity.START
            setPadding(8, 12, 8, 12)
            setTextColor(if (themeManager.isDarkMode) Color.LTGRAY else Color.DKGRAY)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        val countView = TextView(this).apply {
            text = count.toString()
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 12)
            setTextColor(if (themeManager.isDarkMode) Color.LTGRAY else Color.DKGRAY)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        dataRow.addView(categoryView)
        dataRow.addView(countView)
        tableLayout.addView(dataRow)
    }

    cardView.addView(tableLayout)
    statsContainer.addView(cardView)
}

fun StatisticsActivity.addTopSuggestionsStatistics() {
    if (suggestionCounts.isEmpty()) return

    // Title
    val titleView = TextView(this).apply {
        text = "En Çok Kullanılan Öneriler"
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
    statsContainer.addView(titleView)

    // Top suggestions card
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

    // Suggestions table
    val tableLayout = TableLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setColumnStretchable(0, true)
        setColumnStretchable(1, true)
    }

    // Table header
    val headerRow = TableRow(this).apply {
        layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val suggestionHeader = TextView(this).apply {
        text = "Öneri"
        textSize = 16f
        setTypeface(null, Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(8, 16, 8, 16)
        setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
    }

    val countHeader = TextView(this).apply {
        text = "Sayı"
        textSize = 16f
        setTypeface(null, Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(8, 16, 8, 16)
        setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
    }

    headerRow.addView(suggestionHeader)
    headerRow.addView(countHeader)
    tableLayout.addView(headerRow)

    // Suggestion data - top 10 most used suggestions
    val topSuggestions = suggestionCounts.entries
        .sortedByDescending { it.value }
        .take(10)

    for ((suggestion, count) in topSuggestions) {
        val dataRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val suggestionView = TextView(this).apply {
            text = suggestion
            textSize = 14f
            gravity = Gravity.START
            setPadding(8, 12, 8, 12)
            setTextColor(if (themeManager.isDarkMode) Color.LTGRAY else Color.DKGRAY)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        val countView = TextView(this).apply {
            text = count.toString()
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 12)
            setTextColor(if (themeManager.isDarkMode) Color.LTGRAY else Color.DKGRAY)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        dataRow.addView(suggestionView)
        dataRow.addView(countView)
        tableLayout.addView(dataRow)
    }

    cardView.addView(tableLayout)
    statsContainer.addView(cardView)
}

private fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    )
}