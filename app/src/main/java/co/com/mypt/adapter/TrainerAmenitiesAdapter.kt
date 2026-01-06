package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.ActivityModel

class TrainerAmenitiesAdapter(
    var applicationContext: Context,
   var activitiesModelList: ArrayList<ActivityModel>): RecyclerView.Adapter<TrainerAmenitiesAdapter.TrainerAmenitiesHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainerAmenitiesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.amenities_with_dot, parent, false)
        return TrainerAmenitiesHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrainerAmenitiesHolder,
        position: Int
    ) {
        var selectTImeModel= activitiesModelList[position]

        if(position == 2){
            holder.tvTime.text = "+${activitiesModelList.size - 2}"
            holder.im.visibility=View.GONE
        }
        else{
            holder.tvTime.text = selectTImeModel.name

        }
    }

    override fun getItemCount(): Int {
        if(activitiesModelList.size >3)
            return 3
        return activitiesModelList.size
    }

    class TrainerAmenitiesHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.exercise)
        var im=view.findViewById<ImageView>(R.id.im)

    }

}
