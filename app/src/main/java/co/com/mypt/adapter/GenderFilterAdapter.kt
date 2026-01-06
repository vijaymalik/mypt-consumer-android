package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.GenderModel

class GenderFilterAdapter(
    var applicationContext: Context,
    var genderList: ArrayList<GenderModel>,
    var selectedgenderIds: MutableSet<String>
): RecyclerView.Adapter<GenderFilterAdapter.GenderFilterHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenderFilterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_all_list, parent, false)
        return GenderFilterHolder(view)
    }

    override fun onBindViewHolder(
        holder: GenderFilterHolder,
        position: Int
    ) {
        var genderModel= genderList[position]
        holder.tv.text = genderModel.name
        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = selectedgenderIds.contains(genderModel.id)

        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val pos = it.tag as Int
            val isChecked = !holder.check.isChecked
            holder.check.isChecked = isChecked

            if (isChecked) {
                selectedgenderIds.add(genderList[pos].id)
            } else {
                selectedgenderIds.remove(genderList[pos].id)
            }
        }
        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedgenderIds.add(genderModel.id)
            } else {
                selectedgenderIds.remove(genderModel.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return genderList.size
    }

    class GenderFilterHolder(view: View): RecyclerView.ViewHolder(view) {
        var tv=view.findViewById<TextView>(R.id.tv)
        var check=view.findViewById<CheckBox>(R.id.check)

    }

    fun getSelectedIdString(): String {
        return selectedgenderIds.joinToString(",")
    }

}
