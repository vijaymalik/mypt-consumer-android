package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.ColorShopModel
import java.util.ArrayList

class ChooseColorAdapter(var context: Context?, var chooseColorList: ArrayList<ColorShopModel>) :
    RecyclerView.Adapter<ChooseColorAdapter.ChooseColorHolder>() {
    class ChooseColorHolder (view: View):RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tvname)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseColorAdapter.ChooseColorHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.choosecolor_list, parent, false)
        return ChooseColorHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseColorAdapter.ChooseColorHolder, position: Int) {
       var chooseColorShopModel=chooseColorList[position]
        holder.tvname.setText(chooseColorShopModel.name)
    }

    override fun getItemCount(): Int {
        return chooseColorList.size
    }

}
