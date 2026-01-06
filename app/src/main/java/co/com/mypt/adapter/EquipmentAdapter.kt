package co.com.mypt.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.EquipmentModel
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList

class EquipmentAdapter(var equipmentModelList: ArrayList<EquipmentModel>, var activity: Activity) : RecyclerView.Adapter<EquipmentAdapter.EquipmentHolder>() {

    private val selectedPositions = ArrayList<Int>()
    private val selectedIds = ArrayList<Int>()
    class EquipmentHolder (view: View):RecyclerView.ViewHolder(view){
      var tvname=view.findViewById<TextView>(R.id.tvname)
      var im=view.findViewById<ImageView>(R.id.im)
      var linear=view.findViewById<LinearLayout>(R.id.linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.equipment_list, parent, false)
        return EquipmentHolder(view)
    }

    override fun getItemCount(): Int {
      return equipmentModelList.size
    }

    override fun onBindViewHolder(holder: EquipmentHolder, position: Int) {
        var equipmentHolder=equipmentModelList[position]
        holder.linear.setTag(position)
        holder.tvname.setText(equipmentHolder.name)

        Glide.with(activity!!).load(equipmentHolder.image).fitCenter().error(R.drawable.dum).into(holder.im)
        val isSelected = selectedPositions.contains(position)
        if (isSelected){
            holder.linear.setBackgroundResource(R.drawable.equipment_rectangle)
        }else{
            holder.linear.setBackgroundResource(R.drawable.equipment_drawable)
        }
        holder.itemView.tag = position
        holder.itemView.setOnClickListener {

            val pos = it.tag as Int
            if (selectedPositions.contains(pos)) {
                // If item is already selected, unselect it
                selectedPositions.remove(pos)
                selectedIds.remove(equipmentModelList[pos].id.toInt())
            } else {
                // If item is not selected, select it
                selectedPositions.add(pos)
                selectedIds.add(equipmentModelList[pos].id.toInt())
            }
            // Notify adapter to refresh the UI
            Log.e("selectedposition",""+selectedPositions)

            val intent = Intent("selectedEquipmentId")
            intent.putIntegerArrayListExtra("selectedPositions",selectedIds)
            activity!!.sendBroadcast(intent)
            notifyItemChanged(pos)
        }}


    }


