package co.com.mypt.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.R
import co.com.mypt.adapter.CalendarAdapterAddTimeSlot
import co.com.mypt.adapter.CalendarScheduleAdapter
import co.com.mypt.adapter.MyScheduleAdapter
import co.com.mypt.calendarUtils.Event
import co.com.mypt.model.MyScheduleModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CalendarMyScheduleActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private lateinit var calendarDates: List<co.com.mypt.calendarUtils.Date>
    private lateinit var calendarAdapter: CalendarScheduleAdapter
    var scheduleModelList :ArrayList<MyScheduleModel> = ArrayList()
    lateinit var fitness_planBottomSheetDialog: BottomSheetDialog
    lateinit var addTimeSLotBottomSheetDialog: BottomSheetDialog
    var eventDate = "26-01-2025"
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    private lateinit var events: List<Event>
    lateinit var linearRecycler:RecyclerView
    lateinit var tvAddNewEvent:TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    private val calendarAddTime = Calendar.getInstance()
    private var currentMonthAddTime = 0
    var eventDateAddTime = "26-01-2025"
    private lateinit var calendarDatesAddTime: List<co.com.mypt.calendarUtils.Date>
    private lateinit var eventsAdd: List<Event>
    val sdfAddTime = SimpleDateFormat("dd-MM-yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_my_schedule)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)
        edit=sharedPreferences.edit()
        fitness_planBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        addTimeSLotBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        linearRecycler=findViewById(R.id.linearRecycler)
        tvAddNewEvent=findViewById(R.id.tvAddNewEvent)
        createPlanAlert()
        createAddTimeAlert()
        tvAddNewEvent.setOnClickListener{
            fitness_planBottomSheetDialog.show()
        }
//        for (i in 0..4) {
//            var myScheduleModel= MyScheduleModel()
//            myScheduleModel.name="5 Exercise"
//            scheduleModelList.add(myScheduleModel)
//        }
//        var myscheduleAdapter= MyScheduleAdapter(scheduleModelList,this)
//        linearRecycler.adapter=myscheduleAdapter

        calendar.time = Date()
        calendarDates = getCalendarDates()
        events = getEvents()

        // Set up the RecyclerView with the adapter
        calendarAdapter = CalendarScheduleAdapter(this, calendarDates, events)

        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: java.util.Date,
                isSelected: Boolean
            ): Int {
                val cal = Calendar.getInstance()
                cal.time = date
                return if (isSelected)
                    if(sdf.format(cal.time) == sdf.format(sdf.parse(eventDate))){
                        return  R.layout.fast_filling_selected_calendar_item
                    }else
                        R.layout.selected_calendar_item
                else
                // here we return items which are not selected
                    if(sdf.format(cal.time) == sdf.format(sdf.parse(eventDate))){
                        return  R.layout.first_special_calendar_item
                    }else
                        R.layout.fast_filling_calendar_item

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: java.util.Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                val tv_date_calendar_item : TextView = holder.itemView.findViewById(R.id.tv_date_calendar_item)
                tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text = DateUtils.getDay3LettersName(date)

            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(
                isSelected: Boolean,
                position: Int,
                date: java.util.Date
            ) {
                /*tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                tvDay.text = DateUtils.getDayName(date)*/
                super.whenSelectionChanged(isSelected, position, date)
            }

        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: java.util.Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                Log.e("date","${date}==========${Calendar.getInstance().time}")
                // in this example sunday and saturday can't be selected, others can
                return true
                //return sdf.parse(sdf.format(cal.time)) >= sdf.parse(sdf.format(Calendar.getInstance().time))

            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar : SingleRowCalendar = findViewById(R.id.main_single_row_calendar)
        val myCalendar = Calendar.getInstance()
        singleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            init()
            select(myCalendar.get(Calendar.DAY_OF_MONTH))
        }
        singleRowCalendar.scrollToPosition(intent.getIntExtra("day",0))
    }


    private fun createPlanAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.plan_your_fitness_layout, null)
        fitness_planBottomSheetDialog.setContentView(bottomSheet)
        val linearPlan=bottomSheet.findViewById<LinearLayout>(R.id.linearPlan)
        val linearHome=bottomSheet.findViewById<LinearLayout>(R.id.linearHome)
        val linearGym=bottomSheet.findViewById<LinearLayout>(R.id.linearGym)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        linearGym.setOnClickListener{
            edit.putString("typeWorkout","work").commit()
            startActivity(Intent(this, GymListActivity::class.java))
        }
        linearHome.setOnClickListener{
            edit.putString("typeWorkout","home").commit()
            startActivity(Intent(this, TrainersListActivity::class.java))
        }
        linearPlan.setOnClickListener{
            addTimeSLotBottomSheetDialog.show()
        }
        fitness_planBottomSheetDialog.setContentView(bottomSheet)

        val window = addTimeSLotBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(fitness_planBottomSheetDialog)
    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun getFutureDatesOfCurrentMonth(): List<java.util.Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<java.util.Date>): List<java.util.Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

    private fun getCalendarDates(): List<co.com.mypt.calendarUtils.Date> {
        val dates = mutableListOf<co.com.mypt.calendarUtils.Date>()
        val calendar = Calendar.getInstance()
        for (i in 1..30) {
            val date = co.com.mypt.calendarUtils.Date(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                i
            )
            dates.add(date)
        }
        return dates
    }

    // Method to fetch events (can be from a database, API, or hardcoded)
    private fun getEvents(): List<Event> {
        val eventList = mutableListOf<Event>()
        val event1 = Event(co.com.mypt.calendarUtils.Date(2024, 11, 5), "Meeting")
        val event2 = Event(co.com.mypt.calendarUtils.Date(2024, 11, 10), "Conference")
        eventList.add(event1)
        eventList.add(event2)
        return eventList
    }
    private fun createAddTimeAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.addtime_slot_bottomsheet, null)
        addTimeSLotBottomSheetDialog.setContentView(bottomSheet)
        //horizonatal calendar
        calendarAddTime.time = Date()
        calendarDatesAddTime = getCalendarDatesAddTImeSlot()
        eventsAdd = getEvents()
        var calendarAdapter1 = CalendarAdapterAddTimeSlot(this, calendarDatesAddTime, eventsAdd)
        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: java.util.Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                // if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view

                return if (isSelected)
                    if(sdfAddTime.format(cal.time) == sdfAddTime.format(sdfAddTime.parse(eventDateAddTime))){
                        return  R.layout.fast_filling_selected_calendar_item
                    }else
                        R.layout.selected_calendar_item
                else
                // here we return items which are not selected
                    if(sdfAddTime.format(cal.time) == sdfAddTime.format(sdfAddTime.parse(eventDateAddTime))){
                        return  R.layout.first_special_calendar_item
                    }else
                        R.layout.fast_filling_calendar_item

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: java.util.Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                val tv_date_calendar_item : TextView = holder.itemView.findViewById(R.id.tv_date_calendar_item)
                tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text = DateUtils.getDay3LettersName(date)

            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(
                isSelected: Boolean,
                position: Int,
                date: java.util.Date
            ) {
                /*tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                tvDay.text = DateUtils.getDayName(date)*/
                super.whenSelectionChanged(isSelected, position, date)
            }

        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: java.util.Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                Log.e("date","${date}==========${Calendar.getInstance().time}")
                // in this example sunday and saturday can't be selected, others can
                return sdfAddTime.parse(sdfAddTime.format(cal.time)) >= sdfAddTime.parse(sdfAddTime.format(Calendar.getInstance().time))

            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar : SingleRowCalendar =bottomSheet.findViewById(R.id.main_single_row_calendar1)
        singleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonthAddTimeSlot())
            init()
            select(3)
        }

        //timePicker code
        var timePicker=bottomSheet.findViewById<TimePicker>(R.id.timePicker)
        var lineardone=bottomSheet.findViewById<LinearLayout>(R.id.lineardone)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        lineardone.setOnClickListener{
            var intent=Intent(this,CalendarSchedulePersonalWorkoutActivity::class.java)
            startActivity(intent)
        }
        timePicker.setOnTimeChangedListener { _, hour, minute -> var hour = hour
            var am_pm = ""
            // AM_PM decider logic
            when {hour == 0 -> { hour += 12
                am_pm = "AM"
            }
                hour == 12 -> am_pm = "PM"
                hour > 12 -> { hour -= 12
                    am_pm = "PM"
                }
                else -> am_pm = "AM"
            }}
        removeColonFromTimePicker(timePicker)

        addTimeSLotBottomSheetDialog.setContentView(bottomSheet)
        val window = addTimeSLotBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        animateBottomSheet(addTimeSLotBottomSheetDialog)


    }
    private fun getCalendarDatesAddTImeSlot(): List<co.com.mypt.calendarUtils.Date> {
        val dates = mutableListOf<co.com.mypt.calendarUtils.Date>()
        val calendar = Calendar.getInstance()

        // Adding some example dates
        for (i in 1..30) {
            val date = co.com.mypt.calendarUtils.Date(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                i
            )
            dates.add(date)
        }
        return dates
    }
    private fun getFutureDatesOfCurrentMonthAddTimeSlot(): List<java.util.Date> {
        // get all next dates of current month
        // currentMonth = intent.getIntExtra("month",calendar[Calendar.MONTH]) -1
        currentMonthAddTime = calendarAddTime[Calendar.MONTH]
        return getDatesAddTime(mutableListOf())
    }
    private fun removeColonFromTimePicker(timePicker: TimePicker) {
        // TimePicker uses a layout with a colon TextView internally. We find and hide it.
        for (i in 0 until timePicker.childCount) {
            val viewGroup = timePicker.getChildAt(i) as? ViewGroup
            if (viewGroup != null) {
                for (j in 0 until viewGroup.childCount) {
                    val innerViewGroup = viewGroup.getChildAt(j) as? ViewGroup
                    if (innerViewGroup != null) {
                        for (k in 0 until innerViewGroup.childCount) {
                            val child = innerViewGroup.getChildAt(k)
                            // Hide the colon (TextView)
                            if (child is android.widget.TextView && child.text == ":") {
                                child.visibility = android.view.View.GONE
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getDatesAddTime(list: MutableList<java.util.Date>): List<java.util.Date> {
        // load dates of whole month
        calendarAddTime.set(Calendar.MONTH, currentMonthAddTime)
        calendarAddTime.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendarAddTime.time)
        while (currentMonthAddTime == calendarAddTime[Calendar.MONTH]) {
            calendarAddTime.add(Calendar.DATE, +1)
            if (calendarAddTime[Calendar.MONTH] == currentMonthAddTime)
                list.add(calendarAddTime.time)
        }
        calendarAddTime.add(Calendar.DATE, -1)
        return list
    }

}