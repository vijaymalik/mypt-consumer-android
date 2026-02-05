package co.com.mypt.activities


import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants.BEST_PLAN_ID
import co.com.mypt.Api.Constants.REVIEW_ADDRESS_ID
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.Webview.CCavenueWebViewActivity
import co.com.mypt.adapter.AddressListAdapter
import co.com.mypt.databinding.ActivityReviewPackageBinding
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.AddressModel
import co.com.mypt.model.AvailablePromo
import co.com.mypt.model.JoinModel
import co.com.mypt.model.ReviewPackageCheckout
import co.com.mypt.model.ReviewPackageCheckout.Data.UpgradePlan
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import org.json.JSONObject


class ReviewPackageActivity : AppCompatActivity() {
//    lateinit var linearpay:LinearLayout
//    lateinit var linearslot:LinearLayout
//    lateinit var linearPreference:LinearLayout
//    lateinit var linearShowCoupon:LinearLayout
//    lateinit var linear:LinearLayout
//    lateinit var linearEdit:LinearLayout
//    lateinit var linearSession:LinearLayout
//    lateinit var linear_Duration:LinearLayout
//    lateinit var membersLayout:LinearLayout

    lateinit var imEditTrainer: ImageView
    lateinit var im_star: ImageView

    //    lateinit var recycler: RecyclerView
//    lateinit var membersListRecyclerView: RecyclerView
    var activitiesModelList: ArrayList<ActivityModel> = ArrayList()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
//    lateinit var headerLayout:LinearLayout

//    lateinit var imTrainer:ImageView
//    lateinit var editAddress:ImageView
//    lateinit var editMembers:ImageView

    //    lateinit var mobile:TextView
//    lateinit var address:TextView
//    lateinit var addressType:TextView
//    lateinit var tvTrainer_name:TextView
//    lateinit var avgRating:TextView
//    lateinit var totalRatings:TextView
//    lateinit var tvTrainingPrefernce:TextView
//    lateinit var tvTime:TextView
//    lateinit var tvPackageDetail:TextView
//    lateinit var tvStartDate:TextView
//    lateinit var tvPackageName:TextView
//    lateinit var tvTotalDuration:TextView
//    lateinit var tvTotalSession:TextView
//    lateinit var tvEndDate:TextView
//    lateinit var tvPrice:TextView
    var updatedAddress = ""

    //    lateinit var nested:NestedScrollView
    var membersList = ArrayList<JoinModel>()
    var tax_rate = ""
    var main_price = ""
    var selectedCouponId: String? = ""
    var upgradIdText: UpgradePlan? = null
    var available_promos: List<AvailablePromo?>? = null
    var isupgreadClick = false
    var selectedAddressId = ""
    private var progressDialog: Dialog? = null
    lateinit var binding: ActivityReviewPackageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        edit = sharedPreferences.edit()
        binding.addressUpdate.setOnClickListener {
            showAddresListDialog()
//            startActivity(Intent(this, AddressListForPackage::class.java))
        }
        binding.viewAllCoupon.setOnClickListener {
            val intent = Intent(this, ViewCouponsOfferActivity::class.java)
            intent.putParcelableArrayListExtra(
                "couponList",
                available_promos as java.util.ArrayList<out Parcelable?>?
            )
            startActivity(intent)
        }
        binding.appliedCoupon.visibility = View.GONE
        binding.upgradePlan.setOnClickListener {
            upgradeDialog()
        }
        binding.back1.setOnClickListener {
            finish()
        }
        if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
            binding.tranningAddress.visibility = View.VISIBLE
            binding.tranningLocation.visibility = View.VISIBLE
            binding.selectedGymView.visibility = View.GONE
        } else {
            binding.tranningAddress.visibility = View.GONE
            binding.tranningLocation.visibility = View.GONE
            binding.selectedGymView.visibility = View.VISIBLE
        }

        binding.tvPayment.setOnClickListener {
            val intent = Intent(this, CCavenueWebViewActivity::class.java)

            if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
                intent.putExtra("type", sharedPreferences.getString("typeWorkout", "").toString())
            } else {
                intent.putExtra("type", "gym")
                intent.putExtra("studio_id", intent.getStringExtra("studio_id").toString())
            }
            val finalAddressId = if (updatedAddress != "")
                updatedAddress
            else
                sharedPreferences.getString(REVIEW_ADDRESS_ID, "").toString()

            intent.putExtra("address_id", finalAddressId)
            val best_plan_id_Final = if (isupgreadClick) {
                upgradIdText?.id ?: ""
            } else {
                intent.getStringExtra(BEST_PLAN_ID).toString()
            }
            intent.putExtra("address_id", best_plan_id_Final)

            intent.putExtra(
                "package_type",
                sharedPreferences.getInt("selectedPackageType", 0).toString()
            )
            intent.putExtra("session_value", intent.getStringExtra("session_value").toString())
            intent.putExtra("trainer_id", intent.getStringExtra("trainer_id").toString())
//        param["skip_offer"] = "true"
            intent.putExtra("offer_id", selectedCouponId ?: "")

            /*if(getIntent().getStringExtra("selectBookOption").equals("BookCreatedPackage")){
                intent.putExtra("selectBookOption","BookCreatedPackage")
                intent.putExtra("type",getIntent().getStringExtra("type"))
                intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
                intent.putExtra("days",getIntent().getStringExtra("days"))
                intent.putExtra("price",getIntent().getStringExtra("price"))
                intent.putExtra("selectedPaymentOption","ccavenue")

            }else{
                intent.putExtra("price", getIntent().getStringExtra("price"))
                intent.putExtra("type",getIntent().getStringExtra("type"))
                intent.putExtra("selectBookOption","normalBookSlot")
                intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                intent.putExtra("selectedPaymentOption","ccavenue")

            }*/

            startActivity(intent)
        }
        getAddressData()
//        editMembers=findViewById(R.id.editMembers)
//        membersListRecyclerView=findViewById(R.id.membersListRecyclerView)
        /*membersLayout=findViewById(R.id.membersLayout)
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
        im_star=findViewById(R.id.im_star)*/


        /* headerLayout.setOnClickListener{
             finish()
         }*/

        /* if (sharedPreferences.getString("typewithout","").equals("withoutTrainer")){
             linearEdit.visibility=View.GONE
             linear_Duration.visibility=View.VISIBLE
             linearSession.visibility=View.GONE
         }else{
             linearEdit.visibility=View.VISIBLE
             linear_Duration.visibility=View.GONE
             linearSession.visibility=View.VISIBLE

         }*/
        /* linearpay.setOnClickListener{
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
           *//*  val intent = Intent(this, BookingConfirmActivity::class.java)
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
            startActivity(intent)*//*
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
            *//*val intent = Intent(this, CreatePackagectivity::class.java)
            intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
            startActivity(intent)*//*
        }
        linear.setOnClickListener{
            val intent = Intent(this, ViewCouponsOfferActivity::class.java)
            startActivity(intent)
        }*/

        /* editAddress.setOnClickListener {
             startActivity(Intent(this, AddressListForPackage::class.java))
         }*/
        /* editMembers.setOnClickListener {
             val intent = Intent(this, MembersListForPackage::class.java)
             intent.putExtra("",getIntent().getStringExtra("trainer_id"))
             intent.putExtra("",getIntent().getStringExtra("studio_id"))
             startActivity(intent)
         }*/
        val filter = IntentFilter("selectedCoupon")
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(countReceiver, filter)
        /*val selectAddressFilter = IntentFilter("selectAddressFilter")
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(updatedAddressId, selectAddressFilter)*/
        getData()
    }

    override fun onResume() {
        super.onResume()

    }

    val updatedAddressId = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updatedAddress = intent?.getStringExtra("addressId").toString()
            getData()
        }
    }
    val countReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            selectedCouponId = intent?.getStringExtra("couponId")
            binding.offerTitle.text = intent?.getStringExtra("couponName")
            binding.appliedCoupon.visibility = View.VISIBLE
            binding.applyCoupon.visibility = View.GONE
            getData()
        }

    }

    fun showAddresListDialog() {
        val dialog = BottomSheetDialog(this) // Fragment -> requireContext()
        dialog.setContentView(R.layout.adress_list_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val recyclerAddress = dialog.findViewById<RecyclerView>(R.id.recyclerAddress)
        recyclerAddress?.adapter = AddressListAdapter(addressList, applicationContext) {
            dialog.dismiss()
            updatedAddress = it
            getData()
        }
        dialog.show()
    }

    var addressList = ArrayList<AddressModel>()
    private fun getAddressData() {

       showProgress()


        GetMethod(ApiURL.getaddress, applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
               hideProgress()

                addressList.clear()
                Log.e("getAddressResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        val jsonArray = jsonObj.optJSONArray("data")

                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject1 = jsonArray.optJSONObject(i)
                                val addressModel = AddressModel()

                                addressModel.building_name = jsonObject1.optString("building_name")
                                addressModel.street = jsonObject1.optString("street")
                                addressModel.landmark = jsonObject1.optString("landmark")
                                addressModel.type = jsonObject1.optString("type")
                                addressModel.city_id = jsonObject1.optString("city_id")
                                addressModel.country_id = jsonObject1.optString("country_id")
                                addressModel.mobile_no = jsonObject1.optString("mobile_no")
                                addressModel.country_name = jsonObject1.optString("country_name")
                                addressModel.city_name = jsonObject1.optString("city_name")
                                addressModel.lat = jsonObject1.optString("lat")
                                addressModel.long = jsonObject1.optString("long")
                                addressModel.id = jsonObject1.optString("id")

                                addressList.add(addressModel)

                            }
                            //                            linearNoAddress.visibility=View.GONE
//                            nested.visibility=View.VISIBLE

                        } else {
//                            linearNoAddress.visibility=View.VISIBLE
//                            nested.visibility=View.GONE
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
               hideProgress()
                error!!.printStackTrace()
            }

        })
    }

    fun upgradeDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.upgrade_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val offerTitle = dialog.findViewById<TextView?>(R.id.offerTitle)
        offerTitle.text = upgradIdText?.title

        val savetilesub = dialog.findViewById<TextView?>(R.id.savetilesub)
        savetilesub.text = "GYM20 every time you book sessions"
        val savetile = dialog.findViewById<TextView?>(R.id.savetile)
        savetile.text = upgradIdText?.badge_text

        val btnSave = dialog.findViewById<RelativeLayout?>(R.id.btnSave)

        btnSave.setOnClickListener(View.OnClickListener { v: View? ->
            isupgreadClick = true
            getData()
            dialog.dismiss()
        })

        dialog.show()

    }


    fun getData() {
        showProgress()

        val param: MutableMap<String, String> = HashMap()

        if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
            param["type"] = sharedPreferences.getString("typeWorkout", "").toString()
        } else {
            param["type"] = "gym"
            param["studio_id"] = intent.getStringExtra("studio_id").toString()
        }
        if (updatedAddress != "")
            param["address_id"] = updatedAddress
        else
            param["address_id"] = sharedPreferences.getString(REVIEW_ADDRESS_ID, "").toString()
        if (isupgreadClick) {
            isupgreadClick = false
            param["best_plan_id"] = upgradIdText?.id ?: ""
        } else {
            param["best_plan_id"] = intent.getStringExtra(BEST_PLAN_ID).toString()
        }

        param["package_type"] = sharedPreferences.getInt("selectedPackageType", 0).toString()
        param["sessions"] = intent.getStringExtra("session_value").toString()
        param["trainer_id"] = intent.getStringExtra("trainer_id").toString()
//        param["skip_offer"] = "true"
        param["offer_id"] = selectedCouponId ?: ""

        Log.e("PackageCheckoutParam", param.toString())


        PostMethod(ApiURL.reviewPackageCheckout, param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                hideProgress()

                try {
                    Log.e("PackageCheckoutRes", data.toString())

                    val data: ReviewPackageCheckout =
                        Gson().fromJson(data, ReviewPackageCheckout::class.java)
                    if (data.status == true) {
                        available_promos = data.data?.available_promos
                        upgradIdText = data.data?.upgrade_plan
                        if (data.data?.upgrade_plan != null) {
                            binding.title.text = data.data.upgrade_plan.title
                            binding.subTitle.text =
                                "Avail at ${data.data.upgrade_plan.badge_text} for ${data.data.upgrade_plan.sessions} sessions"
                        } else {
                            binding.upgradePlanView.visibility = View.GONE
                        }
                        binding.mode.text = "${data.data?.package_details?.type} Training"
                        binding.typeWorkOut.text = "${data.data?.package_details?.type} "
                        binding.tvSession.text = "${data.data?.package_details?.sessions}"
                        binding.validity.text = "${data.data?.package_details?.validity}"
                        binding.packagePrice.text = "AED ${data.data?.package_details?.price}"
                        binding.tvSessionMain.text =
                            "AED ${data.data?.package_details?.price_per_session}/session"
                        binding.trainerName.text =
                            "${data.data?.trainer_detail?.primary_trainer?.name}"
                        binding.selectedGym.text = "${data.data?.studio?.name}"
                        if (data.data?.package_details?.type == "Home")
                            binding.home.text = "Home"
                        binding.address.text =
                            "${data.data?.address?.building_name},${data.data?.address?.street},${data.data?.address?.landmark},${data.data?.address?.city_name},${data.data?.address?.country_name}"
                        selectedAddressId = data.data?.address?.id.toString()
                    } else {
                        binding.home.text = "Gym"
                        binding.address.text = "${data.data?.studio?.address}"
                    }
                    binding.paymentTimeMsg.text = "${data.data?.payment_msg}"
                    /*if(resp.optBoolean("status")){
//                        linearpay.visibility=View.VISIBLE
//                        nested.visibility=View.VISIBLE

                        val addressModel = resp.optJSONObject("data").optJSONObject("address")
                        val landmark = if (addressModel.optString("landmark").isNullOrEmpty()) "${addressModel.optString("landmark")}," else ""

//                        address.text = addressModel.optString("building_name")+", "+addressModel.optString("street")+", "+landmark+addressModel.optString("city_name")+", "+addressModel.optString("country_name")
//                        addressType.text = addressModel.optString("type")
//                        mobile.text = addressModel.optString("mobile_no")

//                        tvTrainer_name.text = resp.optJSONObject("data").optJSONObject("trainer").optString("name")
//                        if (resp.optJSONObject("data").optJSONObject("trainer").optString("averageRating").equals("")){
//                            avgRating.text = "0"
//                        }else{
//                            avgRating.text = resp.optJSONObject("data").optJSONObject("trainer").optString("averageRating")
//
//                        }
//                        totalRatings.text = resp.optJSONObject("data").optJSONObject("trainer").optString("noOfRating")+" ratings"
//                        tvTrainingPrefernce.text = resp.optJSONObject("data").optString("training_preference")
//                        Glide.with(applicationContext).load(resp.optJSONObject("data").optJSONObject("trainer").optString("profile")).fitCenter().error(R.drawable.dumbbell).
//                        placeholder(R.drawable.dumbbell).into(imTrainer)
                        //tvTime.text = resp.optJSONObject("data").optString("slot_time")

//                        tvPackageDetail.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("package")
//                        tvStartDate.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("start_date")
//                        tvEndDate.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("end_date")
//                        tvTotalSession.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("totalSessions")
                        tax_rate = resp.optJSONObject("data").optJSONObject("packageDetail").optString("tax_price")
                        main_price = resp.optJSONObject("data").optJSONObject("packageDetail").optString("main_price")
//                        tvPrice.text = resp.optJSONObject("data").optJSONObject("packageDetail").optString("price").replace("AED","")

                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length()) {
                            val activityModel= ActivityModel()
                            activityModel.name=resp.optJSONObject("data").optJSONObject("trainer").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        val activityAdapter = ReviewActivityAdapter(this@ReviewPackageActivity, activitiesModelList)
//                        recycler.adapter = activityAdapter

                        if(resp.optJSONObject("data").has("userMembers")){
//                            membersLayout.visibility = View.VISIBLE
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
                           *//* val joinAdapter= MembersListAdapter(membersList, applicationContext)
                            membersListRecyclerView.adapter=joinAdapter*//*
                        }
                    }else{
//                        linearpay.visibility=View.GONE
//                        nested.visibility=View.GONE
                    }*/
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                hideProgress()
                error!!.printStackTrace()
            }

        })
    }

    private fun showProgress() {
        if (!isFinishing && !isDestroyed) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.progressDialog(this, "")
            }

            if (progressDialog?.isShowing == false) {
                progressDialog?.show()
            }
        }
    }

    private fun hideProgress() {
        if (!isFinishing && !isDestroyed) {
            progressDialog?.dismiss()
        }
    }

}