package co.com

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SnappingRulerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val largeSpikeHeight = 60f
    private val smallSpikeHeight = 30f
    private val spikeWidth = 4f
    private val spikeInterval = 10  // large spike every 10 units
    private val pixelsPerUnit = 15f  // 20 pixels per unit
    private val largeSpikeTextColor = Color.parseColor("#494C4D")
    private val largeSpikeColor = Color.parseColor("#828485")
    private val smallSpikeColor = Color.parseColor("#525556")
    private val centerTextColor = Color.WHITE

    private val maxAlpha = 255
    private val fadeDistance = 350f  // pixels from center where alpha fades to 0

    private val paintSpike = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 34f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val centerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 64f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        color = centerTextColor
    }

    private val scroller = OverScroller(context)
    private val velocityTracker = VelocityTracker.obtain()
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
    private val minimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity

    private var lastTouchX = 0f
    private var isDragging = false

    private var scrollOffset = 0f  // pixels scrolled, >= 0

    private val maxUnits = 1000
    private val maxScrollOffset = maxUnits * pixelsPerUnit

    private var lastReportedValue = -1
    var onValueChangeListener: ((Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // Calculate the centered value rounded to nearest integer
        val centerValue = (scrollOffset / pixelsPerUnit).roundToInt()

        if (centerValue != lastReportedValue) {
            lastReportedValue = centerValue
            onValueChangeListener?.invoke(centerValue)
        }

        // Draw spikes from 0 to maxUnits
        for (i in 0..maxUnits) {
            val x = centerX + i * pixelsPerUnit - scrollOffset
            if (x < -50 || x > width + 50) continue
            val isLargeSpike = (i % spikeInterval == 0)
            val spikeHeight = if (isLargeSpike) largeSpikeHeight else smallSpikeHeight
            paintSpike.color = if (isLargeSpike) largeSpikeColor else smallSpikeColor
            val fadeZone = fadeDistance  // e.g., 200f pixels
            val distanceFromLeftEdge = x
            val distanceFromRightEdge = width - x
            val alpha = when {
                distanceFromLeftEdge < fadeZone -> ((distanceFromLeftEdge / fadeZone) * maxAlpha).toInt()
                distanceFromRightEdge < fadeZone -> ((distanceFromRightEdge / fadeZone) * maxAlpha).toInt()
                else -> maxAlpha
            }.coerceIn(0, maxAlpha)

            paintSpike.alpha = alpha
            // Draw spike
            canvas.drawRect(
                x - spikeWidth / 2,
                centerY - spikeHeight,
                x + spikeWidth / 2,
                centerY,
                paintSpike
            )

            // Draw text only on large spikes
            if (isLargeSpike  && i != centerValue) {
                paintText.color = largeSpikeTextColor
                paintText.alpha = alpha
                paintText.textSize = 44f
                canvas.drawText(i.toString(), x, centerY - 80f, paintText)
            }
        }

        // Draw large centered value above the ruler
        val rectWidth = 120f  // adjust width as needed to cover center spike and text area
        val rectHeight = 100f
        val rectLeft = centerX - rectWidth / 2
        val rectTop = centerY - 80f - rectHeight + 20f
        val rectRight = centerX + rectWidth / 2
        val rectBottom = centerY - 80f + 20f
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK  // same as background or desired overlay color
            style = Paint.Style.FILL
        }
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, backgroundPaint)
        // Draw large centered value text on top
        centerTextPaint.alpha = maxAlpha
        centerTextPaint.textSize = 64f
        canvas.drawText(centerValue.toString(), centerX, centerY - 80f, centerTextPaint)
    }

    val centeredValue: Int
        get() = (scrollOffset / pixelsPerUnit).roundToInt()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        velocityTracker.addMovement(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) scroller.abortAnimation()
                lastTouchX = event.x
                isDragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = lastTouchX - event.x
                if (!isDragging && abs(dx) > touchSlop) isDragging = true
                if (isDragging) {
                    scrollBy(dx)
                    lastTouchX = event.x
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    velocityTracker.computeCurrentVelocity(1000, maximumVelocity.toFloat())
                    val velocityX = velocityTracker.xVelocity
                    if (abs(velocityX) > minimumVelocity) {
                        fling(-velocityX.toInt())
                    } else {
                        // No fling, just snap to nearest spike
                        snapToNearest()
                    }
                } else {
                    // Not dragging, snap to nearest spike
                    snapToNearest()
                }
                velocityTracker.clear()
                isDragging = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun scrollBy(dx: Float) {
        scrollOffset += dx
        scrollOffset = max(0f, min(scrollOffset, maxScrollOffset))
        invalidate()
    }

    private fun fling(velocityX: Int) {
        scroller.fling(
            scrollOffset.toInt(), 0,
            velocityX, 0,
            0, maxScrollOffset.toInt(),
            0, 0
        )
        invalidate()
    }

    private fun snapToNearest() {
        val nearestUnit = (scrollOffset / pixelsPerUnit).roundToInt()
        val targetScroll = nearestUnit * pixelsPerUnit
        scroller.startScroll(scrollOffset.toInt(), 0, (targetScroll - scrollOffset).toInt(), 0, 300)
        invalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollOffset = scroller.currX.toFloat()
            invalidate()
        }
    }
}