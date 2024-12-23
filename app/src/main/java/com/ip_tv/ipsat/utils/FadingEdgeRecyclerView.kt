package com.ip_tv.ipsat.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.app.App

class FadingEdgeRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun isPaddingOffsetRequired(): Boolean {
        return !this.clipToPadding
    }

    override fun getLeftPaddingOffset(): Int {
        return if (clipToPadding) {
            0
        } else -paddingLeft
    }

    override fun getTopPaddingOffset(): Int {
        return if (clipToPadding) {
            0
        } else -paddingTop
    }

    override fun getRightPaddingOffset(): Int {
        return if (this.clipToPadding) 0 else paddingRight
    }

    override fun getBottomPaddingOffset(): Int {
        return if (clipToPadding) 0 else paddingBottom
    }


}
fun View.alphaAnim() {
    val anim = AnimationUtils.loadAnimation(
        currContext()!!,
        R.anim.alpha_anim
    ).apply {
        duration = 1800L

        fillAfter = true
    }

    startAnimation(anim)

}

fun currContext(): Context? {
    return App.currentContext()
}
fun View.fadeOut() {
    val anim = AnimationUtils.loadAnimation(
        currContext()!!,
        R.anim.fade_out
    ).apply {
        duration = 800L

        fillAfter = true
    }

    startAnimation(anim)

}
