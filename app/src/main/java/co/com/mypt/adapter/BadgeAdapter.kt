package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.BadgeModel

class BadgeAdapter(val context: Context, val badgeArrayList: ArrayList<BadgeModel>):
    RecyclerView.Adapter<BadgeAdapter.RowHolder>() {
    class RowHolder(view:View) : RecyclerView.ViewHolder(view) {
        val lock : ImageView = view.findViewById(R.id.lock)
        val badgeImage : ImageView = view.findViewById(R.id.badgeImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_adapter, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        //return badgeArrayList.size
        return 5
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {

        if (position == 0){
            holder.lock.visibility = View.GONE
        }else
            holder.lock.visibility = View.VISIBLE

    }

}
