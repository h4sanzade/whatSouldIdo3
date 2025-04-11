package com.materialdesign.whatsouldido

import android.content.Intent
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

/**
 * MainActivity'nin dialog ile ilgili fonksiyonlarını içeren extension class
 */
fun MainActivity.showAddSuggestionDialog() {
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

fun MainActivity.showManageSuggestionsDialog() {
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

fun MainActivity.showSettingsDialog() {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)

    val themeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.themeRadioGroup)
    val statsButton = dialogView.findViewById<Button>(R.id.statisticsButton)
    val favoritesButton = dialogView.findViewById<Button>(R.id.favoritesButton)

    // Mevcut tema seçeneğini işaretle
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
            // Tema seçimini kaydet
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

fun MainActivity.showStatisticsActivity() {
    val intent = Intent(this, StatisticsActivity::class.java)
    startActivity(intent)
}

fun MainActivity.showFavoritesDialog() {
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