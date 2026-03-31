package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.Constants.KEY_STORIES_DATA
import co.com.mypt.R
import co.com.mypt.activities.StoryActivity
import co.com.mypt.model.GetStoriesList.Data.StoryList
import com.bumptech.glide.Glide


class StoryAdapter(
    val context: Context?,
    val upcomingClassArraylist: List<StoryList?>?,
    var latitude: Double?,
    var longitude: Double?
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val typeTrain : TextView = itemView.findViewById(R.id.typeTrain)
        val classImage : ImageView = itemView.findViewById(R.id.classImage)
        val iconTrn : ImageView = itemView.findViewById(R.id.iconTrn)
        val relative : RelativeLayout = itemView.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return upcomingClassArraylist?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var upcomingModel= upcomingClassArraylist?.get(position)
        Glide.with(holder.classImage).load(upcomingModel?.category_image).fitCenter().into(holder.classImage)
        Glide.with(holder.iconTrn).load(upcomingModel?.category_icon).fitCenter().into(holder.iconTrn)
        holder.typeTrain.text=upcomingModel?.category_name
        holder.relative.tag = position
        holder.relative.setOnClickListener{
           /* var j=it.tag
            var nearUpcomingCLassModel= upcomingClassArraylist?.get(j as Int)
            var intent= Intent(context, ClassDescriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("schedule_id",nearUpcomingCLassModel.schedule_id)
            intent.putExtra("latitude",latitude)
            intent.putExtra("longitude",longitude)
            context?.startActivity(intent)*/
            upcomingModel?.let {
                val intent= Intent(context, StoryActivity::class.java)
                intent.putExtra(KEY_STORIES_DATA, it)
                context?.startActivity(intent)
            }
        }
    }
}
