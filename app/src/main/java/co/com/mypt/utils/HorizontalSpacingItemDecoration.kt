package co.com.mypt.utils

import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: android.graphics.Rect,
        view: android.view.View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        outRect.top = 0
        outRect.bottom = 0

        // Add start margin only for the first item
        outRect.left = if (position == 0) space else 0

        // Add end margin for all items
        outRect.right = space
    }
}