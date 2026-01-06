package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.LockBadgeAdapter.LockbadgeHolder
import co.com.mypt.model.AchievementListModel
import java.util.ArrayList

class AchievmentListAdapter(
    var context: Context?,
    var achievementArrayList: ArrayList<AchievementListModel>
) : RecyclerView.Adapter<AchievmentListAdapter.AchievmentListHolder>() {
    class AchievmentListHolder(view: View):RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AchievmentListAdapter.AchievmentListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.achievment_list, parent, false)
        return AchievmentListHolder(view)
    }

    override fun onBindViewHolder(
        holder: AchievmentListAdapter.AchievmentListHolder,
        position: Int
    ) {

    }

    override fun getItemCount(): Int {
        return 5
    }

}
