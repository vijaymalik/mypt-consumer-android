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
import co.com.mypt.UpComingClasses.ClassDescriptionActivity
import co.com.mypt.UpComingClasses.LookByCategoryClassesActivity
import co.com.mypt.adapter.ResourceAdapter.ResourceHolder
import co.com.mypt.model.LookbyCategoryModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class LookbyCategoryAdapter(
    var applicationContext: Context?,
    var lookbyCategoryModelList: ArrayList<LookbyCategoryModel>
) : RecyclerView.Adapter<LookbyCategoryAdapter.LookbyCategoryHolder>() {
    class LookbyCategoryHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvCLassess=view.findViewById<TextView>(R.id.tvCLassess)
        var im=view.findViewById<ImageView>(R.id.im)
        var frameLayout=view.findViewById<FrameLayout>(R.id.frameLayout)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LookbyCategoryAdapter.LookbyCategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.look_by_category_list, parent, false)
        return LookbyCategoryHolder(view)
    }

    override fun onBindViewHolder(
        holder: LookbyCategoryAdapter.LookbyCategoryHolder,
        position: Int
    ) {
        var lookbyCategoryModel=lookbyCategoryModelList[position]
        holder.tvname.setText(lookbyCategoryModel.category_name)
        holder.tvCLassess.setText(lookbyCategoryModel.classCount)
        Glide.with(applicationContext!!).load(lookbyCategoryModel.category_image).fitCenter().into(holder.im)

        holder.frameLayout.setTag(position)
        holder.frameLayout.setOnClickListener{
            var j=it.tag
            var lookbyCategoryModel=lookbyCategoryModelList[j as Int]
            var intent= Intent(applicationContext, LookByCategoryClassesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.putExtra("category_id",lookbyCategoryModel.category_id)

            applicationContext?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        if (lookbyCategoryModelList.size>6){
            return 6
        }else{
            return lookbyCategoryModelList.size

        }
    }

}
