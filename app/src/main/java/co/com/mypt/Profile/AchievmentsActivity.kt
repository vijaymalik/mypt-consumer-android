 package co.com.mypt.Profile

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R

class AchievmentsActivity : AppCompatActivity() {
    lateinit var headerLayout:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievments)
        headerLayout=findViewById(R.id.headerLayout)
        headerLayout.setOnClickListener{
            finish()
        }

    }
}