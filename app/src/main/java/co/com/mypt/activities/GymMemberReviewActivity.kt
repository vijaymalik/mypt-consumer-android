package co.com.mypt.activities

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.adapter.CertificateAdapter
import co.com.mypt.adapter.GalleryAdapter
import co.com.mypt.adapter.MembersListAdapter
import co.com.mypt.adapter.ReviewActivityAdapter
import co.com.mypt.adapter.SpecialitiesAdapter
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.CertificateModel
import co.com.mypt.model.GalleryModel
import co.com.mypt.model.JoinModel
import co.com.mypt.model.SpecialitiesModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class GymMemberReviewActivity : AppCompatActivity() {
    lateinit var tvTrainer_name:TextView
    lateinit var recycler:RecyclerView
    lateinit var im_star:ImageView
    lateinit var imEditgym:ImageView
    lateinit var imTrainer:ImageView
    lateinit var avgRating:TextView
    lateinit var totalRatings:TextView
    lateinit var tvPackageDetail:TextView
    lateinit var tvStartDate:TextView
    lateinit var tvEndDate:TextView
    lateinit var tvTotalDuration:TextView
    lateinit var tvPrice:TextView
    lateinit var tvPayment:TextView
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    lateinit var linearpay: LinearLayout
    var tax_rate=""
    var main_price=""
    var total_price=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_member_review)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        recycler=findViewById(R.id.recycler)
        im_star=findViewById(R.id.im_star)
        avgRating=findViewById(R.id.avgRating)
        totalRatings=findViewById(R.id.totalRatings)
        imTrainer=findViewById(R.id.imTrainer)
        tvPackageDetail=findViewById(R.id.tvPackageDetail)
        tvEndDate=findViewById(R.id.tvEndDate)
        tvStartDate=findViewById(R.id.tvStartDate)
        tvPrice=findViewById(R.id.tvPrice)
        tvTotalDuration=findViewById(R.id.tvTotalDuration)
        imEditgym=findViewById(R.id.imEditgym)
        tvPayment=findViewById(R.id.tvPayment)
        linearpay=findViewById(R.id.linearpay)
        tvPrice.setText(intent.getStringExtra("price"))
        imEditgym.setOnClickListener{
            val intent = Intent(this, GymListActivity::class.java)
            intent.putExtra("type",getIntent().getStringExtra("type"))
            startActivity(intent)
        }
        tvPayment.setOnClickListener{
            // edit.remove("typeWorkout").apply()
            val intent = Intent(this, GymMemberPaymentScreenActivity2::class.java)
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("days",getIntent().getStringExtra("days"))
            intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
            intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
            intent.putExtra("tax_rate",tax_rate)
            intent.putExtra("main_price",main_price)
            Log.e("mainprice",""+main_price)
            intent.putExtra("total_price",total_price)
            startActivity(intent)
          /*  val intent = Intent(this, BookMemberShipActivity::class.java)
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("days",getIntent().getStringExtra("days"))
            intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
            intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
            startActivity(intent)*/

        }

    }
    override fun onResume() {
        super.onResume()
        getData()
    }
    private fun getData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api=ApiURL.review_package+intent.getStringExtra("price")+"&start_date="+intent.getStringExtra("apistart_date")+"&end_date="+intent.getStringExtra("apiend_date")+
                "&days="+intent.getStringExtra("days")+"&studio_id="+intent.getStringExtra("studio_id")

        Log.e("GymMemberReviewApi",""+api)

        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("ReviewPackageResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonData=jsonObj.optJSONObject("data")
                        tvTrainer_name.setText(jsonData.optJSONObject("studio").optString("name"))
                        tax_rate=jsonData.optString("tax_amount")
                        main_price=jsonData.optString("main_price")
                        total_price=jsonData.optString("price")
                   /*     if (jsonData.optString("averageRating").equals("")){
                            avgRating.setText("0")

                        }else{
                            avgRating.setText(jsonData.optString("averageRating"))

                        }*/

                        totalRatings.setText(jsonData.optJSONObject("studio").optString("total_rating")+" k ratings")
                        avgRating.setText(jsonData.optJSONObject("studio").optString("avg_rating"))
                        Glide.with(applicationContext).load(jsonData.optJSONObject("studio").optString("profile")).fitCenter().into(imTrainer)

                        tvPackageDetail.text = jsonData.optJSONObject("packageDetail").optString("package")
                        tvStartDate.text = jsonData.optJSONObject("packageDetail").optString("start_date")
                        tvEndDate.text = jsonData.optJSONObject("packageDetail").optString("end_date")
                        tvTotalDuration.text =jsonData.optJSONObject("packageDetail").optString("total_duration")

                        for(i in 0 until jsonData.optJSONObject("studio").optJSONArray("tags").length())
                        {
                            var activityModel=ActivityModel()
                            activityModel.name=jsonData.optJSONObject("studio").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter(applicationContext, activitiesModelList)
                        recycler.adapter = activityAdapter

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