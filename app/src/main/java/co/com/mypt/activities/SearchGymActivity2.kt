package co.com.mypt.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.SearchGymAdapter
import co.com.mypt.model.GymModel
import com.android.volley.VolleyError
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.Locale

class SearchGymActivity2 : AppCompatActivity() {
    var gymModelList :ArrayList<GymModel> = ArrayList()
    lateinit var searchName:TextInputEditText
    lateinit var tv: TextView
    lateinit var linearGymNearme: LinearLayout
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
    lateinit var recycleSearch:RecyclerView
    lateinit var imBack:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_gym2)
        linearGymNearme=findViewById(R.id.linearGymNearme)
        searchName=findViewById(R.id.searchName)
        tv=findViewById(R.id.tv)
        recycleSearch=findViewById(R.id.recycleSearch)
        imBack=findViewById(R.id.imBack)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBVhnjhV5pynJXnSN2VTn-zhLGeIc7VcRw");
        }
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        linearGymNearme.setOnClickListener{
            finish()
        }
        imBack.setOnClickListener{
            finish()
        }
    }
    private fun getgymList(latitude: Double, longitude: Double) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        Log.e("gymListApi",""+ ApiURL.getTrainer+0+"&tag_id="+0+"&type="+"gym"+"&long="+ longitude +"&lat="+ latitude)
        GetMethod(ApiURL.getTrainer+0+"&tag_id="+0+"&type="+"gym"+"&long="+ longitude +"&lat="+latitude,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                gymModelList.clear()
                Log.e("getGymListResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayList=jsonObj.optJSONObject("data").optJSONArray("studios")
                        if (jsonArrayList.length()>0){
                            for(i in 0 until jsonArrayList.length()){
                                var jsonObject1 = jsonArrayList.optJSONObject(i)
                                val GymModel = GymModel()
                                GymModel.name = jsonObject1.optString("name")
                                GymModel.tag = jsonObject1.optString("tag")
                                GymModel.id = jsonObject1.optString("id")
                                GymModel.distance = jsonObject1.optString("distance")
                                GymModel.slot = jsonObject1.optString("slot")
                                GymModel.noOfRating = jsonObject1.optString("noOfRating")
                                GymModel.averageRating = jsonObject1.optString("averageRating")
                                GymModel.profile = jsonObject1.optString("profile")
                                GymModel.location = jsonObject1.optString("location")
                                GymModel.timing = jsonObject1.optString("timing")
                                GymModel.activity = jsonObject1.optJSONArray("activity")
                                gymModelList.add(GymModel)
                            }

                            var searchgymAdapter= SearchGymAdapter(applicationContext,gymModelList,"gym",latitude,longitude)
                            recycleSearch.adapter=searchgymAdapter

                            searchName.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

//On text changed in Edit text start filtering the list

                                    /*
                                                            if (hospitalAdapter != null) {
                                                                hospitalAdapter.filter(charSequence.toString())
                                                                if (hospitalAdapter.itemCount == 0) {
                                                                    HospitalrecycleSearch.visibility = View.GONE
                                                                    tv.visibility = View.VISIBLE
                                                                    return
                                                                }
                                                                tv.visibility = View.GONE
                                                                HospitalrecycleSearch.visibility = View.VISIBLE
                                                            }
                                    */
                                    searchgymAdapter.filter(charSequence.toString())
                                    if (searchName.text.toString().trim() != "") {
                                        if (searchgymAdapter.itemCount == 0) {
                                            recycleSearch.visibility = View.GONE
                                            tv.visibility = View.VISIBLE
                                            return
                                        }
                                        tv.visibility = View.GONE
                                        recycleSearch.visibility = View.VISIBLE
                                    }else{
                                        tv.visibility = View.GONE
                                        recycleSearch.visibility = View.VISIBLE

                                    }

                                }

                                override fun afterTextChanged(editable: Editable) {}
                            })


                        }else{

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
                               // tvLocation.text = address

                            }, 500)

                            getgymList(latitude!!, longitude!!)



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

}