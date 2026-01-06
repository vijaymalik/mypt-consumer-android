package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import java.util.ArrayList

class IntakeAdapter(var context: Context?, var intakeList: ArrayList<IntakeModel>) :
    RecyclerView.Adapter<IntakeAdapter.IntakeHolder>() {
    class IntakeHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var tvquantity=view.findViewById<TextView>(R.id.tvquantity)
        var view=view.findViewById<View>(R.id.view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeAdapter.IntakeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.intake_list, parent, false)
        return IntakeHolder(view)
    }

    override fun onBindViewHolder(holder: IntakeAdapter.IntakeHolder, position: Int) {
        var intakeModel=intakeList[position]
        holder.tvTime.setText(intakeModel.time)
        holder.tvquantity.setText(intakeModel.ml)
        if (intakeList.size-1==position){
            holder.view.visibility=View.GONE
        }else{
            holder.view.visibility=View.VISIBLE

        }
    }

    override fun getItemCount(): Int {
        return intakeList.size
    }

}
