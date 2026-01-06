package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter1.Activity_Holder
import co.com.mypt.model.CompleteModel

class CompleteBookingExerciseAdapter(var applicationContext: Context?, var completeModelList: ArrayList<CompleteModel>) : RecyclerView.Adapter<CompleteBookingExerciseAdapter.CompleteViewHolder>() {
    class CompleteViewHolder (view: View):RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tvname)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompleteBookingExerciseAdapter.CompleteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.excercise_layout, parent, false)
        return CompleteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CompleteBookingExerciseAdapter.CompleteViewHolder,
        position: Int
    ) {
        var selectTImeModel= completeModelList[position]
        holder.tvname.setText(selectTImeModel.name)

    }

    override fun getItemCount(): Int {
       return completeModelList.size
    }

}
