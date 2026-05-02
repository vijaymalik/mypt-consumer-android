package co.com.mypt.rulerHeight

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import co.com.mypt.R
import kotlin.math.abs
import kotlin.math.roundToInt

class SessionRulerViewHorizontal @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ===== View State =====
    private var viewWidth = 0
    private var midScreenPoint = 0
    private var mainPoint = 0f
    private var downPoint = 0f
    private var isMoving = false
    private var lastHapticTick = -1

    // ===== Configuration =====
    private val START_VALUE = 0
    private var maxValue = 100f
    private val INTERVAL = 5
    private val SUB_TICKS = 5
    private val valuePerTick = INTERVAL / SUB_TICKS.toFloat()

    private var pxmm = 0f // Pixels per tick
    private var scrollAnimator: ValueAnimator? = null
    private var mListener: onViewUpdateListenerWeight? = null

    // ===== Paints & Dimensions =====
    private lateinit var rulerPaint: Paint
    private lateinit var textPaint: TextPaint
    private var scaleLineSmall = 0f
    private var scaleLineLarge = 0f
    private var textStartPoint = 0f
    private val reduceTextGapPx = (40 * resources.displayMetrics.density)

    init {
        initPaints()
    }

    private fun initPaints() {
        rulerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = resources.getDimension(R.dimen.txt_size)
        }

        scaleLineSmall = resources.getDimension(R.dimen.scale_line_small)
        scaleLineLarge = resources.getDimension(R.dimen.session_scale_line_large)
        textStartPoint = resources.getDimension(R.dimen.text_start_point)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewWidth = w
        midScreenPoint = w / 2

        // Important: Re-calculate pixels per millimeter based on actual width
        pxmm = w / 30f

        // Scroll to the saved value now that we have a width
        val totalTicks = (currentValue - START_VALUE) / valuePerTick
        mainPoint = midScreenPoint - (totalTicks * pxmm)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Find the exact single index that is closest to the center
        val centerTickIndex = ((midScreenPoint - mainPoint) / pxmm).roundToInt()
        val fadeDistance = viewWidth / 4f

        var i = 0
        while (true) {
            val xPos = mainPoint + (i * pxmm)

            if (xPos < -100) { i++; continue }
            if (xPos > viewWidth + 100) break

            val value = START_VALUE + (i * valuePerTick)
            if (value > maxValue) break

            // STRICT CHECKS
            val isExactlyCenter = (i == centerTickIndex)
            val isMajor = (i % SUB_TICKS == 0)

            // 1. Line Style (Strict)
            if (isExactlyCenter) {
                rulerPaint.color = resources.getColor(R.color.center_text_weight)
                rulerPaint.strokeWidth = 6f
                rulerPaint.alpha = 255
                // Make ONLY this one long
                val lineHeight = scaleLineLarge * 2.2f
                canvas.drawLine(xPos, height.toFloat() - lineHeight, xPos, height.toFloat(), rulerPaint)
            } else {
                rulerPaint.color = resources.getColor(R.color.rulerlargeline)
                rulerPaint.strokeWidth = if (isMajor) 4f else 2f

                // Standard Fading for non-center ticks
                val alpha = when {
                    xPos < fadeDistance -> (xPos / fadeDistance * 255).toInt()
                    xPos > viewWidth - fadeDistance -> ((viewWidth - xPos) / fadeDistance * 255).toInt()
                    else -> 255
                }.coerceIn(0, 255)
                rulerPaint.alpha = alpha

                val lineHeight = if (isMajor) scaleLineLarge else scaleLineSmall
                canvas.drawLine(xPos, height.toFloat() - lineHeight, xPos, height.toFloat(), rulerPaint)
            }

            // 2. Text Logic
            // Show text if it's a major tick OR the one exactly in the center
            if (isMajor || isExactlyCenter) {
                val label = value.toInt().toString()

                // Position the text
                val textY = if (isExactlyCenter) {
                    // Higher for the center highlight
                    height - (scaleLineLarge * 2.2f) - 30f
                } else {
                    // Standard height for regular numbers
                    height - scaleLineLarge - 30f
                }

                textPaint.color = if (isExactlyCenter) resources.getColor(R.color.center_text_weight)
                else resources.getColor(R.color.rulartextcolor)

                // If it's a major tick right next to the center one, we dim it to prevent overlap
                textPaint.alpha = if (isExactlyCenter) 255 else {
                    val dist = abs(i - centerTickIndex)
                    if (dist == 1) 50 else rulerPaint.alpha // Very faint if it's a neighbor
                }

                textPaint.textSize = if (isExactlyCenter) resources.getDimension(R.dimen.txt_size) * 1.4f
                else resources.getDimension(R.dimen.txt_size)

                canvas.drawText(label, xPos, textY, textPaint)
            }
            i++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scrollAnimator?.cancel()
                isMoving = true
                downPoint = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val diff = event.x - downPoint
                mainPoint += diff
                downPoint = event.x

                // Boundaries
                val minPoint = midScreenPoint - ((maxValue / valuePerTick) * pxmm)
                mainPoint = mainPoint.coerceIn(minPoint, midScreenPoint.toFloat())

                // Haptics on every tick cross
                val centerTickIndex = ((midScreenPoint - mainPoint) / pxmm).roundToInt()
                if (centerTickIndex != lastHapticTick) {
                    lastHapticTick = centerTickIndex
                    performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    notifyUpdate(centerTickIndex)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isMoving = false
                smoothSnapToValue()
            }
        }
        return true
    }

    private fun smoothSnapToValue() {
        val targetTicks = ((midScreenPoint - mainPoint) / pxmm).roundToInt()
        val targetMainPoint = midScreenPoint - (targetTicks * pxmm)

        scrollAnimator = ValueAnimator.ofFloat(mainPoint, targetMainPoint).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                mainPoint = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        notifyUpdate(targetTicks)
    }

    private fun notifyUpdate(ticks: Int) {
        val finalValue = START_VALUE + (ticks * valuePerTick)
        mListener?.onViewUpdate(finalValue.coerceIn(0f, maxValue))
    }

    fun applyDefaultValue(value: Float) {
        post {
            val totalTicks = (value - START_VALUE) / valuePerTick
            mainPoint = midScreenPoint - (totalTicks * pxmm)
            invalidate()
        }
    }

    fun setUpdateListenerWeight(listener: onViewUpdateListenerWeight?) {
        this.mListener = listener
    }
    /**
     * Sets the maximum value of the ruler and refreshes the view.
     */
    fun setMaxValue(max: Float) {
        this.maxValue = max
        invalidate()
    }

    /**
     * Moves the ruler to a specific value.
     * Uses .post to ensure midScreenPoint is calculated if called during initialization.
     */
    private var currentValue: Float = 0f // Store the value here

    fun setDefaultValue(value: Float) {
        this.currentValue = value.coerceIn(START_VALUE.toFloat(), maxValue)

        // Case A: View is already measured (dynamic update)
        if (viewWidth > 0) {
            val totalTicks = (currentValue - START_VALUE) / valuePerTick
            mainPoint = midScreenPoint - (totalTicks * pxmm)
            invalidate()
        } else {
            // Case B: View is not ready (initial setup)
            // onSizeChanged will pick up 'currentValue' and scroll to it
            post {
                if (viewWidth > 0) {
                    val totalTicks = (currentValue - START_VALUE) / valuePerTick
                    mainPoint = midScreenPoint - (totalTicks * pxmm)
                    invalidate()
                }
            }
        }
    }
}