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

class SemiCircleProgressViewHome @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val strokeWidth = 60f  // Large thickness for progress bar
    private val cornerRadius = 5f // Rounded effect at the ends
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.progress_default)
        style = Paint.Style.STROKE
        strokeWidth = this@SemiCircleProgressViewHome.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND
    }

    private var progress = 0f
    private var shader: Shader? = null

    // Set progress with animation
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
            setProgressDrawable(R.drawable.progress_gradient)
        }
    }


    // Set a drawable resource as progress color
    fun setProgressDrawable(drawableId: Int) {
        if (width == 0 || height == 0) return  // Avoid crash

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

        // Shrink drawing area to account for stroke width and corner radius
        val padding = (strokeWidth / 2)
        val rectF = RectF(padding, padding, width - padding, height.toFloat())

        // Draw background arc
        canvas.drawArc(rectF, 180f, 180f, false, backgroundPaint)

        // Draw progress arc
        shader?.let {
            progressPaint.shader = it
            canvas.drawArc(rectF, 180f, (progress / 100) * 180, false, progressPaint)
        }
    }
}