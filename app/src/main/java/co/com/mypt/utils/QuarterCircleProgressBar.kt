package co.com.mypt.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import co.com.mypt.R

class QuarterCircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var maxValue = 100f
    private var progress = 0f
    private var shader: Shader? = null

    private val strokeWidth = 25f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.progress_default_1)
        style = Paint.Style.STROKE
        strokeWidth = this@QuarterCircleProgressBar.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@QuarterCircleProgressBar.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    fun setMaxValue(max: Float) {
        maxValue = max
        invalidate()
    }

    fun setProgressWithAnimation(targetProgress: Float) {
        val animator = ValueAnimator.ofFloat(progress, targetProgress)
        animator.duration = 1500
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            setProgressDrawable(R.drawable.progress_gradient_2)
        }
    }

    fun setProgressDrawable(drawableId: Int) {
        if (width == 0 || height == 0) return

        val drawable = ContextCompat.getDrawable(context, drawableId)
        drawable?.let {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            progressPaint.shader = shader
        }
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = strokeWidth / 2
        val size = minOf(width, height).toFloat() - padding * 2

        // This rect will draw the quarter arc from bottom to right
        val rectF = RectF(
            width - size - padding,
            height - size - padding,
            width - padding,
            height - padding
        )

        val sweepAngle = (progress / maxValue) * 90f

        // Draw background quarter arc
        canvas.drawArc(rectF, 180f, 90f, false, backgroundPaint)

        // Draw progress quarter arc
        shader?.let {
            progressPaint.shader = it
            canvas.drawArc(rectF, 180f, sweepAngle, false, progressPaint)
        }
    }
}


