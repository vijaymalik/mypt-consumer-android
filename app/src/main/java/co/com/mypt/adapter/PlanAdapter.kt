package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Profile.MyPlanActivity
import co.com.mypt.R
import co.com.mypt.model.PlanModel
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlin.collections.ArrayList

class PlanAdapter(var applicationContext: Context, var planArrayList: ArrayList<PlanModel>) :
    RecyclerView.Adapter<PlanAdapter.PlanHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanAdapter.PlanHolder {
       var view=LayoutInflater.from(parent.context).inflate(R.layout.plan_list,null)
        return PlanHolder(view)
    }

    class PlanHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvplanName=view.findViewById<TextView>(R.id.tvplanName)
        var tvRemainingSession=view.findViewById<TextView>(R.id.tvRemainingSession)
        var imSheild=view.findViewById<ImageView>(R.id.imSheild)
        var linearPlan=view.findViewById<LinearLayout>(R.id.linearPlan)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
        var plan_progress=view.findViewById<LinearProgressIndicator>(R.id.plan_progress)

    }

    override fun onBindViewHolder(holder: PlanAdapter.PlanHolder, position: Int) {
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        holder.relative.layoutParams = RelativeLayout.LayoutParams(
            screenWidth-150,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        var planModel=planArrayList.get(position)
        holder.tvplanName.text =planModel.getTier
        holder.plan_progress.progress =planModel.remaining_sessions.toInt()
        holder.tvRemainingSession.text =planModel.remaining_sessions+" Sessions Remaining ending in "+planModel.remaining_days+" days"
        holder.linearPlan.setTag(position)
        Glide.with(applicationContext!!).load(planModel.tier_image).fitCenter().into(holder.imSheild)
        holder.linearPlan.setTag(position)
        holder.linearPlan.setOnClickListener{
            var j=it.tag
            var planModel=planArrayList.get(j as Int)
            var intent= Intent(applicationContext, MyPlanActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.putExtra("total_days",planModel.total_days)
            intent.putExtra("remaining_days",planModel.remaining_days)
            intent.putExtra("remaining_sessions",planModel.remaining_sessions)
            intent.putExtra("total_sessions",planModel.total_sessions)
            intent.putExtra("plan_name",planModel.getTier)
            intent.putExtra("isUpgrade",planModel.isUpgrade)
            intent.putExtra("plan_id",planModel.plan_id)
            applicationContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return planArrayList.size
    }

}
