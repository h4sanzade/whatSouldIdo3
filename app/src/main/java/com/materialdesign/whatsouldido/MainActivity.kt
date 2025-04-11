package com.materialdesign.whatsouldido

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONException
import org.json.JSONObject
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var titleTextView: TextView
    private lateinit var emojiTextView: TextView
    private lateinit var suggestionTextView: TextView
    private lateinit var countTextView: TextView
    private lateinit var generateButton: Button
    private lateinit var addButton: Button
    private lateinit var manageButton: Button
    private lateinit var categoryChipGroup: ChipGroup
    private lateinit var favoriteButton: ImageButton
    private lateinit var settingsButton: ImageButton

    // Fix: Make sure suggestionManager is properly initialized and accessible to extension functions
    val suggestionManager = SuggestionManager()
    val animationManager = AnimationManager()
    val themeManager = ThemeManager()
    val suggestionCounts = mutableMapOf<String, Int>()
    val random = Random()
    val favorites = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadData()
        setupClickListeners()
        setupCategoryChips()
        loadFavorites()

        // Tema ayarlarını yükle
        themeManager.loadThemeSettings(this)
        updateTheme()
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.mainLayout)
        titleTextView = findViewById(R.id.titleTextView)
        emojiTextView = findViewById(R.id.emojiTextView)
        suggestionTextView = findViewById(R.id.suggestionTextView)
        countTextView = findViewById(R.id.countTextView)
        generateButton = findViewById(R.id.generateButton)
        addButton = findViewById(R.id.addButton)
        manageButton = findViewById(R.id.manageButton)
        categoryChipGroup = findViewById(R.id.categoryChipGroup)
        favoriteButton = findViewById(R.id.favoriteButton)
        settingsButton = findViewById(R.id.settingsButton)
    }

    private fun loadData() {
        suggestionManager.loadSuggestions(this)
        loadCounts()
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

    private fun saveCounts() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val jsonObj = JSONObject()

        for ((suggestion, count) in suggestionCounts) {
            jsonObj.put(suggestion, count)
        }

        editor.putString("counts", jsonObj.toString())
        editor.apply()
    }

    private fun setupClickListeners() {
        generateButton.setOnClickListener {
            generateRandomSuggestion()
        }

        addButton.setOnClickListener {
            showAddSuggestionDialog()
        }

        manageButton.setOnClickListener {
            showManageSuggestionsDialog()
        }

        favoriteButton.setOnClickListener {
            toggleFavorite(suggestionTextView.text.toString())
        }

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun setupCategoryChips() {
        categoryChipGroup.removeAllViews()

        // Tüm kategoriler için chip
        val allChip = Chip(this)
        allChip.text = "Tümü"
        allChip.isCheckable = true
        allChip.isChecked = true
        allChip.setChipBackgroundColorResource(R.color.colorPrimary)
        categoryChipGroup.addView(allChip)

        // Diğer kategoriler için chip'ler
        for (category in suggestionManager.getCategories()) {
            val chip = Chip(this)
            chip.text = "${category.emoji} ${category.name}"
            chip.isCheckable = true
            chip.chipBackgroundColor = ColorStateList.valueOf(category.color)
            categoryChipGroup.addView(chip)
        }

        // Chip seçildiğinde filtreleme yapılsın
        categoryChipGroup.setOnCheckedChangeListener { _, _ ->
            // Kategori filtrelemesi yapacağız
        }
    }

    fun generateRandomSuggestion() {
        if (suggestionManager.suggestionsList.isEmpty()) {
            Toast.makeText(this, "Henüz öneri eklenmemiş!", Toast.LENGTH_SHORT).show()
            return
        }

        val randomIndex = random.nextInt(suggestionManager.suggestionsList.size)
        val suggestion = suggestionManager.suggestionsList[randomIndex]

        animationManager.animateSuggestion(suggestionTextView, suggestion)
        updateEmoji(suggestion)
        updateCount(suggestion)

        // Favori durumunu güncelle
        updateFavoriteButton(suggestion)
    }

    fun updateEmoji(suggestion: String) {
        val category = suggestionManager.getCategoryForSuggestion(suggestion)
        val emoji = category?.emoji ?: "🎲"
        emojiTextView.text = emoji
        animationManager.animateEmoji(emojiTextView)
    }

    fun updateCount(suggestion: String) {
        val count = suggestionCounts.getOrDefault(suggestion, 0) + 1
        suggestionCounts[suggestion] = count
        countTextView.text = "$count kez önerildi"
        saveCounts()
    }

    fun updateTheme() {
        themeManager.updateBackground(this, mainLayout, suggestionTextView, countTextView)
    }
}