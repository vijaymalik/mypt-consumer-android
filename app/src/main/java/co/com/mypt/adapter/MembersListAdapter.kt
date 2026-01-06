package co.com.mypt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.JoinModel

class MembersListAdapter (
    var joinList: ArrayList<JoinModel>,
    var activity: Context
): RecyclerView.Adapter<MembersListAdapter.JoinHolder>() {
    class JoinHolder(var view: View): RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvgender=view.findViewById<TextView>(R.id.tvgender)
        var tvage=view.findViewById<TextView>(R.id.tvage)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
        var imEdit=view.findViewById<ImageView>(R.id.imEdit)
        var imDelete=view.findViewById<ImageView>(R.id.imDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.members_list, parent, false)
        return JoinHolder(view)

    }

    override fun getItemCount(): Int {
        return joinList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: JoinHolder, position: Int) {
        var joinModel=joinList[position]
        holder.relative.tag = position
        holder.imEdit.tag = position
        holder.imDelete.tag = position

        holder.tvname.text = joinModel.name
        holder.tvage.text = joinModel.age
        holder.imEdit.visibility= View.GONE
        holder.imDelete.visibility= View.GONE

        if (joinModel.gender == "null"){
            holder.tvgender.text = ""
            holder.tvgender.setBackgroundDrawable(null)
        }else{
            holder.tvgender.text = joinModel.gender
            holder.tvgender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.joining_rectangle))
        }
        if (joinModel.age == "" || joinModel.age == "null"){
            holder.tvage.visibility = View.GONE
            holder.tvage.text = ""
            holder.tvage.setBackgroundDrawable(null)

        }else{
            holder.tvage.visibility = View.VISIBLE
            holder.tvage.text = joinModel.age
            holder.tvage.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.joining_rectangle))
        }
    }

}