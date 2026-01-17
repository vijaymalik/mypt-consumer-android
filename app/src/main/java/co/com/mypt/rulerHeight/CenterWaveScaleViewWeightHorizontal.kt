package co.com.mypt.rulerHeight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import co.com.mypt.R

class CenterWaveScaleViewWeightHorizontal(
    mycontext: Context,
    attrs: AttributeSet?
) : View(mycontext, attrs) {

    var viewWidth = 0
    var viewHeight = 0
    var midScreenPoint = 0

    var startingPoint = 0f
    var downPoint = 0f
    var movablePoint = 0f
    var downPointClone = 0f

    private var mainPoint = 0f
    private var mainPointClone = 0f

    private var isMove = false

    private var mListener: onViewUpdateListenerWeight? = null

    lateinit var rulerPaint: Paint
    lateinit var textPaint: Paint
    lateinit var goldenPaint: Paint

    private var rulerSize = 0f
    private var endPoint = 0

    var isSizeChanged = false
    var userStartingPoint = 0f

    private var scaleLineSmall = 0
    private var scaleLineMedium = 0
    private var scaleLineLarge = 0
    private var textStartPoint = 0

    // ===== WAVE CONFIG =====
    private val WAVE_ITEM_COUNT = 7
    private val WAVE_HALF = WAVE_ITEM_COUNT / 2   // 3

    // ===== WEIGHT SCALE CONFIG =====
    private val STEP_VALUE = 0.1f        // 0.1, 0.2 ...
    private val STEPS_PER_UNIT = 10       // 10 steps = 1.0
    private val CENTER_WEIGHT = 0.5f

    var isFirstTime = true
    private val reduceTextGapPx by lazy {
        (20 * resources.displayMetrics.density).toInt() // try 4–8 dp
    }

    init {
        if (!isInEditMode) initPaints()
    }

    private fun initPaints() {

        rulerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }

        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = resources.getDimension(R.dimen.txt_size)
            color = resources.getColor(R.color.rulartextcolor)
        }

        goldenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            color = resources.getColor(R.color.yellow)
            strokeCap = Paint.Cap.ROUND
        }

        scaleLineSmall = resources.getDimension(R.dimen.scale_line_small).toInt()
        scaleLineMedium = resources.getDimension(R.dimen.scale_line_medium).toInt()
        scaleLineLarge = resources.getDimension(R.dimen.scale_line_large).toInt()
        textStartPoint = resources.getDimension(R.dimen.text_start_point).toInt()
    }

    fun setUpdateListenerWeight(listener: onViewUpdateListenerWeight?) {
        mListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        viewWidth = w
        viewHeight = h

        screenSize = width
        pxmm = screenSize / 50f

        rulerSize = pxmm * 10
        midScreenPoint = width / 2
        endPoint = height - 40

        if (isSizeChanged) {
            isSizeChanged = false
            mainPoint = midScreenPoint - (userStartingPoint * 10 * pxmm)
        }
    }

    override fun onDraw(canvas: Canvas) {

        startingPoint = mainPoint
        var i = 0

        val visibleWidth = viewWidth + 20
        val fadeDistance = 200
        val waveRangePx = pxmm * WAVE_HALF

        while (true) {

            if (startingPoint > screenSize) break
            startingPoint += pxmm

            val distanceFromCenter =
                kotlin.math.abs(startingPoint - midScreenPoint)

            val waveRatio = if (distanceFromCenter < waveRangePx) {
                1f - (distanceFromCenter / waveRangePx)
            } else 0f

            val baseSize = when {
                i % 10 == 0 -> scaleLineLarge
                i % 5 == 0 -> scaleLineMedium
                else -> scaleLineSmall
            }

            val waveExtra = (baseSize * 0.6f * waveRatio).toInt()
            val finalSize = baseSize + waveExtra

            val alpha = when {
                startingPoint < fadeDistance ->
                    ((startingPoint / fadeDistance) * 255).toInt()
                startingPoint > visibleWidth - fadeDistance ->
                    ((1 - ((startingPoint - (visibleWidth - fadeDistance)) / fadeDistance)) * 255).toInt()
                else -> 255
            }

            rulerPaint.color = Color.argb(
                alpha,
                Color.red(resources.getColor(R.color.rulerlargeline)),
                Color.green(resources.getColor(R.color.rulerlargeline)),
                Color.blue(resources.getColor(R.color.rulerlargeline))
            )

            rulerPaint.strokeWidth =
                (if (i % 10 == 0 || i % 5 == 0) 5f else 2f) + (waveRatio * 2f)

            // ===== DRAW HORIZONTAL SCALE LINE =====
            canvas.drawLine(
                startingPoint,
                (endPoint - finalSize).toFloat(),
                startingPoint,
                endPoint.toFloat(),
                rulerPaint
            )

            // ===== DRAW TEXT =====
            if (i % 10 == 0) {

                val isCenterText = distanceFromCenter < pxmm / 2

                textPaint.color = if (isCenterText) {
                    resources.getColor(R.color.center_text_weight)
                } else {
                    Color.argb(
                        alpha,
                        Color.red(resources.getColor(R.color.rulartextcolor)),
                        Color.green(resources.getColor(R.color.rulartextcolor)),
                        Color.blue(resources.getColor(R.color.rulartextcolor))
                    )
                }

                textPaint.textSize = if (isCenterText) {
                    resources.getDimension(R.dimen.txt_size) * 1.15f
                } else {
                    resources.getDimension(R.dimen.txt_size)
                }
                val label = "${i / 10}"
                val textWidth = textPaint.measureText(label)
                canvas.drawText(
                    label,
                    startingPoint - textWidth / 2f,
                    (endPoint - textStartPoint+ reduceTextGapPx).toFloat(),
                    textPaint
                )
            }

            i++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        mainPointClone = if (mainPoint <= midScreenPoint) -mainPoint else mainPoint
        mListener?.onViewUpdate((midScreenPoint + mainPointClone) / (pxmm * 10))

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                isMove = true
                downPointClone = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isMove) return true

                movablePoint = event.x
                val diff = movablePoint - downPointClone

                if (kotlin.math.abs(diff) > 1) {
                    mainPoint += diff
                    downPointClone = movablePoint

                    if (mainPoint > midScreenPoint) {
                        mainPoint = midScreenPoint.toFloat()
                        isMove = false
                    }
                    invalidate()
                }
            }
        }
        return true
    }

    fun initializeStartingPoint(point: Float) {
        userStartingPoint = point
        isSizeChanged = true

        if (isFirstTime) {
            isFirstTime = false
            mListener?.onViewUpdate(point)
        }
    }

    companion object {
        var screenSize = 500
        var pxmm = screenSize / 50f
    }
}
