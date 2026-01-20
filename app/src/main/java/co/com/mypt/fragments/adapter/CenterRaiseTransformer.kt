package co.com.mypt.fragments.adapter

import android.view.View
import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer

class CenterRaiseTransformer : DiscreteScrollItemTransformer {

    override fun transformItem(
        item: View,
        position: Float
    ) {
        val absPos = kotlin.math.abs(position)

        // Raise center item by 40dp
        val translationY = -40f * (1f - absPos)
        item.translationY = translationY

        // Optional scale polish
        val scale = 0.9f + (1f - absPos) * 0.1f
        item.scaleX = scale
        item.scaleY = scale
    }
}

