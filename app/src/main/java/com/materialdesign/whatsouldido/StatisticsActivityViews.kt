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
import androidx.core.content.ContextCompat

/**
 * StatisticsActivity'nin görünüm oluşturma fonksiyonlarını içeren extension class
 */
fun StatisticsActivity.addCategoryStatistics() {
    if (categoryUsage.isEmpty()) return

    // Başlık
    val titleView = createSectionTitleView("Kategori İstatistikleri")
    statsContainer.addView(titleView)

    // Kategori kullanım kartı
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

    // Kategori tablosu
    val tableLayout = TableLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        stretchAllColumns = true
        isStretchAllColumns = true
    }

    // Tablo başlığı
    val headerRow = TableRow(this)
    val categoryHeader = createTableHeaderTextView("Kategori")
    val countHeader = createTableHeaderTextView("Kullanım")

    headerRow.addView(categoryHeader)
    headerRow.addView(countHeader)
    tableLayout.addView(headerRow)

    // Kategori verileri
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

fun StatisticsActivity.addTopSuggestionsStatistics() {
    if (suggestionCounts.isEmpty()) return

    // Başlık
    val titleView = createSectionTitleView("En Çok Kullanılan Öneriler")
    statsContainer.addView(titleView)

    // Top öneriler kartı
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

    // Öneriler tablosu
    val tableLayout = TableLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        stretchAllColumns = true
        isStretchAllColumns = true
    }

    // Tablo başlığı
    val headerRow = TableRow(this)
    val suggestionHeader = createTableHeaderTextView("Öneri")
    val countHeader = createTableHeaderTextView("Sayı")

    headerRow.addView(suggestionHeader)
    headerRow.addView(countHeader)
    tableLayout.addView(headerRow)

    // Öneri verileri - en çok kullanılan 10 öneri
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

fun StatisticsActivity.addGeneralStatistics() {
    // Başlık
    val titleView = createSectionTitleView("Genel İstatistikler")
    statsContainer.addView(titleView)

    // Genel istatistikler kartı
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

    // Genel istatistikler container
    val statsLayout = LinearLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        orientation = LinearLayout.VERTICAL
        setPadding(8, 8, 8, 8)
    }

    // Toplam öneri sayısı
    val totalSuggestions = suggestionManager.suggestionsList.size
    val totalSuggestionsView = createStatItemView("Toplam Öneri Sayısı", totalSuggestions.toString())
    statsLayout.addView(totalSuggestionsView)

    // Toplam kullanım sayısı
    val totalUsage = suggestionCounts.values.sum()
    val totalUsageView = createStatItemView("Toplam Kullanım", totalUsage.toString())
    statsLayout.addView(totalUsageView)

    // Ortalama kullanım sayısı
    val averageUsage = if (suggestionCounts.isNotEmpty()) {
        String.format("%.2f", totalUsage.toFloat() / suggestionCounts.size)
    } else {
        "0"
    }
    val averageUsageView = createStatItemView("Ortalama Kullanım", averageUsage)
    statsLayout.addView(averageUsageView)

    // En çok kullanılan öneri
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

// Yardımcı görünüm oluşturma fonksiyonları
private fun StatisticsActivity.createSectionTitleView(title: String): TextView {
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
        if (themeManager.isDarkMode) {
            setTextColor(Color.WHITE)
        } else {
            setTextColor(Color.BLACK)
        }
    }
}

private fun StatisticsActivity.createTableHeaderTextView(text: String): TextView {
    return TextView(this).apply {
        this.text = text
        textSize = 16f
        setTypeface(null, Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(8, 16, 8, 16)
        if (themeManager.isDarkMode) {
            setTextColor(Color.WHITE)
        } else {
            setTextColor(Color.BLACK)
        }
    }
}

private fun StatisticsActivity.createTableCellTextView(text: String): TextView {
    return TextView(this).apply {
        this.text = text
        textSize = 14f
        gravity = if (text.matches(Regex("\\d+"))) Gravity.CENTER else Gravity.START
        setPadding(8, 12, 8, 12)
        if (themeManager.isDarkMode) {
            setTextColor(Color.LTGRAY)
        } else {
            setTextColor(Color.DKGRAY)
        }
    }
}

private fun StatisticsActivity.createStatItemView(label: String, value: String): LinearLayout {
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
        if (themeManager.isDarkMode) {
            setTextColor(Color.WHITE)
        } else {
            setTextColor(Color.BLACK)
        }
    }

    val valueView = TextView(this).apply {
        text = value
        textSize = 16f
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        if (themeManager.isDarkMode) {
            setTextColor(Color.LTGRAY)
        } else {
            setTextColor(Color.DKGRAY)
        }
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

fun StatisticsActivity.addTimeBasedStatistics() {
    if (timeUsage.isEmpty()) return

    // Başlık
    val titleView = createSectionTitleView("Zaman Bazlı İstatistikler")
    statsContainer.addView(titleView)

    // Zaman bazlı istatistikler kartı
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

    // Zaman bazlı istatistikler tablosu
    val tableLayout = TableLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        stretchAllColumns = true
        isStretchAllColumns = true
    }

    // Tablo başlığı
    val headerRow = TableRow(this)
    val timeHeader = createTableHeaderTextView("Zaman Aralığı")
    val usageHeader = createTableHeaderTextView("Kullanım Sayısı")

    headerRow.addView(timeHeader)
    headerRow.addView(usageHeader)
    tableLayout.addView(headerRow)

    // Zaman verileri
    val sortedTimeUsage = timeUsage.entries.sortedByDescending { it.value }
    for ((timeRange, count) in sortedTimeUsage) {
        val dataRow = TableRow(this)
        val timeView = createTableCellTextView(timeRange)
        val countView = createTableCellTextView(count.toString())

        dataRow.addView(timeView)
        dataRow.addView(countView)
        tableLayout.addView(dataRow)
    }

    cardView.addView(tableLayout)
    statsContainer.addView(cardView)
}

fun StatisticsActivity.addMoodStatistics() {
    if (moodUsage.isEmpty()) return

    // Başlık
    val titleView = createSectionTitleView("Ruh Hali İstatistikleri")
    statsContainer.addView(titleView)

    // Ruh hali istatistikleri kartı
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

    // Ruh hali istatistikleri tablosu
    val tableLayout = TableLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        stretchAllColumns = true
        isStretchAllColumns = true
    }

    // Tablo başlığı
    val headerRow = TableRow(this)
    val moodHeader = createTableHeaderTextView("Ruh Hali")
    val countHeader = createTableHeaderTextView("Kullanım Sayısı")

    headerRow.addView(moodHeader)
    headerRow.addView(countHeader)
    tableLayout.addView(headerRow)

    val sortedMoodUsage = moodUsage.entries.sortedByDescending { it.value }
    for ((mood, count) in sortedMoodUsage) {
        val dataRow = TableRow(this)
        val moodView = createTableCellTextView(mood)
        val countView = createTableCellTextView(count.toString())

        dataRow.addView(moodView)
        dataRow.addView(countView)
        tableLayout.addView(dataRow)
    }

    cardView.addView(tableLayout)
    statsContainer.addView(cardView)
}