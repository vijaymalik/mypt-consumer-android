package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.FeatureAdapter.FeatureHolder
import co.com.mypt.model.FrequentlyModel
import java.util.ArrayList

class FrequentlyAdapter(var activity: Context, var frequentlyModelArrayList: ArrayList<FrequentlyModel>) :
    RecyclerView.Adapter<FrequentlyAdapter.FrequentlyHolder>() {
    class FrequentlyHolder (view: View):RecyclerView.ViewHolder(view){
        var tvprice=view.findViewById<TextView>(R.id.tvprice)
        var tvcurrency=view.findViewById<TextView>(R.id.tvcurrency)
        var tvRealPrice=view.findViewById<TextView>(R.id.tvRealPrice)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FrequentlyAdapter.FrequentlyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trending_list, parent, false)
        return FrequentlyHolder(view)
    }

    override fun onBindViewHolder(holder: FrequentlyAdapter.FrequentlyHolder, position: Int) {
        var frequentlyModel=frequentlyModelArrayList[position]
        holder.tvprice.setText(frequentlyModel.price)
    }

    override fun getItemCount(): Int {
        return frequentlyModelArrayList.size
    }

}
