package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.SelectTimefromBookaTrainerCalendarActivity2
import co.com.mypt.adapter.CalendarAdapter1.CalendarHolder
import co.com.mypt.calendarUtils.Date
import co.com.mypt.calendarUtils.Event

class CalendarBookTrainerAdapter(var selectTimefromBookaTrainerCalendarActivity2: SelectTimefromBookaTrainerCalendarActivity2, var calendarDates: List<Date>, var events: List<Event>):RecyclerView.Adapter<CalendarBookTrainerAdapter.CalendarHolder>() {
    class CalendarHolder(view: View):RecyclerView.ViewHolder(view) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val eventIndicator: View = itemView.findViewById(R.id.eventIndicator)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarBookTrainerAdapter.CalendarHolder {
        val view = LayoutInflater.from(selectTimefromBookaTrainerCalendarActivity2).inflate(R.layout.item_calendar, parent, false)
        return CalendarHolder(view)
    }

    override fun onBindViewHolder(
        holder: CalendarBookTrainerAdapter.CalendarHolder,
        position: Int
    ) {
        val date = calendarDates[position]
        holder.tvDate.text = date.day.toString()

        // Check if there's an event for this date and add an indicator
        if (events.any { it.eventDate == date }) {
            holder.eventIndicator.visibility = View.VISIBLE
        } else {
            holder.eventIndicator.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
       return calendarDates.size
    }
}