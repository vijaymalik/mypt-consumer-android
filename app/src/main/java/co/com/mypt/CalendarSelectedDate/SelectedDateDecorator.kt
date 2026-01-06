package co.com.mypt.CalendarSelectedDate

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectedDateDecorator(
    private val backgroundDrawable: Drawable,
    private var selectedDate: CalendarDay? = null
) : DayViewDecorator {



    override fun shouldDecorate(day: CalendarDay): Boolean {
        // Decorate only the selected date
        return day == selectedDate
    }

    override fun decorate(view: DayViewFacade) {
        // Set the rectangle background
        view.setSelectionDrawable(backgroundDrawable)
    }

    fun setSelectedDate(date: CalendarDay?) {
        selectedDate = date
    }
}
