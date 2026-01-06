package co.com.mypt.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.CreateWorkoutFlow.SuperSet.EditExerciseActivity
import co.com.mypt.R
import co.com.mypt.activities.CalendarSchedulePersonalWorkoutActivity
import co.com.mypt.model.SelectExcerciseModel
import com.bumptech.glide.Glide

class ChooseExcerciseAdapter(
    var excerxiseModelList: ArrayList<SelectExcerciseModel>,
    var calendarSchedulePersonalWorkoutActivity: CalendarSchedulePersonalWorkoutActivity,
    var switchCompat: SwitchCompat,
    var Type: String?,
    var checked: Boolean
):RecyclerView.Adapter<ChooseExcerciseAdapter.ChooseExcerciseaHolder>() {
    lateinit var alertDialog:AlertDialog

    class ChooseExcerciseaHolder (view: View):RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)

        var tvTittle=view.findViewById<TextView>(R.id.tvTittle)
        var tvsecondActiviy=view.findViewById<TextView>(R.id.tvsecondActiviy)
        var tvReps=view.findViewById<TextView>(R.id.tvReps)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var im=view.findViewById<ImageView>(R.id.im)
        var imclose=view.findViewById<ImageView>(R.id.imclose)
        var imSecond=view.findViewById<ImageView>(R.id.imSecond)
        var linearBottomRest=view.findViewById<LinearLayout>(R.id.linearBottomRest)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
        var verticalDots=view.findViewById<ImageView>(R.id.verticalDots)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseExcerciseAdapter.ChooseExcerciseaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.excercise_list, parent, false)
        return ChooseExcerciseaHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseExcerciseAdapter.ChooseExcerciseaHolder, position: Int) {
       var chooseExcerciseModel=excerxiseModelList[position]
        holder.tv.setText(chooseExcerciseModel.category)
        holder.tvTittle.setText(chooseExcerciseModel.name)
        holder.tvReps.setText(chooseExcerciseModel.raps)
        holder.tvcal.setText(chooseExcerciseModel.calories)
       // holder.tvsecondActiviy.setText("Rest $bottominitialValue seconds")
        holder.tvsecondActiviy.setText("Rest ${chooseExcerciseModel.rest_duration} seconds")
        holder.verticalDots.setTag(position)
        holder.verticalDots.setOnClickListener {
            var h=it.tag
            var chooseExcerciseModel=excerxiseModelList[h as Int]
            showCustomMenu(calendarSchedulePersonalWorkoutActivity,chooseExcerciseModel,h,it)
        }
        Glide.with(calendarSchedulePersonalWorkoutActivity).load(chooseExcerciseModel.image).fitCenter().error(R.drawable.gymgirl).into(holder.im)
        if (chooseExcerciseModel.type=="exercise"){
            holder.linearBottomRest.visibility=View.GONE
            holder.relative.visibility=View.VISIBLE

        }else{
            holder.linearBottomRest.visibility=View.VISIBLE
            holder.relative.visibility=View.GONE

        }
        holder.imSecond.setTag(position)

        holder.imSecond.setOnClickListener {
            var j=it.tag
            var chooseExcerciseModel=excerxiseModelList[j as Int]

            showEditRestDialog(holder.tvsecondActiviy,chooseExcerciseModel)
        }
        holder.imclose.setOnClickListener {
            var intent= Intent("deleteRest")
            calendarSchedulePersonalWorkoutActivity.sendBroadcast(intent)



        }
    }

    override fun getItemCount(): Int {
       return excerxiseModelList.size
    }
    private fun showEditRestDialog(tvrest: TextView, chooseExcerciseModel: SelectExcerciseModel) {
        var selectedMinutes = 0
        var selectedSeconds = 0
        val dialog = Dialog(calendarSchedulePersonalWorkoutActivity)
        val bottomSheet = calendarSchedulePersonalWorkoutActivity.layoutInflater.inflate(R.layout.second_edit, null)
        dialog.setContentView(bottomSheet)
        var minutePicker=bottomSheet.findViewById<NumberPicker>(R.id.minutePicker)
        var secondPicker=bottomSheet.findViewById<NumberPicker>(R.id.secondPicker)
        var txt_ok=bottomSheet.findViewById<TextView>(R.id.txt_ok)
        var txt_close=bottomSheet.findViewById<TextView>(R.id.txt_close)
        txt_ok.setOnClickListener {

            val totalSeconds = selectedMinutes * 60 + selectedSeconds
            // list[position].rest_duration = totalSeconds.toString()
            if (totalSeconds==0){
                Toast.makeText(calendarSchedulePersonalWorkoutActivity,"Rest time must be greater than 0 seconds",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
           // tvrest.text = "Rest $totalSeconds seconds"
            chooseExcerciseModel.rest_duration=totalSeconds.toString()
            notifyDataSetChanged()
            dialog.dismiss()
        }

        txt_close.setOnClickListener {
            dialog.dismiss()
        }
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.setFormatter { i -> String.format("%02d", i) }
        minutePicker.wrapSelectorWheel = true

        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.setFormatter { i -> String.format("%02d", i) }
        secondPicker.wrapSelectorWheel = true

        selectedMinutes = minutePicker.value
        selectedSeconds = secondPicker.value


        minutePicker.setOnValueChangedListener { numberPicker, i, newValue ->
            selectedMinutes = newValue
        }

        secondPicker.setOnValueChangedListener{secondPicker, i, newValue ->
            selectedSeconds = newValue
        }
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_bg)



        dialog.create()
        dialog.show()
    }
    private fun showCustomMenu(
        context1: Context,
        chooseExcerciseModel: SelectExcerciseModel,
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

        // Handle menu item clicks
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_edit).setOnClickListener {
            val intent = Intent(context1, EditExerciseActivity::class.java)
            intent.putExtra("workoutType", Type)
            intent.putExtra("item",chooseExcerciseModel)
            (calendarSchedulePersonalWorkoutActivity).editUpdateValue.launch(intent)

            popupWindow.dismiss()
        }
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_delete).setOnClickListener {
            popupWindow.dismiss()
            deleteAlertDialog(h,popupWindow,chooseExcerciseModel)

        }
        // Show the popup anchored below the view
        popupWindow.showAsDropDown(view, 0, 10) // xOff, yOff
    }
    private fun deleteAlertDialog(
        h: Int,
        popupWindow: PopupWindow,
        chooseExcerciseModel: SelectExcerciseModel
    ) {
        var  alertBuilder = AlertDialog.Builder(calendarSchedulePersonalWorkoutActivity,R.style.MyAlertDialogTheme)
        val dialogView: View = LayoutInflater.from(calendarSchedulePersonalWorkoutActivity).inflate(R.layout.delete_workout_alert, null)
        var tv=dialogView.findViewById<TextView>(R.id.tv)
        var tvmsg=dialogView.findViewById<TextView>(R.id.tvmsg)
        var tvcancel=dialogView.findViewById<TextView>(R.id.tvcancel)
        var tvdelete=dialogView.findViewById<TextView>(R.id.tvdelete)
        tvcancel.setOnClickListener {
            alertDialog.dismiss()
            popupWindow.dismiss()
        }
        tvdelete.setOnClickListener {
            alertDialog.dismiss()
            (calendarSchedulePersonalWorkoutActivity).deleteExercise(h)
        }
        tv.setText("Delete Exercise")
        tvmsg.setText("You’re going to delete this exercise. Are \nyou sure?")
        alertBuilder.setView(dialogView)
        alertDialog = alertBuilder.create()
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }
}
