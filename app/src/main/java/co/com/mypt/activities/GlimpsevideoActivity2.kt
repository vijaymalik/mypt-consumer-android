package co.com.mypt.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.R

class GlimpsevideoActivity2 : AppCompatActivity() {
    lateinit var videoView:VideoView
    lateinit var linearplay:LinearLayout
    lateinit var imclose:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_glimpsevideo2)
        videoView=findViewById(R.id.videoView)
        linearplay=findViewById(R.id.linearplay)
        imclose=findViewById(R.id.imclose)

       // val uri = Uri.parse("android.resource://" + applicationContext!!.packageName + "/" + R.raw.launcher_video)
        val uri = Uri.parse(intent.getStringExtra("glimpsevideo"))
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener {
           videoView.seekTo(1)
        }

        videoView.setOnCompletionListener {
            linearplay.visibility = View.VISIBLE
        }
        linearplay.setOnClickListener {
            videoView.start()
            linearplay.visibility = View.GONE
        }
        imclose.setOnClickListener {
            videoView.stopPlayback()
            finish()
        }
        videoView.setOnClickListener{
            videoView.pause()
            linearplay.visibility = View.VISIBLE
        }
    }
}