package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.TrainerStudiosResponse

class HomePageTrainerStudioDetailAdapter :
    RecyclerView.Adapter<HomePageTrainerStudioDetailAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var studioList = mutableListOf<TrainerStudiosResponse.Data.Studio>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivSelect: ImageView = itemView.findViewById<ImageView>(R.id.ivSelect)
        val tvGymName: TextView = itemView.findViewById<TextView>(R.id.tvGymName)
        val tvGymAddress: TextView = itemView.findViewById<TextView>(R.id.tvGymAddress)
        val tvGymDistance: TextView = itemView.findViewById<TextView>(R.id.tvGymDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_trainer_bottomsheet_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studioList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = studioList[position]
        holder.tvGymName.text = model.name
        holder.tvGymAddress.text = model.address
        holder.tvGymDistance.text = model.distance

        if (position == selectedPosition) {
            holder.ivSelect.setImageResource(R.drawable.radio_select)
        } else {
            holder.ivSelect.setImageResource(R.drawable.radio_unselect)
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
        }
    }

    fun updateData(list: List<TrainerStudiosResponse.Data.Studio>) {
        studioList.clear()
        studioList.addAll(list)
        selectedPosition = if (studioList.isNotEmpty()) 0 else -1
        notifyDataSetChanged()
    }

    fun getSelectedItem(): TrainerStudiosResponse.Data.Studio? {
        return if (selectedPosition != -1) studioList[selectedPosition] else null
    }
}