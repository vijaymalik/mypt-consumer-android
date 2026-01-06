package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.ReviewModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class ReviewAdapter(var context: Context, var reviewArrayList: ArrayList<ReviewModel>) :RecyclerView.Adapter<ReviewAdapter.reviewHolder>(){
    class reviewHolder(view:View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var tvDetail=view.findViewById<TextView>(R.id.tvDetail)
        var im=view.findViewById<ImageView>(R.id.im)
        var rBar=view.findViewById<RatingBar>(R.id.rBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.reviewHolder {
       var view=LayoutInflater.from(context).inflate(R.layout.review_layout,null)
        return reviewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.reviewHolder, position: Int) {
        var reviewModel=reviewArrayList.get(position)
        holder.tvname.setText(reviewModel.name)
        holder.tvTime.setText(reviewModel.time)
        holder.tvDetail.setText(reviewModel.description)
        Glide.with(context).load(reviewModel.image).fitCenter().into(holder.im)
        holder.rBar.rating = reviewModel.rating.toFloat()

    }

    override fun getItemCount(): Int {
       return reviewArrayList.size
    }

}
