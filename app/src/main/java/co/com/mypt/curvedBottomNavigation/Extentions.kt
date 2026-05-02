package co.com.mypt.curvedBottomNavigation

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

val Int.toPx: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.dp(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).toInt()

fun Float.dp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
)

@RequiresApi(Build.VERSION_CODES.O)
fun Context.findFont(font: Int): Typeface {
    return this.resources.getFont(font)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

internal fun tintColor(
    @ColorInt color: Int
) = ColorStateList.valueOf(color)

fun <T> View?.updateLayoutParams(onLayoutChange: (params: T) -> Unit) {
    if (this == null)
        return
    try {
        @Suppress("UNCHECKED_CAST")
        onLayoutChange(layoutParams as T)
        layoutParams = layoutParams
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> (() -> T).withDelay(delay: Long = 250L) {
    Handler(Looper.getMainLooper()).postDelayed({ this.invoke() }, delay)
}

inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let {
        if (it >= 0) enumValues<T>()[it] else default
    }

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

fun View.setMargins(
    leftMarginDp: Int? = null,
    topMarginDp: Int? = null,
    rightMarginDp: Int? = null,
    bottomMarginDp: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        leftMarginDp?.run { params.leftMargin = this.dpToPx(context) }
        topMarginDp?.run { params.topMargin = this.dpToPx(context) }
        rightMarginDp?.run { params.rightMargin = this.dpToPx(context) }
        bottomMarginDp?.run { params.bottomMargin = this.dpToPx(context) }
        requestLayout()
    }
}
fun Any.log(tag: String = "") {
    if (tag.equals("")) {
        Log.d("TAG_QMR", this.toString())
    } else {
        Log.d("TAG_QMR $tag", this.toString())

    }
}
fun TextView.setColoredTextRes(
    fullText: String,
    highlightText: String,
    @ColorRes normalColorRes: Int,
    @ColorRes highlightColorRes: Int,
    onClick: (() -> Unit)? = null
) {
    setColoredText(
        fullText = fullText,
        highlightText = highlightText,
        normalColor = ContextCompat.getColor(context, normalColorRes),
        highlightColor = ContextCompat.getColor(context, highlightColorRes),
        onClick = onClick
    )
}

fun TextView.setColoredText(
    fullText: String,
    highlightText: String,
    normalColor: Int,
    highlightColor: Int,
    onClick: (() -> Unit)? = null
) {
    val spannable = SpannableString(fullText)

    // Apply normal color
    spannable.setSpan(
        ForegroundColorSpan(normalColor),
        0,
        fullText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val startIndex = fullText.indexOf(highlightText)
    if (startIndex == -1) {
        text = fullText
        return
    }

    val endIndex = startIndex + highlightText.length

    // Apply highlight color
    spannable.setSpan(
        ForegroundColorSpan(highlightColor),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Optional clickable span
    if (onClick != null) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // remove underline
            }
        }

        spannable.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        movementMethod = LinkMovementMethod.getInstance()
        this.highlightColor = Color.TRANSPARENT
    }

    text = spannable
}

fun TextView.setUnderlineClickableText(
    fullText: String,
    targetText: String,
    onClick: (() -> Unit)? = null
) {
    val spannable = SpannableString(fullText)

    val start = fullText.indexOf(targetText)
    if (start == -1) {
        text = fullText
        return
    }
    val end = start + targetText.length

    // Underline
    spannable.setSpan(
        UnderlineSpan(),
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Click (optional)
    onClick?.let {
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                it()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = this@setUnderlineClickableText.currentTextColor
                ds.typeface = this@setUnderlineClickableText.typeface
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }

    text = spannable
}