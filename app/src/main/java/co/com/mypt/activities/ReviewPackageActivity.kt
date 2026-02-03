package co.com.mypt.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants.BEST_PLAN_ID
import co.com.mypt.Api.Constants.REVIEW_ADDRESS_ID
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.MembersListAdapter
import co.com.mypt.adapter.ReviewActivityAdapter
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.JoinModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class ReviewPackageActivity : AppCompatActivity() {
    lateinit var linearpay:LinearLayout
    lateinit var linearslot:LinearLayout
    lateinit var linearPreference:LinearLayout
    lateinit var linearShowCoupon:LinearLayout
    lateinit var linear:LinearLayout
    lateinit var linearEdit:LinearLayout
    lateinit var linearSession:LinearLayout
    lateinit var linear_Duration:LinearLayout
    lateinit var membersLayout:LinearLayout

    lateinit var imEditTrainer:ImageView
    lateinit var im_star:ImageView
    lateinit var recycler: RecyclerView
    lateinit var membersListRecyclerView: RecyclerView
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    lateinit var headerLayout:LinearLayout

    lateinit var imTrainer:ImageView
    lateinit var editAddress:ImageView
    lateinit var editMembers:ImageView

    lateinit var mobile:TextView
    lateinit var address:TextView
    lateinit var addressType:TextView
    lateinit var tvTrainer_name:TextView
    lateinit var avgRating:TextView
    lateinit var totalRatings:TextView
    lateinit var tvTrainingPrefernce:TextView
    lateinit var tvTime:TextView
    lateinit var tvPackageDetail:TextView
    lateinit var tvStartDate:TextView
    lateinit var tvPackageName:TextView
    lateinit var tvTotalDuration:TextView
    lateinit var tvTotalSession:TextView
    lateinit var tvEndDate:TextView
    lateinit var tvPrice:TextView
    var updatedAddress = ""
    lateinit var nested:NestedScrollView
    var membersList = ArrayList<JoinModel>()
    var tax_rate=""
    var main_price=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_package)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)
        edit=sharedPreferences.edit()

        editMembers=findViewById(R.id.editMembers)
        membersListRecyclerView=findViewById(R.id.membersListRecyclerView)
        membersLayout=findViewById(R.id.membersLayout)
        mobile=findViewById(R.id.mobile)
        address=findViewById(R.id.address)
        addressType=findViewById(R.id.addressType)
        editAddress=findViewById(R.id.editAddress)
        linearEdit=findViewById(R.id.linearEdit)
        linearslot=findViewById(R.id.linearslot)
        linear=findViewById(R.id.linear)
        linearSession=findViewById(R.id.linearSession)
        recycler=findViewById(R.id.recycler)
        linearPreference=findViewById(R.id.linearPreference)
        linearShowCoupon=findViewById(R.id.linearShowCoupon)
        linear_Duration=findViewById(R.id.linear_Duration)
        imEditTrainer=findViewById(R.id.imEditTrainer)
        headerLayout=findViewById(R.id.headerLayout)
        imTrainer=findViewById(R.id.imTrainer)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        tvTrainingPrefernce=findViewById(R.id.tvTrainingPrefernce)
        avgRating=findViewById(R.id.avgRating)
        totalRatings=findViewById(R.id.totalRatings)
        tvPackageDetail=findViewById(R.id.tvPackageDetail)
        tvStartDate=findViewById(R.id.tvStartDate)

        tvTotalDuration=findViewById(R.id.tvTotalDuration)
        tvTotalSession=findViewById(R.id.tvTotalSession)
        tvEndDate=findViewById(R.id.tvEndDate)
        tvPrice=findViewById(R.id.tvPrice)
        nested=findViewById(R.id.nested)
        linearpay=findViewById(R.id.linearpay)
        tvTime=findViewById(R.id.tvTime)
        im_star=findViewById(R.id.im_star)


        headerLayout.setOnClickListener{
            finish()
        }

        if (sharedPreferences.getString("typewithout","").equals("withoutTrainer")){
            linearEdit.visibility=View.GONE
            linear_Duration.visibility=View.VISIBLE
            linearSession.visibility=View.GONE
        }else{
            linearEdit.visibility=View.VISIBLE
            linear_Duration.visibility=View.GONE
            linearSession.visibility=View.VISIBLE

        }
        linearpay.setOnClickListener{
           // edit.remove("typeWorkout").apply()
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
            intent.putExtra("price",tvPrice.text.toString())
            intent.putExtra("tax_rate",tax_rate)
            intent.putExtra("main_price",main_price)
            startActivity(intent)*/
        }
        linearslot.setOnClickListener{
            val intent = Intent(this, DuringSeesionActivity::class.java)
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
            startActivity(intent)
        }
        imEditTrainer.setOnClickListener{
            val intent = Intent(this, TrainersListActivity::class.java)
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            startActivity(intent)
        }
        linearPreference.setOnClickListener{
            sendBroadcast(Intent("closeClass"))
            finish()
            /*val intent = Intent(this, CreatePackagectivity::class.java)
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
            startActivity(intent)*/
        }
        linear.setOnClickListener{
            val intent = Intent(this, ViewCouponsOfferActivity::class.java)
            startActivity(intent)
        }

        editAddress.setOnClickListener {
            startActivity(Intent(this, AddressListForPackage::class.java))
        }
        editMembers.setOnClickListener {
            val intent = Intent(this, MembersListForPackage::class.java)
            intent.putExtra("",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("",getIntent().getStringExtra("studio_id"))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(countReceiver, IntentFilter("selectedCoupon"), RECEIVER_NOT_EXPORTED)
            registerReceiver(updatedAddressId, IntentFilter("updatedAddressId"), RECEIVER_NOT_EXPORTED)
        }else{
            registerReceiver(countReceiver, IntentFilter("selectedCoupon"))
            registerReceiver(updatedAddressId, IntentFilter("updatedAddressId"))
        }
        getData()
    }

    val countReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.getIntExtra("selectedPosition",-1) > -1){
                linearShowCoupon.visibility = View.VISIBLE
            }else{
                linearShowCoupon.visibility = View.GONE

            }
        }

    }
    val updatedAddressId = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updatedAddress = intent?.getStringExtra("addressId").toString()
        }
    }

    fun getData()  {
        val param: MutableMap<String, String> = HashMap()

        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            param["type"] = sharedPreferences.getString("typeWorkout","").toString()
        }else{
            param["type"] = "gym"
        }
        param["best_plan_id"] = intent.getStringExtra(BEST_PLAN_ID).toString()
        param["package_type"] = sharedPreferences.getInt("selectedPackageType",0).toString()
        param["sessions"] = intent.getStringExtra("session_value").toString()
        param["trainer_id"] =intent.getStringExtra("trainer_id").toString()
        param["studio_id"] = intent.getStringExtra("studio_id").toString()

        if(updatedAddress != "")
            param["address_id"] =updatedAddress
        else
            param["address_id"] =sharedPreferences.getString(REVIEW_ADDRESS_ID,"").toString()

//        param["slot_id"] = ""+ intent.getStringExtra("slot_id")
//        param["date"] =""+ intent.getStringExtra("apistart_date")
//        param["end_date"] =""+ intent.getStringExtra("apiend_date")
        android.util.Log.e("PackageCheckoutParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        PostMethod(ApiURL.reviewPackageCheckout,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                try {
                    android.util.Log.e("PackageCheckoutRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        linearpay.visibility=View.VISIBLE
                        nested.visibility=View.VISIBLE

                        val addressModel = resp.optJSONObject("data").optJSONObject("address")
                        val landmark = if (addressModel.optString("landmark").isNullOrEmpty()) "${addressModel.optString("landmark")}," else ""

                        address.text = addressModel.optString("building_name")+", "+addressModel.optString("street")+", "+landmark+addressModel.optString("city_name")+", "+addressModel.optString("country_name")
                        addressType.text = addressModel.optString("type")
                        mobile.text = addressModel.optString("mobile_no")

                        tvTrainer_name.text = resp.optJSONObject("data").optJSONObject("trainer").optString("name")
                        if (resp.optJSONObject("data").optJSONObject("trainer").optString("averageRating").equals("")){
                            avgRating.text = "0"
                        }else{
                            avgRating.text = resp.optJSONObject("data").optJSONObject("trainer").optString("averageRating")

                        }
                        totalRatings.text = resp.optJSONObject("data").optJSONObject("trainer").optString("noOfRating")+" ratings"
                        tvTrainingPrefernce.text = resp.optJSONObject("data").optString("training_preference")
                        Glide.with(applicationContext).load(resp.optJSONObject("data").optJSONObject("trainer").optString("profile")).fitCenter().error(R.drawable.dumbbell).
                        placeholder(R.drawable.dumbbell).into(imTrainer)
                        tvTime.text = resp.optJSONObject("data").optString("slot_time")

                        tvPackageDetail.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("package")
                        tvStartDate.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("start_date")
                        tvEndDate.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("end_date")
                        tvTotalSession.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("totalSessions")
                        tax_rate = resp.optJSONObject("data").optJSONObject("packageDetail").optString("tax_price")
                        main_price = resp.optJSONObject("data").optJSONObject("packageDetail").optString("main_price")
                        tvPrice.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("price").replace("AED","")

                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length()) {
                            val activityModel= ActivityModel()
                            activityModel.name=resp.optJSONObject("data").optJSONObject("trainer").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        val activityAdapter = ReviewActivityAdapter(this@ReviewPackageActivity, activitiesModelList)
                        recycler.adapter = activityAdapter

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

}