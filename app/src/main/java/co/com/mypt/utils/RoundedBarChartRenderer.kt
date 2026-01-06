package co.com.mypt.utils

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarChart,
    animator: com.github.mikephil.charting.animation.ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val radius = 12f // Radius for rounded corners

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        val phaseY = mAnimator.phaseY

        for (i in 0 until dataSet.entryCount) {
            val entry = dataSet.getEntryForIndex(i) as BarEntry
            val vals = entry.yVals ?: continue

            var posY = 0f
            var negY = 0f

            for (k in vals.indices) {
                val value = vals[k]
                val yStart: Float
                val yEnd: Float

                if (value >= 0f) {
                    yStart = posY
                    posY += value
                    yEnd = posY
                } else {
                    yStart = negY
                    negY += value
                    yEnd = negY
                }

                val left = entry.x - mChart.barData.barWidth / 2f
                val right = entry.x + mChart.barData.barWidth / 2f

                val top = yEnd
                val bottom = yStart

                val barRect = RectF(left, top, right, bottom)
                trans.rectToPixelPhase(barRect, phaseY)

                val path = Path()
                val isFirst = k == 0
                val isLast = k == vals.size - 1

                val radii = when {
                    isFirst && isLast -> floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
                    isFirst -> floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius) // bottom
                    isLast -> floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f) // top
                    else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
                }

                path.addRoundRect(barRect, radii, Path.Direction.CW)

                mRenderPaint.color = dataSet.getColor(k)
                c.drawPath(path, mRenderPaint)
            }
        }
    }
}
