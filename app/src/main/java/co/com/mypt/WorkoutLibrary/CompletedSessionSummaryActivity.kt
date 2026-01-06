package co.com.mypt.WorkoutLibrary



import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.WorkSummaryAdapter
import co.com.mypt.model.SemiCircleProgressView
import co.com.mypt.model.WorkoutListModel
import com.android.volley.VolleyError
import org.json.JSONObject

class CompletedSessionSummaryActivity : AppCompatActivity() {
    lateinit var semiProgress: SemiCircleProgressView
    lateinit var tvPercentage: TextView
    lateinit var tv_complete: TextView
    lateinit var linearHeader: LinearLayout
    lateinit var recycler_workoutlist: RecyclerView
    lateinit var worklistAdapter:WorkSummaryAdapter
    var workoutList :ArrayList<WorkoutListModel> = ArrayList()

    lateinit var caloriesBar : ProgressBar
    lateinit var workoutDurationBar : ProgressBar
    lateinit var heartRateBar : ProgressBar

    lateinit var caloriesBarText : TextView
    lateinit var workoutDurationBarText : TextView
    lateinit var heartRateBarText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_completed_session_summary)
        semiProgress=findViewById(R.id.semiProgress)
        linearHeader=findViewById(R.id.linearHeader)
        tv_complete=findViewById(R.id.tv_complete)
        tvPercentage=findViewById(R.id.tvPercentage)
        caloriesBar=findViewById(R.id.caloriesBar)
        caloriesBarText=findViewById(R.id.caloriesBarText)
        workoutDurationBar=findViewById(R.id.workoutDurationBar)
        workoutDurationBarText=findViewById(R.id.workoutDurationBarText)
        heartRateBar=findViewById(R.id.heartRateBar)
        heartRateBarText=findViewById(R.id.heartRateBarText)
        recycler_workoutlist=findViewById(R.id.recycler_workoutlist)


        //barChart=findViewById(R.id.chart1)
        linearHeader.setOnClickListener {
            finish()
        }


        semiProgress.viewTreeObserver.addOnGlobalLayoutListener {
            semiProgress.setProgressDrawable(R.drawable.progress_gradient)
        }

        textShader(tvPercentage)

        tv_complete.setOnClickListener {
            val intent = Intent(this, StreakActivity::class.java)
            startActivity(intent)
        }

    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#9EBCFF"),
                Color.parseColor("#FAFAFA")
            ), null, Shader.TileMode.MIRROR
        )
        tv.paint.shader = textShader
    }

    private fun getSessionData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@CompletedSessionSummaryActivity,"")
        progressDialog.show()
        Log.e("workoutSummaryApi",ApiURL.workout_summary+intent.getStringExtra("session_id"))
        workoutList.clear()
        GetMethod(ApiURL.workout_summary+intent.getStringExtra("session_id"),this@CompletedSessionSummaryActivity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("workoutSummaryResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("success")){
                        tvPercentage.text = resp.optJSONObject("data").optString("percentage_completed") +"%"
                        semiProgress.setProgressWithAnimation(resp.optJSONObject("data").optString("percentage_completed").toFloat())
                        workoutDurationBar.progress = Integer.parseInt(resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(1).optString("calories"))

                        caloriesBarText.text =
                            resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(0).optString("calories")
                        workoutDurationBarText.text =
                            resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(1).optString("calories")
                        heartRateBarText.text =
                            resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(2).optString("calories")

                        ObjectAnimator.ofInt(caloriesBar, "progress", Integer.parseInt(resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(0).optString("calories"))+15)
                            .setDuration(1500)
                            .start()

                        ObjectAnimator.ofInt(heartRateBar, "progress", Integer.parseInt(resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(2).optString("calories"))+15)
                            .setDuration(1500)
                            .start()

                        ObjectAnimator.ofInt(workoutDurationBar, "progress", Integer.parseInt(resp.optJSONObject("data").optJSONArray("score_blocks").optJSONObject(1).optString("calories"))+15)
                            .setDuration(1500)
                            .start()

                        var jsonArray=resp.optJSONObject("data").optJSONArray("summary")
                        for(i in 0 until jsonArray.length()){
                            var jsonObject=jsonArray.optJSONObject(i)
                            var workoutListModel= WorkoutListModel()
                            workoutListModel.reps=jsonObject.optString("reps")
                            workoutListModel.exercise_name=jsonObject.optString("exercise_name")
                            workoutListModel.expected_reps=jsonObject.optString("expected_reps")
                            workoutListModel.calories=jsonObject.optString("calories")
                            workoutListModel.status=jsonObject.optString("status")
                            workoutList.add(workoutListModel)
                        }

                        var workoutlistAdapter= WorkSummaryAdapter(workoutList,this@CompletedSessionSummaryActivity)
                        recycler_workoutlist.adapter=workoutlistAdapter
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
        getSessionData()
    }

}