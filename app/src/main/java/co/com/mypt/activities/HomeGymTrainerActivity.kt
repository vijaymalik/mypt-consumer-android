package co.com.mypt.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.R


class HomeGymTrainerActivity :AppCompatActivity() {

    lateinit var gymWorkout : ImageView
    lateinit var homeWorkout : ImageView
    lateinit var back : ImageView
    lateinit var radioStateHome : ImageView
    lateinit var radioStateGym : ImageView
    lateinit var tvcontinue : TextView
    lateinit var tvcontinueView : LinearLayout
//    lateinit var im:ImageView
    var type=""
    lateinit var sharedPreferences:SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    var isOptionSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_gym_trainer_activity)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor=sharedPreferences.edit()
        gymWorkout = findViewById(R.id.gymWorkout)
        homeWorkout = findViewById(R.id.homeWorkout)
        tvcontinue = findViewById(R.id.tvcontinue)
        tvcontinueView = findViewById(R.id.tvcontinueView)
        back = findViewById(R.id.back)
        radioStateHome = findViewById(R.id.radioStateHome)
        radioStateGym = findViewById(R.id.radioStateGym)
//        im = findViewById(R.id.im)
        back.setOnClickListener{
            finish()
        }
        editor.putString("typeWorkout","").commit()
        editor.putString("typewithout","").commit()

        /*val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(30,25,30,0)
        gymWorkout.setLayoutParams(layoutParams)

        val layoutParams1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams1.setMargins(30,0,30,0)
        homeWorkout.setLayoutParams(layoutParams1)*/

        gymWorkout.setOnClickListener {
            type="work"
            gymWorkout.setImageResource(R.drawable.selected_gym_workout)
            homeWorkout.setImageResource(R.drawable.home_workout)

            tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle)
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
            radioStateHome.setImageDrawable(getDrawable(R.drawable.radio_unselect))
            radioStateGym.setImageDrawable(getDrawable(R.drawable.radio_select))
        }
        homeWorkout.setOnClickListener {
            type="home"
            gymWorkout.setImageResource(R.drawable.gym_workout)
            homeWorkout.setImageResource(R.drawable.selected_home_workout)

            tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle)
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
            radioStateHome.setImageDrawable(getDrawable(R.drawable.radio_select))
            radioStateGym.setImageDrawable(getDrawable(R.drawable.radio_unselect))

        }
        tvcontinueView.setOnClickListener {
            if (type.equals("home",true) || type.equals("work",true)){
                if(type.equals("home",true)){
                    editor.putString("typeWorkout",type).apply()
                    startActivity(Intent(this, TrainersListActivity::class.java))
                }
                else{
                    var intent=Intent(this, GymListActivity::class.java)
                    editor.putString("typeWorkout",type).apply()
                    startActivity(intent)

                }
            }

        }

    }
}