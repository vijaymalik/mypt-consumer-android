package co.com.mypt.Profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.BarChart.RoundedBarChart
import co.com.mypt.BarChart.RoundedBarChartWithThreeColors
import co.com.mypt.More.HealthStatusActivity
import co.com.mypt.More.MyTrainerActivity
import co.com.mypt.More.SettingsActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.adapter.AwardsAdapter
import co.com.mypt.adapter.HealthPreferenceAdapter
import co.com.mypt.adapter.MyStreakAdapter
import co.com.mypt.adapter.PlanAdapter
import co.com.mypt.model.AwardsModel
import co.com.mypt.model.HealthModel
import co.com.mypt.model.PlanModel
import co.com.mypt.model.StreakModel
import co.com.mypt.utils.CustomMarkerView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import org.json.JSONArray
import org.json.JSONObject

class NewUserProfileActivity : AppCompatActivity() {
    var isUpgrade=""
    var plan_name=""
    var remaining_session=""
    var total_sessions=""
    var remaining_days=""
    var total_days=""
    lateinit var awardRecycler:RecyclerView
    lateinit var healthRecycle:RecyclerView
    lateinit var linearAward:LinearLayout
    lateinit var linearTrainerList:LinearLayout
    lateinit var linearNoAward:LinearLayout
    lateinit var tvCalorie:TextView
    lateinit var tvkcal:TextView
    lateinit var tvplanName:TextView
    lateinit var imSheild:ImageView
    lateinit var tvRemainingSession:TextView
    lateinit var plan_progress:ProgressBar
    lateinit var tvml:TextView
    lateinit var tvMember:TextView
    lateinit var tvHydration:TextView
    lateinit var tvmsg:TextView
    lateinit var tvdays:TextView
    lateinit var tvStreak:TextView
    lateinit var tvNotification:TextView
    lateinit var tvUpdateLocation:TextView
    lateinit var tvUpdateDefine:TextView
    lateinit var tvname:TextView
    lateinit var tvNoHealth:TextView
    lateinit var tvTrainerCount:TextView
    lateinit var back_1:ImageView
    lateinit var imtrainer1:ImageView
    lateinit var improfile:ImageView
    lateinit var imHeath:ImageView
    lateinit var imred:ImageView
    lateinit var tvVersionCode:TextView
    lateinit var imTrainer3:ImageView
    lateinit var imTraineer2:ImageView
    lateinit var imAwardBack:ImageView
    lateinit var coverImage:ImageView
    lateinit var linearMyPTScore:LinearLayout
    lateinit var linearPlan:LinearLayout
    lateinit var linearSetting:LinearLayout
    lateinit var linearNoPlan:LinearLayout
    lateinit var linearLocation:LinearLayout
    lateinit var linearStats:LinearLayout
    lateinit var headerLayout1:LinearLayout
    lateinit var linearHealthPreference:LinearLayout
    lateinit var linearNoTrainer:LinearLayout
    lateinit var linearAchievmentTrack:LinearLayout
    lateinit var barChartCalorie:BarChart
    lateinit var linearPersonalInfo:LinearLayout
    lateinit var StatsLinear:LinearLayout
    lateinit var linearAcitivity:LinearLayout
    lateinit var recyclerStreak:RecyclerView
    lateinit var planRecyclerview:RecyclerView
    lateinit var cardTraniner3:CardView
    lateinit var cardTrainer2:CardView
    lateinit var cardTrainer1:CardView
    lateinit var cardTrainerCount:CardView
    lateinit var linearPreferenceHealth:LinearLayout
    var awardsArrayList = ArrayList<AwardsModel>()
    var healthArrayList = ArrayList<HealthModel>()
    var planArrayList = ArrayList<PlanModel>()
    lateinit var barChart: BarChart
    val highThreshold = 2400f
    val mediumThreshold = 2000f
    private lateinit var lineChart: LineChart
    var streakArrayList = ArrayList<StreakModel>()
    lateinit var nested:NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_profile)
        linearSetting=findViewById(R.id.linearSetting)
        linearNoAward=findViewById(R.id.linearNoAward)
        awardRecycler=findViewById(R.id.awardRecycler)
        imred=findViewById(R.id.imred)
        StatsLinear=findViewById(R.id.StatsLinear)
        planRecyclerview=findViewById(R.id.planRecyclerview)
        nested=findViewById(R.id.nested)
        tvUpdateDefine=findViewById(R.id.tvUpdateDefine)
        linearNoPlan=findViewById(R.id.linearNoPlan)
        cardTraniner3=findViewById(R.id.cardTraniner3)
        cardTrainer2=findViewById(R.id.cardTrainer2)
        cardTrainer1=findViewById(R.id.cardTrainer1)
        imTrainer3=findViewById(R.id.imTrainer3)
        imTraineer2=findViewById(R.id.imTraineer2)
        imAwardBack=findViewById(R.id.imAwardBack)
        coverImage=findViewById(R.id.coverImage)
        linearAcitivity=findViewById(R.id.linearAcitivity)
        linearMyPTScore=findViewById(R.id.linearMyPTScore)
        imtrainer1=findViewById(R.id.imtrainer1)
        cardTrainerCount=findViewById(R.id.cardTrainerCount)
        tvTrainerCount=findViewById(R.id.tvTrainerCount)
        tvname=findViewById(R.id.tvname)
        tvNoHealth=findViewById(R.id.tvNoHealth)
        back_1=findViewById(R.id.back_1)
        tvmsg=findViewById(R.id.tvmsg)
        tvVersionCode=findViewById(R.id.tvVersionCode)
        tvNotification=findViewById(R.id.tvNotification)
        linearTrainerList=findViewById(R.id.linearTrainerList)
        linearNoTrainer=findViewById(R.id.linearNoTrainer)
        improfile=findViewById(R.id.improfile)
        imHeath=findViewById(R.id.imHeath)
        linearHealthPreference=findViewById(R.id.linearHealthPreference)
        plan_progress=findViewById(R.id.plan_progress)
        linearAward=findViewById(R.id.linearAward)
        linearPlan=findViewById(R.id.linearPlan)
        headerLayout1=findViewById(R.id.headerLayout1)
        linearAchievmentTrack=findViewById(R.id.linearAchievmentTrack)
        recyclerStreak=findViewById(R.id.recyclerStreak)
        linearPersonalInfo=findViewById(R.id.linearPersonalInfo)

        barChart = findViewById(R.id.barChart)
        linearLocation = findViewById(R.id.linearLocation)
        linearStats = findViewById(R.id.linearStats)
        tvMember = findViewById(R.id.tvMember)
        tvplanName = findViewById(R.id.tvplanName)
        barChartCalorie   = findViewById(R.id.barChartCalorie)
        healthRecycle = findViewById(R.id.healthRecycle)
        lineChart = findViewById(R.id.lineChart)
        tvCalorie = findViewById(R.id.tvCalorie)
        tvkcal = findViewById(R.id.tvkcal)
        imSheild = findViewById(R.id.imSheild)
        tvRemainingSession = findViewById(R.id.tvRemainingSession)
        tvml = findViewById(R.id.tvml)
        tvdays = findViewById(R.id.tvdays)
        tvUpdateLocation = findViewById(R.id.tvUpdateLocation)
        tvStreak = findViewById(R.id.tvStreak)
        tvHydration = findViewById(R.id.tvHydration)
        var streakAdapter= MyStreakAdapter(applicationContext,streakArrayList)
        recyclerStreak.adapter=streakAdapter
        caloriebarchartData()
        headerLayout1.setOnClickListener{
         finish()
        }
        back_1.setOnClickListener{
         finish()
        }
        linearSetting.setOnClickListener{
            var intent= Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        linearAward.setOnClickListener{
            var intent= Intent(this, AwardAllActivity::class.java)
            startActivity(intent)
        }
        linearHealthPreference.setOnClickListener{
            var intent= Intent(this, HealthStatusActivity::class.java)
            intent.putExtra("selectedType","1")
            startActivity(intent)
        }
        StatsLinear.setOnClickListener{
            var intent= Intent(this, HealthStatusActivity::class.java)
            intent.putExtra("selectedType","2")
            startActivity(intent)
        }
        linearNoPlan.setOnClickListener{
            var intent= Intent(this, HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }


        linearPersonalInfo.setOnClickListener{
            var intent= Intent(this, PersonInfoActivity::class.java)
            startActivity(intent)
        }

        val currentVersion = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName

        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown"
        }

        tvVersionCode.text = "V$currentVersion"
        tvUpdateDefine.text = resources.getString(R.string.CurrentVersion)

        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {

                // Compare with your current version and act accordingly
                imred.visibility=View.VISIBLE
                tvUpdateDefine.text = resources.getString(R.string.app_update_available)

                var  latestVersionCode = appUpdateInfo.availableVersionCode()
                Log.e("latest",""+latestVersionCode)
                tvVersionCode.text = "V$latestVersionCode"
            }
        }

        HydrationLineChart()
        textShader(tvCalorie)
        textShader(tvkcal)
        textShader(tvHydration)
        textShader(tvml)
        textShader(tvdays)

    }


    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF")
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
    private fun HydrationLineChart() {
        // 1. Generate chart data
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 200f))
        entries.add(Entry(1f, 250f))
        entries.add(Entry(2f, 180f))
        entries.add(Entry(3f, 300f))
        entries.add(Entry(4f, 220f))
        entries.add(Entry(5f, 350f))

        // 2. Create a LineDataSet from the data
        val dataSet = LineDataSet(entries, "Hydration")
        dataSet.color = Color.parseColor("#00C1AA")// Line color
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawCircles(false)

        dataSet.setDrawValues(false)
        dataSet.setValueTextColor(Color.WHITE)

        dataSet.lineWidth = 2f
        /*dataSet.setDrawCircles(true)  // Show circles on data points
        dataSet.setCircleColor(Color.BLUE)
        dataSet.setCircleRadius(5f)
        dataSet.setDrawValues(false)*/   // Hide values above the data points

        // 3.  Apply the gradient fill
        if (Utils.getSDKInt() >= 18) {
            val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.sea_green_gradient)
            dataSet.fillDrawable = drawable
            dataSet.setDrawFilled(true)
        } else {
            dataSet.fillColor = Color.parseColor("#00C1AA")
            dataSet.setDrawFilled(true) // Still need to enable filling even with a solid color
        }

        // 4. Create a LineData object with the DataSet
        val lineData = LineData(dataSet)

        // 5. Set the data to the chart
        lineChart.data = lineData

        // 6.  Customize the chart (optional)
        lineChart.isHighlightPerTapEnabled = true
        lineChart.description.isEnabled = false // Remove description label
        lineChart.xAxis.setDrawGridLines(false) // Remove x-axis grid lines

        lineChart.axisLeft.gridColor = Color.parseColor("#343739")

        lineChart.axisLeft.setDrawGridLines(true)
        lineChart.axisLeft.setDrawAxisLine(false)
        lineChart.axisRight.isEnabled = false  // Remove right y-axis
        lineChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM // X-Axis at the bottom



        //X-Axis Values
        val xAxis = lineChart.xAxis
        xAxis.textColor = Color.parseColor("#959595")
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Set the position of the X-axis
        xAxis.granularity = 1f // Optional: set to 1f to avoid displaying fractional values
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        xAxis.setDrawAxisLine(false)

        //Y-Axis Values
        val yAxis = lineChart.axisLeft // Or chart.axisRight for the right Y-axis
        yAxis.textColor = Color.parseColor("#50535B")
        yAxis.axisMinimum = 0f      // Set the minimum value to 0
        yAxis.axisMaximum = 500f    // Set the maximum value to 500
        yAxis.granularity = 100f
        yAxis.setDrawGridLines(false)
        yAxis.setDrawLabels(false)
        yAxis.setDrawAxisLine(false)

        val limitLine = LimitLine(200f, "").apply {
            lineWidth = 2f
            lineColor = Color.parseColor("#FFFFFF")

            enableDashedLine(10f, 5f, 0f)
        }
        yAxis.addLimitLine(limitLine)

        val legend = lineChart.legend
        legend.isEnabled = false
        // 7. Refresh the chart
        lineChart.invalidate() // Refreshes the chart to display the data
    }


    private fun caloriebarchartData() {

        val values = listOf(1800f, 2000f, 2200f, 1900f, 2500f, 2100f, 2300f)
        val entries = mutableListOf<BarEntry>()
        val colors = mutableListOf<Int>()

        for ((index, value) in values.withIndex()) {
            entries.add(BarEntry(index.toFloat(), value))
            val color = when {
                value >= highThreshold -> Color.parseColor("#F38D1B")
                value >= mediumThreshold -> Color.parseColor("#F6AA54")
                else -> Color.parseColor("#F9C78D")
            }
            colors.add(color)
        }

        // Create Bar Dataset
        val dataSet = BarDataSet(entries, "Calories").apply {
            setDrawValues(false)
            setColors(colors)
        }
        // Set Gradient to Bars
        /*val gradientDrawable = getDrawable(R.drawable.bar_gradient) as GradientDrawable
        dataSet.setGradientColor(gradientDrawable.colors?.get(0) ?: Color.BLUE, gradientDrawable.colors?.get(1) ?: Color.CYAN)*/

        // Apply Custom Renderer for Rounded Corners

        val barData = BarData(dataSet)
        barChartCalorie.data = barData

        barChartCalorie.renderer = RoundedBarChartWithThreeColors(
            barChartCalorie,
            barChartCalorie.animator,
            barChartCalorie.viewPortHandler,
        )
        barData.barWidth = 0.6f
        (barChartCalorie.renderer as RoundedBarChartWithThreeColors).setRadius(25f)

        // X-Axis Setup
        val xAxis = barChartCalorie.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f



        // Y-Axis Setup
        val leftAxis: YAxis = barChartCalorie.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.granularity = 1f
        //leftAxis.axisMaximum = 3000f
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        barChartCalorie.axisRight.isEnabled = false // Hide Right Y-Axis


        // Customize Chart Appearance
        barChartCalorie.description.isEnabled = false
        barChartCalorie.setFitBars(true)
        barChartCalorie.animateY(1000)
        barChartCalorie.legend.isEnabled = false

        barChartCalorie.notifyDataSetChanged()
        barChart.invalidate()
    }
    private fun barchartData(optJSONArray: JSONArray?) {
        val entries = ArrayList<BarEntry>()
        for (i in 0 until optJSONArray!!.length()){
            var jsonObject=optJSONArray.optJSONObject(i)
            entries.add(BarEntry(i.toFloat(),jsonObject.optString("score").toFloat()))
        }
        // Create Bar Dataset
        val dataSet = BarDataSet(entries, "Score").apply {
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
                val mv = CustomMarkerView(this@NewUserProfileActivity, R.layout.custom_bar_chart_marker_view) // Replace YourActivity with your activity's name
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
        leftAxis.granularity = 1f
        //leftAxis.axisMaximum = 3000f
        leftAxis.textColor=getColor(R.color.progress_track_color_1)
        leftAxis.axisLineColor=getColor(R.color.progress_track_color)
        //leftAxis.setDrawLabels(false)
        leftAxis.gridColor=getColor(R.color.progress_track_color)
        barChart.axisRight.isEnabled = false // Hide Right Y-Axis
/*
        // Dashed Limit Line at 2200 Calories
        val limitLine = LimitLine(2200f, "Calorie Limit").apply {
            lineWidth = 2f
            lineColor = Color.parseColor("#31343A")
            textColor = Color.parseColor("#606060")
            textSize = 12f
            enableDashedLine(10f, 5f, 0f)
        }
        leftAxis.addLimitLine(limitLine)*/

        // Customize Chart Appearance
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)

        barChart.legend.isEnabled = false

        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }
    private fun getUserDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api= ApiURL.userprofile
        Log.e("UserProfile",""+api)
        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                awardsArrayList.clear()
                planArrayList.clear()
                Log.e("getprofileResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        nested.visibility=View.VISIBLE
                        var jsonObjectData=jsonObj.optJSONObject("data")
                        tvname.text = jsonObjectData.optString("name")

                        Glide.with(applicationContext!!).load(jsonObjectData.optString("image")).fitCenter().error(R.drawable._no_image).into(improfile)
                        Glide.with(applicationContext!!).load(jsonObjectData.optString("cover_image")).fitCenter().error(R.drawable.profile_background).into(coverImage)
                        if (jsonObjectData.optJSONObject("location").optString("address").equals("")){
                            tvUpdateLocation.text = applicationContext.resources.getString(R.string.update_your_location)
                            tvUpdateLocation.setOnClickListener{
                                var intent=Intent(applicationContext,PersonInfoActivity::class.java)
                                intent.putExtra("updateLocation","imageLocation")
                                startActivity(intent)
                            }
                        }else{
                            tvUpdateLocation.text = jsonObjectData.optJSONObject("location").optString("address")
                        }


                        if (jsonObjectData.optJSONArray("otherSubscriptions").length()>0){
                            planRecyclerview.visibility= View.VISIBLE
                            tvMember.visibility= View.VISIBLE
                            linearNoPlan.visibility= View.GONE
                            plan_name=jsonObjectData.optJSONObject("plan").optString("getTier")
                            Log.e("plan_name",""+plan_name+" Member")
                            if (plan_name.equals("Silver")){
                                tvMember.setBackgroundResource(R.drawable.silver_plan_drawable)
                            }
                            else if (plan_name.equals("Gold")){
                                tvMember.setBackgroundResource(R.drawable.gold_member_drawable)
                            }
                            else if (plan_name.equals("Platinum")){
                                tvMember.setBackgroundResource(R.drawable.silver_plan_drawable)
                            }else{
                                tvMember.setBackgroundResource(R.drawable.vip_plan_drawable)
                            }
                            tvMember.setText(plan_name+" Member")
                            for (i in 0 until jsonObjectData.optJSONArray("otherSubscriptions").length()){
                                var jsonObject=jsonObjectData.optJSONArray("otherSubscriptions").optJSONObject(i)
                                var planModel= PlanModel()
                                planModel.isPackage=jsonObject.optString("isPackage")
                                planModel.getTier=jsonObject.optString("getTier")
                                planModel.isUpgrade=jsonObject.optString("isUpgrade")
                                planModel.remaining_sessions=jsonObject.optString("remaining_sessions")
                                planModel.total_sessions=jsonObject.optString("total_sessions")
                                planModel.remaining_days=jsonObject.optString("remaining_days")
                                planModel.total_days=jsonObject.optString("total_days")
                                planModel.tier_image=jsonObject.optString("tier_image")
                                planModel.plan_id=jsonObject.optString("plan_id")
                                planArrayList.add(planModel)
                            }
                            var planAdapter= PlanAdapter(applicationContext,planArrayList)
                            planRecyclerview.adapter=planAdapter

                        }else{
                            planRecyclerview.visibility= View.GONE
                            linearNoPlan.visibility= View.VISIBLE
                        }




                        if (jsonObjectData.optJSONArray("trainers").length()>0){
                            linearTrainerList.visibility=View.VISIBLE
                            linearNoTrainer.visibility=View.GONE
                            linearTrainerList.setOnClickListener{
                                var intent=Intent(this@NewUserProfileActivity,MyTrainerActivity::class.java)
                                startActivity(intent)
                            }

                        }else{
                            linearTrainerList.visibility=View.GONE
                            linearNoTrainer.visibility=View.VISIBLE
                            linearNoTrainer.setOnClickListener{
                                var intent=Intent(this@NewUserProfileActivity,MyTrainerActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        if (jsonObjectData.optJSONArray("trainers").length()>0){
                            linearTrainerList.visibility=View.VISIBLE
                            linearNoTrainer.visibility=View.GONE
                            if (jsonObjectData.optJSONArray("trainers").length()==1){
                                cardTrainer1.visibility=View.VISIBLE
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(0).optString("image")).fitCenter().error(R.drawable._no_image).into(imtrainer1)

                            }
                            if (jsonObjectData.optJSONArray("trainers").length()==2){
                                cardTrainer1.visibility=View.VISIBLE
                                cardTrainer2.visibility=View.VISIBLE
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(0).optString("image")).fitCenter().error(R.drawable._no_image).into(imtrainer1)
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(1).optString("image")).fitCenter().error(R.drawable._no_image).into(imTraineer2)

                            }
                            if (jsonObjectData.optJSONArray("trainers").length()==3){
                                cardTrainer1.visibility=View.VISIBLE
                                cardTrainer2.visibility=View.VISIBLE
                                cardTraniner3.visibility=View.VISIBLE
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(0).optString("image")).fitCenter().error(R.drawable._no_image).into(imtrainer1)
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(1).optString("image")).fitCenter().error(R.drawable._no_image).into(imTraineer2)
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(2).optString("image")).fitCenter().error(R.drawable._no_image).into(imTrainer3)
                            }
                            if (jsonObjectData.optJSONArray("trainers").length()>3){
                                cardTrainer1.visibility=View.VISIBLE
                                cardTrainer2.visibility=View.VISIBLE
                                cardTraniner3.visibility=View.VISIBLE
                                cardTrainerCount.visibility=View.VISIBLE
                                tvTrainerCount.text = "+"+(jsonObjectData.optJSONArray("trainers").length()-3)
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(0).optString("image")).fitCenter().error(R.drawable._no_image).into(imtrainer1)
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(1).optString("image")).fitCenter().error(R.drawable._no_image).into(imTraineer2)
                                Glide.with(applicationContext!!).load(jsonObjectData.optJSONArray("trainers").optJSONObject(2).optString("image")).fitCenter().error(R.drawable._no_image).into(imTrainer3)
                            }

                        }else{
                            linearTrainerList.visibility=View.GONE
                            linearNoTrainer.visibility=View.VISIBLE
                        }
                        if (jsonObjectData.optJSONArray("healthPrefernce").length()>0){
                            for(i in 0 until jsonObjectData.optJSONArray("healthPrefernce").length()){
                                var jsonObject=jsonObjectData.optJSONArray("healthPrefernce").optJSONObject(i)
                                var healthModel=HealthModel()
                                healthModel.icon=jsonObject.optString("icon")
                                healthModel.name=jsonObject.optString("name")
                                healthArrayList.add(healthModel)
                            }
                            healthRecycle.adapter = HealthPreferenceAdapter(applicationContext,healthArrayList)

                            imHeath.visibility=View.GONE
                            tvNoHealth.visibility=View.GONE
                            healthRecycle.visibility=View.VISIBLE

                        }else{
                            imHeath.visibility=View.VISIBLE
                            tvNoHealth.visibility=View.VISIBLE
                            healthRecycle.visibility=View.GONE
                        }
                        if (jsonObjectData.optJSONArray("mypt_chart").length()>0){
                            barchartData(jsonObjectData.optJSONArray("mypt_chart"))
                            linearAcitivity.visibility=View.GONE
                            linearMyPTScore.visibility=View.VISIBLE

                        }else{
                            linearAcitivity.visibility=View.VISIBLE
                            linearMyPTScore.visibility=View.GONE
                        }
                        if (jsonObjectData.optJSONArray("awards").length()>0){
                            for (i in 0 until jsonObjectData.optJSONArray("awards").length()) {
                                var jsonObject=jsonObjectData.optJSONArray("awards").optJSONObject(i)
                                var awardsModel= AwardsModel()
                                awardsModel.weight=jsonObject.optString("title")
                                awardsModel.name=jsonObject.optString("name")
                                awardsModel.icon=jsonObject.optString("icon")
                                awardsArrayList.add(awardsModel)
                            }
                            awardRecycler.adapter = AwardsAdapter(applicationContext,awardsArrayList)
                            imAwardBack.visibility=View.VISIBLE
                            awardRecycler.visibility=View.VISIBLE
                            linearNoAward.visibility=View.GONE

                        }else{
                            imAwardBack.visibility=View.GONE
                            awardRecycler.visibility=View.GONE
                            linearNoAward.visibility=View.VISIBLE

                        }
                        tvmsg.text = jsonObjectData.optJSONObject("activityLog").optString("msg")
                        tvNotification.text = jsonObjectData.optJSONObject("activityLog").optString("notification")

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

    override fun onResume() {
        super.onResume()
        getUserDetail()

    }
}