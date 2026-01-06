package co.com.mypt.fragments

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CreateWorkoutFlow.SuperSet.CreateWorkoutSupersetActivity
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.CalendarSchedulePersonalWorkoutActivity
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.adapter.ChooseExcerciseAdapter
import co.com.mypt.adapter.MyScheduleAdapter
import co.com.mypt.model.MyScheduleModel
import co.com.mypt.model.SelectExcerciseModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarFragment : Fragment() {

    private val calendar = Calendar.getInstance()
    private val calendarAddTime = Calendar.getInstance()

    var eventDate = "26-11-2024"
    var eventDateAddTime = "26-11-2024"

    val sdf = SimpleDateFormat("dd-MM-yyyy")
    val sdfAddTime = SimpleDateFormat("dd-MM-yyyy")
    lateinit var fitness_planBottomSheetDialog: BottomSheetDialog
    lateinit var addTimeSLotBottomSheetDialog: BottomSheetDialog
    lateinit var sharedPreferences:SharedPreferences
    lateinit var edit:SharedPreferences.Editor
    lateinit var linearPlan:LinearLayout
    lateinit var linearNoSession:LinearLayout
    lateinit var singleRowCalendar:SingleRowCalendar
    var type=""
    var slot_Date=""
    var workoutsSelectedDate=""
    var am_pm = ""
    var slot_time=""
    private var selectedDate: Date? = null
    private var selectedPosition: Int = -1
    lateinit var recyclePlan: RecyclerView
    lateinit var scheduleTv: TextView
    var scheduleModelList :ArrayList<MyScheduleModel> = ArrayList()
    var selectedDateposition=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view= inflater.inflate(R.layout.fragment_calendar, container, false)
        singleRowCalendar = view.findViewById(R.id.main_single_row_calendar)

        fitness_planBottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        addTimeSLotBottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(requireActivity())
        edit=sharedPreferences.edit()
        calendar.time = Date()

        scheduleTv=view.findViewById(R.id.scheduleTv)
        recyclePlan=view.findViewById(R.id.recyclePlan)

        linearPlan=view.findViewById(R.id.linearPlan)
        linearNoSession=view.findViewById(R.id.linearNoSession)

        linearPlan.setOnClickListener{
            val bottomSheet = layoutInflater.inflate(R.layout.plan_your_fitness_layout, null)
            fitness_planBottomSheetDialog.setContentView(bottomSheet)
            val linearPlan=bottomSheet.findViewById<LinearLayout>(R.id.linearPlan)
            val linearHome=bottomSheet.findViewById<LinearLayout>(R.id.linearHome)
            val linearGym=bottomSheet.findViewById<LinearLayout>(R.id.linearGym)
            val linearRegular=bottomSheet.findViewById<LinearLayout>(R.id.linearRegular)
            val linearcircuit=bottomSheet.findViewById<LinearLayout>(R.id.linearcircuit)
            val linearSuperSet=bottomSheet.findViewById<LinearLayout>(R.id.linearSuperSet)
            var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
            val layoutParams = standard_bottom_sheet.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
            standard_bottom_sheet.layoutParams = layoutParams
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

            linearGym.setOnClickListener{
                edit.putString("typeWorkout","work").commit()
                startActivity(Intent(activity, GymListActivity::class.java))
            }
            linearHome.setOnClickListener{
                edit.putString("typeWorkout","home").commit()
                startActivity(Intent(activity, TrainersListActivity::class.java))
            }
            linearSuperSet.setOnClickListener{
                type="superset"
                slot_Date=""
                addTimeSlotAlert()
            }
            linearRegular.setOnClickListener{
                type="regular"
                slot_Date=""
                addTimeSlotAlert()
            }

            linearcircuit.setOnClickListener{
                type="circuit"
                slot_Date=""
                addTimeSlotAlert()
            }
            fitness_planBottomSheetDialog.setContentView(bottomSheet)

            val window = addTimeSLotBottomSheetDialog.window
            window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

            animateBottomSheet(fitness_planBottomSheetDialog)
            fitness_planBottomSheetDialog.show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val myCalendarViewManager = object : CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                Log.e("onCreateDateisSelected","$isSelected")
                return if (isSelected)
                    R.layout.fast_filling_selected_calendar_item
                else
                    R.layout.fast_filling_calendar_item

            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                val tv_date_calendar_item : TextView = holder.itemView.findViewById(R.id.tv_date_calendar_item)
                tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text = DateUtils.getDay3LettersName(date)

            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object : CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(
                isSelected: Boolean,
                position: Int,
                date: Date
            ) {
                if (isSelected) {
                    selectedDate = date
                    selectedPosition = position
                }
                super.whenSelectionChanged(isSelected, position, date)
            }

        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                val cal = Calendar.getInstance()
                cal.time = date

                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH)
                val date: Date? = sdf.parse(date.toString()) // Format to only yyyy-MM-dd if needed
                workoutsSelectedDate = sdfAddTime.format(date!!)
                lifecycle.coroutineScope.launch{
                    delay(200)
                    getMyWorkout("$workoutsSelectedDate")
                }
                return true

            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        singleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 30
            includeCurrentDate = true
            initialPositionIndex=0
            init()
            select(0)
        }

        addTimeSLotBottomSheetDialog.setOnDismissListener {
            try {
                singleRowCalendar.apply {
                    calendarViewManager = myCalendarViewManager
                    calendarChangesObserver = myCalendarChangesObserver
                    calendarSelectionManager = mySelectionManager
                    futureDaysCount = 30
                    includeCurrentDate = true
                    init()
                    if (selectedPosition != -1) {
                        select(selectedPosition)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        workoutsSelectedDate = sdfAddTime.format(Date().time)
        //getMyWorkout("$workoutsSelectedDate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(deleteWorkout, IntentFilter("deleteWorkout"), RECEIVER_EXPORTED)
        }else{
            requireActivity().registerReceiver(deleteWorkout, IntentFilter("deleteWorkout"))

        }
    }
    val deleteWorkout = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            getMyWorkout("$workoutsSelectedDate")


        }
    }

    private fun addTimeSlotAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.addtime_slot_bottomsheet, null)
        addTimeSLotBottomSheetDialog.setContentView(bottomSheet)

        val bottomSheetBehaviour = addTimeSLotBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheetBehaviour!!)
        // Enable wrap_content for default (expanded) height
        behavior.isFitToContents = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()  // Prevent full screen
        behavior.isHideable = true

        calendarAddTime.time = Date()

        val myCalendarViewManager1= object : CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                val cal = Calendar.getInstance()
                cal.time = date

                return if (isSelected)
                    R.layout.fast_filling_selected_calendar_item
                else
                    R.layout.fast_filling_calendar_item
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                val tv_date_calendar_item : TextView = holder.itemView.findViewById(R.id.tv_date_calendar_item)
                tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text = DateUtils.getDay3LettersName(date)
            }
        }

        // using calendar changes observer we can track changes in calendar
        val bottomCalendarChangesObserver= object : CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(
                isSelected: Boolean,
                position: Int,
                date: Date
            ) {
                super.whenSelectionChanged(isSelected, position, date)
            }

        }

        // selection manager is responsible for managing selection
        val mySelectionManager1 = object : CalendarSelectionManager {

            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                val cal = Calendar.getInstance()
                cal.time = date
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH)
                val date: Date? = sdf.parse(date.toString()) // Format to only yyyy-MM-dd if needed
                slot_Date = sdfAddTime.format(date!!)
                selectedDateposition=position
                Log.e("slotDate",slot_Date)

                return true

            }
        }

        val bottomSheetCalendar : SingleRowCalendar =bottomSheet.findViewById(R.id.main_single_row_calendar1)
        bottomSheetCalendar.apply {
            calendarViewManager = myCalendarViewManager1
            calendarChangesObserver = bottomCalendarChangesObserver
            calendarSelectionManager = mySelectionManager1
            futureDaysCount = 30
            includeCurrentDate = true
            init()
        }

        //timePicker code
        var timePicker=bottomSheet.findViewById<TimePicker>(R.id.timePicker)
        var lineardone=bottomSheet.findViewById<LinearLayout>(R.id.lineardone)

        lineardone.setOnClickListener{

            slot_time=""+timePicker.hour +":"+ timePicker.minute
            Log.e("slotTime",slot_time)
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // Define the desired output format for 12-hour time with AM/PM
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            // Parse the input 24-hour time string into a Date object
            val date = inputFormat.parse(slot_time)
            // Format the Date object into the 12-hour format
            Log.e("outputdate",""+outputFormat.format(date))
            if (slot_Date.equals("")){
                Toast.makeText(activity,"Select date", Toast.LENGTH_SHORT).show()
            }else{
                fitness_planBottomSheetDialog.dismiss()
                addTimeSLotBottomSheetDialog.dismiss()
                if (type.equals("superset")){
                    var intent=Intent(activity, CreateWorkoutSupersetActivity::class.java)
                    intent.putExtra("type",type)
                    intent.putExtra("workout_id","")
                    intent.putExtra("slotTime",outputFormat.format(date).toString().uppercase())
                    intent.putExtra("slotStartDate",slot_Date)
                    intent.putExtra("selectedDateposition",selectedDateposition)
                    startActivity(intent)
                }else{
                    var intent=Intent(activity,CalendarSchedulePersonalWorkoutActivity::class.java)
                    intent.putExtra("type",type)
                    intent.putExtra("workout_id","")
                    intent.putExtra("slotTime",outputFormat.format(date).toString().uppercase())
                    intent.putExtra("slotStartDate",slot_Date)
                    intent.putExtra("selectedDateposition",selectedDateposition)
                    startActivity(intent)
                }

            }
        }

        removeColonFromTimePicker(timePicker)

        val window = addTimeSLotBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        animateBottomSheet(addTimeSLotBottomSheetDialog)
        addTimeSLotBottomSheetDialog.show()
    }
    private fun removeColonFromTimePicker(timePicker: TimePicker) {
        for (i in 0 until timePicker.childCount) {
            val viewGroup = timePicker.getChildAt(i) as? ViewGroup
            if (viewGroup != null) {
                for (j in 0 until viewGroup.childCount) {
                    val innerViewGroup = viewGroup.getChildAt(j) as? ViewGroup
                    if (innerViewGroup != null) {
                        for (k in 0 until innerViewGroup.childCount) {
                            val child = innerViewGroup.getChildAt(k)
                            if (child is TextView && child.text == ":") {
                                child.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun getMyWorkout(date_number1: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext()!!,"")
        progressDialog.show()
        Log.e("APi,",""+ApiURL.myworkouts+date_number1+"&status=1")
        GetMethod(ApiURL.myworkouts+date_number1+"&status=1",requireContext()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                scheduleModelList.clear()

                Log.e("MyWorkoutResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                var jsonObject1 = jsonArray.optJSONObject(i)
                                var myScheduleModel= MyScheduleModel()
                                myScheduleModel.category = ""+jsonObject1.optString("category")
                                myScheduleModel.id = ""+jsonObject1.optString("id")
                                myScheduleModel.type = ""+jsonObject1.optString("type")
                                myScheduleModel.assigned_id = ""+jsonObject1.optString("assigned_id")
                                myScheduleModel.title = ""+jsonObject1.optString("title")
                                myScheduleModel.percentage = ""+jsonObject1.optString("percentage")

                                myScheduleModel.previewImage = ""+jsonObject1.optString("previewImage")
                                myScheduleModel.status = ""+jsonObject1.optString("status")
                                myScheduleModel.pt_score = ""+jsonObject1.optString("pt_score")
                                myScheduleModel.time= ""+jsonObject1.optString("time")
                                myScheduleModel.exercise_count= ""+jsonObject1.optString("exercise_count")
                                myScheduleModel.totalDuration= ""+jsonObject1.optString("totalDuration")
                                myScheduleModel.date= ""+jsonObject1.optString("date")
                                scheduleModelList.add(myScheduleModel)
                            }

                            Log.e("scheduleModelList.size",scheduleModelList.size.toString())
                            recyclePlan.adapter = MyScheduleAdapter(activity,scheduleModelList,type)
                            scheduleTv.visibility=View.VISIBLE
                            recyclePlan.visibility=View.VISIBLE
                            linearNoSession.visibility=View.GONE

                        }
                        else{
                            scheduleTv.visibility=View.GONE
                            recyclePlan.visibility=View.GONE
                            linearNoSession.visibility=View.VISIBLE
                        }





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
}