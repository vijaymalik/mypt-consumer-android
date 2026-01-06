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
import co.com.mypt.WorkoutLibrary.ActiveessionWorkoutActivity
import co.com.mypt.model.FeaturedWorkoutModel
import com.bumptech.glide.Glide

class UpcomingSessionLibraryAdapter(
    var applicationContext: Context,
    var upcomingSessionArrayList: ArrayList<FeaturedWorkoutModel>,
    var workout_id: String,
    var session_id: String
): RecyclerView.Adapter<UpcomingSessionLibraryAdapter.SessionHolder>() {
    class SessionHolder (view: View): RecyclerView.ViewHolder(view){
        var im=view.findViewById<ImageView>(R.id.im)
        var tvType=view.findViewById<TextView>(R.id.tvType)
        var tvName=view.findViewById<TextView>(R.id.tvName)
        var totalWorkouts=view.findViewById<TextView>(R.id.totalWorkouts)
        var tvReps=view.findViewById<TextView>(R.id.tvReps)
        var tvCalorie=view.findViewById<TextView>(R.id.tvCalorie)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingSessionLibraryAdapter.SessionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_session_workout, parent, false)
        return SessionHolder(view)
    }

    override fun onBindViewHolder(
        holder: UpcomingSessionLibraryAdapter.SessionHolder,
        position: Int
    ) {
        var upcomingSessionModel=upcomingSessionArrayList[position]
       // holder.totalWorkouts.setText(upcomingSessionModel.totalWorkout)
        holder.tvType.setText(upcomingSessionModel.category_name)
        holder.tvReps.setText(upcomingSessionModel.reps)
        holder.tvCalorie.setText(upcomingSessionModel.calories)
        holder.tvName.setText(upcomingSessionModel.name)
        Glide.with(applicationContext!!).load(upcomingSessionModel.image).fitCenter().error(R.drawable.gymgirl).into(holder.im)
        holder.linear.setTag(position)
        holder.linear.setOnClickListener {
            var h=it.tag
            var upcomingSessionModel=upcomingSessionArrayList[h as Int]

            var intent1= Intent(applicationContext,ActiveessionWorkoutActivity::class.java)
            intent1.putExtra("wokout_id",workout_id)
            intent1.putExtra("session_id",session_id)
            intent1.putExtra("exercise_id",upcomingSessionModel.workout_exercise_id)
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent1)
        }

    }

    override fun getItemCount(): Int {
        return upcomingSessionArrayList.size
    }

}
