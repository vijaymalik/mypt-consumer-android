package co.com.mypt.More

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

import co.com.mypt.adapter.FavouriteWorkoutAdapter
import co.com.mypt.model.FavouriteWorkoutModel
import co.com.mypt.model.MealListModel

class FavouriteWorkoutActivity : AppCompatActivity() {
    lateinit var FavouriteWorkoutRecyclerView:RecyclerView
    lateinit var headerLayout:LinearLayout
    lateinit var relative:RelativeLayout
    var favouriteWorkoutArrayList = ArrayList<FavouriteWorkoutModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_workout)
        FavouriteWorkoutRecyclerView=findViewById(R.id.FavouriteWorkoutRecyclerView)
        headerLayout=findViewById(R.id.headerLayout)
        relative=findViewById(R.id.relative)
        headerLayout.setOnClickListener{
            finish()
        }
        relative.setOnClickListener{

        }
        for (i in 0..4) {
            var favouriteWorkoutModel= FavouriteWorkoutModel()
            favouriteWorkoutModel.name="Upper Strength 2"
            favouriteWorkoutArrayList.add(favouriteWorkoutModel)
        }
        FavouriteWorkoutRecyclerView.adapter = FavouriteWorkoutAdapter(applicationContext,favouriteWorkoutArrayList)

    }
}