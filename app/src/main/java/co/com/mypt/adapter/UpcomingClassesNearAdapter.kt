package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.UpComingClasses.Upcoming_classes_near_youActivity
import co.com.mypt.model.ExerciseModel
import com.bumptech.glide.Glide

class UpcomingClassesNearAdapter(var upcomingClassesNearYouactivity: Upcoming_classes_near_youActivity,var  exerciseList: ArrayList<ExerciseModel>) : RecyclerView.Adapter<UpcomingClassesNearAdapter.UpcomingHolder>() {
    var selectedIndex = 0

    class UpcomingHolder(view: View):RecyclerView.ViewHolder(view) {
        val ll : LinearLayout = view.findViewById(R.id.ll)
        val exerciseIcon : ImageView = view.findViewById(R.id.exerciseIcon)
        val exercise : TextView = view.findViewById(R.id.exercise)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingClassesNearAdapter.UpcomingHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_list_exercise_adapter_layout, parent, false)
        return UpcomingHolder(view)
    }

    override fun onBindViewHolder(
        holder: UpcomingClassesNearAdapter.UpcomingHolder,
        position: Int
    ) {
        var exerciseModel=exerciseList[position]

        holder.ll.tag = position

        if(selectedIndex == position){
            holder.ll.background = upcomingClassesNearYouactivity.resources.getDrawable(R.drawable.category_border_bg)
            holder.exercise.setTextColor(upcomingClassesNearYouactivity.resources.getColor(R.color.white))
        }else{
            holder.ll.background = null
            holder.exercise.setTextColor(upcomingClassesNearYouactivity.resources.getColor(R.color.headingcolor))
        }
        Glide.with(upcomingClassesNearYouactivity!!).load(exerciseModel.icon).fitCenter().error(R.drawable.dumbbell).placeholder(R.drawable.dumbbell).into(holder.exerciseIcon)
        holder.exercise.setText(exerciseModel.name)
        holder.ll.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            notifyDataSetChanged()
        }
        holder.ll.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            var exerciseModel=exerciseList.get(pos)
            var intent= Intent("tagclass")
            if (exerciseModel.name.equals("All Category")){
                intent.putExtra("filter","0")
            }else{
                intent.putExtra("filter","1")
            }
            intent.putExtra("tag_id",exerciseModel.id)
            upcomingClassesNearYouactivity.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
       return exerciseList.size
    }

}

