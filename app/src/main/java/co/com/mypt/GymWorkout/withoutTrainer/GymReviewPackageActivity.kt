package co.com.mypt.GymWorkout.withoutTrainer

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.BookingConfirmActivity
import co.com.mypt.activities.DuringSeesionActivity
import co.com.mypt.activities.MembersListForPackage
import co.com.mypt.activities.PaymentSelectionActivity
import co.com.mypt.activities.ReviewPackageActivity
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.adapter.ActivityAdapter1
import co.com.mypt.adapter.GymActivityAdapter
import co.com.mypt.adapter.MembersListAdapter
import co.com.mypt.adapter.ReviewActivityAdapter
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.GymActivityModel
import co.com.mypt.model.JoinModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class GymReviewPackageActivity : AppCompatActivity() {
    lateinit var tvPayment:TextView
    lateinit var imGym:ImageView

    lateinit var recycler:RecyclerView
    lateinit var linearslot: LinearLayout
    lateinit var linearPreference:LinearLayout
    lateinit var linearSession:LinearLayout
    lateinit var linearvalidity:LinearLayout
    lateinit var tvTrainer_name:TextView
    lateinit var imTrainer:ImageView
    lateinit var totalRatings:TextView


    lateinit var recycler1:RecyclerView
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    var gymmactivitiesModelList :ArrayList<GymActivityModel> = ArrayList()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    lateinit var im_star:ImageView
    lateinit var avgRating:TextView
    lateinit var tvTrainingPrefernce:TextView
    lateinit var tvPackageDetail:TextView
    lateinit var tvsession:TextView
    lateinit var tvLocation:TextView
    lateinit var tvStartDate:TextView
    lateinit var tvEndDate:TextView
    lateinit var tvGymName:TextView
    lateinit var tvPrice:TextView
    lateinit var tvTime:TextView
    lateinit var tvTotalDuration:TextView
    lateinit var tvdistance:TextView
    lateinit var tvValidity:TextView
    lateinit var nested: NestedScrollView
    lateinit var linearpay: LinearLayout
    lateinit var headerLayout: LinearLayout
    lateinit var imEditGym: ImageView
    lateinit var imEditTrainer: ImageView
    lateinit var im_star1: ImageView

    lateinit var editMembers:ImageView
    lateinit var membersListRecyclerView: RecyclerView
    var membersList = ArrayList<JoinModel>()
    lateinit var membersLayout:LinearLayout
    var tax_rate=""
    var main_price=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_review_package)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)
        edit=sharedPreferences.edit()

        editMembers=findViewById(R.id.editMembers)
        membersLayout=findViewById(R.id.membersLayout)
        membersListRecyclerView=findViewById(R.id.membersListRecyclerView)
        tvPayment=findViewById(R.id.tvPayment)
        linearSession=findViewById(R.id.linearSession)
        imEditGym=findViewById(R.id.imEditGym)
        recycler=findViewById(R.id.recycler)
        recycler1=findViewById(R.id.recycler1)
        linearslot=findViewById(R.id.linearslot)
        linearPreference=findViewById(R.id.linearPreference)
        linearvalidity=findViewById(R.id.linearvalidity)
        imTrainer=findViewById(R.id.imTrainer)
        totalRatings=findViewById(R.id.totalRatings)
        im_star=findViewById(R.id.im_star)
        avgRating=findViewById(R.id.avgRating)
        nested=findViewById(R.id.nested)
        tvTrainingPrefernce=findViewById(R.id.tvTrainingPrefernce)
        tvTime=findViewById(R.id.tvTime)
        linearpay=findViewById(R.id.linearpay)
        imEditTrainer=findViewById(R.id.imEditTrainer)
        tvGymName=findViewById(R.id.tvGymName)
        im_star1=findViewById(R.id.im_star1)
        tvdistance=findViewById(R.id.tvdistance)
        tvLocation=findViewById(R.id.tvLocation)
        imEditGym=findViewById(R.id.imEditGym)
        tvPackageDetail=findViewById(R.id.tvPackageDetail)
        tvStartDate=findViewById(R.id.tvStartDate)
        tvEndDate=findViewById(R.id.tvEndDate)
        tvsession=findViewById(R.id.tvsession)

        tvValidity=findViewById(R.id.tvValidity)
        tvPrice=findViewById(R.id.tvPrice)
        imGym=findViewById(R.id.imGym)
        headerLayout=findViewById(R.id.headerLayout)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        Log.e("studio_id",""+ intent.getStringExtra("studio_id"))
        headerLayout.setOnClickListener {
            finish()
        }
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
           linearSession.visibility=View.GONE
           linearvalidity.visibility=View.VISIBLE
        }else{
            linearSession.visibility=View.VISIBLE
            linearvalidity.visibility=View.GONE
        }
        tvPayment.setOnClickListener{

            val intent = Intent(this, PaymentSelectionActivity::class.java)
            intent.putExtra("selectBookOption","BookCreatedPackage")
            intent.putExtra("type",getIntent().getStringExtra("type"))
            intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
            intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
            intent.putExtra("days",getIntent().getStringExtra("days"))
            intent.putExtra("price",tvPrice.text.toString())
            intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
            intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
            intent.putExtra("tax_rate",tax_rate)
            intent.putExtra("main_price",main_price)
            startActivity(intent)
          /*  val intent = Intent(this, BookingConfirmActivity::class.java)
            intent.putExtra("selectBookOption","BookCreatedPackage")
            intent.putExtra("type",getIntent().getStringExtra("type"))
            intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
            intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
            intent.putExtra("days",getIntent().getStringExtra("days"))
            intent.putExtra("price",tvPrice.text.toString())
            intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
            intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
            startActivity(intent)*/
        }
        imEditGym.setOnClickListener{
            val intent = Intent(this, GymListActivity::class.java)
            startActivity(intent)
        }
        linearslot.setOnClickListener{
            /*val intent = Intent(this, DuringSeesionActivity::class.java)
            intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
            intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
            intent.putExtra("address_id",getIntent().getStringExtra("address_id"))

            intent.putExtra("setstart_dates",getIntent().getStringExtra("setstart_dates"))
            intent.putExtra("setstart_days",getIntent().getStringExtra("setstart_days"))
            intent.putExtra("setend_dates",getIntent().getStringExtra("setend_dates"))
            intent.putExtra("setend_days",getIntent().getStringExtra("setend_days"))
            intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
            startActivity(intent)*/
            finish()
        }
        linearPreference.setOnClickListener{
            sendBroadcast(Intent("closegymClass"))
            finish()

        }

        imEditTrainer.setOnClickListener{
            val intent = Intent(this, TrainersListActivity::class.java)
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))

            startActivity(intent)
        }

        editMembers.setOnClickListener {
            val intent = Intent(this, MembersListForPackage::class.java)
            intent.putExtra("",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("",getIntent().getStringExtra("studio_id"))
            startActivity(intent)
        }
    }
    fun getData()  {
        val param: MutableMap<String, String> = HashMap()
        param["package_type"] = ""+sharedPreferences.getInt("selectedPackageType",0)
        param["sessions"] = ""+ intent.getStringExtra("session_value")
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            param["type"] = ""+sharedPreferences.getString("typeWorkout","")
        }else{
            param["type"] = "gym"
        }
        param["trainer_id"] =""+ intent.getStringExtra("trainer_id")
        param["studio_id"] = ""+ intent.getStringExtra("studio_id")
        param["date"] =""+ intent.getStringExtra("apistart_date")
        param["end_date"] =""+ intent.getStringExtra("apiend_date")
        param["address_id"] =""+ intent.getStringExtra("address_id")
        param["slot_id"] = ""+ intent.getStringExtra("slot_id")

        android.util.Log.e("PackageCheckoutParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.packagecheckout,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                try {
                    android.util.Log.e("GymPackageCheckoutRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        linearpay.visibility=View.VISIBLE
                        nested.visibility=View.VISIBLE
                        tvTrainer_name.text = resp.optJSONObject("data").optJSONObject("trainer").optString("name")
                        tvGymName.text = resp.optJSONObject("data").optJSONObject("studio").optString("name")
                        tvdistance.text = resp.optJSONObject("data").optJSONObject("studio").optString("distance")
                        tvLocation.text = resp.optJSONObject("data").optJSONObject("studio").optString("location")
                        if (resp.optJSONObject("data").optJSONObject("trainer").optString("averageRating").equals("")){
                            avgRating.text = "0"

                        }else{
                            avgRating.text = resp.optJSONObject("data").optJSONObject("trainer").optString("averageRating")

                        }
                        totalRatings.text = resp.optJSONObject("data").optJSONObject("trainer").optString("noOfRating")+" "+"ratings"
                        tvTrainingPrefernce.text = resp.optJSONObject("data").optString("training_preference")
                        Glide.with(applicationContext).load(resp.optJSONObject("data").optJSONObject("trainer").optString("profile")).fitCenter().error(R.drawable.dumbbell).
                        placeholder(R.drawable.dumbbell).into(imTrainer)

                        Glide.with(applicationContext).load(resp.optJSONObject("data").optJSONObject("studio").optString("image")).fitCenter().error(R.drawable.dumbbell).
                        placeholder(R.drawable.dumbbell).into(imGym)
                        tvTime.text = resp.optJSONObject("data").optString("slot_time")

                        tvPackageDetail.text =
                            resp.optJSONObject("data").optJSONObject("packageDetail").optString("package")
                        tvStartDate.text =
                            resp.optJSONObject("data").optJSONObject("packageDetail").optString("start_date")
                        tvEndDate.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("end_date")
                        tvsession.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("totalSessions")
                        tvPrice.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("price").replace("AED","")
                        tax_rate = resp.optJSONObject("data").optJSONObject("packageDetail").optString("tax_price")
                        main_price = resp.optJSONObject("data").optJSONObject("packageDetail").optString("main_price")

                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                        {
                            var activityModel= ActivityModel()
                            activityModel.name=resp.optJSONObject("data").optJSONObject("trainer").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter1(applicationContext, activitiesModelList)
                        recycler.adapter = activityAdapter

                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("studio").optJSONArray("tags").length())
                        {
                            var gymActivityModel= GymActivityModel()
                            gymActivityModel.name=resp.optJSONObject("data").optJSONObject("studio").optJSONArray("tags").get(i).toString()
                            gymmactivitiesModelList.add(gymActivityModel)
                        }
                        var gymactivityAdapter = GymActivityAdapter(applicationContext, gymmactivitiesModelList)
                        recycler1.adapter = gymactivityAdapter

                        if(resp.optJSONObject("data").has("userMembers")){
                            membersLayout.visibility = View.VISIBLE
                            membersList.clear()
                            val jsonArray=resp.optJSONObject("data").optJSONArray("userMembers")
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject=jsonArray.optJSONObject(i)
                                val activityModel= JoinModel()
                                activityModel.id=jsonObject.optString("id")
                                activityModel.name=jsonObject.optString("name")
                                activityModel.age=jsonObject.optString("age")
                                activityModel.gender=jsonObject.optString("gender")
                                membersList.add(activityModel)
                            }
                            val joinAdapter= MembersListAdapter(membersList, applicationContext)
                            membersListRecyclerView.adapter=joinAdapter
                        }
                    }else{
                        linearpay.visibility=View.GONE
                        nested.visibility=View.GONE

                    }


                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        getData()
    }
}