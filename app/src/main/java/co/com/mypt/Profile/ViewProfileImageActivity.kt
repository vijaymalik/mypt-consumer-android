package co.com.mypt.Profile

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R
import com.bumptech.glide.Glide

class ViewProfileImageActivity : AppCompatActivity() {
    lateinit var tvProfile:TextView
    lateinit var im:ImageView
    lateinit var headerLayout:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_image)
        tvProfile=findViewById(R.id.tvProfile)
        headerLayout=findViewById(R.id.headerLayout)
        im=findViewById(R.id.im)
        Glide.with(applicationContext!!).load(intent.getStringExtra("profileim")).fitCenter().error(R.drawable._no_image).into(im)

        headerLayout.setOnClickListener{
            finish()
        }

    }
}