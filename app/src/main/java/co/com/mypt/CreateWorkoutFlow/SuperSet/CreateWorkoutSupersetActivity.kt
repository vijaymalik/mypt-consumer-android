package co.com.mypt.CreateWorkoutFlow.SuperSet

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.JsonPostRequest
import co.com.mypt.Api.JsonResponseData
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.ChooseExcerciseActivity
import co.com.mypt.adapter.CalendarAdapterAddTimeSlot
import co.com.mypt.adapter.ChooseExcerciseforSuperSetAdapter
import co.com.mypt.adapter.ReminderAdapter
import co.com.mypt.adapter.SuperSetListAdapter
import co.com.mypt.adapter.SupersetGridAdapter
import co.com.mypt.calendarUtils.Event
import co.com.mypt.model.RemindModel
import co.com.mypt.model.SelectExcerciseModel
import co.com.mypt.model.SuperSetListModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Locale.getDefault
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class CreateWorkoutSupersetActivity : AppCompatActivity() {
    var type=""
    var end_date=""
    lateinit var recyclerExercise:RecyclerView
    lateinit var linearAdd:LinearLayout
    lateinit var linearAddRest:LinearLayout
    lateinit var card_how_to: CardView

    lateinit var linearReps:LinearLayout
    lateinit var linearHeader:LinearLayout
    lateinit var chooseExcerciseAdapter: ChooseExcerciseforSuperSetAdapter
    var excerxiseModelList :ArrayList<SelectExcerciseModel> = ArrayList()
    var reminderModelList :ArrayList<RemindModel> = ArrayList()
    var supersetModelList :ArrayList<SuperSetListModel> = ArrayList()
    var selectdays :ArrayList<String> = ArrayList()
    lateinit var tvTime:TextView
    lateinit var tvCalories:TextView
    lateinit var tvSlotTime:TextView
    lateinit var tvExercises:TextView
    lateinit var tvEndDate:TextView
    lateinit var tvsecondActiviy:TextView
    lateinit var imSecond: ImageView

    lateinit var tvRemindDone:TextView
    lateinit var tvValue:TextView
    lateinit var textSelectExercise:TextView

    lateinit var ed1:TextView
    lateinit var tvSaveExercise:TextView
    lateinit var howToText:TextView
    lateinit var howToText1:TextView
    lateinit var howToText2:TextView
    lateinit var howToText3:TextView

    lateinit var workoutType:TextView
    lateinit var tvCreateGroup:TextView
    lateinit var edWorkoutname: EditText
    lateinit var linearNotify:LinearLayout
    lateinit var linearActivityRest:LinearLayout
    lateinit var linearAddNewSuperSet:LinearLayout

    lateinit var remindRecycler: RecyclerView
    lateinit var supersetRecycler: RecyclerView
    lateinit var recyclerSuperSetList: RecyclerView
    lateinit var standard_bottom_sheet:LinearLayout
    lateinit var scrollView: NestedScrollView

    var workout_id=""

    lateinit var imclose: ImageView

    lateinit var notifyBottomSheetDialog:BottomSheetDialog
    lateinit var rulerBottomSheetDialog:BottomSheetDialog
    lateinit var repsBottomSheetDialog:BottomSheetDialog
    lateinit var createSupersetBottomSheetDialog:BottomSheetDialog
    var datePickerDialog: DatePickerDialog? = null
    val items = listOf("Weeks","Days")
    lateinit var addTimeSLotBottomSheetDialog: BottomSheetDialog
    private val calendarAddTime = Calendar.getInstance()
    private var currentMonthAddTime = 0
    var eventDateAddTime = "26-11-2024"
    private lateinit var calendarDatesAddTime: List<co.com.mypt.calendarUtils.Date>
    private lateinit var eventsAdd: List<Event>
    val sdfAddTime = SimpleDateFormat("dd-MM-yyyy")
    var checkEnd=""
    var remind_id=""
    var totalCalories = 0
    var superSetCount = 1
    private var count: Int = 3

    private var showAllOptions = false
    var selectedExercisemodelLists = ArrayList<SelectExcerciseModel>()
    var supersetAdapter:SupersetGridAdapter ? =null
    var new_set=false
    var startDate=""

    var total_duration=0
    lateinit var checkEndON: CheckBox
    lateinit var check5: CheckBox
    lateinit var tvm:TextView
    lateinit var tvf:TextView
    lateinit var tvS:TextView
    lateinit var tvSu:TextView
    lateinit var tvw:TextView
    lateinit var tvTh:TextView
    lateinit var tvT:TextView
    var selectedtime=""
    var selectedDateposition=0
    var am_pm = ""
    val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedItem = result.data?.getParcelableExtra<SelectExcerciseModel>("updatedItem")
            updatedItem?.let { updateModelList(it) }
        }
    }

    private fun updateModelList(updatedItem: SelectExcerciseModel) {
        val index = excerxiseModelList.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            excerxiseModelList[index] = updatedItem
            chooseExcerciseAdapter.notifyItemChanged(index)
            updateGridList()
        }
    }
    val steps = listOf(
        "Add exercise",
        "Create a superset, and choose form the added exercise",
        "Define number of sets for each superset",
        "Add rest period"
    )

    fun displaySimpleOrderedListWithSpaces(
        textView: TextView,
        textView1: TextView,
        textView2: TextView,
        textView3: TextView,
        textArray: List<String>,
        context: CreateWorkoutSupersetActivity
    ) {
        val tempPaint = TextPaint()
        tempPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            14f,
            context.resources.displayMetrics
        )
        val gapAfterNumberDp = 4f
        val gapAfterNumberPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            gapAfterNumberDp,
            context.resources.displayMetrics
        )

        textArray.forEachIndexed { index, itemText ->
            val content = SpannableStringBuilder()
            val contentStart = content.length
            val numberString = "${index + 1}.  "
            content.append(numberString)
            content.append(itemText)
            val contentEnd = content.length

            val numberStringWidth = tempPaint.measureText(numberString)
            val indentForRestBasedOnNumber = (numberStringWidth + gapAfterNumberPixels).toInt()

            content.setSpan(
                LeadingMarginSpan.Standard(0, indentForRestBasedOnNumber),
                contentStart,
                contentEnd,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )

            if (index ==0) {
                textView.text = content
            }
            if (index ==1) {
                textView1.text = content
            }
            if (index ==2) {
                textView2.text = content
            }
            if (index ==3) {
                textView3.text = content
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout_superset)
        workout_id=""+intent.getStringExtra("workout_id")
        addTimeSLotBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        imclose = findViewById(R.id.imclose)
        scrollView = findViewById(R.id.scrollView)
        linearAddNewSuperSet = findViewById(R.id.linearAddNewSuperSet)
        card_how_to = findViewById(R.id.card_how_to)
        imSecond = findViewById(R.id.imSecond)
        tvsecondActiviy = findViewById(R.id.tvsecondActiviy)
        linearActivityRest = findViewById(R.id.linearActivityRest)
        recyclerExercise=findViewById(R.id.recyclerExercise)
        linearAdd=findViewById(R.id.linearAdd)
        workoutType=findViewById(R.id.workoutType)
        linearReps=findViewById(R.id.linearReps)
        tvSaveExercise=findViewById(R.id.tvSaveExercise)
        edWorkoutname=findViewById(R.id.edWorkoutname)
        linearHeader=findViewById(R.id.linearHeader)
        linearAddRest=findViewById(R.id.linearAddRest)
        howToText=findViewById(R.id.howToText)
        howToText1=findViewById(R.id.howToText1)
        howToText2=findViewById(R.id.howToText2)
        howToText3=findViewById(R.id.howToText3)
        displaySimpleOrderedListWithSpaces(howToText,howToText1,howToText2,howToText3,steps,context = this)

        tvTime=findViewById(R.id.tvTime)
        tvSlotTime=findViewById(R.id.tvSlotTime)
        tvCalories =findViewById(R.id.tvCalories)
        tvExercises =findViewById(R.id.tvExercises)
        textSelectExercise =findViewById(R.id.textSelectExercise)
        linearNotify =findViewById(R.id.linearNotify)
        recyclerSuperSetList =findViewById(R.id.recyclerSuperSetList)


        selectedtime=""+intent.getStringExtra("slotTime")
        selectedDateposition=intent.getIntExtra("selectedDateposition",0)
        startDate = intent.getStringExtra("slotStartDate").toString()
        tvSlotTime.text = startDate+","+selectedtime
        tvSlotTime.setOnClickListener {
            addTimeSLotBottomSheetDialog.show()
        }
        linearHeader.setOnClickListener {
            finish()
        }
        linearAddRest.setOnClickListener {
            if (supersetModelList[supersetModelList.size-1].type == "rest") {
                Toast.makeText(applicationContext,"Rest is already added", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            type="rest"
            new_set=true
            createWorkoutData()
        }
        linearAddNewSuperSet.setOnClickListener {
            new_set=true
            tvSaveExercise.text = "Create superset Group"
            recyclerExercise.visibility=View.VISIBLE
            recyclerSuperSetList.visibility=View.GONE
            tvSaveExercise.background = resources.getDrawable(R.drawable.rectangle_btn)
            tvSaveExercise.setTextColor(resources.getColor(R.color.white))
            selectedExercisemodelLists.clear()
            chooseExcerciseAdapter= ChooseExcerciseforSuperSetAdapter(
                excerxiseModelList,
                this@CreateWorkoutSupersetActivity,
                selectedExercisemodelLists,
            )
            recyclerExercise.adapter=chooseExcerciseAdapter
            linearAddNewSuperSet.visibility = View.GONE
            linearAddRest.visibility = View.GONE
            linearAdd.visibility = View.VISIBLE

        }

        imSecond.setOnClickListener {
            showEditRestDialog()
        }
        imclose.setOnClickListener {
            linearActivityRest.visibility= View.GONE
        }
        workoutType.text = intent.getStringExtra("type")?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }

        notifyBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        repsBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        createSupersetBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        rulerBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        addTimeSlotAlert()
        createNotifyAlert()
        createRepsAlert()

        if (workout_id!==""){
            showAllOptions = true
            getSuperSetListData(workout_id)
        }else
            getRemindMeData()

        if (excerxiseModelList.isNotEmpty()){
            tvExercises.setText(excerxiseModelList.size)
        }else{
            tvExercises.text = "0"
        }

        linearAdd.setOnClickListener{
            var intent= Intent(this,ChooseExcerciseActivity::class.java)
            intent.putExtra("type","superset")
            intent.putParcelableArrayListExtra("exercise_listfromAdd", excerxiseModelList)
            startActivity(intent)
        }

        tvSaveExercise.setOnClickListener{
            if (selectedExercisemodelLists.size>1 || tvSaveExercise.text.toString().contains("Save")){
                if (edWorkoutname.text.toString().trim()==""){
                    edWorkoutname.requestFocus()
                    //scrollView.scrollTo(0,0)
                    edWorkoutname.error = "Please enter workout name"
                    return@setOnClickListener
                }
                if (checkEnd.equals("checkEndDate") && tvEndDate.text.toString().equals("")){
                    Toast.makeText(applicationContext,"Select end date",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (selectdays.isEmpty() && checkEnd.equals("")){
                    Toast.makeText(applicationContext,"Please select reps frequency",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (remind_id.equals("")){
                    Toast.makeText(applicationContext,"Please select reminder Option", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (tvSaveExercise.text.toString().contains("Save")){
                    type="Save"
                    new_set = false
                    createWorkoutData()
                    return@setOnClickListener
                }

                if (excerxiseModelList.size==0){
                    Toast.makeText(applicationContext,"Please add exercise", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                type=""
                createSuperSetAlert()
                val sheet = createSupersetBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                sheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true

                    // Set height to match parent
                    it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }
            else{
                Toast.makeText(applicationContext,"Please select at least two exercise to create a superset",
                    Toast.LENGTH_LONG).show()
            }


        }
        linearReps.setOnClickListener{
            repsBottomSheetDialog.show()
        }
        linearNotify.setOnClickListener{
            notifyBottomSheetDialog.show()
        }


        textShader(tvTime)
        textShader(tvCalories)
        textShader(tvExercises)
    }

    private fun createWorkoutData() {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("id",workout_id)
            jsonObject.put("is_set_new",new_set)
            jsonObject.put("name",edWorkoutname.text.toString())
            jsonObject.put("description","")
            jsonObject.put("type", "superset")
            jsonObject.put("category_id","1")
            jsonObject.put("repeat_duration",ed1.text.toString())
            jsonObject.put("repeat_days", selectdays.joinToString())
            jsonObject.put("start_date",startDate)
            jsonObject.put("end_date", end_date)
            jsonObject.put("time",selectedtime.uppercase())
            jsonObject.put("remind_me", remind_id)
            var superserjsonArray= JSONArray()
            if (tvSaveExercise.text.toString().contains("Create superset Group")){
                var jsonArray= JSONArray()
                for(i in 0 until selectedExercisemodelLists.size){
                    var jsonObject1= JSONObject()
                    jsonObject1.put("id",selectedExercisemodelLists[i].id)
                    jsonObject1.put("type","exercise")
                    jsonObject1.put("sets",tvValue.text.toString())
                    jsonObject1.put("reps",selectedExercisemodelLists[i].raps)
                    jsonObject1.put("time_type","2")
                    jsonArray.put(jsonObject1)
                }
                var supersetJson=JSONObject()
                supersetJson.put("type","superset")
                supersetJson.put("exercises",jsonArray)
                superserjsonArray.put(supersetJson)
                showAllOptions = false
            }
            else if (type.equals("rest")){
                    var supersetJson=JSONObject()
                    supersetJson.put("type","rest")
                    supersetJson.put("duration","60")

                    superserjsonArray.put(supersetJson)
                }
            else {
                for(i in 0 until supersetModelList.size){
                    var supersetJson=JSONObject()
                    if (supersetModelList[i].type.equals("rest")){
                        supersetJson.put("type","rest")
                        supersetJson.put("duration",supersetModelList[i].duration)
                        superserjsonArray.put(supersetJson)
                    }else{
                        var jsonArray= JSONArray()
                        for(j in 0 until supersetModelList[i].exercises.length()){
                            jsonArray.put(supersetModelList[i].exercises[j])
                        }
                        supersetJson.put("type","superset")
                        supersetJson.put("exercises",jsonArray)
                        superserjsonArray.put(supersetJson)
                    }
                }
            }
            jsonObject.put("supersets",superserjsonArray)
            Log.e("createSuperSetWorkoutParam", "" + jsonObject)

            val progressDialog: Dialog = ProgressDialog.progressDialog(this@CreateWorkoutSupersetActivity,"")
            progressDialog.show()

            JsonPostRequest(ApiURL.createworkout, jsonObject, this).startPostMethod(object :
                JsonResponseData {

                override fun responseObject(response: JSONObject?) {
                    createSupersetBottomSheetDialog.dismiss()
                    progressDialog.dismiss()

                    Log.e("createWorkoutResp", "" + response)

                    if(response!!.optBoolean("status")){
                        Toast.makeText(applicationContext,""+response.optString("msg"), Toast.LENGTH_LONG).show()
                        if (type.equals("Save")){
                            finish()
                            return
                        }
                        workout_id=response.optJSONObject("data").optString("workout_id")
                        getSuperSetListData(workout_id)
                    }

                }

                override fun error(error: VolleyError?) {
                    error!!.printStackTrace()
                    progressDialog.dismiss()
                }



            })

        }
        catch (e:Exception){
            e.printStackTrace()
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

        //horizonatal calendar
        calendarAddTime.time = Date()
        calendarDatesAddTime = getCalendarDatesAddTImeSlot()
        eventsAdd = getEvents()
        CalendarAdapterAddTimeSlot(this, calendarDatesAddTime, eventsAdd)
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
                // if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view
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

        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH)
                val date: Date? = sdf.parse(date.toString()) // Format to only yyyy-MM-dd if needed
                startDate = sdfAddTime.format(date!!)
                // selectedDateposition=position
                Log.e("slotDate",startDate)
                return true
            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar : SingleRowCalendar =bottomSheet.findViewById(R.id.main_single_row_calendar1)
        singleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 30
            includeCurrentDate = true
            init()
            select(selectedDateposition)
        }

        //timePicker code
        var timePicker=bottomSheet.findViewById<TimePicker>(R.id.timePicker)
        var lineardone=bottomSheet.findViewById<LinearLayout>(R.id.lineardone)

        val timePartsAmPm = extractHour24MinuteFromHhMmA(selectedtime)

        if (timePartsAmPm != null) {
            val hour = timePartsAmPm.first // This will be in 24-hour format (e.g., 15 for 3 PM)
            val minute = timePartsAmPm.second
            timePicker.hour=hour
            timePicker.minute=minute
            Log.d("TimeExtraction", "Parsed AM/PM: Hour=$hour (24h), Minute=$minute")
            // Use 'hour' (which is 24-hour) and 'minute' to set your TimePicker
        } else {
            Log.e("TimeExtraction", "Failed to parse AM/PM time string: $selectedtime")
        }


        lineardone.setOnClickListener{
            selectedtime=""+timePicker.hour +":"+ timePicker.minute
            Log.e("slotTime",selectedtime)
            //  tvSlotTime.text = start_Date+","+selectedtime

            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // Define the desired output format for 12-hour time with AM/PM
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            // Parse the input 24-hour time string into a Date object
            val date = inputFormat.parse(selectedtime)
            // Format the Date object into the 12-hour format
            Log.e("outputdate",""+outputFormat.format(date))
            selectedtime=outputFormat.format(date)

            tvSlotTime.setText(startDate+","+selectedtime)

            addTimeSLotBottomSheetDialog.dismiss()
        }
        timePicker.setOnTimeChangedListener { _, hour, minute -> var hour = hour
            // AM_PM decider logic
            when {hour == 0 -> { hour += 12
                am_pm = "AM"
            }
                hour == 12 -> am_pm = "PM"
                hour > 12 -> { hour -= 12
                    am_pm = "PM"
                }
                else -> am_pm = "AM"
            }
        }

        removeColonFromTimePicker(timePicker)
        val window = addTimeSLotBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        animateBottomSheet(addTimeSLotBottomSheetDialog)

    }
    fun extractHour24MinuteFromHhMmA(timeString: String?): Pair<Int, Int>? { // Added null safety for input
        if (timeString.isNullOrEmpty()) {
            return null
        }
        return try {
            // Use "hh" for 12-hour format and "a" for AM/PM marker for parsing
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf.parse(timeString)

            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                // Get the hour in 24-hour format directly
                val hour24 = calendar.get(Calendar.HOUR_OF_DAY) // This gives 0-23 range
                val minute = calendar.get(Calendar.MINUTE)
                Pair(hour24, minute)
            } else {
                null // Parsing returned null
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
            null // Error during parsing
        }
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
    private fun getFutureDatesOfCurrentMonthAddTimeSlot(): List<Date> {
        // get all next dates of current month
        // currentMonth = intent.getIntExtra("month",calendar[Calendar.MONTH]) -1
        currentMonthAddTime = calendarAddTime[Calendar.MONTH]
        return getDatesAddTime(mutableListOf())
    }
    private fun getDatesAddTime(list: MutableList<Date>): List<Date> {
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
    private fun getEvents(): List<Event> {
        val eventList = mutableListOf<Event>()
        val event1 = Event(co.com.mypt.calendarUtils.Date(2024, 11, 5), "Meeting")
        val event2 = Event(co.com.mypt.calendarUtils.Date(2024, 11, 10), "Conference")
        eventList.add(event1)
        eventList.add(event2)
        return eventList
    }

    private fun createRepsAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.reps_bottomsheet, null)
        repsBottomSheetDialog.setCancelable(true)
        var atweek =bottomSheet.findViewById<AutoCompleteTextView>(R.id.atweek)
        tvEndDate =bottomSheet.findViewById<TextView>(R.id.tvEndDate)
        var imUp =bottomSheet.findViewById<ImageView>(R.id.imUp)
        var imDown =bottomSheet.findViewById<ImageView>(R.id.imDown)
        ed1 =bottomSheet.findViewById(R.id.ed1)
        tvm =bottomSheet.findViewById<TextView>(R.id.tvm)
        tvT =bottomSheet.findViewById<TextView>(R.id.tvT)
        tvw =bottomSheet.findViewById<TextView>(R.id.tvw)
        tvTh =bottomSheet.findViewById<TextView>(R.id.tvTh)
        tvf =bottomSheet.findViewById<TextView>(R.id.tvf)
        tvS =bottomSheet.findViewById<TextView>(R.id.tvS)
        tvSu =bottomSheet.findViewById<TextView>(R.id.tvSu)
        var tvDone =bottomSheet.findViewById<TextView>(R.id.tvDone)
        checkEndON =bottomSheet.findViewById<CheckBox>(R.id.checkEndON)
        check5 =bottomSheet.findViewById<CheckBox>(R.id.check5)

        check5.setOnClickListener {
            checkEnd="checknoEndDate"
            checkEndON.isChecked=false
            check5.isChecked=true
        }
        checkEndON.setOnClickListener {
            checkEnd="checkEndDate"
            check5.isChecked=false
            checkEndON.isChecked=true
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR] // current year
            val mMonth = c[Calendar.MONTH] // current month
            val mDay = c[Calendar.DAY_OF_MONTH] // current day
            // date picker dialog
            datePickerDialog = DatePickerDialog(
                this,
                { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    // set day of month , month and year value in the edit text
                    val myCalendar = Calendar.getInstance()
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = monthOfYear
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val myFormat = "dd-MM-yyyy" //In which you need put here
                    val sdf =
                        SimpleDateFormat(myFormat, Locale.US)
                    tvEndDate.text = sdf.format(myCalendar.time)
                    end_date=tvEndDate.text.toString()

                }, mYear, mMonth, mDay
            )
            var tempdate=sdfAddTime.parse(intent.getStringExtra("slotStartDate"))
            val cal = Calendar.getInstance()
            cal.setTime(tempdate)
            cal.add(Calendar.DATE, 1)
            //datePickerDialog!!.datePicker.minDate = cal.time.time
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("dd-MM-yyyy")
            try {
                val mDate = sdf.parse(startDate)
                end_date=tvEndDate.text.toString()
                datePickerDialog!!.datePicker.minDate = mDate.time
                //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            datePickerDialog!!.show()
        }
        atweek.setText("Weeks")
        tvDone.setOnClickListener{
            // addTimeSLotBottomSheetDialog.show()
            if (selectdays.isEmpty()){
                Toast.makeText(applicationContext,"Select day",Toast.LENGTH_SHORT).show()
            }
            else if (checkEnd.equals("")){
                Toast.makeText(applicationContext,"Select end date",Toast.LENGTH_SHORT).show()
            } else if (checkEnd.equals("checkEndDate") && tvEndDate.text.toString().isEmpty()){
                Toast.makeText(applicationContext,"Select end date",Toast.LENGTH_SHORT).show()
            }
            else{
                repsBottomSheetDialog.dismiss()
            }
        }


        val adapter = ArrayAdapter(this,R.layout.weellist, items)

        atweek.threshold = 0
        atweek.setAdapter(adapter)
        repsBottomSheetDialog.setContentView(bottomSheet)
        val bottomSheetBehaviour = repsBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheetBehaviour!!)
        // Enable wrap_content for default (expanded) height
        behavior.isFitToContents = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.maxHeight = (resources.displayMetrics.heightPixels * 0.7).toInt()  // Prevent full screen
        behavior.isHideable = true

        atweek.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            // atweek.showDropDown()
            true
        })
        atweek.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            parent.getItemAtPosition(position) as String
            -1
            ed1.text = "1"

        }
        tvm.setOnClickListener{
            if(selectdays.contains("2")){
                selectdays.remove("2")
                changecolor(tvm,false)
            }else{
                selectdays.add("2")
                changecolor(tvm,true)
            }

        }
        tvT.setOnClickListener{
            if(selectdays.contains("3")){
                selectdays.remove("3")
                changecolor(tvT,false)
            }else{
                selectdays.add("3")
                changecolor(tvT,true)
            }
        }
        tvw.setOnClickListener{
            if(selectdays.contains("4")){
                selectdays.remove("4")
                changecolor(tvw,false)
            }else{
                selectdays.add("4")
                changecolor(tvw,true)
            }
        }
        tvTh.setOnClickListener{
            if(selectdays.contains("5")){
                selectdays.remove("5")
                changecolor(tvTh,false)
            }else{
                selectdays.add("5")
                changecolor(tvTh,true)
            }
        }
        tvf.setOnClickListener{
            if(selectdays.contains("6")){
                selectdays.remove("6")
                changecolor(tvf,false)
            }else{
                selectdays.add("6")
                changecolor(tvf,true)
            }

        }
        tvS.setOnClickListener{
            if(selectdays.contains("7")){
                selectdays.remove("7")
                changecolor(tvS,false)
            }else{
                selectdays.add("7")
                changecolor(tvS,true)
            }
        }
        tvSu.setOnClickListener{
            if(selectdays.contains("1")){
                selectdays.remove("1")
                changecolor(tvSu,false)
            }else{
                selectdays.add("1")
                changecolor(tvSu,true)
            }

        }

        tvEndDate.setOnClickListener{

        }
        imUp.setOnClickListener{
            if (!ed1.text.toString().equals("")){
                var counterMax = Integer.parseInt(ed1.text.toString())
                if (atweek.text.toString().equals("Weeks")){
                    if (counterMax<4){
                        counterMax++ // Increase the counter
                        ed1.text = counterMax.toString()
                    }
                }else{
                    if (counterMax<6){
                        counterMax++ // Increase the counter
                        ed1.text = counterMax.toString()
                    }
                }
            }



        }
        imDown.setOnClickListener{
            if (!ed1.text.toString().equals("")){
                var counterMax = Integer.parseInt(ed1.text.toString())
                if (counterMax>1){
                    counterMax-- // Decrease the counter
                    ed1.text = counterMax.toString()
                }
            }

            /*  if (atweek.equals("Weeks")){
                  if (counterMax >4 && counterMax==4){
                      counterMax++ // Increase the counter
                      ed1.setText(counterMax.toString())
                  }
              }else{
                  if (counterMax >6 && counterMax==6){
                      counterMax++ // Increase the counter
                      ed1.setText(counterMax.toString())
                  }
              }*/
        }
        val window = repsBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

    }
    private fun createSuperSetAlert() {
        count=3
        val bottomSheet = layoutInflater.inflate(R.layout.create_superset_bottomsheet, null)
        createSupersetBottomSheetDialog.setCancelable(true)
        supersetRecycler =bottomSheet.findViewById(R.id.supersetRecycler)
        var linearAddSets =bottomSheet.findViewById<LinearLayout>(R.id.linearAddSets)
        tvValue =bottomSheet.findViewById(R.id.tvValue)
        tvCreateGroup =bottomSheet.findViewById(R.id.tvCreateGroup)
        var imMinusMininmumReps =bottomSheet.findViewById<ImageView>(R.id.imMinusMininmumReps)
        var imAddMinimumReps =bottomSheet.findViewById<ImageView>(R.id.imAddMinimumReps)
        var delete =bottomSheet.findViewById<ImageView>(R.id.close)
        var title =bottomSheet.findViewById<TextView>(R.id.title)

        title.text = "Superset $superSetCount"
        supersetAdapter= SupersetGridAdapter(this@CreateWorkoutSupersetActivity, selectedExercisemodelLists,createSupersetBottomSheetDialog)
        supersetRecycler.adapter=supersetAdapter
        tvCreateGroup.setOnClickListener{
            if (selectedExercisemodelLists.isEmpty()){
                return@setOnClickListener
            }
            if (selectedExercisemodelLists.size>1){
                createWorkoutData()
            }else{
                Toast.makeText(applicationContext,"Please select at least two exercise to create a superset",
                    Toast.LENGTH_LONG).show()
            }

        }

        createSupersetBottomSheetDialog.setContentView(bottomSheet)
        linearAddSets.setOnClickListener { // Or findViewById(R.id.plusButton)
            createSupersetBottomSheetDialog.dismiss()
        }
        imAddMinimumReps.setOnClickListener { // Or findViewById(R.id.plusButton)
            count++
            updateQuantityDisplay()
        }
        // Set click listener for minus button
        imMinusMininmumReps.setOnClickListener { // Or findViewById(R.id.minusButton)
            if (count > 1) { // Optional: Prevent negative values
                count--
                updateQuantityDisplay()
            }
        }
        delete.setOnClickListener { // Or findViewById(R.id.minusButton)

            tvCreateGroup.background = resources.getDrawable(R.drawable.rectangle_btn)
            tvCreateGroup.setTextColor(resources.getColor(R.color.white))
            supersetRecycler.visibility=View.GONE
            selectedExercisemodelLists.clear()
            chooseExcerciseAdapter= ChooseExcerciseforSuperSetAdapter(
                excerxiseModelList,
                this@CreateWorkoutSupersetActivity,
                selectedExercisemodelLists
            )
            recyclerExercise.adapter=chooseExcerciseAdapter
            supersetRecycler.visibility=View.GONE
        }

        val window = createSupersetBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        createSupersetBottomSheetDialog.show()

    }

    private fun updateQuantityDisplay() {
        tvValue.text = count.toString() // Or findViewById(R.id.quantityTextView).text = count.toString()
    }
    private fun changecolor(
        tvm: TextView,
        bool: Boolean
    ) {
        if (bool==true){
            tvm.background = resources.getDrawable(R.drawable.equipment_rectangle)
        }else{
            tvm.background = resources.getDrawable(R.drawable.booking_choicelayout)

        }
    }

    private fun createNotifyAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.remind_me_bottomsheet, null)

        remindRecycler =bottomSheet.findViewById<RecyclerView>(R.id.remindRecycler)
        tvRemindDone =bottomSheet.findViewById<TextView>(R.id.tvRemindDone)
        //

        tvRemindDone.setOnClickListener {
            if (remind_id.equals("")){
                return@setOnClickListener
            }else{
                notifyBottomSheetDialog.dismiss()
            }
        }


        notifyBottomSheetDialog.setContentView(bottomSheet)
        val bottomSheetBehaviour = notifyBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheetBehaviour!!)
        // Enable wrap_content for default (expanded) height
        behavior.isFitToContents = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()  // Prevent full screen
        behavior.isHideable = true

        val window = notifyBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.shader = textShader
    }
    private fun getRemindMeData() {
        val api = ApiURL.remindworkouttime
        Log.e("remindWorkoutTime",api)
        Log.e("remind_id",remind_id)
        reminderModelList.clear()
        GetMethod(api, this).startMethod(object : ResponseData {
            override fun response(data: String?) {
                Log.e("remindworkoutResponse",""+data)
                try {
                    val jsonObj = JSONObject(data)
                    if(jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        for(i in 0 until jsonArray!!.length()){
                            var jsonObject1=jsonArray.optJSONObject(i)
                            var remindModel=RemindModel()
                            remindModel.id=""+jsonObject1.optString("id")
                            remindModel.remind_time=""+jsonObject1.optString("remind_time")
                            remindModel.time=""+jsonObject1.optString("remind_time")
                            reminderModelList.add(remindModel)
                        }
                        var reminerAdapter= ReminderAdapter(
                            applicationContext,
                            reminderModelList,
                            remind_id
                        )
                        remindRecycler.adapter=reminerAdapter
                    }


                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error?.printStackTrace()
            }
        })
    }
    fun daysDiffFromToday(dateStr: String, pattern: String = "yyyy-MM-dd"): Long {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val target = Calendar.getInstance().apply { time = sdf.parse(dateStr)!! }
        val today = Calendar.getInstance()

        // Normalize time to midnight for both dates
        listOf(today, target).forEach {
            it.set(Calendar.HOUR_OF_DAY, 0)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
            it.set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = target.timeInMillis - today.timeInMillis
        return abs(TimeUnit.MILLISECONDS.toDays(diffMillis))
    }
    private fun getSuperSetListData(workout_id1: String) {
        val api = ApiURL.seteditworkout+workout_id1
        Log.e("GetsuperSetAPi",api)
        supersetModelList.clear()
        GetMethod(api, this).startMethod(object : ResponseData {
            override fun response(data: String?) {
                Log.e("GetsupersetResponse",""+data)
                try {
                    val jsonObj = JSONObject(data)
                    if(jsonObj.optBoolean("status")){
                        var jsonobject1=jsonObj.optJSONObject("data")
                       /* count=jsonobject1.optString("sets_round").toInt()
                        updateQuantityDisplay()
                        tvValue.setText(jsonobject1.optString("sets_round"))*/

                        workoutType.text = jsonobject1.optString("type").capitalize()
                        edWorkoutname.setText(jsonobject1.optString("name"))
                        remind_id=jsonobject1.optString("remind_me")
                        tvRemindDone.setBackgroundResource(R.drawable.white_rectangle)
                        tvRemindDone.setTextColor(getColor(R.color.buttontextcolor))

                        tvExercises.text = jsonobject1.optString("total_exercise")
                        tvCalories.text = jsonobject1.optString("calories")

                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", getDefault())
                        val outputFormat = SimpleDateFormat("dd-MM-yyyy", getDefault())

                        if (jsonobject1.optString("end_date").equals("") || jsonobject1.optString("end_date").equals("null")){
                            checkEnd="checknoEndDate"
                            checkEndON.isChecked=false
                            check5.isChecked=true
                        }else{
                            checkEnd="checkEndDate"
                            check5.isChecked=false
                            checkEndON.isChecked=true
                            tvEndDate.text = outputFormat.format(inputFormat.parse(jsonobject1.optString("end_date")))

                        }
                        selectedtime=jsonobject1.optString("time")
                        val date = inputFormat.parse(jsonobject1.optString("start_date"))
                        startDate = outputFormat.format(date!!)
                        selectedDateposition = daysDiffFromToday(jsonobject1.optString("start_date")).toInt()
                        tvSlotTime.text = outputFormat.format(date!!)+","+jsonobject1.optString("time")
                        addTimeSlotAlert()
                        ed1.text = jsonobject1.optString("repeat_duration")
                        var repeatdays=jsonobject1.optString("repeat_days").split(",")
                        selectdays.clear()
                        for (i in 0 until repeatdays.size){
                            selectdays.add(repeatdays[i].trim())
                            if (repeatdays[i].trim().equals("1")){
                                changecolor(tvSu,true)
                            } else if (repeatdays[i].trim().equals("2")){
                                changecolor(tvm,true)
                            } else if (repeatdays[i].trim().equals("3")){
                                changecolor(tvT,true)
                            }else if (repeatdays[i].trim().equals("4")){
                                changecolor(tvw,true)
                            }else if (repeatdays[i].trim().equals("5")){
                                changecolor(tvTh,true)
                            }else if (repeatdays[i].trim().equals("6")){
                                changecolor(tvf,true)
                            }else{
                                changecolor(tvS,true)
                            }
                        }

                        var jsonArray=jsonobject1.optJSONArray("supersets")
                        if (jsonArray.length()>0){
                            recyclerExercise.visibility= View.GONE
                            recyclerSuperSetList.visibility= View.VISIBLE

                            if(!showAllOptions){
                                linearAdd.visibility=View.GONE
                                linearAddRest.visibility=View.VISIBLE
                                linearAddNewSuperSet.visibility=View.VISIBLE
                            }else{
                                linearAdd.visibility=View.VISIBLE
                                linearAddRest.visibility=View.VISIBLE
                                linearAddNewSuperSet.visibility=View.VISIBLE
                            }

                            card_how_to.visibility=View.GONE
                            tvSaveExercise.visibility=View.VISIBLE

                            tvSaveExercise.text = "Save"
                            tvSaveExercise.background = resources.getDrawable(R.drawable.white_rectangle)
                            tvSaveExercise.setTextColor(resources.getColor(R.color.buttontextcolor))

                            for(i in 0 until jsonArray!!.length()){
                                var jsonObjectset=jsonArray.optJSONObject(i)
                                var superListModel=SuperSetListModel()
                                superListModel.type=""+jsonObjectset.optString("type")
                                superListModel.sets_position=""+jsonObjectset.optString("sets_position")
                                if (jsonObjectset.optString("type").equals("rest")){
                                    superListModel.duration=""+jsonObjectset.optString("duration")
                                    superListModel.exercises= JSONArray()

                                }else{
                                    superSetCount++
                                    superListModel.duration=""
                                    superListModel.exercises=jsonObjectset.optJSONArray("exercises")
                                }
                                supersetModelList.add(superListModel)
                            }
                            var supersetListAdapter=
                                SuperSetListAdapter(this@CreateWorkoutSupersetActivity, supersetModelList,jsonobject1.optString("workout_id"))
                            recyclerSuperSetList.adapter=supersetListAdapter

                        }else{
                            tvSaveExercise.text = "Create superset Group"
                            recyclerExercise.visibility= View.VISIBLE
                            recyclerSuperSetList.visibility= View.GONE
                            linearAdd.visibility=View.VISIBLE
                            linearAddRest.visibility=View.GONE
                            linearAddNewSuperSet.visibility=View.GONE
                        }
                    }


                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
                getRemindMeData()
            }

            override fun error(error: VolleyError?) {
                error?.printStackTrace()
            }
        })
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(updateSuperSetList, IntentFilter("updateSuperSet"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(updateSuperSetList, IntentFilter("updateSuperSet"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(AddExerciseList, IntentFilter("selectedSupersetExerciseList"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(AddExerciseList, IntentFilter("selectedSupersetExerciseList"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(CreateSuperSetExerciseList, IntentFilter("createSupersetExercise"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(CreateSuperSetExerciseList, IntentFilter("createSupersetExercise"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(selectRemind, IntentFilter("selectRemindId"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(selectRemind, IntentFilter("selectRemindId"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(deleteAllSet, IntentFilter("delete"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(deleteAllSet, IntentFilter("delete"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(deleteSelectedExercise, IntentFilter("deleteSelectedExercise"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(deleteSelectedExercise, IntentFilter("deleteSelectedExercise"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(deleteSuperSetRest, IntentFilter("deleteSuperSetRest"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(deleteSuperSetRest, IntentFilter("deleteSuperSetRest"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(deleteSuperSetExercise, IntentFilter("deleteSuperSetExercise"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(deleteSuperSetExercise, IntentFilter("deleteSuperSetExercise"))
        }
    }
   /* override fun onPause() {
        super.onPause()
        unregisterReceiver(AddExerciseList)
        unregisterReceiver(selectRemind)
        unregisterReceiver(deleteRest)
    }*/

    val updateSuperSetList = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            getSuperSetListData(workout_id)
        }

    }
    val CreateSuperSetExerciseList = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            selectedExercisemodelLists = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableArrayListExtra("exercise_list", SelectExcerciseModel::class.java)
            } else {
                @Suppress("DEPRECATION")
                (intent?.getParcelableArrayListExtra("exercise_list"))
            })!!


            if (selectedExercisemodelLists.size>1) {
                tvSaveExercise.background = resources.getDrawable(R.drawable.white_rectangle)
                tvSaveExercise.setTextColor(resources.getColor(R.color.buttontextcolor))


            }else{
                tvSaveExercise.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvSaveExercise.setTextColor(resources.getColor(R.color.white))

            }
        }

    }

    val AddExerciseList = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("check","checkSuperSet")

            if (intent != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    excerxiseModelList =
                        intent.getParcelableArrayListExtra("exercise_list", SelectExcerciseModel::class.java)!!
                } else {
                    @Suppress("DEPRECATION")
                    excerxiseModelList = intent.getParcelableArrayListExtra("exercise_list")!!
                }


                chooseExcerciseAdapter= ChooseExcerciseforSuperSetAdapter(
                    excerxiseModelList,
                    this@CreateWorkoutSupersetActivity,
                    selectedExercisemodelLists,
                )
                recyclerExercise.adapter=chooseExcerciseAdapter
                calorieDuration()


                Log.e("exerciseModelbroad",""+excerxiseModelList.size)
                if (excerxiseModelList.size>0){
                    card_how_to.visibility=View.GONE
                    tvSaveExercise.visibility=View.VISIBLE

                }else{
                    card_how_to.visibility=View.VISIBLE
                    tvSaveExercise.visibility=View.GONE
                }
                if(workout_id != ""){
                    new_set = true
                }
                tvSaveExercise.text = "Create superset Group"
                recyclerExercise.visibility= View.VISIBLE
                recyclerSuperSetList.visibility= View.GONE
                linearAdd.visibility=View.VISIBLE
                linearAddRest.visibility=View.GONE
                linearAddNewSuperSet.visibility=View.GONE

                updateGridList()
            }
        }
    }
    fun updateGridList(){
        if(selectedExercisemodelLists.isNotEmpty()){
            val selectedExerciseIds = selectedExercisemodelLists
                .filter { it.type != "rest" } // Ensure we only consider actual exercises from the selection
                .map { it.id }                // Get their IDs
                .toSet()                      // Convert to a Set for efficient 'contains' checks

            val foundItemsInMainList = excerxiseModelList.filter { mainListItem ->
                mainListItem.id in selectedExerciseIds && mainListItem.type != "rest" // Also ensure it's an exercise in the main list
            }
            selectedExercisemodelLists.clear()
            selectedExercisemodelLists.addAll(foundItemsInMainList)
            if (supersetAdapter!=null)
                supersetAdapter!!.notifyDataSetChanged()
        }
    }
    val selectRemind = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            remind_id=""+intent!!.getStringExtra("remind_id")
            if (remind_id.equals("")){
                tvRemindDone.setBackgroundResource(R.drawable.add_address_drawable)
                tvRemindDone.setTextColor(getColor(R.color.subheadingcolor))

            }else{
                tvRemindDone.setBackgroundResource(R.drawable.white_rectangle)
                tvRemindDone.setTextColor(getColor(R.color.buttontextcolor))
            }

        }
    }
    val deleteAllSet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("position",""+intent!!.getIntExtra("position",0))
            selectedExercisemodelLists.removeAt(intent!!.getIntExtra("position",0))
            chooseExcerciseAdapter= ChooseExcerciseforSuperSetAdapter(
                excerxiseModelList,
                this@CreateWorkoutSupersetActivity,
                selectedExercisemodelLists
            )
            recyclerExercise.adapter=chooseExcerciseAdapter
            supersetAdapter!!.notifyDataSetChanged()

            if (selectedExercisemodelLists.size>0){
                supersetRecycler.visibility=View.VISIBLE
                tvCreateGroup.background = resources.getDrawable(R.drawable.white_rectangle)
                tvCreateGroup.setTextColor(resources.getColor(R.color.buttontextcolor))
            }else{
                tvCreateGroup.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvCreateGroup.setTextColor(resources.getColor(R.color.white))
                supersetRecycler.visibility=View.GONE

            }
        }
    }

    val deleteSuperSetRest = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            new_set=false
            type=""

            Log.e("positionset",""+intent!!.getIntExtra("position",0))
            val removePos = intent.getIntExtra("position",0)
            supersetModelList.removeAt(removePos)
            recyclerSuperSetList.adapter!!.notifyItemRemoved(removePos)
            createWorkoutData()
        }
    }
    val deleteSuperSetExercise = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            getSuperSetListData(workout_id)
        }
    }

    val deleteSelectedExercise = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {


        }
    }

    private fun showEditRestDialog(){
        var selectedMinutes = 0
        var selectedSeconds = 0
        val dialog = Dialog(this@CreateWorkoutSupersetActivity)
        val bottomSheet = layoutInflater.inflate(R.layout.second_edit, null)
        dialog.setContentView(bottomSheet)
        var minutePicker=bottomSheet.findViewById<NumberPicker>(R.id.minutePicker)
        var secondPicker=bottomSheet.findViewById<NumberPicker>(R.id.secondPicker)
        var txt_ok=bottomSheet.findViewById<TextView>(R.id.txt_ok)
        var txt_close=bottomSheet.findViewById<TextView>(R.id.txt_close)
        txt_ok.setOnClickListener {
            val totalSeconds = selectedMinutes * 60 + selectedSeconds
            // list[position].rest_duration = totalSeconds.toString()
            tvsecondActiviy.text = "Rest $totalSeconds seconds"

            dialog.dismiss()
        }

        txt_close.setOnClickListener {
            dialog.dismiss()
        }
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.setFormatter { i -> String.format("%02d", i) }
        minutePicker.wrapSelectorWheel = true

        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.setFormatter { i -> String.format("%02d", i) }
        secondPicker.wrapSelectorWheel = true

        selectedMinutes = minutePicker.value
        selectedSeconds = secondPicker.value


        minutePicker.setOnValueChangedListener { numberPicker, i, newValue ->
            selectedMinutes = newValue
        }

        secondPicker.setOnValueChangedListener{secondPicker, i, newValue ->
            selectedSeconds = newValue
        }
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_bg)



        dialog.create()
        dialog.show()
    }
    fun calorieDuration(){
        totalCalories=0
        total_duration=0
        for (exercise in excerxiseModelList) {
            totalCalories += exercise.calories.replace("null","").toInt()
            total_duration += exercise.duration.replace("null","").toInt()

        }
        tvExercises.text = ""+excerxiseModelList.size
        tvCalories.text = totalCalories.toString()

        tvTime.text = ""+total_duration+"s"

    }
}