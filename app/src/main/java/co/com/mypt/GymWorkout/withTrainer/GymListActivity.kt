package co.com.mypt.GymWorkout.withTrainer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withoutTrainer.GymValidityActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.SearchGymActivity2
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.adapter.GymListAdapter
import co.com.mypt.adapter.GymListExerciseAdapter
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel
import com.android.volley.VolleyError
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.json.JSONObject
import java.util.Locale
import kotlin.text.equals

class GymListActivity : AppCompatActivity() {
    lateinit var exerciseRecyclerView : RecyclerView
    lateinit var trainerRecyclerView : RecyclerView
    var trainerList = ArrayList<TrainersModel>()
    lateinit var im_search:ImageView
    var exerciseList = ArrayList<ExerciseModel>()
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
   // lateinit var context_ : Context
    lateinit var sharedPreferences: SharedPreferences
    var filter="0"
    var tag_id="0"
    lateinit var tvNodata : TextView
//    lateinit var tvgymCount : TextView
//    lateinit var tvLocation : TextView
//    lateinit var linearLocation : LinearLayout
    lateinit var back_1 : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_list)
       // context_ = this

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        trainerRecyclerView = findViewById(R.id.trainerRecyclerView)
        im_search = findViewById(R.id.im_search)
//        tvLocation = findViewById(R.id.tvLocation)
        tvNodata = findViewById(R.id.tvNodata)
//        tvgymCount = findViewById(R.id.tvgymCount)
        back_1 = findViewById(R.id.back_1)
//        linearLocation = findViewById(R.id.linearLocation)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Log.e("flowType",""+intent.getStringExtra("type"))
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBVhnjhV5pynJXnSN2VTn-zhLGeIc7VcRw");
        }
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        im_search.setOnClickListener{
            var intent= Intent(applicationContext,SearchGymActivity2::class.java)
            startActivity(intent)
        }
        back_1.setOnClickListener{
           finish()
        }
       /* linearLocation.setOnClickListener{
            val fields = listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LOCATION)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)

                .build(this)

            startAutocomplete.launch(intent)
        }*/
    }
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    Log.e("place",""+place)

                    latitude = place.location?.latitude
                    longitude = place.location?.longitude
                    Log.e("autolat",""+latitude)
                    Log.e("autolong",""+longitude)
//                    tvLocation.text = place.name
                    getTagData(latitude,longitude)
                    getgymList(tag_id,filter)

                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e("canceled", "User canceled autocomplete")
            }
        }


    private fun getTagData(latitude: Double?, longitude: Double?) {
        /*val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()*/
        Log.e("trainerTagListApi",""+ ApiURL.getTrainer+0+"&tag_id=0&type="+"gym"+"&long="+longitude+"&lat="+latitude)
        GetMethod(ApiURL.getTrainer+0+"&tag_id=0&type="+"gym"+"&long="+longitude+"&lat="+latitude,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                //progressDialog.dismiss()
                exerciseList.clear()
                Log.e("getGymTagResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayTag=jsonObj.optJSONObject("data").optJSONArray("tags")
                        val exerciseModel = ExerciseModel()
                        exerciseModel.name = "All Workouts"
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

                        exerciseRecyclerView.adapter = GymListExerciseAdapter(this@GymListActivity,exerciseList)

                    }


                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                //progressDialog.dismiss()
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
//                                tvLocation.text = address

                            }, 500)
                            getTagData(latitude,longitude)
                            getgymList(tag_id,filter)



                            mFusedLocationClient.removeLocationUpdates(locationCallback)
                            isResumed1 = false
                        }
                    }
                }
            }
        }
        /*mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )*/
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
            registerReceiver(trainerListBroadCast, IntentFilter("gymtag"), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(trainerListBroadCast, IntentFilter("gymtag"))
        }

        if(sharedPreferences.getString("tempaddress","") != "") {
            latitude = sharedPreferences.getString("templat", "0.0")!!.toDouble()
            longitude = sharedPreferences.getString("templong", "0.0")!!.toDouble()
//            address = "" + sharedPreferences.getString("tempaddress", "")

            sharedPreferences.edit().remove("templat").apply()
            sharedPreferences.edit().remove("templong").apply()
            sharedPreferences.edit().remove("tempaddress").apply()
            getTagData(latitude,longitude)
            getgymList(tag_id,filter)
        }
        }

    var trainerListBroadCast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                filter= intent.getStringExtra("filter")!!
                tag_id= intent.getStringExtra("tag_id")!!
                getgymList(tag_id,filter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getgymList(tag_id: String?, filter: String?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        Log.e("gymListApi",""+ApiURL.getTrainer+filter+"&tag_id="+tag_id+"&type="+"gym"+"&long="+longitude+"&lat="+latitude)
        GetMethod(ApiURL.getTrainer+filter+"&tag_id="+tag_id+"&type="+"gym"+"&long="+longitude+"&lat="+latitude,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                trainerList.clear()
                Log.e("getGymListResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayList=jsonObj.optJSONObject("data").optJSONArray("studios")
                        if (jsonArrayList.length()>0){
                            for(i in 0 until jsonArrayList.length()){
                                var jsonObject1 = jsonArrayList.optJSONObject(i)
                                val trainerModel = TrainersModel()
                                trainerModel.name = jsonObject1.optString("name")
                                trainerModel.tag = jsonObject1.optString("tag")
                                trainerModel.id = jsonObject1.optString("id")
                                trainerModel.distance = jsonObject1.optString("distance")
                                trainerModel.slot = jsonObject1.optString("slot")
                                trainerModel.noOfRating = jsonObject1.optString("noOfRating")
                                trainerModel.averageRating = jsonObject1.optString("averageRating")
                                trainerModel.profile = jsonObject1.optString("profile")
                                trainerModel.location = jsonObject1.optString("location")
                                trainerModel.timing = jsonObject1.optString("timing")
                                trainerModel.canMembership = jsonObject1.optString("canMembership")
                                trainerModel.canBook = jsonObject1.optString("canBook")
                                trainerModel.activity = jsonObject1.optJSONArray("activity")
                                trainerList.add(trainerModel)
                            }

                            trainerRecyclerView.adapter = GymListAdapter(this@GymListActivity,trainerList,intent.getStringExtra("type"),"gym",latitude,longitude){type,id->
                                if ((type.equals("withoutTrainer")) || (type.equals("withTrainer"))){
                                    if (type.equals("withoutTrainer")){
                                        val intent = Intent(this@GymListActivity, GymValidityActivity::class.java)
                                        intent.putExtra("studio_id",id)
                                        intent.putExtra("type","withoutTrainer")
                                        startActivity(intent)
                                    }else{
                                        val intent = Intent(this@GymListActivity, TrainersListActivity::class.java)
                                        intent.putExtra("type","withTrainer")
                                        intent.putExtra("studio_id",id)
                                        startActivity(intent)
                                    }
                                }else{
                                    val intent = Intent(this@GymListActivity, TrainersListActivity::class.java)
                                    if (sharedPreferences.getString("typeWorkout","").equals("work")){
                                        intent.putExtra("studio_id",id)
                                    }
                                    startActivity(intent)
                                }
                            }

                            trainerRecyclerView.visibility= View.VISIBLE
                            tvNodata.visibility= View.GONE

//                            tvgymCount.setText("${trainerList.size} Gyms Near You")

                        }else{
//                            tvgymCount.setText("No Gyms Near You")

                            trainerRecyclerView.visibility= View.GONE
                            tvNodata.visibility= View.VISIBLE
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