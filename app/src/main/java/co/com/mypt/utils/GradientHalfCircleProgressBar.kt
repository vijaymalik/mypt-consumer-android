package co.com.mypt.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class GradientHalfCircleProgressBar(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var maxValue = 100f
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress = 0f
    private var startColor = Color.parseColor("#FF8940") //121212
    private var endColor = Color.parseColor("#FF8940")
    private var borderColor = Color.parseColor("#1FA95E09")
    private var borderWidth = 4f // Thickness of the outer border
    private var backgroundColor = Color.parseColor("#2A170B") // Color for the background track
    private var backgroundWidth = 25f // Thickness of the background track
    private var progressBarWidth = 23f // Thickness of the progress bar

    init {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
        borderPaint.color = borderColor
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = backgroundWidth
        backgroundPaint.color = backgroundColor
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.strokeWidth = progressBarWidth
    }

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }

    fun setMaxValue(max: Float) {
        maxValue = max
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height.toFloat()  // bottom-center

        val maxRadius = Math.min(width, height) / 2f

        val outerBorderRadius = maxRadius - borderWidth / 2f
        val backgroundRadius = outerBorderRadius - borderWidth / 2f - backgroundWidth / 2f
        val innerBorderRadius = backgroundRadius - backgroundWidth / 2f - borderWidth / 2f

        // Outer border arc
        val outerRect = RectF(
            centerX - outerBorderRadius,
            centerY - outerBorderRadius,
            centerX + outerBorderRadius,
            centerY + outerBorderRadius
        )
        canvas.drawArc(outerRect, 180f, 180f, false, borderPaint)

        // Inner border arc
        val innerRect = RectF(
            centerX - innerBorderRadius,
            centerY - innerBorderRadius,
            centerX + innerBorderRadius,
            centerY + innerBorderRadius
        )
        canvas.drawArc(innerRect, 180f, 180f, false, borderPaint)

        // Background arc
        val backgroundRect = RectF(
            centerX - backgroundRadius,
            centerY - backgroundRadius,
            centerX + backgroundRadius,
            centerY + backgroundRadius
        )
        canvas.drawArc(backgroundRect, 180f, 180f, false, backgroundPaint)

        // Gradient progress arc
        val shader = LinearGradient(
            centerX - backgroundRadius,
            centerY,
            centerX + backgroundRadius,
            centerY,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
        progressPaint.shader = shader
        val sweepAngle = (progress / maxValue) * 180f

        canvas.drawArc(backgroundRect, 180f, sweepAngle, false, progressPaint)
    }

    fun setGradientColors(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
        invalidate()
    }

    fun setProgressWithAnimation(targetProgress: Float, duration: Long = 1000) {
        var tempProgress = targetProgress
        if(targetProgress < 10)
            tempProgress = targetProgress + 15
        else if (targetProgress > 50)
            tempProgress = targetProgress - 15

        val animator = ValueAnimator.ofFloat(progress, tempProgress)
        animator.duration = 1500

        animator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

}
