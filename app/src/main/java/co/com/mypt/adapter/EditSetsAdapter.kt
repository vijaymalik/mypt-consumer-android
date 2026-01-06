package co.com.mypt.adapter

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.EditExerciseActivity
import co.com.mypt.model.ExcerciseModel


class EditSetsAdapter(
    var excerxiseModelList: ArrayList<ExcerciseModel>,
    var editExerciseActivity: EditExerciseActivity):RecyclerView.Adapter<EditSetsAdapter.EditSetholder>() {

    class EditSetholder(view: View):RecyclerView.ViewHolder(view) {
        var linearclose=view.findViewById<LinearLayout>(R.id.linearclose)
        var lineardetail=view.findViewById<LinearLayout>(R.id.lineardetail)
        var imarrowdown=view.findViewById<ImageView>(R.id.imarrowdown)
        var imarrowUp=view.findViewById<ImageView>(R.id.imarrowUp)
        var tvApply=view.findViewById<TextView>(R.id.tvApply)
        var tvMinimum=view.findViewById<TextView>(R.id.tvMinimum)
        var tvMax=view.findViewById<TextView>(R.id.tvMax)
        var imMinusMininmumReps=view.findViewById<ImageView>(R.id.imMinusMininmumReps)
        var imAddMinimumReps=view.findViewById<ImageView>(R.id.imAddMinimumReps)
        var imMaximumMinus=view.findViewById<ImageView>(R.id.imMaximumMinus)
        var imAddMaximumReps=view.findViewById<ImageView>(R.id.imAddMaximumReps)
        var imRestTime=view.findViewById<ImageView>(R.id.imRestTime)
        var imRestTimeAdd=view.findViewById<ImageView>(R.id.imRestTimeAdd)
        var tvRestTime=view.findViewById<TextView>(R.id.tvRestTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditSetsAdapter.EditSetholder {
       var layoutInflater=LayoutInflater.from(editExerciseActivity).inflate(R.layout.editsets_list,null)
        return EditSetholder(layoutInflater)
    }

    override fun onBindViewHolder(holder: EditSetsAdapter.EditSetholder, position: Int) {
       var exerciseModel=excerxiseModelList[position]
        holder.linearclose.setTag(position)
        holder.imarrowUp.setTag(position)
        holder.imMinusMininmumReps.setTag(position)
        holder.imAddMinimumReps.setTag(position)
        holder.imMaximumMinus.setTag(position)
        holder.imAddMaximumReps.setTag(position)
        holder.imRestTime.setTag(position)
        holder.imRestTimeAdd.setTag(position)
        holder.tvApply.setPaintFlags(holder.tvApply.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        holder.linearclose.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)

            holder.imarrowUp.visibility=View.VISIBLE
            holder.lineardetail.visibility=View.VISIBLE
            holder.imarrowdown.visibility=View.GONE

            val intent = Intent("expandImage")
            editExerciseActivity!!.sendBroadcast(intent)
        }
        holder.imMinusMininmumReps.setOnClickListener{
            var j=it.tag
            var counter = Integer.parseInt(holder.tvMinimum.text.toString())

            var exerciseModel = excerxiseModelList.get(j as Int)
            if (counter>0){
                counter-- // Decrease the counter
                holder.tvMinimum.text = counter.toString()
            }

        }
        holder.imAddMinimumReps.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)
            var counter = Integer.parseInt(holder.tvMinimum.text.toString())
            counter++ // Increase the counter
            holder.tvMinimum.text = counter.toString()
        }
        holder.imMaximumMinus.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)
            var counterMax = Integer.parseInt(holder.tvMax.text.toString())
            if (counterMax>0){
                counterMax-- // Decrease the counter
                holder.tvMax.text = counterMax.toString()
            }

        }
        holder.imAddMaximumReps.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)
            var counterMax = Integer.parseInt(holder.tvMax.text.toString())
            counterMax++ // Increase the counter
            holder.tvMax.text = counterMax.toString()
        }
        holder.imRestTimeAdd.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)
            var h=holder.tvRestTime.text.toString().split(":")
            var counterRest = h[0].toInt()

            counterRest++ // Increase the counter
            holder.tvRestTime.text = counterRest.toString()+":00"
        }
        holder.imRestTime.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)
            var h=holder.tvRestTime.text.toString().split(":")
            var counterRest = h[0].toInt()
            if (counterRest>0){
                counterRest-- // Decrease the counter
                holder.tvRestTime.text = counterRest.toString()+":00"
            }

        }

        holder.imarrowUp.setOnClickListener{
            var j=it.tag
            var exerciseModel = excerxiseModelList.get(j as Int)

            holder.imarrowdown.visibility=View.VISIBLE
            holder.lineardetail.visibility=View.GONE
            holder.imarrowUp.visibility=View.GONE

            val intent = Intent("expandImage")
            editExerciseActivity!!.sendBroadcast(intent)
        }

    }

    override fun getItemCount(): Int {
        return excerxiseModelList.size
    }

}
