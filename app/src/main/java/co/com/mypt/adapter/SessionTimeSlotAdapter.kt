package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

import co.com.mypt.adapter.SelectTimeAdapter.SelectTImeHolder
import co.com.mypt.model.SelectTImeModel

class SessionTimeSlotAdapter(var context: Context, var selectTimeModelList: ArrayList<SelectTImeModel>) : RecyclerView.Adapter<SessionTimeSlotAdapter.SelectViewHolder>() {
    private var selectedPosition = -1

    class SelectViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionTimeSlotAdapter.SelectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selecttime_layout, parent, false)
        return SelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionTimeSlotAdapter.SelectViewHolder, position: Int) {
        var selectTImeModel=selectTimeModelList.get(position)
        holder.tvTime.setText(selectTImeModel.timeslot)

        if (selectTImeModel.isBooked.equals("true")){
            holder.tvTime.setBackgroundResource(R.drawable.grey_rectangle) // Selected color
        }else{
            holder.tvTime.setBackgroundResource(R.drawable.night_rectangle) // Default color
            /*  if (selectedPosition == position) {
                  holder.tvTime.setBackgroundResource(R.drawable.slot_selection_rectangle) // Selected color
              } else {
                  holder.tvTime.setBackgroundResource(R.drawable.night_rectangle) // Default color
              }*/
        }



        holder.itemView.setOnClickListener {
            if (selectTImeModel.isBooked.equals("true")){

            }else {
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

                val intent = Intent("sessionselect_time")
                intent.putExtra("selectposition", "slot")
                intent.putExtra("slot_id", selectTImeModel.id)
                context.sendBroadcast(intent)
                previousPosition?.let { notifyItemChanged(it) }
                //  notifyItemChanged(position)

            }
        }

        }

    override fun getItemCount(): Int {
        return selectTimeModelList.size
    }

}
