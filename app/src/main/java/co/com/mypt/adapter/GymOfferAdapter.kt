package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.GymOfferModel
import com.bumptech.glide.Glide

class GymOfferAdapter(var context: Context, var gymOfferModelList: ArrayList<GymOfferModel>) : RecyclerView.Adapter<GymOfferAdapter.GymOfferHolder>() {
    class GymOfferHolder(view: View):RecyclerView.ViewHolder(view) {
        var tv=view.findViewById<TextView>(R.id.tv)
        var im=view.findViewById<ImageView>(R.id.im)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GymOfferAdapter.GymOfferHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.gym_offer_layout,null)
        return GymOfferHolder(view)
    }

    override fun onBindViewHolder(holder: GymOfferAdapter.GymOfferHolder, position: Int) {
       var gymOfferModel=gymOfferModelList.get(position)
        holder.tv.setText(gymOfferModel.name)
        Glide.with(context!!).load(gymOfferModel.icon).fitCenter().into(holder.im)

    }

    override fun getItemCount(): Int {
        return gymOfferModelList.size
    }

}
