package co.com.mypt.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.Constants
import co.com.mypt.R
import co.com.mypt.activities.ChooseExcerciseActivity

import co.com.mypt.model.BodyPartsModel
import co.com.mypt.model.MusclesModel
import co.com.mypt.model.SelectExcerciseModel
import com.bumptech.glide.Glide

class MuscleAdapter(var musclesModelList: ArrayList<MusclesModel>, var activity: ChooseExcerciseActivity) : RecyclerView.Adapter<MuscleAdapter.MuscleHolder>() {
    private val selectedPositions = ArrayList<Int>()
    private val selectedIds = ArrayList<Int>()
    class MuscleHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var im=view.findViewById<ImageView>(R.id.im)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleAdapter.MuscleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bodyparts_list, parent, false)
        return MuscleHolder(view)
    }

    override fun onBindViewHolder(holder: MuscleAdapter.MuscleHolder, position: Int) {
        var musclesModel=musclesModelList[position]
        holder.tvname.setText(musclesModel.name)
        Glide.with(activity).load(musclesModel.image).fitCenter().error(R.drawable.guest_user).into(holder.im)
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
                selectedIds.remove(musclesModelList[pos].id.toInt())
            } else {
                // If item is not selected, select it
                selectedPositions.add(pos)
                selectedIds.add(musclesModelList[pos].id.toInt())
            }
            // Notify adapter to refresh the UI
            Log.e("selectedposition",""+selectedPositions)

            val intent = Intent("selectedMuscleId")
            intent.putIntegerArrayListExtra("selectedPositions",selectedIds)
            activity!!.sendBroadcast(intent)
            notifyItemChanged(pos)
        }
    }

    override fun getItemCount(): Int {
        return musclesModelList.size
    }

}
