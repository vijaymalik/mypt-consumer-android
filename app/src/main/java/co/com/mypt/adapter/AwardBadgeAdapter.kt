package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.BadgeAdapter.RowHolder
import co.com.mypt.model.BadgeModel
import java.util.ArrayList

class AwardBadgeAdapter(context: Context?, badgeArrayList: ArrayList<BadgeModel>):RecyclerView.Adapter<AwardBadgeAdapter.AwardHolder>() {
    class AwardHolder(view: View):RecyclerView.ViewHolder(view) {
        val lock : ImageView = view.findViewById(R.id.lock)
        val badgeImage : ImageView = view.findViewById(R.id.badgeImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AwardBadgeAdapter.AwardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.max_sets_badge_list, parent, false)
        return AwardHolder(view)
    }

    override fun onBindViewHolder(holder: AwardBadgeAdapter.AwardHolder, position: Int) {
        if (position == 0){
            holder.lock.visibility = View.GONE
        }else
            holder.lock.visibility = View.VISIBLE


    }

    override fun getItemCount(): Int {
       return 5
    }

}
