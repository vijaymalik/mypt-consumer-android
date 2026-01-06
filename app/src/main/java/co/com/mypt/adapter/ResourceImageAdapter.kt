package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.UpComingClasses.ResourceDetailActivity
import co.com.mypt.model.ResourceImageModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.NonDisposableHandle.parent

class ResourceImageAdapter(var activity: ResourceDetailActivity,var  resourceImageArrayList: ArrayList<ResourceImageModel>) : RecyclerView.Adapter<ResourceImageAdapter.ImageHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResourceImageAdapter.ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resource_related_image, parent, false)

        return ImageHolder(view)
    }

    class ImageHolder(view: View):RecyclerView.ViewHolder(view) {
        var im=view.findViewById<ImageView>(R.id.im)


    }

    override fun onBindViewHolder(holder: ResourceImageAdapter.ImageHolder, position: Int) {
        var resourceImageModel=resourceImageArrayList[position]
        Glide.with(activity).load(R.drawable.gymgirl).fitCenter().into(holder.im)

    }

    override fun getItemCount(): Int {
       return resourceImageArrayList.size
    }

}
