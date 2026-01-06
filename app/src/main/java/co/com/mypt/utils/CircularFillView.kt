package co.com.mypt.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import co.com.mypt.R

class CircularFillView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)  {

    var progress: Float = 0f  // Progress value (0 to 1)
        set(value) {
            field = value
            invalidate()  // Redraw the view whenever progress changes
        }

    var cornerRadius: Float = 100f  // Corner radius for rounded rectangle
        set(value) {
            field = value
            invalidate()
        }
    var strokewidth: Float = 20f  // Corner radius for rounded rectangle
        set(value) {
            field = value
            invalidate()
        }

     val bgPaint = Paint().apply {
        color = resources.getColor(R.color.progress_track_color,null)  // Unprogressed stroke color
        strokeWidth = 20f
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    val progressPaint = Paint().apply {
        color = Color.BLUE  // Progressive stroke color
        strokeWidth = strokewidth
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val path = Path()
    private val pathMeasure = PathMeasure()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat() - bgPaint.strokeWidth
        val height = height.toFloat() - bgPaint.strokeWidth

        val rect = RectF(
            bgPaint.strokeWidth / 2,
            bgPaint.strokeWidth / 2,
            width,
            height
        )

        // Create rounded rectangle path
        path.reset()
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CCW)

        // Measure total path length
        pathMeasure.setPath(path, true)
        val totalLength = pathMeasure.length

        // Calculate the start position at the top-center
        val topEdgeHalf = (rect.width() - 2 * cornerRadius) / 2
        val topCenterOffset = cornerRadius + topEdgeHalf

        // Draw full unprogressed path in background color
        canvas.drawPath(path, bgPaint)

        // Calculate end position based on progress
        val startPos = topCenterOffset
        val endPos = startPos + (totalLength * progress)

        // Draw the progressively revealed path
        val drawnPath = Path()
        if (endPos <= totalLength) {
            pathMeasure.getSegment(startPos, endPos, drawnPath, true)
        } else {
            pathMeasure.getSegment(startPos, totalLength, drawnPath, true)
            pathMeasure.getSegment(0f, endPos - totalLength, drawnPath, true)
        }

        // Draw progressive stroke over the background stroke
        canvas.drawPath(drawnPath, progressPaint)
    }
}
