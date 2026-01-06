package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.CalendarMyScheduleActivity
import co.com.mypt.calendarUtils.Date
import co.com.mypt.calendarUtils.Event


class CalendarScheduleAdapter(
    private val context: Context,
    private val dates: List<Date>,
    private val events: List<Event>
) : RecyclerView.Adapter<CalendarScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = dates[position]
        holder.tvDate.text = date.day.toString()

        // Check if there's an event for this date and add an indicator
        if (events.any { it.eventDate == date }) {
            holder.eventIndicator.visibility = View.VISIBLE
        } else {
            holder.eventIndicator.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = dates.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val eventIndicator: View = itemView.findViewById(R.id.eventIndicator)
    }
}
