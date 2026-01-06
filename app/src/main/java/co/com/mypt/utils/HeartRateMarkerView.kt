package co.com.mypt.utils



import android.content.Context
import android.widget.TextView
import co.com.mypt.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class HeartRateMarkerView(context: Context, layoutRes: Int) : MarkerView(context, layoutRes) {
    private val tvBpm: TextView = findViewById(R.id.tvBpm)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvBpm.text = "${e?.y?.toInt()} bpm"
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
