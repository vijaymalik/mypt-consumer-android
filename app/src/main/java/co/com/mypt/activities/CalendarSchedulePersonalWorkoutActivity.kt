package co.com.mypt.activities

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
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.JsonPostRequest
import co.com.mypt.Api.JsonResponseData
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.CalendarAdapterAddTimeSlot
import co.com.mypt.adapter.ChooseExcerciseAdapter
import co.com.mypt.adapter.ReminderAdapter
import co.com.mypt.calendarUtils.Event
import co.com.mypt.model.RemindModel
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
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class CalendarSchedulePersonalWorkoutActivity : AppCompatActivity() {
    var workout_id=""
    var end_date=""
    lateinit var recyclerExercise:RecyclerView
    lateinit var linearAdd:LinearLayout
    lateinit var rulerViewBottom: co.com.mypt.utils.RulerView
    lateinit var linearReps:LinearLayout
    lateinit var linearHeader:LinearLayout
    lateinit var checkEndON: CheckBox
    lateinit var check5: CheckBox
    lateinit var chooseExcerciseAdapter:ChooseExcerciseAdapter
    var excerxiseModelList :ArrayList<SelectExcerciseModel> = ArrayList()
    var reminderModelList :ArrayList<RemindModel> = ArrayList()
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
    lateinit var ed1:TextView
    lateinit var tvSaveExercise:TextView
    lateinit var tvm:TextView
    lateinit var tvf:TextView
    lateinit var tvS:TextView
    lateinit var tvSu:TextView
    lateinit var tvw:TextView
    lateinit var tvTh:TextView
    lateinit var tvT:TextView
    lateinit var card: CardView
    lateinit var hideText:TextView
    lateinit var workoutType:TextView
    lateinit var edWorkoutname: EditText
    lateinit var linearNotify:LinearLayout
    lateinit var linearActivityRest:LinearLayout
    lateinit var linearHide:LinearLayout
    lateinit var remindRecycler: RecyclerView
    lateinit var standard_bottom_sheet:LinearLayout
    lateinit var linearCircuitRound:LinearLayout
    lateinit var cardRest: CardView
    lateinit var linearCircuitpart:LinearLayout
    lateinit var imMinusMininmumReps: ImageView
    lateinit var imAddMinimumReps: ImageView
    lateinit var imclose: ImageView
    lateinit var imArrow: ImageView
    lateinit var notifyBottomSheetDialog:BottomSheetDialog
    lateinit var rulerBottomSheetDialog:BottomSheetDialog
    lateinit var repsBottomSheetDialog:BottomSheetDialog
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
    var totalExercises = 0
    private var count: Int = 1
    lateinit var switchCompat: SwitchCompat
    lateinit var rulerview: co.com.mypt.utils.RulerView
    var initialValue=60
    var bottominitialValue=60
    private var showRests = false
    var screenType=""
    var total_duration=0
    var start_Date=""
    var selectedtime=""
    var selectedDateposition=0
    var am_pm = ""
    //val workoutItems = ArrayList<SelectExcerciseModel>()
    val editUpdateValue = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedItem = result.data?.getParcelableExtra<SelectExcerciseModel>("updatedItem")
            updatedItem?.let { updateModelList(it) }
        }
    }
    // 1. Define your listener once
    val rulerListener: (Int) -> Unit = { value ->
        println("Centered value: $value")
        initialValue = value
        tvsecondActiviy.text = "Rest $initialValue seconds"

        if (!switchCompat.isChecked && excerxiseModelList.isNotEmpty()) {
            if(excerxiseModelList[excerxiseModelList.size-1].type == "rest"){
                excerxiseModelList[excerxiseModelList.size-1].rest_duration = initialValue.toString()
                chooseExcerciseAdapter.notifyItemChanged(excerxiseModelList.size-1)
            }
            //linearActivityRest.visibility = View.VISIBLE
        }
    }

    private fun updateModelList(updatedItem: SelectExcerciseModel) {
        val index = excerxiseModelList.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            excerxiseModelList[index] = updatedItem
            chooseExcerciseAdapter.notifyItemChanged(index)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_schedule_personal_workout)
        workout_id=""+intent.getStringExtra("workout_id")

        addTimeSLotBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        imclose = findViewById(R.id.imclose)
        card = findViewById(R.id.card)
        imSecond = findViewById(R.id.imSecond)
        tvsecondActiviy = findViewById(R.id.tvsecondActiviy)
        linearActivityRest = findViewById(R.id.linearActivityRest)
        rulerview = findViewById(R.id.rulerview)
        recyclerExercise=findViewById(R.id.recyclerExercise)
        linearAdd=findViewById(R.id.linearAdd)
        switchCompat=findViewById(R.id.switchCompat)
        workoutType=findViewById(R.id.workoutType)
        hideText=findViewById(R.id.hideText)
        linearCircuitpart=findViewById(R.id.linearCircuitpart)
        cardRest=findViewById(R.id.cardRest)
        linearCircuitRound=findViewById(R.id.linearCircuitRound)
        linearReps=findViewById(R.id.linearReps)
        imArrow=findViewById(R.id.imArrow)
        tvValue=findViewById(R.id.tvValue)
        linearHide=findViewById(R.id.linearHide)
        tvSaveExercise=findViewById(R.id.tvSaveExercise)
        edWorkoutname=findViewById(R.id.edWorkoutname)
        linearHeader=findViewById(R.id.linearHeader)
        imMinusMininmumReps=findViewById(R.id.imMinusMininmumReps)
        imAddMinimumReps=findViewById(R.id.imAddMinimumReps)
        tvTime=findViewById(R.id.tvTime)
        tvSlotTime=findViewById(R.id.tvSlotTime)
        tvCalories =findViewById(R.id.tvCalories)
        tvExercises =findViewById(R.id.tvExercises)
        linearNotify =findViewById(R.id.linearNotify)

        rulerview.onValueChangeListener = rulerListener
        rulerview.setInitialPosition(0)
        try {
            screenType= intent.getStringExtra("screenType").toString()
            if (screenType.equals("calendar")){
                start_Date=""
                getDetailData(workout_id,)
            }else{
                start_Date=""+intent.getStringExtra("slotStartDate")
                selectedtime=""+intent.getStringExtra("slotTime")
                selectedDateposition=intent.getIntExtra("selectedDateposition",0)

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

        if (intent.getStringExtra("type").equals("circuit")){
            linearCircuitpart.visibility=View.VISIBLE
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.circuit_color))

        }else if(intent.getStringExtra("type").equals("regular")){
            //tvSaveExercise.visibility=View.VISIBLE
            linearCircuitpart.visibility=View.GONE
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.regular_color))

        }else{
            linearCircuitpart.visibility=View.GONE


        }
        tvSlotTime.text = start_Date+","+selectedtime
        tvSlotTime.setOnClickListener {
            addTimeSLotBottomSheetDialog.show()
        }
        updateQuantityDisplay()

        linearHide.setOnClickListener {
            if (hideText.text.toString().contains("Hide")){
                linearCircuitRound.visibility=View.GONE
                cardRest.visibility=View.GONE
                hideText.text = "Show circuit setting"
                imArrow.rotation=90f

            }else{
                linearCircuitRound.visibility=View.VISIBLE
                cardRest.visibility=View.VISIBLE
                hideText.text = "Hide circuit setting"
                imArrow.rotation=270f
            }

        }
        linearHeader.setOnClickListener {
            finish()
        }

        imSecond.setOnClickListener {
            showEditRestDialog()
        }
        imclose.setOnClickListener {
            linearActivityRest.visibility= View.GONE
        }
        workoutType.text = intent.getStringExtra("type")?.capitalize()

        notifyBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        repsBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        rulerBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        addTimeSlotAlert()
        createNotifyAlert()
        createRepsAlert()


        imAddMinimumReps.setOnClickListener {
            count++
            updateQuantityDisplay()
        }
        // Set click listener for minus button
        imMinusMininmumReps.setOnClickListener {
            if (count > 1) {
                count--
                updateQuantityDisplay()
            }
        }

        switchCompat.setOnClickListener {
            showRests = switchCompat.isChecked
            if(excerxiseModelList.isNotEmpty()){
                if (switchCompat.isChecked){
                    createRulerAlert()
                    linearActivityRest.visibility= View.GONE
                    val currentExercisesOnly = ArrayList(excerxiseModelList.filter { it.type != "rest" })
                    excerxiseModelList.clear()
                    currentExercisesOnly.forEachIndexed { index, exercise ->
                        excerxiseModelList.add(exercise)
                        excerxiseModelList.add(
                            SelectExcerciseModel(
                                category = "", // Or appropriate defaults for your model
                                raps = "",
                                video_path = "",
                                video_type = "",
                                calories = "",
                                image = "",
                                id = "",
                                name = "",
                                type = "rest",
                                rest_duration = bottominitialValue.toString()
                            )
                        )
                    }
                    chooseExcerciseAdapter.notifyDataSetChanged()

                    /*
                    excerxiseModelList.removeAll { it.type == "rest" }
                    workoutItems.clear()
                    for ((index, ex) in excerxiseModelList.withIndex()) {
                        workoutItems.add(ex)
                        if (showRests && index < excerxiseModelList.size) {
                            workoutItems.add(SelectExcerciseModel(
                                category = "",raps="", video_path = "", video_type = "", calories = "", image = "", id = "", name = "", type = "rest",
                                rest_duration = bottominitialValue.toString()
                            ))
                        }

                    }
                    chooseExcerciseAdapter= ChooseExcerciseAdapter(
                        workoutItems,
                        this@CalendarSchedulePersonalWorkoutActivity,
                        switchCompat,
                        intent.getStringExtra("type"),
                        switchCompat.isChecked
                    )
                    recyclerExercise.adapter=chooseExcerciseAdapter*/
                }
                else{
                    excerxiseModelList.removeAll { it.type == "rest" }
                    excerxiseModelList.add(SelectExcerciseModel(
                        category = "",raps="", video_path = "", video_type = "", calories = "", image = "", id = "", name = "",
                        type = "rest",
                        rest_duration = initialValue.toString()
                    ))
                    chooseExcerciseAdapter= ChooseExcerciseAdapter(
                        excerxiseModelList,
                        this@CalendarSchedulePersonalWorkoutActivity,
                        switchCompat,
                        intent.getStringExtra("type"),
                        switchCompat.isChecked
                    )
                    recyclerExercise.adapter=chooseExcerciseAdapter
                }
            }
        }

        linearAdd.setOnClickListener{
            var intent= Intent(this,ChooseExcerciseActivity::class.java)
            intent.putExtra("type",getIntent().getStringExtra("type"))
           // Log.e("type",""+intent.getStringExtra("type"))
            intent.putParcelableArrayListExtra("exercise_listfromAdd", excerxiseModelList)
            startActivity(intent)
        }

        tvSaveExercise.setOnClickListener{
            if (excerxiseModelList.size>0){
                if (edWorkoutname.text.toString().trim()==""){
                    edWorkoutname.requestFocus()
                    edWorkoutname.error = "Please enter workout name"
                    return@setOnClickListener
                }
                if (checkEnd.equals("checkEndDate") && tvEndDate.text.toString().contains("Select Date")){
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
                if (excerxiseModelList.size==0){
                    Toast.makeText(applicationContext,"Please add exercise", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                createWorkoutData()
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
    private fun updateQuantityDisplay() {
        tvValue.text = count.toString() // Or findViewById(R.id.quantityTextView).text = count.toString()
    }

    private fun createWorkoutData() {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("id",workout_id)
            jsonObject.put("name",edWorkoutname.text.toString())
            jsonObject.put("description","")
            jsonObject.put("category_id","1")
            jsonObject.put("repeat_duration",ed1.text.toString())
            jsonObject.put("repeat_days", selectdays.joinToString())
            jsonObject.put("start_date",start_Date)
            jsonObject.put("end_date", end_date)
            jsonObject.put("time",selectedtime.uppercase())
            jsonObject.put("remind_me", remind_id)
            jsonObject.put("rest_status", showRests)

            if (intent.getStringExtra("type").equals("regular")){
                jsonObject.put("type", "regular")
                var jsonArray= JSONArray()
                for(i in 0 until excerxiseModelList.size){
                    var jsonObject1= JSONObject()
                    jsonObject1.put("id",excerxiseModelList[i].id)
                    jsonObject1.put("type","exercise")
                    jsonObject1.put("sets","3")
                    jsonObject1.put("reps",excerxiseModelList[i].raps)
                    jsonObject1.put("time_type","2")
                    jsonObject1.put("rest_duration","30")
                    jsonArray.put(jsonObject1)
                }
                jsonObject.put("exercises",jsonArray)
            }else{
                jsonObject.put("type", "circuit")
                jsonObject.put("sets_round",tvValue.text.toString())

                var jsonArray= JSONArray()
                /*if (switchCompat.isChecked){
                    for(i in 0 until workoutItems.size){
                        var jsonObject1= JSONObject()
                        jsonObject1.put("id",workoutItems[i].id)
                        jsonObject1.put("type",workoutItems[i].type)
                        jsonObject1.put("sets","3")
                        jsonObject1.put("reps",workoutItems[i].raps)
                        jsonObject1.put("time_type","2")
                        jsonObject1.put("rest_duration",workoutItems[i].rest_duration)
                        jsonArray.put(jsonObject1)
                    }
                }
                else{
                    for(i in 0 until excerxiseModelList.size){
                        var jsonObject1= JSONObject()
                        jsonObject1.put("id",excerxiseModelList[i].id)
                        jsonObject1.put("type",excerxiseModelList[i].type)
                        jsonObject1.put("sets","3")
                        jsonObject1.put("reps",excerxiseModelList[i].raps)
                        jsonObject1.put("time_type","2")
                        jsonObject1.put("rest_duration",excerxiseModelList[i].rest_duration)
                        jsonArray.put(jsonObject1)
                    }
                   if (linearActivityRest.visibility== View.VISIBLE){
                       var jsonObject1= JSONObject()
                       jsonObject1.put("id","")
                       jsonObject1.put("type","rest")
                       jsonObject1.put("sets","")
                       jsonObject1.put("reps","")
                       jsonObject1.put("time_type","2")
                       jsonObject1.put("rest_duration",initialValue)
                       jsonArray.put(jsonObject1)
                   }

                }*/

                for(i in 0 until excerxiseModelList.size){
                    var jsonObject1= JSONObject()
                    jsonObject1.put("id",excerxiseModelList[i].id)
                    jsonObject1.put("type",excerxiseModelList[i].type)
                    jsonObject1.put("sets","3")
                    jsonObject1.put("reps",excerxiseModelList[i].raps)
                    jsonObject1.put("time_type","2")
                    jsonObject1.put("rest_duration",excerxiseModelList[i].rest_duration)
                    jsonArray.put(jsonObject1)
                }

                jsonObject.put("exercises",jsonArray)
            }



            Log.e("createWorkoutParam", "" + jsonObject)
            val progressDialog: Dialog = ProgressDialog.progressDialog(this@CalendarSchedulePersonalWorkoutActivity,"")
            progressDialog.show()
            JsonPostRequest(ApiURL.createworkout, jsonObject, this).startPostMethod(object :
                JsonResponseData {
                override fun responseObject(response: JSONObject?) {
                    Log.e("createWorkoutResp", "" + response)
                    if(response!!.optBoolean("status")){
                        Toast.makeText(applicationContext,""+response.optString("msg"), Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                        finish()
                       /* var intent= Intent(this,CalendarMyScheduleActivity::class.java)
                        startActivity(intent)*/
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
    private fun getDetailData(workout_id1: String) {
        val api = ApiURL.seteditworkout+workout_id1
        Log.e("GetRegularDetailPi",api)

        GetMethod(api, this).startMethod(object : ResponseData {
            override fun response(data: String?) {
                Log.e("GetregulardetailResponse",""+data)
                try {
                    val jsonObj = JSONObject(data)
                    if(jsonObj.optBoolean("status")){
                        var jsonobject1=jsonObj.optJSONObject("data")
                        if (jsonobject1.optString("type").equals("circuit")){
                            count=jsonobject1.optString("sets_round").toInt()
                            updateQuantityDisplay()
                            tvValue.text = jsonobject1.optString("sets_round")

                        }

                        workoutType.text = jsonobject1.optString("type").replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                            else it.toString()
                        }
                        edWorkoutname.setText(jsonobject1.optString("name"))
                        remind_id=jsonobject1.optString("remind_me")
                        tvRemindDone.setBackgroundResource(R.drawable.white_rectangle)
                        tvRemindDone.setTextColor(getColor(R.color.buttontextcolor))

                        switchCompat.isChecked = jsonobject1.optBoolean("rest_status")
                        showRests = jsonobject1.optBoolean("rest_status")
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        if (jsonobject1.optString("end_date").replace("null","").equals("")){
                            checkEnd="checknoEndDate"
                            checkEndON.isChecked=false
                            check5.isChecked=true
                        }else{
                            checkEnd="checkEndDate"
                            check5.isChecked=false
                            checkEndON.isChecked=true
                            tvEndDate.text =outputFormat.format(inputFormat.parse(jsonobject1.optString("end_date"))!!)

                        }
                        selectedtime=jsonobject1.optString("time")
                        val date = inputFormat.parse(jsonobject1.optString("start_date"))
                        tvSlotTime.text = outputFormat.format(date!!)+","+jsonobject1.optString("time")
                        start_Date=outputFormat.format(date!!)
                        selectedDateposition = daysDiffFromToday(jsonobject1.optString("start_date")).toInt()
                        addTimeSlotAlert()
                        var jsonArray=jsonobject1.optJSONArray("exercises")
                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                var exerciseModel= SelectExcerciseModel()
                                var jsonObject=jsonArray.optJSONObject(i)
                                exerciseModel.id=jsonObject.optString("id")
                                exerciseModel.category=jsonObject.optString("category")
                                exerciseModel.sets=jsonObject.optString("sets")
                                exerciseModel.name=jsonObject.optString("name")
                                exerciseModel.image=jsonObject.optString("image")
                                exerciseModel.calories=jsonObject.optString("calories")
                                exerciseModel.raps=jsonObject.optString("reps")
                                exerciseModel.type=jsonObject.optString("type")
                                exerciseModel.duration=jsonObject.optString("duration")
                                exerciseModel.rest_duration=jsonObject.optString("rest_duration")
                                excerxiseModelList.add(exerciseModel)
                                if(jsonObject.optString("type").equals("exercise")){
                                    totalExercises++
                                    totalCalories += jsonObject.optString("calories").replace("null","0").toInt()
                                    total_duration += jsonObject.optString("duration").replace("null","0").toInt()
                                }
                            }
                            if (!switchCompat.isChecked){
                                initialValue=excerxiseModelList[excerxiseModelList.size-1].rest_duration.toInt()
                                rulerview.onValueChangeListener = null
                                rulerview.setInitialPosition(initialValue)
                                lifecycle.coroutineScope.launch {
                                    delay(1200)
                                    rulerview.onValueChangeListener = rulerListener

                                }
                            }


                            tvCalories.text = "$totalCalories"
                            tvExercises.text = "$totalExercises"

                            tvTime.text = ""+total_duration+"s"
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

                            chooseExcerciseAdapter= ChooseExcerciseAdapter(
                                excerxiseModelList,
                                this@CalendarSchedulePersonalWorkoutActivity,
                                switchCompat,
                                intent.getStringExtra("type"),
                                switchCompat.isChecked
                            )
                            recyclerExercise.adapter=chooseExcerciseAdapter



                            if (intent.getStringExtra("type").equals("circuit")){
                                if (excerxiseModelList.size>0){
                                    tvSaveExercise.setBackgroundResource(R.drawable.white_rectangle)
                                    tvSaveExercise.setTextColor(getColor(R.color.buttontextcolor))
                                    tvSaveExercise.visibility=View.VISIBLE
                                    linearActivityRest.visibility=View.GONE

                                }else{
                                    tvSaveExercise.visibility=View.GONE
                                }

                            }
                            else if(intent.getStringExtra("type").equals("regular")){
                                tvSaveExercise.visibility=View.VISIBLE
                                if (excerxiseModelList.size>0){
                                    tvSaveExercise.setBackgroundResource(R.drawable.white_rectangle)
                                    tvSaveExercise.setTextColor(getColor(R.color.buttontextcolor))
                                }else{
                                    tvSaveExercise.setBackgroundResource(R.drawable.rectangle_btn)
                                    tvSaveExercise.setTextColor(getColor(R.color.subheadingcolor))
                                }
                            }else{

                            }
                        }else{

                        }
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
                start_Date = sdfAddTime.format(date!!)
               // selectedDateposition=position
                Log.e("slotDate",start_Date)
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

            tvSlotTime.setText(start_Date+","+selectedtime)

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
            tvEndDate.text = "Select Date"
            end_date=""
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
            var tempdate=sdfAddTime.parse(start_Date)
            val cal = Calendar.getInstance()
            cal.setTime(tempdate)
            cal.add(Calendar.DATE, 1)
            datePickerDialog!!.datePicker.minDate = cal.time.time
            @SuppressLint("SimpleDateFormat") val sdf =
                SimpleDateFormat("dd-MM-yyyy")
            try {
                val mDate = sdf.parse(start_Date)
                Log.e("mDate",""+mDate)
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
            } else if (checkEnd.equals("checkEndDate") && tvEndDate.text.toString().contains("Select Date")){
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

        remindRecycler =bottomSheet.findViewById(R.id.remindRecycler)
        tvRemindDone =bottomSheet.findViewById(R.id.tvRemindDone)
        getRemindMeData()

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
        behavior.isFitToContents = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.maxHeight = (resources.displayMetrics.heightPixels * 0.7).toInt()  // Prevent full screen
        behavior.isHideable = true

        val window = notifyBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    private fun createRulerAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.ruler_bottomsheet, null)

        rulerViewBottom =bottomSheet.findViewById(R.id.rulerviewbottom)
        var relative =bottomSheet.findViewById<RelativeLayout>(R.id.relative)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        var rulervalue=60
        rulerViewBottom.setInitialPosition(0)
        rulerViewBottom.onValueChangeListener = { value ->
            rulervalue=value
        }
        relative.setOnClickListener {
            bottominitialValue=rulervalue

            val currentExercisesOnly = ArrayList(excerxiseModelList.filter { it.type != "rest" })
            excerxiseModelList.clear()
            if(showRests){
                currentExercisesOnly.forEachIndexed { index, exercise ->
                    excerxiseModelList.add(exercise)
                    excerxiseModelList.add(
                        SelectExcerciseModel(
                            category = "", // Or appropriate defaults for your model
                            raps = "",
                            video_path = "",
                            video_type = "",
                            calories = "",
                            image = "",
                            id = "",
                            name = "",
                            type = "rest",
                            rest_duration = bottominitialValue.toString()
                        )
                    )
                }
            }
            else {
                excerxiseModelList.addAll(currentExercisesOnly)
            }
            chooseExcerciseAdapter.notifyDataSetChanged()


            /*workoutItems.clear()
            for ((index, ex) in excerxiseModelList.withIndex()) {
                workoutItems.add(ex)
                if (showRests && index < excerxiseModelList.size) {
                    workoutItems.add(SelectExcerciseModel(
                        category = "",raps="", video_path = "", video_type = "", calories = "", image = "", id = "", name = "", type = "rest",
                        rest_duration = bottominitialValue.toString()
                    ))
                }

            }
            Log.e("workoutItem",""+workoutItems.size)

            chooseExcerciseAdapter= ChooseExcerciseAdapter(
                workoutItems,
                this@CalendarSchedulePersonalWorkoutActivity,
                switchCompat,
                intent.getStringExtra("type"),
                switchCompat.isChecked
            )
            recyclerExercise.adapter=chooseExcerciseAdapter*/
            rulerBottomSheetDialog.dismiss()
        }


        rulerBottomSheetDialog.setContentView(bottomSheet)
        rulerBottomSheetDialog.show()
        val window = rulerBottomSheetDialog.window
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
                        var reminerAdapter= ReminderAdapter(applicationContext, reminderModelList,remind_id)
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
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(AddExerciseList, IntentFilter("selectedExerciseList"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(AddExerciseList, IntentFilter("selectedExerciseList"))

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(selectRemind, IntentFilter("selectRemindId"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(selectRemind, IntentFilter("selectRemindId"))

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(deleteRest, IntentFilter("deleteRest"),
                RECEIVER_EXPORTED)
        }else{
            registerReceiver(deleteRest, IntentFilter("deleteRest"))

        }
    }
  /*  override fun onPause() {
        super.onPause()
        unregisterReceiver(AddExerciseList)
        unregisterReceiver(selectRemind)
        unregisterReceiver(deleteRest)
    }*/
    val AddExerciseList = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("check","checkRegular")
            if (intent != null) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                     excerxiseModelList = intent.getParcelableArrayListExtra("exercise_list", SelectExcerciseModel::class.java)!!
                } else {
                    @Suppress("DEPRECATION")
                    excerxiseModelList = intent.getParcelableArrayListExtra("exercise_list")!!
                }

                if (switchCompat.isChecked){
                    val actualExercises = ArrayList(excerxiseModelList.filter { it.type != "rest" })
                    excerxiseModelList.clear()
                    if (actualExercises.isNotEmpty()) {
                        for (i in 0 until actualExercises.size) {
                            val currentExercise = actualExercises[i]
                            excerxiseModelList.add(currentExercise)
                            val isLastExerciseInLoop = (i == actualExercises.size - 1)
                            if (isLastExerciseInLoop) {
                                excerxiseModelList.add(
                                    SelectExcerciseModel(
                                        category = "", raps = "", video_path = "", video_type = "",
                                        calories = "", image = "",
                                        id = "",
                                        name = "", type = "rest",
                                        rest_duration = bottominitialValue.toString()
                                    )
                                )
                            } else {
                                excerxiseModelList.add(
                                    SelectExcerciseModel(
                                        category = "", raps = "", video_path = "", video_type = "",
                                        calories = "", image = "", id = "",
                                        name = "", type = "rest",
                                        rest_duration = bottominitialValue.toString()
                                    )
                                )
                            }
                        }
                    }

                }
                else{
                    if(getIntent().getStringExtra("type").equals("circuit")){
                        excerxiseModelList.removeAll { it.type == "rest" }
                        excerxiseModelList.add(SelectExcerciseModel(
                            category = "",raps="", video_path = "", video_type = "", calories = "", image = "", id = "", name = "",
                            type = "rest",
                            rest_duration = initialValue.toString()
                        ))
                    }
                }
                chooseExcerciseAdapter= ChooseExcerciseAdapter(
                    excerxiseModelList,
                    this@CalendarSchedulePersonalWorkoutActivity,
                    switchCompat,
                    getIntent().getStringExtra("type"),
                    switchCompat.isChecked
                )
                recyclerExercise.adapter=chooseExcerciseAdapter

                calorieDuration()


                if (getIntent().getStringExtra("type").equals("circuit")){
                    if (excerxiseModelList.isNotEmpty()){
                        tvSaveExercise.setBackgroundResource(R.drawable.white_rectangle)
                        tvSaveExercise.setTextColor(getColor(R.color.buttontextcolor))
                        tvSaveExercise.visibility=View.VISIBLE

                    }else{
                        tvSaveExercise.visibility=View.GONE
                    }

                }
                else if(getIntent().getStringExtra("type").equals("regular")){
                    tvSaveExercise.visibility=View.VISIBLE
                    if (excerxiseModelList.size>0){
                        tvSaveExercise.setBackgroundResource(R.drawable.white_rectangle)
                        tvSaveExercise.setTextColor(getColor(R.color.buttontextcolor))
                    }else{
                        tvSaveExercise.setBackgroundResource(R.drawable.rectangle_btn)
                        tvSaveExercise.setTextColor(getColor(R.color.subheadingcolor))
                    }
                }
            }
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
    val deleteRest = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            excerxiseModelList.removeAll { it.type == "rest" }
            showRests=false
            switchCompat.isChecked=showRests
            chooseExcerciseAdapter= ChooseExcerciseAdapter(
                excerxiseModelList,
                this@CalendarSchedulePersonalWorkoutActivity,
                switchCompat,
                getIntent().getStringExtra("type"), switchCompat.isChecked
            )
            recyclerExercise.adapter=chooseExcerciseAdapter

        }
    }

    private fun showEditRestDialog(){
        var selectedMinutes = 0
        var selectedSeconds = 0
        val dialog = Dialog(this@CalendarSchedulePersonalWorkoutActivity)
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
        totalExercises=0
        for (exercise in excerxiseModelList) {
            if(exercise.type == "exercise"){
                totalCalories += exercise.calories.trim().replace("null","0").toInt()
                total_duration += exercise.duration.trim().replace("null","0").toInt()
                totalExercises++
            }
        }
        tvExercises.text = "$totalExercises"
        tvCalories.text = totalCalories.toString()

        tvTime.text = ""+total_duration+"s"

    }

    fun deleteExercise(h: Int) {
        if (switchCompat.isChecked){
            if (h < excerxiseModelList.size-1 && excerxiseModelList[h+1].type.equals("rest")){
                excerxiseModelList.removeAt(h + 1)
                excerxiseModelList.removeAt(h)
            }else if (h < excerxiseModelList.size) {
                excerxiseModelList.removeAt(h)
            }
        }
        else{
            if (excerxiseModelList.size == 2) {
                val lastItem = excerxiseModelList.lastOrNull() // Safe way to get the last item
                val secondLastItem = excerxiseModelList.getOrNull(excerxiseModelList.size - 2) // Safe way to get second last

                if (lastItem != null && secondLastItem != null &&
                    secondLastItem.type == "exercise" && lastItem.type == "rest") {
                    // It's often clearer to remove by object if you're sure they are the last ones,
                    // but index is safer if the list could somehow change concurrently (unlikely here).
                    // Sticking to index removal for safety and consistency:
                    val originalSize = excerxiseModelList.size
                    excerxiseModelList.removeAt(originalSize - 1) // Remove last
                    excerxiseModelList.removeAt(originalSize - 2) // Remove what was second last

                    Log.d("ListUpdate", "Removed last exercise and its following rest (Kotlin check).")


                } else {
                    excerxiseModelList.removeAt(h)
                }
            } else {
                excerxiseModelList.removeAt(h)
            }

        }

        chooseExcerciseAdapter.notifyDataSetChanged()
        calorieDuration()
    }

}




