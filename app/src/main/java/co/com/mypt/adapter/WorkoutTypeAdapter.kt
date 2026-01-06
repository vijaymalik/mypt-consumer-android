package co.com.mypt.adapter

import android.content.Context
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
import co.com.mypt.adapter.CalendarAdapterAddTimeSlot.CalendarHolder
import co.com.mypt.model.WorkoutTypeModel
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import java.util.ArrayList

class WorkoutTypeAdapter(var context: Context?, var workoutTypeArrayList: ArrayList<WorkoutTypeModel>) :
    RecyclerView.Adapter<WorkoutTypeAdapter.WorkHolder>() {
    private val selectedPositions = mutableSetOf<Int>()
    private val selectedIds = ArrayList<Int>()
    class WorkHolder(view: View):RecyclerView.ViewHolder(view){
        val im: ImageView = itemView.findViewById(R.id.im)
        val linearRectangle: LinearLayout = itemView.findViewById(R.id.linearRectangle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.workout_type_list, parent, false)
        return WorkHolder(view)
    }

    override fun getItemCount(): Int {
      return workoutTypeArrayList.size
    }

    override fun onBindViewHolder(holder: WorkHolder, position: Int) {
       var workoutTypeModel=workoutTypeArrayList[position]
        holder.linearRectangle.setTag(position)

        if (selectedPositions.contains(position)) {
            holder.linearRectangle.setBackgroundResource(R.drawable.workout_rectangle_lght_blue)
        } else {
            holder.linearRectangle.setBackgroundResource(R.drawable.workout_layout_rectangle)

        }
        holder.linearRectangle.setOnClickListener{
            val pos = it.tag as Int
            if (selectedPositions.contains(pos)) {
                // If item is already selected, unselect it
                selectedPositions.remove(pos)
                selectedIds.remove(workoutTypeArrayList[pos].id.toInt())
            } else {
                // If item is not selected, select it
                selectedPositions.add(pos)
                selectedIds.add(workoutTypeArrayList[pos].id.toInt())
            }
            // Notify adapter to refresh the UI
            val intent = Intent("selectedWorkoutType")
            if(selectedPositions.isNotEmpty()){
                intent.putExtra("count", "1")
            }else{
                intent.putExtra("count", "0")
            }
            intent.putIntegerArrayListExtra("selectedPositions",selectedIds)
            context!!.sendBroadcast(intent)

            notifyItemChanged(pos)


        }

        Glide.with(context!!).load(workoutTypeModel.image).fitCenter().into(holder.im)

    }

}
