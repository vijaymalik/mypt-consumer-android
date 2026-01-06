package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.model.UpcomingMealsModel
import co.com.mypt.R
import java.util.ArrayList

class UpcomingMealsAdapter(
    val context: Context?,
    val upcomingMealsArraylist: ArrayList<UpcomingMealsModel>
) : RecyclerView.Adapter<UpcomingMealsAdapter.ViewHolder>() {
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var title:TextView=itemView.findViewById(R.id.title)
        var calories:TextView=itemView.findViewById(R.id.calories)
        var protien:TextView=itemView.findViewById(R.id.protien)
        var fat:TextView=itemView.findViewById(R.id.fat)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_meals_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return upcomingMealsArraylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var upcomingMealsModel=upcomingMealsArraylist[position]
        holder.title.setText(upcomingMealsModel.meal_name)
        holder.calories.setText(upcomingMealsModel.calories)
        holder.protien.setText(upcomingMealsModel.proteins)
        holder.fat.setText(upcomingMealsModel.fats)
    }

}
