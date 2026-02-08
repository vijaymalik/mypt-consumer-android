package co.com.mypt.curvedBottomNavigation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class CurvedBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#111111")
        style = Paint.Style.FILL
    }

    private val neonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fadeDistancePx = 30f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        val curveHeight = h * 0.3f

        /* ---------- Background Path ---------- */

        val bgPath = Path().apply {
            moveTo(0f, curveHeight)
            quadTo(w / 2, 0f, w, curveHeight)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        canvas.drawPath(bgPath, bgPaint)

        /* ---------- Neon Curve Path ---------- */

        val curvePath = Path().apply {
            moveTo(0f, curveHeight)
            quadTo(w / 2, 0f, w, curveHeight)
        }

        val pathMeasure = PathMeasure(curvePath, false)
        val pathLength = pathMeasure.length

        val start = fadeDistancePx
        val end = pathLength - fadeDistancePx

        val visiblePath = Path()
        pathMeasure.getSegment(start, end, visiblePath, true)

        /* ---------- Gradient (Fade Edges) ---------- */

        val gradient = LinearGradient(
            0f, 0f,
            w, 0f,

            intArrayOf(
                Color.TRANSPARENT,
                Color.parseColor("#00FFCC"),
                Color.parseColor("#00FFCC"),
                Color.TRANSPARENT
            ),

            floatArrayOf(
                0f,
                0.15f,
                0.85f,
                1f
            ),

            Shader.TileMode.CLAMP
        )

        neonPaint.shader = gradient

        /* ---------- Stroke Width (Taper) ---------- */

        val centerThickness = dpToPx(2f)
        val edgeThickness = dpToPx(0.5f)

        neonPaint.strokeWidth = centerThickness

        neonPaint.setShadowLayer(
            12f,
            0f,
            0f,
            Color.parseColor("#00FFCC")
        )

        canvas.drawPath(visiblePath, neonPaint)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
