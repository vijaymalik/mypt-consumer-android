package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter1.Activity_Holder
import co.com.mypt.model.CategoryModel
import java.util.ArrayList

class CategoryFilterAdapter(var context: Context?, var categoryModelList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryFilterAdapter.CategoryFilterHolder>() {
    class CategoryFilterHolder (view: View):RecyclerView.ViewHolder(view){
        var checkname=view.findViewById<TextView>(R.id.checkname)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryFilterAdapter.CategoryFilterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return CategoryFilterHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryFilterAdapter.CategoryFilterHolder,
        position: Int
    ) {
        var categoryModel=categoryModelList[position]
        holder.checkname.setText(categoryModel.name)
    }

    override fun getItemCount(): Int {
       return categoryModelList.size
    }

}
