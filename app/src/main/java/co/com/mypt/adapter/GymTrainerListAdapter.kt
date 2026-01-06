package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.BookSlot
import co.com.mypt.activities.TrainerDetails
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel

class GymTrainerListAdapter(var context_: Context,var  trainerList: ArrayList<TrainersModel>) : RecyclerView.Adapter<GymTrainerListAdapter.GymTrainerListHolder>() {
    class GymTrainerListHolder(view: View):RecyclerView.ViewHolder(view) {
        val exerciseRecyclerView : RecyclerView = view.findViewById(R.id.exerciseRecyclerView)
        val bookSlot : TextView = view.findViewById(R.id.bookSlot)
        val relative : RelativeLayout = view.findViewById(R.id.relative)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GymTrainerListAdapter.GymTrainerListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_list_linear_adapter_layout, parent, false)
        return GymTrainerListHolder(view)
    }

    override fun onBindViewHolder(
        holder: GymTrainerListAdapter.GymTrainerListHolder,
        position: Int
    ) {
        val exerciseList = ArrayList<ExerciseModel>()
        holder.exerciseRecyclerView.adapter = ExerciseAdapter(context_,exerciseList)

        holder.bookSlot.tag = position
        holder.relative.tag = position
        holder.bookSlot.setOnClickListener {
            val pos = it.tag as Int
            val intent = Intent(context_, BookSlot::class.java)
            context_.startActivity(intent)
        }
        holder.relative.setOnClickListener {
            val pos = it.tag as Int
            val intent = Intent(context_, TrainerDetails::class.java)
            context_.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

}
