package co.com.mypt.Goals

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import co.com.mypt.R
import co.com.mypt.utils.GradientDonutChartView
import co.com.mypt.utils.RoundedBarChartRenderer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class SleepTrackingActivity : AppCompatActivity() {

    val arcList = mutableListOf<Triple<Int, Int, Float>>()
    lateinit var day : TextView
    lateinit var month : TextView
    lateinit var week : TextView
    lateinit var sleepStatus : TextView
    lateinit var deepSleepTime : TextView
    lateinit var lightSleepTime : TextView
    lateinit var remSleepTime : TextView
    lateinit var awakeSleepTime : TextView
    lateinit var avgSleepTime : TextView

    lateinit var deepSleepInfo : ImageView
    lateinit var lightSleepInfo : ImageView
    lateinit var remSleepInfo : ImageView
    lateinit var awakeInfo : ImageView

    lateinit var deepSleepInfoLayout : LinearLayout

    lateinit var donutChart : GradientDonutChartView
    lateinit var barChart : BarChart
    lateinit var barChartMonthly : BarChart

    lateinit var donutLayout : RelativeLayout
    lateinit var stackBarChartLayout : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.sleep_track_activity)

        deepSleepInfoLayout = findViewById(R.id.deepSleepInfoLayout)
        deepSleepInfo = findViewById(R.id.deepSleepInfo)
        lightSleepInfo = findViewById(R.id.lightSleepInfo)
        remSleepInfo = findViewById(R.id.remSleepInfo)
        awakeInfo = findViewById(R.id.awakeInfo)
        stackBarChartLayout = findViewById(R.id.stackBarChartLayout)
        donutLayout = findViewById(R.id.donutLayout)
        avgSleepTime = findViewById(R.id.avgSleepTime)
        lightSleepTime = findViewById(R.id.lightSleepTime)
        remSleepTime = findViewById(R.id.remSleepTime)
        deepSleepTime = findViewById(R.id.deepSleepTime)
        awakeSleepTime = findViewById(R.id.awakeSleepTime)
        sleepStatus = findViewById(R.id.sleepStatus)
        day = findViewById(R.id.day)
        month = findViewById(R.id.month)
        week = findViewById(R.id.week)
        barChart = findViewById(R.id.barChart)
        barChartMonthly = findViewById(R.id.barChartMonthly)

        donutChart = findViewById(R.id.donutChart)

        day.setOnClickListener {
            day.setTextColor(resources.getColor(R.color.black,null))
            month.setTextColor(resources.getColor(R.color.headingcolor,null))
            week.setTextColor(resources.getColor(R.color.headingcolor,null))

            day.background = resources.getDrawable(R.drawable.white_rectangle,null)
            month.background = null
            week.background = null

            donutLayout.visibility = View.VISIBLE
            stackBarChartLayout.visibility = View.GONE
        }

        month.setOnClickListener {
            day.setTextColor(resources.getColor(R.color.headingcolor,null))
            month.setTextColor(resources.getColor(R.color.black,null))
            week.setTextColor(resources.getColor(R.color.headingcolor,null))

            day.background = null
            month.background = resources.getDrawable(R.drawable.white_rectangle,null)
            week.background = null

            donutLayout.visibility = View.GONE
            stackBarChartLayout.visibility = View.VISIBLE
            barChart.visibility = View.GONE
            barChartMonthly.visibility = View.VISIBLE
            barChartMonthly.animateY(1000)
        }

        week.setOnClickListener {
            day.setTextColor(resources.getColor(R.color.headingcolor,null))
            month.setTextColor(resources.getColor(R.color.headingcolor,null))
            week.setTextColor(resources.getColor(R.color.black,null))

            day.background = null
            month.background = null
            week.background = resources.getDrawable(R.drawable.white_rectangle,null)

            donutLayout.visibility = View.GONE
            stackBarChartLayout.visibility = View.VISIBLE
            barChartMonthly.visibility = View.GONE
            barChart.visibility = View.VISIBLE
            barChart.animateY(1000)
        }

        deepSleepInfo.setOnClickListener {
            if(deepSleepInfoLayout.isVisible)
                deepSleepInfoLayout.visibility = View.GONE
            else
                deepSleepInfoLayout.visibility = View.VISIBLE
        }
        
        deepSleepTime.text = getStyledTimeString( "49 min")
        awakeSleepTime.text = getStyledTimeString( "5 h  11 min")
        lightSleepTime.text = getStyledTimeString( "12 h  59 min")
        remSleepTime.text = getStyledTimeString("11 min")
        avgSleepTime.text = getStyledTimeString("7 h  24 min")

        textShader4Color(sleepStatus)
        textShader(deepSleepTime)
        textShader(awakeSleepTime)
        textShader(lightSleepTime)
        textShader(remSleepTime)
        textShader(avgSleepTime)

        setMonthBarChart()
        setWeekBarChart()
        setDonutChart()
    }

    private fun setMonthBarChart() {
        val sleepData = listOf(
            floatArrayOf(5f, 3f, 1f, 2f), // Mon: Deep, Light, Awake, Remaining
            floatArrayOf(1.5f, 5f, 1f, 1.5f),
            floatArrayOf(2.2f, 2.5f,5f, 2.5f),
            floatArrayOf(3f, 2f, 1.5f, 5f),
            floatArrayOf(2.5f, 3f, 5f, 1.5f),
            floatArrayOf(1f, 5f, 2f, 1.5f),
            floatArrayOf(5f, 2.5f, 1f, 2f),
            floatArrayOf(5f, 3f, 1f, 2f), // Mon: Deep, Light, Awake, Remaining
            floatArrayOf(1.5f, 5f, 1f, 1.5f),
            floatArrayOf(2.2f, 2.5f,5f, 2.5f),
            floatArrayOf(3f, 2f, 1.5f, 5f),
            floatArrayOf(3f, 2f, 1.5f, 5f),
        )
        val entries = sleepData.mapIndexed { index, values ->
            BarEntry(index.toFloat(), values)
        }

        val dataSet = BarDataSet(entries, "Stacked Data").apply {
            setColors(
                resources.getColor(R.color.deepSleep,null), // Orange
                resources.getColor(R.color.lightSleep,null), // Green
                resources.getColor(R.color.remSleep,null), // Green
                resources.getColor(R.color.awake,null), // Green
            )
            setDrawValues(false)
            stackLabels = arrayOf("Deep Sleep", "Light Sleep", "Rem", "Awake")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        barChartMonthly.data = BarData(dataSet)
        barChartMonthly.data.barWidth = 0.8f
        barChartMonthly.renderer = RoundedBarChartRenderer(barChartMonthly, barChartMonthly.animator, barChartMonthly.viewPortHandler)

        barChartMonthly.axisLeft.axisMinimum = 0f
        barChartMonthly.axisLeft.granularity = 4f
        barChartMonthly.axisLeft.textColor = resources.getColor(R.color.edittextbordercolor,null)

        barChartMonthly.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if(value.toInt() < 10) {
                    "0${value.toInt()}:00"
                }else
                    "${value.toInt()}:00"
            }
        }

        //val labels = listOf("Jan", "Feb", "Mar","Apr", "May", "June", "Jul")
        barChartMonthly.xAxis.apply {
            valueFormatter = MonthValueFormatter()
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            textColor = resources.getColor(R.color.edittextbordercolor,null)
        }

        barChartMonthly.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                barChartMonthly.highlightValue(null)
                barChartMonthly.marker = null
            }

            override fun onNothingSelected() {
                barChartMonthly.highlightValue(null)
                barChartMonthly.marker = null
            }
        })

        barChartMonthly.axisRight.isEnabled = false
        barChartMonthly.description.isEnabled = false
        barChartMonthly.legend.isEnabled = false
        //barChartMonthly.setFitBars(true)
        barChartMonthly.animateY(1000)
        barChartMonthly.invalidate()

    }

    private fun setWeekBarChart() {

        val sleepData = listOf(
            floatArrayOf(5f, 3f, 1f, 2f), // Mon: Deep, Light, Awake, Remaining
            floatArrayOf(1.5f, 5f, 1f, 1.5f),
            floatArrayOf(2.2f, 2.5f,5f, 2.5f),
            floatArrayOf(3f, 2f, 1.5f, 5f),
            floatArrayOf(2.5f, 3f, 5f, 1.5f),
            floatArrayOf(1f, 5f, 2f, 1.5f),
            floatArrayOf(5f, 2.5f, 1f, 2f)
        )
        val entries = sleepData.mapIndexed { index, values ->
            BarEntry(index.toFloat(), values)
        }

        val dataSet = BarDataSet(entries, "Stacked Data").apply {
            setColors(
                resources.getColor(R.color.deepSleep,null), // Orange
                resources.getColor(R.color.lightSleep,null), // Green
                resources.getColor(R.color.remSleep,null), // Green
                resources.getColor(R.color.awake,null), // Green
            )
            setDrawValues(false)
            stackLabels = arrayOf("Deep Sleep", "Light Sleep", "Rem", "Awake")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        barChart.data = BarData(dataSet)
        barChart.data.barWidth = 0.8f
        barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.granularity = 4f
        barChart.axisLeft.textColor = resources.getColor(R.color.edittextbordercolor,null)

        barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if(value.toInt() < 10) {
                    "0${value.toInt()}:00"
                }else
                    "${value.toInt()}:00"
            }
        }

        val labels = listOf("M", "T", "W","T", "F", "S", "S")
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            textColor = resources.getColor(R.color.edittextbordercolor,null)
        }

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                barChart.highlightValue(null)
                barChart.marker = null
            }

            override fun onNothingSelected() {
                barChart.highlightValue(null)
                barChart.marker = null
            }
        })

        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun getStyledTimeString(time: String): SpannableString {
        val spannable = SpannableString(time)

        val regex = Regex("\\d+")
        val matches = regex.findAll(time)

        for (match in matches) {
            spannable.setSpan(
                RelativeSizeSpan(2.3f), // 1.5x larger size for numbers
                match.range.first,
                match.range.last + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
    private fun textShader4Color(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#C1D0B2"),
                Color.parseColor("#CACACA"),
                Color.parseColor("#FAFAFA"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
    private fun setDonutChart() {
        val apiResponse = listOf(
            mapOf("duration" to 12, "startColor" to "#6442AB", "endColor" to "#260F57"),
            mapOf("duration" to 4, "startColor" to "#7FC9CC", "endColor" to "#406566"),
            mapOf("duration" to 2, "startColor" to "#646B42", "endColor" to "#C4D181"),
            mapOf("duration" to 6, "startColor" to "#E2BCA0", "endColor" to "#6B5342")
        )

        var total = 0
        for (item in apiResponse) {
            total += item["duration"] as Int
        }

        for (item in apiResponse) {
            val duration = item["duration"] as Int
            val startColor = item["startColor"] as String
            val endColor = item["endColor"] as String

            val angle = (duration.toFloat() / total) * 360f

            val startColorInt = Color.parseColor(startColor)
            val endColorInt = Color.parseColor(endColor)

            arcList.add(Triple(startColorInt, endColorInt, angle))
        }

        donutChart.setArcData(arcList)
    }
}

class MonthValueFormatter : ValueFormatter() {
    private val monthLabels = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in 0..11) monthLabels[index] else ""
    }
}