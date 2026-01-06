package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

import co.com.mypt.model.FavouriteWorkoutModel

class FavouriteWorkoutAdapter(var applicationContext: Context?, var favouriteWorkoutArrayList: ArrayList<FavouriteWorkoutModel>) : RecyclerView.Adapter<FavouriteWorkoutAdapter.FavouriteWorkoutHolder>() {
    class FavouriteWorkoutHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvName=view.findViewById<TextView>(R.id.tvName)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteWorkoutAdapter.FavouriteWorkoutHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_workout_list, parent, false)
        return FavouriteWorkoutHolder(view)
    }

    override fun onBindViewHolder(
        holder: FavouriteWorkoutAdapter.FavouriteWorkoutHolder,
        position: Int
    ) {
        var favouriteWorkoutModel=favouriteWorkoutArrayList[position]
        holder.tvName.setText(favouriteWorkoutModel.name)
    }

    override fun getItemCount(): Int {
        return favouriteWorkoutArrayList.size
    }

}
