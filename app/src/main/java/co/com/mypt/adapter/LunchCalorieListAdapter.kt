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
import co.com.mypt.model.LunchListModel

class LunchCalorieListAdapter(
    var context: Context?,
    var lunchArrayList: ArrayList<LunchListModel>,
    var calorieIntakeactivity: Calorie_IntakeActivity,
    var calorieType: String
) : RecyclerView.Adapter<LunchCalorieListAdapter.LunchViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LunchCalorieListAdapter.LunchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calorie_list, parent, false)
        return LunchViewHolder(view)
    }

    class LunchViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var imEdit=view.findViewById<ImageView>(R.id.imEdit)
    }

    override fun onBindViewHolder(holder: LunchCalorieListAdapter.LunchViewHolder, position: Int) {
        var breakFastListModel=lunchArrayList[position]
        holder.tvname.setText(breakFastListModel.name)
        holder.tvcal.setText(breakFastListModel.calories)
        holder.imEdit.setTag(position)

        holder.imEdit.setOnClickListener{
            var j=it.tag
            var breakFastListModel=lunchArrayList[j as Int]
            calorieIntakeactivity.editCalorieBottomSheet(
                breakFastListModel.id,
                breakFastListModel.name,
                breakFastListModel.calories,
                calorieType
            )
        }
    }

    override fun getItemCount(): Int {
        return lunchArrayList.size
    }

}
