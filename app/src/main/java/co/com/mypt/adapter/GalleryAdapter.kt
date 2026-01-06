package co.com.mypt.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.GalleryModel
import com.bumptech.glide.Glide

class GalleryAdapter(val context: Context, val galleryArrayList: ArrayList<GalleryModel>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
        var selectedIndex = -1
    class ViewHolder(item:View) : RecyclerView.ViewHolder(item) {
        val videoView : VideoView = item.findViewById(R.id.videoView)
        val play : ImageView = item.findViewById(R.id.play)
        val im : ImageView = item.findViewById(R.id.im)
        val relative : RelativeLayout = item.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return galleryArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var galleryModel=galleryArrayList[position]
        if (galleryModel.is_video.equals("false")){
            Glide.with(context!!).load(galleryModel.media_path).fitCenter().into(holder.im)
            holder.relative.visibility=View.GONE
            holder.im.visibility=View.VISIBLE
        }else{
            val uri = Uri.parse(galleryModel.media_path)
            holder.videoView.setVideoURI(uri)
            holder.relative.visibility=View.VISIBLE
            holder.im.visibility=View.GONE
        }


        holder.videoView.setOnPreparedListener {
            holder.videoView.seekTo(1)
        }

        holder.videoView.setOnCompletionListener {
            holder.play.visibility = View.VISIBLE
        }

        if(selectedIndex == position){
            if(holder.play.visibility == View.GONE) {
                holder.play.visibility = View.VISIBLE
                holder.videoView.stopPlayback()
            }
            else{
                holder.play.visibility = View.GONE
                holder.videoView.start()
            }
        }else{
            holder.play.visibility = View.VISIBLE
            holder.videoView.stopPlayback()
        }

        holder.videoView.tag = position
        holder.videoView.setOnClickListener {
            if(selectedIndex == it.tag as Int){
                notifyItemChanged(selectedIndex)
                return@setOnClickListener
            }
            notifyItemChanged(selectedIndex)
            selectedIndex = it.tag as Int
            notifyItemChanged(selectedIndex)
        }
        holder.play.tag = position
        holder.play.setOnClickListener {
            if(selectedIndex == it.tag as Int){
                notifyItemChanged(selectedIndex)
                return@setOnClickListener
            }
            notifyItemChanged(selectedIndex)
            selectedIndex = it.tag as Int
            notifyItemChanged(selectedIndex)
        }
    }

}
