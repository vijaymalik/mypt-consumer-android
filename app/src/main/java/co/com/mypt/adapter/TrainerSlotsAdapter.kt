package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.Slot

class TrainerSlotsAdapter(val onItemSelected:(Slot?)-> Unit) :
    RecyclerView.Adapter<TrainerSlotsAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var slotList = mutableListOf<Slot>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateSlot: TextView = itemView.findViewById<TextView>(R.id.tvDateSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trainer_slot_bottomsheet_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return slotList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = slotList[position]
        val startTime = model.time.substringBefore(" - ")
        holder.tvDateSlot.text = startTime

        if (position == selectedPosition) {
            holder.tvDateSlot.setBackgroundResource(R.drawable.category_border_bg)
        } else {
            holder.tvDateSlot.setBackgroundResource(R.drawable.edittext_background)
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.absoluteAdapterPosition

            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = currentPosition

            if (previousPosition != -1) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)
            onItemSelected(slotList[selectedPosition])
        }
    }

    fun updateData(list: List<Slot>) {
        slotList.clear()
        slotList.addAll(list)
        selectedPosition = -1
        notifyDataSetChanged()
        onItemSelected(null)
    }

}