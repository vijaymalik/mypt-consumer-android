package co.com.mypt.calendarUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventMultiColorDecorator(private val color: Int, private val dates: List<CalendarDay>) : DayViewDecorator {

    // Return true if we want to decorate the given day
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    // This method is called to apply the decoration
    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, color))  // Apply color as background for the day
    }
}