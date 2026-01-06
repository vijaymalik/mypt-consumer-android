package co.com.mypt.WorkoutLibrary

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.R
import co.com.mypt.utils.Axis
import co.com.mypt.utils.RadarView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import java.util.Locale


class SpiderWebSessionSummaryActivity : AppCompatActivity() {

    lateinit var  chart: RadarChart
    lateinit var  radarchart: RadarView
    lateinit var tvScore: TextView
    lateinit var tv_complete: TextView
    lateinit var headerLayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spider_web_session_summary)
        chart = findViewById<RadarChart>(R.id.chart1)
        radarchart = findViewById(R.id.radarchart)
        tv_complete=findViewById(R.id.tv_complete)
        tvScore=findViewById(R.id.tvScore)
        headerLayout=findViewById(R.id.headerLayout)
        headerLayout.setOnClickListener {
            finish()
        }

        radarchart.setAxes(
            listOf(
                Axis("Calories", intent.getStringExtra("calories")!!.toFloat(), 0f, 500f),
                Axis("Steps", intent.getStringExtra("steps")!!.toFloat(), 0f,10000f ),
                Axis("Routine\nadherence ", intent.getStringExtra("routine")!!.toFloat(), 0f,200f ),
                Axis("Exercises Completed", 100F, 0f,20f ),
                Axis("Duration", intent.getStringExtra("duration_score")!!.toFloat(), 0f, 3600f),
                Axis("Heart Zone", intent.getStringExtra("heart_zone")!!.toFloat(), 0f, 100f),
            )
        )
        var scorevalue= intent.getStringExtra("ptScore")!!.toFloat()
        tvScore.text =String.format(Locale.getDefault(), "%.02f",scorevalue)
        tv_complete.setOnClickListener{
            var intent1= Intent(this,CompletedSessionSummaryActivity::class.java)
            intent1.putExtra("session_id",intent.getStringExtra("session_id"))
            intent1.putExtra("minute_duration",intent.getStringExtra("minute_duration"))
            intent1.putExtra("calories",intent.getStringExtra("calories"))
            intent1.putExtra("heart_zone",intent.getStringExtra("heart_zone"))
            startActivity(intent1)
        }
       // chart.setBackgroundColor(Color.rgb(60, 65, 82))
        textShader(tvScore)

        chart.description.isEnabled = false

        chart.webLineWidth = 1f
        chart.webColor = Color.LTGRAY
        chart.webLineWidthInner = 1f
        chart.webColorInner = Color.LTGRAY
        chart.webAlpha = 100
1
       /* val mv: MarkerView = RadarMarkerView(this, R.layout.radar_markerview)
        mv.chartView = chart // For bounds control
        chart.marker = mv // Set the marker to the chart
*/
        setData()

        chart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        val xAxis = chart.xAxis
        //xAxis.typeface = tfLight
        xAxis.textSize = 9f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f

        val mActivities = arrayOf("Calories", "Steps", "Routine adherence","Exercises Completed", "Duration", "Heart zone")

        xAxis.valueFormatter = IndexAxisValueFormatter(mActivities)
        xAxis.textColor = Color.WHITE

        val yAxis = chart.yAxis
        //yAxis.typeface = tfLight
        yAxis.setLabelCount(5, false)
        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 80f
        yAxis.setDrawLabels(false)

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
      //  l.typeface = tfLight
        l.xEntrySpace = 7f
        l.yEntrySpace = 5f
        l.textColor = Color.WHITE

    }
    private fun setData() {
        val mul = 80f
        val min = 20f
        val cnt = 5

        val entries1 = ArrayList<RadarEntry>()
        val entries2 = ArrayList<RadarEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until cnt) {
            val val1 = (Math.random() * mul).toFloat() + min
            Log.e("val1",""+val1)
            entries1.add(RadarEntry(val1))

            val val2 = (Math.random() * mul).toFloat() + min
            Log.e("val2",""+val2)

            entries2.add(RadarEntry(val2))
        }
        entries1.add(RadarEntry(intent.getStringExtra("calories")!!.toFloat()))
        entries1.add(RadarEntry(intent.getStringExtra("steps")!!.toFloat()))
        entries1.add(RadarEntry(intent.getStringExtra("routine")!!.toFloat()))
        entries1.add(RadarEntry(intent.getStringExtra("totalExercise")!!.toFloat()))
        entries1.add(RadarEntry(intent.getStringExtra("sec_duration")!!.toFloat()))
        entries1.add(RadarEntry(intent.getStringExtra("heart_zone")!!.toFloat()))


        val set1 = RadarDataSet(entries1, "Last Week")
        set1.color = Color.rgb(103, 110, 129)
        set1.fillColor = Color.rgb(103, 110, 129)
        set1.setDrawFilled(true)
        set1.fillAlpha = 180
        set1.lineWidth = 2f
        set1.isDrawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)



        val sets = ArrayList<IRadarDataSet>()
        sets.add(set1)

        val data = RadarData(sets)
     //   data.setValueTypeface(tfLight)
        data.setValueTextSize(8f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.WHITE)

        chart.data = data
        chart.invalidate()
    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#9EBCFF"),
                Color.parseColor("#FAFAFA")
            ), null, Shader.TileMode.MIRROR
        )
        tv.paint.shader = textShader
    }

}