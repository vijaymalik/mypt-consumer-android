package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.LockBadgeAdapter.LockbadgeHolder
import co.com.mypt.model.HealthModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class HealthPreferenceAdapter(var context: Context?, var healthArrayList: ArrayList<HealthModel>) :
    RecyclerView.Adapter<HealthPreferenceAdapter.HealthHolder>() {

    class HealthHolder (view: View):RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var im=view.findViewById<ImageView>(R.id.im)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HealthPreferenceAdapter.HealthHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.health_prefernce_list, parent, false)
        return HealthHolder(view)
    }

    override fun onBindViewHolder(holder: HealthPreferenceAdapter.HealthHolder, position: Int) {
        var healthModel=healthArrayList.get(position)
        holder.tvname.setText(healthModel.name)
        Glide.with(context!!).load(healthModel.icon).fitCenter().into(holder.im)


    }

    override fun getItemCount(): Int {
      return healthArrayList.size
    }

}
