package co.com.mypt.utils

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CarouselRecyclerview(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    private var carouselLayoutManagerBuilder: CarouselLayoutManager.Builder =
        CarouselLayoutManager.Builder()

    private var layoutManagerState: Parcelable? = null

    companion object {
        private const val SAVE_SUPER_STATE = "super-state"
        private const val SAVE_LAYOUT_MANAGER = "layout-manager-state"
    }

    init {
        layoutManager = carouselLayoutManagerBuilder.build()
        isChildrenDrawingOrderEnabled = true
    }

    fun set3DItem(is3DItem: Boolean) {
        carouselLayoutManagerBuilder.set3DItem(is3DItem)
        layoutManager = carouselLayoutManagerBuilder.build()
    }

    fun setInfinite(isInfinite: Boolean) {
        carouselLayoutManagerBuilder.setIsInfinite(isInfinite)
        layoutManager = carouselLayoutManagerBuilder.build()
    }
    fun setFlat(isFlat: Boolean) {
        carouselLayoutManagerBuilder.setIsFlat(isFlat)
        layoutManager = carouselLayoutManagerBuilder.build()
    }

    fun setAlpha(isAlpha: Boolean) {
        carouselLayoutManagerBuilder.setIsAlpha(isAlpha)
        layoutManager = carouselLayoutManagerBuilder.build()
    }

    fun setIntervalRatio(ratio: Float) {
        carouselLayoutManagerBuilder.setIntervalRatio(ratio)
        layoutManager = carouselLayoutManagerBuilder.build()
    }

    fun setIsScrollingEnabled(isScrollingEnabled: Boolean) {
        carouselLayoutManagerBuilder.setIsScrollingEnabled(isScrollingEnabled)
        layoutManager = carouselLayoutManagerBuilder.build()
    }

    fun setOrientation(@Orientation orientation: Int) {
        carouselLayoutManagerBuilder.setOrientation(orientation)
        layoutManager = carouselLayoutManagerBuilder.build()
    }

    fun getCarouselLayoutManager(): CarouselLayoutManager {
        return layoutManager as CarouselLayoutManager
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val center: Int = getCarouselLayoutManager().centerPosition()

        val actualPos: Int = getCarouselLayoutManager().getChildActualPos(i)
        var order: Int = i

        if (actualPos != Int.MIN_VALUE) {
            val dist = actualPos - center
            order = if (dist < 0) {
                i
            } else {
                childCount - 1 - dist
            }
        }

        if (order < 0) order = 0 else if (order > childCount - 1) order = childCount - 1

        return order
    }


    fun setItemSelectListener(listener: CarouselLayoutManager.OnSelected) {
        getCarouselLayoutManager().setOnSelectedListener(listener)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SAVE_SUPER_STATE, super.onSaveInstanceState())

        bundle.putParcelable(SAVE_LAYOUT_MANAGER, getCarouselLayoutManager().onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            layoutManagerState = state.getParcelable(SAVE_LAYOUT_MANAGER)
            super.onRestoreInstanceState(state.getParcelable(SAVE_SUPER_STATE))

        }else super.onRestoreInstanceState(state)

    }

    fun getSelectedPosition() = getCarouselLayoutManager().getSelectedPosition()

    private fun restorePosition() {
        if(layoutManagerState != null) {
            getCarouselLayoutManager().onRestoreInstanceState(layoutManagerState)
            layoutManagerState = null
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        restorePosition()
    }

    fun attachIndicator(indicator: CarouselIndicatorView, itemCount: Int) {
        indicator.setPageCount(itemCount)

        setItemSelectListener(object : CarouselLayoutManager.OnSelected{
            override fun onItemSelected(position: Int) {
                indicator.setSelectedPage(position % itemCount) // if infinite scrolling
            }

        })
    }

    fun attachSmoothIndicator(indicator: CarouselIndicatorView) {
        val adapter = adapter ?: return
        indicator.setPageCount(adapter.itemCount)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(this)

        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lm = layoutManager as? LinearLayoutManager ?: return
                val firstPos = lm.findFirstVisibleItemPosition()
                val firstView = lm.findViewByPosition(firstPos) ?: return

                val width = firstView.width
                val offset = -firstView.left / width.toFloat()  // 0f → 1f

                indicator.setProgress(firstPos, offset)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lm = layoutManager as? LinearLayoutManager ?: return
                    val snapView = snapHelper.findSnapView(lm) ?: return
                    val snapPos = lm.getPosition(snapView)
                    indicator.setSelectedPage(snapPos)
                }
            }
        })
    }
}
