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
import co.com.mypt.adapter.ActivityAdapter1.Activity_Holder
import co.com.mypt.model.CalorieBurnModel
import java.util.ArrayList

class CalorieBurnWeekAdapter(var context: Context?, var calorieBurnArrayList: ArrayList<CalorieBurnModel>) :
    RecyclerView.Adapter<CalorieBurnWeekAdapter.CalorieBurnWeekHolder>() {
    private var selectedPosition: Int = -1

    class CalorieBurnWeekHolder (view: View):RecyclerView.ViewHolder(view){
        var tvdate=view.findViewById<TextView>(R.id.tvdate)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
        var card=view.findViewById<CardView>(R.id.card)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalorieBurnWeekAdapter.CalorieBurnWeekHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.week_list, parent, false)
        return CalorieBurnWeekHolder(view)
    }

    override fun onBindViewHolder(
        holder: CalorieBurnWeekAdapter.CalorieBurnWeekHolder,
        position: Int
    ) {
        var calorieBurnModel=calorieBurnArrayList[position]
        holder.tvdate.setText(calorieBurnModel.date)
        holder.card.setTag(position)
        holder.linear.setTag(position)
        if (position == selectedPosition) {
            // Set the selected background with a different border color
                holder.linear.background = context!!.resources.getDrawable(R.drawable.weeklist_shadow)
            holder.card.cardElevation=16f

        } else {
            // Default background
            holder.linear.background = context!!.resources.getDrawable(R.drawable.weekly_list_drawable)
            holder.card.cardElevation=0f
        }
        holder.linear.setOnClickListener {
            var j=it.tag
            selectedPosition = j as Int // Set the clicked item as selected
            notifyDataSetChanged() // Notify adapter to update the item background
        }
    }

    override fun getItemCount(): Int {
        return calorieBurnArrayList.size
    }

}
