package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.CreateWorkoutFlow.SuperSet.CreateWorkoutSupersetActivity
import co.com.mypt.CreateWorkoutFlow.SuperSet.EditExerciseActivity
import co.com.mypt.R
import co.com.mypt.model.SelectExcerciseModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class SupersetGridAdapter(
    var context: CreateWorkoutSupersetActivity,
    var selectedExercisemodelLists: ArrayList<SelectExcerciseModel>,
    var createSupersetBottomSheetDialog: BottomSheetDialog
): RecyclerView.Adapter<SupersetGridAdapter.SupersetHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SupersetGridAdapter.SupersetHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selectedsuperset_list, parent, false)
        return SupersetHolder(view)
    }

    class SupersetHolder(view: View): RecyclerView.ViewHolder(view) {
        var txt_exercise_category=view.findViewById<TextView>(R.id.txt_exercise_category)
        var txt_exercise_name=view.findViewById<TextView>(R.id.txt_exercise_name)
        var txt_reps=view.findViewById<TextView>(R.id.txt_reps)
        var img_menu=view.findViewById<ImageView>(R.id.img_menu)
    }

    override fun onBindViewHolder(
        holder: SupersetGridAdapter.SupersetHolder,
        position: Int
    ) {
        var selectExerciseModel=selectedExercisemodelLists[position]
        holder.txt_exercise_category.setText(selectExerciseModel.category)
        holder.txt_exercise_name.setText(selectExerciseModel.name)
        holder.txt_reps.setText(selectExerciseModel.raps)
        holder.img_menu.setTag(position)
        holder.img_menu.setOnClickListener {
            var h=it.tag
            var selectExerciseModel=selectedExercisemodelLists[h as Int]
            showCustomMenu(context,selectExerciseModel,h,it)

        }
    }

  /*  private fun createEditDelete(
        context1: Context,
        selectExerciseModel: SelectExcerciseModel,
        h: Int
    ) {
        val dialog = Dialog(context1)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.edit_selected_supersetalert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val linear_edit = dialog.findViewById<LinearLayoutCompat>(R.id.linear_edit)
        val linear_delete = dialog.findViewById<LinearLayoutCompat>(R.id.linear_delete)
        linear_delete.setOnClickListener {
            var intent= Intent("delete")
            intent.putExtra("position",h)
            context1.sendBroadcast(intent)
            dialog.dismiss()

        }

        dialog.show()

    }*/
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
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 10f

        // Handle menu item clicks
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_edit).setOnClickListener {
            val intent = Intent(context, EditExerciseActivity::class.java)
            intent.putExtra("item", selectExerciseModel)
            intent.putExtra("workoutType", "superset")
            (context).editLauncher.launch(intent)
            createSupersetBottomSheetDialog.dismiss()
            popupWindow.dismiss()
        }
        popupView.findViewById<LinearLayoutCompat>(R.id.linear_delete).setOnClickListener {
            var intent= Intent("delete")
            intent.putExtra("position",h)
            context1.sendBroadcast(intent)
            popupWindow.dismiss()
        }
        // Show the popup anchored below the view
        popupWindow.showAsDropDown(view, -200, 10) // xOff, yOff
    }

    override fun getItemCount(): Int {
        return selectedExercisemodelLists.size
    }

}
