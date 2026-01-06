package co.com.mypt.calendarUtils

import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DisableDatesBeforeStartDecorator(
    private val selectedMonth: Int,
    private val disabledColor: Int,
    val selectedDate: CalendarDay
) :
    DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.isBefore(selectedDate) || day.month != selectedMonth
    }

    override fun decorate(view: DayViewFacade) {
        // Disable selection
        view.setDaysDisabled(true)
        // Apply custom color
        view.addSpan(ForegroundColorSpan(disabledColor))
    }

}
