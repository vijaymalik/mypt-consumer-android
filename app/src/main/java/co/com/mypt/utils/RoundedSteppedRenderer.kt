package co.com.mypt.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedSteppedRenderer(
    chart: LineChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val cornerRadius: Float = 20f // Radius of rounded corners
) : LineChartRenderer(chart, animator, viewPortHandler) {

    private val path = Path()
    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.BLUE // Customize color
        isAntiAlias = true
    }

    override fun drawLinear(c: Canvas, dataSet: ILineDataSet) {
        if (dataSet.entryCount < 2) return

        val trans = mChart.getTransformer(dataSet.axisDependency)
        val phaseY = mAnimator.phaseY

        path.reset()

        val entryCount = dataSet.entryCount
        val firstEntry = dataSet.getEntryForIndex(0)
        val pt = floatArrayOf(firstEntry.x, firstEntry.y * phaseY)
        trans.pointValuesToPixel(pt)
        path.moveTo(pt[0], pt[1]) // Move to first point

        for (i in 1 until entryCount) {
            val prevEntry = dataSet.getEntryForIndex(i - 1)
            val curEntry = dataSet.getEntryForIndex(i)

            val prevPt = floatArrayOf(prevEntry.x, prevEntry.y * phaseY)
            val curPt = floatArrayOf(curEntry.x, curEntry.y * phaseY)
            trans.pointValuesToPixel(prevPt)
            trans.pointValuesToPixel(curPt)

            val midX = (prevPt[0] + curPt[0]) / 2

            // 1️⃣ Draw the horizontal step BEFORE the curve
            path.lineTo(midX - cornerRadius, prevPt[1])

            // 2️⃣ Bezier curve for rounded corner
            path.cubicTo(
                midX - cornerRadius, prevPt[1],  // Control point 1
                midX + cornerRadius, curPt[1],  // Control point 2
                midX + cornerRadius, curPt[1]   // End point
            )

            // 3️⃣ Draw the vertical step AFTER the curve
            path.lineTo(curPt[0], curPt[1])
        }

        c.drawPath(path, linePaint)
    }
}
