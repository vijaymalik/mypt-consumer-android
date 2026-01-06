package co.com.mypt.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import co.com.mypt.R
import kotlin.math.abs

class DoubleRangeSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var minValue = 50f
    private var maxValue = 90f
    private var rangeMin = 0f
    private var rangeMax = 100f

    private var thumbSize = 40f
    private var barHeight = 10f
    private var textSize = 25f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var activeThumb: Thumb? = null

    private val thumbBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.thumb_icon)
        .let { Bitmap.createScaledBitmap(it, thumbSize.toInt(), thumbSize.toInt(), true) }

    enum class Thumb { MIN, MAX }

    interface OnRangeChangeListener {
        fun onRangeChanged(min: Float, max: Float)
    }

    private var rangeChangeListener: OnRangeChangeListener? = null

    fun setOnRangeChangeListener(listener: OnRangeChangeListener) {
        this.rangeChangeListener = listener
    }

    fun getSelectedMinValue(): Float = minValue
    fun getSelectedMaxValue(): Float = maxValue

    fun setSelectedValues(min: Float, max: Float) {
        this.minValue = min.coerceIn(rangeMin, rangeMax)
        this.maxValue = max.coerceIn(rangeMin, rangeMax)
        invalidate()
        rangeChangeListener?.onRangeChanged(minValue, maxValue)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barY = height / 2f
        val minX = valueToX(minValue)
        val maxX = valueToX(maxValue)

        // Draw full track
        paint.color = Color.GRAY
        canvas.drawRect((thumbSize / 2) + 28, barY - barHeight / 2, width - (thumbSize / 2) - 35, barY + barHeight / 2, paint)

        // Draw selected range
        paint.color = context.getColor(R.color.orangecolor)
        canvas.drawRect(minX, barY - barHeight / 2, maxX, barY + barHeight / 2, paint)

        // Draw thumbs
        canvas.drawBitmap(thumbBitmap, minX - thumbSize / 2, barY - thumbSize / 2, paint)
        canvas.drawBitmap(thumbBitmap, maxX - thumbSize / 2, barY - thumbSize / 2, paint)

        // Draw labels below thumbs
        paint.color = context.getColor(R.color.txt_clr_2)
        paint.textSize = textSize
        canvas.drawText("${minValue.toInt()}", minX - 12, barY + thumbSize + 10, paint)
        canvas.drawText("${maxValue.toInt()}", maxX - 20, barY + thumbSize + 10, paint)
    }

    private fun valueToX(value: Float): Float {
        return (width - thumbSize) * (value - rangeMin) / (rangeMax - rangeMin) + thumbSize / 2
    }

    private fun xToValue(x: Float): Float {
        return ((x - thumbSize / 2) / (width - thumbSize) * (rangeMax - rangeMin) + rangeMin)
            .coerceIn(rangeMin, rangeMax)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                activeThumb = when {
                    isThumbTouched(event.x, minValue) -> Thumb.MIN
                    isThumbTouched(event.x, maxValue) -> Thumb.MAX
                    else -> null
                }
            }

            MotionEvent.ACTION_MOVE -> {
                activeThumb?.let {
                    val newValue = xToValue(event.x)

                    when (it) {
                        Thumb.MIN -> {
                            if (newValue < maxValue && newValue != minValue) {
                                minValue = newValue
                                invalidate()
                                rangeChangeListener?.onRangeChanged(minValue, maxValue)
                            }
                        }

                        Thumb.MAX -> {
                            if (newValue > minValue && newValue != maxValue) {
                                maxValue = newValue
                                invalidate()
                                rangeChangeListener?.onRangeChanged(minValue, maxValue)
                            }
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> activeThumb = null
        }
        return true
    }

    private fun isThumbTouched(x: Float, value: Float): Boolean {
        return abs(x - valueToX(value)) <= thumbSize / 2
    }
}
