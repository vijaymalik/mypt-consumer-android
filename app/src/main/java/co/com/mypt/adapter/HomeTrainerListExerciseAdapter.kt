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

class HomeTrainerListExerciseAdapter(
    val activity: Context,
    val exerciseList: ArrayList<ExerciseModel>
) : RecyclerView.Adapter<HomeTrainerListExerciseAdapter.ViewHolder>() {
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
        Glide.with(activity!!).load(exerciseModel.icon).fitCenter().error(R.drawable.dumbbell).placeholder(R.drawable.dumbbell).into(holder.exerciseIcon)
        holder.exercise.setText(exerciseModel.name)
        holder.ll.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            var exerciseModel=exerciseList.get(pos)
            var intent= Intent("tag")
            if (exerciseModel.name.equals("All Workouts")){
                intent.putExtra("filter","0")
            }else{
                intent.putExtra("filter","1")
            }
            intent.putExtra("tag_id",exerciseModel.id)
            activity.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

}
