package co.com.mypt.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

class PreferenceAdapter(
    var activity: FragmentActivity?,
    var imageList: List<Int>,
    var textList: List<String>,
    var selectedImageList: List<Int>
) : RecyclerView.Adapter<PreferenceAdapter.PreferenceHolder>() {
    var selectedPositions = -1
    class PreferenceHolder(view: View):RecyclerView.ViewHolder(view) {


        val tv: TextView = itemView.findViewById(R.id.tv)
        val relative: RelativeLayout = itemView.findViewById(R.id.relative)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreferenceHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prefernce_list, parent, false)
        return PreferenceHolder(view)
    }

    override fun onBindViewHolder(holder: PreferenceHolder, position: Int) {
        if (selectedPositions==position) {
            holder.relative.setBackgroundResource(selectedImageList[position])  // Use selected background
        } else {
            holder.relative.setBackgroundResource(imageList[position])  // Use default background
        }
        holder.tv.text = textList[position]
        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            selectedPositions= holder.itemView.tag as Int
            // Notify adapter to refresh the UI
            Log.e("selectedposition",""+selectedPositions)

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
