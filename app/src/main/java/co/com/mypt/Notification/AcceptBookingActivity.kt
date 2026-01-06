package co.com.mypt.Notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.AddressNotificationAdapter
import co.com.mypt.model.BreakFastListModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.util.Locale

class AcceptBookingActivity : AppCompatActivity() {
    lateinit var headerLayout: LinearLayout
    lateinit var addressBottomSheetDialog:BottomSheetDialog
    var addressArrayList = ArrayList<BreakFastListModel>()
    var address_id=""
    var checkType=""
    lateinit var editAddress: ImageView
    lateinit var imTrainer: ImageView
    lateinit var tvTrainerName: TextView
    lateinit var tvdistance: TextView
    lateinit var tvPrice: TextView
    lateinit var tvTrainerLocation: TextView
    lateinit var avgRating: TextView
    lateinit var textAddress: TextView

    lateinit var tvDateTime: TextView
    lateinit var tvSessionType: TextView
    lateinit var tvLocation: TextView
    lateinit var tvAddress: TextView
    lateinit var tvPayment: TextView
    lateinit var view1: View
    lateinit var tv1PTamountmsg: TextView
    lateinit var tvRateTrainer: TextView
    lateinit var tvcurrency: TextView
    lateinit var cardChangeAddress: CardView
    lateinit var tvTrainingPrefernce: TextView
    lateinit var recycleraddresslist: RecyclerView
    lateinit var tvCost: TextView
    lateinit var tvSaveAddress: TextView
    lateinit var linearpay: LinearLayout
    lateinit var dataLayout: LinearLayout
    lateinit var nested: NestedScrollView
    lateinit var AddnewAddress: CardView
    lateinit var addressAdapter: AddressNotificationAdapter
    lateinit var feedbackBottomSheetDialog:BottomSheetDialog
    lateinit var rateBottomSheetDialog:BottomSheetDialog
    var showProgress = false
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null
    var isResumed1 = true
    var isclick=0
    var trainerId=""
    var addresstext=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_booking)
        rateBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        feedbackBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        addressBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        editAddress=findViewById(R.id.editAddress)
        view1=findViewById(R.id.view1)
        headerLayout=findViewById(R.id.headerLayout)
        dataLayout=findViewById(R.id.dataLayout)
        nested=findViewById(R.id.nested)
        imTrainer=findViewById(R.id.imTrainer)
        linearpay=findViewById(R.id.linearpay)
        tvTrainerName=findViewById(R.id.tvTrainerName)
        tvdistance=findViewById(R.id.tvdistance)
        tvTrainerLocation=findViewById(R.id.tvTrainerLocation)
        avgRating=findViewById(R.id.avgRating)
        tvRateTrainer=findViewById(R.id.tvRateTrainer)
        tvDateTime=findViewById(R.id.tvDateTime)
        tvSessionType=findViewById(R.id.tvSessionType)
        tvCost=findViewById(R.id.tvCost)
        tvAddress=findViewById(R.id.tvAddress)
        tv1PTamountmsg=findViewById(R.id.tv1PTamountmsg)
        tvcurrency=findViewById(R.id.tvcurrency)

        AddnewAddress=findViewById(R.id.AddnewAddress)
        tvTrainingPrefernce=findViewById(R.id.tvTrainingPrefernce)
        tvPrice=findViewById(R.id.tvPrice)
        tvPayment=findViewById(R.id.tvPayment)
        headerLayout.setOnClickListener {
            finish()
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        editAddress.setOnClickListener {
            val sheet = addressBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                // Set height to match parent
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            addressBottomSheetDialog.show()
        }
        AddnewAddress.setOnClickListener {
            var intent= Intent(this@AcceptBookingActivity, SelectNotificationAddressActivity::class.java)
            startActivity(intent)
        }
        tvRateTrainer.setOnClickListener{
            val sheet = rateBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                // Set height to match parent
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            rateBottomSheetDialog.show()
        }
    }
    private fun trainerRatingAlert() {

        val bottomSheet = layoutInflater.inflate(R.layout.rate_trainer_bottomsheet, null)
        var tvsubmit_feedback=bottomSheet.findViewById<TextView>(R.id.tvsubmit_feedback)
        var title=bottomSheet.findViewById<TextView>(R.id.title)
        var feedback=bottomSheet.findViewById<EditText>(R.id.feedback)
        var rating=bottomSheet.findViewById<RatingBar>(R.id.rating)
        /*if(tvSessionType.text.toString().contains("gym")){
            title.text = "Rate MyPT Studio"
        }*/
        tvsubmit_feedback.setOnClickListener{
            if(rating.rating <= 0.0){
                Toast.makeText(this@AcceptBookingActivity,"Please give rating to continue", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            trainerRating(feedback.text.toString(), rating.rating,feedback)
            rateBottomSheetDialog.dismiss()
        }
        rateBottomSheetDialog.setContentView(bottomSheet)

    }
    private fun feebbackAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.feedback_alert_layout, null)
        var tvdone=bottomSheet.findViewById<TextView>(R.id.tvdone)
        tvdone.setOnClickListener{
            feedbackBottomSheetDialog.dismiss()
        }
        feedbackBottomSheetDialog.setContentView(bottomSheet)
    }

    private fun trainerRating(feedback: String, rating: Float, feedbackEt: EditText) {
        val param: MutableMap<String, String> = HashMap()
        param["trainer_id"] = trainerId
        param["booking_id"] = intent.getStringExtra("id").toString()
        param["message"] = feedback
        param["rating"] = "$rating"

        Log.e("ratingRequestParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.rateTrainer,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("ratingRequestRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        feedbackBottomSheetDialog.show()
                        feedbackEt.text.clear()
                        getBookingDetail()

                    }else{
                        rateBottomSheetDialog.show()
                        if(resp.optString("msg") == "Validation Error"){
                            Toast.makeText(applicationContext,"Something went wrong please try again later",Toast.LENGTH_LONG).show()
                            return
                        }
                        Toast.makeText(applicationContext,resp.optString("msg"),Toast.LENGTH_LONG).show()
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

    @SuppressLint("ClickableViewAccessibility")
    private fun createSelectDeliveryAddress(canOpen: Boolean) {

        val bottomSheet = layoutInflater.inflate(R.layout.selectdelivery_address_bottomsheet, null)
        addressBottomSheetDialog.setContentView(bottomSheet)
        val parent = bottomSheet.parent as View

        var cardAddnewAddress =bottomSheet.findViewById<CardView>(R.id.cardAddnewAddress)
        cardChangeAddress =bottomSheet.findViewById<CardView>(R.id.cardChangeAddress)
        tvSaveAddress =bottomSheet.findViewById(R.id.tvSaveAddress)
        textAddress =bottomSheet.findViewById(R.id.textAddress)
         recycleraddresslist =bottomSheet.findViewById<RecyclerView>(R.id.recycleraddresslist)

        recycleraddresslist.setOnTouchListener { _, event ->
            // Tell parent (bottom sheet) not to intercept touch when scrolling
            parent.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        addressAdapter= AddressNotificationAdapter(this@AcceptBookingActivity, addressArrayList,address_id)
        recycleraddresslist.adapter=addressAdapter
        tvSaveAddress.visibility=View.VISIBLE
        recycleraddresslist.visibility=View.VISIBLE

        cardAddnewAddress.setOnClickListener{
            var intent= Intent(this@AcceptBookingActivity, SelectNotificationAddressActivity::class.java)
            startActivity(intent)
        }
        cardChangeAddress.setOnClickListener{
            addressBottomSheetDialog.dismiss()
        }
        val window = addressBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(addressBottomSheetDialog)
        addressBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if(canOpen)
            addressBottomSheetDialog.show()
    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }


    private fun checkLocationPermission(): Boolean {
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        /*  if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
             listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
         }*/
        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            //requestPermissions(listPermissionsNeeded.toTypedArray<String>(), 123)
            return false
        }
        return true
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
                Log.i("DEBUG", "permission granted")
            } else {
                // if permission denied then check whether never ask
                // again is selected or not by making use of
                // !ActivityCompat.shouldShowRequestPermissionRationale(
                // requireActivity(), Manifest.permission.CAMERA)
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setCancelable(false)
                    builder.setTitle(resources.getString(R.string.LocationPermission))
                    builder.setMessage(resources.getString(R.string.grantlocationpermission))
                    builder.setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        dialog.dismiss()
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("package:" + this.packageName)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(intent)
                    }
                    builder.create()
                    builder.show()

                    // User selected the Never Ask Again Option
                }
                else checkLocationPermission()
                Log.i("DEBUG", "permission denied")
            }
        }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(
            this
        )
        alertDialogBuilder.setMessage(resources.getString(R.string.gpsisenabled))
            .setCancelable(false)
            .setPositiveButton(R.string.EnableGPS,
                DialogInterface.OnClickListener { dialog, id ->
                    showProgress = true
                    val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                })
        /*
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        checkLocationPermission();
                    }
                });
*/
        val alert = alertDialogBuilder.create()
        alert.show()
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        //mMap.clear();
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED*/
        ) {

        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, (20 * 1000).toLong())
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        if (isResumed1) {
                            showProgress = false
                            progressDialog.dismiss()
                            longitude = location.longitude
                            latitude = location.latitude
                            LatLng(latitude!!, longitude!!)
                            Log.e("latitude",""+latitude)
                            Log.e("longitude",""+longitude)
                            var addresses: List<Address>? = null
                            var address = ""
                            var cityState = ""
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    geocoder.getFromLocation(
                                        latitude!!, longitude!!, 1
                                    ) { addressList: List<Address?>? ->
                                        address = addressList!![0]!!.getAddressLine(0)
                                        cityState = addressList!![0]!!.subLocality +", "+ addressList!![0]!!.locality
                                    }
                                } else {
                                    addresses =
                                        geocoder.getFromLocation(latitude!!, longitude!!, 1)!!
                                    address = addresses[0].getAddressLine(0)
                                    cityState = addresses[0].subLocality +", "+ addresses[0].locality
                                }
                                getBookingDetail()


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                //  tvLocation.text = address

                            }, 500)


                            mFusedLocationClient.removeLocationUpdates(locationCallback)
                            isResumed1 = false
                        }
                    }
                }
            }
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
   private fun getBookingDetail() {
       val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
       progressDialog.show()
       var api=""
       api= ApiURL.booking_detail+intent.getStringExtra("id")+"&long="+longitude+"&lat="+latitude

       Log.e("getbookingDetailApi",""+api)

       GetMethod(api,applicationContext).startMethod(object :
           ResponseData {

           override fun response(data: String?) {
               progressDialog.dismiss()

               Log.e("getBookingResponse",data.toString())
               try {
                   val jsonObj = JSONObject(data!!)
                   if (jsonObj.optBoolean("status")){
                       nested.visibility= View.VISIBLE
                       linearpay.visibility= View.VISIBLE
                       val jsonObjectData=jsonObj.optJSONObject("data")
                       //  tvDateTime.setText(jsonObjectData.optString("booked_at"))
                       // cancellationMsg.setText(jsonObjectData.optString("cancellationPolicyMsg"))

                       if(jsonObjectData.optJSONObject("bookingDetail").optString("type") == "gym")
                           editAddress.visibility = View.GONE
                       var type=jsonObjectData.optJSONObject("bookingDetail").optString("type").capitalize()
                       tvSessionType.text =type+" Session"
                       tvDateTime.text = jsonObjectData.optJSONObject("bookingDetail").optString("training_date")
                       address_id = jsonObjectData.optJSONObject("bookingDetail").optString("address_id")

                       trainerId = jsonObjectData.optJSONObject("trainerDetail").optString("trainer_id")
                       tvTrainerName.text = jsonObjectData.optJSONObject("trainerDetail").optString("name")
                       tvdistance.text = jsonObjectData.optJSONObject("trainerDetail").optString("distance")
                       tvTrainerLocation.text = jsonObjectData.optJSONObject("trainerDetail").optString("location")
                       addresstext= jsonObjectData.optJSONObject("bookingDetail").optString("location")
                       tvAddress.text = addresstext
                       avgRating.text = jsonObjectData.optJSONObject("trainerDetail").optString("averageRating")

                       Glide.with(applicationContext).load(jsonObjectData.optJSONObject("trainerDetail").optString("profile")).fitCenter().error(R.drawable.dummy_trainer).
                       placeholder(R.drawable.dumbbell).into(imTrainer)
                       if (jsonObjectData.optString("isPackage").equals("true")){
                           tvCost.text = "1 PT Credit"
                           tvPrice.text = "1PT"
                           tvcurrency.text = "Balance"
                           tvPayment.text = "Confirm Booking"
                           tv1PTamountmsg.visibility=View.VISIBLE
                           tvPayment.setOnClickListener {
                               val intent = Intent(this@AcceptBookingActivity,
                                   NotificationBookingConfirmActivity::class.java)
                               intent.putExtra("booking_id",getIntent().getStringExtra("id"))
                               intent.putExtra("price","")
                               intent.putExtra("transaction_id","")
                               intent.putExtra("payment_type","")
                               intent.putExtra("address_id",address_id)

                               startActivity(intent)
                           }
                       }
                       else{
                           tv1PTamountmsg.visibility=View.GONE
                           tvCost.text = jsonObjectData.optString("bookingPrice")+" AED"
                           tvPrice.text = jsonObjectData.optString("bookingPrice")
                           tvcurrency.text = "AED"
                           tvPayment.text = "Confirm & pay"
                           tvPayment.setOnClickListener {
                               var intent=Intent(this@AcceptBookingActivity, NotificationPaymentSelectionActivity::class.java)
                               intent.putExtra("booking_id",getIntent().getStringExtra("id"))
                               intent.putExtra("price",jsonObjectData.optString("bookingPrice"))
                               intent.putExtra("tax_rate",jsonObjectData.optString("tax_amount"))
                               intent.putExtra("main_price",jsonObjectData.optString("main_price"))
                               intent.putExtra("address_id",jsonObjectData.optJSONObject("bookingDetail").optString("address_id"))
                               startActivity(intent)
                           }
                       }
                       if (!jsonObjectData.optBoolean("isAlreadyReview")){
                           view1.visibility=View.VISIBLE
                           tvRateTrainer.visibility=View.VISIBLE
                       }else{
                           view1.visibility=View.GONE
                           tvRateTrainer.visibility=View.GONE
                       }
                       tvTrainingPrefernce.text = jsonObjectData.optString("trainingPrefernce")
                       dataLayout.visibility = View.VISIBLE
                       trainerRatingAlert()
                       feebbackAlert()
                       getAddressData()

                   }else{
                       nested.visibility= View.GONE
                       linearpay.visibility= View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(selectAddress)
    }
    override fun onResume() {
        super.onResume()
        getAddressData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(selectAddress, IntentFilter("notificationselectAddress"),
                RECEIVER_EXPORTED
            )
        }else{
            registerReceiver(selectAddress, IntentFilter("notificationselectAddress"))
        }
    }
    val selectAddress = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            address_id=""+intent!!.getStringExtra("address_id")
            addresstext= intent.getStringExtra("address_name").toString()
            tvAddress.text = addresstext

            checkType=""+intent!!.getStringExtra("typeselect")
            Log.e("address_idBroadcast",address_id)
            if (address_id.equals("")){
                cardChangeAddress.setCardBackgroundColor(resources.getColor(R.color.progress_track_color_1))
                textAddress.setTextColor(getColor(R.color.subheadingcolor))


            }else{
                cardChangeAddress.setCardBackgroundColor(resources.getColor(R.color.white))
                textAddress.setTextColor(getColor(R.color.buttontextcolor))


            }

        }
    }
    /*private fun sendBookingData() {
        val param: MutableMap<String, String> = HashMap()
        if (intent.getStringExtra("selectBookOption").equals("normalBookSlot")){
            param["is_package"] = ""
            param["package_type"] = ""
            param["date"] = ""
            param["end_date"] = ""
            param["sessions"] = ""
            param["price"] = ""
            param["days"] = ""
        }else{
            param["is_package"] = "1"
            param["package_type"] = ""+sharedPreferences.getInt("selectedPackageType",0)
            param["date"] = ""+ intent.getStringExtra("apistart_date")
            param["end_date"] =""+ intent.getStringExtra("apiend_date")
            param["sessions"] =""+ intent.getStringExtra("session_value")
            param["price"] = ""+ intent.getStringExtra("price")
            param["days"] = ""+ intent.getStringExtra("days")
        }
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            param["studio_id"] =""
            param["type"] = ""+sharedPreferences.getString("typeWorkout","")
            param["address_id"] = ""+ intent.getStringExtra("address_id")
        }else{
            param["studio_id"] =""+ intent.getStringExtra("studio_id")
            param["type"] = "gym"
            param["address_id"] = ""
        }
        param["trainer_id"] =""+ intent.getStringExtra("trainer_id")
        param["slot_id"] = ""+ intent.getStringExtra("slot_id")
        param["transaction_id"] = ""+ intent.getStringExtra("transaction_id")
        param["payment_type"] = ""+ intent.getStringExtra("selectedPaymentOption")


        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        Log.e("bookSLotParam", param.toString())

        PostMethod(ApiURL.bookslot,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("BookingConfirmResp",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        animUpDown?.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(p0: Animation?) {
                                lifecycleScope.launch {
                                    delay(500)
                                    header.visibility= View.VISIBLE


                                    delay(1370L)
                                    detailLL.visibility= View.VISIBLE
                                    detailLL.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_top))

                                    mediaPlayer?.start()
                                }
                            }

                            override fun onAnimationRepeat(p0: Animation?) {
                            }

                            override fun onAnimationEnd(p0: Animation?) {

                            }
                        })
                        header.startAnimation(animUpDown)

                        sharedPreferences.edit().remove("typeWorkout").apply()
                        tvPackage.text = resp.optJSONObject("data")!!.optString("package")
                        tvTime.text = resp.optJSONObject("data")!!.optString("timing")
                        tvLocation.text = resp.optJSONObject("data")!!.optString("location")
                        tvStartDate.text = resp.optJSONObject("data")!!.optJSONObject("date").optString("start_date")
                        tvValid.text = resp.optJSONObject("data")!!.optJSONObject("date").optString("valid_till")
                        tvTrainerDetail.text =
                            resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")
                        Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optString("qr")).fitCenter().into(imQr)
                        Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).fitCenter().into(imTrainer)
                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                        {
                            var activityModel=ActivityModel()
                            activityModel.name=resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter(applicationContext, activitiesModelList)
                        recyclerActivity.adapter = activityAdapter

                        qrWebView.getSettings().loadWithOverviewMode = true
                        qrWebView.getSettings().useWideViewPort = true
                        qrWebView.getSettings().builtInZoomControls = true
                        qrWebView.getSettings().displayZoomControls = false
                        qrWebView.loadUrl(resp.optJSONObject("data")!!.optString("qr"))
                    }
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"), Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })
    }*/
    private fun getAddressData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        GetMethod(ApiURL.getaddress,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                addressArrayList.clear()
                Log.e("getAddressResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        val jsonArray=jsonObj.optJSONArray("data")

                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                val jsonObject1=jsonArray.optJSONObject(i)
                                val addressModel= BreakFastListModel()

                                addressModel.building_name=jsonObject1.optString("building_name")
                                addressModel.street=jsonObject1.optString("street")
                                addressModel.landmark=jsonObject1.optString("landmark")
                                addressModel.type=jsonObject1.optString("type")
                                addressModel.city_id=jsonObject1.optString("city_id")
                                addressModel.country_id=jsonObject1.optString("country_id")
                                addressModel.mobile_no=jsonObject1.optString("mobile_no")
                                addressModel.country_name=jsonObject1.optString("country_name")
                                addressModel.city_name=jsonObject1.optString("city_name")
                                addressModel.lat=jsonObject1.optString("lat")
                                addressModel.long=jsonObject1.optString("long")
                                addressModel.id=jsonObject1.optString("id")
                                addressModel.name=jsonObject1.optString("name")

                                addressArrayList.add(addressModel)

                            }
                            if (addressBottomSheetDialog.isShowing) {
                                // BottomSheet is open
                                addressAdapter.notifyDataSetChanged()
                                /*addressBottomSheetDialog.dismiss()
                                createSelectDeliveryAddress(true)*/
                            } else {
                                createSelectDeliveryAddress(false)
                            }
                        }else{
                            tvSaveAddress.visibility=View.GONE
                            recycleraddresslist.visibility=View.GONE
                        }
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