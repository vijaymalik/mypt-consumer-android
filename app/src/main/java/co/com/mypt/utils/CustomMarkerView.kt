package co.com.mypt.utils

import android.content.Context
import android.widget.TextView
import co.com.mypt.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent) // Replace tvContent with the ID of your TextView in the marker layout

    override fun refreshContent(e: Entry, highlight: Highlight) {
        tvContent.text = "${e.y}"
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height+25).toFloat())
    }
}