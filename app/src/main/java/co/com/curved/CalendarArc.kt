package co.com.curved

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.utils.SharedDOBViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarArc :  Fragment()  {
    private val viewModel: SharedDOBViewModel by activityViewModels()

    private val years = ArrayList<String>()
    private var months = ArrayList<String>()
    private var days = ArrayList<String>()
    var currentMonth = 0
    var currentDay = 0
    var currentYear = 0
    lateinit var age : TextView
    lateinit var recyclerDay : RecyclerView
    lateinit var recyclerYear : RecyclerView
    lateinit var recyclerMonth : RecyclerView
    lateinit var context_: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context_ = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_cal_arc, container, false)
        age = view.findViewById(R.id.age)
        recyclerDay = view.findViewById(R.id.recyclerDay)
        recyclerMonth = view.findViewById(R.id.recyclerMonth)
        recyclerYear = view.findViewById(R.id.recyclerYear)

        val today = Calendar.getInstance()
        currentMonth = today.get(Calendar.MONTH)
        currentDay = today.get(Calendar.DAY_OF_MONTH)
        currentYear = today.get(Calendar.YEAR)

        years.add("")
        years.add("")
        for (year in 1825..currentYear) {
            years.add(year.toString())
        }
        years.add("")
        years.add("")

        recyclerYear.layoutManager = CurvedLayoutManager(context_)
        val arcAdapter = ArcCalendarAdapter(years,recyclerYear)
        recyclerYear.adapter = arcAdapter

        val defaultYear = currentYear - 18
        val defaultIndex = years.indexOf(defaultYear.toString())-2
        recyclerYear.scrollToPosition(defaultIndex)

        months = getMonthsForYear(defaultYear)
        recyclerMonth.layoutManager = CurvedLayoutManager(context_)
        val monthAdapter = MonthCalAdapter(months,recyclerMonth)
        recyclerMonth.adapter = monthAdapter
        recyclerMonth.scrollToPosition(currentMonth)

        val sdf = SimpleDateFormat("dd")
        val selectedMonthDates : List<Date> = getDates(mutableListOf())
        days.clear()
        days.add("")
        days.add("")
        for (i in selectedMonthDates.indices){
            days+= sdf.format(selectedMonthDates[i])
        }
        days.add("")
        days.add("")

        recyclerDay.layoutManager = CurvedLayoutManager(context_)
        val dayAdapter = DayCalAdapter(days,recyclerDay)
        recyclerDay.adapter = dayAdapter
        recyclerDay.scrollToPosition(currentDay)

        /** Year Snap Helper**/
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerYear)

        val snapOnScrollListener = SnapOnScrollListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int, currentValue :Int, type :Int) {
                    Log.d("SnapListener", "$currentValue------Snapped to position: $position")
                    val sdf = SimpleDateFormat("dd")
                    currentYear = years[position].toInt()
                    months = getMonthsForYear(currentYear)
                    monthAdapter.updateList(months)
                    //recyclerMonth.adapter = ArcCalendarAdapter(months,recyclerYear)
                    //currentMonth = 1

                    /*recyclerDay.adapter = DayCalAdapter(days,recyclerDay)
                    currentDay = ("0${days[2]}").toInt()*/
                    if (currentYear == Calendar.getInstance().get(Calendar.YEAR)) {
                        if(currentMonth > Calendar.getInstance().get(Calendar.MONTH)){
                            currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                            if(currentDay > Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                        }
                    }

                    val selectedMonthDates : List<Date> = getDates(mutableListOf())
                    val days = ArrayList<String>()
                    days.add("")
                    days.add("")
                    for (i in selectedMonthDates.indices){
                        days.add(sdf.format(selectedMonthDates[i]))
                    }
                    days.add("")
                    days.add("")

                    recyclerDay.recycledViewPool.clear()
                    recyclerDay.post {
                        dayAdapter.updateList(days)
                    }

                    val currentMonth = when (currentMonth+1) {
                        in 1..9 -> {
                            ("0${currentMonth+1}")
                        }
                        0 -> {
                            "01"
                        }
                        else -> {
                            currentMonth+1
                        }
                    }
                    var tempDay = ""
                    tempDay = if(currentDay < 10){
                        "0$currentDay"
                    } else
                        "$currentDay"

                    val yourAge = calculateAge("$tempDay/$currentMonth/$currentYear")
                    age.text = yourAge
                }
            },
            currentYear,
            1
        )
        recyclerYear.addOnScrollListener(snapOnScrollListener)


        /** Month Snap Helper**/
        val monthSnapHelper = LinearSnapHelper()
        monthSnapHelper.attachToRecyclerView(recyclerMonth)

        val snapOnScrollListenerMonth = SnapOnScrollListener(
            monthSnapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int, currentValue :Int, type :Int) {
                    Log.d("SnapListener", "$currentValue------Snapped to position: $position")
                    val sdf = SimpleDateFormat("dd")

                    currentMonth = if(position-2 <10){
                        ("0${position-2}").toInt()
                    } else{
                        position-2
                    }

                    val selectedMonthDates : List<Date> = getDates(mutableListOf())
                    val days = ArrayList<String>()
                    days.add("")
                    days.add("")
                    for (i in selectedMonthDates.indices){
                        days.add(sdf.format(selectedMonthDates[i]))
                    }
                    days.add("")
                    days.add("")

                    recyclerDay.recycledViewPool.clear()
                    recyclerDay.post {
                        dayAdapter.updateList(days)
                    }

                    val currentMonth = when (currentMonth+1) {
                        in 1..9 -> {
                            ("0${currentMonth+1}")
                        }
                        0 -> {
                            "01"
                        }
                        else -> {
                            currentMonth+1
                        }
                    }
                    var tempDay = ""
                    tempDay = if(currentDay < 10){
                        "0$currentDay"
                    } else
                        "$currentDay"

                    val yourAge = calculateAge("$tempDay/$currentMonth/$currentYear")

                    age.text = yourAge
                }
            },
            currentMonth,
            2
        )
        recyclerMonth.addOnScrollListener(snapOnScrollListenerMonth)


        /** Day Snap Helper**/
        val snapHelperDay = LinearSnapHelper()
        snapHelperDay.attachToRecyclerView(recyclerDay)

        val snapOnScrollListenerDay = SnapOnScrollListener(
            snapHelperDay,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int, currentValue :Int, type :Int) {
                    Log.d("SnapListener", "$currentValue------Snapped to position: $position")
                    //val sdf = SimpleDateFormat("dd")
                     if (days[position].isNullOrEmpty())return
                    currentDay = if(days[position].toInt() <10){
                        ("0${days[position]}").toInt()
                    } else{
                        days[position].toInt()
                    }

                    val currentMonth = when (currentMonth+1) {
                        in 1..9 -> {
                            ("0${currentMonth+1}")
                        }
                        0 -> {
                            "01"
                        }
                        else -> {
                            currentMonth+1
                        }
                    }
                    var tempDay = ""
                    tempDay = if(currentDay < 10){
                        "0$currentDay"
                    } else
                        "$currentDay"

                    val yourAge = calculateAge("$tempDay/$currentMonth/$currentYear")

                    age.text = yourAge
                }
            },
            currentDay,
            3
        )
        recyclerDay.addOnScrollListener(snapOnScrollListenerDay)

        /*setupRecyclerView(recyclerDay, days,today.get(Calendar.DAY_OF_MONTH),3) { selectedDay ->
            println("Selected Day: $selectedDay")
        }*/
        return view
    }

    fun calculateAge(selectedDate: String): String {
        viewModel.data.value = selectedDate.replace("/","-")

        Log.e("selectedDate------->",selectedDate)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ✅ Modern API (Android 8+)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(selectedDate, formatter)
            val currentDate = LocalDate.now()

            // ✅ If birth date == today, return "0 years, 0 months" immediately
            if (birthDate.isEqual(currentDate)) {
                return "0 years"
            }

            val period = Period.between(birthDate, currentDate)

            var years = period.years
            var months = period.months

            // ✅ Ensure months don't roll over incorrectly
            if (currentDate.dayOfMonth < birthDate.dayOfMonth) {
                months--
                if (months < 0) {
                    years--
                    months += 12
                }
            }
// years, $months months
            return if (years<0) "0" else "$years"
        } else {
            // ✅ Legacy API (Older Android versions)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val birthDate = dateFormat.parse(selectedDate) ?: return "0 years, 0 months"

            val birthCalendar = Calendar.getInstance().apply { time = birthDate }
            val today = Calendar.getInstance()

            // ✅ If birth date == today, return "0 years, 0 months" immediately
            if (birthCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                birthCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                birthCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                return "0 years"
            }

            var years = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            var months = today.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)

            // ✅ Ensure months don't roll over incorrectly
            if (today.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH)) {
                months--
                if (months < 0) {
                    years--
                    months += 12
                }
            }
// years, $months months
            return if (years<0)"0" else "$years"
        }
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)


        while (currentMonth == calendar[Calendar.MONTH]) {
            val date = calendar.time

            // If it's the current month, don't add future dates
            if (currentYear == today[Calendar.YEAR] && currentMonth == today[Calendar.MONTH]) {
                if (calendar[Calendar.DAY_OF_MONTH] >= today[Calendar.DAY_OF_MONTH]) {
                    break
                }
            }

            list.add(date)
            calendar.add(Calendar.DATE, 1)
        }
        return list
    }

    fun getMonthsForYear(selectedYear: Int): ArrayList<String> {
        val monthsList = arrayListOf<String>()
        monthsList.add("")
        monthsList.add("")

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) // 0 = January, 11 = December

        val monthsArray = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        when {
            selectedYear < currentYear -> {
                // Past year: Add all months
                monthsList.addAll(monthsArray)
            }
            selectedYear == currentYear -> {
                // Current year: Add months up to the current month
                for (i in 0..currentMonth) {
                    monthsList.add(monthsArray[i])
                }
            }
            else -> {
                // Future year: Add all months
                monthsList.addAll(monthsArray)
            }
        }
        monthsList.add("")
        monthsList.add("")

        return monthsList
    }
}