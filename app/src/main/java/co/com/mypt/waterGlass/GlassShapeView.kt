package co.com.mypt.waterGlass

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class GlassShapeView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var fillPercentage = 0f // Water fill percentage (0-100%)

    private val paintGlass = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintWater = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintGradientOverlay = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintImage = Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderWidth = 2f * resources.displayMetrics.density // **10dp border width**
    private val cornerRadius = 45f // Rounded corners for glass
    private val waterCornerRadius = 30f // Smooth rounded corners for water

    private var imageBitmap: Bitmap? = null

    init {
        paintWater.style = Paint.Style.FILL
        paintWater.alpha = 180 // Semi-transparent water

        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = borderWidth
    }

    fun setImage(resourceId: Int) {
        val drawable = ContextCompat.getDrawable(context, resourceId)
        imageBitmap = drawable?.let {
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            bitmap
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // **Glass Background Gradient**
        val glassShader = LinearGradient(
            0f, height / 2, width, height / 2,
            intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#1A584471")),
            null, Shader.TileMode.CLAMP
        )
        paintGlass.shader = glassShader

        // **Define the Inverted Trapezoidal Glass Shape**
        val glassPath = Path().apply {
            moveTo(width * 0.05f + cornerRadius, 0f) // Top-left
            lineTo(width * 0.95f - cornerRadius, 0f) // Top-right
            quadTo(width * 0.95f, 0f, width * 0.95f, cornerRadius) // Smooth Top-right curve
            lineTo(width * 0.8f, height - cornerRadius) // Right-side (Narrow at Bottom)
            quadTo(width * 0.8f, height, width * 0.8f - cornerRadius, height) // Bottom-right curve
            lineTo(width * 0.2f + cornerRadius, height) // Bottom
            quadTo(width * 0.2f, height, width * 0.2f, height - cornerRadius) // Bottom-left curve
            lineTo(width * 0.05f, cornerRadius) // Left-side (Narrow at Bottom)
            quadTo(width * 0.05f, 0f, width * 0.05f + cornerRadius, 0f) // Smooth Top-left curve
            close()
        }

        // **Draw Glass Shape**
        canvas.drawPath(glassPath, paintGlass)

        // **Glass Border Gradient (Top to Bottom)**
        val borderShader = LinearGradient(
            0f, 0f, 0f, height, // **Gradient from top to bottom**
            intArrayOf(Color.parseColor("#5B5D60"), Color.parseColor("#0037393C")),
            null, Shader.TileMode.CLAMP
        )
        paintBorder.shader = borderShader

        // Draw Glass Border
        canvas.drawPath(glassPath, paintBorder)

        // **Fixing Water Shape for Inverted Glass**
        val waterHeight = height * (1 - fillPercentage / 100f)

        // **Water Fill Gradient (Left to Right)**
        val waterShader = LinearGradient(
            0f, waterHeight, width, waterHeight,
            intArrayOf(Color.parseColor("#00B8FB"), Color.parseColor("#004FFF")),
            null, Shader.TileMode.CLAMP
        )
        paintWater.shader = waterShader

        val waterPath = Path().apply {
            moveTo(width * 0.05f + waterCornerRadius, waterHeight) // Top-left of water
            lineTo(width * 0.95f - waterCornerRadius, waterHeight) // Top-right of water

            // **Fix:** Ensuring smooth rounded top corners even at low water levels
            if (fillPercentage > 5) {
                quadTo(width * 0.95f, waterHeight, width * 0.95f, waterHeight + waterCornerRadius)
                lineTo(width * 0.8f, height - cornerRadius) // Right-side
                quadTo(width * 0.8f, height, width * 0.8f - cornerRadius, height) // Bottom-right curve
            } else {
                lineTo(width * 0.95f, waterHeight) // Straight top if water is too low
                lineTo(width * 0.8f, height) // Right-side
            }

            lineTo(width * 0.2f + cornerRadius, height) // Bottom
            quadTo(width * 0.2f, height, width * 0.2f, height - cornerRadius) // Bottom-left curve
            lineTo(width * 0.05f, waterHeight + waterCornerRadius) // Left-side
            quadTo(width * 0.05f, waterHeight, width * 0.05f + waterCornerRadius, waterHeight) // Smooth Top-left curve
            close()
        }

        // Clip Water to the Glass Shape
        canvas.save()
        canvas.clipPath(glassPath)
        canvas.drawPath(waterPath, paintWater)
        canvas.restore()
    }

    // Animate water filling
    fun setFillPercentage(percentage: Float) {
        val animator = ValueAnimator.ofFloat(fillPercentage, percentage).apply {
            duration = 1000
            addUpdateListener {
                fillPercentage = it.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }
}