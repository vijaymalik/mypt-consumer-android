package co.com.mypt.WorkoutLibrary

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import com.android.volley.VolleyError
import org.json.JSONObject

class SessionSummaryActivity : AppCompatActivity() {
    lateinit var tv_complete:TextView
    lateinit var tvTime:TextView
    lateinit var headerLayout: LinearLayout
    lateinit var tvCalories:TextView
    lateinit var tvExercises:TextView
    lateinit var tvWorkoutName:TextView
    lateinit var tvWorkoutTotal:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session_summary)
        tv_complete=findViewById(R.id.tv_complete)
        tvTime=findViewById(R.id.tvTime)
        tvWorkoutName=findViewById(R.id.tvWorkoutName)
        headerLayout=findViewById(R.id.headerLayout)
        tvCalories=findViewById(R.id.tvCalories)
        tvExercises=findViewById(R.id.tvExercises)
        tvWorkoutTotal=findViewById(R.id.tvWorkoutTotal)
        headerLayout.setOnClickListener {
            finish()
        }
        tv_complete.setOnClickListener{
            completeWorkout()


        }

    }

    override fun onResume() {
        super.onResume()
        getWorkoutDetail()
    }
    private fun getWorkoutDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@SessionSummaryActivity,"")
        progressDialog.show()

        var api=""
        api= ApiURL.wokoutdetail+intent.getStringExtra("wokout_id")+"&type="

        Log.e("workoutDetailUrl",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {

            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("workoutDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        tvTime.text = jsonObj.optJSONObject("data").optString("duration")
                        tvCalories.text = jsonObj.optJSONObject("data").optString("calories")
                        tvExercises.text = jsonObj.optJSONObject("data").optString("exercises_count")
                        tvWorkoutTotal.text = jsonObj.optJSONObject("data").optString("exercises_count")+" Total"
                        tvWorkoutName.text = jsonObj.optJSONObject("data").optString("name")


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

    private fun completeWorkout() {
        val param: MutableMap<String, String> = HashMap()
        param["session_id"] =""+intent.getStringExtra("session_id")

        Log.e("completeWorkoutParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this@SessionSummaryActivity,"")
        progressDialog.show()

        PostMethod(ApiURL.workoutcomplete,param, applicationContext).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("completeExerciseRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var intent1= Intent(this@SessionSummaryActivity,SpiderWebSessionSummaryActivity::class.java)
                        intent1.putExtra("session_id",intent.getStringExtra("session_id"))
                        intent1.putExtra("calories",resp.optJSONObject("data").optJSONObject("workoutData").optString("calories"))
                        intent1.putExtra("heart_zone",resp.optJSONObject("data").optJSONObject("workoutData").optString("heart_zone"))
                        intent1.putExtra("steps",resp.optJSONObject("data").optJSONObject("workoutData").optString("steps"))
                        intent1.putExtra("minute_duration",resp.optJSONObject("data").optJSONObject("workoutData").optString("minute_duration"))
                        intent1.putExtra("sec_duration",resp.optJSONObject("data").optJSONObject("workoutData").optString("sec_duration"))
                        intent1.putExtra("routine",resp.optJSONObject("data").optJSONObject("workoutData").optString("routine"))
                        intent1.putExtra("duration_score",resp.optJSONObject("data").optJSONObject("workoutData").optString("duration_score"))
                        intent1.putExtra("ptScore",resp.optJSONObject("data").optString("ptScore"))
                        intent1.putExtra("totalExercise",tvExercises.text.toString())

                        startActivity(intent1)
                    }else{
                        Toast.makeText(applicationContext,resp.optString("msg"), Toast.LENGTH_LONG).show()
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

