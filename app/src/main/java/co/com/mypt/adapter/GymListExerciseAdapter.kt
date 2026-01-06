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
import co.com.mypt.adapter.TrainerListExerciseAdapter.ViewHolder
import co.com.mypt.model.ExerciseModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class GymListExerciseAdapter(var context: Context, var exerciseList: ArrayList<ExerciseModel>) : RecyclerView.Adapter<GymListExerciseAdapter.GymListHolder>() {
    var selectedIndex = 0

    class GymListHolder (view: View):RecyclerView.ViewHolder(view){
        val ll : LinearLayout = view.findViewById(R.id.ll)
        val exerciseIcon : ImageView = view.findViewById(R.id.exerciseIcon)
        val exercise : TextView = view.findViewById(R.id.exercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_list_exercise_adapter_layout, parent, false)
        return GymListHolder(view)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: GymListHolder, position: Int) {
        var exerciseModel=exerciseList[position]

        holder.ll.tag = position

        if(selectedIndex == position){
            holder.ll.background = context.resources.getDrawable(R.drawable.category_border_bg)
            holder.exercise.setTextColor(context.resources.getColor(R.color.white))
        }else{
            holder.ll.background = null
            holder.exercise.setTextColor(context.resources.getColor(R.color.headingcolor))
        }
        Glide.with(context!!).load(exerciseModel.icon).fitCenter().error(R.drawable.dumbbell).placeholder(R.drawable.dumbbell).into(holder.exerciseIcon)
        holder.exercise.setText(exerciseModel.name)

        holder.ll.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            var exerciseModel=exerciseList.get(pos)
            var intent= Intent("gymtag")
            if (exerciseModel.name.equals("All Workouts")){
                intent.putExtra("filter","0")
            }else{
                intent.putExtra("filter","1")
            }
            intent.putExtra("tag_id",exerciseModel.id)
            context.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

}
