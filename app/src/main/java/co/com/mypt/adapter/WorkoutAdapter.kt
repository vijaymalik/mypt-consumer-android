package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.WorkoutLibraryActivity
import co.com.mypt.model.AllWorkoutTypeModel
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator

class WorkoutAdapter(
    val context: Context?,
    val workoutArraylist: ArrayList<AllWorkoutTypeModel>,
    var datename: String,
    var screentype: String
) :
    RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val workoutImage : ImageView = itemView.findViewById(R.id.workoutImage)
        val progressLayout : LinearLayout = itemView.findViewById(R.id.progressLayout)
        val completedLayout : LinearLayout = itemView.findViewById(R.id.completedLayout)
        val mainLayout : LinearLayout = itemView.findViewById(R.id.mainLayout)
        val imComplete : ImageView = itemView.findViewById(R.id.imComplete)
        val exerciseName : TextView = itemView.findViewById(R.id.exerciseName)
        val exerciseType : TextView = itemView.findViewById(R.id.exerciseType)
        val exerciseTypeCompleted : TextView = itemView.findViewById(R.id.exerciseTypeCompleted)
        val exerciseNameCompleted : TextView = itemView.findViewById(R.id.exerciseNameCompleted)
        val exerciseStatus : TextView = itemView.findViewById(R.id.exerciseStatus)
        val completedPercentage : TextView = itemView.findViewById(R.id.completedPercentage)
        val completedScore : TextView = itemView.findViewById(R.id.completedScore)

        val p_Bar : LinearProgressIndicator = itemView.findViewById(R.id.p_Bar)
        val card : CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.myworkout_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workoutArraylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var workoutmodel=workoutArraylist[position]

        if (workoutmodel.isCompleted.equals(true)){
            holder.completedLayout.visibility=View.VISIBLE
            holder.imComplete.visibility=View.VISIBLE
            holder.progressLayout.visibility=View.GONE
            holder.completedScore.text = "+"+workoutmodel.pt_score+"Score"
            if (context != null) {
                holder.mainLayout.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.completed_gradient, context.theme)
            }

        }else{
            holder.progressLayout.visibility=View.VISIBLE
            holder.completedLayout.visibility=View.GONE
            holder.imComplete.visibility=View.GONE
            if (context != null) {
                holder.mainLayout.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.product_gradient, context.theme)
            }

        }
        holder.card.tag = position

        holder.card.setOnClickListener {
            var h=it.tag
            var workoutmodel=workoutArraylist[h as Int]
            if (datename.equals("Today") && workoutmodel.isCompleted.equals(false)){
                val intent= Intent(context, WorkoutLibraryActivity::class.java)
                intent.putExtra("wokout_id",workoutmodel.id)
                intent.putExtra("assign_id",workoutmodel.assigned_id)
                context?.startActivity(intent)
            }
        }

        Glide.with(context!!).load(workoutmodel.previewImage).fitCenter().error(R.drawable.guest_user).into(holder.workoutImage)
        holder.exerciseName.text = workoutmodel.title
        holder.exerciseNameCompleted.text = workoutmodel.title
        holder.exerciseType.text = workoutmodel.category
        holder.exerciseTypeCompleted.text = workoutmodel.category
        holder.exerciseStatus.text = workoutmodel.status
        holder.p_Bar.setProgress(workoutmodel.percentage.toInt())
        holder.completedPercentage.text = workoutmodel.percentage + "%"

    }

}
