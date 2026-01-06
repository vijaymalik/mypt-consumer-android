package co.com.mypt.Goals

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.BarChart.RoundedBarWithTickCrossImageChart
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.IntakeAdapter
import co.com.mypt.adapter.IntakeModel
import com.android.volley.VolleyError
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class HydrationActivity : AppCompatActivity() {
    var glassSizeValue=""
    lateinit var intakeRecycler:RecyclerView
    lateinit var barChart: BarChart
    lateinit var glassSizeLayout: RelativeLayout
    lateinit var addWater: TextView
    var currentWaterLevel = 0
    var maxWaterLevel =0
    var waterLevelPercent = 0.0
    var intakeList :ArrayList<IntakeModel> = ArrayList()
    lateinit var waterIntakeProgress : ProgressBar
    lateinit var tvMaxWaterLevel : TextView
    lateinit var cardTodaysIntake : CardView
    lateinit var tvCurrentWaterLevel : TextView
    lateinit var tvWaterLevelPercent : TextView
    lateinit var tvml : TextView
    lateinit var tvGlassSize : TextView
    lateinit var tvGoal : TextView
    lateinit var tvMonthlyAverage : TextView
    lateinit var tvWeeklyAverage : TextView
    lateinit var tvAvgCompletion : TextView
    lateinit var  tvquantity : TextView
    lateinit var linearAddWater : LinearLayout
    lateinit var nested : NestedScrollView
    lateinit var tvDrinkFrequency : TextView
    lateinit var tvWeekDate : TextView
    lateinit var edQuantity : EditText
    lateinit var back_1 : ImageView
    lateinit var standard_bottom_sheet:RelativeLayout
    lateinit var standard_bottom_sheet_unit_picker:LinearLayout
    lateinit var goalBottomSheetDialog:BottomSheetDialog
    lateinit var unitPickerBottomSheet:BottomSheetDialog
    var selectedUnit = ""
    var isSelected=false
    var selectedTypeValue=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_hydration)
        goalBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        unitPickerBottomSheet = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        back_1=findViewById(R.id.back_1)
        nested=findViewById(R.id.nested)
        tvWaterLevelPercent=findViewById(R.id.waterLevelPercent)
        cardTodaysIntake=findViewById(R.id.cardTodaysIntake)
        tvMaxWaterLevel=findViewById(R.id.tvMaxWaterLevel)
        tvCurrentWaterLevel=findViewById(R.id.tvCurrentWaterLevel)
        waterIntakeProgress=findViewById(R.id.waterIntakeProgress)
        addWater=findViewById(R.id.addWater)

        linearAddWater=findViewById(R.id.linearAddWater)
        tvml=findViewById(R.id.tvml)
        tvMonthlyAverage=findViewById(R.id.tvMonthlyAverage)
        tvWeeklyAverage=findViewById(R.id.tvWeeklyAverage)
        tvAvgCompletion=findViewById(R.id.tvAvgCompletion)
        tvGoal=findViewById(R.id.tvGoal)
        tvGlassSize=findViewById(R.id.tvGlassSize)
        intakeRecycler=findViewById(R.id.intakeRecycler)
        barChart = findViewById(R.id.barChart)
        tvDrinkFrequency = findViewById(R.id.tvDrinkFrequency)
        glassSizeLayout = findViewById(R.id.glassSizeLayout)
        tvWeekDate = findViewById(R.id.tvWeekDate)
        gethydrationData()
        glassSizeLayout.setOnClickListener{
            var intent=Intent(this,GlassSizeActivity::class.java)
            intent.putExtra("glassSize",glassSizeValue)
            startActivity(intent)

        }
        tvGoal.setOnClickListener{
            goalbottomSheet(tvGoal.text.toString())

        }
        textShader(tvWaterLevelPercent)

        back_1.setOnClickListener {
            finish()
        }
/*
        tvml.setOnClickListener {
            val bottomSheet = layoutInflater.inflate(R.layout.select_unit_bottom_sheet, null)
            standard_bottom_sheet_unit_picker = bottomSheet.findViewById(R.id.standard_bottom_sheet)
            unitPickerBottomSheet.setCancelable(false)
            val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet_unit_picker)
            val layoutParams = standard_bottom_sheet_unit_picker.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
            standard_bottom_sheet_unit_picker.layoutParams = layoutParams
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

            var im_close =bottomSheet.findViewById<ImageView>(R.id.close)
            var unitPicker =bottomSheet.findViewById<NumberPicker>(R.id.unitPicker)
            var tvOk=bottomSheet.findViewById<TextView>(R.id.tvOk)

            val units = arrayOf("ml", "L")
            unitPicker.minValue = 0
            unitPicker.maxValue = units.size - 1
            unitPicker.displayedValues = units
            if (selectedUnit != ""){
                if(selectedUnit == "ml"){
                    unitPicker.value = 0
                }else
                    unitPicker.value = 1
            }

            im_close.setOnClickListener{
                unitPickerBottomSheet.dismiss()
            }

            tvOk.setOnClickListener{
                selectedUnit =""+unitPicker.value
                //getData()
                unitPickerBottomSheet.dismiss()
                Toast.makeText(this,selectedUnit,Toast.LENGTH_SHORT).show()
            }
            unitPickerBottomSheet.setContentView(bottomSheet)


            val window = unitPickerBottomSheet.window
            window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
            unitPickerBottomSheet.show()
        }*/
    }


    private fun goalbottomSheet(goal: String) {
        val bottomSheet = layoutInflater.inflate(R.layout.goalbottomsheet_layout, null)
        standard_bottom_sheet = bottomSheet.findViewById(R.id.standard_bottom_sheet)
        goalBottomSheetDialog.setCancelable(false)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        var im_close =bottomSheet.findViewById<ImageView>(R.id.im_close)
        var recommendedCheckbox =bottomSheet.findViewById<CheckBox>(R.id.recommendedCheckbox)
        var CustomCheckBox =bottomSheet.findViewById<CheckBox>(R.id.CustomCheckBox)
        var linearCancel =bottomSheet.findViewById<LinearLayout>(R.id.linearCancel)
        var tvSave =bottomSheet.findViewById<TextView>(R.id.tvSave)
        tvquantity =bottomSheet.findViewById<TextView>(R.id.tvquantity)
        edQuantity =bottomSheet.findViewById<EditText>(R.id.edQuantity)
        tvquantity.setText(goal)
        edQuantity.setText(goal)
        im_close.setOnClickListener{
            goalBottomSheetDialog.dismiss()
        }
        recommendedCheckbox.setOnClickListener{
            if (recommendedCheckbox.isChecked){
                isSelected=true
                selectedTypeValue=tvquantity.text.toString().replace(tvml.text.toString(),"")
                recommendedCheckbox.isChecked=true
                CustomCheckBox.isChecked=false
                tvSave.background = ContextCompat.getDrawable(this, R.drawable.update_plan_drawable)
                tvSave.setTextColor(resources.getColor(R.color.buttontextcolor))
            }else{
                isSelected=false
                selectedTypeValue=""
                recommendedCheckbox.isChecked=false
                tvSave.background = ContextCompat.getDrawable(this, R.drawable.grey_payment_drawable)
                tvSave.setTextColor(resources.getColor(R.color.headingcolor))
            }

        }
        CustomCheckBox.setOnClickListener{
            if (CustomCheckBox.isChecked){
                isSelected=true
                selectedTypeValue=edQuantity.text.toString().replace(tvml.text.toString(),"")
                recommendedCheckbox.isChecked=false
                CustomCheckBox.isChecked=true
                tvSave.background = ContextCompat.getDrawable(this, R.drawable.update_plan_drawable)
                tvSave.setTextColor(resources.getColor(R.color.buttontextcolor))
            }else{
                isSelected=false
                selectedTypeValue=""
                CustomCheckBox.isChecked=false
                tvSave.background = ContextCompat.getDrawable(this, R.drawable.grey_payment_drawable)
                tvSave.setTextColor(resources.getColor(R.color.headingcolor))
            }
        }
        tvSave.setOnClickListener{
            if (isSelected){
                UpdateGoalsData(selectedTypeValue)

            }
        }
        goalBottomSheetDialog.setContentView(bottomSheet)
        goalBottomSheetDialog.show()


        val window = goalBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

    }


    private fun UpdateGoalsData(selectedTypeValue: String) {
        val param: MutableMap<String, String> = HashMap()
        param["custom_value"] =""+selectedTypeValue
        param["id"] = ""+intent.getStringExtra("hydration_id")

        Log.e("glassVolumeParam", param.toString())

        PostMethod(ApiURL.makecustomgoal,param, applicationContext).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {

                try {
                    Log.e("glassVolumeRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        goalBottomSheetDialog.dismiss()
                        isSelected=false
                        gethydrationData()

                        Toast.makeText(applicationContext,resp.optString("msg"),Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext,resp.optString("msg"),Toast.LENGTH_LONG).show()

                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error!!.printStackTrace()
            }

        })
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
        tv.paint.setShader(textShader)
    }

    private fun gethydrationData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.getnutritiondata+intent.getStringExtra("hydration_id")

        Log.e("getHydrationdataAPi",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                intakeList.clear()
                Log.e("getHydrationDataResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if(jsonObj.optBoolean("status")){
                        nested.visibility=View.VISIBLE
                        tvMonthlyAverage.setText(jsonObj.optJSONObject("data").optJSONObject("report").optString("monthly_avg")+""+
                                jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit"))
                        tvWeeklyAverage.setText(jsonObj.optJSONObject("data").optJSONObject("report").optString("weekly_avg")+""+
                                jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit"))
                        tvAvgCompletion.setText(jsonObj.optJSONObject("data").optJSONObject("report").optString("avg_completion_time"))
                        tvDrinkFrequency.setText(jsonObj.optJSONObject("data").optJSONObject("report").optString("drink_frequency"))

                        tvml.setText(jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit"))
                        glassSizeValue=jsonObj.optJSONObject("data").optJSONObject("settings").optString("glass_size")
                        tvGlassSize.setText(jsonObj.optJSONObject("data").optJSONObject("settings").optString("glass_size")+
                                jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit"))
                        tvGoal.setText(jsonObj.optJSONObject("data").optJSONObject("settings").optString("goal")+" "+
                                jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit"))


                        if (jsonObj.optJSONObject("data").optJSONArray("today_logs").length()>0){
                            for (i in 0 until jsonObj.optJSONObject("data").optJSONArray("today_logs").length()) {
                                var intakeModel= IntakeModel()
                                var jsonObject=jsonObj.optJSONObject("data").optJSONArray("today_logs").optJSONObject(i)
                                intakeModel.ml=jsonObject.optString("quantity_ml")+jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit")
                                intakeModel.time=jsonObject.optString("time")
                                intakeList.add(intakeModel)
                            }
                            var intakeAdapter = IntakeAdapter(applicationContext, intakeList)
                            intakeRecycler.adapter = intakeAdapter
                            cardTodaysIntake.visibility= View.VISIBLE
                        }else{
                            cardTodaysIntake.visibility= View.GONE

                        }


                        maxWaterLevel= jsonObj.optJSONObject("data")!!.optJSONObject("goal").optInt("target")
                        currentWaterLevel= jsonObj.optJSONObject("data")!!.optInt("progress")


                        waterIntakeProgress.max = maxWaterLevel
                        waterIntakeProgress.progress = currentWaterLevel
                      //  waterLevelPercent = ((currentWaterLevel.toDouble() / maxWaterLevel.toDouble()) * 100)
                        tvWaterLevelPercent.text =jsonObj.optJSONObject("data")!!.optString("percentage") + " %"
                        addWater.text =jsonObj.optJSONObject("data")!!.optJSONObject("settings").optString("glass_size") +
                                jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit")
                        tvCurrentWaterLevel.text="$currentWaterLevel"
                        tvMaxWaterLevel.text="/$maxWaterLevel"+ jsonObj.optJSONObject("data").optJSONObject("settings").optString("unit")

                        linearAddWater.setOnClickListener {
                            addWaterData(jsonObj.optJSONObject("data")!!.optJSONObject("settings").optInt("glass_size"),)

                        }
                        val startdDate = convertDateFormat(jsonObj.optJSONObject("data").optJSONArray("weekly_chart").optJSONObject(0).optString("date"))
                        val enddDate = convertDateFormat(jsonObj.optJSONObject("data").optJSONArray("weekly_chart").optJSONObject(6).optString("date"))

                        tvWeekDate.setText(startdDate+" - "+ enddDate)

                        barchartData(jsonObj.optJSONObject("data").optJSONArray("weekly_chart"))


                    }else{
                        nested.visibility=View.GONE


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
    private fun barchartData(optJSONArray: JSONArray?) {

        // Weekly Calorie Data
        val entries = ArrayList<BarEntry>()
        val goals = ArrayList<String>()
        val dates = ArrayList<String>()
        for (i in 0 until optJSONArray!!.length()){
            val jsonObject=optJSONArray.optJSONObject(i)
            entries.add(BarEntry(i.toFloat(),jsonObject.optString("intake").toFloat()))
            goals.add(jsonObject.optString("is_completed"))
            dates.add(jsonObject.optString("date"))
        }

        // Create Bar Dataset
        val dataSet = BarDataSet(entries, "Calories").apply {
            setDrawValues(true)
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        val tickImage = BitmapFactory.decodeResource(
            getResources(),
            R.drawable.tick_with_circle
        )
        val crossImage = BitmapFactory.decodeResource(
            getResources(),
            R.drawable.cross_with_circle
        )

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.renderer = RoundedBarWithTickCrossImageChart(
            barChart,
            barChart.animator,
            barChart.viewPortHandler,
            Color.parseColor("#004FFF"),
            Color.parseColor("#00B8FB"),
            tickImage,
            crossImage,
            goals,
            dates
        )
        barData.barWidth = 0.4f

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                // Show the marker when a value is selected
                /*val mv = CustomMarkerView(this@HydrationActivity, R.layout.custom_bar_chart_marker_view) // Replace YourActivity with your activity's name
                mv.chartView = barChart
                barChart.marker = mv*/
                barChart.highlightValue(null)
                barChart.marker = null
                //barChart.highlightValue(h) // Highlight the selected value
            }

            override fun onNothingSelected() {
                // Hide the marker when nothing is selected
                barChart.highlightValue(null)
                barChart.marker = null
            }
        })
        // X-Axis Setup
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textColor=getColor(R.color.progress_track_color_1)
        xAxis.axisLineColor=getColor(R.color.progress_track_color)

        // Y-Axis Setup
        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
       // leftAxis.axisMaximum = 3000f
        leftAxis.textColor=getColor(R.color.progress_track_color_1)
        leftAxis.axisLineColor=getColor(R.color.progress_track_color)
        leftAxis.setDrawLabels(true)
        leftAxis.gridColor=getColor(R.color.progress_track_color)
        barChart.axisRight.isEnabled = false // Hide Right Y-Axis

        // Customize Chart Appearance
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)

        val legend = barChart.legend
        legend.isEnabled = false

        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }
    private fun addWaterData(glassSize: Int) {
        val param: MutableMap<String, String> = HashMap()
        param["id"] =""+intent.getStringExtra("hydration_id")
        param["value"] = addWater.text.toString().replace("ml","")

        Log.e("addNuitritionHydrationParam", param.toString())



        PostMethod(ApiURL.addnutritionhydration,param, applicationContext).startPostMethod(object : ResponseData{
            override fun response(data: String?) {

                try {
                    Log.e("addNutritionHydrationRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        currentWaterLevel += glassSize
                        if(currentWaterLevel <= maxWaterLevel) {
                            tvCurrentWaterLevel.text="$currentWaterLevel"
                            waterLevelPercent = ((currentWaterLevel.toDouble() / maxWaterLevel.toDouble()) * 100)
                            tvWaterLevelPercent.text = String.format("%.2f", waterLevelPercent) + " %"

                            val progressAnimator = ObjectAnimator.ofInt(waterIntakeProgress, "progress",
                                waterIntakeProgress.progress, currentWaterLevel)
                            progressAnimator.setDuration(1000)
                            progressAnimator.start()
                            gethydrationData()
                        }
                    }else{
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error!!.printStackTrace()
            }

        })
    }
    fun convertDateFormat(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

    override fun onResume() {
        super.onResume()
        gethydrationData()
    }
}

class TickCrossValueFormatter : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        return if (barEntry != null && barEntry.y > 2000) "✔" else "✖" // Condition for tick/cross
    }

}
