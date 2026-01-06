package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.ActiveSession.BeforeArrivingActivity
import co.com.mypt.R
import co.com.mypt.adapter.AchievmentListAdapter.AchievmentListHolder
import co.com.mypt.model.CheckArrayModel

class CheckListAdapter(
   var  activity: BeforeArrivingActivity,
    var checkArrayList: ArrayList<CheckArrayModel>): RecyclerView.Adapter<CheckListAdapter.CheckHolder> (){
    class CheckHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvdetail=view.findViewById<TextView>(R.id.tvdetail)
        var tvName=view.findViewById<TextView>(R.id.tvName)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckListAdapter.CheckHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checklist, parent, false)
        return CheckHolder(view)
    }

    override fun onBindViewHolder(holder: CheckListAdapter.CheckHolder, position: Int) {
       var checkModel=checkArrayList[position]
        holder.tvdetail.setText(checkModel.description)
        holder.tvName.setText(checkModel.tittle)

    }

    override fun getItemCount(): Int {
       return checkArrayList.size
    }

}
