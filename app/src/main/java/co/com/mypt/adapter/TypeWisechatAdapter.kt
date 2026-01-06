package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ShopCategoryAdapter.ViewHolder
import co.com.mypt.model.TypeWiseChatModel
import java.util.ArrayList

class TypeWisechatAdapter(var applicationContext: Context?,
    var typewisechartArrayList: ArrayList<TypeWiseChatModel>
) : RecyclerView.Adapter<TypeWisechatAdapter.TypeWisechatHolder>() {
    var selectedIndex = 0

    class TypeWisechatHolder(view: View):RecyclerView.ViewHolder(view) {
        val categories : TextView = view.findViewById(R.id.categories)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TypeWisechatAdapter.TypeWisechatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_type_list, parent, false)
        return TypeWisechatHolder(view)
    }

    override fun onBindViewHolder(holder: TypeWisechatAdapter.TypeWisechatHolder, position: Int) {
        val data = typewisechartArrayList[position]
        holder.categories.text = data.name

        if(selectedIndex == position){
            holder.categories.background = applicationContext!!.resources.getDrawable(R.drawable.category_border_bg)
            holder.categories.setTextColor(applicationContext!!.resources.getColor(R.color.white))
        }
        else{
            holder.categories.background = null
            holder.categories.setTextColor(applicationContext!!.resources.getColor(R.color.headingcolor))
        }

        holder.categories.tag = position
        holder.categories.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
       return typewisechartArrayList.size
    }

}
