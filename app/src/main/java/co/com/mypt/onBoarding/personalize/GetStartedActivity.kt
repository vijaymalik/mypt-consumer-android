package co.com.mypt.onBoarding.personalize

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import co.com.mypt.activities.MainActivity
import co.com.mypt.R
import co.com.mypt.fragments.BookingFragment

class GetStartedActivity : AppCompatActivity() {
    lateinit var tvcontinue : TextView
    lateinit var videoView: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        tvcontinue = findViewById(R.id.tvcontinue)
        tvcontinue.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("type","newUser")
            TaskStackBuilder.create(this).addNextIntentWithParentStack(intent).startActivities()
            finish()
        }
        videoView = findViewById(R.id.idVideoView)
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.gem)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnPreparedListener { mp -> mp.setLooping(true) }

    }

}