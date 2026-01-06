package co.com.mypt.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class CurvedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 85f
        color = Color.parseColor("#2A120B") // Dark background track color
        strokeCap = Paint.Cap.ROUND
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 95f
        color = Color.parseColor("#26FF8940") // Dark background
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 83f
        strokeCap = Paint.Cap.ROUND
    }

    private var progress = 0f // Between 0 to 100
    private var gradientShader: Shader? = null

    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 100f) // Ensure progress is within 0-100
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Create a gradient from left to right of the arc
        gradientShader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            intArrayOf(Color.parseColor("#040C0B"), Color.parseColor("#FF8940")),
            null,
            Shader.TileMode.CLAMP
        )
        progressPaint.shader = gradientShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val left = 0f  // Align with the left edge
        val right = width.toFloat()  // Align with the right edge
        val top = height * 0.25f  // Adjust curvature
        val bottom = height * 1.5f  // Extend lower stroke so it doesn't look lifted

        val arcRect = RectF(left, top, right, bottom)

        // Draw the outermost stroke
        canvas.drawArc(arcRect, 180f, 180f, false, strokePaint)

        // Draw background arc
        canvas.drawArc(arcRect, 180f, 180f, false, backgroundPaint)

        // Draw progress arc
        canvas.drawArc(arcRect, 180f, (progress/100) * 180f, false, progressPaint)
    }
}