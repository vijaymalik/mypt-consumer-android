package co.com.mypt.onBoarding.personalize

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import co.com.mypt.R
import co.com.mypt.onBoarding.PersonalizedActivity2
import co.com.mypt.utils.SharedLocationViewModel
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
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Locale

class LocationSelectFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: SharedLocationViewModel by activityViewModels()
    var isFirstLocationUpdate = true
    private var selectedLatLng: LatLng? = null
    private var currentMarker: Marker? = null

    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    lateinit var mMap: GoogleMap
    lateinit var edaddress: EditText
    lateinit var tvlocation: TextView
    lateinit var stateCountry: TextView
    lateinit var currentLoc: ImageView

    private var longitude: Double? = null
    private var latitude: Double? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location_select, container, false)
        edaddress = view.findViewById(R.id.edaddress)
        tvlocation = view.findViewById(R.id.tvlocation)
        stateCountry = view.findViewById(R.id.stateCountry)
        currentLoc = view.findViewById(R.id.currentLoc)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.refreshLocation.observe(context as PersonalizedActivity2) { data ->
            if (checkLocationPermission()) {
                isFirstLocationUpdate = true
                getCurrentLocation()
            }
        }
        return view
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            longitude = place.latLng?.longitude
            latitude = place.latLng?.latitude
            val latLng = LatLng(latitude!!, longitude!!)
            selectedLatLng = latLng
            moveCameraAndMarker(latLng)
        }
    }

    private fun checkLocationPermission(): Boolean {
        val location = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (location != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return false
        }
        return true
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                val builder = AlertDialog.Builder(context)
                builder.setCancelable(false)
                builder.setTitle(getString(R.string.LocationPermission))
                builder.setMessage(getString(R.string.grantlocationpermission))
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = ("package:" + requireActivity().packageName).toUri()
                    startActivity(intent)
                }
                builder.create().show()
            } else {
                checkLocationPermission()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        Log.e("onResume","onResume")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map1) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), "AIzaSyBcjdk3tch99jhgrQx2miMW3xdRW9By8Vc")
        }

        if (checkLocationPermission() && isFirstLocationUpdate) {
            getCurrentLocation()
        }

        currentLoc.setOnClickListener {
            if (checkLocationPermission()) {
                isFirstLocationUpdate = true
                getCurrentLocation()
            }
        }

        edaddress.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.DISPLAY_NAME,
                    Place.Field.FORMATTED_ADDRESS,
                    Place.Field.LOCATION
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireActivity())
                resultLauncher.launch(intent)
            }
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null && isFirstLocationUpdate) {
                        latitude = location.latitude
                        longitude = location.longitude
                        val latLng = LatLng(latitude!!, longitude!!)
                        selectedLatLng = latLng
                        moveCameraAndMarker(latLng)
                        isFirstLocationUpdate = false
                        mFusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                }
            }
        }

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun moveCameraAndMarker(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
        updateMarker(latLng)
        updateAddress(latLng)
    }

    private fun updateMarker(position: LatLng) {
        /*currentMarker?.remove()
        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(position)
                .title("Selected Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )*/
    }

    private fun updateAddress(latLng: LatLng) {
        var address = ""
        var cityState = ""
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                address = addresses[0].getAddressLine(0)
                cityState = addresses[0].subLocality + ", " + addresses[0].locality
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            tvlocation.text = address
            stateCountry.text = cityState
            viewModel.data.value = "$address,$cityState~${latLng.latitude}~${latLng.longitude}"
        }, 300)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map))
        getCurrentLocation()

        mMap.setOnCameraIdleListener {
            selectedLatLng = mMap.cameraPosition.target
            selectedLatLng?.let {
                updateMarker(it)
                updateAddress(it)
            }
        }
        Log.e("onMapReady","onMapReady")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

}
