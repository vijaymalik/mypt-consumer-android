package co.com.mypt.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.toColorInt

class CarouselIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var pageCount = 0
    private var selectedPage = 0

    private val circleSize = 8        // dp → px later if needed
    private val lineWidth = 40
    private val indicatorHeight = 8
    private val margin = 8

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    fun setPageCount(count: Int) {
        pageCount = count
        buildIndicators()
    }

    fun setSelectedPage(position: Int) {
        selectedPage = position
        updateIndicators()
    }

    private fun buildIndicators() {
        removeAllViews()
        for (i in 0 until pageCount) {
            val view = View(context)
            val params = LayoutParams(circleSize, indicatorHeight)
            params.marginEnd = margin
            view.layoutParams = params
            addView(view)
        }
        updateIndicators()
    }

    private fun updateIndicators() {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (i == selectedPage) {
                setLine(view)
            } else {
                setCircle(view)
            }
            view.requestLayout()
        }
    }

    // 🔹 NEW: Smooth progress method
    fun setProgress(position: Int, offset: Float) {
        for (i in 0 until childCount) {
            val view = getChildAt(i)

            if (i == position) {
                // Expanding current indicator from circle → line
                val progressWidth =
                    (circleSize + (lineWidth - circleSize) * (1 - offset)).toInt()
                view.layoutParams.width = progressWidth
                view.layoutParams.height = indicatorHeight
                view.background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 4f
                    setColor("#FAFAFA".toColorInt())
                }
            } else if (i == position + 1 && offset > 0f) {
                // Growing next indicator (circle → line)
                val progressWidth =
                    (circleSize + (lineWidth - circleSize) * offset).toInt()
                view.layoutParams.width = progressWidth
                view.layoutParams.height = indicatorHeight
                view.background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 4f
                    setColor("#FAFAFA".toColorInt())
                }
            } else {
                // Inactive → circle
                setCircle(view)
            }

            view.requestLayout()
        }
    }

    private fun setCircle(view: View) {
        view.layoutParams.width = circleSize
        view.layoutParams.height = circleSize
        view.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor("#31343A".toColorInt())
        }
    }

    private fun setLine(view: View) {
        view.layoutParams.width = lineWidth
        view.layoutParams.height = indicatorHeight
        view.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 5f
            setColor("#FAFAFA".toColorInt())
        }
    }
}
