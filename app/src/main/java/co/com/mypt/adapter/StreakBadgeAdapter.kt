package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.StreakBadgeModel

class StreakBadgeAdapter(val context: Context?, val streakBadgeArrayList: ArrayList<StreakBadgeModel>)  :
    RecyclerView.Adapter<StreakBadgeAdapter.RowHolder>() {
    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {
        val silverLayout: RelativeLayout = view.findViewById(R.id.silverLayout)
        val goldenLayout: RelativeLayout = view.findViewById(R.id.goldenLayout)
        val days: TextView = view.findViewById(R.id.days)
        val days1: TextView = view.findViewById(R.id.days1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.streak_badge_adapter, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        //return badgeArrayList.size
        return 5
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {

        holder.days.text = "0${position+1}"
        holder.days1.text = "0${position+1}"

        /*if(position == 4){
            holder.silverLayout.visibility = View.VISIBLE
            holder.goldenLayout.visibility = View.GONE
        }else{
            holder.silverLayout.visibility = View.GONE
            holder.goldenLayout.visibility = View.VISIBLE
        }*/
    }

}