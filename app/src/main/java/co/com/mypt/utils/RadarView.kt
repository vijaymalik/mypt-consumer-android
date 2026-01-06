package co.com.mypt.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// Axis model to hold label, raw value, and its min/max
data class Axis(val label: String, val value: Float, val min: Float, val max: Float)

class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var axes: List<Axis> = emptyList()
    private var count: Int = 0
    private var radius: Float = 0f
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var angle: Float = 0f

    // Paints
    private val webPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val valuePaint = Paint().apply {
        color = Color.parseColor("#8020c997") // translucent green
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        textSize = 30f
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        color = Color.parseColor("#339966")
        style = Paint.Style.FILL
        alpha = 120
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (axes.isEmpty()) return

        count = axes.size
        angle = (2 * Math.PI / count).toFloat()
        centerX = width / 2
        centerY = height / 2
        radius = min(width, height) * 0.35f

        drawWeb(canvas)
        drawLabels(canvas)
        drawDataShapeWithValleys(canvas)
    }

    // Normalization helper
    private fun normalize(value: Float, min: Float, max: Float): Float {
        /*if (max == min) return 0f
        Log.e("normalizeValue","${(value - min) / (max - min)}")
        return (value - min) / (max - min)*/
        return value/100
    }

    private fun drawWeb(canvas: Canvas) {
        val levels = 4
        val cornerRadius = radius * 0.05f // Relative to web size
        val webPath = Path()

        for (level in 1..levels) {
            webPath.reset()
            val levelRadius = radius * level / levels

            for (i in 0 until count) {
                val theta1 = angle * i - Math.PI.toFloat() / 2
                val theta2 = angle * (i + 1) - Math.PI.toFloat() / 2

                val startX = centerX + levelRadius * cos(theta1)
                val startY = centerY + levelRadius * sin(theta1)

                val endX = centerX + levelRadius * cos(theta2)
                val endY = centerY + levelRadius * sin(theta2)

                val ctrlX1 = startX + cornerRadius * cos(theta1 + Math.PI.toFloat() / 4)
                val ctrlY1 = startY + cornerRadius * sin(theta1 + Math.PI.toFloat() / 4)

                val ctrlX2 = endX + cornerRadius * cos(theta2 - Math.PI.toFloat() / 4)
                val ctrlY2 = endY + cornerRadius * sin(theta2 - Math.PI.toFloat() / 4)

                if (i == 0) {
                    webPath.moveTo(startX, startY)
                }

                webPath.cubicTo(
                    ctrlX1, ctrlY1,
                    ctrlX2, ctrlY2,
                    endX, endY
                )
            }
            webPath.close()
            canvas.drawPath(webPath, webPaint)
        }

        // Radial lines
        val radialPath = Path()
        for (i in 0 until count) {
            val theta = angle * i - PI.toFloat() / 2
            radialPath.reset()

            // Start slightly inside to create rounded connection at center
            val startOffset = radius * 0.18f
            radialPath.moveTo(
                (centerX + startOffset * cos(theta)).toFloat(),
                (centerY + startOffset * sin(theta)).toFloat()
            )

            // End slightly before the edge to create rounded connection
            val endOffset = radius * 0.99f
            radialPath.lineTo(
                (centerX + endOffset * cos(theta)).toFloat(),
                (centerY + endOffset * sin(theta)).toFloat()
            )

            canvas.drawPath(radialPath, webPaint)
        }

        drawStar(canvas, centerX.toFloat(), centerY.toFloat(), 20f, 40f, 6) // Adjust size and points as needed
    }

    private fun drawLabels(canvas: Canvas) {
        val metrics = textPaint.fontMetrics
        val labelOffset = radius + 40

        for (i in 0 until count) {
            val lines: List<String> = axes[i].label.split("\n")
            val lineHeight = metrics.descent - metrics.ascent

            val theta = angle * i - Math.PI.toFloat() / 2
            val x = (centerX + labelOffset * cos(theta))
            val y = (centerY + labelOffset * sin(theta))
            for (i in 0..<lines.size) {
                canvas.drawText(lines[i], x, y + lineHeight * i, textPaint)
            }
            //canvas.drawText(lines[i], x, y + (metrics.descent - metrics.ascent) / 4, textPaint)
        }
    }

    private fun drawDataShapeWithValleys(canvas: Canvas) {
        val outerPoints = Array(count) { i ->
            val theta = angle * i - Math.PI.toFloat() / 2
            val axis = axes[i]
            val normalized = normalize(axis.value, axis.min, axis.max)

            PointF(
                (centerX + radius * normalized * cos(theta)).toFloat(),
                (centerY + radius * normalized * sin(theta)).toFloat()
            )
        }

        val valleyFactor = 0.5f        // inward pull for valleys (0.3 = deep spikes, 0.7 = shallow)
        val outerCornerRadius = 40f    // 🔥 larger radius for outer spikes
        val valleyCornerRadius = 35f   // smaller radius for valleys
        val starPoints = mutableListOf<Pair<PointF, Boolean>>() // point + isOuter

        // Build alternating outer + valley points
        for (i in outerPoints.indices) {
            val current = outerPoints[i]
            val next = outerPoints[(i + 1) % count]

            // Add outer
            starPoints.add(current to true)

            // Add valley (midpoint pulled inward)
            val midX = (current.x + next.x) / 2
            val midY = (current.y + next.y) / 2
            val valleyX = centerX + (midX - centerX) * valleyFactor
            val valleyY = centerY + (midY - centerY) * valleyFactor
            starPoints.add(PointF(valleyX, valleyY) to false)
        }

        val path = Path()
        for (i in starPoints.indices) {
            val (current, isOuter) = starPoints[i]
            val (prev, _) = starPoints[(i - 1 + starPoints.size) % starPoints.size]
            val (next, _) = starPoints[(i + 1) % starPoints.size]

            val radius = if (isOuter) outerCornerRadius else valleyCornerRadius

            // Direction vectors
            val v1 = PointF(current.x - prev.x, current.y - prev.y)
            val v2 = PointF(next.x - current.x, next.y - current.y)

            val v1Len = sqrt(v1.x * v1.x + v1.y * v1.y)
            val v2Len = sqrt(v2.x * v2.x + v2.y * v2.y)

            val start = PointF(
                current.x - v1.x / v1Len * radius,
                current.y - v1.y / v1Len * radius
            )
            val end = PointF(
                current.x + v2.x / v2Len * radius,
                current.y + v2.y / v2Len * radius
            )

            val ctrl1 = PointF(
                start.x + v1.x / v1Len * radius,
                start.y + v1.y / v1Len * radius
            )
            val ctrl2 = PointF(
                end.x - v2.x / v2Len * radius,
                end.y - v2.y / v2Len * radius
            )

            if (i == 0) {
                path.moveTo(start.x, start.y)
            } else {
                path.lineTo(start.x, start.y)
            }

            path.cubicTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, end.x, end.y)
        }

        path.close()

        // Fill + stroke
        canvas.drawPath(path, fillPaint)
        valuePaint.style = Paint.Style.STROKE
        valuePaint.strokeWidth = 4f
        canvas.drawPath(path, valuePaint)
    }



    // Setter for data
    fun setAxes(newAxes: List<Axis>) {
        axes = newAxes
        invalidate()
    }

    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, innerRadius: Float, outerRadius: Float, points: Int) {
        val starPath = Path()
        val angleStep = Math.PI * 2 / points

        for (i in 0 until points * 2) {
            val currentRadius = if (i % 2 == 0) outerRadius else innerRadius
            val currentAngle = i * angleStep / 2 - Math.PI / 2

            // Don't round the very first point (we'll close the path later)
            if (i == 0) {
                starPath.moveTo(
                    cx + currentRadius * cos(currentAngle).toFloat(),
                    cy + currentRadius * sin(currentAngle).toFloat()
                )
                continue
            }

            val prevRadius = if ((i-1) % 2 == 0) outerRadius else innerRadius
            val prevAngle = (i-1) * angleStep / 2 - Math.PI / 2

            val x = cx + currentRadius * cos(currentAngle)
            val y = cy + currentRadius * sin(currentAngle)

            // Calculate control points for smooth rounded corners
            val tangent1X = cx + prevRadius * cos(prevAngle + angleStep/5.5)
            val tangent1Y = cy + prevRadius * sin(prevAngle + angleStep/5.5)

            val tangent2X = cx + currentRadius * cos(currentAngle - angleStep/5.5)
            val tangent2Y = cy + currentRadius * sin(currentAngle - angleStep/5.5)

            // Draw the rounded corner
            starPath.cubicTo(
                tangent1X.toFloat(), tangent1Y.toFloat(),
                tangent2X.toFloat(), tangent2Y.toFloat(),
                x.toFloat(), y.toFloat()
            )
        }

        // Close the path with a final rounded segment
        val firstPointAngle = -Math.PI / 2
        val lastPointRadius = if ((points*2-1) % 2 == 0) outerRadius else innerRadius
        val lastPointAngle = (points*2-1) * angleStep / 2 - Math.PI / 2

        val tangent1X = cx + lastPointRadius * cos(lastPointAngle + angleStep/4)
        val tangent1Y = cy + lastPointRadius * sin(lastPointAngle + angleStep/4)

        val tangent2X = cx + outerRadius * cos(firstPointAngle - angleStep/4)
        val tangent2Y = cy + outerRadius * sin(firstPointAngle - angleStep/4)

        starPath.cubicTo(
            tangent1X.toFloat(), tangent1Y.toFloat(),
            tangent2X.toFloat(), tangent2Y.toFloat(),
            cx + outerRadius * cos(firstPointAngle).toFloat(),
            cy + outerRadius * sin(firstPointAngle).toFloat()
        )

        canvas.drawPath(starPath, webPaint)
    }

}