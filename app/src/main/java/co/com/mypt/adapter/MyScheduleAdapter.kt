package co.com.mypt.adapter

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CreateWorkoutFlow.SuperSet.CreateWorkoutSupersetActivity
import co.com.mypt.R
import co.com.mypt.activities.CalendarSchedulePersonalWorkoutActivity
import co.com.mypt.model.MyScheduleModel
import com.android.volley.VolleyError
import org.json.JSONObject

class MyScheduleAdapter(
    var activity: FragmentActivity?,
    var scheduleModelList: ArrayList<MyScheduleModel>,
    var type: String
) :
    RecyclerView.Adapter<MyScheduleAdapter.MyScheduleHolder>() {
    lateinit var alertBuilder:AlertDialog.Builder
    lateinit var alertDialog:AlertDialog
    class MyScheduleHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvExercises=view.findViewById<TextView>(R.id.tvExercises)
        var im_edit=view.findViewById<ImageView>(R.id.im_edit)
        var im_delete=view.findViewById<ImageView>(R.id.im_delete)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
        var tvduration=view.findViewById<TextView>(R.id.tvduration)
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvhours=view.findViewById<TextView>(R.id.tvhours)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScheduleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_list, parent, false)
        return MyScheduleHolder(view)
    }

    override fun getItemCount(): Int {
        return scheduleModelList.size
    }

    override fun onBindViewHolder(holder: MyScheduleHolder, position: Int) {
        var scheduleHolder=scheduleModelList[position]

        holder.tvname.setText(scheduleHolder.title)
        holder.tvExercises.setText(scheduleHolder.exercise_count+" Exercise")
        holder.tvduration.setText(scheduleHolder.time)
        holder.tvhours.setText(scheduleHolder.totalDuration+" min")
        holder.im_edit.setTag(position)
        holder.im_delete.setTag(position)
        holder.relative.setTag(position)
        holder.im_edit.setOnClickListener{
            val pos = it.tag as Int
            var scheduleHolder=scheduleModelList[pos]
            if (scheduleHolder.type.equals("superset")){
                var intent=Intent(activity, CreateWorkoutSupersetActivity::class.java)
                intent.putExtra("type",scheduleHolder.type)
                intent.putExtra("slotTime",scheduleHolder.time)
                intent.putExtra("slotStartDate",scheduleHolder.date)
                intent.putExtra("workout_id",scheduleHolder.id)
                intent.putExtra("screenType","calendar")
                activity!!.startActivity(intent)
            }else{
                var intent=Intent(activity,CalendarSchedulePersonalWorkoutActivity::class.java)
                intent.putExtra("type",scheduleHolder.type)
                intent.putExtra("slotTime",scheduleHolder.time)
                intent.putExtra("slotStartDate",scheduleHolder.date)
                intent.putExtra("workout_id",scheduleHolder.id)
                intent.putExtra("screenType","calendar")
                activity!!.startActivity(intent)
            }

        }
        holder.im_delete.setOnClickListener{
            val pos = it.tag as Int
            var scheduleHolder=scheduleModelList[pos]
            deleteAlertDialog(scheduleHolder.id)

        }
     /*   holder.relative.setOnClickListener{
            var intent= Intent(activity, WorkoutLibraryActivity::class.java)
            activity!!.startActivity(intent)
        }*/

    }
    private fun deleteWorkout(id: String) {
        val api = ApiURL.workoutdelete+id
        Log.e("workoutDeleteAPi",api)

        GetMethod(api,activity).startMethod(object : ResponseData {
            override fun response(data: String?) {
                Log.e("workoutDeleteResponse",""+data)
                try {
                    val jsonObj = JSONObject(data)
                    if(jsonObj.optBoolean("status")){
                        alertDialog.dismiss()
                        Toast.makeText(activity,jsonObj.optString("msg"), Toast.LENGTH_SHORT).show()
                        var intent= Intent("deleteWorkout")
                        activity!!.sendBroadcast(intent)
                    }else{
                        alertDialog.dismiss()

                        Toast.makeText(activity,jsonObj.optString("msg"), Toast.LENGTH_SHORT).show()

                    }


                } catch (e: Exception) {
                    alertDialog.dismiss()
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error?.printStackTrace()
            }
        })
    }

    private fun deleteAlertDialog(id: String) {
        alertBuilder = AlertDialog.Builder(activity,R.style.MyAlertDialogTheme)
        val dialogView: View = LayoutInflater.from(activity).inflate(R.layout.delete_workout_alert, null)
        alertBuilder.setView(dialogView)
        var tvcancel=dialogView.findViewById<TextView>(R.id.tvcancel)
        var tvdelete=dialogView.findViewById<TextView>(R.id.tvdelete)
        tvcancel.setOnClickListener {
            alertDialog.dismiss()
        }
        tvdelete.setOnClickListener {
            deleteWorkout(id)

        }

        alertDialog = alertBuilder.create()
        alertDialog.show()
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

}
