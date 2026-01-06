package co.com.mypt.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class GradientDonutChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var arcData: List<Triple<Int, Int, Float>> = emptyList()

    private val strokeWidth = 50f
    private val arcGapDegrees = 13.5f
    private val insetPadding = dpToPx(10f) // Padding from view edge

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Needed for PorterDuff CLEAR
    }

    fun setArcData(data: List<Triple<Int, Int, Float>>) {
        arcData = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (arcData.isEmpty()) return

        // Effective drawing area
        val usableWidth = width - insetPadding * 2
        val usableHeight = height - insetPadding * 2

        val centerX = width / 2f
        val centerY = height / 2f
        val outerRadius = min(usableWidth, usableHeight) / 2f

        // 🟤 Step 1: Black circular background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#101113")
        }
        canvas.drawCircle(centerX, centerY, outerRadius+20, bgPaint)

        // 🎨 Step 2: Draw donut arcs
        val arcRadius = outerRadius+10 - strokeWidth / 2f
        val oval = RectF(
            centerX - arcRadius,
            centerY - arcRadius,
            centerX + arcRadius,
            centerY + arcRadius
        )

        var startAngle = -90f

        for ((startColor, endColor, fullSweep) in arcData) {
            val sweepAngle = fullSweep - arcGapDegrees

            val startRadians = Math.toRadians(startAngle.toDouble())
            val endRadians = Math.toRadians((startAngle + sweepAngle).toDouble())

            val x0 = (centerX + cos(startRadians) * arcRadius).toFloat()
            val y0 = (centerY + sin(startRadians) * arcRadius).toFloat()
            val x1 = (centerX + cos(endRadians) * arcRadius).toFloat()
            val y1 = (centerY + sin(endRadians) * arcRadius).toFloat()

            val shader = LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = this@GradientDonutChartView.strokeWidth
                strokeCap = Paint.Cap.ROUND
                this.shader = shader
            }

            canvas.drawArc(oval, startAngle, sweepAngle, false, paint)

            startAngle += fullSweep
        }
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }
}