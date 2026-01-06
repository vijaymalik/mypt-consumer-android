package co.com.curved

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R

class ArcCalendarAdapter(
    private var items: ArrayList<String>,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<ArcCalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        textView.textSize = 20f
        textView.setTextColor(Color.GRAY)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val layoutParams = RecyclerView.LayoutParams(
            (parent.width / 5).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = layoutParams

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.bindingAdapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        holder.textView.text = items[position]

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val centerPosition = (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2

        val recyclerCenterX = recyclerView.width / 2f
        val itemCenterX = holder.itemView.x + (holder.itemView.width / 2f)
        val context= holder.itemView.context
        if (position == centerPosition) {
            // **✅ Keep Center Text Straight & Bold**
            holder.textView.rotation = 0f
            holder.textView.setTextColor(Color.parseColor("#FAFAFA"))
            val typeface = ResourcesCompat.getFont(context, R.font.funnel_sans_bold)
            holder.textView.typeface = typeface
            holder.textView.translationY = 0f
        } else {
            // **✅ Rotate Side Texts Smoothly**
            /*val normalizedDistance = (distanceFromCenter / recyclerCenterX).coerceIn(-1f, 1f)
            val angle = normalizedDistance * maxRotation
            val curveY = radius - sqrt(radius * radius - distanceFromCenter * distanceFromCenter)

            holder.textView.rotation = angle // Rotate only side texts
            holder.textView.translationY = curveY*/ // Adjust Y position for curve
            holder.textView.setTextColor(Color.parseColor("#606060"))
            val typeface = ResourcesCompat.getFont(context, R.font.funnel_sans_medium)
            holder.textView.typeface = typeface
        }
    }

    override fun getItemCount(): Int = items.size
    fun updateList(newList: ArrayList<String>) {
        val diffCallback = MyDiffUtil(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Update the list with new data
        items.clear()
        items.addAll(newList)

        // Dispatch updates
        diffResult.dispatchUpdatesTo(this)
    }
}
