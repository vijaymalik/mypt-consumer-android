package co.com.mypt.rulerHeight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import co.com.mypt.R
import kotlin.math.roundToInt


class SessionRulerViewHorizontal(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {

    // ===== View size =====
    private var viewWidth = 0
    private var viewHeight = 0
    private var midScreenPoint = 0

    // ===== Touch =====
    private var mainPoint = 0f
    private var downPoint = 0f
    private var isMove = false

    // ===== Drawing =====
    private var startingPoint = 0f
    private var endPoint = 0

    private var scaleLineSmall = 0
    private var scaleLineMedium = 0
    private var scaleLineLarge = 0
    private var textStartPoint = 0

    private lateinit var rulerPaint: Paint
    private lateinit var textPaint: TextPaint

    private var lastHapticTick = Int.MIN_VALUE
    private var mListener: onViewUpdateListenerWeight? = null

    // ===== SCALE CONFIG =====
    private val START_VALUE = 0
    private val INTERVAL = 5
    private val SUB_TICKS = 5   // 4 small ticks between numbers

    private var maxValue = 100f

    private val valuePerTick = INTERVAL / SUB_TICKS.toFloat()
    private val reduceTextGapPx by lazy {
        (60 * resources.displayMetrics.density).toInt() // try 4–8 dp
    }
    init {
        if (!isInEditMode) initPaints()
    }

    private fun initPaints() {
        rulerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = resources.getColor(R.color.rulerlargeline)
            strokeCap = Paint.Cap.ROUND
        }

        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = resources.getDimension(R.dimen.txt_size)
            color = resources.getColor(R.color.rulartextcolor)
        }

        scaleLineSmall = resources.getDimension(R.dimen.scale_line_small).toInt()
        scaleLineMedium = resources.getDimension(R.dimen.scale_line_medium).toInt()
        scaleLineLarge = resources.getDimension(R.dimen.session_scale_line_large).toInt()
        textStartPoint = resources.getDimension(R.dimen.text_start_point).toInt()
    }

    fun setUpdateListenerWeight(listener: onViewUpdateListenerWeight?) {
        mListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewWidth = w
        viewHeight = h

        screenSize = w
        pxmm = screenSize / 40f

        midScreenPoint = w / 2
        endPoint = h - 40
        applyDefaultValue()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        startingPoint = mainPoint
        var i = 0

        val visibleWidth = viewWidth + 20
        val fadeDistance = 200

        while (true) {

            if (startingPoint > screenSize) break
            startingPoint += pxmm

            val distanceFromCenter =
                kotlin.math.abs(startingPoint - midScreenPoint)

            val tickIndex = i
            val centerTickIndex = ((midScreenPoint - mainPoint) / pxmm).roundToInt()

            val isCenterTick = tickIndex == centerTickIndex
            val isMajorTick = i % SUB_TICKS == 0
//            val isMediumTick = i % SUB_TICKS == SUB_TICKS / 2

            val value = START_VALUE + (i / SUB_TICKS) * INTERVAL
            if (value > maxValue) break
            // ===== HAPTIC =====
            if (isCenterTick && isMajorTick && i != lastHapticTick) {
                lastHapticTick = i
                performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }

            // ===== LINE HEIGHT =====
            var lineHeight = when {
                isMajorTick -> scaleLineLarge
//                isMediumTick -> scaleLineMedium
                else -> scaleLineSmall
            }

            if (isCenterTick) {
                lineHeight = (lineHeight * 1.3f).toInt()
            }

            // ===== FADE =====
            val alpha = when {
                startingPoint < fadeDistance ->
                    ((startingPoint / fadeDistance) * 255).toInt()
                startingPoint > visibleWidth - fadeDistance ->
                    ((1 - ((startingPoint - (visibleWidth - fadeDistance)) / fadeDistance)) * 255).toInt()
                else -> 255
            }

            rulerPaint.alpha = alpha
            rulerPaint.strokeWidth = when {
                isCenterTick -> 5f
                /*isMajorTick -> 5f*/
                else -> 3f
            }

            // ===== DRAW LINE =====
            canvas.drawLine(
                startingPoint,
                (endPoint - lineHeight).toFloat(),
                startingPoint,
                endPoint.toFloat(),
                rulerPaint
            )

            // ===== DRAW TEXT =====
            if (isMajorTick) {

                textPaint.textSize = if (isCenterTick) {
                    resources.getDimension(R.dimen.txt_size) * 1.25f
                } else {
                    resources.getDimension(R.dimen.txt_size)
                }

                textPaint.color = if (isCenterTick) {
                    resources.getColor(R.color.center_text_weight)
                } else {
                    resources.getColor(R.color.rulartextcolor)
                }

                val label = value.toString()
                val textWidth = textPaint.measureText(label)
                val textYOffset = if (isCenterTick) 12 else 0

                canvas.drawText(
                    label,
                    startingPoint - textWidth / 2f,
                    (endPoint - textStartPoint +reduceTextGapPx-textYOffset).toFloat(),
                    textPaint
                )
            }

            i++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                isMove = true
                downPoint = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isMove) return true

                val diff = event.x - downPoint
                if (kotlin.math.abs(diff) > 1) {
                    mainPoint += diff
                    downPoint = event.x

                    val minPoint = getMinMainPoint()

                    if (mainPoint > midScreenPoint) {
                        mainPoint = midScreenPoint.toFloat()
                        isMove = false
                    } else if (mainPoint < minPoint) {
                        mainPoint = minPoint
                        isMove = false
                    }

                    invalidate()
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {

            }
        }

        val totalTicks = (midScreenPoint - mainPoint) / pxmm

        var value = START_VALUE + (totalTicks * valuePerTick)

// snap to exact tick
        value = (value / valuePerTick).toInt() * valuePerTick

// clamp
        value = value.coerceIn(START_VALUE.toFloat(), maxValue)

        mListener?.onViewUpdate(value)

        return true
    }
    private var defaultValue = 0f

    fun setDefaultValue(value: Float) {
        defaultValue = value.coerceIn(START_VALUE.toFloat(), maxValue)

        if (viewWidth > 0) {
            applyDefaultValue()
            invalidate()
        }
    }
    private fun applyDefaultValue() {
        // number of ticks from start
        val totalTicks = (defaultValue - START_VALUE) / valuePerTick

        // move ruler so this value is at center
        mainPoint = midScreenPoint - (totalTicks * pxmm)
    }

    fun setMaxValue(max: Float) {
        maxValue = max
        invalidate()
    }

    private fun getMinMainPoint(): Float {
        val totalTicks = (maxValue - START_VALUE)
        return midScreenPoint - (totalTicks * pxmm)
    }

    companion object {
        var screenSize = 500
        var pxmm = screenSize / 40f
    }
}
