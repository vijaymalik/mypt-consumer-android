package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.CalendarAdapter1.CalendarHolder
import co.com.mypt.calendarUtils.Date
import co.com.mypt.calendarUtils.Event

class CalendarAdapterAddTimeSlot(var activity: FragmentActivity?, var calendarDatesAddTime: List<Date>, var eventsAdd: List<Event>):RecyclerView.Adapter<CalendarAdapterAddTimeSlot.CalendarHolder> ()  {
    class CalendarHolder(view: View):RecyclerView.ViewHolder(view) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val eventIndicator: View = itemView.findViewById(R.id.eventIndicator)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarAdapterAddTimeSlot.CalendarHolder {
      var view = LayoutInflater.from(activity).inflate(R.layout.item_calendar, parent, false)
        return CalendarHolder(view)
    }

    override fun onBindViewHolder(
        holder: CalendarAdapterAddTimeSlot.CalendarHolder,
        position: Int
    ) {
        val date = calendarDatesAddTime[position]
        holder.tvDate.text = date.day.toString()

        // Check if there's an event for this date and add an indicator
        if (eventsAdd.any { it.eventDate == date }) {
            holder.eventIndicator.visibility = View.VISIBLE
        } else {
            holder.eventIndicator.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return calendarDatesAddTime.size
    }
}