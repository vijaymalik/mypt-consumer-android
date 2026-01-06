package co.com.mypt.BarChart

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler



class RoundedBarChartWithThreeColors(
    chart: BarDataProvider, animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    private var mRadius = 30f // rounded corner radius

    fun setRadius(radius: Float) {
        this.mRadius = radius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mShadowPaint.color = dataSet.barShadowColor

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        // Draw shadows if enabled
        if (mChart.isDrawBarShadowEnabled) {
            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

                // Draw shadow behind the bar with rounded corners
                val shadowRect = RectF(
                    buffer.buffer[j], mViewPortHandler.contentTop(),
                    buffer.buffer[j + 2], mViewPortHandler.contentBottom()
                )

                c.drawRoundRect(shadowRect, mRadius, mRadius, mShadowPaint)
                j += 4
            }
        }

        // Draw bars with rounded corners and colors
        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

            // Set paint color for current bar, supports multiple colors
            mRenderPaint.color = dataSet.getColor(j / 4)

            val barRect = RectF(
                buffer.buffer[j], buffer.buffer[j + 1],
                buffer.buffer[j + 2], buffer.buffer[j + 3]
            )

            c.drawRoundRect(barRect, mRadius, mRadius, mRenderPaint)
            j += 4
        }
    }
}
