package co.com.mypt.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.curvedBottomNavigation.dpToPx
import co.com.mypt.model.Trainer
import co.com.mypt.utils.HorizontalSpaceItemDecoration
import com.bumptech.glide.Glide


class HomeSmartSuggestionsAdapter(
    private val context: Context,
    private val trainers: List<Trainer>,
    private val listenerQuickBook: (Trainer) -> Unit,
    private val listenerFullSchedule: (Trainer) -> Unit
) : RecyclerView.Adapter<HomeSmartSuggestionsAdapter.HomeSmartSuggestionsViewHolder>() {

    inner class HomeSmartSuggestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgTrainer: ImageView = itemView.findViewById(R.id.imgTrainer)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val btnQuickBook: LinearLayout = itemView.findViewById(R.id.btnQuickBook)
        val btnFullSchedule: LinearLayout = itemView.findViewById(R.id.btnFullSchedule)
        val rvSlots: RecyclerView = itemView.findViewById(R.id.rvSlots)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSmartSuggestionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_smart_suggesstions_adapter_layout, parent, false)
        return HomeSmartSuggestionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeSmartSuggestionsViewHolder, position: Int) {
        val trainer = trainers[position]
        holder.itemView.layoutParams.width = getItemWidth()
        // Bind trainer info
        holder.tvName.text = trainer.name
        Glide.with(context).load(trainer.profile).fitCenter().into(holder.imgTrainer)
        holder.rvSlots.addItemDecoration(HorizontalSpaceItemDecoration(0, middleSpace = 12.dpToPx(context)))

        holder.rvSlots.adapter = HomeSmartSuggestionsSlotsAdapter(context,trainer.slots)
        holder.rvSlots.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        // Click listeners
        holder.btnQuickBook.setOnClickListener {
            listenerQuickBook(trainer)
        }
        holder.btnFullSchedule.setOnClickListener {
            listenerFullSchedule(trainer)
        }
    }

    override fun getItemCount(): Int = trainers.size

    private fun getItemWidth(): Int{
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        return (screenWidth * 0.85).toInt()
    }
}
