package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.GoalsAdapter.GoalsHolder
import co.com.mypt.model.StepcountWeekModel

class StepCountWeekAdapter(var applicationContext: Context?, var stepCountWeekArrayList: ArrayList<StepcountWeekModel>) : RecyclerView.Adapter<StepCountWeekAdapter.StepCountHolder>() {
    private var selectedPosition: Int = -1

    class StepCountHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvdate=view.findViewById<TextView>(R.id.tvdate)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
        var card=view.findViewById<CardView>(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepCountHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.week_list, parent, false)
        return StepCountHolder(view)
    }

    override fun getItemCount(): Int {
       return stepCountWeekArrayList.size
    }

    override fun onBindViewHolder(holder: StepCountHolder, position: Int) {
        var calorieBurnModel=stepCountWeekArrayList[position]
        holder.tvdate.setText(calorieBurnModel.date)
        holder.card.setTag(position)
        holder.linear.setTag(position)
        if (position == selectedPosition) {
            // Set the selected background with a different border color
            holder.linear.background = applicationContext!!.resources.getDrawable(R.drawable.weeklist_shadow)
            holder.card.cardElevation=16f

        } else {
            // Default background
            holder.linear.background = applicationContext!!.resources.getDrawable(R.drawable.weekly_list_drawable)
            holder.card.cardElevation=0f
        }
        holder.linear.setOnClickListener {
            var j=it.tag
            selectedPosition = j as Int // Set the clicked item as selected
            notifyDataSetChanged() // Notify adapter to update the item background
        }
    }

}
