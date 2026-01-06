package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.TimeSLotModel

class TimeSLotAdapter(
    var applicationContext: Context,
    var timeSLotModelList: ArrayList<TimeSLotModel>,
    var selectedtimeSLotIds: MutableSet<String>
): RecyclerView.Adapter<TimeSLotAdapter.TimeSLotHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeSLotHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_all_list, parent, false)
        return TimeSLotHolder(view)
    }

    override fun onBindViewHolder(
        holder: TimeSLotHolder,
        position: Int
    ) {
        var genderModel= timeSLotModelList[position]
        holder.tv.text = genderModel.name
        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = selectedtimeSLotIds.contains(genderModel.id)

        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val pos = it.tag as Int
            val isChecked = !holder.check.isChecked
            holder.check.isChecked = isChecked

            if (isChecked) {
                selectedtimeSLotIds.add(timeSLotModelList[pos].id)
            } else {
                selectedtimeSLotIds.remove(timeSLotModelList[pos].id)
            }
        }
        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedtimeSLotIds.add(genderModel.id)
            } else {
                selectedtimeSLotIds.remove(genderModel.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return timeSLotModelList.size
    }

    class TimeSLotHolder (view: View): RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)
        var check=view.findViewById<CheckBox>(R.id.check)
    }
    fun getSelectedIdString(): String {
        return selectedtimeSLotIds.joinToString(",")
    }

}
