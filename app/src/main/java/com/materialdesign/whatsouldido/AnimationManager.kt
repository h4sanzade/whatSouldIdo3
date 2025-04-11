package com.materialdesign.whatsouldido

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView

class AnimationManager {
    fun animateSuggestion(textView: TextView, suggestion: String) {
        val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f)
        fadeOut.duration = 200

        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        fadeIn.duration = 200

        val scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 0.8f, 1f)

        val animatorSet = AnimatorSet()

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textView.text = suggestion

                val scaleAndFadeSet = AnimatorSet()
                scaleAndFadeSet.playTogether(fadeIn, scaleX, scaleY)
                scaleAndFadeSet.start()
            }
        })

        animatorSet.play(fadeOut)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    fun animateEmoji(textView: TextView) {
        val bounce = ObjectAnimator.ofFloat(textView, "translationY", 0f, -20f, 0f)
        bounce.duration = 500
        bounce.interpolator = AccelerateDecelerateInterpolator()

        val rotate = ObjectAnimator.ofFloat(textView, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotate.duration = 500

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(bounce, rotate)
        animatorSet.start()
    }
}