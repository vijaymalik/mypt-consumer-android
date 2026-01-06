package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.ChooseExcerciseActivity
import co.com.mypt.model.SelectExcerciseModel
import com.bumptech.glide.Glide


class SelectExcerciseAdapter(
    var excerxiseModelList: ArrayList<SelectExcerciseModel>,
    var activity: ChooseExcerciseActivity,
    alreadySelectedExcerxiseModelList: ArrayList<SelectExcerciseModel>
):RecyclerView.Adapter<SelectExcerciseAdapter.SelectHoler>() {

    private var selectedModelList = ArrayList<SelectExcerciseModel>()
    class SelectHoler(view: View):RecyclerView.ViewHolder(view) {
        var tv=view.findViewById<TextView>(R.id.tv)
        var tvreps=view.findViewById<TextView>(R.id.tvreps)
        var tvcalorie=view.findViewById<TextView>(R.id.tvcalorie)
        var tvexercise_name=view.findViewById<TextView>(R.id.tvexercise_name)
        var checkbox=view.findViewById<CheckBox>(R.id.checkbox)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
        var im=view.findViewById<ImageView>(R.id.im)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_excercise_list, parent, false)
        return SelectHoler(view)
    }

    override fun onBindViewHolder(holder: SelectHoler, position: Int) {
        var selectExcerciseModel=excerxiseModelList[position]
        holder.tv.setText(selectExcerciseModel.category)
        holder.tvexercise_name.setText(selectExcerciseModel.name)
        holder.tvreps.setText(selectExcerciseModel.raps)
        holder.tvcalorie.setText(selectExcerciseModel.calories)
        Glide.with(activity!!).load(selectExcerciseModel.image).fitCenter().into(holder.im)


        val isChecked =  selectedModelList.any{
            it.id==selectExcerciseModel.id
        }

        /*for (i in 0 until selectedModelList.size){
            if (selectedModelList[i].id==selectExcerciseModel.id){
                 isChecked =  true
            }
        }*/
        holder.checkbox.isChecked = isChecked

        holder.relative.setBackgroundResource(
            if (isChecked) R.drawable.choose_exercise_rectangle
            else R.drawable.excercise_gradient
        )

        holder.checkbox.setTag(position)
        holder.checkbox.setOnClickListener {
            var j=it.tag
            var selectExcerciseModel=excerxiseModelList[j as Int]
            val intent = Intent("selectedExercise")
            if (holder.checkbox.isChecked) {
                holder.relative.setBackgroundResource(R.drawable.choose_exercise_rectangle)
                selectedModelList.add(selectExcerciseModel)
                intent.putParcelableArrayListExtra("exercise_list", selectedModelList)
                intent.putExtra("position", j.toString())
                intent.putExtra("type", "add")
            }else{
                holder.relative.setBackgroundResource(R.drawable.excercise_gradient)

                selectedModelList.removeAll{ it -> it.id == selectExcerciseModel.id}

                intent.putParcelableArrayListExtra("exercise_list", selectedModelList)
                intent.putExtra("position", j.toString())
                intent.putExtra("type", "remove")
            }
            activity.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

    init {
        selectedModelList = alreadySelectedExcerxiseModelList
    }

    override fun getItemCount(): Int {
        return excerxiseModelList.size
    }

    fun clearData() {
        excerxiseModelList.clear()
        notifyDataSetChanged()
    }
}
