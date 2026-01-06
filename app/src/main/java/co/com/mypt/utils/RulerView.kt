package co.com.mypt.utils // Make sure this matches your package structure

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R // Make sure this R import is correct for your project
import kotlin.math.abs

class RulerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val layoutManager: LinearLayoutManager
    private val snapHelper: LinearSnapHelper
    private val adapter: NumberAdapter

    private val numbers = (1..60).toList() // Example range

    var onValueChangeListener: ((Int) -> Unit)? = null
    private var isProgrammaticScrollInProgress = false

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_ruler, this, true)
        recyclerView = view.findViewById(R.id.numberRecyclerView)

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        adapter = NumberAdapter(numbers)
        recyclerView.adapter = adapter

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.clipToPadding = false

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!isProgrammaticScrollInProgress && dx != 0) {
                    updateChildrenAppearance()
                }
            }

            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isProgrammaticScrollInProgress = false
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isProgrammaticScrollInProgress = false
                    updateChildrenAppearance()
                    notifyNewCenteredValue()
                } else {
                    updateChildrenAppearance()
                }
            }
        })

        recyclerView.post {
            updateChildrenAppearance()
            notifyNewCenteredValue()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val itemWidthPx = dpToPx(40)
        val sidePadding = (w / 2) - (itemWidthPx / 2)

        recyclerView.setPadding(
            sidePadding.coerceAtLeast(0), 0,
            sidePadding.coerceAtLeast(0), 0
        )

        recyclerView.post {
            updateChildrenAppearance()
            notifyNewCenteredValue()
        }
    }

    fun setInitialPosition(value: Int) {
        val position = numbers.indexOf(value).takeIf { it != -1 } ?: (0)
        if (position < 0 || position >= adapter.itemCount) return

        isProgrammaticScrollInProgress = true
        recyclerView.scrollToPosition(position)

        recyclerView.post {
            updateChildrenAppearance()
            notifyNewCenteredValue()
        }
    }

    private fun notifyNewCenteredValue() {
        val centerValue = getCurrentCenteredValue()
        centerValue?.let { onValueChangeListener?.invoke(it) }
    }

    fun getCurrentCenteredValue(): Int? {
        val snapView = snapHelper.findSnapView(layoutManager) ?: return null
        val position = layoutManager.getPosition(snapView)
        return if (position != RecyclerView.NO_POSITION && position < numbers.size) {
            numbers[position]
        } else {
            null
        }
    }

    private fun updateChildrenAppearance() {
        val recyclerViewCenterX = recyclerView.width / 2
        if (recyclerViewCenterX == 0 && recyclerView.width == 0) return // Not laid out yet

        val snappedView = snapHelper.findSnapView(layoutManager)
        val snappedItemPosition = if (snappedView != null) {
            layoutManager.getPosition(snappedView)
        } else {
            RecyclerView.NO_POSITION
        }

        for (i in 0 until recyclerView.childCount) {
            val childView = recyclerView.getChildAt(i) ?: continue
            val childItemPosition = layoutManager.getPosition(childView)
            val numberTextView = childView.findViewById<TextView>(R.id.numberText) ?: continue

            val isCentered = childItemPosition == snappedItemPosition

            // Text Size
            val targetTextSize = if (isCentered) 30f else 20f
            if (abs(numberTextView.textSize - targetTextSize * resources.displayMetrics.scaledDensity) > 0.5f) {
                numberTextView.textSize = targetTextSize
            }

            // Text Color
            val targetColor = if (isCentered) Color.WHITE else Color.LTGRAY
            if (numberTextView.currentTextColor != targetColor) {
                numberTextView.setTextColor(targetColor)
            }

            // Typeface
            val targetTypeface = if (isCentered) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            if (numberTextView.typeface != targetTypeface) {
                numberTextView.typeface = targetTypeface
            }

            // Alpha for Fading Edges
            val childViewCenter = (childView.left + childView.right) / 2
            val distanceFromRecyclerViewCenter = abs(childViewCenter - recyclerViewCenterX)

            // Start fading items that are further away from the center
            val fadeEffectMaxDistance = recyclerViewCenterX.toFloat() * 0.8f
            val fadeEffectMinDistance = recyclerViewCenterX.toFloat() * 0.3f

            val alphaValue = when {
                distanceFromRecyclerViewCenter < fadeEffectMinDistance -> 1f
                distanceFromRecyclerViewCenter > fadeEffectMaxDistance -> 0.2f
                else -> {
                    val fraction = (distanceFromRecyclerViewCenter - fadeEffectMinDistance) / (fadeEffectMaxDistance - fadeEffectMinDistance)
                    (1f - fraction * 0.5f).coerceIn(0.2f, 1f)
                }
            }
            childView.alpha = alphaValue
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    class NumberAdapter(private var numbers: List<Int>) : RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

        inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val numberText: TextView = itemView.findViewById(R.id.numberText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_number, parent, false)
            return NumberViewHolder(view)
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.numberText.text = numbers[position].toString()
            holder.numberText.textSize = 20f
            holder.numberText.setTextColor(Color.LTGRAY)
            holder.numberText.typeface = Typeface.DEFAULT
            holder.itemView.alpha = 1.0f
        }

        override fun getItemCount(): Int = numbers.size
    }
}
