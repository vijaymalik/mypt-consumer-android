package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.GoalsModel
import com.bumptech.glide.Glide

class GoalsAdapter(
    var activity: FragmentActivity?,
    var goalsArrayList: ArrayList<GoalsModel>,
) : RecyclerView.Adapter<GoalsAdapter.GoalsHolder>() {
    private val selectedPositions = mutableSetOf<Int>()
    private val selectedIds = ArrayList<Int>()

    class GoalsHolder (view: View):RecyclerView.ViewHolder(view){
//        val tv: TextView = itemView.findViewById(R.id.tv)
        val relative: RelativeLayout = itemView.findViewById(R.id.relative)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val selectedImageView: ImageView = itemView.findViewById(R.id.selectedImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.personalized_layout, parent, false)
        return GoalsHolder(view)
    }

    override fun onBindViewHolder(holder: GoalsHolder, position: Int) {
        val model = goalsArrayList[position]

        Glide.with(activity!!).load(model.selectedImage).preload()
        Glide.with(activity!!).load(model.selectedImage).preload()

        Glide.with(activity!!).load(model.selectedImage).fitCenter().into(holder.selectedImageView)
        Glide.with(activity!!).load(model.unselectedImage).fitCenter().into(holder.imageView)

        val isSelected = selectedPositions.contains(position)
        holder.selectedImageView.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.imageView.visibility = if (isSelected) View.GONE else View.VISIBLE

//        holder.tv.text = model.name

        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val pos = it.tag as Int
            if (selectedPositions.contains(pos)) {
                // If item is already selected, unselect it
                selectedPositions.remove(pos)
                selectedIds.remove(goalsArrayList[pos].id.toInt())
            }
            else {
                // If item is not selected, select it
                selectedPositions.add(pos)
                selectedIds.add(goalsArrayList[pos].id.toInt())
            }

            val intent = Intent("selectedCount")
            if(selectedPositions.isNotEmpty()){
                intent.putExtra("count", "1")
            }
            else{
                intent.putExtra("count", "0")
            }
            intent.putIntegerArrayListExtra("selectedGoals",selectedIds)
            activity?.sendBroadcast(intent)

            notifyItemChanged(pos)
        }

    }

    override fun getItemCount(): Int {
        return goalsArrayList.size

    }

}
