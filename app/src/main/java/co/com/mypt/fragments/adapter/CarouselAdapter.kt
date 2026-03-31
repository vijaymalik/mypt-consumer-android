package co.com.mypt.fragments.adapter

import android.content.Context
import android.graphics.Typeface
import android.media.Image
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.curvedBottomNavigation.dpToPx
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
        holder.tvValidity.text=item?.validity_text?:""
         Glide.with(holder.topBackground)
            .load(item?.background_image)
            .into(holder.topBackground)

        item?.features?.take(3)?.forEach { feature ->

            val context = holder.perksText.context
            val featureLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 0, 0, 12.dpToPx(context))
                this.layoutParams = layoutParams
                gravity = Gravity.CENTER_VERTICAL
            }

            val featureIcon = ImageView(context).apply {
                setImageResource(R.drawable.tick_white)
                val iconParams = LinearLayout.LayoutParams(
                    20.dpToPx(context),
                    20.dpToPx(context)
                )
                iconParams.setMargins(0, 0, 8.dpToPx(context), 0)
                layoutParams = iconParams
            }
           val textView = TextView(context).apply {
                text = feature
                textSize = 12f
               typeface = getFunnelSansRegularTypeface(context)
                setTextColor(context.getColor(R.color.location_txt))
            }

            featureLayout.addView(featureIcon)
            featureLayout.addView(textView)

            holder.perksText.addView(featureLayout)
        }
    }

    override fun getItemCount() = data.size

    class CarouselVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var perksText = itemView.findViewById<LinearLayout>(R.id.perksText)
        var title = itemView.findViewById<TextView>(R.id.title)
        var sessionCount = itemView.findViewById<TextView>(R.id.sessionCount)
        var tvValidity = itemView.findViewById<TextView>(R.id.tvValidity)
        var packagePrice = itemView.findViewById<TextView>(R.id.packagePrice)
        var perSession = itemView.findViewById<TextView>(R.id.perSession)
        var saveTxt = itemView.findViewById<TextView>(R.id.saveTxt)
        var topBackground = itemView.findViewById<ImageView>(R.id.topBackground)
    }

    companion object {
        private var funnelSansRegularTypeFace: Typeface? = null

        fun getFunnelSansRegularTypeface(context: Context): Typeface {
            if (funnelSansRegularTypeFace == null) {
                funnelSansRegularTypeFace = ResourcesCompat.getFont(context, R.font.funnel_sans_regular)
            }
            return funnelSansRegularTypeFace!!
        }
    }
}

