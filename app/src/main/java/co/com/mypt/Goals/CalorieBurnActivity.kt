package co.com.mypt.Goals

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter1
import co.com.mypt.adapter.CalorieBurnWeekAdapter
import co.com.mypt.calendarUtils.Event
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.CalorieBurnModel
import co.com.mypt.utils.CustomMarkerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils

class CalorieBurnActivity : AppCompatActivity() {
    lateinit var recylerWeek:RecyclerView
    lateinit var calorieBurnModel: CalorieBurnModel
    var calorieBurnArrayList :ArrayList<CalorieBurnModel> = ArrayList()
    lateinit var lineChart:LineChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_burn)
        recylerWeek=findViewById(R.id.recylerWeek)
        lineChart=findViewById(R.id.lineChart)
        for (i in 0..4) {
            calorieBurnModel= CalorieBurnModel()
            calorieBurnModel.date="31/09 - 06/10"
            calorieBurnArrayList.add(calorieBurnModel)
        }
        var calorieBurnWeekAdapter = CalorieBurnWeekAdapter(applicationContext, calorieBurnArrayList)
        recylerWeek.adapter = calorieBurnWeekAdapter



        //Part1
        val entries = ArrayList<Entry>()

//Part2
        entries.add(Entry(0f, 10f))
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))
        entries.add(Entry(4f, 20f))
        entries.add(Entry(5f, 16f))
        entries.add(Entry(6f, 16f))

//Part3
        val vl = LineDataSet(entries, "My Type")
        vl.color = resources.getColor(R.color.sea_color)
//Part4
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.white
        vl.fillAlpha = R.color.red
        vl.circleRadius = 5f
        vl.setDrawFilled(true)

        vl.fillColor = resources.getColor(R.color.sea_color)

//Part5
        lineChart.xAxis.labelRotationAngle = 0f
        lineChart.getAxisLeft().setDrawGridLines(false)
        lineChart.axisLeft.gridColor=getColor(R.color.buttongreycolor)

        val legend = lineChart.legend
        legend.isEnabled = false
        if (Utils.getSDKInt() >= 18) {
            val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.gradient_bg)
            vl.fillDrawable = drawable
            vl.setDrawFilled(true)
        } else {
            vl.fillColor = Color.parseColor("#00C1AA")
            vl.setDrawFilled(true) // Still need to enable filling even with a solid color
        }
//Part6
        lineChart.data = LineData(vl)

//Part7
        lineChart.axisRight.isEnabled = false
  //      lineChart.xAxis.axisMaximum = j+0.1f

//Part8
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

//Part9
        lineChart.description.text = "Days"
        lineChart.setNoDataText("No forex yet!")

//Part10
        lineChart.animateX(1800, Easing.EaseInExpo)

//Part11

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                // Show the marker when a value is selected
                val mv = CustomMarkerView(this@CalorieBurnActivity, R.layout.custom_bar_chart_marker_view) // Replace YourActivity with your activity's name
                mv.chartView = lineChart
                lineChart.marker = mv
                lineChart.highlightValue(h) // Highlight the selected value
            }

            override fun onNothingSelected() {
                // Hide the marker when nothing is selected
                lineChart.highlightValue(null)
                lineChart.marker = null
            }
        })

        val xAxis = lineChart.xAxis
        xAxis.textColor = Color.parseColor("#606060")
        xAxis.position = XAxis.XAxisPosition.TOP // Set the position of the X-axis
        xAxis.granularity = 1f // Optional: set to 1f to avoid displaying fractional values
        xAxis.valueFormatter = object : ValueFormatter() {
            private val labels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") // Your custom labels

            override fun getFormattedValue(value: Float): String {
                return if (value >= 0 && value < labels.size) {
                    labels[value.toInt()]
                } else {
                    "" // Or a default value for out-of-range indices
                }
            }
        }
        // Dashed Limit Line at 2200 Calories
        val limitLine = LimitLine(100f, "Calorie Limit").apply {
            lineWidth = 2f
            lineColor = Color.parseColor("#31343A")
            textColor = Color.parseColor("#606060")
            textSize = 12f
            enableDashedLine(10f, 5f, 0f)
        }
            lineChart.axisLeft.addLimitLine(limitLine)
    }
}