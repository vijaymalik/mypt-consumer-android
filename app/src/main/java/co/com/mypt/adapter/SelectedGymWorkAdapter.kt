package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.GoalsAdapter.GoalsHolder

class SelectedGymWorkAdapter(var context: Context, var imageList: List<Int>, var textList: List<String>, var selectedImageList: List<Int>) : RecyclerView.Adapter<SelectedGymWorkAdapter.SelectedGymWorkAdapterHolder>() {
    var selectedPositions = -1
    class SelectedGymWorkAdapterHolder (view: View):RecyclerView.ViewHolder(view){
        val tv: TextView = itemView.findViewById(R.id.tv)
        val relative: RelativeLayout = itemView.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedGymWorkAdapterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_gym_trainer, parent, false)
        return SelectedGymWorkAdapterHolder(view)
    }

    override fun getItemCount(): Int {
        return selectedImageList.size
    }

    override fun onBindViewHolder(holder: SelectedGymWorkAdapterHolder, position: Int) {
        holder.itemView.setTag(position)
        if (selectedPositions==position) {
            holder.relative.setBackgroundResource(selectedImageList[position])  // Use selected background
        } else {
            holder.relative.setBackgroundResource(imageList[position])  // Use default background
        }
        holder.tv.setText(textList[position])
        holder.itemView.setOnClickListener {
            selectedPositions= holder.itemView.getTag() as Int
            // Notify adapter to refresh the UI
            Log.e("selectedposition",""+selectedPositions);
            val intent = Intent("withandWithoutTrainer")
            intent.putExtra("position", position)
            Log.e("position",""+position)
            context!!.sendBroadcast(intent)
            notifyDataSetChanged()
        }
    }

}
