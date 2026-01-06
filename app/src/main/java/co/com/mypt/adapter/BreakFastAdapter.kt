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
import co.com.mypt.model.BreakFastListModel

class BreakFastAdapter(
    var applicationContext: Context?,
    var breakFastArrayList: ArrayList<BreakFastListModel>,
    var calorieIntakeactivity: Calorie_IntakeActivity,
    var calorieType: String,

    ) : RecyclerView.Adapter<BreakFastAdapter.BreakFastHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BreakFastAdapter.BreakFastHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calorie_list, parent, false)
        return BreakFastHolder(view)
    }

    class BreakFastHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var imEdit=view.findViewById<ImageView>(R.id.imEdit)
    }

    override fun onBindViewHolder(holder: BreakFastAdapter.BreakFastHolder, position: Int) {
       var breakFastListModel=breakFastArrayList[position]
        holder.tvname.setText(breakFastListModel.name)
        holder.tvcal.setText(breakFastListModel.calories)
        holder.imEdit.setTag(position)

        holder.imEdit.setOnClickListener{
            var j=it.tag
            var breakFastListModel=breakFastArrayList[j as Int]

            calorieIntakeactivity.editCalorieBottomSheet(
                breakFastListModel.id,
                breakFastListModel.name,
                breakFastListModel.calories,
                calorieType
            )
        }
    }



    override fun getItemCount(): Int {
        return breakFastArrayList.size
    }

}
