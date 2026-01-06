package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.UpComingClasses.ResourceDetailActivity
import co.com.mypt.UpComingClasses.ResourcesDetailActivity

import co.com.mypt.model.ResourceModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class ResourceAdapter(var applicationContext: Context?, var resourceModelList: ArrayList<ResourceModel>) :
    RecyclerView.Adapter<ResourceAdapter.ResourceHolder>() {
    class ResourceHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
       // var tvRating=view.findViewById<TextView>(R.id.tvRating)
        var tvLike=view.findViewById<TextView>(R.id.tvLike)
        var im=view.findViewById<ImageView>(R.id.im)
        var resourceLayout=view.findViewById<FrameLayout>(R.id.resourceLayout)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResourceAdapter.ResourceHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resource_layout, parent, false)
        return ResourceHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceAdapter.ResourceHolder, position: Int) {
        var resourceModel=resourceModelList[position]
        holder.tvname.setText(resourceModel.title)
       // holder.tvView.setText(resourceModel.view)
       // holder.tvRating.setText(resourceModel.rating)
        Glide.with(applicationContext!!).load(resourceModel.image).fitCenter().into(holder.im)
        holder.resourceLayout.setTag(position)
        holder.resourceLayout.setOnClickListener{
            var j=it.tag
            var resourceModel=resourceModelList[j as Int]
            var intent= Intent(applicationContext,ResourcesDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.putExtra("tittle",resourceModel.title)
            intent.putExtra("image",resourceModel.image)
            intent.putExtra("description",resourceModel.description)
            intent.putExtra("date",resourceModel.date)
            intent.putExtra("reading_time",resourceModel.reading_time)
            applicationContext!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return resourceModelList.size
    }

}
