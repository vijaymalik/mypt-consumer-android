package co.com.mypt.Notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.com.mypt.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

class SelectNotificationAddressActivity : AppCompatActivity() , OnMapReadyCallback {
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
    lateinit var  mMap: GoogleMap
    lateinit var edaddress: EditText
    lateinit var tvlocation: TextView
    lateinit var stateCountry: TextView
    lateinit var currentLoc: ImageView
    lateinit var tvContinue:TextView
    lateinit var linearheader:LinearLayout
    lateinit var addressBottomSheetDialog: BottomSheetDialog
    var type=""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select_notification_address)
        edaddress=findViewById(R.id.edaddress)
        tvlocation=findViewById(R.id.tvlocation)
        stateCountry=findViewById(R.id.stateCountry)
        currentLoc=findViewById(R.id.currentLoc)
        tvContinue=findViewById(R.id.tvContinue)
        linearheader=findViewById(R.id.linearheader)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map1) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val apiKey = "AIzaSyBcjdk3tch99jhgrQx2miMW3xdRW9By8Vc"

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()

        }

        linearheader.setOnClickListener{
            finish()
        }
        currentLoc.setOnClickListener {
            if (checkLocationPermission()) {
                isclick = 0
                isResumed1 = true
                getCurrentLocation()
            }
        }
        edaddress.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                isclick=1
                val fields: List<Place.Field> = listOf(
                    Place.Field.ID,
                    Place.Field.DISPLAY_NAME,
                    Place.Field.FORMATTED_ADDRESS,
                    Place.Field.LOCATION
                )
                // Start the autocomplete intent.
                val intent: Intent =
                    Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN,
                        fields
                    ) //NIGERIA
                        .build(this)
                resultLauncher.launch(intent)
                //startActivityForResult(intent, 3)
            }
            true
        }
        addressBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        tvContinue.setOnClickListener{
           var intent=Intent(this@SelectNotificationAddressActivity, NotificationAddressFormActivity::class.java)
            intent.putExtra("latitude",latitude)
            intent.putExtra("longitude",longitude)
            intent.putExtra("location",tvlocation.text.toString())
            startActivity(intent)
            finish()
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(result.data)

                Log.i("ADDS", "Place: " + place.displayName + ", " + place.id + ", " + place.formattedAddress)
                // Toast.makeText(Signup.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                longitude = place.location?.longitude
                latitude = place.location?.latitude

                val latlng=LatLng(latitude!!, longitude!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15.0f))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f))

            }
            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(result.data)
                Toast.makeText(
                    this,
                    "Error: " + status.statusMessage,
                    Toast.LENGTH_LONG
                ).show()
                Log.i("ADDS", status.statusMessage!!)
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
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
                    val builder = AlertDialog.Builder(this@SelectNotificationAddressActivity)
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
        val alertDialogBuilder = AlertDialog.Builder(this@SelectNotificationAddressActivity)
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

                            /*tvlocation.text = address
                            edaddress.setText(address)*/
                            if(isclick == 0){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15.0f))
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f))
                            }
                            onMapCameraReady()

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

    private fun onMapCameraReady() {
        mMap.setOnCameraIdleListener { //get latlng at the center by calling
            val midLatLng: LatLng = mMap.cameraPosition.target
            latitude = midLatLng.latitude
            longitude = midLatLng.longitude
            var addresses: List<Address>? = null
            var address = ""
            var cityState = ""
            var streetname = ""
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(
                        latitude!!, longitude!!, 1
                    ) { addressList: List<Address?>? ->
                        address = addressList!![0]!!.getAddressLine(0)

                        if (addressList!![0]!!.subLocality.equals("null") && addressList!![0]!!.locality.equals("null")){
                            cityState=""
                        }else if (addressList!![0]!!.locality.equals("null") && !addressList!![0]!!.subLocality.equals("null")){
                            cityState = addressList!![0]!!.subLocality

                        }else if (!addressList!![0]!!.locality.equals("null") && addressList!![0]!!.subLocality.equals("null")){
                            cityState = addressList!![0]!!.locality

                        }else{
                            cityState = addressList!![0]!!.subLocality +", "+ addressList!![0]!!.locality

                        }


                    }
                } else {
                    addresses =
                        geocoder.getFromLocation(latitude!!, longitude!!, 1)!!
                    address = addresses[0].getAddressLine(0)

                    if (addresses!![0]!!.subLocality.equals("null") && addresses!![0]!!.locality.equals("null")){
                        cityState=""
                        streetname=""
                    }else if (addresses!![0]!!.locality.equals("null") && !addresses!![0]!!.subLocality.equals("null")){
                        cityState = addresses!![0]!!.subLocality
                        streetname = addresses!![0]!!.subLocality

                    }else if (!addresses!![0]!!.locality.equals("null") && addresses!![0]!!.subLocality.equals("null")){
                        cityState = addresses!![0]!!.locality

                    }else{
                        cityState = addresses!![0]!!.subLocality +", "+ addresses!![0]!!.locality
                    }


                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                tvlocation.text = address
                stateCountry.text = cityState
                Log.e("address",address)
                Log.e("citystate",cityState)
               // tvBuildingName.setText(address)
               // tvStreetName.setText(streetname)
                //edaddress.setText(address)
                //  viewModel.data.value  = "${tvlocation.text},${stateCountry.text}-$latitude-$longitude"
            }, 500)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map))
        getCurrentLocation()


//        mMap.isMyLocationEnabled = true

        /* mMap.setOnMapClickListener {
             val fragmentManager =
                 requireActivity()!!.supportFragmentManager
             fragmentManager.beginTransaction().replace(R.id.flFragment, MapFragment())
                 .addToBackStack("w").commit()
         }*/
    }
}