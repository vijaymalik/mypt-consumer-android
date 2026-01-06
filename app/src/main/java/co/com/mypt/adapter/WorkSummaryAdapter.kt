package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.WorkoutListModel

class WorkSummaryAdapter(var workoutList: ArrayList<WorkoutListModel>, var context: Context) : RecyclerView.Adapter<WorkSummaryAdapter.WorkHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_summary_list, parent, false)
        return WorkHolder(view)
    }

    class WorkHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvReps=view.findViewById<TextView>(R.id.tvReps)
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var imComplete=view.findViewById<ImageView>(R.id.imComplete)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)

    }

    override fun onBindViewHolder(holder: WorkHolder, position: Int) {
        val workoutListModel=workoutList[position]
        holder.tvname.setText(workoutListModel.exercise_name)
        holder.tvReps.setText(workoutListModel.reps)
        holder.tvcal.setText(workoutListModel.calories)
        /*if (position % 2 ==0 ){
            holder.relative.setBackgroundResource(R.drawable.pending_exercise_drawable)
            holder.imComplete.setImageResource(R.drawable.pending)
        }else{
            holder.relative.setBackgroundResource(R.drawable.workout_list_drawable)
            holder.imComplete.setImageResource(R.drawable.green_circle)
        }*/

    }

    override fun getItemCount(): Int {
       return workoutList.size
    }

}
