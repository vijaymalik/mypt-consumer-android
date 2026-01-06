package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.AchievmentListAdapter.AchievmentListHolder
import co.com.mypt.model.PreferedTrainerModel

class PrefferedTrainerAdapter(
    var applicationContext: Context,
    var preferedTrainerArraylist: ArrayList<PreferedTrainerModel>): RecyclerView.Adapter<PrefferedTrainerAdapter.PreferedHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreferedHolder {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.prefered_trainer_list,null, false)
        return PreferedHolder(view)
    }

    override fun onBindViewHolder(
        holder: PreferedHolder,
        position: Int
    ) {


    }

    override fun getItemCount(): Int {
        return 6
    }

    class PreferedHolder(view: View) : RecyclerView.ViewHolder(view){
        var tvTrainerName=view.findViewById<TextView>(R.id.tvTrainerName)
        var tvtime=view.findViewById<TextView>(R.id.tvtime)
        var im=view.findViewById<ImageView>(R.id.im)
        var check=view.findViewById<CheckBox>(R.id.check)

    }

}
