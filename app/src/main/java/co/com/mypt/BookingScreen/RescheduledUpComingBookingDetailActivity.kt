package co.com.mypt.BookingScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
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
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.model.ActivityModel
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


class RescheduledUpComingBookingDetailActivity : AppCompatActivity() {
    lateinit var cancelBottomSheetDialog:BottomSheetDialog
    lateinit var tvAccept:TextView
    lateinit var tvSession:TextView
    lateinit var tvDecline:TextView
    lateinit var lineaReschedule:LinearLayout
    lateinit var headerLayout:LinearLayout
    lateinit var linearFindTrainer:LinearLayout
    lateinit var tvLearn:TextView

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

    lateinit var tvTrainer_name:TextView
    lateinit var home_workout:TextView
    lateinit var tvBookingDate:TextView
    lateinit var tvLocation:TextView
    lateinit var tvdistance:TextView
    lateinit var avgRating:TextView
    lateinit var cancellationMsg:TextView
    lateinit var tvamountPaid:TextView
    lateinit var tvTrainingLocation:TextView
    lateinit var tvTrainingDate:TextView
    lateinit var nested: NestedScrollView
    lateinit var tvContact:TextView
    lateinit var tvFindTrainer:TextView
    lateinit var imTrainer: ImageView
    lateinit  var linearQRCode : LinearLayout
    lateinit var qrBottomSheetDialog:BottomSheetDialog
    lateinit var  im:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rescheduled_up_coming_booking_detail)

        cancelBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        qrBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        tvLearn=findViewById(R.id.tvLearn)
        linearQRCode = findViewById(R.id.linearQRCode)
        tvAccept=findViewById(R.id.tvAccept)
        tvFindTrainer=findViewById(R.id.tvFindTrainer)
        tvDecline=findViewById(R.id.tvDecline)
        tvSession=findViewById(R.id.tvSession)
        headerLayout=findViewById(R.id.headerLayout)
        lineaReschedule=findViewById(R.id.lineaReschedule)
        linearFindTrainer=findViewById(R.id.linearFindTrainer)
        nested=findViewById(R.id.nested)

        home_workout = findViewById(R.id.home_workout)
        tvLocation = findViewById(R.id.tvLocation)
        tvBookingDate = findViewById(R.id.tvBookingDate)
        tvTrainer_name = findViewById(R.id.tvTrainer_name)
        tvdistance = findViewById(R.id.tvdistance)
        avgRating = findViewById(R.id.avgRating)
        tvamountPaid = findViewById(R.id.tvamountPaid)
        tvTrainingLocation = findViewById(R.id.tvTrainingLocation)
        cancellationMsg = findViewById(R.id.cancellationMsg)
        tvTrainingDate = findViewById(R.id.tvTrainingDate)
        imTrainer = findViewById(R.id.imTrainer)
        tvContact = findViewById(R.id.tvContact)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        tvLearn.setPaintFlags(tvLearn.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        createLearnMoreAlert()
        bookingQrAlert()

        tvLearn.setOnClickListener{
            cancelBottomSheetDialog.show()
        }

        linearQRCode.setOnClickListener {
            qrBottomSheetDialog.show()
        }
        headerLayout.setOnClickListener{
          finish()
        }
        tvAccept.setOnClickListener{
            doAccept("1")

        }
        tvDecline.setOnClickListener{
            doReject("2")
        }
        tvFindTrainer.setOnClickListener{
            var intent= Intent(applicationContext, HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun doReject(s: String) {
        val param: MutableMap<String, String> = HashMap()

        param["id"] = ""+getIntent().getStringExtra("bookingid")
        param["type"] = ""+s
        Log.e("rejectParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        PostMethod(ApiURL.accept_reject,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("rejectRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        lineaReschedule.visibility= View.GONE
                        linearFindTrainer.visibility= View.VISIBLE
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
    }
    private fun doAccept(s: String) {
        val param: MutableMap<String, String> = HashMap()

        param["id"] = ""+getIntent().getStringExtra("bookingid")
        param["type"] = ""+s
        Log.e("accpetParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        PostMethod(ApiURL.accept_reject,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("acceptRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        lineaReschedule.visibility= View.GONE
                        tvSession.visibility= View.VISIBLE
                        tvSession.setText("Your session has been successfully rescheduled to ${resp.optJSONObject("data").optString("msg")} We look forward to seeing you then!")
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
    }

    private fun createLearnMoreAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.cancelpolicy_bottomsheet_dialog, null)
        cancelBottomSheetDialog.setContentView(bottomSheet)

    }
    private fun checkLocationPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.setData(Uri.parse("package:" + this.packageName))
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
                            longitude = location.longitude
                            latitude = location.latitude
                            val latlng = LatLng(latitude!!, longitude!!)
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

        android.util.Log.e("getbookingDetailApi",""+api)

        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                android.util.Log.e("getBookingResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        nested.visibility=View.VISIBLE
                        linearQRCode.visibility=View.VISIBLE

                        var jsonObjectData=jsonObj.optJSONObject("data")
                        tvBookingDate.setText(jsonObjectData.optString("booked_at"))
                        cancellationMsg.setText(jsonObjectData.optString("msg"))
                        home_workout.setText(jsonObjectData.optJSONObject("bookingDetail").optString("type")+" Workout")

                        tvContact.setText(jsonObjectData.optJSONObject("bookingDetail").optString("contact"))
                        tvamountPaid.setText(jsonObjectData.optJSONObject("bookingDetail").optString("price"))
                        tvTrainingLocation.setText(jsonObjectData.optJSONObject("bookingDetail").optString("location"))
                        tvTrainingDate.setText(jsonObjectData.optJSONObject("bookingDetail").optString("training_date"))

                        tvTrainer_name.setText(jsonObjectData.optJSONObject("trainerDetail").optString("name"))
                        tvdistance.setText(jsonObjectData.optJSONObject("trainerDetail").optString("distance"))
                        tvLocation.setText(jsonObjectData.optJSONObject("trainerDetail").optString("location"))
                        avgRating.setText(jsonObjectData.optJSONObject("trainerDetail").optString("averageRating"))
                        Glide.with(applicationContext).load(jsonObjectData.optJSONObject("bookingDetail").optString("qr")).fitCenter().into(im)

                        Glide.with(applicationContext).load(jsonObjectData.optJSONObject("trainerDetail").optString("profile")).fitCenter().error(R.drawable.dummy_trainer).
                        placeholder(R.drawable.dumbbell).into(imTrainer)
                        if (jsonObjectData.optString("is_declined").equals("true")){
                            linearFindTrainer.visibility=View.VISIBLE
                        }
                        if (jsonObjectData.optString("is_declined").equals("false") && jsonObjectData.optString("can_cancel_request").equals("false")){
                            lineaReschedule.visibility=View.VISIBLE
                        }

                    }else{
                        linearQRCode.visibility=View.GONE
                        nested.visibility=View.GONE

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
    private fun bookingQrAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.booking_qrcode_layout, null)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        val imclose=bottomSheet.findViewById<ImageView>(R.id.close)

        im=bottomSheet.findViewById<ImageView>(R.id.im)
        imclose.setOnClickListener{
            qrBottomSheetDialog.dismiss()
        }

        qrBottomSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(qrBottomSheetDialog)
    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
}