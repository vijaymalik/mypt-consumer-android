package co.com.calculateheight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import co.com.mypt.R
import co.com.mypt.rulerHeight.CenterWaveScaleViewWeightHorizontal
import onViewUpdateListener


class   MyScaleView(mycontext: Context, attrs: AttributeSet?) :
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
    private var mListener: onViewUpdateListener? = null
//    lateinit var gradientPaint: Paint
    private var rulersize = 0f
    lateinit var rulerPaint: Paint
    lateinit var textPaint: Paint
//    lateinit var goldenPaint: Paint
    private var endPoint = 0
    var isSizeChanged: Boolean = false
    var userStartingPoint: Float = 0f
    private var scaleLineSmall = 0
    private var scaleLineMedium = 0
    private var scaleLineLarge = 0
    private var textStartPoint = 0
//    private var yellowLineStrokeWidth = 0
    var isFirstTime: Boolean = true
    private val WAVE_ITEM_COUNT = 7
    private val WAVE_HALF = WAVE_ITEM_COUNT / 2   // 3
    private var lastHapticTick = Int.MIN_VALUE
    init {
        if (!isInEditMode) {
            init(context)
        }
    }

    private fun init(context: Context) {

        rulersize = pxmm * 10
        rulerPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 0f
            isAntiAlias = false
            color = Color.WHITE
        }


        textPaint = TextPaint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 0f
            isAntiAlias = true
            textSize = resources.getDimension(R.dimen.txt_size)
            color = resources.getColor(R.color.rulartextcolor)
        }

//        gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        yellowLineStrokeWidth = resources.getDimension(R.dimen.yellow_line_stroke_width).toInt()
//        goldenPaint = Paint()
//        goldenPaint.style = Paint.Style.FILL_AND_STROKE
//        goldenPaint.color = context.resources.getColor(R.color.yellow)
//        goldenPaint.strokeWidth = yellowLineStrokeWidth.toFloat()
//        goldenPaint.strokeJoin = Paint.Join.ROUND
//        goldenPaint.strokeCap = Paint.Cap.ROUND
//        goldenPaint.setPathEffect(CornerPathEffect(10f))
//        goldenPaint.isAntiAlias = true
        scaleLineSmall = resources.getDimension(R.dimen.scale_line_small).toInt()
        scaleLineMedium = resources.getDimension(R.dimen.scale_line_medium).toInt()
        scaleLineLarge = resources.getDimension(R.dimen.scale_line_large_cm).toInt()
        textStartPoint = resources.getDimension(R.dimen.text_start_point).toInt()
    }

    fun setUpdateListener(onViewUpdateListener: onViewUpdateListener?) {
        mListener = onViewUpdateListener
    }

    public override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        viewWidth = w
        viewHeight = h
        screenSize = height
        pxmm = screenSize / 50f
        midScreenPoint = height / 2
        endPoint = width - 40
        if (isSizeChanged) {
            isSizeChanged = false
            mainPoint = midScreenPoint - (userStartingPoint * 10 * pxmm)
        }
      /*  gradientPaint!!.setShader(
            LinearGradient(
                0f, 0f, width.toFloat(), rulersize, resources.getColor(R.color.green),
                resources.getColor(R.color.transparent_white), Shader.TileMode.MIRROR
            )
        )*/

    }

    override fun onDraw(canvas: Canvas) {

        startingPoint = mainPoint
        val waveRangePx = pxmm * WAVE_HALF

        val visibleHeight = viewHeight + 20
        val fadeDistance = 200

        var i = 0
        while (true) {

            if (startingPoint > screenSize) break

            startingPoint += pxmm

            // 🔥 MUST be recalculated for every line
            val distanceFromCenter =
                kotlin.math.abs(startingPoint - midScreenPoint)
            if (distanceFromCenter < pxmm / 2f) {
                if (i != lastHapticTick) {
                    lastHapticTick = i

                    if (i % 10 == 0) {
                        performHapticFeedback(
                            HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                        )
                    }
                }
            }

            val waveRatio = if (distanceFromCenter < waveRangePx) {
                1f - (distanceFromCenter / waveRangePx)
            } else {
                0f
            }

            // Fade alpha
            val alpha = when {
                startingPoint < fadeDistance ->
                    ((startingPoint / fadeDistance) * 255).toInt()

                startingPoint > visibleHeight - fadeDistance ->
                    ((1 - ((startingPoint - (visibleHeight - fadeDistance)) / fadeDistance)) * 255).toInt()

                else -> 255
            }

            val baseSize = when {
                i % 10 == 0 -> scaleLineLarge
                i % 5 == 0 -> scaleLineMedium
                else -> scaleLineSmall
            }

            val waveExtra = (baseSize * 0.6f * waveRatio).toInt()
            val size = baseSize + waveExtra

            rulerPaint.strokeWidth = if (i % 10 == 0 || i % 5 == 0) {
                5f + (waveRatio * 2f)
            } else {
                2f + (waveRatio * 1.5f)
            }

            rulerPaint.color = when {
                i % 10 == 0 || i % 5 == 0 ->
                    Color.argb(alpha,
                        Color.red(resources.getColor(R.color.rulerlargeline)),
                        Color.green(resources.getColor(R.color.rulerlargeline)),
                        Color.blue(resources.getColor(R.color.rulerlargeline)))

                else ->
                    Color.argb(alpha,
                        Color.red(resources.getColor(R.color.rulersmallline)),
                        Color.green(resources.getColor(R.color.rulersmallline)),
                        Color.blue(resources.getColor(R.color.rulersmallline)))
            }

            canvas.drawLine(
                (endPoint - size).toFloat(),
                startingPoint,
                endPoint.toFloat(),
                startingPoint,
                rulerPaint
            )

            // TEXT
            if (i % 10 == 0) {

                val isCenterText = distanceFromCenter < pxmm

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

                canvas.drawText(
                    "${i / 10} cm",
                    (endPoint - textStartPoint).toFloat(),
                    startingPoint + 8,
                    textPaint
                )
            }

            i++
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        println("touch event fire")
        mainPointClone = mainPoint
        if (mainPoint <= midScreenPoint) {
            mainPointClone = -mainPoint
        }
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
                    /**
                     * if user first starts moving downward and then upwards then
                     * this method makes it to move upward
                     */
                    if (isUpward) {
                        downpoint = event.y
                        downPointClone = downpoint
                    }
                    isDown = true
                    isUpward = false
                    /**
                     * make this differnce of 1, otherwise it moves very fast and
                     * nothing shows clearly
                     */
                    if (downPointClone - movablePoint > 1) {
                        mainPoint += -(downPointClone - movablePoint)
                        downPointClone = movablePoint
                        invalidate()
                        updateValueToUi()
                    }

                } else {
// downwards
                    if (isMove) {
                        /**
                         * if user first starts moving upward and then downwards,
                         * then this method makes it to move upward
                         */
                        if (isDown) {
                            downpoint = event.y
                            downPointClone = downpoint
                        }
                        isDown = false
                        isUpward = true
                        if (movablePoint - downpoint > 1) {
                            mainPoint += (movablePoint - downPointClone)
                            downPointClone = movablePoint
                            if (mainPoint > midScreenPoint) {
                                mainPoint = midScreenPoint.toFloat()
                                isMove = false
                            }
                            invalidate()
                            updateValueToUi()
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> println("up")
            else -> {}
        }
        return true
    }
    fun updateValueToUi(){
        val rawValue = (midScreenPoint + mainPointClone) / (pxmm * 10)
        val snappedValue = kotlin.math.round(rawValue * 10f) / 10f
        mListener?.onViewUpdate(snappedValue)
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