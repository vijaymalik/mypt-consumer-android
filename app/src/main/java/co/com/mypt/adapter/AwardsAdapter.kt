package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Profile.AchievmentsActivity
import co.com.mypt.R
import co.com.mypt.adapter.MealVerticalListAdapter.MealVerticalHolder
import co.com.mypt.model.AwardsModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class AwardsAdapter(var context: Context?, var awardsArrayList: ArrayList<AwardsModel>) :
    RecyclerView.Adapter<AwardsAdapter.AwardsHolder>() {
    class AwardsHolder (view: View):RecyclerView.ViewHolder(view){
        var tvweight=view.findViewById<TextView>(R.id.tvweight)
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var im=view.findViewById<ImageView>(R.id.im)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AwardsAdapter.AwardsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.awards_list, parent, false)
        return AwardsHolder(view)
    }

    override fun onBindViewHolder(holder: AwardsAdapter.AwardsHolder, position: Int) {
        var awardsModel=awardsArrayList[position]
        holder.tvweight.setText(awardsModel.weight)
        holder.tvname.setText(awardsModel.name)
        Glide.with(context!!).load(awardsModel.icon).into(holder.im)
        holder.linear.setTag(position)
        holder.linear.setOnClickListener{
            var j=it.tag
            var awardsModel=awardsArrayList[j as Int]
            var intent= Intent(context,AchievmentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context!!.startActivity(intent)


        }
    }

    override fun getItemCount(): Int {
        return awardsArrayList.size
    }

}
