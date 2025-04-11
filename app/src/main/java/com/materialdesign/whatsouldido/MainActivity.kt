package com.materialdesign.whatsouldido

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    internal lateinit var favoriteButton: ImageButton
    private lateinit var settingsButton: ImageButton

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

        val allChip = Chip(this)
        allChip.text = "Tümü"
        allChip.isCheckable = true
        allChip.isChecked = true
        allChip.setChipBackgroundColorResource(R.color.colorPrimary)
        categoryChipGroup.addView(allChip)

        for (category in suggestionManager.getCategories()) {
            val chip = Chip(this)
            chip.text = "${category.emoji} ${category.name}"
            chip.isCheckable = true
            chip.chipBackgroundColor = ColorStateList.valueOf(category.color)
            categoryChipGroup.addView(chip)
        }

        categoryChipGroup.setOnCheckedChangeListener { _, _ -> }
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

    // MainActivityDialogs.kt functions
    fun showAddSuggestionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_suggestion, null)
        val suggestionEditText = dialogView.findViewById<EditText>(R.id.suggestionEditText)

        AlertDialog.Builder(this)
            .setTitle("Yeni Öneri Ekle")
            .setView(dialogView)
            .setPositiveButton("Ekle") { _, _ ->
                val newSuggestion = suggestionEditText.text.toString().trim()
                if (newSuggestion.isNotEmpty()) {
                    if (!suggestionManager.suggestionsList.contains(newSuggestion)) {
                        suggestionManager.suggestionsList.add(newSuggestion)
                        suggestionManager.saveSuggestions(this)
                        Toast.makeText(this, "Öneri eklendi!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Bu öneri zaten var!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Boş öneri eklenemez!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    fun showManageSuggestionsDialog() {
        if (suggestionManager.suggestionsList.isEmpty()) {
            Toast.makeText(this, "Henüz öneri eklenmemiş!", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_suggestions, null)
        val listView = dialogView.findViewById<ListView>(R.id.suggestionsListView)

        val adapter = SuggestionsAdapter(this, suggestionManager.suggestionsList) { position ->
            suggestionManager.suggestionsList.removeAt(position)
            suggestionManager.saveSuggestions(this)
        }

        listView.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Önerileri Yönet")
            .setView(dialogView)
            .setPositiveButton("Tamam", null)
            .show()
    }

    fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)

        val themeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.themeRadioGroup)
        val statsButton = dialogView.findViewById<Button>(R.id.statisticsButton)
        val favoritesButton = dialogView.findViewById<Button>(R.id.favoritesButton)

        when (themeManager.currentTheme) {
            "system" -> themeRadioGroup.check(R.id.radioSystem)
            "light" -> themeRadioGroup.check(R.id.radioLight)
            "dark" -> themeRadioGroup.check(R.id.radioDark)
            "custom" -> themeRadioGroup.check(R.id.radioCustom)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Ayarlar")
            .setView(dialogView)
            .setPositiveButton("Tamam") { _, _ ->
                val selectedId = themeRadioGroup.checkedRadioButtonId
                val selectedTheme = when (selectedId) {
                    R.id.radioSystem -> "system"
                    R.id.radioLight -> "light"
                    R.id.radioDark -> "dark"
                    R.id.radioCustom -> "custom"
                    else -> "system"
                }

                if (selectedTheme != themeManager.currentTheme) {
                    themeManager.setTheme(selectedTheme, this)
                    updateTheme()
                }
            }
            .setNegativeButton("İptal", null)
            .create()

        statsButton.setOnClickListener {
            dialog.dismiss()
            showStatisticsActivity()
        }

        favoritesButton.setOnClickListener {
            dialog.dismiss()
            showFavoritesDialog()
        }

        dialog.show()
    }

    fun showStatisticsActivity() {
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }

    fun showFavoritesDialog() {
        if (favorites.isEmpty()) {
            Toast.makeText(this, "Henüz favori önerin yok!", Toast.LENGTH_SHORT).show()
            return
        }

        val favoritesList = favorites.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favoritesList)

        AlertDialog.Builder(this)
            .setTitle("Favori Önerilerim")
            .setAdapter(adapter) { _, position ->
                val selectedSuggestion = favoritesList[position]
                animationManager.animateSuggestion(suggestionTextView, selectedSuggestion)
                updateEmoji(selectedSuggestion)
                updateCount(selectedSuggestion)
                updateFavoriteButton(selectedSuggestion)
            }
            .setPositiveButton("Tamam", null)
            .show()
    }

    // MainActivityFavorites.kt functions
    fun toggleFavorite(suggestion: String) {
        if (suggestion.isEmpty() || suggestion == "Öneri için butona tıkla!") return

        if (favorites.contains(suggestion)) {
            favorites.remove(suggestion)
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
        } else {
            favorites.add(suggestion)
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)

            val scaleX = ObjectAnimator.ofFloat(favoriteButton, "scaleX", 1f, 1.3f, 1f)
            val scaleY = ObjectAnimator.ofFloat(favoriteButton, "scaleY", 1f, 1.3f, 1f)
            scaleX.duration = 300
            scaleY.duration = 300
            AnimatorSet().apply {
                playTogether(scaleX, scaleY)
                start()
            }
        }

        saveFavorites()
    }

    fun updateFavoriteButton(suggestion: String) {
        favoriteButton.setImageResource(
            if (favorites.contains(suggestion))
                android.R.drawable.btn_star_big_on
            else
                android.R.drawable.btn_star_big_off
        )
    }

    fun loadFavorites() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val favoritesSet = sharedPrefs.getStringSet("favorites", null)
        favorites.clear()
        if (favoritesSet != null) {
            favorites.addAll(favoritesSet)
        }
    }

    fun saveFavorites() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putStringSet("favorites", favorites)
        editor.apply()
    }
}