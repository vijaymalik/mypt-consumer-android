package co.com.mypt.UpComingClasses

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ResourceImageAdapter
import co.com.mypt.adapter.SpecialitiesAdapter
import co.com.mypt.model.ResourceImageModel
import co.com.mypt.model.SpecialitiesModel

class ResourceDetailActivity : AppCompatActivity() {
    lateinit var videoView: VideoView
    lateinit var play : ImageView
    lateinit var recycler : RecyclerView
    var resourceImageArrayList = ArrayList<ResourceImageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resource_detail)

        videoView = findViewById(R.id.videoView)
        recycler = findViewById(R.id.recycler)
        play = findViewById(R.id.play)

        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.launcher_video)
        videoView.setVideoURI(uri)
        play.setOnClickListener {
            play.visibility = View.GONE
            videoView.start()
        }
        videoView.setOnPreparedListener {
            videoView.seekTo(1)
        }
        videoView.setOnCompletionListener {
            play.visibility = View.VISIBLE
        }
        videoView.setOnClickListener {
            if(videoView.isPlaying) {
                play.visibility = View.VISIBLE
                videoView.pause()
            }
            else{
                play.visibility = View.GONE
                videoView.resume()
            }
        }
        for(i in 0 until 5){
            var resourceImageModel=ResourceImageModel()
            resourceImageModel.image= ""+R.drawable.gymgirl
            resourceImageArrayList.add(resourceImageModel)

        }
        recycler.adapter = ResourceImageAdapter(this,resourceImageArrayList)
    }
}