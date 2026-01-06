package co.com.mypt.Goals

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.BarChart.RoundedBarChart
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.BreakFastAdapter
import co.com.mypt.adapter.DinnerCalorieAdapter
import co.com.mypt.adapter.LunchCalorieListAdapter
import co.com.mypt.adapter.SnackCalorieAdapter
import co.com.mypt.model.BreakFastListModel
import co.com.mypt.model.DinnerListModel
import co.com.mypt.model.LunchListModel
import co.com.mypt.model.SnackListModel
import co.com.mypt.utils.CustomMarkerView
import com.android.volley.VolleyError
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject

class Calorie_IntakeActivity : AppCompatActivity() {
    lateinit var calorieprogressBar: ProgressBar
    lateinit var linearAddCalorie: LinearLayout
    lateinit var linearAddLunch: LinearLayout
    lateinit var linearSnackAdd: LinearLayout
    lateinit var linearAddDinner: LinearLayout
    lateinit var headerLayout: LinearLayout
    lateinit var breakfastRecycler: RecyclerView
    lateinit var LunchRecycler: RecyclerView
    lateinit var SnackRecycler: RecyclerView
    lateinit var dinnerRecycler: RecyclerView
    var progressStatus = 0
    val handler = Handler()
    lateinit var addCalorieBottomSheetDialog:BottomSheetDialog
    var breakFastArrayList = ArrayList<BreakFastListModel>()
    var lunchArrayList = ArrayList<LunchListModel>()
    var snackList = ArrayList<SnackListModel>()
    var dinnerList = ArrayList<DinnerListModel>()
    lateinit var editCalorieBottomSheetDialog:BottomSheetDialog
    lateinit var barChart:BarChart
    lateinit var nestedScroll:NestedScrollView
    lateinit var chart:ImageView
    lateinit var tvcalorieIntake:TextView
    lateinit var CalorieIntakeOf:TextView
    lateinit var tvLunchCalorie:TextView
    lateinit var tvBreakfastCalorie:TextView
    lateinit var tvSnackCalorie:TextView
    lateinit var tvml:TextView
    lateinit var tvRemainingCalorie:TextView
    lateinit var tvdinnerCalorie:TextView
    lateinit var textCalories:TextInputEditText
    lateinit var textMeal:TextInputEditText
    var calorieType=""
    var selectType=""
    var total_progress=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_intake)
        calorieprogressBar=findViewById(R.id.calorieprogressBar)
        linearAddCalorie=findViewById(R.id.linearAddCalorie)
        tvBreakfastCalorie=findViewById(R.id.tvBreakfastCalorie)
        breakfastRecycler=findViewById(R.id.breakfastRecycler)
        LunchRecycler=findViewById(R.id.LunchRecycler)
        barChart = findViewById(R.id.barChart)
        headerLayout = findViewById(R.id.headerLayout)
        tvSnackCalorie = findViewById(R.id.tvSnackCalorie)
        SnackRecycler=findViewById(R.id.SnackRecycler)
        dinnerRecycler=findViewById(R.id.dinnerRecycler)
        linearAddLunch=findViewById(R.id.linearAddLunch)
        linearSnackAdd=findViewById(R.id.linearSnackAdd)
        linearAddDinner=findViewById(R.id.linearAddDinner)
        nestedScroll=findViewById(R.id.nestedScroll)
        chart=findViewById(R.id.chart)

        tvcalorieIntake=findViewById(R.id.tvcalorieIntake)
        CalorieIntakeOf=findViewById(R.id.CalorieIntakeOf)
        tvRemainingCalorie=findViewById(R.id.tvRemainingCalorie)
        tvLunchCalorie=findViewById(R.id.tvLunchCalorie)
        tvdinnerCalorie=findViewById(R.id.tvdinnerCalorie)
        headerLayout.setOnClickListener {
            finish()
        }
        getCalorieData()
        chart.setOnClickListener {
            nestedScroll.scrollToBottom()
        }


        linearAddCalorie.setOnClickListener{
            calorieType="breakfast"
            addCalorieBottomSheetDialog.show()
        }
        linearAddLunch.setOnClickListener{
            calorieType="lunch"
            addCalorieBottomSheetDialog.show()
        }
        linearSnackAdd.setOnClickListener{
            calorieType="snack"
            addCalorieBottomSheetDialog.show()
        }
        linearAddDinner.setOnClickListener{
            calorieType="dinner"

            addCalorieBottomSheetDialog.show()
        }
    }


    private fun getCalorieData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.getnutritiondata+intent.getStringExtra("calorieIntake_id")

        Log.e("getnutritiondataAPi",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("getnutritionDataResponse",data.toString())
                breakFastArrayList.clear()
                lunchArrayList.clear()
                snackList.clear()
                dinnerList.clear()
                try {
                    val jsonObj = JSONObject(data!!)
                    if(jsonObj.optBoolean("status")){
                        nestedScroll.visibility=View.VISIBLE
                        tvcalorieIntake.setText(intent.getStringExtra("calorieIntake"))
                        calorieprogressBar.setProgress(jsonObj.optJSONObject("data").optInt("progress"))
                        CalorieIntakeOf.setText((jsonObj.optJSONObject("data").optString("progress"))+" of "+jsonObj.optJSONObject("data").optJSONObject("goal").optString("target"))
                        var weeklyChartJsonArray=jsonObj.optJSONObject("data").optJSONArray("weekly_chart")
                        tvRemainingCalorie.setText("Remaining: "+(jsonObj.optJSONObject("data").optString("remaining")))
                        total_progress=
                            jsonObj.optJSONObject("data").optJSONObject("goal").optInt("target").toString()
                        calorieprogressBar.max = total_progress.toInt()
                       // progressStatus=jsonObj.optJSONObject("data").optInt("progress")


                        tvBreakfastCalorie.setText(jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("breakfast").optString("total_calories")
                        +" of "+jsonObj.optJSONObject("data").optJSONObject("goal").optInt("target"))

                        var breakfastJsonArray=jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("breakfast").optJSONArray("logs")

                        if (breakfastJsonArray.length()>0){
                            for (i in 0 until breakfastJsonArray.length()){
                                var breakfastJsonObject=breakfastJsonArray.optJSONObject(i)
                                var breakfastListModel= BreakFastListModel()
                                breakfastListModel.name = breakfastJsonObject.optString("meal_name")
                                breakfastListModel.calories = breakfastJsonObject.optString("calories")
                                breakfastListModel.id = breakfastJsonObject.optString("id")
                                breakFastArrayList.add(breakfastListModel)
                            }
                            var breakfastAdapter = BreakFastAdapter(applicationContext, breakFastArrayList,this@Calorie_IntakeActivity,"breakfast")
                            breakfastRecycler.adapter = breakfastAdapter
                            breakfastRecycler.visibility=View.VISIBLE
                        }else{
                            breakfastRecycler.visibility=View.GONE

                        }

                        tvLunchCalorie.setText(jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("lunch").optString("total_calories")
                        +" of "+jsonObj.optJSONObject("data").optJSONObject("goal").optInt("target"))

                        var lunchJsonArray=jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("lunch").optJSONArray("logs")
                        if (lunchJsonArray.length()>0){
                            for (i in 0 until lunchJsonArray.length()){
                                var breakfastJsonObject=lunchJsonArray.optJSONObject(i)
                                var lunchListModel= LunchListModel()
                                lunchListModel.name = breakfastJsonObject.optString("meal_name")
                                lunchListModel.calories = breakfastJsonObject.optString("calories")
                                lunchListModel.id = breakfastJsonObject.optString("id")
                                lunchArrayList.add(lunchListModel)
                            }
                            var lunchCalorieAdapter = LunchCalorieListAdapter(applicationContext, lunchArrayList,this@Calorie_IntakeActivity,"lunch")
                            LunchRecycler.adapter = lunchCalorieAdapter
                            LunchRecycler.visibility=View.VISIBLE
                        }else{
                            LunchRecycler.visibility=View.GONE

                        }

                        tvSnackCalorie.setText(jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("snack").optString("total_calories")
                        +" of "+jsonObj.optJSONObject("data").optJSONObject("goal").optInt("target"))

                        var SnackJsonArray=jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("snack").optJSONArray("logs")
                        if (SnackJsonArray.length()>0){
                            for (i in 0 until SnackJsonArray.length()){
                                var breakfastJsonObject=SnackJsonArray.optJSONObject(i)
                                var snackListModel= SnackListModel()
                                snackListModel.name = breakfastJsonObject.optString("meal_name")
                                snackListModel.calories = breakfastJsonObject.optString("calories")
                                snackListModel.id = breakfastJsonObject.optString("id")
                                snackList.add(snackListModel)
                            }
                            var snackCalorieAdapter = SnackCalorieAdapter(applicationContext, snackList,this@Calorie_IntakeActivity,"snack")
                            SnackRecycler.adapter = snackCalorieAdapter
                            SnackRecycler.visibility=View.VISIBLE
                        }else{
                            SnackRecycler.visibility=View.GONE

                        }

                        tvdinnerCalorie.setText(jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("dinner").optString("total_calories")
                        +" of "+jsonObj.optJSONObject("data").optJSONObject("goal").optInt("target"))

                        var dinnerJsonArray=jsonObj.optJSONObject("data").optJSONObject("logs").optJSONObject("dinner").optJSONArray("logs")
                        if (dinnerJsonArray.length()>0){
                            for (i in 0 until dinnerJsonArray.length()){
                                var breakfastJsonObject=dinnerJsonArray.optJSONObject(i)
                                var dinnerListModel= DinnerListModel()
                                dinnerListModel.name = breakfastJsonObject.optString("meal_name")
                                dinnerListModel.calories = breakfastJsonObject.optString("calories")
                                dinnerListModel.id = breakfastJsonObject.optString("id")
                                dinnerList.add(dinnerListModel)
                            }
                            var dinnerCalorieAdapter = DinnerCalorieAdapter(applicationContext, dinnerList,this@Calorie_IntakeActivity,"dinner")
                            dinnerRecycler.adapter = dinnerCalorieAdapter
                            dinnerRecycler.visibility=View.VISIBLE
                        }else{
                            dinnerRecycler.visibility=View.GONE

                        }

                        aDDcalorieBottomSheet(total_progress)

                        barchartData(weeklyChartJsonArray,jsonObj.optJSONObject("data").optJSONObject("goal").optInt("target"),jsonObj.optJSONObject("data").optJSONObject("goal").optString("unit"))



                    }else{
                        nestedScroll.visibility=View.GONE

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

    private fun barchartData(weeklyChartJsonArray: JSONArray?, targetValue: Int, target_unit: String) {
        Log.e("targetvalue",""+targetValue)
        // Weekly Calorie Data
        val entries = ArrayList<BarEntry>()

        for (i in 0 until weeklyChartJsonArray!!.length()){
            var jsonObject=weeklyChartJsonArray.optJSONObject(i)
            entries.add(BarEntry(i.toFloat(),jsonObject.optString("calories").toFloat()))
        }

        // Create Bar Dataset
        val dataSet = BarDataSet(entries, "Calories").apply {
            setDrawValues(false)
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }
        // Set Gradient to Bars
        /*val gradientDrawable = getDrawable(R.drawable.bar_gradient) as GradientDrawable
        dataSet.setGradientColor(gradientDrawable.colors?.get(0) ?: Color.BLUE, gradientDrawable.colors?.get(1) ?: Color.CYAN)*/

        // Apply Custom Renderer for Rounded Corners

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.renderer = RoundedBarChart(
            barChart,
            barChart.animator,
            barChart.viewPortHandler,
            Color.parseColor("#005B50"),
            Color.parseColor("#00C1AA")
        )
        barData.barWidth = 0.4f

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                // Show the marker when a value is selected
                val mv = CustomMarkerView(this@Calorie_IntakeActivity, R.layout.custom_bar_chart_marker_view) // Replace YourActivity with your activity's name
                mv.chartView = barChart
                barChart.marker = mv
                barChart.highlightValue(h) // Highlight the selected value
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
        val currentMax = leftAxis.mAxisMaximum  // get current max

        if (targetValue > currentMax) {
            leftAxis.axisMaximum = targetValue + 1000f  // optional padding
        }
        else
            leftAxis.axisMaximum = currentMax+1000f
        leftAxis.textColor=getColor(R.color.progress_track_color_1)
        leftAxis.axisLineColor=getColor(R.color.progress_track_color)
        leftAxis.setDrawLabels(false)
        leftAxis.gridColor=getColor(R.color.progress_track_color)
        barChart.axisRight.isEnabled = false // Hide Right Y-Axis

        // Dashed Limit Line at 2200 Calories
        val limitLine = LimitLine(targetValue.toFloat(), targetValue.toString()+""+target_unit).apply {
            lineWidth = 2f
            lineColor = Color.parseColor("#31343A")
            textColor = Color.parseColor("#606060")
            textSize = 12f
            enableDashedLine(10f, 5f, 0f)
        }
        leftAxis.addLimitLine(limitLine)

        // Customize Chart Appearance
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)

        barChart.legend.isEnabled = false

        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }

    private fun aDDcalorieBottomSheet(total_progress: String) {

        addCalorieBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.add_calorie_bottomsheet_dialog, null)
        addCalorieBottomSheetDialog.setContentView(bottomSheet)


        textCalories =bottomSheet.findViewById<TextInputEditText>(R.id.textCalories)
        textMeal =bottomSheet.findViewById<TextInputEditText>(R.id.textMeal)
        val tvSave =bottomSheet.findViewById<TextView>(R.id.tvSave)
        val tvcalorietakemsg =bottomSheet.findViewById<TextView>(R.id.tvcalorietakemsg)
        tvcalorietakemsg.setText("${total_progress} Calories intake is recommended as per your body profile")
        tvSave.setOnClickListener{
            if (textMeal.getText().toString().trim { it <= ' ' }.matches("".toRegex())) {
                textMeal.setError(resources.getString(R.string.enter_meal_name))
            } else if (textCalories.getText().toString().trim { it <= ' ' }.matches("".toRegex()) ||  textCalories.getText().toString().equals("0") ){
                textCalories.setError(resources.getString(R.string.enter_calorie))
            }
            else {
                addCalorieData(
                    textMeal.text.toString(),
                    textCalories.text.toString(),
                    "",
                    calorieType
                )
            }
        }
        val window = addCalorieBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(addCalorieBottomSheetDialog)
      /*  addCalorieBottomSheetDialog.setOnShowListener {
            val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }*/
        addCalorieBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    private fun addCalorieData(
        textmeal: String,
        textcalorie: String,
        id: String,
        calorieType: String
    ) {
        val param: MutableMap<String, String> = HashMap()
        param["id"] =""+intent.getStringExtra("calorieIntake_id")
        param["type"] = calorieType
        param["value"] = textcalorie
        param["meal_name"] = textmeal
        param["meal_id"] =id
        Log.e("addFoodNuitritionParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.addnutrition_calories,param, applicationContext).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("addNutritionCalorieRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        if (id.equals(""))
                            addCalorieBottomSheetDialog.dismiss()
                        else
                            editCalorieBottomSheetDialog.dismiss()
                        getCalorieData()
                        Toast.makeText(this@Calorie_IntakeActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@Calorie_IntakeActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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

    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    fun editCalorieBottomSheet(id: String, name: String, calories: String, calorieType: String) {
        editCalorieBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.edit_calorie_bottomsheet_dialog, null)
        editCalorieBottomSheetDialog.setContentView(bottomSheet)


        val edCalories =bottomSheet.findViewById<TextInputEditText>(R.id.edCalories)
        val edMeal =bottomSheet.findViewById<TextInputEditText>(R.id.edMeal)
        val tvSaveEdit =bottomSheet.findViewById<TextView>(R.id.tvSaveEdit)
        edCalories.setText(calories)
        edMeal.setText(name)
        tvSaveEdit.setOnClickListener{
            if (edMeal.getText().toString().trim { it <= ' ' }.matches("".toRegex())) {
                edMeal.setError(resources.getString(R.string.enter_meal_name))
            } else if (edCalories.getText().toString().trim { it <= ' ' }.matches("".toRegex()) ||  edCalories.getText().toString().equals("0") ){
                edCalories.setError(resources.getString(R.string.enter_calorie))
            }
            else {
                addCalorieData(edMeal.text.toString(),edCalories.text.toString(),id,calorieType)
            }
        }

        val window = editCalorieBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(editCalorieBottomSheetDialog)
        /*  addCalorieBottomSheetDialog.setOnShowListener {
              val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
              bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
              bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
          }*/
        editCalorieBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editCalorieBottomSheetDialog.show()

    }
    private fun setupBarChart() {
        val calorieData = listOf(2000f, 1800f, 2200f, 2500f, 1900f, 2300f, 2100f)
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val entries = calorieData.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }

        val barDataSet = BarDataSet(entries, "Calories (kcal)").apply {
            color = Color.parseColor("#FF6200EE")
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f // Set bar width

        barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)

            // Customize X-Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return days.getOrNull(value.toInt()) ?: value.toString()
                    }
                }
            }

            // Customize Y-Axis
            axisLeft.apply {
                axisMinimum = 1500f // Minimum calorie value
                axisMaximum = 3000f // Maximum calorie value
                granularity = 500f
            }

            axisRight.isEnabled = false // Hide right Y-Axis

            animateY(1000)
        }
    }

    private fun NestedScrollView.scrollToBottom() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY+ height)
        smoothScrollBy(0, delta)
    }

}