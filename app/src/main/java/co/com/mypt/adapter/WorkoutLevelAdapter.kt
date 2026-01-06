package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.WorkoutLevelModel
import java.util.ArrayList

class WorkoutLevelAdapter(
    var applicationContext: Context?,
    var workoutLvelArrayList: ArrayList<WorkoutLevelModel>
) : RecyclerView.Adapter<WorkoutLevelAdapter.WorkoutLevelHolder>() {
    class WorkoutLevelHolder (view: View):RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tv)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutLevelAdapter.WorkoutLevelHolder {
        var view = LayoutInflater.from(applicationContext).inflate(R.layout.workout_level_list, parent, false)
        return WorkoutLevelHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutLevelAdapter.WorkoutLevelHolder, position: Int) {
        var workoutModel=workoutLvelArrayList[position]
        holder.tvname.setText(workoutModel.name)

    }

    override fun getItemCount(): Int {
       return workoutLvelArrayList.size
    }

}
