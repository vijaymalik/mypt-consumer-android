package co.com.mypt.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import co.com.mypt.R
import co.com.mypt.adapter.EditSetsAdapter
import co.com.mypt.model.ExcerciseModel

class EditExerciseActivity : AppCompatActivity() {
    lateinit var editAdapter: EditSetsAdapter
    var excerxiseModelList :ArrayList<ExcerciseModel> = ArrayList()
    lateinit var recycler:RecyclerView
    lateinit var linearshrink:LinearLayout
    lateinit var content:FrameLayout
    lateinit var timerText1:LinearLayout
    lateinit var timerText2:LinearLayout
    lateinit var timerImage2:ImageView
    lateinit var timerImage1:ImageView

    lateinit var caloriesLL1:LinearLayout
    lateinit var caloriesLL2:LinearLayout
    lateinit var caloriesImg1:ImageView
    lateinit var caloriesImg2:ImageView
    lateinit var back_1:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercise)
        recycler=findViewById(R.id.recycler)
        linearshrink=findViewById(R.id.linearshrink)
        timerImage1=findViewById(R.id.timerImage1)
        timerImage2=findViewById(R.id.timerImage2)
        timerText1=findViewById(R.id.timerText1)
        timerText2=findViewById(R.id.timerText2)
        caloriesLL1=findViewById(R.id.caloriesLL1)
        caloriesLL2=findViewById(R.id.caloriesLL2)
        caloriesImg1=findViewById(R.id.caloriesImg1)
        caloriesImg2=findViewById(R.id.caloriesImg2)
        back_1=findViewById(R.id.back_1)

        content=findViewById(R.id.content)

        back_1.setOnClickListener {
            finish()
        }

        for (i in 0..4) {
            var exerciseModel= ExcerciseModel()
            exerciseModel.setname="Set 1"
            excerxiseModelList.add(exerciseModel)
        }
        editAdapter= EditSetsAdapter(excerxiseModelList,this)
        recycler.adapter=editAdapter
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            registerReceiver(expandImage, IntentFilter("expandImage"), RECEIVER_EXPORTED)
        else
            registerReceiver(expandImage, IntentFilter("expandImage"))
    }

    val expandImage = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val transitionSet = TransitionSet().apply {
                addTransition(ChangeBounds())
                addTransition(Fade())
                duration = 500
            }
            TransitionManager.beginDelayedTransition(content, transitionSet)

            // Toggle visibility
            timerImage1.visibility = View.GONE
            timerText1.visibility = View.GONE
            timerImage2.visibility = View.VISIBLE
            timerText2.visibility = View.VISIBLE

            caloriesLL1.visibility = View.GONE
            caloriesLL2.visibility = View.VISIBLE
            caloriesImg1.visibility = View.GONE
            caloriesImg2.visibility = View.VISIBLE
        }
    }
  }