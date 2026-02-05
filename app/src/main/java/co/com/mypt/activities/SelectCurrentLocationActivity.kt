package co.com.mypt.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
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
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants.ISFROMGYMWORKOUT
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.AddressListAdapter
import co.com.mypt.model.AddressModel
import co.com.mypt.model.CityModel
import com.android.volley.VolleyError
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.util.Arrays
import java.util.Locale
import androidx.core.content.edit
import co.com.mypt.Api.Constants.REVIEW_ADDRESS_ID

class SelectCurrentLocationActivity : AppCompatActivity() , OnMapReadyCallback {

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
    var cityList = ArrayList<CityModel>()
    var country_name=""
    var country_id=""
    var city_id=""
    var type=""


    lateinit var tvCity:TextInputEditText
    lateinit var tvStreetName:TextInputEditText
    lateinit var tvBuildingName:TextInputEditText
    lateinit var autoCompleteCity:AutoCompleteTextView
    var cityArrayList=ArrayList<String>()
    var isFromGymWorkoutL: Boolean?=false
    private var isAddressAdded=false
    private var addressId=""
    lateinit var sharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)

        setContentView(R.layout.activity_select_current_location)
        edaddress=findViewById(R.id.edaddress)
        tvlocation=findViewById(R.id.tvlocation)
        stateCountry=findViewById(R.id.stateCountry)
        currentLoc=findViewById(R.id.currentLoc)
        tvContinue=findViewById(R.id.tvContinue)
        linearheader=findViewById(R.id.linearheader)
        isFromGymWorkoutL=intent?.getBooleanExtra(ISFROMGYMWORKOUT,false)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map1) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val apiKey = "AIzaSyBcjdk3tch99jhgrQx2miMW3xdRW9By8Vc"

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }
//        if (intent.getStringExtra("link").equals("add")){
            if (checkLocationPermission() && isclick==0) {
                getCurrentLocation()

            }
//        }

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
                val fields: List<Place.Field> = Arrays.asList(
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
        getCityListData()
        addressBottomSheet()

        tvContinue.setOnClickListener{
            if (isAddressAdded){
                if ((latitude ==null || latitude ==0.0) ||(longitude ==null || longitude ==0.0) ){
                    Toast.makeText(this, "Please select location", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                startTrainerList()
                return@setOnClickListener
            }
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
        if (isFromGymWorkoutL == true){
            getAddressData()
        }

    }

    fun startTrainerList(){
        sharedPreferences.edit(commit = true) { putString(REVIEW_ADDRESS_ID, addressId) }
        val intent= Intent(this@SelectCurrentLocationActivity, TrainersListActivity::class.java)
        intent.putExtra("address_id",addressId)
        intent.putExtra("longitude",longitude)
        intent.putExtra("latitude",latitude)
        startActivity(intent)
    }

    private fun getAddressData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        GetMethod(ApiURL.getaddress,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                Log.e("getAddressResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        val jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray != null && jsonArray.length()>0){
                            addressId= jsonArray.optJSONObject(0).optString("id")
                            isAddressAdded=true
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
    private fun getCityListData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        GetMethod(ApiURL.get_cities,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                cityList.clear()
                Log.e("getCityListResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONObject("data").optJSONArray("cities")
                        country_id=jsonObj.optJSONObject("data").optString("id")
                        country_name=jsonObj.optJSONObject("data").optString("name")

                        for(i in 0 until jsonArray.length()){
                            var jsonObject1=jsonArray.optJSONObject(i)
                            var cityModel= CityModel()

                            cityModel.id=jsonObject1.optString("id")
                            cityModel.name=jsonObject1.optString("name")
                            cityList.add(cityModel)
                            cityArrayList.add(jsonObject1.optString("name"))

                        }
                        val adapter = ArrayAdapter(applicationContext,R.layout.custom_dropdown_item, cityArrayList)
                        autoCompleteCity.setAdapter(adapter)

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

    private fun addressBottomSheet() {
        val bottomSheet = layoutInflater.inflate(R.layout.add_address_bottomsheet, null)
        autoCompleteCity=bottomSheet.findViewById<AutoCompleteTextView>(R.id.autoCompleteCity)
        var stateLinearLayout=bottomSheet.findViewById<LinearLayout>(R.id.stateLinearLayout)
        tvCity=bottomSheet.findViewById<TextInputEditText>(R.id.tvCity)
        var linearclose=bottomSheet.findViewById<LinearLayout>(R.id.linearclose)
        var tvCountry=bottomSheet.findViewById<TextInputEditText>(R.id.tvCountry)
        var tvmobile=bottomSheet.findViewById<TextInputEditText>(R.id.tvmobile)
        tvBuildingName=bottomSheet.findViewById<TextInputEditText>(R.id.tvBuildingName)
        tvStreetName=bottomSheet.findViewById<TextInputEditText>(R.id.tvStreetName)
        var tvLandmark=bottomSheet.findViewById<TextInputEditText>(R.id.tvLandmark)
        var stateTextInputLayout=bottomSheet.findViewById<TextInputLayout>(R.id.stateTextInputLayout)
        var linearSave=bottomSheet.findViewById<LinearLayout>(R.id.linearSave)
        var checkHome=bottomSheet.findViewById<MaterialButton>(R.id.checkHome)
        var checkOffice=bottomSheet.findViewById<MaterialButton>(R.id.checkOffice)
        var checkOthers=bottomSheet.findViewById<MaterialButton>(R.id.checkOthers)
        var tvAddNew=bottomSheet.findViewById<TextView>(R.id.tvAddNew)
        if (intent.getStringExtra("link").equals("edit")){
            tvAddNew.setText(resources.getString(R.string.edit_address))
            tvBuildingName.setText(intent.getStringExtra("building_name"))
            tvStreetName.setText(intent.getStringExtra("street"))
            if (intent.getStringExtra("landmark").equals("null")){
                tvLandmark.setText("")
            }else {
                tvLandmark.setText(intent.getStringExtra("landmark"))
            }

            tvmobile.setText(intent.getStringExtra("mobile"))
            tvCity.setText(intent.getStringExtra("city"))
            city_id=""+intent.getStringExtra("city_id")
            country_id=""+intent.getStringExtra("country_id")
            if (intent.getStringExtra("type").equals("home")){
                type="home"
                checkHome.isCheckable = true
                checkHome.isChecked=true

                checkOffice.isChecked=false
                checkOthers.isChecked=false
            }else if ((intent.getStringExtra("type").equals("office"))){
                type="office"
                checkOffice.isCheckable = true
                checkOffice.isChecked=true

                checkHome.isChecked=false
                checkOthers.isChecked=false
            }else{
                type="others"
                checkOthers.isCheckable = true
                checkOthers.isChecked=true

                checkHome.isChecked=false
                checkOffice.isChecked=false
            }
        }
        linearclose.setOnClickListener{
            addressBottomSheetDialog.dismiss()
        }
        checkHome.setOnClickListener{
            type="home"
            checkHome.isCheckable = true
            checkHome.isChecked=true
            checkOffice.isChecked=false
            checkOthers.isChecked=false
        }
        checkOffice.setOnClickListener{
            type="office"
            checkOffice.isCheckable = true
            checkOffice.isChecked=true

            checkHome.isChecked=false
            checkOthers.isChecked=false
        }
        checkOthers.setOnClickListener{
            type="others"
            checkOthers.isCheckable = true
            checkOthers.isChecked=true

            checkHome.isChecked=false
            checkOffice.isChecked=false
        }

        tvCity.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        stateTextInputLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        stateLinearLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })

        autoCompleteCity.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val selection = parent.getItemAtPosition(position) as String
            var pos = -1
            for (i in cityList.indices) {
                if (selection.contains(cityList[i].name)) {
                    pos = i
                    break
                }
            }

            city_id = cityList[pos].id
            tvCity.setText(cityList[pos].name)


        })
        linearSave.setOnClickListener{
            if (type.equals("")){
                Toast.makeText(applicationContext,R.string.select_addressType,Toast.LENGTH_LONG).show()
            }else  if (tvBuildingName.getText().toString().trim { it <= ' ' }.matches("".toRegex())) {
                tvBuildingName.setError(resources.getString(R.string.enter_apartment_name))
            } else if (tvStreetName.getText().toString().trim { it <= ' ' }.matches("".toRegex())){
                tvStreetName.setError(resources.getString(R.string.enter_build_name))

            }else if (tvmobile.getText().toString().trim { it <= ' ' }.matches("".toRegex())){
                tvmobile.setError(resources.getString(R.string.enter_street))

            }
           /* else if (tvmobile.getText().toString().trim().length<10){
                tvmobile.setError(resources.getString(R.string.enter_valid_phone_number))

            }*/else if (city_id.equals("")){
                Toast.makeText(applicationContext,R.string.select_city,Toast.LENGTH_LONG).show()
            }
            else {
                sendAddaddressData(tvBuildingName,tvStreetName,tvLandmark,type,tvmobile,city_id)
            }
        }
        addressBottomSheetDialog.setContentView(bottomSheet)
        BottomSheetAnimation.animateBottomSheet(addressBottomSheetDialog)

    }

    private fun sendAddaddressData(
        tvBuildingName: TextInputEditText,
        tvStreetName: TextInputEditText,
        tvLandmark: TextInputEditText,
        type: String,
        tvmobile: TextInputEditText,
        city_id: String,

        ) {
        val param: MutableMap<String, String> = HashMap()
        param["building_name"] = tvBuildingName.text.toString()
        param["street"] = tvStreetName.text.toString()
        param["city_id"] = city_id
        param["lat"] = ""+latitude
        param["long"] = ""+longitude
        param["country_id"] = country_id
        param["landmark"] = tvLandmark.text.toString()
        param["type"] = type
        param["mobile_no"] = tvmobile.text.toString()
        if (intent.getStringExtra("link").equals("edit")){
            param["id"] =""+intent.getStringExtra("address_id")

        }else{
            param["id"] = ""

        }
        Log.e("addAddressParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.addaddress,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("addressRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        addressBottomSheetDialog.dismiss()
                        finish()
                        if (isFromGymWorkoutL == true){
                            startTrainerList() }
                    }else{
                        startTrainerList()
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
                    val builder = AlertDialog.Builder(this@SelectCurrentLocationActivity)
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
        val alertDialogBuilder = AlertDialog.Builder(this@SelectCurrentLocationActivity)
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
                tvBuildingName.setText(address)
                tvStreetName.setText(streetname)
                //edaddress.setText(address)
                //  viewModel.data.value  = "${tvlocation.text},${stateCountry.text}-$latitude-$longitude"
            }, 500)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map))
        if (intent.getStringExtra("link").equals("add")){
            getCurrentLocation()

        }else{
            longitude = intent.getDoubleExtra("long",0.0)
            latitude = intent.getDoubleExtra("lat",0.0)
            val location = LatLng(latitude!!, longitude!!)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f))
            onMapCameraReady()
        }

//        mMap.isMyLocationEnabled = true

        /* mMap.setOnMapClickListener {
             val fragmentManager =
                 requireActivity()!!.supportFragmentManager
             fragmentManager.beginTransaction().replace(R.id.flFragment, MapFragment())
                 .addToBackStack("w").commit()
         }*/
    }
}