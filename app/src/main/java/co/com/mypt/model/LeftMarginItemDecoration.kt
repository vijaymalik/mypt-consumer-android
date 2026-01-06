package co.com.mypt.model

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LeftMarginItemDecoration(private val leftMargin: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = leftMargin
    }
}
