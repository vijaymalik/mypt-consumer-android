package co.com.mypt.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SliderLayoutManager(context: Context?) : LinearLayoutManager(context) {

    init {
        orientation = HORIZONTAL
    }

    var callback: OnItemSelectedListener? = null
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        recyclerView = view!!
        if (recyclerView.onFlingListener == null) {
            LinearSnapHelper().attachToRecyclerView(recyclerView)
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleDownView()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleDownView()
            return scrolled
        } else {
            return 0
        }
    }

    private fun scaleDownView() {
        val mid = width / 2.0f
        if (width == 0) return
        for (i in 0 until childCount) {

            // Calculating the distance of the child from the center
            val child = getChildAt(i)
            if (child != null){
                val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
                val distanceFromCenter = Math.abs(mid - childMid)

                // The scaling formula
                val scale = 1-Math.sqrt((distanceFromCenter/width).toDouble()).toFloat()*0.1f

                // Set scale to view
                child.scaleX = scale
                child.scaleY = scale

                val distanceFromLeftEdge = child.left
                val distanceFromRightEdge = recyclerView.width - child.right
                val alpha = when {
                    distanceFromLeftEdge < 100 -> (distanceFromLeftEdge.toFloat() / 100).coerceIn(0f, 1f)
                    distanceFromRightEdge < 100 -> (distanceFromRightEdge.toFloat() / 100).coerceIn(0f, 1f)
                    else -> 1f
                }
                child.alpha = alpha
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // When scroll stops we notify on the selected item
        if (state.equals(RecyclerView.SCROLL_STATE_IDLE)) {

            // Find the closest child to the recyclerView center --> this is the selected item.
            val recyclerViewCenterX = getRecyclerViewCenterX()
            var minDistance = recyclerView.width
            var position = -1
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                var newDistance = abs(childCenterX - recyclerViewCenterX)
                if (newDistance < minDistance) {
                    minDistance = newDistance
                    position = recyclerView.getChildLayoutPosition(child)
                }
            }

            // Notify on item selection
            callback?.onItemSelected(position)
        }
    }

    private fun getRecyclerViewCenterX() : Int {
        return (recyclerView.right - recyclerView.left)/2 + recyclerView.left
    }

    interface OnItemSelectedListener {
        fun onItemSelected(layoutPosition: Int)
    }
}