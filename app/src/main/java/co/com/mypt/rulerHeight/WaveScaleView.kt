package co.com.mypt.rulerHeight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveScaleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // ================= CONFIG =================
    private val TOTAL_ITEMS = 200
    private val LONG_SPOKE_EVERY = 10
    private val WAVE_ITEM_COUNT = 7
    private val WAVE_HALF = WAVE_ITEM_COUNT / 2   // 3
    private val BASE_SCALE = 0.6f
    private val MAX_WAVE_SCALE = 1f

    // ================= PAINTS =================
    private val spokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = dp(2f)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = sp(12f)
        textAlign = Paint.Align.CENTER
    }

    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        strokeWidth = dp(3f)
    }

    // ================= SIZE =================
    private var pxPerItem = 0f
    private var waveRangePx = 0f
    private var centerX = 0f
    private var centerY = 0f

    // ================= STATE =================
    private var scrollOffset = 0f

    // ================= INIT =================
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        pxPerItem = dp(12f)
        waveRangePx = pxPerItem * WAVE_HALF
        centerX = width / 2f
        centerY = height / 2f
    }

    // ================= DRAW =================
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val firstIndex = ((scrollOffset - centerX) / pxPerItem).toInt() - 2
        val lastIndex = firstIndex + (width / pxPerItem).toInt() + 4

        for (i in firstIndex..lastIndex) {
            if (i < 0 || i >= TOTAL_ITEMS) continue

            val x = centerX + (i * pxPerItem) - scrollOffset
            val distance = kotlin.math.abs(x - centerX)

            val scale = if (distance <= waveRangePx) {
                val ratio = distance / waveRangePx
                MAX_WAVE_SCALE - (ratio * ratio * 0.4f)
            } else {
                BASE_SCALE
            }

            val isLong = i % LONG_SPOKE_EVERY == 0
            val spokeHeight = if (isLong) dp(40f) else dp(24f)

            canvas.save()
            canvas.scale(1f, scale, x, centerY)

            canvas.drawLine(
                x,
                centerY - spokeHeight,
                x,
                centerY,
                spokePaint
            )

            canvas.restore()

            // Text only for every 10th item
            if (isLong) {
                canvas.drawText(
                    i.toString(),
                    x,
                    centerY + dp(18f),
                    textPaint
                )
            }
        }

        // Center Indicator
        canvas.drawLine(
            centerX,
            centerY - dp(50f),
            centerX,
            centerY + dp(20f),
            centerPaint
        )
    }

    // ================= SCROLL =================
    fun updateScroll(dx: Float) {
        scrollOffset = (scrollOffset + dx).coerceIn(0f, TOTAL_ITEMS * pxPerItem)
        invalidate()
    }

    // ================= UTILS =================
    private fun dp(value: Float): Float =
        value * resources.displayMetrics.density

    private fun sp(value: Float): Float =
        value * resources.displayMetrics.scaledDensity
}
