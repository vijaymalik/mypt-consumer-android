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
import co.com.mypt.model.AllWorkoutTypeModel
import co.com.mypt.model.WorkoutTypeModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class AllWorkoutTypeAdapter(
    var context: Context?,
    var workouttypeArraylist: ArrayList<WorkoutTypeModel>) : RecyclerView.Adapter<AllWorkoutTypeAdapter.WorkoutTypeHolder>() {
    var selectedIndex = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllWorkoutTypeAdapter.WorkoutTypeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allworkout_type, parent, false)
        return WorkoutTypeHolder(view)
    }

    class WorkoutTypeHolder(view:View):RecyclerView.ViewHolder(view) {
        var name=view.findViewById<TextView>(R.id.name)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
        var im=view.findViewById<ImageView>(R.id.im)
    }

    override fun onBindViewHolder(holder: AllWorkoutTypeAdapter.WorkoutTypeHolder, position: Int) {
        var allWorkoutTypeModel=workouttypeArraylist[position]
        holder.name.setText(allWorkoutTypeModel.name)
        if(selectedIndex == position){
            holder.linear.background = context!!.resources.getDrawable(R.drawable.category_border_bg)
        }
        else{
            holder.linear.background = null
        }
        Glide.with(context!!).load(allWorkoutTypeModel.image).fitCenter().error(R.drawable.dumbbell).placeholder(R.drawable.dumbbell).into(holder.im)

        holder.linear.tag = position
        holder.linear.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            var allWorkoutTypeModel=workouttypeArraylist.get(pos)
            var intent= Intent("tagExercise")
            intent.putExtra("tag_id",allWorkoutTypeModel.id)
            context!!.sendBroadcast(intent)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return workouttypeArraylist.size
    }

}
