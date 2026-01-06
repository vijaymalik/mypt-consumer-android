package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.model.TransformationModel
import co.com.mypt.R
import java.util.ArrayList

class TransformationAdapter(
    val context: Context?,
    val transformationArraylist: ArrayList<TransformationModel>
) : RecyclerView.Adapter<TransformationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transformation_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

}
