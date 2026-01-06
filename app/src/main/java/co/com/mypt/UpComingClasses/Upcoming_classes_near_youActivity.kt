package co.com.mypt.UpComingClasses

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.FullNearUpcomingClassAdapter
import co.com.mypt.adapter.LookbyFullCategoryAdapter
import co.com.mypt.adapter.UpcomingClassesNearAdapter
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.FullNearUpcomingCLassModel
import co.com.mypt.model.LookbyFullCategoryModel
import com.android.volley.VolleyError
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.util.Locale

class Upcoming_classes_near_youActivity : AppCompatActivity() {
    lateinit var exerciseRecyclerView : RecyclerView
    lateinit var recyclerUpcomingClassNearYou : RecyclerView
    lateinit var recyclerLookbyCategory : RecyclerView
    lateinit var tvLocation : TextView
    lateinit var linearLook : LinearLayout
    lateinit var headerLayout : LinearLayout
    lateinit var nested : NestedScrollView
    lateinit var tv : TextView
    var exerciseList = ArrayList<ExerciseModel>()
    var fullupcomingClassesModelList :ArrayList<FullNearUpcomingCLassModel> = ArrayList()
    var LookbyFullCategoryModelList :ArrayList<LookbyFullCategoryModel> = ArrayList()
    var isResumed1 = true
    var isclick=0
    var showProgress = false
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null
    var filter="0"
    var tag_id="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming_classes_near_you)
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        recyclerUpcomingClassNearYou = findViewById(R.id.recyclerUpcomingClassNearYou)
        recyclerLookbyCategory = findViewById(R.id.recyclerLookbyCategory)
        tvLocation = findViewById(R.id.tvLocation)
        tv = findViewById(R.id.tv)
        headerLayout = findViewById(R.id.headerLayout)
        linearLook = findViewById(R.id.linearLook)
        nested = findViewById(R.id.nested)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        headerLayout.setOnClickListener{
            finish()
        }
        linearLook.setOnClickListener{
            var intent= Intent(applicationContext,ClassCategoryActivity::class.java)
            startActivity(intent)
        }
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }



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

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                tvLocation.text = address

                            }, 500)

                            getTagData(latitude!!, longitude!!)
                            getViewAllCategory(tag_id,filter)



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

    override fun onResume() {
        super.onResume()
        isResumed1 = true

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            showGPSDisabledAlertToUser()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(classTagList, IntentFilter("tagclass"), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(classTagList, IntentFilter("tagclass"))
        }

    }
    var classTagList: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                filter= intent.getStringExtra("filter")!!
                tag_id= intent.getStringExtra("tag_id")!!
                getViewAllCategory(tag_id,filter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getTagData(latitude: Double?, longitude: Double?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""

        api= ApiURL.viewallClassess+latitude+"&long="+longitude+"&is_filter="+"0"+"&category_id="+""

        Log.e("ViewAllClassesTagApi",api)
        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                exerciseList.clear()
                Log.e("getTagResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        nested.visibility=View.VISIBLE
                        var jsonArrayTag=jsonObj.optJSONObject("data").optJSONArray("allcategories")
                        val exerciseModel = ExerciseModel()
                        exerciseModel.name = "All Category"
                        exerciseModel.id ="0"
                        exerciseModel.icon =""
                        exerciseList.add(exerciseModel)
                        for(i in 0 until jsonArrayTag.length()){
                            var jsonObject1 = jsonArrayTag.optJSONObject(i)
                            val exerciseModel = ExerciseModel()
                            exerciseModel.name = jsonObject1.optString("name")
                            exerciseModel.id = jsonObject1.optString("id")
                            exerciseModel.icon = jsonObject1.optString("icon")
                            exerciseList.add(exerciseModel)
                        }
                        exerciseRecyclerView.adapter = UpcomingClassesNearAdapter(this@Upcoming_classes_near_youActivity,exerciseList)
                    }else{
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

    private fun getViewAllCategory(tag_id: String, filter: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.viewallClassess+latitude+"&long="+longitude+"&is_filter="+filter+"&category_id="+tag_id

        Log.e("ViewAllClassesApi",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                fullupcomingClassesModelList.clear()
                Log.e("ClassesWithCategoryResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    var jsonArrayAllClassess=jsonObj.optJSONObject("data").optJSONArray("allClasses")

                    var jsonArrayCategory=jsonObj.optJSONObject("data").optJSONArray("classesWithCategory")
                    if (jsonArrayAllClassess.length()>0){
                        for(i in 0 until jsonArrayAllClassess.length()){
                            var json=jsonArrayAllClassess.optJSONObject(i)
                            var fullNearUpcomingCLassModel= FullNearUpcomingCLassModel()
                            fullNearUpcomingCLassModel.cla_ss=json.optString("class")
                            fullNearUpcomingCLassModel.class_id=json.optString("class_id")
                            fullNearUpcomingCLassModel.schedule_id=json.optString("schedule_id")
                            fullNearUpcomingCLassModel.category_id=json.optString("category_id")
                            fullNearUpcomingCLassModel.image=json.optString("image")
                            fullNearUpcomingCLassModel.status=json.optString("status")
                            fullNearUpcomingCLassModel.location=json.optString("location")
                            fullNearUpcomingCLassModel.type=json.optString("type")
                            fullNearUpcomingCLassModel.studio_name=json.optString("studio_name")
                            fullNearUpcomingCLassModel.time=json.optString("time")
                            fullNearUpcomingCLassModel.price=json.optString("price")
                            fullNearUpcomingCLassModel.trained_by=json.optString("trained_by")
                            fullNearUpcomingCLassModel.trainer_image=json.optString("trainer_image")
                            fullNearUpcomingCLassModel.schedule_id=json.optString("schedule_id")

                            fullupcomingClassesModelList.add(fullNearUpcomingCLassModel)
                        }
                        var fullNearUpcomingCLassModel = FullNearUpcomingClassAdapter(applicationContext, fullupcomingClassesModelList,latitude,longitude)
                        recyclerUpcomingClassNearYou.adapter = fullNearUpcomingCLassModel
                        tv.visibility= View.GONE
                        recyclerUpcomingClassNearYou.visibility= View.VISIBLE
                    }else{
                        tv.visibility= View.VISIBLE
                        recyclerUpcomingClassNearYou.visibility= View.GONE
                    }


                    if (jsonArrayCategory.length()>0){
                        for(i in 0 until jsonArrayCategory.length()){
                            var jsonCategory=jsonArrayCategory.optJSONObject(i)
                            var lookbyFullCategoryModel= LookbyFullCategoryModel()

                            lookbyFullCategoryModel.category_id=jsonCategory.optString("category_id")
                            lookbyFullCategoryModel.category_name=jsonCategory.optString("category_name")
                            lookbyFullCategoryModel.category_image=jsonCategory.optString("category_image")
                            lookbyFullCategoryModel.classCount=jsonCategory.optString("classCount")

                            LookbyFullCategoryModelList.add(lookbyFullCategoryModel)
                        }
                        var lookbyCategoryAdapter = LookbyFullCategoryAdapter(applicationContext, LookbyFullCategoryModelList)
                        recyclerLookbyCategory.adapter = lookbyCategoryAdapter

                        linearLook.visibility= View.VISIBLE
                        recyclerLookbyCategory.visibility= View.VISIBLE

                    }else{
                        linearLook.visibility= View.GONE
                        recyclerLookbyCategory.visibility= View.GONE

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