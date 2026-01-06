package co.com.mypt.More

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.HealthDietListAdapter
import co.com.mypt.adapter.OtherResourceAdapter
import co.com.mypt.model.MealListModel
import co.com.mypt.model.OtherResourceModel

import co.com.mypt.utils.SemiCircleProgressViewHealth
import com.android.volley.VolleyError
import co.com.mypt.utils.GradientHalfCircleProgressBar
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HealthStatusActivity : AppCompatActivity() {
    lateinit var nestedStats:NestedScrollView
    lateinit var healthNested:NestedScrollView
    lateinit var tvHealth:TextView
    lateinit var tvStatus:TextView
    lateinit var tvResources:TextView
    lateinit var tvLooseWeight:TextView
    lateinit var tvcholestrol:TextView
    lateinit var tvstatsWeight:TextView
    lateinit var tvHDLvalue:TextView
    lateinit var tvBodyMassStatus:TextView
    lateinit var tvBodyMass:TextView
    lateinit var tvHDLstatus:TextView
    lateinit var tvCHolestrolstatus:TextView
    lateinit var tvNoDataCholoestrol:TextView
    lateinit var tvWeight:TextView
    lateinit var tvNoDataHDl:TextView
    lateinit var linearBodyMass:LinearLayout
    lateinit var linearBodyFat:LinearLayout
    lateinit var linearBodyMassValues:LinearLayout
    lateinit var linearCholestrolValue:LinearLayout
    lateinit var linearBodyFatNA:LinearLayout
    lateinit var linearHDlvalueSection:LinearLayout
    lateinit var linearActivity:LinearLayout
    lateinit var linearWaist:LinearLayout
    lateinit var linearWaistNA:LinearLayout
    lateinit var linearCardioNA:LinearLayout
    lateinit var linearHealthOverview:LinearLayout
    lateinit var linearHealthText:LinearLayout
    lateinit var linearCardio:LinearLayout
    lateinit var linearCalorie:LinearLayout
    lateinit var linearExercise:LinearLayout
    lateinit var semiHDlRelative:RelativeLayout
    lateinit var tvHeight:TextView
    lateinit var tvNA2:TextView
    lateinit var tvNA1:TextView
    lateinit var tvStatsHeight:TextView
    lateinit var tvBodyMassNA:TextView
    lateinit var tvBodyFatNA:TextView
    lateinit var tvBodyFat:TextView
    lateinit var tvSubscapula:TextView
    lateinit var tvSubscapular:TextView
    lateinit var tvAxilla:TextView
    lateinit var tvTriceps:TextView
    lateinit var tvThigs:TextView
    lateinit var tvHipCircumference:TextView
    lateinit var tvWaistCircumference:TextView
    lateinit var tvWaistValue:TextView
    lateinit var tvAbdomen:TextView
    lateinit var tvCHest:TextView
    lateinit var tvRestingHeartRate:TextView
    lateinit var tvWaistNA:TextView
    lateinit var tvCardio:TextView
    lateinit var tvCardioNA:TextView
    lateinit var tvActivities:TextView
    lateinit var tvCalories:TextView
    lateinit var tvExercise:TextView
    lateinit var tvDate:TextView
    lateinit var tvMaximumHeartRate:TextView
    lateinit var tvSystolicBloodPressure:TextView
    lateinit var tvDiastolicBloodPressure:TextView
    lateinit var tvWaiststatus:TextView
    lateinit var imDietArrow:ImageView
    lateinit var relative:RelativeLayout
    lateinit var semiCHolestrolRelative:RelativeLayout
    lateinit var dietRecycler:RecyclerView
    lateinit var imCreateMeal:ImageView
    private lateinit var semiCHolestrol: SemiCircleProgressViewHealth
    private lateinit var semiHdlProgress: SemiCircleProgressViewHealth
    private lateinit var activitiesProgress: GradientHalfCircleProgressBar
    private lateinit var caloriesProgress: GradientHalfCircleProgressBar
    private lateinit var exerciseProgress: GradientHalfCircleProgressBar

    var weightvalue=""
    lateinit var linearheader:LinearLayout
    var otherresourceModelList :ArrayList<OtherResourceModel> = ArrayList()
    lateinit var recyclerOtherResource: RecyclerView
    var mealArrayList = ArrayList<MealListModel>()
    var selectedType="1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_status)
        exerciseProgress=findViewById(R.id.exerciseProgress)
        linearExercise=findViewById(R.id.linearExercise)
        caloriesProgress=findViewById(R.id.caloriesProgress)
        activitiesProgress=findViewById(R.id.activitiesProgress)
        tvCalories=findViewById(R.id.tvCalories)
        linearActivity=findViewById(R.id.linearActivity)
        tvExercise=findViewById(R.id.tvExercise)
        linearCalorie=findViewById(R.id.linearCalorie)
        imCreateMeal=findViewById(R.id.imCreateMeal)
        tvStatsHeight=findViewById(R.id.tvStatsHeight)
        tvBodyMassStatus=findViewById(R.id.tvBodyMassStatus)
        tvBodyMass=findViewById(R.id.tvBodyMass)
        tvBodyMassNA=findViewById(R.id.tvBodyMassNA)
        tvWaistCircumference=findViewById(R.id.tvWaistCircumference)
        tvRestingHeartRate=findViewById(R.id.tvRestingHeartRate)
        nestedStats=findViewById(R.id.nestedStats)
        linearHealthOverview=findViewById(R.id.linearHealthOverview)
        linearHealthText=findViewById(R.id.linearHealthText)
        linearBodyMass=findViewById(R.id.linearBodyMass)
        linearBodyFat=findViewById(R.id.linearBodyFat)
        tvWaiststatus=findViewById(R.id.tvWaiststatus)
        linearBodyMassValues=findViewById(R.id.linearBodyMassValues)
        healthNested=findViewById(R.id.healthNested)
        tvBodyFat=findViewById(R.id.tvBodyFat)
        tvSubscapula=findViewById(R.id.tvSubscapula)
        linearWaistNA=findViewById(R.id.linearWaistNA)
        tvWaistValue=findViewById(R.id.tvWaistValue)
        tvCardioNA=findViewById(R.id.tvCardioNA)
        tvDate=findViewById(R.id.tvDate)
        tvMaximumHeartRate=findViewById(R.id.tvMaximumHeartRate)
        tvSystolicBloodPressure=findViewById(R.id.tvSystolicBloodPressure)
        tvDiastolicBloodPressure=findViewById(R.id.tvDiastolicBloodPressure)
        tvHipCircumference=findViewById(R.id.tvHipCircumference)
        tvAxilla=findViewById(R.id.tvAxilla)
        tvWaistNA=findViewById(R.id.tvWaistNA)
        tvCardio=findViewById(R.id.tvCardio)
        linearCardio=findViewById(R.id.linearCardio)
        linearCardioNA=findViewById(R.id.linearCardioNA)
        tvSubscapular=findViewById(R.id.tvSubscapular)
        tvTriceps=findViewById(R.id.tvTriceps)
        tvThigs=findViewById(R.id.tvThigs)
        tvAbdomen=findViewById(R.id.tvAbdomen)
        tvActivities=findViewById(R.id.tvActivities)
        tvCHest=findViewById(R.id.tvCHest)
        tvNoDataCholoestrol=findViewById(R.id.tvNoDataCholoestrol)
        tvNA1=findViewById(R.id.tvNA1)
        tvstatsWeight=findViewById(R.id.tvstatsWeight)
        tvStatsHeight=findViewById(R.id.tvStatsHeight)
        tvHDLvalue=findViewById(R.id.tvHDLvalue)
        tvNA2=findViewById(R.id.tvNA2)
        tvNoDataHDl=findViewById(R.id.tvNoDataHDl)
        tvHDLstatus=findViewById(R.id.tvHDLstatus)
        linearWaist=findViewById(R.id.linearWaist)
        tvBodyFatNA=findViewById(R.id.tvBodyFatNA)
        linearHDlvalueSection=findViewById(R.id.linearHDlvalueSection)
        semiHdlProgress=findViewById(R.id.semiHdlProgress)
        semiHDlRelative=findViewById(R.id.semiHDlRelative)
        tvcholestrol=findViewById(R.id.tvcholestrol)
        tvCHolestrolstatus=findViewById(R.id.tvCHolestrolstatus)
        semiCHolestrolRelative=findViewById(R.id.semiCHolestrolRelative)
        linearCholestrolValue=findViewById(R.id.linearCholestrolValue)
        linearBodyFatNA=findViewById(R.id.linearBodyFatNA)
        semiCHolestrol=findViewById(R.id.semiCHolestrol)
        tvHealth=findViewById(R.id.tvHealth)
        tvResources=findViewById(R.id.tvResources)
        tvWeight=findViewById(R.id.tvWeight)
        tvHeight=findViewById(R.id.tvHeight)
        tvLooseWeight=findViewById(R.id.tvLooseWeight)
        imDietArrow=findViewById(R.id.imDietArrow)
        dietRecycler=findViewById(R.id.dietRecycler)
        relative=findViewById(R.id.relative)
        tvStatus=findViewById(R.id.tvStatus)
        linearheader=findViewById(R.id.linearheader)
        recyclerOtherResource=findViewById(R.id.recyclerOtherResource)

        semiCHolestrol.viewTreeObserver.addOnGlobalLayoutListener {
            semiCHolestrol.setProgressDrawable(R.drawable.chloestrol_gradient_drawable)
        }
        semiHdlProgress.viewTreeObserver.addOnGlobalLayoutListener {
            semiHdlProgress.setProgressDrawable(R.drawable.health_drawable)
        }

        textShader(tvHeight)
        textShader(tvWeight)
        textShader(tvcholestrol)
        textShader(tvHDLvalue)
        textShader(tvNA1)
        textShader(tvNA2)
        textShader(tvBodyMassNA)
        textShader(tvStatsHeight)
        textShader(tvstatsWeight)
        textShader(tvBodyMass)
        textShader(tvBodyFat)
        textShader(tvBodyFatNA)
        textShader(tvWaistNA)
        textShader(tvWaistValue)
        textShader(tvCardioNA)
        textShader(tvCalories)
        textShader(tvExercise)
        textShader(tvActivities)

        relative.setOnClickListener {


        }
        linearheader.setOnClickListener {
            finish()
        }
        tvHealth.setOnClickListener {
            healthNested.visibility=View.VISIBLE
            nestedStats.visibility=View.GONE
            tvHealth.setTextColor(resources.getColor(R.color.black))
            tvStatus.setTextColor(resources.getColor(R.color.white))

            tvStatus.background = null
            tvHealth.background = resources.getDrawable(R.drawable.white_rectangle_8dp)
            selectedType="1"
            getHealthStatsData(selectedType)
        }
        tvStatus.setOnClickListener {
            healthNested.visibility=View.GONE
            nestedStats.visibility=View.VISIBLE
            tvHealth.setTextColor(resources.getColor(R.color.white))
            tvStatus.setTextColor(resources.getColor(R.color.black))
            tvStatus.background = resources.getDrawable(R.drawable.white_rectangle_8dp)
            tvHealth.background = null
            selectedType="2"
            getHealthStatsData(selectedType)
            activitiesProgress.setProgressWithAnimation(100f)
            caloriesProgress.setProgressWithAnimation(95f)
            exerciseProgress.setProgressWithAnimation(90f)
        }

        activitiesProgress.setProgressWithAnimation(2f)
        caloriesProgress.setProgressWithAnimation(20f)
        exerciseProgress.setProgressWithAnimation(50f)
        getMealsData(this)

    }
    private fun getMealsData(context: Context?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(context!!,"")
        progressDialog.show()

        var api=""
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)

        api= ApiURL.user_meals+"?date="+currentDate

        Log.e("UserMealUrl",api)
        GetMethod(api
            ,context).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                mealArrayList.clear()
                Log.e("MealListDatewiseResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArray=jsonObj.optJSONArray("data")

                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                var json=jsonArray.optJSONObject(i)
                                var mealListModel= MealListModel()
                                mealListModel.meal_name=json.optString("meal_name")
                                mealListModel.id=json.optString("id")
                                mealListModel.meal_type=json.optString("meal_type")
                                mealListModel.calories=json.optString("calories")
                                mealListModel.proteins=json.optString("proteins")
                                mealListModel.carbs=json.optString("carbs")
                                mealListModel.fats=json.optString("fats")
                                mealListModel.meal_time=json.optString("meal_time")
                                mealListModel.fitness_goal=json.optString("fitness_goal")
                                mealListModel.is_saved=json.optString("is_saved")
                                mealArrayList.add(mealListModel)
                            }
                            dietRecycler.adapter = HealthDietListAdapter(this@HealthStatusActivity,mealArrayList)
                            dietRecycler.visibility= View.VISIBLE
                            imCreateMeal.visibility=View.GONE



                        }else{
                            dietRecycler.visibility= View.GONE
                            imCreateMeal.visibility=View.VISIBLE



                        }




                    }else{

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
        getAllResources()
        try {
            if (intent.getStringExtra("selectedType").equals("1")){
                healthNested.visibility=View.VISIBLE
                nestedStats.visibility=View.GONE
                tvHealth.setTextColor(resources.getColor(R.color.black))
                tvStatus.setTextColor(resources.getColor(R.color.white))

                tvStatus.background = null
                tvHealth.background = resources.getDrawable(R.drawable.white_rectangle_8dp)
                selectedType="1"
                getHealthStatsData(selectedType)
            }else{
                healthNested.visibility=View.GONE
                nestedStats.visibility=View.VISIBLE
                tvHealth.setTextColor(resources.getColor(R.color.white))

                tvStatus.setTextColor(resources.getColor(R.color.black))
                tvStatus.background = resources.getDrawable(R.drawable.white_rectangle_8dp)
                tvHealth.background = null
                selectedType="2"
                 getHealthStatsData(selectedType)

            }

        }catch (e:Exception){
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(AddToLike, IntentFilter("LikeFavourite"),
                RECEIVER_EXPORTED)

        }
        else{
            registerReceiver(AddToLike, IntentFilter("LikeFavourite"))
        }
    }
    val AddToLike = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
          getMealsData(context)
        }

    }
    private fun getAllResources() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.getresources

        Log.e("getResourcessUrl",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                otherresourceModelList.clear()
                Log.e("AllResourcesResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArrayResources=jsonObj.optJSONArray("data")

                        if (jsonArrayResources.length()>0){
                            tvResources.visibility= View.VISIBLE
                            recyclerOtherResource.visibility= View.VISIBLE
                            for(i in 0 until jsonArrayResources.length()){
                                var json=jsonArrayResources.optJSONObject(i)
                                var otherResourceModel= OtherResourceModel()

                                otherResourceModel.title=json.optString("title")
                                otherResourceModel.description=json.optString("description")
                                otherResourceModel.date=json.optString("date")
                                otherResourceModel.reading_time=json.optString("reading_time")
                                otherResourceModel.image=json.optString("image")

                                otherresourceModelList.add(otherResourceModel)
                            }

                            var otherresourceAdapter = OtherResourceAdapter(applicationContext, otherresourceModelList)
                            recyclerOtherResource.adapter = otherresourceAdapter
                        }else{
                            tvResources.visibility= View.GONE
                            recyclerOtherResource.visibility= View.GONE
                        }





                    }else{

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
    private fun getHealthStatsData(selectedType: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.userhealth_stats+selectedType

        Log.e("userHealthStatusApi",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("healthStatsResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        if(selectedType.equals("1")){
                            var jsonData=jsonObj.optJSONObject("data").optJSONObject("healthData")
                            var jsonDatacardio=jsonObj.optJSONObject("data").optJSONObject("cardioInsights")
                            if (jsonData.optString("height").equals("")){
                                tvHeight.text = "NA"
                            }else{
                                if (jsonData.optString("height").contains("ft")){
                                    // Regular expression to extract feet and inches
                                    val regex = Regex("(\\d+)ft\\s*(\\d+)in")
                                    val matchResult = regex.find(jsonData.optString("height"))

                                    if (matchResult != null) {
                                        val (feet, inches) = matchResult.destructured
                                        val spanned = HtmlCompat.fromHtml("<big><b>${feet.toInt()}</b></big><small> ft</small><big><b>${inches.toInt()}</b></big><small> in</small>", HtmlCompat.FROM_HTML_MODE_COMPACT)
                                        tvHeight.text = spanned
                                    }

                                }else{
                                    val spanned = HtmlCompat.fromHtml("<big><b>${jsonData.optString("height").replace("cms","").replace("cm","")}</b></big><small> cm</small>", HtmlCompat.FROM_HTML_MODE_COMPACT)
                                    tvHeight.text = spanned
                                }
                            }
                            if (jsonObj.optJSONObject("data").optJSONObject("healthData").optString("weight").equals("")){
                                tvWeight.text = "NA"
                            }else{
                                tvWeight.text =
                                    jsonObj.optJSONObject("data").optJSONObject("healthData").optString("weight")

                            }


                            if (jsonObj.optJSONObject("data").optString("customGoal").equals("")){
                                tvLooseWeight.text = "Data not available"
                            }else{
                                tvLooseWeight.text = jsonObj.optJSONObject("data").optString("customGoal")

                            }
                            if (jsonDatacardio.optJSONObject("cholesterol").optString("value").equals(""))
                            {
                                tvNA1.visibility=View.VISIBLE
                                tvNoDataCholoestrol.visibility=View.VISIBLE
                                semiCHolestrolRelative.visibility=View.GONE
                                linearCholestrolValue.visibility=View.GONE

                            }else{
                                semiCHolestrol.setMaxValue(250f)
                                semiCHolestrol.setProgressWithAnimation(jsonDatacardio.optJSONObject("cholesterol").optString("value").toFloat())
                                tvNA1.visibility=View.GONE
                                tvNoDataCholoestrol.visibility=View.GONE
                                semiCHolestrolRelative.visibility=View.VISIBLE
                                linearCholestrolValue.visibility=View.VISIBLE
                                tvcholestrol.text = jsonDatacardio.optJSONObject("cholesterol").optString("value")
                                tvCHolestrolstatus.text =
                                    jsonDatacardio.optJSONObject("cholesterol").optString("status")
                            }
                            if (jsonDatacardio.optJSONObject("hdl").optString("value").equals("")){
                                tvNA2.visibility=View.VISIBLE
                                tvNoDataHDl.visibility=View.VISIBLE
                                linearHDlvalueSection.visibility=View.GONE
                                semiHDlRelative.visibility=View.GONE



                            }else{
                                semiHdlProgress.setMaxValue(130f)

                                semiHdlProgress.setProgressWithAnimation(jsonDatacardio.optJSONObject("hdl").optString("value").toFloat())

                                tvNA2.visibility=View.GONE
                                tvNoDataHDl.visibility=View.GONE
                                linearHDlvalueSection.visibility=View.VISIBLE
                                semiHDlRelative.visibility=View.VISIBLE
                                tvHDLvalue.text = jsonDatacardio.optJSONObject("hdl").optString("value")
                                tvHDLstatus.text = jsonDatacardio.optJSONObject("hdl").optString("status")

                            }

                        }else{
                            var jsonDataphysical=jsonObj.optJSONObject("data").optJSONObject("physicalMeasurement")
                            var jsonDatahealthOverview=jsonObj.optJSONObject("data").optJSONObject("healthOverview")
                            var jsonDatabodyComposition=jsonObj.optJSONObject("data").optJSONObject("bodyComposition")
                            var jsonDatabodyFat=jsonObj.optJSONObject("data").optJSONObject("bodyFat")
                            var jsonDatawaist_hip_ratio=jsonObj.optJSONObject("data").optJSONObject("waist_hip_ratio")
                            var jsonDatacardiovascular=jsonObj.optJSONObject("data").optJSONObject("cardiovascular")
                            tvDate.text = jsonDatahealthOverview.optString("month")
                            if (jsonDataphysical.optString("height").equals("")){
                                tvStatsHeight.text = "NA"
                            }else{
                                if (jsonDataphysical.optString("height").contains("ft")){
                                    // Regular expression to extract feet and inches
                                    val regex = Regex("(\\d+)ft\\s*(\\d+)in")
                                    val matchResult = regex.find(jsonDataphysical.optString("height"))

                                    if (matchResult != null) {
                                        val (feet, inches) = matchResult.destructured
                                        val spanned = HtmlCompat.fromHtml("<big><b>${feet.toInt()}</b></big><small> ft</small><big><b>${inches.toInt()}</b></big><small> in</small>", HtmlCompat.FROM_HTML_MODE_COMPACT)
                                        tvStatsHeight.text = spanned
                                    }

                                }else{
                                    val spanned = HtmlCompat.fromHtml("<big><b>${jsonDataphysical.optString("height").replace("cms","").replace("cm","")}</b></big><small> cm</small>", HtmlCompat.FROM_HTML_MODE_COMPACT)
                                    tvStatsHeight.text = spanned
                                }
                            }
                            if (jsonDataphysical.optString("weight").equals("")){
                                tvstatsWeight.text = "NA"
                            }else{
                                tvstatsWeight.text = jsonDataphysical.optString("weight")
                            }

                            if (jsonDatabodyComposition.optString("body_mass").equals("")){
                                linearBodyMass.visibility=View.VISIBLE
                                linearBodyMassValues.visibility=View.GONE
                            }
                            else{
                                linearBodyMass.visibility=View.GONE
                                linearBodyMassValues.visibility=View.VISIBLE
                                tvBodyMass.text = jsonDatabodyComposition.optString("body_mass")
                                tvBodyMassStatus.text = jsonDatabodyComposition.optString("body_mass_status")
                            }
                            if (jsonDatabodyFat.optString("body_fat").equals("")){
                                linearBodyFatNA.visibility=View.VISIBLE
                                linearBodyFat.visibility=View.GONE
                            }
                            else{
                                linearBodyFatNA.visibility=View.GONE
                                linearBodyFat.visibility=View.VISIBLE
                                tvBodyFat.text = jsonDatabodyFat.optString("body_fat")
                                tvBodyMassStatus.text = jsonDatabodyFat.optString("body_fat_status")
                                tvCHest.text = jsonDatabodyFat.optString("chest")
                                tvAbdomen.text = jsonDatabodyFat.optString("abdomen")
                                tvThigs.text = jsonDatabodyFat.optString("thighs")
                                tvTriceps.text = jsonDatabodyFat.optString("triceps")
                                tvSubscapular.text = jsonDatabodyFat.optString("subscapular")
                                tvAxilla.text = jsonDatabodyFat.optString("axila")
                                tvSubscapula.text = jsonDatabodyFat.optString("subscapula")

                            }
                            if (jsonDatawaist_hip_ratio.optString("ratio").equals("")){
                                linearWaistNA.visibility=View.VISIBLE
                                linearWaist.visibility=View.GONE
                            }
                            else{
                                linearWaistNA.visibility=View.GONE
                                linearWaist.visibility=View.VISIBLE
                                tvWaistValue.text = jsonDatawaist_hip_ratio.optString("ratio")
                                tvHipCircumference.text = jsonDatawaist_hip_ratio.optString("hip")
                                tvWaistCircumference.text = jsonDatawaist_hip_ratio.optString("waist")
                                tvWaiststatus.text = jsonDatawaist_hip_ratio.optString("status")


                            }
                            if (jsonDatacardiovascular.optString("resting_heart_rate").equals("")){
                                linearCardioNA.visibility=View.VISIBLE
                                tvCardio.visibility=View.GONE
                                linearCardio.visibility=View.GONE
                            }
                            else{
                                linearCardioNA.visibility=View.GONE
                                linearCardio.visibility=View.VISIBLE
                                tvCardio.visibility=View.VISIBLE
                                tvRestingHeartRate.text = jsonDatacardiovascular.optString("resting_heart_rate")
                                tvMaximumHeartRate.text = jsonDatacardiovascular.optString("max_heart_rate")
                                tvDiastolicBloodPressure.text = jsonDatacardiovascular.optString("diastolic_bp")
                                tvSystolicBloodPressure.text = jsonDatacardiovascular.optString("systolic_bp")

                            }
                            if (jsonDatahealthOverview.optString("activities").equals("") && jsonDatahealthOverview.optString("calories").equals("") && jsonDatahealthOverview.optString("exercise").equals("")){
                                linearHealthOverview.visibility=View.GONE
                                linearHealthText.visibility=View.GONE
                            }
                            else{
                                linearHealthOverview.visibility=View.VISIBLE
                                linearHealthText.visibility=View.VISIBLE
                                if (jsonDatahealthOverview.optString("activities").equals("")){
                                    linearActivity.visibility=View.GONE
                                }else{
                                    tvActivities.text = jsonDatahealthOverview.optString("activities")

                                }
                                if (jsonDatahealthOverview.optString("calories").equals("")){
                                    linearCalorie.visibility=View.GONE
                                }else{
                                    tvCalories.text = jsonDatahealthOverview.optString("calories")
                                }
                                if (jsonDatahealthOverview.optString("exercise").equals("")){
                                    linearExercise.visibility=View.GONE
                                }else{
                                    tvExercise.text = jsonDatahealthOverview.optString("exercise")
                                }


                            }

                        }





                    }else{

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

}