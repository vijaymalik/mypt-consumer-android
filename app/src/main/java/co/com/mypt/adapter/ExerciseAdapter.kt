package co.com.mypt.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.model.ExerciseModel
import co.com.mypt.R
import java.util.ArrayList

class ExerciseAdapter(val context: Context?, val exerciseArraylist: ArrayList<ExerciseModel>) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exercise = itemView.findViewById<TextView>(R.id.exercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_tags_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.e("exerciselist",""+exerciseArraylist.size)
        return exerciseArraylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var exerciseModel=exerciseArraylist[position]
        if(position ==4) {
            holder.exercise.text = "+"+((exerciseArraylist.size) - (position+1))
        }else{
            holder.exercise.text=exerciseModel.name
        }
        Log.e("exercisename",exerciseModel.name)
    }

}
