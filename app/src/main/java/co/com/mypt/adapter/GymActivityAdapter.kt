package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.GymActivityModel
import java.util.ArrayList

class GymActivityAdapter(
    var applicationContext: Context,
    var gymmactivitiesModelList: ArrayList<GymActivityModel>) : RecyclerView.Adapter<GymActivityAdapter.GymHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymActivityAdapter.GymHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_activity_list, parent, false)
        return GymHolder(view)
    }

    class GymHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.tvTime)

    }

    override fun onBindViewHolder(holder: GymActivityAdapter.GymHolder, position: Int) {
        var selectTImeModel= gymmactivitiesModelList[position]

        if(position == 2)
            holder.tvTime.text = "+${gymmactivitiesModelList.size - 2}"
        else
            holder.tvTime.text = selectTImeModel.name
    }

    override fun getItemCount(): Int {
        if (gymmactivitiesModelList.size>3){
            return 3
        }else{
            return gymmactivitiesModelList.size
        }
    }

}
