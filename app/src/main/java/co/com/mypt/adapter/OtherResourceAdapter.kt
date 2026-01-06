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
import co.com.mypt.R
import co.com.mypt.UpComingClasses.ResourcesDetailActivity
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.OtherResourceModel
import com.bumptech.glide.Glide

class OtherResourceAdapter(var context: Context?, var otherresourceModelList: ArrayList<OtherResourceModel>) : RecyclerView.Adapter<OtherResourceAdapter.OtherHolder>() {
    class OtherHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvResourcesName=view.findViewById<TextView>(R.id.tvResourcesName)
        var tvDescription=view.findViewById<TextView>(R.id.tvDescription)
        var tvreadTime=view.findViewById<TextView>(R.id.tvreadTime)
        var imResource=view.findViewById<ImageView>(R.id.imResource)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.other_resource_list, parent, false)
        return OtherHolder(view)
    }

    override fun getItemCount(): Int {
        return otherresourceModelList.size
    }

    override fun onBindViewHolder(holder: OtherHolder, position: Int) {
       var otherResourceModel=otherresourceModelList[position]
        holder.tvResourcesName.setText(otherResourceModel.title)
        holder.tvDescription.setText(otherResourceModel.description)
        holder.tvreadTime.setText(otherResourceModel.reading_time)
        Glide.with(context!!).load(otherResourceModel.image).fitCenter().error(R.drawable._no_image).into(holder.imResource)
        holder.relative.setTag(position)
        holder.relative.setOnClickListener{
            var j=it.tag
            var resourceModel=otherresourceModelList[j as Int]
            var intent= Intent(context, ResourcesDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.putExtra("tittle",resourceModel.title)
            intent.putExtra("image",resourceModel.image)
            intent.putExtra("description",resourceModel.description)
            intent.putExtra("date",resourceModel.date)
            intent.putExtra("reading_time",resourceModel.reading_time)
            context!!.startActivity(intent)

        }
    }

}
