package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.SelectTimefromBookaTrainerCalendarActivity2
import co.com.mypt.adapter.SelectTimeAdapter.SelectTImeHolder
import co.com.mypt.model.SelectTImeModel
import java.util.ArrayList

class SelectTimeCalendarAdapter(var activity2: SelectTimefromBookaTrainerCalendarActivity2, var selectTimeModelList: ArrayList<SelectTImeModel>):RecyclerView.Adapter<SelectTimeCalendarAdapter.SelectHolder> (){
    private var selectedPosition: Int? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectTimeCalendarAdapter.SelectHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selecttime_layout, parent, false)
        return SelectHolder(view)
    }

    class SelectHolder (view: View):RecyclerView.ViewHolder(view){
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
    }

    override fun onBindViewHolder(holder: SelectTimeCalendarAdapter.SelectHolder, position: Int) {
        var selectTImeModel=selectTimeModelList.get(position)
        holder.tvTime.setText(selectTImeModel.timeslot)
        if (selectedPosition == position) {
            holder.tvTime.setBackgroundResource(R.drawable.grey_rectangle) // Selected color
        } else {
            holder.tvTime.setBackgroundResource(R.drawable.night_rectangle) // Default color
        }

        // Handle item clicks
        holder.itemView.setOnClickListener {
            // Update selected position
            val previousPosition = selectedPosition
            //selectedPosition = if (selectedPosition == position) null else position
            selectedPosition = holder.adapterPosition

            // Notify the changes for both positions
            previousPosition?.let { notifyItemChanged(it) }
            val intent = Intent("selecttime1")
            intent.putExtra("selectposition", "slot")
            activity2!!.sendBroadcast(intent)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return selectTimeModelList.size
    }

}
