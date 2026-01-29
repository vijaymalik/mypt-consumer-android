package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainerGroupDetail.Data.SecondaryTrainer
import com.bumptech.glide.Glide

class SecondaryTrainerListAdapter(
    val trainerListModels: List<SecondaryTrainer?>,
) : RecyclerView.Adapter<SecondaryTrainerListAdapter.ViewHolder>() {


    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val exerciseRecyclerView: RecyclerView = item.findViewById(R.id.exerciseRecyclerView)
        val primaryTrainerName: TextView = item.findViewById(R.id.primaryTrainerName)
        val avgRating: TextView = item.findViewById(R.id.avgRating)
        val imgTrainer: ImageView = item.findViewById(R.id.imgTrainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.secondary_trainer_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trainerListModels?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trainersModel = trainerListModels[position]

        holder.primaryTrainerName.text = trainersModel?.name

        val exerciseList = trainersModel?.tags?.map {
            val exerciseModel = ExerciseModel()
            exerciseModel.name = it ?: ""
            exerciseModel
        }
        holder.exerciseRecyclerView.adapter = PrimaryTrainerTagAdapter(exerciseList ?: emptyList())


        if (trainersModel?.rating == 0)
            holder.avgRating.text = "0"
        else
            holder.avgRating.text = trainersModel?.rating.toString()

        Glide.with(holder.imgTrainer).load(trainersModel?.profile).fitCenter()
            .into(holder.imgTrainer)

    }


}
