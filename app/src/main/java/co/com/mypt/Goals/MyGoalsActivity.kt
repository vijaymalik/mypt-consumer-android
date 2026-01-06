package co.com.mypt.Goals

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import com.android.volley.VolleyError
import org.json.JSONObject

class MyGoalsActivity : AppCompatActivity() {
    var hydrationid=""
    lateinit var linearCalorie:LinearLayout
    lateinit var step_countLiner:LinearLayout
    lateinit var linearNutrition:LinearLayout
    lateinit var linearHydration:LinearLayout
    lateinit var nested:NestedScrollView
    lateinit var headerLayout:LinearLayout
    lateinit var linearSleep:LinearLayout
    lateinit var tvCalorieIntake:TextView
    lateinit var tvBurnCalori:TextView
    lateinit var tvNoGoals:TextView
    lateinit var tvHydrationName:TextView
    lateinit var tvCalorieIntakeOf:TextView
    lateinit var tvTotalBurn:TextView
    lateinit var tvCalorieIntakeRemaining:TextView
    lateinit var tvRemainingCalorie:TextView
    lateinit var nutritionCalorieProgress:ProgressBar
    lateinit var calorieProgressBar:ProgressBar
    lateinit var hydrationProgress:ProgressBar
    lateinit var sleepActivity:TextView
    lateinit var tvGlassValue:TextView
    var calorieBurn_id=""
    var calorieIntakeid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_goals)
        linearCalorie=findViewById(R.id.linearCalorie)
        nested=findViewById(R.id.nested)
        linearNutrition=findViewById(R.id.linearNutrition)
        tvHydrationName=findViewById(R.id.tvHydrationName)
        step_countLiner=findViewById(R.id.step_countLiner)
        tvCalorieIntake=findViewById(R.id.tvCalorieIntake)
        nutritionCalorieProgress=findViewById(R.id.nutritionCalorieProgress)
        linearHydration=findViewById(R.id.linearHydration)
        headerLayout=findViewById(R.id.headerLayout)
        tvBurnCalori=findViewById(R.id.tvBurnCalori)
        tvTotalBurn=findViewById(R.id.tvTotalBurn)
        tvNoGoals=findViewById(R.id.tvNoGoals)
        linearSleep=findViewById(R.id.linearSleep)
        calorieProgressBar=findViewById(R.id.calorieProgressBar)
        tvRemainingCalorie=findViewById(R.id.tvRemainingCalorie)
        tvGlassValue=findViewById(R.id.tvGlassValue)
        sleepActivity=findViewById(R.id.sleepActivity)
        tvCalorieIntakeOf=findViewById(R.id.tvCalorieIntakeOf)
        hydrationProgress=findViewById(R.id.hydrationProgress)
        tvCalorieIntakeRemaining=findViewById(R.id.tvCalorieIntakeRemaining)

        linearCalorie.setOnClickListener{
            val intent=Intent(applicationContext,CalorieBurnActivity::class.java)
            startActivity(intent)
        }
        step_countLiner.setOnClickListener{
            val intent=Intent(applicationContext,StepCountActivity::class.java)
            startActivity(intent)
        }
        linearNutrition.setOnClickListener{
            val intent=Intent(applicationContext,Calorie_IntakeActivity::class.java)
            intent.putExtra("calorieIntake_id",calorieIntakeid)
            intent.putExtra("calorieIntake",tvCalorieIntake.text.toString())

            startActivity(intent)
        }
        sleepActivity.setOnClickListener{
            val intent=Intent(applicationContext,SleepTrackingActivity::class.java)
            startActivity(intent)
        }
        linearHydration.setOnClickListener{
            val intent=Intent(applicationContext,HydrationActivity::class.java)
            intent.putExtra("hydration_id",hydrationid)
            startActivity(intent)
        }
        headerLayout.setOnClickListener{
            finish()
        }

    }
    private fun getGoalsData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.usergoals

        Log.e("UserGoalAPi",api)
        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                hydrationProgress.invalidate()
                calorieProgressBar.invalidate()
                Log.e("UserGoalResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if(jsonObj.optBoolean("status")){
                        nested.visibility=View.VISIBLE
                        var workoutCalorieJson=jsonObj.optJSONObject("data").optJSONObject("workout").optJSONObject("goalsDetails").optJSONObject("calories")
                        var workoutStepsJson=jsonObj.optJSONObject("data").optJSONObject("workout").optJSONObject("goalsDetails").optJSONObject("steps")
                        var nutritionCalorieJson=jsonObj.optJSONObject("data").optJSONObject("nutrition").optJSONObject("goalsDetails").optJSONObject("calories")
                        var nutritionhydrationJson=jsonObj.optJSONObject("data").optJSONObject("nutrition").optJSONObject("goalsDetails").optJSONObject("hydration")
                        var sleepJson=jsonObj.optJSONObject("data").optJSONObject("habit").optJSONObject("goalsDetails").optJSONObject("sleep")
                       if (workoutCalorieJson.optString("id").equals("") && workoutStepsJson.optString("id").equals("") &&
                           nutritionCalorieJson.optString("id").equals("") && nutritionhydrationJson.optString("id").equals("") && sleepJson.optString("id").equals("")){
                           nested.visibility=View.GONE
                           tvNoGoals.visibility=View.VISIBLE
                       }else{
                           nested.visibility=View.VISIBLE
                           tvNoGoals.visibility=View.GONE
                       }
                        if ((workoutCalorieJson.optString("id").equals(""))){
                            linearCalorie.visibility=View.GONE
                        }else{
                            linearCalorie.visibility=View.VISIBLE
                            calorieBurn_id=workoutCalorieJson.optString("id")
                            tvBurnCalori.text = workoutCalorieJson.optString("name")
                            tvTotalBurn.text = workoutCalorieJson.optString("progress")+" of "+(workoutCalorieJson.optString("total"))
                            tvRemainingCalorie.text = "Remaining: "+workoutCalorieJson.optString("remaining")

                            calorieProgressBar.max=(workoutCalorieJson.optInt("total"))
                            calorieProgressBar.progress = workoutCalorieJson.optInt("progress")
                        }
                        if ((workoutStepsJson.optString("id").equals(""))){
                            step_countLiner.visibility=View.GONE
                        }else{
                            var step_id=workoutStepsJson.optString("id")
                            step_countLiner.visibility=View.VISIBLE
                        }
                        if ((nutritionCalorieJson.optString("id").equals(""))){
                            linearNutrition.visibility=View.GONE

                        }else{
                            calorieIntakeid=nutritionCalorieJson.optString("id")
                            linearNutrition.visibility=View.VISIBLE
                            tvCalorieIntake.text = nutritionCalorieJson.optString("name")
                            tvCalorieIntakeOf.text = nutritionCalorieJson.optString("progress")+" of "+(nutritionCalorieJson.optString("total"))
                            tvCalorieIntakeRemaining.text = "Remaining: "+nutritionCalorieJson.optString("remaining")

                            nutritionCalorieProgress.max=(nutritionCalorieJson.optInt("total"))
                            nutritionCalorieProgress.progress = nutritionCalorieJson.optInt("progress")

                        }
                        if ((nutritionhydrationJson.optString("id").equals(""))){
                            linearHydration.visibility=View.GONE

                        }else{
                            hydrationid=nutritionhydrationJson.optString("id")

                            linearHydration.visibility=View.VISIBLE
                            tvHydrationName.text = nutritionhydrationJson.optString("name")

                            hydrationProgress.max=(nutritionhydrationJson.optInt("total"))
                            hydrationProgress.progress = nutritionhydrationJson.optInt("progress")

                            tvGlassValue.text = "+ "+nutritionhydrationJson.optString("glass_value")+nutritionhydrationJson.optString("glass_type")

                        }
                        if ((sleepJson.optString("id").equals(""))){
                            linearSleep.visibility=View.GONE

                        }else{
                            var hydrationid=sleepJson.optString("id")

                            linearSleep.visibility=View.VISIBLE


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

    override fun onResume() {
        super.onResume()
        //getGoalsData()

    }


}