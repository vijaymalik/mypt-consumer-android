package co.com.mypt.UpComingClasses

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.GlimpseofOurCLassesAdapter
import co.com.mypt.adapter.LookbyCategoryAdapter
import co.com.mypt.adapter.NearUpcomingClassAdapter
import co.com.mypt.adapter.ResourceAdapter
import co.com.mypt.model.GlimpseCLassesModel
import co.com.mypt.model.LookbyCategoryModel
import co.com.mypt.model.NearUpcomingCLassModel
import co.com.mypt.model.ResourceModel
import co.com.mypt.utils.CarouselRecyclerview
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

class UpComingClassActivity : AppCompatActivity() {
    lateinit var recyclerUpcomingClassNearYou:RecyclerView
    lateinit var recyclerLookbyCategory:RecyclerView
    lateinit var recyclerResource:RecyclerView
    lateinit var linearHeader:LinearLayout
    var upcomingClassesModelList :ArrayList<NearUpcomingCLassModel> = ArrayList()
    var resourceModelList :ArrayList<ResourceModel> = ArrayList()
    var LookbyCategoryModelList :ArrayList<LookbyCategoryModel> = ArrayList()
    lateinit var tvNear:TextView
    lateinit var tvGlimpse:TextView
    lateinit var tvLookByCategory:TextView
    lateinit var tvResources:TextView
    lateinit var linearViewALlLook:LinearLayout
    lateinit var nested:NestedScrollView
    lateinit var carouselRecyclerview: CarouselRecyclerview
    lateinit var glimpseofOurClasses: GlimpseofOurCLassesAdapter
    var glimpseOfClassesArrayList = ArrayList<GlimpseCLassesModel>()
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
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_up_coming_class)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)

        linearHeader=findViewById(R.id.linearHeader)
        recyclerUpcomingClassNearYou=findViewById(R.id.recyclerUpcomingClassNearYou)
        recyclerLookbyCategory=findViewById(R.id.recyclerLookbyCategory)
        recyclerResource=findViewById(R.id.recyclerResource)
        tvNear=findViewById(R.id.tvNear)
        tvResources=findViewById(R.id.tvResources)
        tvGlimpse=findViewById(R.id.tvGlimpse)
        linearViewALlLook=findViewById(R.id.linearViewALlLook)
        nested=findViewById(R.id.nested)
        tvLookByCategory=findViewById(R.id.tvLookByCategory)
        carouselRecyclerview = findViewById(R.id.carouselRecyclerview)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        linearHeader.setOnClickListener{
            finish()
        }

        tvNear.setOnClickListener{
            var intent= Intent(applicationContext,Upcoming_classes_near_youActivity::class.java)
            startActivity(intent)
        }
        tvResources.setOnClickListener{
            var intent= Intent(applicationContext,AllResourcesActivity::class.java)
            startActivity(intent)
        }
        linearViewALlLook.setOnClickListener{
            var intent= Intent(applicationContext,ClassCategoryActivity::class.java)
            startActivity(intent)
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

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                              //  tvLocation.text = address

                            }, 500)
                            getClasses(latitude!!, longitude!!)

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

    }
    private fun getClasses(latitude: Double, longitude: Double) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
        api= ApiURL.upcoming_classes+latitude+"&long="+longitude

        Log.e("UpcomingclassUrl",api)
        GetMethod(api
            ,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("UpcomingResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        nested.visibility=View.VISIBLE
                        var jsonArrayAllClassess=jsonObj.optJSONObject("data").optJSONArray("allClasses")
                        var jsonArrayClassVideos=jsonObj.optJSONObject("data").optJSONArray("allClassVideos")
                        var jsonArrayResourcesVideos=jsonObj.optJSONObject("data").optJSONArray("resources")
                        var jsonArrayCategory=jsonObj.optJSONObject("data").optJSONArray("classesWithCategory")
                        if (jsonArrayAllClassess.length()>0){
                            tvNear.visibility=View.VISIBLE
                            recyclerUpcomingClassNearYou.visibility=View.VISIBLE
                            for(i in 0 until jsonArrayAllClassess.length()){
                                var json=jsonArrayAllClassess.optJSONObject(i)
                                var nearUpcomingCLassModel= NearUpcomingCLassModel()
                                nearUpcomingCLassModel.cla_ss=json.optString("class")
                                nearUpcomingCLassModel.id=json.optString("id")
                                nearUpcomingCLassModel.image=json.optString("image")
                                nearUpcomingCLassModel.status=json.optString("status")
                                nearUpcomingCLassModel.location=json.optString("location")
                                nearUpcomingCLassModel.type=json.optString("type")
                                nearUpcomingCLassModel.time=json.optString("time")
                                nearUpcomingCLassModel.price=json.optString("price")
                                nearUpcomingCLassModel.trained_by=json.optString("trained_by")
                                nearUpcomingCLassModel.trainer_image=json.optString("trainer_image")
                                nearUpcomingCLassModel.studio_name=json.optString("studio_name")
                                nearUpcomingCLassModel.schedule_id=json.optString("schedule_id")
                                upcomingClassesModelList.add(nearUpcomingCLassModel)
                            }
                            var nearUpcomingClassAdapter = NearUpcomingClassAdapter(applicationContext, upcomingClassesModelList,latitude,longitude)
                            recyclerUpcomingClassNearYou.adapter = nearUpcomingClassAdapter
                            tvNear.visibility= View.VISIBLE
                            recyclerUpcomingClassNearYou.visibility= View.VISIBLE
                        }else{
                            tvNear.visibility= View.GONE
                            recyclerUpcomingClassNearYou.visibility= View.GONE
                        }

                        if (jsonArrayClassVideos.length()>0){
                            tvGlimpse.visibility=View.VISIBLE
                            carouselRecyclerview.visibility=View.VISIBLE
                            for(i in 0 until jsonArrayClassVideos.length()){
                                var glimpseCLassesModel= GlimpseCLassesModel()
                                // glimpseCLassesModel.video="android.resource://" + applicationContext!!.packageName + "/" + R.raw.launcher_video
                                glimpseCLassesModel.video=jsonArrayClassVideos.get(i).toString()
                                glimpseOfClassesArrayList.add(glimpseCLassesModel)
                            }
                            glimpseofOurClasses = GlimpseofOurCLassesAdapter(applicationContext, glimpseOfClassesArrayList)
                            carouselRecyclerview.adapter = glimpseofOurClasses
                            tvGlimpse.visibility=View.VISIBLE
                            carouselRecyclerview.visibility=View.VISIBLE

                        }else{
                            tvGlimpse.visibility=View.GONE
                            carouselRecyclerview.visibility=View.GONE
                        }
                        if (jsonArrayResourcesVideos.length()>0){
                            tvResources.visibility=View.VISIBLE
                            recyclerResource.visibility=View.VISIBLE
                            for(i in 0 until jsonArrayResourcesVideos.length()){
                                var jsonResource=jsonArrayResourcesVideos.optJSONObject(i)
                                var resourceModel= ResourceModel()
                                resourceModel.title=jsonResource.optString("title")
                                resourceModel.image=jsonResource.optString("image")
                                resourceModel.description=jsonResource.optString("description")
                                resourceModel.date=jsonResource.optString("date")
                                resourceModel.reading_time=jsonResource.optString("reading_time")

                                resourceModelList.add(resourceModel)
                            }
                            var resourceAdapter = ResourceAdapter(applicationContext, resourceModelList)
                            recyclerResource.adapter = resourceAdapter
                            tvResources.visibility=View.VISIBLE
                            recyclerResource.visibility=View.VISIBLE

                        }else{
                            tvResources.visibility=View.GONE
                            recyclerResource.visibility=View.GONE

                        }
                        if (jsonArrayCategory.length()>0){
                            tvLookByCategory.visibility=View.VISIBLE
                            recyclerLookbyCategory.visibility=View.VISIBLE
                            for(i in 0 until jsonArrayCategory.length()){
                                var jsonCategory=jsonArrayCategory.optJSONObject(i)
                                var lookbyCategoryModel= LookbyCategoryModel()
                                lookbyCategoryModel.category_id=jsonCategory.optString("category_id")
                                lookbyCategoryModel.category_name=jsonCategory.optString("category_name")
                                lookbyCategoryModel.category_image=jsonCategory.optString("category_image")
                                lookbyCategoryModel.classCount=jsonCategory.optString("classCount")

                                LookbyCategoryModelList.add(lookbyCategoryModel)
                            }
                            var lookbyCategoryAdapter = LookbyCategoryAdapter(applicationContext, LookbyCategoryModelList)
                            recyclerLookbyCategory.adapter = lookbyCategoryAdapter
                            tvLookByCategory.visibility=View.VISIBLE
                            recyclerLookbyCategory.visibility=View.VISIBLE

                        }else{
                            tvLookByCategory.visibility=View.GONE
                            recyclerLookbyCategory.visibility=View.GONE

                        }
                        carouselRecyclerview.set3DItem(false)
                        carouselRecyclerview.setInfinite(false)
                        carouselRecyclerview.setAlpha(true)
                        carouselRecyclerview.setFlat(false)
                        carouselRecyclerview.setIsScrollingEnabled(true)
                        carouselRecyclerview.setIntervalRatio(0.8f)
                        //carouselRecyclerview.scrollToPosition(1)

                        carouselRecyclerview.startAnimation(
                            AnimationUtils.loadAnimation(
                                applicationContext,
                                R.anim.bottom_sheet_slide_up
                            )
                        )



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

}