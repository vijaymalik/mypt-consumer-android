package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.MealListAdapter.MealListHolder
import co.com.mypt.model.MealListModel
import java.util.ArrayList

class MealVerticalListAdapter(
    var applicationContext: Context?,
    var mealArrayList: ArrayList<MealListModel>
) : RecyclerView.Adapter<MealVerticalListAdapter.MealVerticalHolder>() {
    class MealVerticalHolder(view: View):RecyclerView.ViewHolder(view) {
        var checkbox=view.findViewById<CheckBox>(R.id.checkbox)
        var tv=view.findViewById<TextView>(R.id.tv)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var tvprotein=view.findViewById<TextView>(R.id.tvprotein)
        var tvFat=view.findViewById<TextView>(R.id.tvFat)
        var tvtime=view.findViewById<TextView>(R.id.tvtime)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MealVerticalListAdapter.MealVerticalHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_vertical_list, parent, false)
        return MealVerticalHolder(view)
    }

    override fun onBindViewHolder(
        holder: MealVerticalListAdapter.MealVerticalHolder,
        position: Int
    ) {
        var mealModel=mealArrayList[position]
        holder.tv.setText(mealModel.meal_name)
        holder.tvcal.setText(mealModel.calories)
        holder.tvtime.setText(mealModel.meal_time)
        holder.tvprotein.setText(mealModel.proteins+"g")
        holder.tvFat.setText(mealModel.fats+"g")

       /* holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            val intent = Intent("selectedExercise")
            if (isChecked) {
                holder.relative.setBackgroundResource(R.drawable.choose_exercise_rectangle)
                intent.putExtra("position", position.toString())
                intent.putExtra("type", "add")

            }else{
                holder.relative.setBackgroundResource(R.drawable.excercise_gradient)
                intent.putExtra("position", position.toString())
                intent.putExtra("type", "remove")
            }
            activity!!.sendBroadcast(intent)
            notifyDataSetChanged()


        })*/
    }

    override fun getItemCount(): Int {
      return mealArrayList.size
    }

}
