package co.com.mypt.waterGlass

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class WaterScaleSliderLayoutManager(
    context: Context?,
    movementListener: MovementListener,
    var weightLabels: List<String>
) :
    LinearLayoutManager(context) {
    private var recyclerView: RecyclerView? = null
    private val movementListener: MovementListener

    init {
        setOrientation(HORIZONTAL)
        this.movementListener = movementListener
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        recyclerView = view
        recyclerView!!.onFlingListener = null;
        LinearSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        val position = calculatePosition()
        movementListener.onItemChanged(weightLabels[position])
        return scrolled
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            val position = calculatePosition()
            movementListener.onItemSelected(position)
        }
    }

    private fun calculatePosition(): Int {
        val recyclerViewCentreX = recyclerViewCentreX.toFloat()
        var minDistance = recyclerView!!.width.toFloat()
        var position = -1
        for (i in 0 until recyclerView!!.childCount) {
            val child = recyclerView!!.getChildAt(i)
            val childCentreX =
                getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)).toFloat() / 2
            val newDistance =
                abs((childCentreX - recyclerViewCentreX).toDouble()).toFloat()
            if (newDistance < minDistance) {
                minDistance = newDistance
                position = recyclerView!!.getChildLayoutPosition(child)
            }
        }
        return position
    }

    private val recyclerViewCentreX: Int
        private get() = ((recyclerView!!.right - recyclerView!!.left).toFloat() / 2 + recyclerView!!.left.toFloat()).toInt()

    interface MovementListener {
        fun onItemSelected(selectedIndex: Int)
        fun onItemChanged(selectedIndex: String)
    }
}