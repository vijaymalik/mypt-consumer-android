package co.com.mypt.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

class GenderAdapter(
    var activity: FragmentActivity?,
    var imageList: List<Int>,
    var textList: List<String>,
    var selectedImageList: List<Int>,
    var iconList: List<Int>

) : RecyclerView.Adapter<GenderAdapter.GenderHolder>() {
    public var selectedPositions = -1

    class GenderHolder (view:View):RecyclerView.ViewHolder(view){
        val tv: TextView = itemView.findViewById(R.id.tv)
        val relative: View = itemView.findViewById(R.id.relative)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val icon: ImageView = itemView.findViewById(R.id.icon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gender_layout, parent, false)
        return GenderHolder(view)
    }

    override fun onBindViewHolder(holder: GenderHolder, position: Int) {
        holder.itemView.tag = position
        if (selectedPositions==position) {
            holder.imageView.setImageResource(selectedImageList[position])  // Use selected background
        } else {
            holder.imageView.setImageResource(imageList[position])  // Use default background
        }
        holder.icon.setImageResource(iconList[position])  // Use default background

        holder.tv.text = textList[position]
        holder.itemView.setOnClickListener {
            selectedPositions= holder.itemView.tag as Int
            // Notify adapter to refresh the UI
            Log.e("selectedposition",""+textList[selectedPositions])
            val intent = Intent("selectedCount")
            intent.putExtra("count", "1")
            intent.putExtra("selectedValue", textList[selectedPositions])

            activity!!.sendBroadcast(intent)
            notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int {
        return imageList.size

    }

}
