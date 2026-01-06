package co.com.mypt.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.MuscleAdapter.MuscleHolder
import co.com.mypt.model.BodyPartsModel

class BodyPartsAdapter(var bodypatrsArrayList: ArrayList<BodyPartsModel>, var activity: Activity) : RecyclerView.Adapter<BodyPartsAdapter.BodyPartsHolder>() {
    class BodyPartsHolder (view: View):RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tvname)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyPartsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bodyparts_list, parent, false)
        return BodyPartsHolder(view)
    }

    override fun getItemCount(): Int {
        return bodypatrsArrayList.size
    }

    override fun onBindViewHolder(holder: BodyPartsHolder, position: Int) {
        var bodyPartsModel=bodypatrsArrayList[position]
        holder.tvname.setText(bodyPartsModel.name)

    }

}
