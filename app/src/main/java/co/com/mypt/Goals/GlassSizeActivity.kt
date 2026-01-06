package co.com.mypt.Goals

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.R
import co.com.mypt.onBoarding.weightClass.FadeEdgeDecoration
import co.com.mypt.onBoarding.weightClass.Screen
import co.com.mypt.waterGlass.GlassShapeView
import co.com.mypt.waterGlass.WaterLabelAdapter
import co.com.mypt.waterGlass.WaterRepo
import co.com.mypt.waterGlass.WaterScaleSliderLayoutManager
import com.android.volley.VolleyError
import org.json.JSONObject

class GlassSizeActivity : AppCompatActivity(), WaterScaleSliderLayoutManager.MovementListener {

    lateinit var water: TextView
    lateinit var tvSave: TextView
    lateinit var headerLayout: LinearLayout
    lateinit var repo: WaterRepo
    lateinit var water_recycler_view: RecyclerView
    lateinit var glassShapeView: GlassShapeView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.glass_size_activity)
        repo = WaterRepo()
        water_recycler_view = findViewById(R.id.water_recycler_view)
        water = findViewById(R.id.water)
        tvSave = findViewById(R.id.tvSave)
        headerLayout = findViewById(R.id.headerLayout)

        glassShapeView = findViewById(R.id.glassShapeView)
        headerLayout.setOnClickListener{
            finish()
        }
        repo.setStartLabel(intent.getStringExtra("glassSize")!!.toInt())
        val percentage = (repo.getStartLabel() / 1000f) * 100
        glassShapeView.setFillPercentage(percentage)
        val padding = Screen.getScreenWidth(this) / 2
        water_recycler_view.setPadding(padding, 0, padding, 0)

        val adapter = WaterLabelAdapter(this)
        adapter.setData(repo.getWaterLabels())
        Log.e("water",""+repo.getWaterLabels().size)
        // Set the initial data based on the default unit
        val layoutManager = WaterScaleSliderLayoutManager(this, this,repo.getWaterLabels())
        water_recycler_view.layoutManager = layoutManager
        water_recycler_view.itemAnimator = DefaultItemAnimator()
        water_recycler_view.adapter = adapter

        water_recycler_view.scrollToPosition(repo.getWaterLabels().indexOf(intent.getStringExtra("glassSize")))

        water_recycler_view.addItemDecoration(FadeEdgeDecoration(this))  // Scroll to the start weight

        // Update the weight label text with the selected unit
        //val weightLabel = repo.getWaterLabels()[repo.getStartLabel()]
        val weightLabel = intent.getStringExtra("glassSize")
        val htmlString = "<big><b>$weightLabel</b></big><small><font color=#959595>ml</font></small>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        water.text = spanned

        tvSave.setOnClickListener{
            getSaveData()
        }
    }

    override fun onItemSelected(selectedIndex: Int) {

    }

    override fun onItemChanged(selectedIndex: String) {
        val number = selectedIndex.toFloat()
        val percentage = (number / 1000f) * 100
        glassShapeView.setFillPercentage(percentage)

        val htmlString = "<big><b>$selectedIndex</b></big><small><font color=#959595>ml</font></small>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        water.text = spanned
    }
    private fun getSaveData() {
        val param: MutableMap<String, String> = HashMap()
        param["volume"] =""+water.text.toString().replace("ml","")
        param["type"] = ""

        Log.e("glassVolumeParam", param.toString())

        PostMethod(ApiURL.glassvolume,param, applicationContext).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {

                try {
                    Log.e("glassVolumeRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        finish()
                    }else{
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error!!.printStackTrace()
            }

        })
    }
}