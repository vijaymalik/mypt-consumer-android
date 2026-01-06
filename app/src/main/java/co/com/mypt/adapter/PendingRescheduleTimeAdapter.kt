package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.interfaces.OnTimeSlotSelectedListener
import co.com.mypt.model.SelectTImeModel

class PendingRescheduleTimeAdapter(var activity: Context,
                                   var selectTimeModelList: ArrayList<SelectTImeModel>,
                                   private val listener: OnTimeSlotSelectedListener
) :
    RecyclerView.Adapter<PendingRescheduleTimeAdapter.RescheduleTimeHolder>() {
    private var selectedPosition: Int? = null

    class RescheduleTimeHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RescheduleTimeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selecttime_layout, parent, false)
        return RescheduleTimeHolder(view)
    }

    override fun onBindViewHolder(
        holder: RescheduleTimeHolder,
        position: Int
    ) {
        var selectTImeModel=selectTimeModelList.get(position)
        holder.tvTime.text = selectTImeModel.timeslot
        if (selectTImeModel.isBooked.equals("true")) {
            holder.tvTime.setBackgroundResource(R.drawable.grey_rectangle) // Selected color
        } else {
            holder.tvTime.setBackgroundResource(R.drawable.night_rectangle) // Default color
        }

        // Handle item clicks
        holder.itemView.setOnClickListener {

            if (selectTImeModel.isBooked.equals("true")){

            }else{
                // Update selected position
                val previousPosition = selectedPosition
                //selectedPosition = if (selectedPosition == position) null else position
                selectedPosition = holder.absoluteAdapterPosition


                if (selectedPosition == position) {
                    holder.tvTime.setBackgroundResource(R.drawable.slot_selection_rectangle) // Selected color
                } else {
                    holder.tvTime.setBackgroundResource(R.drawable.night_rectangle) // Default color
                }
                // Notify the changes for both positions
                listener.onTimeSlotSelected(selectTImeModel.id )

                previousPosition?.let { notifyItemChanged(it) }
                //  notifyItemChanged(position)

            }
        }
    }

    override fun getItemCount(): Int {
        return selectTimeModelList.size
    }

}