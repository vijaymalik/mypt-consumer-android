package co.com.mypt.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(
    private val space: Int,
    private val startSpace: Int? = null,
    private val middleSpace: Int? = null,
    private val endSpace: Int? = null
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Defaults (fallbacks)
        val start = startSpace ?: space
        val end = endSpace ?: space
        val middle = middleSpace ?: space

        when {
            itemCount == 1 -> {
                // Single item → full start & end
                outRect.left = start
                outRect.right = end
            }

            position == 0 -> {
                outRect.left = start
                outRect.right = middle / 2
            }

            position == itemCount - 1 -> {
                outRect.left = middle / 2
                outRect.right = end
            }

            else -> {
                outRect.left = middle / 2
                outRect.right = middle / 2
            }
        }
    }
}