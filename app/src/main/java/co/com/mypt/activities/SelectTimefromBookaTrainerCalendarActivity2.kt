package co.com.mypt.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.CalendarBookTrainerAdapter
import co.com.mypt.adapter.SelectTimeCalendarAdapter
import co.com.mypt.calendarUtils.Date
import co.com.mypt.calendarUtils.Event
import co.com.mypt.model.SelectTImeModel
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar

class SelectTimefromBookaTrainerCalendarActivity2 : AppCompatActivity() {
    private lateinit var recyclerViewTime: RecyclerView
    private lateinit var im_night: ImageView
    private lateinit var linearmrng: LinearLayout
    private lateinit var linernight: LinearLayout
    private lateinit var linearpay: LinearLayout
    private lateinit var tvPayment: TextView
    private lateinit var tvPackage: TextView
    private lateinit var im_mrng: ImageView
    private lateinit var calendarAdapter: CalendarBookTrainerAdapter
    lateinit var selectTimeAdapter: SelectTimeCalendarAdapter
    private lateinit var calendarDates: List<Date>
    private lateinit var events: List<Event>
    var selectTimeModelList : ArrayList<SelectTImeModel> = ArrayList()

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    var eventDate = "26-11-2024"
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    var firstTime = 0
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select_timefrom_booka_trainer_calendar2)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)
        edit=sharedPreferences.edit()
        im_night = findViewById(R.id.im_night)
        linearmrng = findViewById(R.id.linearmrng)
        linernight = findViewById(R.id.linernight)
        im_mrng = findViewById(R.id.im_mrng)
        tvPayment = findViewById(R.id.tvPayment)
        linearpay = findViewById(R.id.linearpay)
        recyclerViewTime = findViewById(R.id.recyclerViewTime)

        calendar.time = java.util.Date()
        currentMonth = intent.getIntExtra("month",calendar[Calendar.MONTH])

        im_night.setOnClickListener{
            linernight.visibility= View.VISIBLE
            linearmrng.visibility= View.GONE
            im_night.visibility= View.GONE
            im_mrng.visibility= View.VISIBLE
        }
        im_mrng.setOnClickListener{
            linernight.visibility= View.GONE
            linearmrng.visibility= View.VISIBLE
            im_night.visibility= View.VISIBLE
            im_mrng.visibility= View.GONE
        }
        tvPayment.setOnClickListener{

            val intent = Intent(this, ReviewPackageBookTrainerCalendarActivity::class.java)
            startActivity(intent)
        }


        selectTimeAdapter = SelectTimeCalendarAdapter(this, selectTimeModelList)

        recyclerViewTime.adapter = selectTimeAdapter

        // Initialize the dates and events
        calendarDates = getCalendarDates()
        events = getEvents()

        // Set up the RecyclerView with the adapter
        calendarAdapter = CalendarBookTrainerAdapter(this, calendarDates, events)

        for (i in 0..9) {
            var selectTimeMode=SelectTImeModel()
            selectTimeMode.timeslot="11:00 - 12:00"
            selectTimeModelList.add(selectTimeMode)
        }

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
                return sdf.parse(sdf.format(cal.time)) >= sdf.parse(sdf.format(Calendar.getInstance().time))

            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar : SingleRowCalendar = findViewById(R.id.main_single_row_calendar)
        singleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            init()
            select(intent.getIntExtra("day",0))
        }
        singleRowCalendar.scrollToPosition(intent.getIntExtra("day",0))


    }
    private fun getDatesOfNextMonth(): List<java.util.Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<java.util.Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }

    private fun getFutureDatesOfCurrentMonth(): List<java.util.Date> {
        // get all next dates of current month
        currentMonth = intent.getIntExtra("month",calendar[Calendar.MONTH]) -1
        //currentMonth = calendar[Calendar.MONTH]
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

    private fun getCalendarDates(): List<Date> {
        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance()

        // Adding some example dates
        for (i in 1..30) {
            val date = Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), i)
            dates.add(date)
        }
        return dates
    }

    // Method to fetch events (can be from a database, API, or hardcoded)
    private fun getEvents(): List<Event> {
        val eventList = mutableListOf<Event>()
        val event1 = Event(Date(2024, 11, 5), "Meeting")
        val event2 = Event(Date(2024, 11, 10), "Conference")
        eventList.add(event1)
        eventList.add(event2)
        return eventList
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(slot, IntentFilter("selecttime1"), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(slot, IntentFilter("selecttime1"))
        }

    }
    val slot = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(linearpay.visibility == View.GONE){
                linearpay.visibility=View.VISIBLE
                linearpay.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_bottom_slow))
            }
        }
    }
}