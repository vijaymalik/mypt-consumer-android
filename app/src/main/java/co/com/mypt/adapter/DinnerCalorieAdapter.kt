package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Goals.Calorie_IntakeActivity
import co.com.mypt.R
import co.com.mypt.model.DinnerListModel
import kotlin.collections.ArrayList

class DinnerCalorieAdapter(
    var applicationContext: Context?,
    var snackList: ArrayList<DinnerListModel>,
    var calorieIntakeactivity: Calorie_IntakeActivity,
    var calorieType: String
) : RecyclerView.Adapter<DinnerCalorieAdapter.DinnerCalorieHolder>() {
    class DinnerCalorieHolder (view: View):RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var imEdit=view.findViewById<ImageView>(R.id.imEdit)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DinnerCalorieAdapter.DinnerCalorieHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calorie_list, parent, false)
        return DinnerCalorieHolder(view)
    }

    override fun onBindViewHolder(holder: DinnerCalorieAdapter.DinnerCalorieHolder, position: Int) {
        var breakFastListModel=snackList[position]
        holder.tvname.setText(breakFastListModel.name)
        holder.tvcal.setText(breakFastListModel.calories)

        holder.imEdit.setTag(position)

        holder.imEdit.setOnClickListener{
            var j=it.tag
            var breakFastListModel=snackList[j as Int]
            calorieIntakeactivity.editCalorieBottomSheet(
                breakFastListModel.id,
                breakFastListModel.name,
                breakFastListModel.calories,
                calorieType
            )
        }
    }

    override fun getItemCount(): Int {
        return snackList.size
    }

}
