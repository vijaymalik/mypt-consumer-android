package co.com.mypt.GymWorkout.withTrainer

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.GymTrainerListAdapter
import co.com.mypt.adapter.TrainerListExerciseAdapter
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel

class GymHomeTrainersActivity : AppCompatActivity() {
    lateinit var exerciseRecyclerView : RecyclerView
    lateinit var trainerRecyclerView : RecyclerView
    lateinit var tvHome:TextView
    lateinit var tvGym:TextView
    var exerciseList = ArrayList<ExerciseModel>()
    var trainerList = ArrayList<TrainersModel>()
    lateinit var context_ : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gym_home_trainers)
        context_ = this

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        tvHome = findViewById(R.id.tvHome)
        tvGym = findViewById(R.id.tvGym)
        trainerRecyclerView = findViewById(R.id.trainerRecyclerView)

        exerciseRecyclerView.adapter = TrainerListExerciseAdapter(context_,exerciseList)
        trainerRecyclerView.adapter = GymTrainerListAdapter(context_,trainerList)

        tvGym.setOnClickListener {
            tvGym.setTextColor(resources.getColor(R.color.black))
            tvHome.setTextColor(resources.getColor(R.color.headingcolor))

            tvHome.background = null
            tvGym.background = resources.getDrawable(R.drawable.gym_button)

        }

        tvHome.setOnClickListener {
            tvGym.setTextColor(resources.getColor(R.color.headingcolor))

            tvHome.setTextColor(resources.getColor(R.color.black))

            tvHome.background = resources.getDrawable(R.drawable.gym_button)
            tvGym.background = null

        }
    }
}