package co.com.mypt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.SearchWorkoutActivity
import co.com.mypt.adapter.BodyPartsAdapter.BodyPartsHolder
import co.com.mypt.model.BodyPartsModel
import java.util.ArrayList

class WorkoutBodyPartsAdapter(
    var applicationContext: Context?,
    var bodypartsArrayList: ArrayList<BodyPartsModel>
) : RecyclerView.Adapter<WorkoutBodyPartsAdapter.BodyPartsHolder>() {


    private var selectedIndex = -1
    class BodyPartsHolder(view:View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tv)
        var checkbox=view.findViewById<CheckBox>(R.id.checkbox)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutBodyPartsAdapter.BodyPartsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bodyparts_layout, parent, false)
        return BodyPartsHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutBodyPartsAdapter.BodyPartsHolder, @SuppressLint("RecyclerView") position: Int) {
        var bodyPartsModel=bodypartsArrayList[position]
        holder.tvname.setText(bodyPartsModel.name)
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = (selectedIndex == position)


        holder.checkbox.setOnClickListener {
            selectedIndex = if (selectedIndex == position) -1 else position
            (applicationContext as SearchWorkoutActivity).updateFilterCount()
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            selectedIndex = if (selectedIndex == position) -1 else position
            (applicationContext as SearchWorkoutActivity).updateFilterCount()

            notifyDataSetChanged()
        }

    }

    fun getSelectedItem(): BodyPartsModel? {
        return if (selectedIndex != -1) bodypartsArrayList[selectedIndex] else null
    }

    fun getSelectedId(): String? {
        return getSelectedItem()?.id
    }

    override fun getItemCount(): Int {
        return bodypartsArrayList.size
    }

}
