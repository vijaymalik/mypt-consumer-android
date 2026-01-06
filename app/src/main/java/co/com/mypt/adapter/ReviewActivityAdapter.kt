package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ActivityModel

class ReviewActivityAdapter(var context: Context, var activitiesModelList: ArrayList<ActivityModel>) :
    RecyclerView.Adapter<ReviewActivityAdapter.ReviewActivityAdapterHolder>() {
    class ReviewActivityAdapterHolder (view:View):RecyclerView.ViewHolder(view){
        var tvTime=view.findViewById<TextView>(R.id.tvTime)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewActivityAdapterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_activity_list, parent, false)
        return ReviewActivityAdapterHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReviewActivityAdapterHolder,
        position: Int
    ) {
        var selectTImeModel= activitiesModelList[position]
        if(position ==2){
            holder.tvTime.text = "+${activitiesModelList.size-2}"
        }
        else
            holder.tvTime.text = selectTImeModel.name

    }

    override fun getItemCount(): Int {
        if(activitiesModelList.size > 3)
            return  3
      return activitiesModelList.size
    }

}
