package co.com.mypt.WorkoutLibrary

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.WorkoutListAdapter
import co.com.mypt.model.WorkoutListModel
import com.android.volley.VolleyError
import org.json.JSONObject


class WorkoutLibraryActivity : AppCompatActivity() {
    var assign_id=""
    lateinit var videoView:VideoView
    lateinit var frameLayout:FrameLayout
    lateinit var workoutRecyclerView:RecyclerView
    lateinit var standard_bottom_sheetLinear:LinearLayout
    lateinit var linearbutton:LinearLayout
    lateinit var headerLayout:LinearLayout
    lateinit var linearDETAIL:LinearLayout
    lateinit var strengthLayout:LinearLayout
    lateinit var linearStart:LinearLayout
    lateinit var relative:RelativeLayout
    lateinit var scrollView:NestedScrollView
    lateinit var tvDetail:TextView
    lateinit var tvStrength:TextView
    lateinit var tvTime:TextView
    lateinit var bookSlot:TextView
    lateinit var tvStart:TextView
    lateinit var tvCalories:TextView
    lateinit var tvExercises:TextView
    lateinit var imWatch1: ImageView
    lateinit var imWatch2: ImageView
    lateinit var cardStart: CardView
    var workoutList :ArrayList<WorkoutListModel> = ArrayList()
    var workoutwithoutrestList :ArrayList<WorkoutListModel> = ArrayList()
    var changeFirstTime = 0
    private var isHeightReduced = false
    var setType=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_workout_library)
        scrollView=findViewById(R.id.scrollView)
        cardStart=findViewById(R.id.cardStart)
        imWatch1=findViewById(R.id.imWatch1)
        imWatch2=findViewById(R.id.imWatch2)
        bookSlot=findViewById(R.id.bookSlot)
        tvStart=findViewById(R.id.tvStart)
        headerLayout=findViewById(R.id.headerLayout)
        frameLayout=findViewById(R.id.frameLayout)
        videoView=findViewById(R.id.videoView)
        tvDetail=findViewById(R.id.tvDetail)
        linearDETAIL=findViewById(R.id.linearDETAIL)
        standard_bottom_sheetLinear=findViewById(R.id.standard_bottom_sheetLinear)
        workoutRecyclerView=findViewById(R.id.workoutRecyclerView)
        relative=findViewById(R.id.relative)
        strengthLayout=findViewById(R.id.strengthLayout)
        linearbutton=findViewById(R.id.linearbutton)
        linearStart=findViewById(R.id.linearStart)
        tvStrength=findViewById(R.id.tvStrength)
        tvTime=findViewById(R.id.tvTime)
        tvExercises=findViewById(R.id.tvExercises)
        tvCalories=findViewById(R.id.tvCalories)
        headerLayout.setOnClickListener {
            finish()
        }
        val height: Int
        val width: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val defaultDisplay =
                DisplayManagerCompat.getInstance(this).getDisplay(Display.DEFAULT_DISPLAY)
            val displayContext = createDisplayContext(defaultDisplay!!)

            width = displayContext.resources.displayMetrics.widthPixels
            height = displayContext.resources.displayMetrics.heightPixels

        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }

        linearDETAIL.setOnClickListener{
            linearbutton.visibility= View.GONE
            standard_bottom_sheetLinear.visibility= View.VISIBLE
            changeFirstTime ++

            animateHeightChange(relative, height/2)
            isHeightReduced = !isHeightReduced

        }


        val observer: ViewTreeObserver = strengthLayout.getViewTreeObserver()
        observer.addOnGlobalLayoutListener {
           // Log.e("changeFirstTime====>","$changeFirstTime")
            if(changeFirstTime==0){
                val h: Int = strengthLayout.height
                val layoutParams = LinearLayout.LayoutParams(width, height - (h+20))
                relative.layoutParams = layoutParams
            }
        }

        val videoParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        videoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        videoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        videoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        videoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        videoView.layoutParams = videoParams

        val frameParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        frameParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        frameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        frameParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        frameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

        frameLayout.layoutParams = frameParams

        // Set the video path from the "res/raw" directory

      //  getWorkoutDetail()
        try {
            assign_id=""+intent.getStringExtra("assign_id")

        }catch (e: Exception){

        }

    }
    private fun setVideoViewToLoop(Videoview : VideoView){
        Videoview.setOnCompletionListener {
            Videoview.start()
        }
    }



    private fun animateHeightChange(view: View, newHeight: Int) {
        val currentHeight = view.layoutParams.height

        // Create ValueAnimator to animate height change
        val animator = ValueAnimator.ofInt(currentHeight, newHeight)
        animator.duration = 300 // Duration of the animation in milliseconds
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout() // Request layout update
        }

        // Start the animation
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        getWorkoutDetail()
    }
    private fun getWorkoutDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@WorkoutLibraryActivity,"")
        progressDialog.show()

        var api=""
        api= ApiURL.wokoutdetail+intent.getStringExtra("wokout_id")

        Log.e("workoutDetailUrl",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
                override fun response(data: String?) {
                progressDialog.dismiss()
                workoutList.clear()
                Log.e("workoutDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        tvStrength.setText(jsonObj.optJSONObject("data").optString("name"))
                        tvCalories.setText(jsonObj.optJSONObject("data").optString("calories"))
                        tvExercises.setText(jsonObj.optJSONObject("data").optString("exercises_count"))
                        setType=jsonObj.optJSONObject("data").optString("type")
                        tvTime.setText(jsonObj.optJSONObject("data").optString("time_in_seconds")+"s")
                        if (jsonObj.optJSONObject("data").optBoolean("isExercisesCompleted").equals(true)){
                            imWatch1.visibility=View.GONE
                            imWatch2.visibility=View.GONE
                            bookSlot.setText("Complete")
                            tvStart.setText("Complete")
                            linearStart.setOnClickListener{
                                val intent1 = Intent(applicationContext, SessionSummaryActivity::class.java)
                                intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                                intent1.putExtra("session_id",jsonObj.optJSONObject("data").optString("session_id"))
                                startActivity(intent1)
                            }
                            cardStart.setOnClickListener{
                                val intent1 = Intent(applicationContext, SessionSummaryActivity::class.java)
                                intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                                intent1.putExtra("session_id",jsonObj.optJSONObject("data").optString("session_id"))
                                startActivity(intent1)
                            }
                        }else{
                            imWatch1.visibility=View.VISIBLE
                            imWatch2.visibility=View.VISIBLE
                            bookSlot.setText("Start")
                            tvStart.setText("Start")
                            linearStart.setOnClickListener{
                                startWorkout()
                            }
                            cardStart.setOnClickListener{
                                startWorkout()
                            }
                        }
                        if (jsonObj.optJSONObject("data").optString("workout_video").equals("")){
                             val videoPath = "android.resource://" + packageName + "/" + R.raw.gym_video
                            videoView.setVideoURI(Uri.parse(videoPath))
                        }else{
                            videoView.setVideoURI(Uri.parse(jsonObj.optJSONObject("data").optString("workout_video")))
                        }
                        videoView.start()
                        setVideoViewToLoop(videoView!!)
                        var jsonArrayExcercise=jsonObj.optJSONObject("data").optJSONArray("exercises")
                        for (i in 0 until jsonArrayExcercise.length()) {
                            var workoutListModel= WorkoutListModel()
                            var json=jsonArrayExcercise.optJSONObject(i)
                            workoutListModel.id=json.optString("id")
                            workoutListModel.workout_exercise_id=json.optString("workout_exercise_id")
                            workoutListModel.name=json.optString("name")
                            workoutListModel.sets=json.optString("sets")
                            workoutListModel.reps=json.optString("reps")
                            workoutListModel.isComplete=json.optString("isComplete")
                            workoutListModel.type=json.optString("type")
                            workoutListModel.image=json.optString("image")
                            workoutListModel.calories=json.optString("calories")
                            workoutListModel.totalRest=json.optString("totalRest")
                            workoutListModel.set_round=json.optString("set_round")
                            workoutList.add(workoutListModel)
                        }

                        var workoutlistAdapter= WorkoutListAdapter(workoutList,this@WorkoutLibraryActivity,intent.getStringExtra("wokout_id"),setType,assign_id.replace("null",""))
                        workoutRecyclerView.adapter=workoutlistAdapter
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
    private fun startWorkout() {
        val param: MutableMap<String, String> = HashMap()
        param["workout_id"] = intent.getStringExtra("wokout_id").toString()
        param["assignment_id"] = assign_id.replace("null","")
        Log.e("workoutStartParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.workoutstart,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("WorkoutStartRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        val session_id = resp.optJSONObject("data").optString("session_id")
                        val status = resp.optJSONObject("data").optString("status")
                        var intent1= Intent(this@WorkoutLibraryActivity,ActiveessionWorkoutActivity::class.java)
                        intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                        intent1.putExtra("session_id",session_id)
                        startActivity(intent1)
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