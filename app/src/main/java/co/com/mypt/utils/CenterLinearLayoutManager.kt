package co.com.mypt.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CenterLinearLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {
    private var pendingScrollPosition = RecyclerView.NO_POSITION
    private var pendingScrollOffset = 0

    fun scrollToPositionWithOffsetAfterLayout(position: Int, offset: Int) {
        pendingScrollPosition = position
        pendingScrollOffset = offset
        requestLayout()
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        if (pendingScrollPosition != RecyclerView.NO_POSITION) {
            scrollToPositionWithOffset(pendingScrollPosition, pendingScrollOffset)
            pendingScrollPosition = RecyclerView.NO_POSITION
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return false // disables horizontal scrolling
    }
}
