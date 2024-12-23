package com.ip_tv.ipsat.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.app.App
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun fadeInView(rvFrame: View) {
    rvFrame.apply {
        alpha = 0f // Boshlanishda ko'rinmas qilish
        visibility = View.VISIBLE // Ko'rinadigan qilish

        // Animatsiyani boshlash
        animate()
            .alpha(1f) // Shaffoflikni 1 ga oshirish
            .setDuration(500) // Animatsiya davomiyligi 500 ms
            .setListener(null) // Listener qo'shmaslik
    }
}

fun fadeOutView(rvFrame: View) {
    rvFrame.apply {
        animate()
            .alpha(0f) // Shaffoflikni 0 ga kamaytirish
            .setDuration(500) // Animatsiya davomiyligi 500 ms
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.INVISIBLE // Animatsiyadan keyin ko'rinmas qilish
                }
            })
    }
}


fun BottomNavigationView.showWithAnimation(fragmentContainerView: View) {
    if (this.visibility == View.VISIBLE) return
    this.visible()
    this.animateTranslationY(0f, 66f, 700)
    fragmentContainerView.animateMarginBottom(66f, 700)
}
fun BottomNavigationView.hideWithoutAnimation(fragmentContainerView: View) {
    if (this.visibility == View.GONE) return
    this.gone()

    val params =
        fragmentContainerView.layoutParams as ConstraintLayout.LayoutParams
    params.setMargins(
        params.leftMargin,
        params.topMargin,
        params.rightMargin,
        0
    )
    fragmentContainerView.layoutParams = params

}

fun BottomNavigationView.hideWithAnimation(fragmentContainerView: View) {
    if (this.visibility == View.GONE) return
    this.animateTranslationY(66f, 0f, 700)
    fragmentContainerView.animateMarginBottom(0f, 700)
}
fun toast(string: String?) {
    if (string != null) {
        println(string)
        MainScope().launch {
            Toast.makeText(App.Companion.instance ?: return@launch, string, Toast.LENGTH_SHORT).show()
        }
    }
}
fun View.visible() {
    this.visibility = View.VISIBLE
}


fun View.gone() {
    this.visibility = View.GONE
}

fun MaterialCardView.select() {
    this.strokeWidth = 5
    this.strokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
}


fun MaterialCardView.unSelect() {
    this.strokeWidth = 0
//    this.strokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
}


fun View.animateTranslationY(animateFrom: Float, animateTo: Float, duration: Long) {
    val animator =
        ObjectAnimator.ofFloat(
            this, "translationY", TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                animateTo,
                resources.displayMetrics
            ), TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                animateFrom,
                resources.displayMetrics
            )
        )
    animator.duration = duration
    if (animateTo == 0f) {
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@animateTranslationY.gone()
            }
        })
    }
    animator.start()

}


fun View.animateMarginBottom(size: Float, duration: Long) {
    val dpToPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        size,
        resources.displayMetrics
    )


    val params =
        this.layoutParams as ConstraintLayout.LayoutParams
    val animator = ValueAnimator.ofInt(params.bottomMargin, dpToPx.toInt())
    animator.addUpdateListener {
        val value = it.animatedValue as Int
        params.setMargins(
            params.leftMargin,
            params.topMargin,
            params.rightMargin,
            value
        )
        this.layoutParams = params
    }
    animator.duration = duration
    animator.start()
}
