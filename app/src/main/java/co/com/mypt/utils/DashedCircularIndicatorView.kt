package co.com.mypt.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DashedCircularIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#31343A")
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(30f, 10f), 0f) // Dashed effect
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F38D1B") // Progress color
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(30f, 10f), 0f) // Dashed effect
    }

    private var progress = 0f // Progress in percentage (0 to 100)

    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 100f)
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = (width.coerceAtMost(height) / 2f) - backgroundPaint.strokeWidth

        val rectF = RectF(
            width / 2f - radius,
            height / 2f - radius,
            width / 2f + radius,
            height / 2f + radius
        )

        // Draw full dashed background circle
        canvas.drawArc(rectF, 0f, 360f, false, backgroundPaint)

        // Draw progress as an arc
        val sweepAngle = (progress / 100) * 360 // Convert progress to angle
        canvas.drawArc(rectF, -0f, sweepAngle, false, progressPaint)
    }
}
