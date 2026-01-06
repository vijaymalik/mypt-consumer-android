package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ExerciseModel

class TrainerTagAdapter(
    val context: Context?,
    val exerciseArraylist: ArrayList<ExerciseModel>,
    val type: String
) :
    RecyclerView.Adapter<TrainerTagAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exercise = itemView.findViewById<TextView>(R.id.exercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_tags, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(type == "Linear"){
            if (exerciseArraylist.size>5)
                5
            else
                exerciseArraylist.size
        }else{
            if (exerciseArraylist.size>3)
                3
            else
                exerciseArraylist.size
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exerciseModel=exerciseArraylist[position]
        if(position ==2 && type == "grid") {
            holder.exercise.text = "+"+((exerciseArraylist.size) - (position+1))
        }else if(position ==4 && type == "Linear") {
            holder.exercise.text = "+"+((exerciseArraylist.size) - (position+1))
        }else{
            holder.exercise.text=exerciseModel.name
        }
    }

}