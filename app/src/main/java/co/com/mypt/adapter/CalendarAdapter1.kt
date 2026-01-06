package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.calendarUtils.CalendarAdapter.ViewHolder
import co.com.mypt.calendarUtils.Date
import co.com.mypt.calendarUtils.Event

class   CalendarAdapter1(var activity: FragmentActivity?, var dates: List<Date>, var events: List<Event>):RecyclerView.Adapter<CalendarAdapter1.CalendarHolder> (){

    class CalendarHolder(view: View):RecyclerView.ViewHolder(view) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val eventIndicator: View = itemView.findViewById(R.id.eventIndicator)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarAdapter1.CalendarHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_calendar, parent, false)
        return CalendarHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarAdapter1.CalendarHolder, position: Int) {
            val date = dates[position]
            holder.tvDate.text = date.day.toString()

            // Check if there's an event for this date and add an indicator
            if (events.any { it.eventDate == date }) {
                holder.eventIndicator.visibility = View.VISIBLE
            } else {
                holder.eventIndicator.visibility = View.GONE
            }
    }

    override fun getItemCount(): Int {
        return dates.size
    }

}
