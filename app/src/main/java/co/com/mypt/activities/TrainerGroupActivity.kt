package co.com.mypt.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    val data =
        "{\"status\":true,\"data\":{\"group\":{\"id\":\"1\",\"name\":\"hi enter a group anme\",\"description\":\"Meet the trainers who'll coach and support your progress.\",\"image\":null,\"type\":\"home\"},\"primary_trainer\":{\"id\":\"49\",\"name\":\"Suresh Chaurasia\",\"profile\":\"http://127.0.0.1:3002/storage/trainer/picture/1749736345_Suresh Chaurasia.png\",\"phone\":\"0586832007\",\"rating\":0,\"tags\":[\"Muscle Building\",\"Strength Conditioning\",\"Body Building\",\"Fat Loss\",\"Functional Training\",\"Injury Prevention & Rehab\",\"Sports Conditioning\",\"Cardiovascular Conditioning\",\"Lower/Upper Back Fix\",\"H.I.I.T Training\",\"PNF Stretch\"],\"badge\":\"PRIMARY\"},\"secondary_trainers\":[{\"id\":\"50\",\"name\":\"Thomas Larbi\",\"profile\":\"http://127.0.0.1:3002/storage/trainer/picture/1749737811_Thomas Larbi.png\",\"phone\":\"0561519136\",\"rating\":0,\"tags\":[\"Muscle Building\",\"Strength Conditioning\",\"Body Building\",\"Fat Loss\",\"Functional Training\",\"Boxing\",\"Kick Boxing\"],\"badge\":\"S1 TRAINER\"},{\"id\":\"57\",\"name\":\"Bekhzod Siddikov\",\"profile\":\"http://127.0.0.1:3002/storage/trainer/picture/1750067832_Bekhzod Siddikov.png\",\"phone\":\"0507274942\",\"rating\":0,\"tags\":[\"Muscle Building\",\"Strength Conditioning\",\"Body Building\",\"Fat Loss\",\"Boxing\",\"Kick Boxing\",\"Mixed Martial Arts\"],\"badge\":\"S2 TRAINER\"}],\"secondary_trainers_note\":\"Secondary trainers follow the same plan to maintain progress\"},\"msg\":\"Group details fetched successfully!\"}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trainer_group_view)
        init()
        dataConversion(data/*intent.getStringExtra(PASS_DATA)*/)


    }

    fun init(){
        exerciseRecyclerView= findViewById(R.id.exerciseRecyclerView)
        secondaryRecyclerView= findViewById(R.id.secondaryRecyclerView)
        primaryTrainerName= findViewById(R.id.primaryTrainerName)
        imgTrainer= findViewById(R.id.imgTrainer)
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