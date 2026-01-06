package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.WorkoutDateListModel

class WorkoutLibraryDateAdapter(val context: Context?, val dateArrayList: ArrayList<WorkoutDateListModel>) :
    RecyclerView.Adapter<WorkoutLibraryDateAdapter.RowHolder>() {
    var selectedIndex = 1
    class RowHolder(view:View) : RecyclerView.ViewHolder(view) {
        val date : TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_library_date_list, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return dateArrayList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val data = dateArrayList[position]
        holder.date.text = data.name

        if(selectedIndex == position){
            holder.date.background = context!!.resources.getDrawable(R.drawable.category_border_bg)
            holder.date.setTextColor(context!!.resources.getColor(R.color.white))
        }
        else{
            holder.date.background = null
            holder.date.setTextColor(context!!.resources.getColor(R.color.headingcolor))
        }

        holder.date.tag = position
        holder.date.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            val data = dateArrayList[pos]
            var intent= Intent("sendDate")
            intent.putExtra("date",data.senddate)
            intent.putExtra("name",data.name)
            context.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

}
