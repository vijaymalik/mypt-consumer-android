package co.com.mypt.UpComingClasses

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.AllResourceAdapter
import co.com.mypt.adapter.GlimpseofOurCLassesAdapter
import co.com.mypt.adapter.LookbyCategoryAdapter
import co.com.mypt.adapter.NearUpcomingClassAdapter
import co.com.mypt.adapter.OtherResourceAdapter
import co.com.mypt.adapter.ResourceAdapter
import co.com.mypt.model.GlimpseCLassesModel
import co.com.mypt.model.LookbyCategoryModel
import co.com.mypt.model.NearUpcomingCLassModel
import co.com.mypt.model.OtherResourceModel
import co.com.mypt.model.ResourceModel
import com.android.volley.VolleyError
import org.json.JSONObject

class AllResourcesActivity : AppCompatActivity() {
    lateinit var linearHeader:LinearLayout
    lateinit var tvResources:TextView
    lateinit var tv:TextView
    lateinit var recyclerOtherResource:RecyclerView
    lateinit var recyclerResource:RecyclerView
    var resourceModelList :ArrayList<ResourceModel> = ArrayList()
    var otherresourceModelList :ArrayList<OtherResourceModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_resources)
        linearHeader=findViewById(R.id.linearHeader)
        tvResources=findViewById(R.id.tvResources)
        tv=findViewById(R.id.tv)
        recyclerOtherResource=findViewById(R.id.recyclerOtherResource)
        recyclerResource=findViewById(R.id.recyclerResource)
        linearHeader.setOnClickListener{
            finish()
        }

       /* for(i in 0 until 5){
          //  var jsonResource=jsonArrayResourcesVideos.optJSONObject(i)
            var otherResourceModel= OtherResourceModel()
            otherResourceModel.name="Stretching and Recovery Sessions"

            otherresourceModelList.add(otherResourceModel)
        }
        var otherresourceAdapter = OtherResourceAdapter(applicationContext, otherresourceModelList)
        recyclerOtherResource.adapter = otherresourceAdapter*/
        getAllResources()
    }

    private fun getAllResources() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.getresources

        Log.e("getResourcessUrl",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("AllResourcesResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArrayResources=jsonObj.optJSONArray("data")

                        if (jsonArrayResources.length()>0){
                            tv.visibility= View.GONE
                            recyclerResource.visibility= View.VISIBLE
                            for(i in 0 until jsonArrayResources.length()){
                                var json=jsonArrayResources.optJSONObject(i)
                                var resourceModel= ResourceModel()
                                resourceModel.title=json.optString("title")
                                resourceModel.description=json.optString("description")
                                resourceModel.date=json.optString("date")
                                resourceModel.reading_time=json.optString("reading_time")
                                resourceModel.image=json.optString("image")

                                resourceModelList.add(resourceModel)
                            }
                            var resourceAdapter = AllResourceAdapter(applicationContext, resourceModelList)
                            recyclerResource.adapter = resourceAdapter

                        }else{
                            tv.visibility= View.VISIBLE
                            recyclerResource.visibility= View.GONE
                        }





                    }else{

                    }


                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }
}