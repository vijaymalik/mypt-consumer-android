package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.Slot


class HomeSmartSuggestionsSlotsAdapter(
    private val context: Context,
    val slots: List<Slot>
) : RecyclerView.Adapter<HomeSmartSuggestionsSlotsAdapter.HomeSmartSuggestionsViewHolder>() {

    inner class HomeSmartSuggestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeSmartSuggestionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_smart_suggesstions_slots_adapter_layout, parent, false)
        return HomeSmartSuggestionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeSmartSuggestionsViewHolder, position: Int) {
        val slot = slots[position]
        holder.tvTime.text = slot.time
    }

    override fun getItemCount(): Int = slots.size
}
