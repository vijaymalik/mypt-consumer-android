package co.com.mypt.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import co.com.mypt.R
import kotlin.math.min

class CustomSplitCircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var segments = 8
    var progress = 5 // how many segments to fill
    var segmentWidth = 2f
    var gapAngle = 4f
    var filledColor =  context.getColor(R.color.orangecolor)
    var emptyColor = context.getColor(R.color.progress_track_color)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val radius = size / 2 - segmentWidth
        val rect = RectF(
            width / 2f - radius,
            height / 2f - radius,
            width / 2f + radius,
            height / 2f + radius
        )

        val sweepAngle = (360f - gapAngle * segments) / segments

        for (i in 0 until segments) {
            paint.color = if (i < progress) filledColor else emptyColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = segmentWidth
            val startAngle = i * (sweepAngle + gapAngle) - 90
            canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
        }
    }
}
