package co.com.mypt.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.CreateWorkoutFlow.SuperSet.CreateWorkoutSupersetActivity
import co.com.mypt.CreateWorkoutFlow.SuperSet.EditExerciseActivity
import co.com.mypt.R
import co.com.mypt.model.SelectExcerciseModel
import com.bumptech.glide.Glide

class ChooseExcerciseforSuperSetAdapter(
    var excerxiseModelList: ArrayList<SelectExcerciseModel>,
    var activity: CreateWorkoutSupersetActivity,
    var selectedExercisemodelLists: ArrayList<SelectExcerciseModel>
):RecyclerView.Adapter<ChooseExcerciseforSuperSetAdapter.ChooseExSuperHolder>() {
    lateinit var alertBuilder:AlertDialog.Builder
    lateinit var alertDialog:AlertDialog

    var selectedModelList = ArrayList<SelectExcerciseModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseExSuperHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.superset_exercise_list, parent, false)
        return ChooseExSuperHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChooseExSuperHolder,
        position: Int
    ) {
        var chooseExcerciseModel=excerxiseModelList[position]
        holder.tv.setText(chooseExcerciseModel.category)
        holder.tvTittle.setText(chooseExcerciseModel.name)
        holder.tvReps.setText(chooseExcerciseModel.raps)
        holder.tvcal.setText(chooseExcerciseModel.calories)
        // holder.tvsecondActiviy.setText("Rest $bottominitialValue seconds")


        Glide.with(activity).load(chooseExcerciseModel.image).fitCenter().error(R.drawable.gymgirl).into(holder.im)
        for (i in 0 until selectedExercisemodelLists.size){
            if (selectedExercisemodelLists[i].id==chooseExcerciseModel.id){
                val isChecked =  true
                holder.checkbox.isChecked = isChecked
            }
        }
        holder.checkbox.text = "Select for Superset ${activity.superSetCount}"
        /*val isChecked =  selectedModelList.contains(chooseExcerciseModel)
        holder.checkbox.isChecked = isChecked*/
        holder.checkbox.setTag(position)
        holder.verticalDots.setTag(position)
        holder.verticalDots.setOnClickListener {
            var h=it.tag
            var selectExerciseModel=excerxiseModelList[h as Int]
            showCustomMenu(activity,selectExerciseModel,h,it)
        }
        holder.checkbox.setOnClickListener {
            var j=it.tag
            var selectExcerciseModel=excerxiseModelList[j as Int]
            val intent = Intent("createSupersetExercise")
            if (holder.checkbox.isChecked) {
                selectedModelList.add(selectExcerciseModel)
                intent.putParcelableArrayListExtra("exercise_list", selectedModelList)
                intent.putExtra("position", j.toString())
                intent.putExtra("type", "add")

            }else{
                selectedModelList.removeAll{ it -> it.id == selectExcerciseModel.id}
                intent.putParcelableArrayListExtra("exercise_list", selectedModelList)
                intent.putExtra("position", j.toString())
                intent.putExtra("type", "remove")
            }
            activity!!.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }
    init {
        selectedModelList = selectedExercisemodelLists
    }

    override fun getItemCount(): Int {
       return excerxiseModelList.size
    }

    class ChooseExSuperHolder(view: View): RecyclerView.ViewHolder(view) {
        var tv=view.findViewById<TextView>(R.id.tv)

        var tvTittle=view.findViewById<TextView>(R.id.tvTittle)
        var tvReps=view.findViewById<TextView>(R.id.tvReps)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var im=view.findViewById<ImageView>(R.id.im)
        var checkbox=view.findViewById<CheckBox>(R.id.check)
        var verticalDots=view.findViewById<ImageView>(R.id.verticalDots)

        var relative=view.findViewById<RelativeLayout>(R.id.relative)

    }
    private fun showCustomMenu(
        context1: Context,
        selectExerciseModel: SelectExcerciseModel,
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
            val intent = Intent(activity, EditExerciseActivity::class.java)
            intent.putExtra("item", selectExerciseModel)
            intent.putExtra("workoutType", "superset")
            (activity).editLauncher.launch(intent)
            popupWindow.dismiss()
        }
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_delete).setOnClickListener {
            popupWindow.dismiss()
            deleteAlertDialog(h,popupWindow)
          /*  var intent= Intent("deleteSelectedExercise")
            intent.putExtra("position",h)
            context1.sendBroadcast(intent)*/
        }
        // Show the popup anchored below the view
        popupWindow.showAsDropDown(view, 20, 10) // xOff, yOff
    }
    private fun deleteAlertDialog(h: Int, popupWindow: PopupWindow) {
        alertBuilder = AlertDialog.Builder(activity,R.style.MyAlertDialogTheme)
        val dialogView: View = LayoutInflater.from(activity).inflate(R.layout.delete_workout_alert, null)
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
            excerxiseModelList.removeAt(h)
            (activity).updateGridList()
            notifyDataSetChanged()
        }
        tv.setText("Delete Exercise")
        tvmsg.setText("You’re going to delete this exercise. Are \n you sure?")
        alertBuilder.setView(dialogView)
        alertDialog = alertBuilder.create()
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

}
