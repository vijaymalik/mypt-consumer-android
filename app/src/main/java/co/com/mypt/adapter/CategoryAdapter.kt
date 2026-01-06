package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.CategoryModel
import java.util.ArrayList

class CategoryAdapter(var applicationContext: Context?, var categoryArrayList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {
    class CategoryHolder(view: View):RecyclerView.ViewHolder(view) {
        val tvname : TextView = view.findViewById(R.id.tvname)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.shop_by_category_list,null)
        return CategoryHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryHolder, position: Int) {
       var categoryModel=categoryArrayList[position]
        holder.tvname.setText(categoryModel.name)
    }

    override fun getItemCount(): Int {
      return categoryArrayList.size
    }

}
