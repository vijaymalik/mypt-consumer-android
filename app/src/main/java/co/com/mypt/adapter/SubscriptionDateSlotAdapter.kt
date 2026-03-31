package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import java.text.SimpleDateFormat
import java.util.Locale


class SubscriptionDateSlotAdapter(
    private val dates: List<String>,
    private val listener: (String, Int) -> Unit
) : RecyclerView.Adapter<SubscriptionDateSlotAdapter.DateViewHolder>() {
    private var selectedPosition = 0
    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.date)
        val ll: LinearLayout = itemView.findViewById(R.id.ll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_subscription_date_adapter_layout, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]

        val rawFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val displayFormat = SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH)
        val parsedDate = rawFormat.parse(date)
        val  displayText = parsedDate?.let { displayFormat.format(it).uppercase() } ?: date
        holder.tvDate.text = displayText
        if (position == selectedPosition) {
            holder.ll.setBackgroundResource(R.drawable.category_border_bg)
        } else {
            holder.ll.background = null
        }
        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            val newPosition = holder.bindingAdapterPosition
            if (newPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            selectedPosition = newPosition

            if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)

            listener(date, selectedPosition)
        }
    }

    override fun getItemCount(): Int = dates.size
}

