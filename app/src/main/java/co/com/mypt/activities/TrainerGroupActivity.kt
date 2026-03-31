package co.com.mypt.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.Constants.PASS_DATA
import co.com.mypt.R
import co.com.mypt.adapter.PrimaryTrainerTagAdapter
import co.com.mypt.adapter.SecondaryTrainerListAdapter
import co.com.mypt.adapter.TrainerTagAdapter
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainerGroupDetail
import com.bumptech.glide.Glide
import com.google.gson.Gson

class TrainerGroupActivity : Activity() {
    lateinit var exerciseRecyclerView: RecyclerView
    lateinit var secondaryRecyclerView: RecyclerView
    lateinit var primaryTrainerName: TextView
    lateinit var imgTrainer: ImageView
    lateinit var back: ImageView
    lateinit var proceedView: LinearLayout
    lateinit var sharedPreferences: SharedPreferences

    val data =
        "{\"status\":true,\"data\":{\"group\":{\"id\":\"1\",\"name\":\"hi enter a group anme\",\"description\":\"Meet the trainers who'll coach and support your progress.\",\"image\":null,\"type\":\"home\"},\"primary_trainer\":{\"id\":\"49\",\"name\":\"Suresh Chaurasia\",\"profile\":\"http://127.0.0.1:3002/storage/trainer/picture/1749736345_Suresh Chaurasia.png\",\"phone\":\"0586832007\",\"rating\":0,\"tags\":[\"Muscle Building\",\"Strength Conditioning\",\"Body Building\",\"Fat Loss\",\"Functional Training\",\"Injury Prevention & Rehab\",\"Sports Conditioning\",\"Cardiovascular Conditioning\",\"Lower/Upper Back Fix\",\"H.I.I.T Training\",\"PNF Stretch\"],\"badge\":\"PRIMARY\"},\"secondary_trainers\":[{\"id\":\"50\",\"name\":\"Thomas Larbi\",\"profile\":\"http://127.0.0.1:3002/storage/trainer/picture/1749737811_Thomas Larbi.png\",\"phone\":\"0561519136\",\"rating\":0,\"tags\":[\"Muscle Building\",\"Strength Conditioning\",\"Body Building\",\"Fat Loss\",\"Functional Training\",\"Boxing\",\"Kick Boxing\"],\"badge\":\"S1 TRAINER\"},{\"id\":\"57\",\"name\":\"Bekhzod Siddikov\",\"profile\":\"http://127.0.0.1:3002/storage/trainer/picture/1750067832_Bekhzod Siddikov.png\",\"phone\":\"0507274942\",\"rating\":0,\"tags\":[\"Muscle Building\",\"Strength Conditioning\",\"Body Building\",\"Fat Loss\",\"Boxing\",\"Kick Boxing\",\"Mixed Martial Arts\"],\"badge\":\"S2 TRAINER\"}],\"secondary_trainers_note\":\"Secondary trainers follow the same plan to maintain progress\"},\"msg\":\"Group details fetched successfully!\"}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trainer_group_view)
        init()
        dataConversion(intent.getStringExtra(PASS_DATA))
    }

    fun init(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        exerciseRecyclerView= findViewById(R.id.exerciseRecyclerView)
        secondaryRecyclerView= findViewById(R.id.secondaryRecyclerView)
        primaryTrainerName= findViewById(R.id.primaryTrainerName)
        imgTrainer= findViewById(R.id.imgTrainer)
        proceedView= findViewById(R.id.proceedView)
        back= findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }
        proceedView.setOnClickListener {
            getPrimaryTrainer()
        }
        if (intent.getBooleanExtra("isFromHome", false)) {
            proceedView.visibility = View.GONE
        } else {
            proceedView.visibility = View.VISIBLE
        }
    }
    fun getPrimaryTrainer() {
        val studio_id=intent.getStringExtra("studio_id")
        val longitude=intent.getStringExtra("long")
        val latitude=intent.getStringExtra("lat")
        val trainerId=intent.getStringExtra("trainerId")
        val addressId=intent.getStringExtra("address_id")
        if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
            val intent = Intent(this, BestPlanTotalSessionWrapperActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("trainer_id", trainerId)
            intent.putExtra("studio_id", studio_id)
            intent.putExtra("type", sharedPreferences.getString("typeWorkout", ""))
            intent.putExtra("long", longitude)
            intent.putExtra("lat", latitude)
            intent.putExtra("address_id", addressId)
            startActivity(intent)
        } else {
            val intent = Intent(this, BestPlanTotalSessionWrapperActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("trainer_id", trainerId)
            intent.putExtra("studio_id", studio_id)
            intent.putExtra("type", sharedPreferences.getString("typeWorkout", ""))
            intent.putExtra("long", longitude)
            intent.putExtra("lat", latitude)
            intent.putExtra("address_id", addressId)
            startActivity(intent)

        }
    }
    fun dataConversion(data: String?) {
        val user: TrainerGroupDetail = Gson().fromJson(data, TrainerGroupDetail::class.java)
        println("++++++   $user")
        Glide.with(imgTrainer).load(user.data?.primary_trainer?.profile).fitCenter().into(imgTrainer)
        primaryTrainerName.text = user.data?.primary_trainer?.name
        val exerciseList = user.data?.primary_trainer?.tags?.map {
            val exerciseModel = ExerciseModel()
            exerciseModel.name = it ?: ""
            exerciseModel
        }

        exerciseRecyclerView.adapter =
            PrimaryTrainerTagAdapter(exerciseList ?: emptyList())
        secondaryRecyclerView.adapter= SecondaryTrainerListAdapter(user.data?.secondary_trainers?:emptyList())
    }
}