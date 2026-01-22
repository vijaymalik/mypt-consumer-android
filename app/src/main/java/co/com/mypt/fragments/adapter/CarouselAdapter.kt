package co.com.mypt.fragments.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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

    override fun onBindViewHolder(holder: CarouselVH, position: Int) {
        for (i in 0..2) {
            val textView = TextView(holder.perksText.context)
            textView.text = "Includes priority trainer booking"
            textView.textSize = 12f
            textView.setTextColor(holder.perksText.context.getColor(R.color.grey_button))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 0, 0, 16)
            textView.setLayoutParams(params)
            holder.perksText.addView(textView)
        }

    }

    override fun getItemCount() = items.size

    class CarouselVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var perksText = itemView.findViewById<LinearLayout>(R.id.perksText)
    }
}

