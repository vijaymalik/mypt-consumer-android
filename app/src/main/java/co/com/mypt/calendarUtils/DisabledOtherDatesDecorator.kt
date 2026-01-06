package co.com.mypt.calendarUtils

import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DisabledOtherDatesDecorator(
    private val currentMonth: Int,
    private val currentYear: Int,
    private val disabledColor: Int, // Pass the color for disabled dates
    private val currentDay: Int
) : DayViewDecorator {
    private val today: CalendarDay = CalendarDay.today()
    override fun shouldDecorate(day: CalendarDay): Boolean {
        // Check if the day is outside the current month
        return day.isBefore(today) || day.month != currentMonth
        //return day.month != currentMonth || day.year != currentYear || day.day != currentDay
    }

    override fun decorate(view: DayViewFacade) {
        // Disable selection
        view.setDaysDisabled(true)
        // Apply custom color
        view.addSpan(ForegroundColorSpan(disabledColor))
    }
}