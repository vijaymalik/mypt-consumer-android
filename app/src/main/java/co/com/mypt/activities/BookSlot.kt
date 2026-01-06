package co.com.mypt.activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CalendarSelectedDate.SelectedDateDecorator
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.calendarUtils.DisabledOtherDatesDecorator
import co.com.mypt.calendarUtils.EventMultiColorDecorator
import com.android.volley.VolleyError
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.json.JSONObject
import org.threeten.bp.DayOfWeek
import java.text.DateFormatSymbols
import java.util.Calendar


class BookSlot : AppCompatActivity(){

    lateinit var calendarView : MaterialCalendarView
    lateinit var monthName : TextView
    lateinit var sharedPreferences:SharedPreferences
    lateinit var headerLayout: LinearLayout
    lateinit var imLeft: ImageView
    lateinit var imRight: ImageView

    var currentMonth=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_slot)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        calendarView = findViewById(R.id.calendarView)
        monthName = findViewById(R.id.monthName)
        headerLayout=findViewById(R.id.headerLayout)
        imLeft=findViewById(R.id.imLeft)
        imRight=findViewById(R.id.imRight)
        headerLayout.setOnClickListener{
            finish()
        }

        calendarView.state().edit()
            .setMinimumDate(CalendarDay.today()) // Disables selection of past dates
            .commit()


        val today = Calendar.getInstance()
        currentMonth = 0+ today.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based


        getSLot(intent.getStringExtra("type"),intent.getStringExtra("trainer_id"),intent.getStringExtra("studio_id"),currentMonth,)

        var currentYear = today.get(Calendar.YEAR)
        monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"

        // Create a custom drawable for the rectangle
        val rectangleDrawable = ContextCompat.getDrawable(this, R.drawable.rectangle_background)

        // Create the custom decorator
        val decorator = SelectedDateDecorator(rectangleDrawable!!)

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
        // Add the decorator to the calendar
        calendarView.addDecorator(decorator)

        calendarView.addDecorator(DisabledOtherDatesDecorator(
            currentMonth,
            currentYear,
            resources.getColor(R.color.smallTextcolor),
            today.get(Calendar.DAY_OF_MONTH)
        ))

        calendarView.topbarVisible = false

        calendarView.setOnMonthChangedListener { _, date ->
            // Update current month and year
            currentMonth = date.month
            currentYear = date.year
            monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"
            // Refresh decorators
            calendarView.removeDecorators() // Clear previous decorators
            calendarView.addDecorator(decorator) // Re-add event dots
            calendarView.addDecorator(
                DisabledOtherDatesDecorator(
                    currentMonth,
                    currentYear,
                    resources.getColor(R.color.smallTextcolor),
                    date.day
                )
            ) // Update disabled dates
            Log.e("currentMonth",""+currentMonth)
            getSLot(intent.getStringExtra("type"),intent.getStringExtra("trainer_id"),intent.getStringExtra("studio_id"),currentMonth)


        }
        imRight.setOnClickListener{
            calendarView.goToNext()

        }
        imLeft.setOnClickListener{
            calendarView.goToPrevious()

        }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            decorator.setSelectedDate(date)
            decorator.setSelectedDate(date)
            widget.invalidateDecorators()
            val intent = Intent(this, SelectTime::class.java)
            intent.putExtra("selectedDate","${date.year}-${date.month}-${date.day}")
            intent.putExtra("day",date.day-1)
            intent.putExtra("month",date.month)
            intent.putExtra("type",getIntent().getStringExtra("type"))
            intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            startActivity(intent)

            /*if(sharedPreferences.getString("calendartype","")!!.equals("TraineratGym")){
                val intent = Intent(this, SelectTimefromBookaTrainerCalendarActivity2::class.java)
                intent.putExtra("selectedDate","${date.day}-${date.month}-${date.year}")
                intent.putExtra("day",date.day-1)
                intent.putExtra("month",date.month)
                startActivity(intent)
            }else{
                val intent = Intent(this, SelectTime::class.java)
                intent.putExtra("selectedDate","${date.day}-${date.month}-${date.year}")
                intent.putExtra("day",date.day-1)
                intent.putExtra("month",date.month)
                startActivity(intent)
            }
*/

        }
    }
    private fun getSLot(type: String?, trainer_id: String?, studio_id: String?, currentMonth: Int) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var selectedMonth=""
        if (currentMonth<10){
            selectedMonth = "0$currentMonth" // Calendar.MONTH is 0-based
        }else{
            selectedMonth ="$currentMonth"// Calendar.MONTH is 0-based

        }
       var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api=ApiURL.get_availability+type+"&trainer_id="+trainer_id+"&studio_id="+studio_id+"&month="+selectedMonth+"&address_id="+ intent.getStringExtra("address_id")
        }else{
            api=ApiURL.get_availability+"gym"+"&trainer_id="+trainer_id+"&studio_id="+studio_id+"&month="+selectedMonth+"&address_id="+""

        }
        Log.e("AvailabiltyUrl",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("AvailabiltyResponse",data.toString())
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

                            val eventDecorator = EventMultiColorDecorator(selectColor, listOf(CalendarDay.from(year.toInt(), month.toInt(), day.toInt())))
                            eventDecorators.add(eventDecorator)
                        }
                    }

                    eventDecorators.forEach { calendarView.addDecorator(it) }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }
}