package co.com.mypt.BookingScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import co.com.mypt.adapter.CompleteBookingExerciseAdapter
import co.com.mypt.model.CompleteModel
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

class CompletedBookingDetailActivity : AppCompatActivity() {
    lateinit var recyclerExercise:RecyclerView
    var completeModelList :ArrayList<CompleteModel> = ArrayList()
    lateinit var rateBottomSheetDialog:BottomSheetDialog
    lateinit var feedbackBottomSheetDialog:BottomSheetDialog
    lateinit var tvRateTrainer:TextView
    lateinit var tvDateTime:TextView
    lateinit var tvSessionType:TextView
    lateinit var tvTrainerName:TextView
    lateinit var tvdistance:TextView
    lateinit var tvLocation:TextView
    lateinit var tvTrainerLocation:TextView
    lateinit var avgRating:TextView
    lateinit var tvCost:TextView
    lateinit var imTrainer:ImageView
    lateinit var nested:NestedScrollView
    lateinit var headerLayout: LinearLayout
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null
    var isResumed1 = true
    var isclick=0
    var showProgress = false
    var trainerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_booking)
        rateBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        feedbackBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        recyclerExercise=findViewById(R.id.recyclerExercise)
        tvRateTrainer=findViewById(R.id.tvRateTrainer)
        tvDateTime=findViewById(R.id.tvDateTime)
        tvSessionType=findViewById(R.id.tvSessionType)
        tvTrainerName=findViewById(R.id.tvTrainerName)
        tvdistance=findViewById(R.id.tvdistance)
        tvLocation=findViewById(R.id.tvLocation)
        tvTrainerLocation=findViewById(R.id.tvTrainerLocation)
        avgRating=findViewById(R.id.avgRating)
        tvCost=findViewById(R.id.tvCost)
        nested=findViewById(R.id.nested)
        imTrainer=findViewById(R.id.imTrainer)
        headerLayout=findViewById(R.id.headerLayout)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        headerLayout.setOnClickListener{
            finish()
        }

        for (i in 0..2) {
            var completeModel= CompleteModel()
            completeModel.name="Exercise ${i+1}"
            completeModelList.add(completeModel)
        }
        var activityAdapter = CompleteBookingExerciseAdapter(applicationContext, completeModelList)
        recyclerExercise.adapter = activityAdapter
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

    private fun feebbackAlert() {

        val bottomSheet = layoutInflater.inflate(R.layout.feedback_alert_layout, null)
        var tvdone=bottomSheet.findViewById<TextView>(R.id.tvdone)
        tvdone.setOnClickListener{
            feedbackBottomSheetDialog.dismiss()
        }
        feedbackBottomSheetDialog.setContentView(bottomSheet)
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
                Toast.makeText(this@CompletedBookingDetailActivity,"Please give rating to continue", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            trainerRating(feedback.text.toString(), rating.rating,feedback)
            rateBottomSheetDialog.dismiss()
        }
        rateBottomSheetDialog.setContentView(bottomSheet)

    }

    private fun trainerRating(feedback: String, rating: Float, feedbackEt: EditText) {
        val param: MutableMap<String, String> = HashMap()
        param["trainer_id"] = trainerId
        param["booking_id"] = intent.getStringExtra("bookingid").toString()
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
                    }else{
                        rateBottomSheetDialog.show()
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
                            progressDialog.dismiss()
                            showProgress = false
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
        if (intent.getStringExtra("type").equals("home")){
            api= ApiURL.booking_detail+intent.getStringExtra("bookingid")+"&long="+""+"&lat="+""
        }else{
            api= ApiURL.booking_detail+intent.getStringExtra("bookingid")+"&long="+longitude+"&lat="+latitude

        }
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
                        var jsonObjectData=jsonObj.optJSONObject("data")
                      //  tvDateTime.setText(jsonObjectData.optString("booked_at"))
                        // cancellationMsg.setText(jsonObjectData.optString("cancellationPolicyMsg"))
                        tvSessionType.text = jsonObjectData.optJSONObject("bookingDetail").optString("type")+" Session"
                        tvLocation.text = jsonObjectData.optJSONObject("bookingDetail").optString("location")
                        tvDateTime.text = jsonObjectData.optJSONObject("bookingDetail").optString("training_date")
                        tvCost.text = jsonObjectData.optJSONObject("bookingDetail").optString("price")

                        trainerId = jsonObjectData.optJSONObject("trainerDetail").optString("trainer_id")
                        tvTrainerName.text = jsonObjectData.optJSONObject("trainerDetail").optString("name")
                        tvdistance.text = jsonObjectData.optJSONObject("trainerDetail").optString("distance")
                        tvTrainerLocation.text = jsonObjectData.optJSONObject("trainerDetail").optString("location")
                        avgRating.text = jsonObjectData.optJSONObject("trainerDetail").optString("averageRating")
                        Glide.with(applicationContext).load(jsonObjectData.optJSONObject("trainerDetail").optString("profile")).fitCenter()
                            .error(R.drawable.dummy_trainer).into(imTrainer)
                        trainerRatingAlert()
                        feebbackAlert()
                    }else{
                        nested.visibility= View.GONE
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