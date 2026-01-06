package co.com.mypt.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.StreakModel

class StreakAdapter(val streakArrayList: ArrayList<StreakModel>, val context: Context) :
    RecyclerView.Adapter<StreakAdapter.RowHolder>() {
    class RowHolder(view:View) : RecyclerView.ViewHolder(view) {
        val daysName : TextView = view.findViewById(R.id.daysName)
        val dayNumber : TextView = view.findViewById(R.id.dayNumber)
        val imcross : ImageView = view.findViewById(R.id.imcross)
        val imgreentick : ImageView = view.findViewById(R.id.imgreentick)
        val imgrey : ImageView = view.findViewById(R.id.imgrey)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_streak_layout, parent, false)
        val displayMetrics = parent.context.resources.displayMetrics
        val totalWidth = displayMetrics.widthPixels

        val marginPx = (20 * displayMetrics.density).toInt() * 2
        val availableWidth = totalWidth - marginPx

        val itemWidth = availableWidth / 7

        view.layoutParams = RecyclerView.LayoutParams(itemWidth, RecyclerView.LayoutParams.WRAP_CONTENT)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return streakArrayList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {

        val streakData = streakArrayList[position]
        if(position+1 < 10)
            holder.dayNumber.text = "0"+(position+1)
        else
            holder.dayNumber.text = "${position+1}"
        holder.daysName.text = streakData.day.dropLast(1)
        Log.e("status",""+streakData.status)
        if (streakData.status.equals("completed")){
            holder.imgreentick.visibility=View.VISIBLE
            holder.imcross.visibility=View.GONE
            holder.imgrey.visibility=View.GONE
        }else if (streakData.status.equals("today_pending")){
            holder.imgreentick.visibility=View.GONE
            holder.imcross.visibility=View.GONE
            holder.imgrey.visibility=View.VISIBLE
        }
        else if (streakData.status.equals("not_started")){
            holder.imgreentick.visibility=View.GONE
            holder.imcross.visibility=View.GONE
            holder.imgrey.visibility=View.VISIBLE
        } else if (streakData.status.equals("upcoming")){
            holder.imgreentick.visibility=View.GONE
            holder.imcross.visibility=View.GONE
            holder.imgrey.visibility=View.VISIBLE
        }else{
            holder.imgreentick.visibility=View.GONE
            holder.imcross.visibility=View.VISIBLE
            holder.imgrey.visibility=View.GONE
        }
    }

}
