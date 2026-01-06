package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.WorkoutLibraryDateAdapter.RowHolder
import java.util.ArrayList

class MealListAdapter(var applicationContext: Context?, var dateArrayList: ArrayList<MealDateModel>) :
    RecyclerView.Adapter<MealListAdapter.MealListHolder>() {
    var selectedIndex = 0

    class MealListHolder(view: View): RecyclerView.ViewHolder(view){
        val date : TextView = view.findViewById(R.id.date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_date_list, parent, false)
        return MealListHolder(view)
    }

    override fun getItemCount(): Int {
        return dateArrayList.size

    }

    override fun onBindViewHolder(holder: MealListHolder, position: Int) {
        val data = dateArrayList[position]
        if (data.is_today.equals("true")){
            holder.date.text = "Today"

        }else{
            holder.date.text = data.date
        }

        if(selectedIndex == position){
            holder.date.background = applicationContext!!.resources.getDrawable(R.drawable.category_border_bg)
            holder.date.setTextColor(applicationContext!!.resources.getColor(R.color.white))
        }
        else{
            holder.date.background = null
            holder.date.setTextColor(applicationContext!!.resources.getColor(R.color.headingcolor))
        }


        holder.date.tag = position
        holder.date.setOnClickListener {
            val pos = it.tag as Int
            selectedIndex = pos
            val datamodel = dateArrayList[pos]
            var intent= Intent("selectDate")
            intent.putExtra("selected_date",datamodel.date)
            applicationContext!!.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

}
