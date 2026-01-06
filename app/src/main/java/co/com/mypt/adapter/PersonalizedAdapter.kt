package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.PreferencesModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PersonalizedAdapter(
    var applicationContext: Context?,
    var preferencesList: ArrayList<PreferencesModel>,

    ) : RecyclerView.Adapter<PersonalizedAdapter.PersonalizeHolder>() {
    private val selectedPositions = ArrayList<Int>()
    private val selectedIds = ArrayList<Int>()

    class PersonalizeHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        val tv: TextView = itemView.findViewById(R.id.tv)
        val relative: RelativeLayout = itemView.findViewById(R.id.relative)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val selectedImageView: ImageView = itemView.findViewById(R.id.selectedImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalizeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.personalized_layout, parent, false)
        return PersonalizeHolder(view)
    }

    override fun getItemCount(): Int {
        return preferencesList.size
    }

    override fun onBindViewHolder(holder: PersonalizeHolder, position: Int) {
        val model = preferencesList[position]
        Glide.with(applicationContext!!).load(model.selectedImage).preload()
        Glide.with(applicationContext!!).load(model.selectedImage).preload()

        Glide.with(applicationContext!!)
            .load(model.selectedImage)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.selectedImageView)

        Glide.with(applicationContext!!)
            .load(model.unselectedImage)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        val isSelected = selectedPositions.contains(position)
        holder.selectedImageView.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.imageView.visibility = if (isSelected) View.GONE else View.VISIBLE

        holder.tv.text = model.name
        holder.itemView.tag = position
        holder.itemView.setOnClickListener {

            val pos = it.tag as Int
            if (selectedPositions.contains(pos)) {
                // If item is already selected, unselect it
                selectedPositions.remove(pos)
                selectedIds.remove(preferencesList[pos].id.toInt())
            } else {
                // If item is not selected, select it
                selectedPositions.add(pos)
                selectedIds.add(preferencesList[pos].id.toInt())
            }
            // Notify adapter to refresh the UI
            Log.e("selectedposition",""+selectedPositions)

            val intent = Intent("selectedCount")
            if(selectedPositions.isNotEmpty()){
                intent.putExtra("count", "1")
            }else{
                intent.putExtra("count", "0")
            }
            intent.putIntegerArrayListExtra("selectedPositions",selectedIds)
            applicationContext!!.sendBroadcast(intent)

            notifyItemChanged(pos)
        }
    }
}
