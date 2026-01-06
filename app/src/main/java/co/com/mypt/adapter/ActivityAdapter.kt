package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ActivityModel

class ActivityAdapter(var context: Context, var activitiesModelList: ArrayList<ActivityModel>) : RecyclerView.Adapter<ActivityAdapter.Activity_Holder>() {
    class Activity_Holder (view: View):RecyclerView.ViewHolder(view){
        var tvTime=view.findViewById<TextView>(R.id.exercise)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): Activity_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_tags, parent, false)
        return Activity_Holder(view)
    }

    override fun onBindViewHolder(holder: Activity_Holder, position: Int) {
        var selectTImeModel= activitiesModelList[position]

        if(position == 2)
            holder.tvTime.text = "+${activitiesModelList.size - 2}"
        else
            holder.tvTime.text = selectTImeModel.name
    }

    override fun getItemCount(): Int {
        if(activitiesModelList.size >3)
            return 3
        return activitiesModelList.size
    }

}
