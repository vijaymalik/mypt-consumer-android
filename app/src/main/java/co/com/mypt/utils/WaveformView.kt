package co.com.mypt.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import co.com.mypt.R

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    val customTypeface = ResourcesCompat.getFont(context, R.font.manrope_semi_bold)
    val textSizeSp = 16f
    val cornerRadiusPx = dpToPx(24f, context)
    val textSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        textSizeSp,
        resources.displayMetrics
    )
    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        pathEffect = CornerPathEffect(10f)
    }

    val dashLine = Paint().apply {
        color = Color.GRAY  // or any color you want
        strokeWidth = 4f
        isAntiAlias = true
        style = Paint.Style.STROKE
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(15f, 10f), 0f)
    }

    private val path = Path()
    private val dataPoints = mutableListOf<Float>()

    fun updateData(values: List<Float>) {
        dataPoints.clear()
        dataPoints.addAll(values)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        path.reset()
        val topMargin = 60f
        val stepX = (width.toFloat() - 150f) / (dataPoints.size - 1).coerceAtLeast(1)
        val baseLine = topMargin + (height - topMargin) / 2f
        val maxValue = dataPoints.maxOrNull() ?: 1f
        val maxAmplitude = (height - topMargin) * 0.4f

        val startX = 0f
        val normalizedStart = (dataPoints[0] / maxValue).coerceIn(0f, 1f)
        var startY = baseLine - normalizedStart * maxAmplitude
        path.moveTo(startX, startY)

        for (i in 1 until dataPoints.size) {
            val normalizedCurr = (dataPoints[i] / maxValue).coerceIn(0f, 1f)
            val currY = baseLine - normalizedCurr * maxAmplitude
            val prevY = baseLine - ((dataPoints[i - 1] / maxValue).coerceIn(0f, 1f)) * maxAmplitude

            val isPeakOrValley = if (i < dataPoints.size - 1) {
                val normalizedNext = (dataPoints[i + 1] / maxValue).coerceIn(0f, 1f)
                val nextY = baseLine - normalizedNext * maxAmplitude
                (currY < prevY && currY < nextY) || (currY > prevY && currY > nextY)
            } else {
                false
            }

            val x = i * stepX
            if (isPeakOrValley) {
                val leftX = x - stepX * 0.1f
                val rightX = x + stepX * 0.1f
                path.lineTo(leftX, currY)
                path.lineTo(rightX, currY)
            } else {
                path.lineTo(x, currY)
            }
        }

        val gradient = LinearGradient(
            0f, baseLine,
            width.toFloat(), baseLine,
            Color.parseColor("#1A14316B"),
            Color.parseColor("#9EBCFF"),
            Shader.TileMode.CLAMP
        )
        linePaint.shader = gradient

        canvas.drawPath(path, linePaint)

        val lastIndex = dataPoints.size - 1

        val normalizedLast = (dataPoints[lastIndex] / maxValue).coerceIn(0f, 1f)
        val lastX = lastIndex * stepX
        val padding = 14f
        val heart = "❤ "
        val bpmText = "${dataPoints[lastIndex].toInt()} bpm"
        val fullText = heart + bpmText

        val paintText = Paint().apply {
            color = Color.parseColor("#DFE3E7")
            textSize = textSizePx
            isAntiAlias = true
            typeface = customTypeface
        }

        val heartPaint = Paint().apply {
            color = "#FF8940".toColorInt()
            textSize = paintText.textSize
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val textBounds = android.graphics.Rect()
        paintText.getTextBounds(fullText, 0, fullText.length, textBounds)

        val textWidth = heartPaint.measureText(heart) + paintText.measureText(bpmText)
        val textHeight = textBounds.height().toFloat()

        val rectLeft = (lastX - textWidth / 2 - padding).coerceIn(0f, width - textWidth - 2 * padding)
        val rectTop = 1f
        val rectRight = rectLeft + textWidth + 2 * padding
        val rectBottom = rectTop + textHeight + 2 * padding

        val rect = android.graphics.RectF(rectLeft, rectTop, rectRight, rectBottom)

        val paintBg = Paint().apply {
            color = Color.parseColor("#1C1F21")
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = 0f
        }
        setLayerType(LAYER_TYPE_SOFTWARE, paintBg) // for shadow
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, paintBg)

        val paintBorder = Paint().apply {
            color = Color.parseColor("#151718")
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, paintBorder)

        val heartBitmap = BitmapFactory.decodeResource(resources, R.drawable.orange_heart)

        val heartSize = dpToPx(18f, context)
        val heartLeft = rectLeft + padding
        val heartTop = rectTop + (rect.height() - heartSize) / 2

        val destRect = Rect(
            heartLeft.toInt(), heartTop.toInt(),
            (heartLeft + heartSize).toInt(), (heartTop + heartSize).toInt()
        )

        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(Color.parseColor("#FF8940"), PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(heartBitmap, null, destRect, paint)

        val textX = heartLeft + heartSize + 10f
        val textY = rectTop + rect.height() / 2 + paintText.textSize / 3
        canvas.drawText(bpmText, textX, textY, paintText)

        val lineStartX = lastX
        val lineStartY = rect.bottom
        val lineEndX = lastX
        val lineEndY = 250f

        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, dashLine)
    }

    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }
}
