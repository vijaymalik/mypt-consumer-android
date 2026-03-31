package co.com.mypt.activities

import android.app.Dialog
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.SelectTimeAdapter
import co.com.mypt.model.AvailabilityModel
import co.com.mypt.model.SelectTImeModel
import com.android.volley.VolleyError
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SelectTime : AppCompatActivity() {
    var tax_rate=""
    var main_price=""
    var slot_id=""
    var time=""
    private lateinit var recyclerViewTime: RecyclerView
    private lateinit var im_night: ImageView
    private lateinit var animLayout: LinearLayout
    private lateinit var linearmrng: LinearLayout
    private lateinit var linernight: LinearLayout
    private lateinit var linearpay: LinearLayout
    private lateinit var linearNoData: LinearLayout
    private lateinit var createPackage: LinearLayout
    private lateinit var tvPayment: TextView
    private lateinit var tvprice: TextView
    private lateinit var tvPackage: TextView
    private lateinit var tvNodata: TextView
    private lateinit var tvCurrency: TextView
    private lateinit var im_mrng: ImageView
    lateinit var selectTimeAdapter: SelectTimeAdapter
    var selectTimeModelList: ArrayList<SelectTImeModel> = ArrayList()

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private var calInit = 0
    var eventDate = "26-11-2024"
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    var slotshift="morning"
    lateinit var headerLayout:LinearLayout
    var availabilityModelList = ArrayList<AvailabilityModel>()
    var isApiHit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_time)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        edit = sharedPreferences.edit()
        animLayout = findViewById(R.id.animLayout)
        createPackage = findViewById(R.id.createPackage)
        tvCurrency = findViewById(R.id.tvCurrency)
        im_night = findViewById(R.id.im_night)
        linearNoData = findViewById(R.id.linearNoData)
        linearmrng = findViewById(R.id.linearmrng)
        tvPackage = findViewById(R.id.tvPackage)
        linernight = findViewById(R.id.linernight)
        im_mrng = findViewById(R.id.im_mrng)
        tvPayment = findViewById(R.id.tvPayment)
        linearpay = findViewById(R.id.linearpay)
        recyclerViewTime = findViewById(R.id.recyclerViewTime)
        tvNodata = findViewById(R.id.tvNodata)
        tvprice = findViewById(R.id.tvprice)

        calendar.time = Date()
        currentMonth = intent.getIntExtra("month", calendar[Calendar.MONTH])
        headerLayout=findViewById(R.id.headerLayout)
        headerLayout.setOnClickListener{
            finish()
        }
        getSelectSlot(
            intent.getStringExtra("type"), intent.getStringExtra("trainer_id"),
            intent.getStringExtra("studio_id"),
            intent.getStringExtra("selectedDate").toString(),
            intent.getStringExtra("address_id"))

        im_night.setOnClickListener {
            slotshift="night"
            linernight.visibility = View.VISIBLE
            linearmrng.visibility = View.GONE
            im_night.visibility = View.GONE
            im_mrng.visibility = View.VISIBLE
            getSelectSlot(
                intent.getStringExtra("type"), intent.getStringExtra("trainer_id"),
                intent.getStringExtra("studio_id"), time,
                intent.getStringExtra("address_id"))

        }
        im_mrng.setOnClickListener {
            slotshift="morning"
            linernight.visibility = View.GONE
            linearmrng.visibility = View.VISIBLE
            im_night.visibility = View.VISIBLE
            im_mrng.visibility = View.GONE
            getSelectSlot(
                intent.getStringExtra("type"),
                intent.getStringExtra("trainer_id"),
                intent.getStringExtra("studio_id"),
                time,intent.getStringExtra("address_id")
            )

        }


        tvPackage.setOnClickListener {
            val intent = Intent(this, BestPlanTotalSessionWrapperActivity::class.java)
            // val intent = Intent(this, DuringSeesionActivity::class.java)
            //intent.putExtra("type",getIntent().getStringExtra("type"))
            intent.putExtra("slot_id",slot_id)
            intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("month",currentMonth)

            startActivity(intent)
        }
    }

    private fun getSelectSlot(
        type: String?,
        trainer_id: String?,
        studio_id: String?,
        time_: String,
        address_id: String?
    ) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getslots+trainer_id+"&type="+type+"&date="+time_+"&timing="+ slotshift+"&studio_id="+studio_id+"&address_id="+address_id
        }else{
            api= ApiURL.getslots+trainer_id+"&type="+"gym"+"&date="+time_+"&timing="+ slotshift+"&studio_id="+studio_id+"&address_id="+""
        }
        Log.e("SelectTimeAPi",api)
        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                selectTimeModelList.clear()
                Log.e("SelectTimeResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)

                    var jsonArraySlots=jsonObj.optJSONObject("data").optJSONArray("slots")
                    Log.e("size",""+jsonArraySlots.length())
                    if (jsonArraySlots.length()>0) {
                        isApiHit++
                        for(i in 0 until jsonArraySlots.length()){
                            val jsonObject1 = jsonArraySlots.optJSONObject(i)
                            var selectTImeModel=SelectTImeModel()
                            selectTImeModel.timeslot=jsonObject1.optString("time")
                            selectTImeModel.isBooked=jsonObject1.optString("isBooked")
                            selectTImeModel.id=jsonObject1.optString("id")
                            selectTimeModelList.add(selectTImeModel)
                        }
                        selectTimeAdapter = SelectTimeAdapter(this@SelectTime, selectTimeModelList)
                        recyclerViewTime.adapter = selectTimeAdapter
                        recyclerViewTime.visibility=View.VISIBLE
                        linearNoData.visibility=View.GONE
                        if (jsonObj.optJSONObject("data").optString("isPackage")=="true"){
                            Log.e("package","package")
                            createPackage.visibility = View.GONE
                            tvprice.text = "1 PT"
                            tvCurrency.visibility=View.GONE
                            tvPayment.setText(resources.getString(R.string.PROCEEDTOBOOK))

                            tvPayment.setOnClickListener {
                                var intent=Intent(this@SelectTime,BookingConfirmActivity::class.java)
                                intent.putExtra("type",getIntent().getStringExtra("type"))
                                intent.putExtra("selectBookOption","normalBookSlot")
                                intent.putExtra("slot_id",slot_id)
                                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                intent.putExtra("transaction_id","")
                                intent.putExtra("selectedPaymentOption","")
                                // edit.remove("typeWorkout").apply()
                                /* val intent = Intent(this, BookingConfirmActivity::class.java)
                                 intent.putExtra("type",getIntent().getStringExtra("type"))
                                 intent.putExtra("selectBookOption","normalBookSlot")
                                 intent.putExtra("slot_id",slot_id)
                                 intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                                 intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                                 intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                 startActivity(intent)*/
                                startActivity(intent)
                            }
                        }else{
                            Log.e("package","packagecheck")
                            tvCurrency.visibility=View.VISIBLE
                            tvprice.text = jsonObj.optJSONObject("data").optString("price").replace("AED","")
                            main_price=jsonObj.optJSONObject("data").optString("main_price")
                            tax_rate=jsonObj.optJSONObject("data").optString("tax_rate")
                            tvPayment.setText(resources.getString(R.string.make_payment))

                            tvPayment.setOnClickListener {
                                var intent=Intent(this@SelectTime,PaymentSelectionActivity::class.java)
                                intent.putExtra("type",getIntent().getStringExtra("type"))
                                intent.putExtra("selectBookOption","normalBookSlot")
                                intent.putExtra("slot_id",slot_id)
                                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                intent.putExtra("price",tvprice.text.toString())
                                intent.putExtra("tax_rate",tax_rate)
                                intent.putExtra("main_price",main_price)
                                // edit.remove("typeWorkout").apply()
                                /* val intent = Intent(this, BookingConfirmActivity::class.java)
                                 intent.putExtra("type",getIntent().getStringExtra("type"))
                                 intent.putExtra("selectBookOption","normalBookSlot")
                                 intent.putExtra("slot_id",slot_id)
                                 intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                                 intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                                 intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                 startActivity(intent)*/
                                startActivity(intent)
                            }
                        }

                    }
                    else{
                        linearNoData.visibility=View.VISIBLE
                        recyclerViewTime.visibility=View.GONE
                        linearpay.visibility =View.GONE
                    }

                    if(calInit == 0 ){
                        availabilityModelList.clear()
                        val availability = jsonObj.optJSONObject("data").optJSONArray("availabilityStatus")
                        for(i in 0 until availability.length()){
                            val jsonObject1 = availability.optJSONObject(i)
                            var availabilityModel= AvailabilityModel()
                            availabilityModel.date = jsonObject1.optString("date")
                            availabilityModel.status = jsonObject1.optString("status")
                            availabilityModelList.add(availabilityModel)
                        }

                        val myCalendarViewManager = object :
                            CalendarViewManager {
                            override fun setCalendarViewResourceId(
                                position: Int,
                                date: Date,
                                isSelected: Boolean
                            ): Int {
                                // set date to calendar according to position where we are
                                val cal = Calendar.getInstance()
                                cal.time = date
                                Log.e("availabilityModelList","${availabilityModelList.size}")


                                return if (isSelected)
                                    when (availabilityModelList[position].status) {
                                        "Closed" -> {
                                            return R.layout.closed_selected_calendar_item
                                        }
                                        "Available" -> {
                                            return R.layout.available_selected_calendar_item
                                        }
                                        "Fully Booked" -> {
                                            return R.layout.booked_selected_calendar_item
                                        }
                                        else -> {
                                            R.layout.fast_filling_selected_calendar_item
                                        }
                                    }
                                else
                                // here we return items which are not selected
                                    if (availabilityModelList[position].status == "Closed"){
                                        return R.layout.closed_calendar_item
                                    }else if (availabilityModelList[position].status == "Available"){
                                        return R.layout.available_calendar_item
                                    }else if (availabilityModelList[position].status == "Fully Booked"){
                                        return R.layout.booked_calendar_item
                                    }else{
                                        R.layout.fast_filling_calendar_item
                                    }

                                // NOTE: if we don't want to do it this way, we can simply change color of background
                                // in bindDataToCalendarView method
                            }

                            override fun bindDataToCalendarView(
                                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                                date: Date,
                                position: Int,
                                isSelected: Boolean
                            ) {
                                // using this method we can bind data to calendar view
                                // good practice is if all views in layout have same IDs in all item views
                                val tv_date_calendar_item: TextView =
                                    holder.itemView.findViewById(R.id.tv_date_calendar_item)
                                tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text =
                                    DateUtils.getDay3LettersName(date)

                            }
                        }

                        // using calendar changes observer we can track changes in calendar
                        val myCalendarChangesObserver = object :
                            CalendarChangesObserver {

                        }

                        // selection manager is responsible for managing selection
                        val mySelectionManager = object : CalendarSelectionManager {
                            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                                // set date to calendar according to position
                                val cal = Calendar.getInstance()
                                cal.time = date
                                val sdf1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                time=sdf1.format(date)
                                if(calInit > 0){
                                    getSelectSlot(
                                        intent.getStringExtra("type"),
                                        intent.getStringExtra("trainer_id"),
                                        intent.getStringExtra("studio_id"),
                                        time,
                                        intent.getStringExtra("address_id")
                                    )
                                }
                                return sdf.parse(sdf.format(cal.time)) >= sdf.parse(sdf.format(Calendar.getInstance().time))

                            }
                        }

                        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
                        val singleRowCalendar: SingleRowCalendar = findViewById(R.id.main_single_row_calendar)
                        singleRowCalendar.apply {
                            calendarViewManager = myCalendarViewManager
                            calendarChangesObserver = myCalendarChangesObserver
                            calendarSelectionManager = mySelectionManager
                            setDates(getFutureDatesOfCurrentMonth())
                            init()
                            select(intent.getIntExtra("day", 0))
                        }
                        singleRowCalendar.scrollToPosition(intent.getIntExtra("day", 0))
                        calInit++

                        singleRowCalendar.startAnimation(
                            AnimationUtils.loadAnimation(
                                applicationContext,
                                R.anim.left_to_right
                            )
                        )
                        animLayout.visibility = View.VISIBLE
                        animLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                applicationContext,
                                R.anim.slide_from_bottom_fast
                            )
                        )
                    }

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
    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = intent.getIntExtra("month", calendar[Calendar.MONTH]) - 1
        //currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<Date>): List<Date> {
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

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(slot, IntentFilter("select_time"), RECEIVER_EXPORTED)
        } else {
            registerReceiver(slot, IntentFilter("select_time"))

        }
    }

    val slot = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.getStringExtra("selectposition").equals("slot")) {
                if (linearpay.visibility == View.GONE) {
                    slot_id=""+intent.getStringExtra("slot_id")
                    linearpay.visibility = View.VISIBLE
                    linearpay.startAnimation(
                        AnimationUtils.loadAnimation(
                            applicationContext,
                            R.anim.slide_from_bottom_slow
                        )
                    )
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(slot)
    }
}