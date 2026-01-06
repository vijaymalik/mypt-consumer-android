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

class SingleRangeBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var value = 10f
    private var rangeMin = 0f
    private var rangeMax = 2000f

    private var thumbWidth = 100f
    private var thumbHeight = 45f
    private var barHeight = 10f
    private var textSize = 25f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val thumbBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.single_slider_thumb)
        .let { Bitmap.createScaledBitmap(it, thumbWidth.toInt(), thumbHeight.toInt(), true) }

    // Listener interface
    interface OnValueChangeListener {
        fun onValueChanged(value: Float)
    }

    private var valueChangeListener: OnValueChangeListener? = null

    fun setOnValueChangeListener(listener: OnValueChangeListener) {
        this.valueChangeListener = listener
    }

    fun getCurrentValue(): Float = value

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barY = height / 2f
        val thumbX = valueToX(value)

        // Draw background track
        paint.color = Color.GRAY
        canvas.drawRect(thumbWidth / 2, barY - barHeight / 2, width - thumbWidth / 2, barY + barHeight / 2, paint)

        // Draw active selected track
        paint.color = context.getColor(R.color.orangecolor)
        canvas.drawRect(thumbWidth / 2, barY - barHeight / 2, thumbX, barY + barHeight / 2, paint)

        // Draw thumb
        canvas.drawBitmap(thumbBitmap, thumbX - thumbWidth / 2, barY - thumbHeight / 2, paint)

        // Draw value text inside thumb
        paint.color = Color.WHITE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER

        val textX = thumbX
        val textY = barY - thumbHeight / 2 + (thumbHeight / 2) + (textSize / 3)

        val textWithTilde = "~ ${value.toInt()}"
        canvas.drawText(textWithTilde, textX, textY, paint)
    }

    private fun valueToX(value: Float): Float {
        return (width - thumbWidth) * (value - rangeMin) / (rangeMax - rangeMin) + thumbWidth / 2
    }

    private fun xToValue(x: Float): Float {
        return ((x - thumbWidth / 2) / (width - thumbWidth) * (rangeMax - rangeMin) + rangeMin)
            .coerceIn(rangeMin, rangeMax)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val newValue = xToValue(event.x)
                if (newValue != value) {
                    value = newValue
                    invalidate()
                    valueChangeListener?.onValueChanged(value)
                }
            }
        }
        return true
    }
}
