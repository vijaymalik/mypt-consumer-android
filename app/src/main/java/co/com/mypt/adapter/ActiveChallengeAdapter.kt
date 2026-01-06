package co.com.mypt.adapter

import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.ActiveChallenge.ChallengeDetailActivity
import co.com.mypt.ActiveChallenge.MyActivityChallengeActivity
import co.com.mypt.R
import co.com.mypt.model.ActiveModel
import co.com.mypt.utils.CircularFillView

class ActiveChallengeAdapter(
   var  activeArraylist: ArrayList<ActiveModel>,
    var activity: MyActivityChallengeActivity): RecyclerView.Adapter<ActiveChallengeAdapter.ActiveHolder>() {
    class ActiveHolder (view: View): RecyclerView.ViewHolder(view){
        var tvChallenge=view.findViewById<TextView>(R.id.tvChallenge)
        var tvWeeks=view.findViewById<TextView>(R.id.tvWeeks)
        var tvWorkouts=view.findViewById<TextView>(R.id.tvWorkouts)
        var tvViewChallenge=view.findViewById<CardView>(R.id.tvViewChallenge)
        var circularBlueView = view.findViewById<CircularFillView>(R.id.circularBlueView)
        var circularOrangeView = view.findViewById<CircularFillView>(R.id.circularOrangeView)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActiveChallengeAdapter.ActiveHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.active_challenge_list, parent, false)
        return ActiveHolder(view)
    }

    override fun onBindViewHolder(
        holder: ActiveChallengeAdapter.ActiveHolder,
        position: Int
    ) {
       var activemodel=activeArraylist[position]
        holder.tvChallenge.text=activemodel.name
    //    holder.circularBlueView.progressPaint.color = activity.resources.getColor(R.color.progressBlue,null)
        holder.circularBlueView.cornerRadius = 38f
    /*    val animator = ObjectAnimator.ofFloat(holder.circularBlueView, "progress", 0f, .60f)
        animator.duration = 5000 // 5 seconds animation
        animator.start()*/

       // holder.circularOrangeView.progressPaint.color = activity.resources.getColor(R.color.orangecolor,null)
        holder.circularOrangeView.cornerRadius = 27f
      /*  val animator1 = ObjectAnimator.ofFloat(holder.circularOrangeView, "progress", 0f, .80f)
        animator1.duration = 5000 // 5 seconds animation
        animator1.start()*/
        holder.tvViewChallenge.setTag(position)
        holder.tvViewChallenge.setOnClickListener {
            var h=it.getTag() as Int
            var activemodel=activeArraylist[h]
            var intent= Intent(activity, ChallengeDetailActivity::class.java)
            activity.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return activeArraylist.size
    }

}
