package co.com.mypt.calendarUtils

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan


class EventDecorator(private val color: Int, private val events: HashMap<CalendarDay, Int>) :
    DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return events.containsKey(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, color)) // Adjust size and color as needed
    }
}