package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ExcerciseModel
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel
import com.bumptech.glide.Glide

class SearchTrainerListAdapter(
    val onProfileClick:(TrainersModel) -> Unit
) : RecyclerView.Adapter<SearchTrainerListAdapter.ViewHolder>() {
    private val trainerListModels = mutableListOf<TrainersModel>()

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val exerciseRecyclerView: RecyclerView = item.findViewById(R.id.exerciseRecyclerView)
        val primaryTrainerName: TextView = item.findViewById(R.id.primaryTrainerName)
        val avgRating: TextView = item.findViewById(R.id.avgRating)
        val imgTrainer: ImageView = item.findViewById(R.id.imgTrainer)
        val llProfileView: LinearLayout = item.findViewById(R.id.llProfileView)
        val tvDistance: TextView = item.findViewById(R.id.tvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.secondary_trainer_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trainerListModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trainersModel = trainerListModels[position]

        holder.primaryTrainerName.text = trainersModel.name
        val exerciseList = ArrayList<ExerciseModel>()
        for(i in 0 until trainersModel.activity.length()){
            val jsonObject=trainersModel.activity.optJSONObject(i)
            val exerciseModel=ExerciseModel()
            exerciseModel.id=jsonObject.optString("id")
            exerciseModel.name=jsonObject.optString("name")
            exerciseList.add(exerciseModel)
        }
        holder.exerciseRecyclerView.adapter = PrimaryTrainerTagAdapter(exerciseList ?: emptyList())

        if(trainersModel.averageRating == "null")
            holder.avgRating.text = "0"
        else
            holder.avgRating.text = trainersModel.averageRating

        Glide.with(holder.imgTrainer).load(trainersModel.profile).fitCenter()
            .into(holder.imgTrainer)
        holder.llProfileView.setOnClickListener {
            onProfileClick(trainersModel)
        }

        holder.tvDistance.text = "${trainersModel.distance} away"
        holder.tvDistance.visibility = View.VISIBLE

    }

    fun updateList(list: List<TrainersModel>) {
        trainerListModels.clear()
        trainerListModels.addAll(list)
        notifyDataSetChanged()
    }

}
