package com.ip_tv.ipsat.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavOptions
import com.google.android.material.snackbar.Snackbar
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.app.App

lateinit var viewpagerChangeListener: ((Int) -> Unit)
fun setPositionListener(listener: (Int) -> Unit) {
    viewpagerChangeListener = listener
}

fun View.expandView() {
    visibility = View.VISIBLE
    animate()
        .alpha(1.0f)
        .translationY(0f)
        .setDuration(300)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

fun snackString(s: String?, activity: Activity? = null, clipboard: String? = null) {
    if (s != null) {
        (activity)?.apply {
            runOnUiThread {
                val snackBar = Snackbar.make(
                    window.decorView.findViewById(android.R.id.content),
                    s,
                    Snackbar.LENGTH_LONG
                )
                snackBar.view.apply {
                    updateLayoutParams<FrameLayout.LayoutParams> {
                        gravity = (Gravity.CENTER_HORIZONTAL or Gravity.TOP)
                        width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    translationY = (24f + 32f)
                    translationZ = 32f
                    val shapeDrawable = ShapeDrawable()
                    shapeDrawable.paint.setColor(activity.getColor(R.color.card_background))// Set the background color if needed
                    shapeDrawable.paint.style = Paint.Style.FILL
                    shapeDrawable.shape = RoundRectShape(
                        floatArrayOf(120f, 120f, 120f, 120f, 120f, 120f, 120f, 120f),
                        null,
                        null
                    )

                    this.background = shapeDrawable
                    setOnClickListener {
                        snackBar.dismiss()
                    }
                    setOnLongClickListener {
                        true
                    }
                }
                snackBar.show()
            }
        }
    }
}

fun EditText.phoneMask() {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val formattedText = formatText(s.toString())
            removeTextChangedListener(this) // TextWatcher ni olib tashlash
            setText(formattedText) // Yangi formatdagi matnni EditText ga o'rnatish
            setSelection(formattedText.length) // Kursorning pozitsiyasini tiklash
            addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable?) {
            // Matnda o'zgarish bo'lsa, editText ga yangi format beriladi
            // TextWatcher ni qaytarish
        }
    }

    // TextWatcher ni EditText ga bog'lash
    addTextChangedListener(textWatcher)

}

private fun formatText(text: String): String {
    val regex = "(\\d{2})(\\d{3})(\\d{2})(\\d{2})".toRegex()
    val replacement = "($1)-$2-$3-$4"
    return text.replace(regex, replacement)
}

fun EditText.requestFocus(length: Int, requestFocus: View) =
    this.addTextChangedListener { if (it.toString().length >= length) requestFocus.requestFocus() }

fun EditText.requestFocusOpeningScreen() {
    requestFocus()
    val imm: InputMethodManager? =
        App.currentContext()!!
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)

}

fun View.slideTop(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_top).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}

fun View.slideStart(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_start).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}


fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.onlyOneClick() {
    isEnabled = false
    this.postDelayed({ this@onlyOneClick.isEnabled = true }, 400)
}

fun View.slideUp(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_up).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}

fun animationTransactionClearStack(clearFragmentID: Int): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()
    navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right)
        .setPopUpTo(clearFragmentID, true)
    return navBuilder
}

fun animationTransaction(): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()
    navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right)
    return navBuilder
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(
    private var defaultInterval: Int = 400,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}
//
//fun showNetworkDialog(context: FragmentActivity, container: View?) {
//    val dialog = Dialog(context)
//    container?.gone()
//    val inflater = LayoutInflater.from(context)
//    var dialogView = inflater.inflate(R.layout.no_connection, null)
//    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.setCancelable(false)
//    dialogView.try_again.setOnClickListener {
//        dialogView.try_againtxt.gone()
//        dialogView.try_again_progress.visible()
//        context.lifecycleScope.launch {
//            delay(600)
//            if (hasConnection()) {
//                container?.visible()
//                dialog.dismiss()
//                dialogView.try_againtxt.visible()
//                dialogView.try_again_progress.gone()
//
//            } else {
//                container?.gone()
//                dialogView.try_againtxt.visible()
//                dialogView.try_again_progress.gone()
//                dialog.setContentView(dialogView)
//                dialog.show()
//            }
//        }
//
//    }
//    dialog.setContentView(dialogView)
//
//    dialog.show()
//
//
//}


fun Fragment.showSnack(
    view: View = requireView(),
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Snackbar.make(view, message, duration).show()
}


fun convertDpToPixel(dp: Float, context: Context): Float {
    val resources: Resources = context.getResources()
    val metrics: DisplayMetrics = resources.getDisplayMetrics()
    return dp * (metrics.densityDpi / 160f)
}

fun AppCompatEditText.clear() {
    setText("")
}


fun View.vibrationAnimation() {
    val vibrationAnim =
        AnimationUtils.loadAnimation(App.instance, R.anim.vibiration_anim)
    startAnimation(vibrationAnim)

}