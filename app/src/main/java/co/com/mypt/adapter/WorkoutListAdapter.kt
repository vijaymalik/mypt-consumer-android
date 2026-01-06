package co.com.mypt.adapter

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.ActiveessionWorkoutActivity
import co.com.mypt.WorkoutLibrary.WorkoutLibraryActivity
import co.com.mypt.model.WorkoutListModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class WorkoutListAdapter(
    var workoutList: ArrayList<WorkoutListModel>,
    var activity: WorkoutLibraryActivity,
    var workout_id: String?,
    var setType: String,
    var assign_id: String
) : RecyclerView.Adapter<WorkoutListAdapter.WorkHolder>() {
    class WorkHolder(view: View):RecyclerView.ViewHolder(view){
        var tvName=view.findViewById<TextView>(R.id.tvName)
        var im=view.findViewById<ImageView>(R.id.im)
        var tvReps=view.findViewById<TextView>(R.id.tvReps)
        var tvSets=view.findViewById<TextView>(R.id.tvSets)
        var calorie=view.findViewById<TextView>(R.id.calorie)
        var linearEdit=view.findViewById<LinearLayout>(R.id.linearEdit)
        var linear=view.findViewById<LinearLayout>(R.id.linear)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutListAdapter.WorkHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_list, parent, false)
        return WorkHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutListAdapter.WorkHolder, position: Int) {
       var workoutListModel=workoutList[position]
        holder.tvName.setText(workoutListModel.name)
        holder.tvReps.setText(workoutListModel.reps)
        holder.tvSets.setText(workoutListModel.sets)
        holder.calorie.setText(workoutListModel.calories)
        Glide.with(activity).load(workoutListModel.image).fitCenter().into(holder.im)
        holder.linearEdit.setTag(position)
        holder.linear.setTag(position)
        if (workoutListModel.isComplete.equals("true") || setType.equals("superset") || setType.equals("circuit")){
            holder.linearEdit.visibility=View.GONE
        }else{
            holder.linearEdit.visibility=View.VISIBLE
            holder.linear.setOnClickListener {
                var j=it.tag
                var workoutListModel=workoutList[j as Int]

                startWorkout(workoutListModel.id)

            }
        }


    }

    override fun getItemCount(): Int {
       return workoutList.size
    }
    private fun startWorkout(id: String) {

        val param: MutableMap<String, String> = HashMap()
        param["workout_id"] = ""+workout_id
        param["assignment_id"] = ""+assign_id
        Log.e("workoutStartParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(activity,"")
        progressDialog.show()

        PostMethod(ApiURL.workoutstart,param, activity).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("WorkoutStartRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        val session_id = resp.optJSONObject("data").optString("session_id")
                        val status = resp.optJSONObject("data").optString("status")
                        var intent1= Intent(activity,ActiveessionWorkoutActivity::class.java)
                        intent1.putExtra("wokout_id",workout_id)
                        intent1.putExtra("session_id",session_id)
                        intent1.putExtra("exercise_id",id)
                        activity.startActivity(intent1)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }

}
