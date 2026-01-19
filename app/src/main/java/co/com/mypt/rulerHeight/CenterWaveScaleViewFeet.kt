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
import co.com.calculateheight.MyScaleView
import co.com.mypt.R

class CenterWaveScaleViewFeet(mycontext: Context, attrs: AttributeSet?) :
    View(mycontext, attrs) {

    var viewWidth: Int = 0
    var viewHeight: Int = 0
    var midScreenPoint: Int = 0
    var startingPoint: Float = 0f
    var downpoint: Float = 0f
    var movablePoint: Float = 0f
    var downPointClone: Float = 0f

    private var mainPoint = 0f
    private var mainPointClone = 0f

    var isDown: Boolean = false
    var isUpward: Boolean = false
    private var isMove = false

    private var mListener: onViewUpdateListenerFeet? = null

    lateinit var gradientPaint: Paint
    lateinit var rulerPaint: Paint
    lateinit var textPaint: Paint
    lateinit var goldenPaint: Paint

    private var rulersize = 0f
    private var endPoint = 0

    var isSizeChanged: Boolean = false
    var userStartingPoint: Float = 0f

    private var scaleLineSmall = 0
    private var scaleLineMedium = 0
    private var scaleLineLarge = 0
    private var textStartPoint = 0
    private var yellowLineStrokeWidth = 0

    // ===== WAVE CONFIG =====
    private val WAVE_ITEM_COUNT = 7
    private val WAVE_HALF = WAVE_ITEM_COUNT / 2   // 3
    private var lastHapticTick = Int.MIN_VALUE
    var isFirstTime: Boolean = true

    init {
        if (!isInEditMode) init(context)
    }

    private fun init(context: Context) {
        yellowLineStrokeWidth =
            resources.getDimension(R.dimen.yellow_line_stroke_width).toInt()

        gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        rulerPaint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        textPaint = TextPaint().apply {
            style = Paint.Style.FILL
            textSize = resources.getDimension(R.dimen.txt_size)
            color = resources.getColor(R.color.rulartextcolor)
        }

        goldenPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = resources.getColor(R.color.yellow)
            strokeWidth = yellowLineStrokeWidth.toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        scaleLineSmall = resources.getDimension(R.dimen.scale_line_small).toInt()
        scaleLineMedium = resources.getDimension(R.dimen.scale_line_medium).toInt()
        scaleLineLarge = resources.getDimension(R.dimen.scale_line_large).toInt()
        textStartPoint = resources.getDimension(R.dimen.text_start_point).toInt()
    }

    fun setUpdateListenerfeet(listener: onViewUpdateListenerFeet?) {
        mListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        viewWidth = w
        viewHeight = h

        screenSize = height
        pxmm = screenSize / 50f

        rulersize = pxmm * 12
        midScreenPoint = height / 2
        endPoint = width - 40

        if (isSizeChanged) {
            isSizeChanged = false
            mainPoint = midScreenPoint - (userStartingPoint * 12 * pxmm)
        }
    }

    override fun onDraw(canvas: Canvas) {
        /*canvas.drawRect(
            0f,
            midScreenPoint - (rulersize / 2),
            width.toFloat(),
            midScreenPoint + (rulersize / 2),
            gradientPaint
        )*/

        startingPoint = mainPoint
        var i = 0

        val visibleHeight = viewHeight + 20
        val fadeDistance = 200
        val waveRangePx = pxmm * WAVE_HALF   // EXACTLY 7 ITEMS

        while (true) {
            if (startingPoint > screenSize) break
            startingPoint += pxmm

            // ===== WAVE CALCULATION =====
            val distanceFromCenter =
                kotlin.math.abs(startingPoint - midScreenPoint)

            if (distanceFromCenter < pxmm / 2f) {
                if (i != lastHapticTick) {
                    lastHapticTick = i
                    if (i % 12 == 0) {
                        performHapticFeedback(
                            HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                        )
                    }
                }
            }
            val waveRatio = if (distanceFromCenter < waveRangePx) {
                1f - (distanceFromCenter / waveRangePx)
            } else 0f

            // ===== BASE SIZE =====
            val baseSize = when {
                i % 12 == 0 -> scaleLineLarge
                i % 6 == 0 -> scaleLineMedium
                else -> scaleLineSmall
            }

            // ===== APPLY WAVE =====
            val waveExtra = (baseSize * 0.6f * waveRatio).toInt()
            val finalSize = baseSize + waveExtra

            // ===== FADE =====
            val alpha = when {
                startingPoint < fadeDistance ->
                    ((startingPoint / fadeDistance) * 255).toInt()
                startingPoint > visibleHeight - fadeDistance ->
                    ((1 - ((startingPoint - (visibleHeight - fadeDistance)) / fadeDistance)) * 255).toInt()
                else -> 255
            }

            rulerPaint.color = when {
                i % 12 == 0 || i % 6 == 0 ->
                    Color.argb(alpha,
                        Color.red(resources.getColor(R.color.rulerlargeline)),
                        Color.green(resources.getColor(R.color.rulerlargeline)),
                        Color.blue(resources.getColor(R.color.rulerlargeline))
                    )
                else ->
                    Color.argb(alpha,
                        Color.red(resources.getColor(R.color.rulersmallline)),
                        Color.green(resources.getColor(R.color.rulersmallline)),
                        Color.blue(resources.getColor(R.color.rulersmallline))
                    )
            }

            rulerPaint.strokeWidth =
                (if (i % 12 == 0 || i % 6 == 0) 5f else 2f) + (waveRatio * 2f)

            // ===== DRAW LINE =====
            canvas.drawLine(
                (endPoint - finalSize).toFloat(),
                startingPoint,
                endPoint.toFloat(),
                startingPoint,
                rulerPaint
            )

            // ===== DRAW TEXT (ONLY 10th / 12th ITEM) =====
            if (i % 12 == 0) {
                val isCenterText = distanceFromCenter < pxmm / 2

                textPaint.color =if (isCenterText) {
                    // 🔥 CENTER TEXT COLOR
                    resources.getColor(R.color.center_text_weight)   // or any highlight color
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

                canvas.drawText(
                    "${i / 12} ft",
                    (endPoint - textStartPoint).toFloat(),
                    startingPoint + 8,
                    textPaint
                )
            }

            i++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mainPointClone = if (mainPoint <= midScreenPoint) -mainPoint else mainPoint

        mListener?.onViewUpdate((midScreenPoint + mainPointClone) / (pxmm * 12))

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isMove = true
                isDown = false
                isUpward = false
                downpoint = event.y
                downPointClone = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                movablePoint = event.y

                if (downPointClone > movablePoint) {
                    if (isUpward) {
                        downPointClone = movablePoint
                    }
                    isDown = true
                    isUpward = false

                    if (downPointClone - movablePoint > 1) {
                        mainPoint -= (downPointClone - movablePoint)
                        downPointClone = movablePoint
                        invalidate()
                    }
                } else {
                    if (isMove) {
                        if (isDown) {
                            downPointClone = movablePoint
                        }
                        isDown = false
                        isUpward = true

                        if (movablePoint - downPointClone > 1) {
                            mainPoint += (movablePoint - downPointClone)
                            downPointClone = movablePoint

                            if (mainPoint > midScreenPoint) {
                                mainPoint = midScreenPoint.toFloat()
                                isMove = false
                            }
                            invalidate()
                        }
                    }
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
        var screenSize: Int = 480
        private var pxmm = screenSize / 50f
    }
}
