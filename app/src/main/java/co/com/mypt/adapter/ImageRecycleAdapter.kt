package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.FeatureAdapter.FeatureHolder
import co.com.mypt.model.ImageRecycleModel
import java.util.ArrayList

class ImageRecycleAdapter(var activity: Context, var imageRecycleArrayList: ArrayList<ImageRecycleModel>) :
    RecyclerView.Adapter<ImageRecycleAdapter.ImageRecycleHolder>() {
    class ImageRecycleHolder(view: View):RecyclerView.ViewHolder(view) {
        var idIVImage=view.findViewById<ImageView>(R.id.idIVImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageRecycleAdapter.ImageRecycleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list, parent, false)
        return ImageRecycleHolder(view)
    }

    override fun onBindViewHolder(holder: ImageRecycleAdapter.ImageRecycleHolder, position: Int) {
        var imageRecycleModel=imageRecycleArrayList[position]
        holder.idIVImage.setImageResource(imageRecycleModel.image)
    }

    override fun getItemCount(): Int {
        return imageRecycleArrayList.size
    }

}
