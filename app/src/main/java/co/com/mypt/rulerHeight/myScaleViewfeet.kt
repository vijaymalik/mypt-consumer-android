package co.com.mypt.rulerHeight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import co.com.mypt.R


class myScaleViewfeet(mycontext: Context, attrs: AttributeSet?) :
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
    private var rulersize = 0f
    lateinit var rulerPaint: Paint
    lateinit var textPaint: Paint
    lateinit var goldenPaint: Paint
    private var endPoint = 0
    var isSizeChanged: Boolean = false
    var userStartingPoint: Float = 0f
    private var scaleLineSmall = 0
    private var scaleLineMedium = 0
    private var scaleLineLarge = 0
    private var textStartPoint = 0
    private var yellowLineStrokeWidth = 0

    private val WAVE_ITEM_COUNT = 7
    private val WAVE_HALF = WAVE_ITEM_COUNT / 2
    var isFirstTime: Boolean = true

    init {
        if (!isInEditMode) {
            init(context)
        }
    }

    private fun init(context: Context) {
        yellowLineStrokeWidth = resources.getDimension(R.dimen.yellow_line_stroke_width).toInt()
        gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rulersize = pxmm * 12
        val waveRangePx = pxmm * WAVE_HALF
        rulerPaint = Paint()
        rulerPaint!!.style = Paint.Style.STROKE
        rulerPaint!!.strokeWidth = 0f
        rulerPaint!!.isAntiAlias = false
        rulerPaint!!.color = Color.WHITE
        textPaint = TextPaint()

        textPaint.style = Paint.Style.FILL
        //textPaint.strokeWidth = 0f
        //textPaint.isAntiAlias = true
        textPaint.textSize = resources.getDimension(R.dimen.txt_size)
        textPaint.color = resources.getColor(R.color.rulartextcolor)

        goldenPaint = Paint()
        goldenPaint!!.style = Paint.Style.FILL_AND_STROKE
        goldenPaint!!.color = context.resources.getColor(R.color.yellow)
        goldenPaint!!.strokeWidth = yellowLineStrokeWidth.toFloat()
        goldenPaint!!.strokeJoin = Paint.Join.ROUND
        goldenPaint!!.strokeCap = Paint.Cap.ROUND
        goldenPaint!!.pathEffect = CornerPathEffect(12f)
        goldenPaint!!.isAntiAlias = true
        scaleLineSmall = resources.getDimension(R.dimen.scale_line_small).toInt()
        scaleLineMedium = resources.getDimension(R.dimen.scale_line_medium).toInt()
        scaleLineLarge = resources.getDimension(R.dimen.scale_line_large).toInt()
        textStartPoint = resources.getDimension(R.dimen.text_start_point).toInt()
    }

    fun setUpdateListenerfeet(onViewUpdateListener: onViewUpdateListenerFeet?) {
        mListener = onViewUpdateListener
    }

    public override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        viewWidth = w
        viewHeight = h
        screenSize = height
        pxmm = screenSize / 67f
        val waveRangePx = pxmm * WAVE_HALF
        midScreenPoint = height / 2
        endPoint = width - 40
        if (isSizeChanged) {
            isSizeChanged = false
            mainPoint = midScreenPoint - (userStartingPoint * 12 * pxmm)
        }
          /*gradientPaint!!.setShader(
              LinearGradient(
                  0f, 0f, width.toFloat(), rulersize, resources.getColor(R.color.rulerlargeline),
                  resources.getColor(R.color.yellow), Shader.TileMode.MIRROR
              )
          )*/

    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawRect(
            0f, midScreenPoint - (rulersize / 2), width.toFloat(), midScreenPoint + (rulersize / 2),
            gradientPaint!!
        )
        startingPoint = mainPoint
        var i = 0
        val visibleHeight = viewHeight+20 // Total height of the view
        val fadeDistance = 200

        while (true) {
            if (startingPoint > screenSize) {
                break
            }
            startingPoint += pxmm

            val size = when {
                i % 12 == 0 -> scaleLineLarge
                i % 6 == 0 -> scaleLineMedium
                else -> scaleLineSmall
            }

            val alpha: Int
            if (startingPoint < fadeDistance) { // Fade out top markers
                alpha = ((startingPoint / fadeDistance) * 255).toInt() // Gradually decrease opacity as it approaches top
            } else if (startingPoint > visibleHeight - fadeDistance) { // Fade out bottom markers
                alpha = ((1 - ((startingPoint - (visibleHeight - fadeDistance)) / fadeDistance)) * 255).toInt() // Gradually decrease opacity as it approaches bottom
            } else {
                alpha = 255 // Full opacity for middle markers
            }

            rulerPaint.color = when {
                i % 12 == 0 -> Color.argb(alpha, Color.red(resources.getColor(R.color.rulerlargeline)), Color.green(resources.getColor(R.color.rulerlargeline)), Color.blue(resources.getColor(R.color.rulerlargeline)))
                i % 6 == 0 -> Color.argb(alpha, Color.red(resources.getColor(R.color.rulerlargeline)), Color.green(resources.getColor(R.color.rulerlargeline)), Color.blue(resources.getColor(R.color.rulerlargeline)))
                else -> Color.argb(alpha, Color.red(resources.getColor(R.color.rulersmallline)), Color.green(resources.getColor(R.color.rulersmallline)), Color.blue(resources.getColor(R.color.rulersmallline)))
            }

            rulerPaint.strokeWidth = when {
                i % 12 == 0 || i % 6 == 0 -> 5f
                else -> 0f
            }

            canvas.drawLine((endPoint - size).toFloat(), startingPoint, endPoint.toFloat(), startingPoint, rulerPaint!!)
            if (i % 12 == 0) {
                textPaint.color = Color.argb(alpha, Color.red(resources.getColor(R.color.rulartextcolor)),
                    Color.green(resources.getColor(R.color.rulartextcolor)),
                    Color.blue(resources.getColor(R.color.rulartextcolor)))
                canvas.drawText(
                    (i / 12).toString() + " ft",
                    (endPoint - textStartPoint).toFloat(),
                    startingPoint + 8,
                    textPaint!!
                )
            }
            ++i
        }
        /*  canvas.drawLine(
              0f, midScreenPoint.toFloat(), (width - 20).toFloat(), midScreenPoint.toFloat(),
              goldenPaint!!
          )*/
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        println("touch event fire")
        mainPointClone = mainPoint
        if (mainPoint <= midScreenPoint) {
            mainPointClone = -mainPoint
        }
        val clickPoint = ((midScreenPoint + mainPointClone) / (pxmm * 12))
        if (mListener != null) {
            mListener!!.onViewUpdate((midScreenPoint + mainPointClone) / (pxmm * 12))
        }
        println("click point$clickPoint")
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
                                mainPoint = midScreenPoint.toFloat() // Keep it centered around midScreen point.
                                isMove = false
                            }
                            /*if (mainPoint > 0) {
                                mainPoint = 0f
                                isMove = false
                            }
                            */
                            invalidate()
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> println("up")
            else -> {}
        }
        return true
    }

    fun initializeStartingPoint(point: Float) {
        userStartingPoint = point
        isSizeChanged = true
        if (isFirstTime) {
            isFirstTime = false
            if (mListener != null) {
                mListener!!.onViewUpdate(point)
            }
        }
    }

    companion object {
        var screenSize: Int = 480
        private var pxmm = screenSize / 67f
    }
}