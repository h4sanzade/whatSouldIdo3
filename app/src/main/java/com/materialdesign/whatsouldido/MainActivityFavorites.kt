package com.materialdesign.whatsouldido

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context

/**
 * MainActivity'nin favoriler ile ilgili fonksiyonlarını içeren extension class
 */
fun MainActivity.toggleFavorite(suggestion: String) {
    if (suggestion.isEmpty() || suggestion == "Öneri için butona tıkla!") return

    if (favorites.contains(suggestion)) {
        favorites.remove(suggestion)
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
    } else {
        favorites.add(suggestion)
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)

        // Favoriye eklendiğinde animasyon
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

fun MainActivity.updateFavoriteButton(suggestion: String) {
    if (favorites.contains(suggestion)) {
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
    } else {
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
    }
}

fun MainActivity.loadFavorites() {
    val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
    val favoritesSet = sharedPrefs.getStringSet("favorites", null)
    favorites.clear()
    if (favoritesSet != null) {
        favorites.addAll(favoritesSet)
    }
}

fun MainActivity.saveFavorites() {
    val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    editor.putStringSet("favorites", favorites)
    editor.apply()
}