package co.com.mypt.GymWorkout.withTrainer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.preference.PreferenceManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.SelectedGymWorkAdapter

class   SelectGymWorkoutActivity : AppCompatActivity() {
    lateinit var recycler: RecyclerView
    lateinit var tvcontinue:TextView
    lateinit var back:ImageView
    lateinit var sharedPreferences:SharedPreferences
    lateinit var edit:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_gym_workout)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        edit=sharedPreferences.edit()
        recycler=findViewById(R.id.recycler)
        tvcontinue=findViewById(R.id.tvcontinue)
        back=findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val imageList = listOf(
            R.drawable.with_trainer,
            R.drawable.without_trainer,
        )
        val selectedImageList = listOf(
            R.drawable.selected_withtrainer,
            R.drawable.selectedwithout_trainer,

        )
        var textList= listOf("With a Trainer","Without a Trainer")

        var goalsAdapter= SelectedGymWorkAdapter(this,imageList,textList,selectedImageList)

        recycler.adapter=goalsAdapter
    }
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(countReceiver, IntentFilter("withandWithoutTrainer"),
                RECEIVER_EXPORTED
            )
        }else{
            registerReceiver(countReceiver, IntentFilter("withandWithoutTrainer"))

        }
    }
    val countReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
            tvcontinue.setTypeface(null, Typeface.BOLD)
            tvcontinue.setOnClickListener {
                val intent_ = Intent(context, GymListActivity::class.java)
                if (intent?.getIntExtra("position", 0) == 1) {
                    edit.putString("typewithout", "withoutTrainer").apply()
                    intent_.putExtra("type", "withoutTrainer")
                }else{
                    edit.putString("typewithout", "withTrainer").apply()
                    intent_.putExtra("type", "withTrainer")
                }


                startActivity(intent_)
            }
        }

    }
}