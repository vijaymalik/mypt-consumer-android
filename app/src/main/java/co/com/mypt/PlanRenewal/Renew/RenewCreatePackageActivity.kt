package co.com.mypt.PlanRenewal.Renew

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.R
import co.com.mypt.activities.TrainersListActivity

class RenewCreatePackageActivity : AppCompatActivity() {

    lateinit var gymWorkout : ImageView
    lateinit var homeWorkout : ImageView
    lateinit var back : ImageView
    lateinit var tvcontinue : TextView
    lateinit var im:ImageView
    var type=""
    var typeArrayList = ArrayList<String>()

    lateinit var sharedPreferences:SharedPreferences
    lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_renew_create_package)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor=sharedPreferences.edit()
        gymWorkout = findViewById(R.id.gymWorkout)
        homeWorkout = findViewById(R.id.homeWorkout)
        tvcontinue = findViewById(R.id.tvcontinue)
        back = findViewById(R.id.back)

        back.setOnClickListener{
            finish()
        }

        typeArrayList = intent.getStringArrayListExtra("typeArrayList")?: arrayListOf()
        Log.e("typeArrayList","$typeArrayList")

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(30,25,30,0)
        gymWorkout.layoutParams = layoutParams

        val layoutParams1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams1.setMargins(30,0,30,0)
        homeWorkout.layoutParams = layoutParams1

        gymWorkout.setOnClickListener {
            type="gym"
            gymWorkout.setImageResource(R.drawable.selected_gym_workout)
            homeWorkout.setImageResource(R.drawable.home_workout)

            tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
        }
        homeWorkout.setOnClickListener {
            type="home"
            gymWorkout.setImageResource(R.drawable.gym_workout)
            homeWorkout.setImageResource(R.drawable.selected_home_workout)

            tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
        }
        tvcontinue.setOnClickListener {
            if(typeArrayList.contains(type)){
                /*if(type.equals("home",true)){
                    startActivity(Intent(this, HomeTotalSessionActivity::class.java))
                }
                else{
                    val intent = Intent(this, RenewHomeGymSessionActivity::class.java)
                    intent.putExtra("id",getIntent().getStringExtra("id"))
                    startActivity(intent)
                }*/
                val intent = Intent(this, RenewHomeGymSessionActivity::class.java)
                intent.putExtra("id",getIntent().getStringExtra("id"))
                startActivity(intent)
            }else{
                if(type.equals("home",true)){
                    editor.putString("typeWorkout",type).apply()
                    startActivity(Intent(this, TrainersListActivity::class.java))
                }
                else{
                    val intent=Intent(this, GymListActivity::class.java)
                    editor.putString("typeWorkout","work").apply()
                    startActivity(intent)
                }
            }
        }

    }
    }
