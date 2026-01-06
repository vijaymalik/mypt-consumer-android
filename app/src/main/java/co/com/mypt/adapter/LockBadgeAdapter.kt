package co.com.mypt.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.LockBadgeModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.ArrayList

class LockBadgeAdapter(var context: Context?, var lockbadgeArrayList: ArrayList<LockBadgeModel>) :
    RecyclerView.Adapter<LockBadgeAdapter.LockbadgeHolder>() {
    class LockbadgeHolder (view: View):RecyclerView.ViewHolder(view){
      //  val progressBar =view.findViewById<CustomSplitCircularProgressBar>(R.id.splitProgressBar)
        val tvProgress =view.findViewById<TextView>(R.id.tvProgress)
        val progressIndicator =view.findViewById<CircularProgressIndicator>(R.id.progressIndicator)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LockBadgeAdapter.LockbadgeHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.lockbadge_list, parent, false)
            return LockbadgeHolder(view)
    }

    override fun onBindViewHolder(holder: LockBadgeAdapter.LockbadgeHolder, position: Int) {
      /*  holder.progressBar.segments = 10
        holder.progressBar.progress = 7
        holder.progressBar.filledColor = context!!.getColor(R.color.orangecolor)
        holder.progressBar.emptyColor = context!!.getColor(R.color.progress_track_color)
        holder.progressBar.invalidate()
        val currentProgress = holder.progressBar.progress
        Log.e("currentProgress",""+currentProgress)*/
        animateProgress(from = 0, to = 40, duration = 2800L,holder.progressIndicator)
    }

    override fun getItemCount(): Int {
        return 5
    }
    private fun animateProgress(
        from: Int,
        to: Int,
        duration: Long,
        progressIndicator: CircularProgressIndicator
    ) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = duration
        animator.addUpdateListener {
            val progress = it.animatedValue as Int
            progressIndicator.setProgressCompat(progress, true)
        }
        animator.start()
    }
}
