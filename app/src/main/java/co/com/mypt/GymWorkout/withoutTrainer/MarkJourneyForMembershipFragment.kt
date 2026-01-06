package co.com.mypt.GymWorkout.withoutTrainer

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CalendarSelectedDate.SelectedDateDecorator
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.calendarUtils.DisableDatesBeforeStartDecorator
import co.com.mypt.calendarUtils.DisabledOtherDatesDecorator
import co.com.mypt.calendarUtils.EventDecorator
import co.com.mypt.calendarUtils.EventMultiColorDecorator
import co.com.mypt.utils.SharedDuringSessionViewModel
import com.android.volley.VolleyError
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.json.JSONObject
import org.threeten.bp.DayOfWeek
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MarkJourneyForMembershipFragment(var trainerId: String?) : Fragment() {
    private val viewModel: SharedDuringSessionViewModel by activityViewModels()

    lateinit var calendarView : MaterialCalendarView
    lateinit var monthName : TextView
    lateinit var tvstartday : TextView
    lateinit var tvendday : TextView
    lateinit var im: ImageView
    lateinit var tvStartDate: TextView
    lateinit var lineaEnd: LinearLayout
    lateinit var linearStart: LinearLayout
    lateinit var tvEndDate: TextView
    lateinit var nextMonth: ImageView
    lateinit var prevMonth: ImageView
    lateinit var im_end: ImageView
    var type="start"
    private var selectedDate: CalendarDay? = null
    private var selectedMonth = 0
    var currentMonth = 0
    lateinit var sharedPreferences: SharedPreferences
    var days=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_mark_journey, container, false)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(requireActivity()!!)
        nextMonth=view.findViewById(R.id.nextMonth)
        prevMonth=view.findViewById(R.id.prevMonth)
        im=view.findViewById(R.id.im)
        im_end=view.findViewById(R.id.im_end)
        tvStartDate=view.findViewById(R.id.tvStartDate)
        lineaEnd=view.findViewById(R.id.lineaEnd)
        tvendday=view.findViewById(R.id.tvendday)
        linearStart=view.findViewById(R.id.linearStart)
        tvstartday=view.findViewById(R.id.tvstartday)
        tvEndDate=view.findViewById(R.id.tvEndDate)
        calendarView =view.findViewById(R.id.calendarView)
        monthName =view.findViewById(R.id.monthName)

        setCalendarData()
        return view

    }

    private fun setCalendarData() {
        calendarView.state().edit()
            .setMinimumDate(CalendarDay.today()) // Disables selection of past dates
            .commit()

        calendarView.setWeekDayFormatter { dayOfWeek ->
            when (dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
                else -> ""
            }
        }

        val eventDays = HashMap<CalendarDay, Int>()

        /** Set Start Date **/
        val today = Calendar.getInstance()
        currentMonth = today.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        var currentYear = today.get(Calendar.YEAR)
        monthName.text = DateFormatSymbols().months.get(currentMonth-1)+" $currentYear"

        //set current date in start date section
        val dateFormat = SimpleDateFormat("dd, MMM", Locale.getDefault())
        val newapiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        tvStartDate.text = dateFormat.format(today.time)
        tvstartday.text = dayFormat.format(today.time)
        viewModel.setstart_dates.value =dateFormat.format(today.time)
        viewModel.setstart_days.value = dayFormat.format(today.time)
        viewModel.apistart_date.value = newapiFormat.format(today.time)


        val rectangleDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.rectangle_background)
        val decorator = SelectedDateDecorator(rectangleDrawable!!)
        //calendarView.addDecorator(decorator)

        val todayDate = CalendarDay.from(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH))
        val currentDateDecorator = SelectedDateDecorator(rectangleDrawable, todayDate)
        calendarView.addDecorator(currentDateDecorator)
        selectedDate = todayDate
        selectedMonth = today.get(Calendar.MONTH)+1

        val eventDecorator = EventDecorator(resources.getColor(R.color.available), eventDays)
        calendarView.addDecorator(eventDecorator)
        calendarView.addDecorator(
            DisabledOtherDatesDecorator(
                currentMonth,
                currentYear,
                resources.getColor(R.color.smallTextcolor),
                today.get(Calendar.DAY_OF_MONTH)
            )
        )

        calendarView.topbarVisible = false

        calendarView.setOnMonthChangedListener { _, date ->
            // Update current month and year
            currentMonth = date.month
            currentYear = date.year
            monthName.text = DateFormatSymbols().months.get(currentMonth-1)+" $currentYear"
            // Refresh decorators
            calendarView.removeDecorators() // Clear previous decorators
            calendarView.addDecorator(eventDecorator) // Re-add event dots
            calendarView.addDecorator(decorator)
            if(type == "start"){
                calendarView.addDecorator(
                    DisabledOtherDatesDecorator(
                        currentMonth,
                        currentYear,
                        resources.getColor(R.color.smallTextcolor),
                        date.day
                    )
                )
            }
            else{
                calendarView.addDecorator(
                    DisableDatesBeforeStartDecorator(
                        date.month,
                        resources.getColor(R.color.smallTextcolor),
                        selectedDate!!
                    )
                )
            }

            getSlot(trainerId,currentMonth)
        }

        calendarView.setOnDateChangedListener { widget, date, selected ->
            val dayOfMonth = date.day

            val calendar = Calendar.getInstance()
            calendar.set(date.year, date.month - 1, date.day) // month is 0-based in Calendar

            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)

            currentDateDecorator.setSelectedDate(date)
            decorator.setSelectedDate(date)

            /** Set Start Date **/
            val tempMonth = SimpleDateFormat("MM", Locale.getDefault()).format(calendar.time)
            selectedDate = date
            selectedMonth = tempMonth.toInt()
            tvstartday.text = dayOfWeek
            tvStartDate.text = "${dayOfMonth},${monthName}"

            viewModel.setstart_dates.value = "${dayOfMonth},${monthName}"
            viewModel.setstart_days.value = dayOfWeek
            viewModel.apistart_date.value = newapiFormat.format(calendar.time)

            /** Set End Date **/
            val endCalendar = Calendar.getInstance()
            endCalendar.set(date.year, date.month - 1, date.day) // Month is 0-based in Calendar
            endCalendar.add(Calendar.DAY_OF_YEAR, days.toInt()) // Add days

            val dayOfMonthEnd = SimpleDateFormat("dd", Locale.getDefault()).format(endCalendar.time)
            val dayOfWeekEnd = SimpleDateFormat("EEE", Locale.getDefault()).format(endCalendar.time)
            val monthNameEnd = SimpleDateFormat("MMM", Locale.getDefault()).format(endCalendar.time)

            tvendday.text = dayOfWeekEnd
            tvEndDate.text = "${dayOfMonthEnd},${monthNameEnd}"
            im_end.visibility = View.VISIBLE

            viewModel.setend_dates.value = "${dayOfMonthEnd},${monthNameEnd}"
            viewModel.setend_days.value = dayOfWeekEnd
            viewModel.apiend_date.value = newapiFormat.format(endCalendar.time)

            val intent1 = Intent("selectedCountFromMarkJourney")
            intent1.putExtra("count", "1")
            context?.sendBroadcast(intent1)

            calendarView.addDecorator(
                DisabledOtherDatesDecorator(
                    currentMonth,
                    currentYear,
                    resources.getColor(R.color.smallTextcolor),
                    date.day
                )
            )


            widget.invalidateDecorators()
        }

        /*linearStart.setOnClickListener{
            type="start"
            val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.rectangle_light_blue)
            val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.grey_light_rectangle)
            linearStart.background = drawable
            tvStartDate.setTextColor(resources.getColor(R.color.headingcolor))
            tvEndDate.setTextColor(resources.getColor(R.color.smallTextcolor))
            lineaEnd.background=drawableEnd

            calendarView.removeDecorators() // Clear previous decorators
            calendarView.addDecorator(eventDecorator) // Re-add event dots
            calendarView.addDecorator(decorator)
            calendarView.addDecorator(
                DisabledOtherDatesDecorator(
                    currentMonth,
                    currentYear,
                    resources.getColor(R.color.smallTextcolor),
                    0
                )
            )
            calendarView.state().edit()
                .setMinimumDate(CalendarDay.today()) // Disables selection of past dates
                .commit()
        }*/

        prevMonth.setOnClickListener {
            val currentDate = calendarView.currentDate
            var month = currentDate.month
            var year = currentDate.year

            // Decrement the month
            if (month == 1) { // January
                month = 12 // Go to December
                year-- // Decrement the year
            } else {
                month-- // Just go to the previous month
            }

            val newDate = CalendarDay.from(year, month, 1)
            calendarView.setCurrentDate(newDate, true)
        }

        nextMonth.setOnClickListener {
            val currentDate = calendarView.currentDate
            var month = currentDate.month
            var year = currentDate.year

            // Increment the month
            if (month == 12) { // December
                month = 1 // Go to January
                year++ // Increment the year
            } else {
                month++ // Just go to the next month
            }

            val newDate = CalendarDay.from(year, month, 1)
            calendarView.setCurrentDate(newDate, true) // Animate to next month

        }
    }

    private fun getSlot(trainerId: String?, currentMonth: Int) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()
        var selectedMonth=""
        if (currentMonth>10){
            selectedMonth = "0$currentMonth" // Calendar.MONTH is 0-based
        }else{
            selectedMonth ="$currentMonth"// Calendar.MONTH is 0-based

        }
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getavailabilityfromJourney+trainerId+"&month="+selectedMonth+"&type="+sharedPreferences.getString("typeWorkout","")

        }else{
            api= ApiURL.getavailabilityfromJourney+trainerId+"&month="+selectedMonth+"&type="+"gym"

        }

        Log.e("MarkYourJourney",api)
        GetMethod(api,context).startMethod(object : ResponseData {
            override fun response(data: String?) {

                Log.e("MarkYourJourneyResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    val eventDecorators = mutableListOf<EventMultiColorDecorator>()

                    var selectColor: Int
                    val jsonArray=jsonObj.optJSONArray("data")

                    if (jsonArray != null) {
                        for(i in 0 until jsonArray.length()){
                            val jsonObject1 = jsonArray.optJSONObject(i)
                            selectColor = if (jsonObject1.optString("status").equals("Closed")){
                                resources.getColor(R.color.smallTextcolor)
                            }else if (jsonObject1.optString("status").equals("Available")){
                                resources.getColor(R.color.available)
                            }else if (jsonObject1.optString("status").equals("Fully Booked")){
                                resources.getColor(R.color.fully_booked)
                            }else{
                                resources.getColor(R.color.fast_filling)
                            }

                            val fulldate=jsonObject1.optString("date")
                            val (year, month, day) = fulldate.split("-")

                            val eventDecorator = EventMultiColorDecorator(selectColor, listOf(
                                CalendarDay.from(year.toInt(), month.toInt(), day.toInt())))
                            eventDecorators.add(eventDecorator)
                        }
                    }

                    eventDecorators.forEach { calendarView.addDecorator(it) }
                    progressDialog.dismiss()

                }catch (e:Exception){
                    progressDialog.dismiss()
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        if (isVisible){
            try {
                /*if (sharedPreferences.getString("typewithout","")!="withoutTrainer" ||
                    sharedPreferences.getString("typewithout","")==""){
                    getSlot(trainerId,currentMonth,)
                }*/
                getSlot(trainerId,currentMonth)

                days=(activity as GymValidityActivity).days
                /** Set End Date **/
                val newapiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = Calendar.getInstance()
                val calendar1 = Calendar.getInstance()
                calendar1.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)) // Month is 0-based in Calendar
                calendar1.add(Calendar.DAY_OF_YEAR, days.toInt()) // Add days

                val dayOfMonthEnd = SimpleDateFormat("dd", Locale.getDefault()).format(calendar1.time)
                val dayOfWeekEnd = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar1.time)
                val monthNameEnd = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar1.time)

                tvendday.text = dayOfWeekEnd
                tvEndDate.text = "${dayOfMonthEnd},${monthNameEnd}"
                im_end.visibility = View.VISIBLE

                viewModel.setend_dates.value = "${dayOfMonthEnd},${monthNameEnd}"
                viewModel.setend_days.value = dayOfWeekEnd
                viewModel.apiend_date.value = newapiFormat.format(calendar1.time)

                val intent = Intent("selectedCountFromMarkJourney")
                intent.putExtra("count", "1")
                context?.sendBroadcast(intent)

            }catch (e:Exception){
                getSlot(trainerId,currentMonth,)

            }

        }
    }

}