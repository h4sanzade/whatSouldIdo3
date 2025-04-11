package com.materialdesign.whatsouldido

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class Category(val name: String, val emoji: String, val color: Int)

class SuggestionManager {
    val suggestionsList = mutableListOf<String>()
    private val categories = mutableListOf<Category>()

    init {
        // Varsayılan kategorileri ekle
        categories.add(Category("Eğlence", "🎮", android.graphics.Color.parseColor("#FF9800")))
        categories.add(Category("Yemek", "🍔", android.graphics.Color.parseColor("#E91E63")))
        categories.add(Category("Spor", "🏃", android.graphics.Color.parseColor("#4CAF50")))
        categories.add(Category("Eğitim", "📚", android.graphics.Color.parseColor("#2196F3")))
        categories.add(Category("Seyahat", "✈️", android.graphics.Color.parseColor("#9C27B0")))
    }

    fun loadSuggestions(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val suggestionsJson = sharedPrefs.getString("suggestions", null)

        suggestionsList.clear()

        if (suggestionsJson != null) {
            try {
                val jsonObject = JSONObject(suggestionsJson)
                val jsonArray = jsonObject.getJSONArray("suggestions")

                for (i in 0 until jsonArray.length()) {
                    suggestionsList.add(jsonArray.getString(i))
                }
            } catch (e: JSONException) {
                e.printStackTrace()

                // Hata durumunda varsayılan önerileri ekle
                addDefaultSuggestions()
            }
        } else {
            // İlk kullanımda varsayılan önerileri ekle
            addDefaultSuggestions()
            saveSuggestions(context)
        }
    }

    fun saveSuggestions(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        for (suggestion in suggestionsList) {
            jsonArray.put(suggestion)
        }

        jsonObject.put("suggestions", jsonArray)
        editor.putString("suggestions", jsonObject.toString())
        editor.putLong("lastUsage", System.currentTimeMillis())
        editor.apply()
    }

    private fun addDefaultSuggestions() {
        // Varsayılan önerileri ekle
        suggestionsList.add("Film izle")
        suggestionsList.add("Kitap oku")
        suggestionsList.add("Spor yap")
        suggestionsList.add("Yeni bir yemek tarifi dene")
        suggestionsList.add("Bir arkadaşını ara")
        suggestionsList.add("Meditasyon yap")
        suggestionsList.add("Yürüyüşe çık")
        suggestionsList.add("Puzzle çöz")
        suggestionsList.add("Müzik dinle")
        suggestionsList.add("Blog yaz")
    }

    fun getCategories(): List<Category> {
        return categories
    }

    fun getCategoryForSuggestion(suggestion: String): Category? {
        // Şu anlık rastgele bir kategori döndür
        // Gerçek uygulamada önerilerin kategorileri saklanmalı
        if (suggestion.isEmpty()) return null

        val index = suggestion.length % categories.size
        return categories[index]
    }
}