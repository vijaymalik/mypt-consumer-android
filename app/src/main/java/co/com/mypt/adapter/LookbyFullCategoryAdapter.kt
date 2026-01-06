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
import co.com.mypt.adapter.NearUpcomingClassAdapter.NearUpcomingClassHolder
import co.com.mypt.model.LookbyFullCategoryModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class LookbyFullCategoryAdapter(
   var  applicationContext: Context?,
    var lookbyFullCategoryModelList: ArrayList<LookbyFullCategoryModel>
) : RecyclerView.Adapter<LookbyFullCategoryAdapter.LookbyFullCategoryHolder>() {
    class LookbyFullCategoryHolder(view: View):RecyclerView.ViewHolder(view) {

        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvCLassess=view.findViewById<TextView>(R.id.tvCLassess)
        var im=view.findViewById<ImageView>(R.id.im)
        var frameLayout=view.findViewById<FrameLayout>(R.id.frameLayout)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LookbyFullCategoryAdapter.LookbyFullCategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.full_look_by_categorylist, parent, false)
        return LookbyFullCategoryHolder(view)
    }

    override fun onBindViewHolder(
        holder: LookbyFullCategoryAdapter.LookbyFullCategoryHolder,
        position: Int
    ) {
        var lookbyFullCategoryModel=lookbyFullCategoryModelList[position]
        holder.tvname.setText(lookbyFullCategoryModel.category_name)
        holder.tvCLassess.setText(lookbyFullCategoryModel.classCount)
        Glide.with(applicationContext!!).load(lookbyFullCategoryModel.category_image).fitCenter().into(holder.im)
        holder.frameLayout.setTag(position)
        holder.frameLayout.setOnClickListener{
            var j=it.tag
            var lookbyFullCategoryModel=lookbyFullCategoryModelList[j as Int]

            var intent= Intent(applicationContext, LookByCategoryClassesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.putExtra("category_id",lookbyFullCategoryModel.category_id)
            applicationContext?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lookbyFullCategoryModelList.size
    }

}
