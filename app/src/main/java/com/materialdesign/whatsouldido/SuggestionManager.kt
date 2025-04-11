package com.materialdesign.whatsouldido

import android.content.Context
import android.graphics.Color
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class Category(val name: String, val emoji: String, val color: Int)

class SuggestionManager {
    val suggestionsList = mutableListOf<String>()
    private val categories = mutableListOf<Category>()

    init {
        categories.add(Category("Eğlence", "🎮", Color.parseColor("#FF9800")))
        categories.add(Category("Yemek", "🍔", Color.parseColor("#E91E63")))
        categories.add(Category("Spor", "🏃", Color.parseColor("#4CAF50")))
        categories.add(Category("Eğitim", "📚", Color.parseColor("#2196F3")))
        categories.add(Category("Seyahat", "✈️", Color.parseColor("#9C27B0")))
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
                addDefaultSuggestions()
            }
        } else {
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
        if (suggestion.isEmpty()) return null
        val index = suggestion.length % categories.size
        return categories[index]
    }
}