package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.NationModel

class NationalitiesAdapter(
    var context: Context,
    var nationalitiesList: ArrayList<NationModel>,
    var selectedNationIds: MutableSet<String>
): RecyclerView.Adapter<NationalitiesAdapter.NationHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NationHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_all_list, parent, false)
        return NationHolder(view)
    }

    override fun onBindViewHolder(
        holder: NationHolder,
        position: Int
    ) {
        var genderModel=nationalitiesList.get(position)
        holder.tv.setText(genderModel.name)
        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = selectedNationIds.contains(genderModel.id)

        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val pos = it.tag as Int
            val isChecked = !holder.check.isChecked
            holder.check.isChecked = isChecked

            if (isChecked) {
                selectedNationIds.add(nationalitiesList[pos].id)
            } else {
                selectedNationIds.remove(nationalitiesList[pos].id)
            }
        }
        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedNationIds.add(genderModel.id)
            } else {
                selectedNationIds.remove(genderModel.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return nationalitiesList.size
    }

    class NationHolder (view: View): RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)
        var check=view.findViewById<CheckBox>(R.id.check)
    }
    fun getSelectedIdString(): String {
        return selectedNationIds.joinToString(",")
    }
}
