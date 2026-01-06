package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.system.Os.link
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.GlimpsevideoActivity2
import co.com.mypt.model.GlimpseCLassesModel


class GlimpseofOurCLassesAdapter(
    var applicationContext: Context?,
    var glimpseOfClassesArrayList: ArrayList<GlimpseCLassesModel>
) :RecyclerView.Adapter<GlimpseofOurCLassesAdapter.GlimpseHolder>(){
    var selectedIndex = -1
    private var player: ExoPlayer? = null

    class GlimpseHolder(view: View):RecyclerView.ViewHolder(view) {
        var videoView=view.findViewById<PlayerView>(R.id.videoView)

        val play : ImageView = view.findViewById(R.id.play)
        val linearplay : LinearLayout = view.findViewById(R.id.linearplay)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GlimpseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.glimpse_of_classes_list, parent, false)
        return GlimpseHolder(view)
    }

    @OptIn(UnstableApi::class)
    override fun onBindViewHolder(holder: GlimpseHolder, position: Int) {
        var glimpseCLassesModel=glimpseOfClassesArrayList[position]

      /*  holder.itemView.post {
            holder.itemView.requestLayout()
        }*/
        player = ExoPlayer.Builder(applicationContext!!).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(glimpseCLassesModel.video))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
            seekTo(1)
        }
        holder.videoView.player = player
        holder.videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

        holder.videoView.tag = position
        holder.videoView.setOnClickListener {
           /* if(selectedIndex == it.tag as Int){
                notifyItemChanged(selectedIndex)
                return@setOnClickListener
            }
            notifyItemChanged(selectedIndex)
            selectedIndex = it.tag as Int
            notifyItemChanged(selectedIndex)*/
        }
        holder.play.tag = position
        holder.linearplay.tag = position
        holder.linearplay.setOnClickListener {
            var h=it.tag
            var glimpseCLassesModel=glimpseOfClassesArrayList[h as Int]

            /*if(selectedIndex == it.tag as Int){
                notifyItemChanged(selectedIndex)
                return@setOnClickListener
            }
            notifyItemChanged(selectedIndex)
            selectedIndex = it.tag as Int
            notifyItemChanged(selectedIndex)*/
            var intent= Intent(applicationContext,GlimpsevideoActivity2::class.java)
            intent.putExtra("glimpsevideo",glimpseCLassesModel.video)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            applicationContext!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return glimpseOfClassesArrayList.size
    }

}
