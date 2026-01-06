package co.com.mypt.ActiveSession

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView

import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.TrainerAmenitiesAdapter
import co.com.mypt.adapter.UpcomingSessionLibraryAdapter
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.FeaturedWorkoutModel
import co.com.mypt.utils.CurvedProgressBar
import co.com.mypt.utils.GradientHalfCircleProgressBar
import co.com.mypt.utils.HeartRateMarkerView
import co.com.mypt.utils.WaveformView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class ActiveSessionDetails : AppCompatActivity() {
    lateinit var recycler: RecyclerView
    lateinit var UpcomingRecycler: RecyclerView
    lateinit var tvDuration: TextView
    lateinit var tvSos: TextView
    lateinit var linearGoal: LinearLayout
    lateinit var linearprogress: LinearLayout
    lateinit var nested: NestedScrollView
    lateinit var tvCalories: TextView
    lateinit var tvExercise: TextView
    lateinit var tvBodyPart: TextView
    lateinit var tvTrainer_name: TextView
    lateinit var tvSec: TextView
    lateinit var tvRating: TextView
    lateinit var tvSlotTime: TextView
    lateinit var trainer_im: ImageView
    lateinit var im_workout: ImageView
    lateinit var heartRateChart: LineChart
    lateinit var timeCurveProgress: CurvedProgressBar
   // lateinit var snakeView: SnakeView
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    var upcomingSessionArrayList = ArrayList<FeaturedWorkoutModel>()
    private lateinit var durationProgress: GradientHalfCircleProgressBar
    private lateinit var caloriesProgress: GradientHalfCircleProgressBar
    private lateinit var exerciseProgress: GradientHalfCircleProgressBar
    var workout_id=""
    var session_id=""

    private val activityScope = CoroutineScope(Dispatchers.Main + Job())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_active_session_details)
        tvDuration=findViewById(R.id.tvDuration)
        tvCalories=findViewById(R.id.tvCalories)
        tvExercise=findViewById(R.id.tvExercise)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        heartRateChart=findViewById(R.id.heartRateChart)
        trainer_im=findViewById(R.id.trainer_im)
        tvRating=findViewById(R.id.tvRating)
        tvSlotTime=findViewById(R.id.tvSlotTime)
        linearGoal=findViewById(R.id.linearGoal)
        nested=findViewById(R.id.nested)
        linearprogress=findViewById(R.id.linearprogress)

        durationProgress=findViewById(R.id.durationProgress)
        exerciseProgress=findViewById(R.id.exerciseProgress)
        caloriesProgress=findViewById(R.id.caloriesProgress)
        recycler=findViewById(R.id.recycler)
        UpcomingRecycler=findViewById(R.id.UpcomingRecycler)
        tvSos=findViewById(R.id.tvSos)
        tvBodyPart=findViewById(R.id.tvBodyPart)
        im_workout=findViewById(R.id.im_workout)
        tvSec=findViewById(R.id.tvSec)
        timeCurveProgress=findViewById(R.id.timeCurveProgress)

        val waveView = findViewById<WaveformView>(R.id.waveformView)
        val values = listOf(40f, 60f, 40f, 60f, 10f, 10f, 70f, 30f, 30f,60f, 10f, 10f, 70f, 30f, 30f)
        waveView.updateData(values)

        caloriesProgress.setProgressWithAnimation(20f)
        exerciseProgress.setProgressWithAnimation(50f)

        textShader(tvDuration)
        textShader(tvCalories)
        textShader(tvExercise)
        textShader(tvSec)
        textShader1(tvBodyPart)
        setupHeartRateChart()



        tvSos.setOnClickListener {
            var phoneNumber = "+971504725319"
            var dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(dialIntent)

        }
        getActiveSessionDetail()
    }
    private fun getActiveSessionDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""

        api= ApiURL.getActiveSession+getIntent().getStringExtra("bookingid")
        Log.e("ActiveSessionApi",""+api)

        GetMethod(api,applicationContext).startMethod(object : ResponseData {


            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("GetActiveSessionResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        tvTrainer_name.setText(jsonObj.optJSONObject("data").optJSONObject("trainerData").optString("name"))
                        tvRating.setText(jsonObj.optJSONObject("data").optJSONObject("trainerData").optString("averageRating"))
                        tvSlotTime.setText(jsonObj.optJSONObject("data").optString("sessionTime"))
                        tvSec.setText(jsonObj.optJSONObject("data").optString("time_remaining_minutes"))
                        timeCurveProgress.setProgress(jsonObj.optJSONObject("data").optString("progress_percent").toFloat())
                        Glide.with(applicationContext).load(jsonObj.optJSONObject("data").optJSONObject("trainerData").optString("profile")).into(trainer_im)

                        for(i in 0 until jsonObj.optJSONObject("data").optJSONObject("trainerData")!!.optJSONArray("tags").length())
                        {
                            var json=jsonObj.optJSONObject("data").optJSONObject("trainerData")!!.optJSONArray("tags").optJSONObject(i)
                            var activityModel=ActivityModel()
                            activityModel.name= json.optString("name")
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = TrainerAmenitiesAdapter(applicationContext, activitiesModelList)
                        recycler.adapter = activityAdapter
                        if (!jsonObj.optJSONObject("data").optJSONObject("workouts").has("duration")){
                            linearGoal.visibility= View.GONE
                        }else{
                            workout_id=jsonObj.optJSONObject("data").optJSONObject("workouts").optString("id")
                            session_id=jsonObj.optJSONObject("data").optJSONObject("workouts").optString("session_id")

                            linearGoal.visibility = View.VISIBLE
                            /*activityScope.launch {
                                val slideUp = AnimationUtils.loadAnimation(this@ActiveSessionDetails, R.anim.slide_up_from_bottom)
                                linearGoal.startAnimation(slideUp)
                                slideUp.setAnimationListener(object :
                                  Animation.AnimationListener {
                                      override fun onAnimationStart(p0: Animation?) {
                                    }
                                    override fun onAnimationEnd(p0: Animation?) {
                                        activityScope.launch {
                                            linearprogress.visibility = View.VISIBLE // Make sure it's visible to animate
                                            var slideDown = AnimationUtils.loadAnimation(this@ActiveSessionDetails, R.anim.slide_down_from_top)
                                            linearprogress.startAnimation(slideDown)

                                            linearprogress.post { // Wait for linearprogress to be measured
                                                val progressHeight = linearprogress.height
                                                if (progressHeight == 0) {
                                                    return@post
                                                }

                                                val translateGoalDown = TranslateAnimation(
                                                    0f,
                                                    0f, // fromX, toX (no horizontal change)
                                                    0f,
                                                    progressHeight.toFloat() // fromY (current), toY (move down by progressHeight)
                                                )
                                                translateGoalDown.duration = 500 // Match progress animation duration
                                                translateGoalDown.fillAfter = true // IMPORTANT: Makes the view stay at the animated position


                                                translateGoalDown.setAnimationListener(object: Animation.AnimationListener {
                                                    override fun onAnimationStart(animation: Animation?) {}

                                                    override fun onAnimationEnd(animation: Animation?) {
                                                        val params = linearGoal.layoutParams as? LinearLayout.LayoutParams // Or LinearLayout.LayoutParams etc.
                                                        params?.let {
                                                            linearGoal.translationY = linearGoal.translationY + progressHeight
                                                            linearGoal.layoutParams = it // Apply changes
                                                            linearGoal.clearAnimation() // Clear fillAfter effect
                                                        }

                                                        // Optional: Scroll to make sure linearprogress and linearGoal are visible
                                                        nested?.post {
                                                            nested.smoothScrollTo(0, linearprogress.top)
                                                        }
                                                    }
                                                    override fun onAnimationRepeat(animation: Animation?) {}
                                                })
                                                linearGoal.startAnimation(translateGoalDown)
                                            }
                                        }

                                        //linearGoal.visibility = View.VISIBLE
                                        }

                                    override fun onAnimationRepeat(p0: Animation?) {

                                    }

                                })
                            }*/
                            tvDuration.setText(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("duration").replace("mins",""))
                            tvCalories.setText(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("calories"))
                            tvExercise.setText(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("exercises_count"))
                            durationProgress.setProgressWithAnimation(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("duration").replace("mins","").toFloat())
                            caloriesProgress.setProgressWithAnimation(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("calories").toFloat())
                            exerciseProgress.setProgressWithAnimation(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("exercises_count").toFloat())
                            caloriesProgress.setMaxValue(1500f)
                            durationProgress.setMaxValue(120f)
                            if (jsonObj.optJSONObject("data").optJSONObject("workouts").optString("image").equals("")){
                                im_workout.visibility=View.INVISIBLE
                            }else{
                                im_workout.visibility=View.VISIBLE
                                Glide.with(applicationContext).load(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("image")).into(im_workout)
                            }
                            tvBodyPart.setText(jsonObj.optJSONObject("data").optJSONObject("workouts").optString("name"))
                            for(i in 0 until jsonObj.optJSONObject("data").optJSONObject("workouts")!!.optJSONArray("exercises").length())
                            {
                                Log.e("exerciselist",""+jsonObj.optJSONObject("data").optJSONObject("workouts")!!.optJSONArray("exercises").length())
                                var jsonObject=jsonObj.optJSONObject("data").optJSONObject("workouts")!!.optJSONArray("exercises").optJSONObject(i)
                                var featuredWorkoutModel=FeaturedWorkoutModel()
                                featuredWorkoutModel.id=jsonObject.optString("id")
                                featuredWorkoutModel.workout_exercise_id=jsonObject.optString("workout_exercise_id")
                                featuredWorkoutModel.name=jsonObject.optString("name")
                                featuredWorkoutModel.image=jsonObject.optString("image")
                                featuredWorkoutModel.calories=jsonObject.optString("calories")
                                featuredWorkoutModel.category_name=jsonObject.optString("category_name")
                                featuredWorkoutModel.video=jsonObject.optString("video")
                                featuredWorkoutModel.sets=jsonObject.optString("sets")
                                featuredWorkoutModel.set_round=jsonObject.optString("set_round")
                                featuredWorkoutModel.reps=jsonObject.optString("reps")
                                featuredWorkoutModel.isComplete=jsonObject.optString("isComplete")
                                featuredWorkoutModel.type=jsonObject.optString("type")
                                featuredWorkoutModel.totalRest=jsonObject.optString("totalRest")
                                upcomingSessionArrayList.add(featuredWorkoutModel)
                            }
                            UpcomingRecycler.adapter =
                                UpcomingSessionLibraryAdapter(applicationContext, upcomingSessionArrayList,workout_id,session_id)
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
    private fun textShader1(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#B3FFFFFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
    private fun setupHeartRateChart() {
        val entries = listOf(
            Entry(0f, 70f),
            Entry(1f, 75f),
            Entry(2f, 68f),
            Entry(3f, 70f),
            Entry(4f, 65f),
            Entry(5f, 70f),
            Entry(6f, 69f),
            Entry(7f, 69f)
        )

        val dataSet = LineDataSet(entries, "Heart Rate").apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            color = Color.WHITE
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(this@ActiveSessionDetails, R.drawable.chart_gradient)
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 2f
        }

        val lineData = LineData(dataSet as ILineDataSet)
        heartRateChart.data = lineData

        heartRateChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            setHighlightPerTapEnabled(true)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false

            xAxis.apply {
                isEnabled = false
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }

            // Dashed vertical line at last point
            val limitLine = LimitLine(7f, "").apply {
                lineColor = Color.DKGRAY
                lineWidth = 1f
                enableDashedLine(10f, 10f, 0f)
            }
            xAxis.removeAllLimitLines()
            xAxis.addLimitLine(limitLine)

            // MarkerView with heart icon and bpm
            val marker = HeartRateMarkerView(this@ActiveSessionDetails, R.layout.hear_rate_marker)
            marker.chartView = this
            this.marker = marker
            animateX(1000)
            heartRateChart.post {
                heartRateChart.highlightValue(Highlight(7f, 0f, 0))
            }
            invalidate()
        }
    }

}