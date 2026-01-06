package co.com.mypt.WorkoutLibrary

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
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
import co.com.mypt.adapter.SessionWorkoutAdapter
import co.com.mypt.model.SessionWorkoutModel
import com.android.volley.VolleyError
import org.json.JSONObject


class ActiveessionWorkoutActivity : AppCompatActivity() {
    lateinit var progressBar: ProgressBar
    lateinit var recycler_workout:RecyclerView
    var progressStatus = 0
    lateinit var sessionWorkoutAdapter:SessionWorkoutAdapter
    var sessionWorkoutList = ArrayList<SessionWorkoutModel>()
    lateinit var imRight:ImageView
    lateinit var tvProgressPercentage: TextView
    lateinit var linearheader: LinearLayout
    lateinit var imLeft:ImageView
    var count=0
    val handler = Handler()
    var progressvalue=0
    var session_id=""
    var exercise_id=""
    var workoutType=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activeession_workout)
        progressBar=findViewById(R.id.progressBar)
        recycler_workout=findViewById(R.id.recycler_workout)
        imRight=findViewById(R.id.imRight)
        imLeft=findViewById(R.id.imLeft)
        linearheader=findViewById(R.id.linearheader)
        tvProgressPercentage=findViewById(R.id.tvProgressPercentage)
        session_id= intent.getStringExtra("session_id").toString()
        try {
            exercise_id= intent.getStringExtra("exercise_id").toString()
        }
        catch (e : Exception){

        }
        linearheader.setOnClickListener {
            finish()
        }


        //fixed recycler view item in center of the screen
        /*val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycler_workout)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) */// For horizontal scrolling
        // Set the LayoutManager to the RecyclerView
        //recycler_workout.layoutManager = linearLayoutManager


        imRight.setOnClickListener{
            //fixed recyclerview scrolling
            recycler_workout.suppressLayout(false)
            if (count < sessionWorkoutList.size - 1) {
                count++
                recycler_workout.scrollToPosition(count)
                sessionWorkoutAdapter.selectedIndex = count
                sessionWorkoutAdapter.notifyItemChanged(count)
            }else{
                if (workoutType.equals("superset") || workoutType.equals("circuit") ){
                    count = 0
                    Log.e("getWorkoutDetail","RightSwipe")
                    getWorkoutDetail()
                    return@setOnClickListener
                }
                val intent1= Intent(applicationContext, SessionSummaryActivity::class.java)
                intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                intent1.putExtra("session_id",session_id)
                startActivity(intent1)
            }

            recycler_workout.suppressLayout(true)

        }
        imLeft.setOnClickListener{
            recycler_workout.suppressLayout(false)
            if (count>0){
                count--
                recycler_workout.scrollToPosition(count)
                sessionWorkoutAdapter.selectedIndex = count
                sessionWorkoutAdapter.notifyItemChanged(count)
            }

            recycler_workout.suppressLayout(true)

        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(exerciseComplete, IntentFilter("exerciseComplete"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(exerciseComplete, IntentFilter("exerciseComplete"))
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(start30s, IntentFilter("start30s"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(start30s, IntentFilter("start30s"))

        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("getWorkoutDetail","OnResume")
        getWorkoutDetail()
    }
    fun onCompleteExercise(position : Int){
        recycler_workout.suppressLayout(false)
        count = position
        Log.e("count","size---->${sessionWorkoutList.size}===== count--->"+count)
        if(sessionWorkoutList.isNotEmpty()){
            if (count < sessionWorkoutList.size - 1) {
                count++
                /*sessionWorkoutList.removeAt(count)
                sessionWorkoutAdapter.notifyItemRemoved(count)*/

                recycler_workout.scrollToPosition(count)
                sessionWorkoutAdapter.selectedIndex = count
                sessionWorkoutAdapter.notifyItemChanged(count)
            }else{
                if (workoutType == "superset" || workoutType == "circuit"){
                    count = 0
                    Log.e("getWorkoutDetail","Complete Exercise")
                    getWorkoutDetail()
                    return
                }
                val intent1 = Intent(applicationContext, SessionSummaryActivity::class.java)
                intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                intent1.putExtra("session_id",session_id)
                startActivity(intent1)
            }
        }

        recycler_workout.suppressLayout(true)
    }
    /*val exerciseComplete = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            recycler_workout.suppressLayout(false)
            count = intent!!.getIntExtra("position",0)
            Log.e("count","size---->${sessionWorkoutList.size}===== count--->"+count)
            if(sessionWorkoutList.isNotEmpty()){
                if (count < sessionWorkoutList.size - 1) {
                    count++
                    *//*sessionWorkoutList.removeAt(count)
                    sessionWorkoutAdapter.notifyItemRemoved(count)*//*

                    recycler_workout.scrollToPosition(count)
                    sessionWorkoutAdapter.selectedIndex = count
                    sessionWorkoutAdapter.notifyItemChanged(count)
                }else{
                    if (workoutType == "superset" || workoutType == "circuit"){
                        count = 0
                        Log.e("getWorkoutDetail","Complete Exercise")
                        getWorkoutDetail()
                        return
                    }
                    val intent1 = Intent(applicationContext, SessionSummaryActivity::class.java)
                    intent1.putExtra("wokout_id",getIntent().getStringExtra("wokout_id"))
                    intent1.putExtra("session_id",session_id)
                    startActivity(intent1)
                }
            }

            recycler_workout.suppressLayout(true)
        }

    }*/
    val start30s = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.getStringExtra("type").equals("start")){
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else{
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }

            }
        }

    }

    private fun getWorkoutDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@ActiveessionWorkoutActivity,"")

        if (!(isFinishing || isDestroyed)) {
            progressDialog.show()
        }

        var api=""
        api= ApiURL.wokoutdetail+intent.getStringExtra("wokout_id")+"&type=pending"

        Log.e("workoutDetailUrl",api)
        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                if (progressDialog.isShowing && !(isFinishing || isDestroyed)) {
                    progressDialog.dismiss()
                }
                sessionWorkoutList.clear()
                Log.e("workoutDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        tvProgressPercentage.setText(jsonObj.optJSONObject("data").optString("percentage")+" percent complete")
                        progressvalue=jsonObj.optJSONObject("data").optInt("percentage")
                        var jsonArrayExcercise=jsonObj.optJSONObject("data").optJSONArray("exercises")
                        workoutType=jsonObj.optJSONObject("data").optString("type")
                        if (jsonArrayExcercise.length()>0){
                            for (i in 0 until jsonArrayExcercise.length()) {
                                var sessionWorkoutModel= SessionWorkoutModel()
                                var json=jsonArrayExcercise.optJSONObject(i)
                                sessionWorkoutModel.id=json.optString("id")
                                sessionWorkoutModel.workout_exercise_id=json.optString("workout_exercise_id")
                                sessionWorkoutModel.name=json.optString("name")
                                sessionWorkoutModel.sets=json.optString("sets")
                                sessionWorkoutModel.reps=json.optString("reps")
                                sessionWorkoutModel.isComplete=json.optString("isComplete")
                                sessionWorkoutModel.type=json.optString("type")
                                sessionWorkoutModel.image=json.optString("image")
                                sessionWorkoutModel.calories=json.optString("calories")
                                sessionWorkoutModel.totalRest=json.optString("totalRest")
                                sessionWorkoutModel.video=json.optString("video")
                                sessionWorkoutModel.set_round=json.optString("set_round")

                                sessionWorkoutList.add(sessionWorkoutModel)
                                if (exercise_id==json.optString("id")){
                                    count=i
                                }
                            }
                            progressValueUpdate(progressvalue)

                            sessionWorkoutAdapter= SessionWorkoutAdapter(this@ActiveessionWorkoutActivity,sessionWorkoutList,session_id)
                            recycler_workout.adapter=sessionWorkoutAdapter
                            try {

                                recycler_workout.scrollToPosition(count)
                                sessionWorkoutAdapter.selectedIndex = count
                                sessionWorkoutAdapter.notifyItemChanged(count)
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                            //fixed recyclerview scrolling
                            recycler_workout.suppressLayout(true)
                        }
                        else{
                            val intent1= Intent(applicationContext, SessionSummaryActivity::class.java)
                            intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                            intent1.putExtra("session_id",session_id)
                            startActivity(intent1)
                            finish()
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                if (progressDialog.isShowing && !(isFinishing || isDestroyed)) {
                    progressDialog.dismiss()
                }
                error!!.printStackTrace()
            }

        })

    }
    fun progressValueUpdate(progressvalue1: Int) {
        tvProgressPercentage.setText(""+progressvalue1 +" percent complete")

        Thread {
            while (progressStatus < progressvalue1) {
                progressStatus += 1
                // Update the progress bar and display the current value in the text view
                handler.post {
                    progressBar.progress = progressStatus

                }
                try {
                    // Sleep for 200 milliseconds.
                    Thread.sleep(25)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            /* val intent = Intent(this@SplashActivity, HomeActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)
             finish()*/
        }.start()
    }
    fun handleRestTime() {
        recycler_workout.suppressLayout(false)

        if (count < sessionWorkoutList.size - 1) {
            count++
            /*sessionWorkoutList.removeAt(count)
            sessionWorkoutAdapter.notifyItemRemoved(count)*/

            recycler_workout.scrollToPosition(count)
            sessionWorkoutAdapter.selectedIndex = count
            sessionWorkoutAdapter.notifyItemChanged(count)
        }else{
            if (workoutType == "superset" || workoutType == "circuit"){
                count = 0
                Log.e("getWorkoutDetail","Handle Reset")

                getWorkoutDetail()
            }else{
                val intent1= Intent(applicationContext, SessionSummaryActivity::class.java)
                intent1.putExtra("wokout_id",intent.getStringExtra("wokout_id"))
                intent1.putExtra("session_id",session_id)
                startActivity(intent1)
                finish()
            }
        }
        recycler_workout.suppressLayout(true)
    }

}