package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.LanguageModel

class LanguageAdapter(
    var applicationContext: Context,
    var languageList: ArrayList<LanguageModel>,
    var selectedLanguageIds: MutableSet<String>
): RecyclerView.Adapter<LanguageAdapter.LanguageHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_all_list, parent, false)
        return LanguageHolder(view)
    }

    override fun onBindViewHolder(
        holder: LanguageHolder,
        position: Int
    ) {
        var genderModel=languageList.get(position)
        holder.tv.setText(genderModel.name)
        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = selectedLanguageIds.contains(genderModel.id)

        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val pos = it.tag as Int
            val isChecked = !holder.check.isChecked
            holder.check.isChecked = isChecked

            if (isChecked) {
                selectedLanguageIds.add(languageList[pos].id)
            } else {
                selectedLanguageIds.remove(languageList[pos].id)
            }
        }
        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedLanguageIds.add(genderModel.id)
            } else {
                selectedLanguageIds.remove(genderModel.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    class LanguageHolder (view: View): RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)
        var check=view.findViewById<CheckBox>(R.id.check)
    }

    fun getSelectedIdString(): String {
        return selectedLanguageIds.joinToString(",")
    }
}
