package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ExerciseModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class TrainerListExerciseAdapter(
    val activity: Context,
    val exerciseList: ArrayList<ExerciseModel>,
    private val onTagClick: ((String, String) -> Unit)? = null
) : RecyclerView.Adapter<TrainerListExerciseAdapter.ViewHolder>() {
    var selectedIndex = 0
    class ViewHolder(item:View) : RecyclerView.ViewHolder(item) {
        val ll : LinearLayout = item.findViewById(R.id.ll)
        val exerciseIcon : ImageView = item.findViewById(R.id.exerciseIcon)
        val exercise : TextView = item.findViewById(R.id.exercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_list_exercise_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var exerciseModel=exerciseList[position]
        holder.ll.tag = position
        if(selectedIndex == position){
            holder.ll.background = activity.resources.getDrawable(R.drawable.category_border_bg)
            holder.exercise.setTextColor(activity.resources.getColor(R.color.white))
        }else{
            holder.ll.background = null
            holder.exercise.setTextColor(activity.resources.getColor(R.color.headingcolor))
        }
        if (!exerciseModel.icon.isNullOrEmpty() && exerciseModel.icon!="null") {
            Glide.with(activity).load(exerciseModel.icon).fitCenter().error(R.drawable.dumbbell)
                .placeholder(R.drawable.dumbbell).into(holder.exerciseIcon)
        }else {
            holder.exerciseIcon.setImageResource(R.drawable.dumbbell)
        }
        holder.exercise.setText(exerciseModel.name)
        holder.ll.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            val exerciseModel=exerciseList.get(pos)
            val filter = if (exerciseModel.name == "All Workouts") {
                "0"
            } else {
                "1"
            }
            onTagClick?.invoke(exerciseModel.id, filter)
            notifyDataSetChanged()
        }
    }

}
