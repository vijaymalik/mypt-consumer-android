package co.com.mypt.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ExerciseAdapter.ViewHolder
import co.com.mypt.model.ExerciseModel
import java.util.ArrayList

class GymExerciseAdapter(var context: Context?, var exerciseList: ArrayList<ExerciseModel>) :
    RecyclerView.Adapter<GymExerciseAdapter.GymViewHolder>() {
    class GymViewHolder (view: View):RecyclerView.ViewHolder(view){
        val exercise = itemView.findViewById<TextView>(R.id.exercise)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GymExerciseAdapter.GymViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gym_exercise_layout, parent, false)
        return GymViewHolder(view)
    }

    override fun onBindViewHolder(holder: GymExerciseAdapter.GymViewHolder, position: Int) {
        var exerciseModel=exerciseList[position]
        if(position ==2) {
            holder.exercise.text = "+"+((exerciseList.size) - (position+1))
        }else{
            holder.exercise.text=exerciseModel.name
        }

    }


    override fun getItemCount(): Int {
        if (exerciseList.size>3){
            return 3
        }else{
            return exerciseList.size

        }
    }
}
