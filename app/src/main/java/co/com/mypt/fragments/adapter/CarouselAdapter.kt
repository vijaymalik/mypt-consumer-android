package co.com.mypt.fragments.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

class CarouselAdapter :
    RecyclerView.Adapter<CarouselAdapter.CarouselVH>() {

    private val items = List(10) { it }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carousel_item_motion, parent, false)
        return CarouselVH(view)
    }

    override fun onBindViewHolder(holder: CarouselVH, position: Int) {}

    override fun getItemCount() = items.size

    class CarouselVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}

