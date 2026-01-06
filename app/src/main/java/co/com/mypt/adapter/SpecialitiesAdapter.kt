package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.SpecialitiesModel

class SpecialitiesAdapter(
    var context: Context,
    var specialitiesArrayList: ArrayList<SpecialitiesModel>
) : RecyclerView.Adapter<SpecialitiesAdapter.ViewHolder>() {
    class ViewHolder(item:View) : RecyclerView.ViewHolder(item) {
        var tv:TextView=item.findViewById(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.specialities_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return specialitiesArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var specialitiesModel=specialitiesArrayList[position]
        holder.tv.setText(specialitiesModel.name)
    }

}
