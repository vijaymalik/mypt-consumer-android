package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.model.ShopCategoryModel
import co.com.mypt.R
import java.util.ArrayList

class ShopCategoryAdapter(val context: Context?, val shopCategoryArraylist: ArrayList<ShopCategoryModel>) :
    RecyclerView.Adapter<ShopCategoryAdapter.ViewHolder>() {
        var selectedIndex = 0
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val categories : TextView = itemView.findViewById(R.id.categories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_category_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(selectedIndex == position){
            holder.categories.background = context!!.resources.getDrawable(R.drawable.category_border_bg)
            holder.categories.setTextColor(context!!.resources.getColor(R.color.white))
        }
        else{
            holder.categories.background = null
            holder.categories.setTextColor(context!!.resources.getColor(R.color.headingcolor))
        }

        holder.categories.tag = position
        holder.categories.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            notifyDataSetChanged()
        }
    }

}
