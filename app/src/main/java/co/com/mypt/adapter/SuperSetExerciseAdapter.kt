package co.com.mypt.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CreateWorkoutFlow.SuperSet.EditExerciseActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.SuperSetExerciseModel
import com.android.volley.VolleyError
import org.json.JSONObject

class SuperSetExerciseAdapter(
    var applicationContext: Context,
    var supersetExerciseList: ArrayList<SuperSetExerciseModel>,
    var workout_id: String,
    var setsPosition: String
): RecyclerView.Adapter<SuperSetExerciseAdapter.SuperSetExerciseHolder>() {
    lateinit var alertDialog:AlertDialog

    class SuperSetExerciseHolder (view: View): RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)
        var tvexercise_name=view.findViewById<TextView>(R.id.tvexercise_name)
        var tvreps=view.findViewById<TextView>(R.id.tvreps)
        var tvcalorie=view.findViewById<TextView>(R.id.tvcalorie)
        var verticalDots=view.findViewById<ImageView>(R.id.verticalDots)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuperSetExerciseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_exercise_list, parent, false)
        return SuperSetExerciseHolder(view)
    }

    override fun onBindViewHolder(holder: SuperSetExerciseHolder, position: Int) {
        var superSetModel=supersetExerciseList[position]
        holder.tv.text = superSetModel.category
        holder.tvexercise_name.text = superSetModel.name
        holder.tvreps.text = superSetModel.reps
        holder.tvcalorie.text = superSetModel.calories
        /*holder.verticalDots.tag = position
        holder.verticalDots.setOnClickListener {
            var h=it.tag
            var superSetModel=supersetExerciseList[h as Int]
            showCustomMenu(applicationContext,superSetModel,h,it)
        }*/
        holder.verticalDots.setOnClickListener { viewClicked -> // Renamed 'it' for clarity
            val currentPosition = holder.bindingAdapterPosition // Get the current position

            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < supersetExerciseList.size) {
                // Check if the position is valid and within the current list bounds
                val superSetModel = supersetExerciseList[currentPosition]
                showCustomMenu(applicationContext, superSetModel, currentPosition, viewClicked)
            } else {
                // Position is invalid or out of bounds. Handle this gracefully.
                // Maybe log an error, show a Toast, or do nothing.
                Log.e("SuperSetAdapter", "Invalid position ($currentPosition) or list empty when clicking verticalDots.")
                Toast.makeText(applicationContext, "Cannot perform action. Item may have been removed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showCustomMenu(
        context1: Context,
        superSetModel: SuperSetExerciseModel,
        h: Int,
        view: View
    ) {
        val popupView = LayoutInflater.from(context1).inflate(R.layout.edit_selected_supersetalert, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // focusable
        )
        // Optional: Add shadow / background
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 10f
        popupView.offsetLeftAndRight(10)

        // Handle menu item clicks
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_edit).setOnClickListener {
            val intent = Intent(context1, EditExerciseActivity::class.java)
            intent.putExtra("workoutType", "Createdsuperset")
            intent.putExtra("item",superSetModel)
            intent.putExtra("setposition",setsPosition)
            intent.putExtra("workout_id", workout_id)
            intent.putExtra("exercise_id", superSetModel.id)
            intent.putExtra("SaveData", "hitApi")
            applicationContext.startActivity(intent)
            popupWindow.dismiss()
        }
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_delete).setOnClickListener {
            popupWindow.dismiss()
            deleteAlertDialog(h,popupWindow,superSetModel)

        }
        // Show the popup anchored below the view
        popupWindow.showAsDropDown(view, 20, 10) // xOff, yOff
    }
    private fun deleteAlertDialog(
        h: Int,
        popupWindow: PopupWindow,
        superSetModel: SuperSetExerciseModel
    ) {
        var  alertBuilder = AlertDialog.Builder(applicationContext,R.style.MyAlertDialogTheme)
        val dialogView: View = LayoutInflater.from(applicationContext).inflate(R.layout.delete_workout_alert, null)
        var tv=dialogView.findViewById<TextView>(R.id.tv)
        var tvmsg=dialogView.findViewById<TextView>(R.id.tvmsg)
        var tvcancel=dialogView.findViewById<TextView>(R.id.tvcancel)
        var tvdelete=dialogView.findViewById<TextView>(R.id.tvdelete)
        tvcancel.setOnClickListener {
            alertDialog.dismiss()
            popupWindow.dismiss()
        }
        tvdelete.setOnClickListener {
            deleteExercise(superSetModel.workout_exercise_id)

        }
        tv.text = "Delete Exercise"
        tvmsg.text = "You’re going to delete this exercise. Are \n you sure?"
        alertBuilder.setView(dialogView)
        alertDialog = alertBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }
    override fun getItemCount(): Int {
        return supersetExerciseList.size
    }
    private fun deleteExercise(workoutExerciseId: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(applicationContext,"")
        progressDialog.show()
        var api = ApiURL.deleteworkoutexercise+workoutExerciseId
        Log.e("DeleteExercise",api)
        GetMethod(api, applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("deleSuperSetExercizResponse",""+data)
                try {
                    val jsonObj = JSONObject(data)
                    if(jsonObj.optBoolean("status")){
                        alertDialog.dismiss()
                        var intent= Intent("deleteSuperSetExercise")
                        applicationContext.sendBroadcast(intent)
                        Toast.makeText(applicationContext,jsonObj.optString("msg"), Toast.LENGTH_SHORT).show()
                    }else{
                        alertDialog.dismiss()
                        
                      //  Toast.makeText(applicationContext,jsonObj.optString("msg"), Toast.LENGTH_SHORT).show()
                        Toast.makeText(applicationContext,jsonObj.optJSONObject("errors").optString("msg"), Toast.LENGTH_SHORT).show()

                    }


                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error?.printStackTrace()
            }
        })
    }

}
