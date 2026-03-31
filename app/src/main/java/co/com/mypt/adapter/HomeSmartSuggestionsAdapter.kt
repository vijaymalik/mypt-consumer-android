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
import co.com.mypt.model.Trainer
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
        val tvTime1: TextView = itemView.findViewById(R.id.tvTime1)
        val tvTime2: TextView = itemView.findViewById(R.id.tvTime2)
        val btnQuickBook: LinearLayout = itemView.findViewById(R.id.btnQuickBook)
        val btnFullSchedule: LinearLayout = itemView.findViewById(R.id.btnFullSchedule)
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

        // Optimized slot handling: show up to 2 slots
        val slots = trainer.slots
        holder.tvTime1.text = slots.getOrNull(0)?.time ?: "--"
        holder.tvTime2.text = slots.getOrNull(1)?.time ?: "--"

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
