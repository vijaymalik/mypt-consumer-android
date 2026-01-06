package co.com.mypt.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.CreateWorkoutFlow.SuperSet.CreateWorkoutSupersetActivity
import co.com.mypt.R
import co.com.mypt.model.SuperSetExerciseModel
import co.com.mypt.model.SuperSetListModel

class SuperSetListAdapter(
    var applicationContext: Context,
    var superSetModelList: ArrayList<SuperSetListModel>,
    var workout_id: String
): RecyclerView.Adapter<SuperSetListAdapter.SuperSetHolder>() {
    var count = 1
    var total_duration = 0
    var supersetExerciseList :ArrayList<SuperSetExerciseModel> = ArrayList()

    class SuperSetHolder(view: View): RecyclerView.ViewHolder(view) {
       var tvSets=view.findViewById<TextView>(R.id.tvSets)
       var tvSuperSet=view.findViewById<TextView>(R.id.tvSuperSet)
       var tvsecondActiviy=view.findViewById<TextView>(R.id.tvsecondActiviy)
       var recyclerSet=view.findViewById<RecyclerView>(R.id.recyclerSet)
       var linearActivityRest=view.findViewById<LinearLayout>(R.id.linearActivityRest)
       var linearSet=view.findViewById<LinearLayout>(R.id.linearSet)
       var imSecond=view.findViewById<ImageView>(R.id.imSecond)
       var imclose=view.findViewById<ImageView>(R.id.imclose)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuperSetHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.creat_set_list, parent, false)
        return SuperSetHolder(view)
    }

    override fun onBindViewHolder(
        holder: SuperSetHolder,
        position: Int
    ) {
        var superSetListModel=superSetModelList[position]
        holder.tvsecondActiviy.text = "Rest ${superSetListModel.duration} seconds"

        if (superSetListModel.type.equals("superset")){
            holder.linearSet.visibility=View.VISIBLE
            holder.linearActivityRest.visibility=View.GONE
            holder.tvSets.text = superSetListModel.exercises.optJSONObject(0).optString("sets")+" Sets"
            if (count>10){
                holder.tvSuperSet.text = "Superset 0$count"
            }else{
                holder.tvSuperSet.text = "Superset $count"
            }
            count++
        }else{
            holder.linearSet.visibility=View.GONE
            holder.linearActivityRest.visibility=View.VISIBLE
        }
        supersetExerciseList = ArrayList()
        for(i in 0 until superSetListModel.exercises!!.length()){
            var jsonObjectset=superSetListModel.exercises.optJSONObject(i)
            var superListModel= SuperSetExerciseModel()
            superListModel.id=""+jsonObjectset.optString("id")
            superListModel.category=""+jsonObjectset.optString("category")
            superListModel.name=jsonObjectset.optString("name")
            superListModel.image=jsonObjectset.optString("image")
            superListModel.calories=jsonObjectset.optString("calories")
            superListModel.type=jsonObjectset.optString("type")
            superListModel.sets=jsonObjectset.optString("sets")
            superListModel.reps=jsonObjectset.optString("reps")
            superListModel.duration=jsonObjectset.optString("duration")
            superListModel.time_type=jsonObjectset.optString("time_type")
            superListModel.note=jsonObjectset.optString("note")
            superListModel.rest_duration=jsonObjectset.optString("rest_duration")
            total_duration += jsonObjectset.optString("duration").toInt()
            superListModel.position=jsonObjectset.optString("position")
            superListModel.sets_position=jsonObjectset.optString("sets_position")
            superListModel.workout_exercise_id=jsonObjectset.optString("workout_exercise_id")
            supersetExerciseList.add(superListModel)
        }
        (applicationContext as CreateWorkoutSupersetActivity).tvTime.text = ""+total_duration+"s"

        var supersetExerciseAdapter=
            SuperSetExerciseAdapter(applicationContext, supersetExerciseList,workout_id,superSetListModel.sets_position)
        holder.recyclerSet.adapter=supersetExerciseAdapter

        holder.imSecond.tag = position
        holder.imclose.tag = position
        holder.imSecond.setOnClickListener {
            var j=it.tag
            var superSetListModel=superSetModelList[j as Int]

            showEditRestDialog(holder.tvsecondActiviy,superSetListModel)
        }
        holder.imclose.setOnClickListener {
            var j=it.tag as Int
            //superSetModelList[j as Int]
            var intent= Intent("deleteSuperSetRest")
            intent.putExtra("position",j)
            applicationContext.sendBroadcast(intent)
        }
    }


    override fun getItemCount(): Int {
        return superSetModelList.size
    }
    private fun showEditRestDialog(tvrest: TextView, superSetListModel: SuperSetListModel) {
        var selectedMinutes = 0
        var selectedSeconds = 0
        val dialog = Dialog(applicationContext)
        val bottomSheet = LayoutInflater.from(applicationContext).inflate(R.layout.second_edit, null)
        dialog.setContentView(bottomSheet)
        var minutePicker=bottomSheet.findViewById<NumberPicker>(R.id.minutePicker)
        var secondPicker=bottomSheet.findViewById<NumberPicker>(R.id.secondPicker)
        var txt_ok=bottomSheet.findViewById<TextView>(R.id.txt_ok)
        var txt_close=bottomSheet.findViewById<TextView>(R.id.txt_close)
        txt_ok.setOnClickListener {

            val totalSeconds = selectedMinutes * 60 + selectedSeconds
            // list[position].rest_duration = totalSeconds.toString()
            if (totalSeconds==0){
                Toast.makeText(applicationContext,"Rest time must be greater than 0 seconds",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // tvrest.text = "Rest $totalSeconds seconds"
            superSetListModel.duration=totalSeconds.toString()
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
}
