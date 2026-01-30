package co.com.mypt.fragments.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.BestPlanList.BestPlanData
import com.bumptech.glide.Glide

class CarouselAdapter (val data: List<BestPlanData?>):
    RecyclerView.Adapter<CarouselAdapter.CarouselVH>() {

//    private val items = List(10) { it }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carousel_item_motion, parent, false)
        return CarouselVH(view)
    }

    override fun onBindViewHolder(holder: CarouselVH, position: Int) {
        val item= data[position]
        holder.title.text=item?.title
        holder.perSession.text="AED ${item?.price_per_session}/session"
        holder.saveTxt.text=item?.badge_text
        holder.packagePrice.text="AED ${item?.price}"
        holder.sessionCount.text="${item?.sessions} sessions"
         Glide.with(holder.topBackground)
            .load(item?.background_image)
            .into(holder.topBackground)
        item?.features?.take(3)?.forEach {it->
            val textView = TextView(holder.perksText.context)
            textView.text = it
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

    override fun getItemCount() = data.size

    class CarouselVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var perksText = itemView.findViewById<LinearLayout>(R.id.perksText)
        var title = itemView.findViewById<TextView>(R.id.title)
        var sessionCount = itemView.findViewById<TextView>(R.id.sessionCount)
        var packagePrice = itemView.findViewById<TextView>(R.id.packagePrice)
        var perSession = itemView.findViewById<TextView>(R.id.perSession)
        var saveTxt = itemView.findViewById<TextView>(R.id.saveTxt)
        var topBackground = itemView.findViewById<ImageView>(R.id.topBackground)
    }
}

