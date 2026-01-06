package co.com.curved

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class CurvedLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scroll = super.scrollHorizontallyBy(dx, recycler, state)
        updateChildren()
        return scroll
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        updateChildren()
    }

    private fun updateChildren() {
        val midPoint = width / 2f
        val maxRadius = width * 1.2f // Increase radius for more curvature
        val itemWidth = width / 5f // Ensure 5 items are visible

        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f
            val distanceFromCenter = abs(midPoint - childMid)

            // Adjust scale for a better 3D effect
            val scaleFactor = 1 - (distanceFromCenter / width * 0.5f) // More curvature effect
            val adjustedRadius = maxRadius - (scaleFactor * maxRadius * 0.5f) // Adjusted dynamic radius

            // Compute Y translation using circular arc formula
            val yOffset = sqrt((adjustedRadius * adjustedRadius) - (distanceFromCenter * distanceFromCenter)) - adjustedRadius

            // Apply transformations
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor
            child.translationY = -yOffset // Increase height adjustment

            // Adjust fading effect
            val alpha = if (distanceFromCenter < itemWidth / 2) 255 else (255 - (distanceFromCenter / width * 255)).toInt()
            child.alpha = max(0.3f, alpha / 255f)

            // Update text color dynamically
            val textView = child.findViewById<TextView>(android.R.id.text1)
            textView.textSize = if (distanceFromCenter < itemWidth / 2) 20f else 18f
            textView?.setTextColor(if (distanceFromCenter < itemWidth / 2) Color.parseColor("#FAFAFA") else Color.parseColor("#606060"))
        }
    }

}
