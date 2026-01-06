package co.com.mypt

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.adapter.AchievmentListAdapter
import co.com.mypt.adapter.AwardBadgeAdapter
import co.com.mypt.model.AchievementListModel
import co.com.mypt.model.LockBadgeModel

class AchievementListActivity : AppCompatActivity() {
    lateinit var headerLayout:LinearLayout
    lateinit var recycler:RecyclerView
    var achievementArrayList = ArrayList<AchievementListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement_list)
        headerLayout=findViewById(R.id.headerLayout)
        recycler=findViewById(R.id.recycler)
        headerLayout.setOnClickListener{
            finish()
        }
        var acheivementListAdapter = AchievmentListAdapter(applicationContext, achievementArrayList)
        recycler.adapter = acheivementListAdapter
    }
}