package co.com.mypt.adapter

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

class WorkingOutAdapter(
    private val activity: FragmentActivity?,
    private val imageList: List<Int>,
    private val textList: List<String>,
    private val selectedImageList: List<Int>
) : RecyclerView.Adapter<WorkingOutAdapter.WorkingHolder>() {

    private var selectedPosition = -1
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(activity!!)

    init {
        setHasStableIds(true) // ✅ Helps RecyclerView manage views better
    }

    inner class WorkingHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = itemView.findViewById(R.id.tv)
        val relative: RelativeLayout = itemView.findViewById(R.id.relative)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val selectedImage: ImageView = itemView.findViewById(R.id.selectedImage)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong() // ✅ Provide stable unique ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkingHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.prefer_list, parent, false)
        return WorkingHolder(view)
    }

    override fun onBindViewHolder(holder: WorkingHolder, position: Int) {
        bindFull(holder, position)
    }

    override fun onBindViewHolder(holder: WorkingHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            // ✅ Partial bind — update only visibility, no Glide calls
            if (selectedPosition == position) {
                holder.selectedImage.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
            } else {
                holder.selectedImage.visibility = View.GONE
                holder.imageView.visibility = View.VISIBLE
            }
        } else {
            // fallback to full binding
            bindFull(holder, position)
        }
    }

    private fun bindFull(holder: WorkingHolder, position: Int) {
        holder.tv.visibility = View.GONE
        holder.imageView.tag = position

        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(10,0,10,0)
        holder.imageView.layoutParams = layoutParams

        val layoutParams1 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams1.setMargins(10,0,10,0)
        holder.selectedImage.layoutParams = layoutParams1
        // ✅ Load only once
        holder.imageView.setImageResource(imageList[position])
        holder.selectedImage.setImageResource(selectedImageList[position])

        // Selection logic
        if (selectedPosition == position) {
            holder.selectedImage.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE
        } else {
            holder.selectedImage.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE
        }

        holder.tv.text = textList[position]

        holder.imageView.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = position

            // ✅ Partial update to avoid flickering
            notifyItemChanged(previous, "payload")
            notifyItemChanged(selectedPosition, "payload")

            Log.e("selectedPosition", "$selectedPosition")

            val intent = Intent("selectedCountworking")
            intent.putExtra("count", "1")
            intent.putExtra("selectedPosition", selectedPosition)
            sharedPreferences.edit {
                putInt("selectedPackageType", selectedPosition + 1)
            }
            activity?.sendBroadcast(intent)
        }
    }

    override fun getItemCount(): Int = imageList.size
}
