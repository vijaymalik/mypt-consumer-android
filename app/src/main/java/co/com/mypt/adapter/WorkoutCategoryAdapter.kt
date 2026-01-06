package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.CategoryWiseWorkoutActivity
import co.com.mypt.WorkoutLibrary.WorkoutLibraryActivity
import co.com.mypt.model.WorkoutCatModel
import com.bumptech.glide.Glide

class WorkoutCategoryAdapter(val context: Context?, val workoutCatArrayList: ArrayList<WorkoutCatModel>) :
    RecyclerView.Adapter<WorkoutCategoryAdapter.RowHolder>() {
    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainCard = view.findViewById<CardView>(R.id.mainCard)
        val image = view.findViewById<ImageView>(R.id.image)
        val name = view.findViewById<TextView>(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_cat_adapter, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return workoutCatArrayList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        var workoutCatModel=workoutCatArrayList[position]
        holder.mainCard.tag = position
        holder.name.setText(workoutCatModel.name)
        Glide.with(context!!).load(workoutCatModel.image).fitCenter().into(holder.image)

        holder.mainCard.setOnClickListener{
            var j =it.tag
            var workoutCatModel=workoutCatArrayList[j as Int]
            val intent= Intent(context, CategoryWiseWorkoutActivity::class.java)
            intent.putExtra("category_id",workoutCatModel.id)
            intent.putExtra("selectedScreen","categoryWise")
            context?.startActivity(intent)
        }
    }

}
