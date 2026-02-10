package co.com.mypt.fragments

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
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.GymWorkout.withTrainer.SelectGymWorkoutActivity
import co.com.mypt.Notification.NotificationListActivity
import co.com.mypt.Profile.NewUserProfileActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.UpComingClasses.UpComingClassActivity
import co.com.mypt.activities.ChooseLocationActivity
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.adapter.NearByGymAdapter
import co.com.mypt.adapter.ShopCategoryAdapter
import co.com.mypt.adapter.ShopProductsAdapter
import co.com.mypt.adapter.TransformationAdapter
import co.com.mypt.adapter.UpcomingClassAdapter
import co.com.mypt.databinding.FragmentGuestUserHomeNewBinding
import co.com.mypt.fragments.viewModels.GuestUserViewModel
import co.com.mypt.model.NearByGymModel
import co.com.mypt.model.ShopCategoryModel
import co.com.mypt.model.ShopProductsModel
import co.com.mypt.model.TransformationModel
import co.com.mypt.model.UpcomingClassModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import co.com.mypt.retrofitApi.UiState
import co.com.mypt.retrofitApi.UserViewModelFactory
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class GuestUserHomeFragmentNew : Fragment(), View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener {

    lateinit var upcomingClassRecyclerView : RecyclerView
    lateinit var transformationRecyclerView : RecyclerView
    lateinit var shopCategoryRecyclerView : RecyclerView
    lateinit var shopProductsRecyclerView : RecyclerView
    lateinit var nearByGymRecyclerView : RecyclerView
    lateinit var differentWorkouts : ImageView
    lateinit var purchaseGymPass : ImageView
    lateinit var planWorkout : ImageView
    lateinit var notification : ImageView

    var transformationArraylist = ArrayList<TransformationModel>()
    var upcomingClassArraylist = ArrayList<UpcomingClassModel>()
    var shopCategoryArraylist = ArrayList<ShopCategoryModel>()
    var shopProductsArraylist = ArrayList<ShopProductsModel>()
    var nearByGymArraylist = ArrayList<NearByGymModel>()

    lateinit var bookATrainer : ImageView
    lateinit var red_circle : ImageView
    lateinit var imUpcomingBlur : ImageView
    lateinit var improfile : ImageView
    lateinit var logout : ImageView
    lateinit var allTransformationStories : TextView
    lateinit var allUpcomingClass : TextView
    lateinit var tvlocation : TextView
//    lateinit var userName : TextView
//    lateinit var tvmember : TextView
    lateinit var allNearByGym : TextView

    private lateinit var mScrollView: ScrollView
    private lateinit var headerLayout: LinearLayout
//    private lateinit var linearProfile: LinearLayout
    var isResumed1 = true
    var isPermissionDialogOpen = false
    var isclick=0
    var showProgress = false
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null
    var lat =""
    var chooseAddress = ""
    var long = ""
    lateinit var sharedPreferences : SharedPreferences
    lateinit var edit:SharedPreferences.Editor
lateinit var profileView: CardView
lateinit var bindingView: FragmentGuestUserHomeNewBinding
    companion object {
        private const val KEY_LAT = "lat"
        private const val KEY_LNG = "lng"
        private const val KEY_ADD = "add"

        fun newInstance(param1: String, param2: String, param3: String): GuestUserHomeFragmentNew {
            val fragment = GuestUserHomeFragmentNew()
            val args = Bundle()
            args.putString(KEY_LAT, param1)
            args.putString(KEY_LNG, param2)
            args.putString(KEY_ADD, param3)
            fragment.arguments = args
            return fragment
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView  = FragmentGuestUserHomeNewBinding.inflate(inflater, container, false)
        val rootView: View = inflater.inflate(R.layout.fragment_guest_user_home_new, container, false)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(requireActivity())
        edit=sharedPreferences.edit()

        logout = rootView.findViewById(R.id.logout)
        profileView = rootView.findViewById(R.id.profileView)
        red_circle = rootView.findViewById(R.id.red_circle)
        notification = rootView.findViewById(R.id.notification)
        planWorkout = rootView.findViewById(R.id.planWorkout)
        allNearByGym = rootView.findViewById(R.id.allNearByGym)
        headerLayout = rootView.findViewById(R.id.headerLayout)
        mScrollView = rootView.findViewById(R.id.scrollView)
        tvlocation = rootView.findViewById(R.id.location)
        imUpcomingBlur = rootView.findViewById(R.id.imUpcomingBlur)
        improfile = rootView.findViewById(R.id.improfile)
        purchaseGymPass = rootView.findViewById(R.id.purchaseGymPass)
        allUpcomingClass = rootView.findViewById(R.id.allUpcomingClass)
        differentWorkouts = rootView.findViewById(R.id.differentWorkouts)
//        linearProfile = rootView.findViewById(R.id.linearProfile)
//        tvmember = rootView.findViewById(R.id.tvmember)

        allTransformationStories = rootView.findViewById(R.id.allTransformationStories)

        shopProductsRecyclerView = rootView.findViewById(R.id.shopProductsRecyclerView)
        shopCategoryRecyclerView = rootView.findViewById(R.id.shopCategoryRecyclerView)
        transformationRecyclerView = rootView.findViewById(R.id.transformationRecyclerView)
        upcomingClassRecyclerView = rootView.findViewById(R.id.upcomingClassRecyclerView)
        nearByGymRecyclerView = rootView.findViewById(R.id.nearByGymRecyclerView)

        bookATrainer = rootView.findViewById(R.id.bookATrainer)
//        userName = rootView.findViewById(R.id.userName)

        // Invoking touch listener to detect movement of ScrollView
        mScrollView.setOnTouchListener(this)
        mScrollView.viewTreeObserver.addOnScrollChangedListener(this)

        transformationRecyclerView.adapter = TransformationAdapter(context,transformationArraylist)
        shopProductsRecyclerView.adapter = ShopProductsAdapter(context,shopProductsArraylist)
        shopCategoryRecyclerView.adapter = ShopCategoryAdapter(context,shopCategoryArraylist)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager


        purchaseGymPass.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(Constants.token,"").toString() != ""){
                val intent = Intent(context, SelectGymWorkoutActivity::class.java)
                startActivity(intent)

            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }
        planWorkout.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(Constants.token,"").toString() != ""){
                /*var fragmentManager= requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.fragment_container,CalendarFragment()).addToBackStack("fragment1").commit()*/
                (context as MainActivity).navController.navigate(R.id.calendarFragment)

            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }
        profileView.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(Constants.token,"").toString() != ""){
                val intent = Intent(context, NewUserProfileActivity::class.java)
                startActivity(intent)

            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }

        tvlocation.setOnClickListener{
            var intent=Intent(activity,ChooseLocationActivity::class.java)
            startActivity(intent)
        }
        if (!Places.isInitialized()) {
            Places.initialize(activity, "AIzaSyBVhnjhV5pynJXnSN2VTn-zhLGeIc7VcRw")
        }

        bookATrainer.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(
                    Constants.token,"").toString() != ""){
                val intent = Intent(context, HomeGymTrainerActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }

        }
        notification.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(
                    Constants.token,"").toString() != ""){
                val intent = Intent(context, NotificationListActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }

        }

        differentWorkouts.setOnClickListener {
            /*var fragmentManager= requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container,LibraryFragment()).addToBackStack("fragment1").commit()*/

            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(
                    Constants.token,"").toString() != ""){
                (context as MainActivity).navController.navigate(R.id.libraryFragment)
            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }

        }

        logout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                sdkInitialize(requireActivity())
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                        null, HttpMethod.DELETE,
                        { response: GraphResponse? ->
                            LoginManager.getInstance().logOut()
                        }).executeAsync()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val intent= Intent(context, PhoneNumberScreenActivity::class.java)
            TaskStackBuilder.create(requireContext()).addNextIntentWithParentStack(intent).startActivities()
            (context as MainActivity).finish()
        }

        allNearByGym.setOnClickListener {
            val intent_ = Intent(context, GymListActivity::class.java)
            edit.putString("typeWorkout","work").apply()
            startActivity(intent_)
        }
        //0location.setText()
        bindingView.myPtRecycleView
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = UserViewModelFactory()

        viewModel = ViewModelProvider(this, factory).get(GuestUserViewModel::class.java)
        collectUsers()
        viewModel.fetchUsers("Bearer " + sharedPreferences.getString("token", ""))
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    override fun onScrollChanged() {
       /* val view = mScrollView.getChildAt(mScrollView.childCount - 1)
        val topDetector = mScrollView.scrollY
        val bottomDetector: Int = view.bottom - (mScrollView.height + mScrollView.scrollY)
        if (bottomDetector > 0) {
            headerLayout.setBackgroundColor(resources.getColor(R.color.buttontextcolor))
        }
        if (topDetector <= 0) {
            headerLayout.background = null
        }*/
    }
    private lateinit var viewModel: GuestUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                context?.sendBroadcast(Intent("finish"))
                (context as MainActivity).finish()
            }
        })
        arguments?.let {
            lat = it.getString(KEY_LAT, "") ?: "" // Provide a default value
            long = it.getString(KEY_LNG, "") ?: "" // Provide a default value
            chooseAddress = it.getString(KEY_ADD, "") ?: "" // Provide a default value
        }

    }

    private fun collectUsers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.userState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            //binding.progressBar.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                           println("DDLDLDLDLDL")
                           // binding.progressBar.visibility = View.GONE

//                            binding.recyclerView.adapter = UserAdapter(state.data)
                        }

                        is UiState.Error -> {

//                            binding.progressBar.visibility = View.GONE

                            /*Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                    }
                }
            }
        }
    }

    private fun getgymList(latitude: Double, longitude: Double) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        Log.e("gymListApi",""+ ApiURL.getTrainer+"0"+"&tag_id="+"0"+"&type="+"gym"+"&long="+longitude +"&lat="+ latitude)
        GetMethod(ApiURL.getTrainer+"0"+"&tag_id="+"0"+"&type="+"gym"+"&long="+ longitude +"&lat="+ latitude,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                nearByGymArraylist.clear()
                Log.e("getGymListResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayList=jsonObj.optJSONObject("data").optJSONArray("studios")
                        if (jsonArrayList.length()>0){
                            for(i in 0 until jsonArrayList.length()){
                                var jsonObject1 = jsonArrayList.optJSONObject(i)
                                val nearByGymModel = NearByGymModel()
                                nearByGymModel.name = jsonObject1.optString("name")
                                nearByGymModel.id = jsonObject1.optString("id")
                                nearByGymModel.distance = jsonObject1.optString("distance")
                                nearByGymModel.slot = jsonObject1.optString("slot")
                                nearByGymModel.noOfRating = jsonObject1.optString("noOfRating")
                                nearByGymModel.averageRating = jsonObject1.optString("averageRating")
                                nearByGymModel.profile = jsonObject1.optString("profile")
                                nearByGymModel.location = jsonObject1.optString("location")
                                nearByGymModel.timing = jsonObject1.optString("timing")
                                nearByGymModel.activity = jsonObject1.optJSONArray("activity")
                                nearByGymModel.tag = jsonObject1.optString("tag")
                                nearByGymModel.description = jsonObject1.optString("description")
                                nearByGymArraylist.add(nearByGymModel)
                            }

                            /*trainerRecyclerView.adapter = GymListAdapter(this@GymListActivity,trainerList,intent.getStringExtra("type"),"gym",
                                this@InactivePlanHomeFragment.latitude,
                                this@InactivePlanHomeFragment.longitude
                            )*/
                            nearByGymRecyclerView.adapter = NearByGymAdapter(context,nearByGymArraylist,latitude,longitude,"gym")
                            nearByGymRecyclerView.visibility= View.VISIBLE

                        }else{
                            nearByGymRecyclerView.visibility= View.GONE

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
        val location = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
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
                Log.e("Permission","Granted")
            }
            else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) /*&& !isPermissionDialogOpen*/) {
                val builder = AlertDialog.Builder(context)
                builder.setCancelable(false)
                builder.setTitle(resources.getString(R.string.LocationPermission))
                builder.setMessage(resources.getString(R.string.grantlocationpermission))
                builder.setPositiveButton(
                    "OK"
                ) { dialog, which ->
                    dialog.dismiss()
                    isPermissionDialogOpen = false
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.setData(Uri.parse("package:${context?.packageName}"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(intent)
                }
                builder.create()
                builder.show()
            }else {
                isPermissionDialogOpen = false
                /*Log.e("checkLocationPermission----->","2")
                checkLocationPermission()*/
            }
        }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(
            activity
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
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED*/
        ) {
            return
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
                                        cityState = addressList[0]!!.subLocality +", "+ addressList[0]!!.locality
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
                            if(activity == null) return
                            Handler(Looper.getMainLooper()).postDelayed({

                                activity?.runOnUiThread(Runnable {
                                    tvlocation.visibility=View.VISIBLE
                                    tvlocation.text = address
                                })

                            }, 2000)

                            getgymList(latitude!!, longitude!!)
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
        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
        var msg = ""
        when (timeOfDay) {
            in 0..11 -> {
                msg = "Good Morning"
            }
            in 12..15 -> {
                msg = "Good Afternoon"
            }
            in 16..20 -> {
                msg = "Good Evening"
            }
            in 21..23 -> {
                msg = "Good Night"
            }
        }
        if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(
                Constants.token,"").toString() != ""){
//            userName.text = "$msg ${sharedPreferences.getString(Constants.name,"")}!"
            Glide.with(requireActivity()!!).load(sharedPreferences.getString(Constants.profile_image,"")).fitCenter().error(R.drawable.guest_user).into(improfile)
            getchecktype()
        }else{
//            userName.text = msg
        }
        if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(Constants.token,"").toString() != ""){
            if(chooseAddress == "" || chooseAddress == "null" ||chooseAddress.equals(null)){
                if (sharedPreferences.getString(Constants.address,"").equals("null") || sharedPreferences.getString(Constants.address,"").equals("")
                    || sharedPreferences.getString(Constants.lat,"").equals("")|| sharedPreferences.getString(Constants.long,"").equals("")){
                    if(!isPermissionDialogOpen) {
                        isPermissionDialogOpen = true
                        if (checkLocationPermission()) {
                            getCurrentLocation()
                        }
                    }
                }else{

                    tvlocation.visibility=View.VISIBLE

                    tvlocation.text = sharedPreferences.getString(Constants.address,"")
                    latitude= sharedPreferences.getString(Constants.lat,"")!!.toDouble()
                    longitude= sharedPreferences.getString(Constants.long,"")!!.toDouble()

                    getgymList(latitude!!, longitude!!)
                    getClasses(latitude!!, longitude!!)
                }
            }
            else{
                tvlocation.visibility=View.VISIBLE
                tvlocation.text = chooseAddress
                latitude= lat.toDouble()
                longitude= long.toDouble()

                getgymList(latitude!!, longitude!!)
                getClasses(latitude!!, longitude!!)
            }
        }
        else{
            if (chooseAddress == "" || chooseAddress == "null" || chooseAddress.equals(null)){
                if(!isPermissionDialogOpen) {
                    isPermissionDialogOpen = true
                    Log.e("checkLocationPermission----->","1")
                    if (checkLocationPermission()) {
                        getCurrentLocation()
                    }
                }
            }
            else{
                latitude=lat.toDouble()
                longitude=long.toDouble()
                tvlocation.visibility=View.VISIBLE
                tvlocation.text = chooseAddress

                getgymList(latitude!!, longitude!!)
                getClasses(latitude!!, longitude!!)
            }
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showGPSDisabledAlertToUser()
            return
        }
    }
    private fun getClasses(latitude: Double, longitude: Double) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()

        var api=""
        api= ApiURL.upcoming_classes+latitude+"&long="+longitude

        Log.e("UpcomingclassnearUrl",api)
        GetMethod(api
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                upcomingClassArraylist.clear()
                Log.e("UpcomingNearResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArrayAllClassess=jsonObj.optJSONObject("data").optJSONArray("allClasses")

                        if (jsonArrayAllClassess.length()>0){

                            for(i in 0 until jsonArrayAllClassess.length()){
                                var json=jsonArrayAllClassess.optJSONObject(i)
                                var nearUpcomingCLassModel= UpcomingClassModel()
                                nearUpcomingCLassModel.cla_ss=json.optString("class")
                                nearUpcomingCLassModel.id=json.optString("id")
                                nearUpcomingCLassModel.image=json.optString("image")
                                nearUpcomingCLassModel.status=json.optString("status")
                                nearUpcomingCLassModel.location=json.optString("location")
                                nearUpcomingCLassModel.type=json.optString("type")
                                nearUpcomingCLassModel.time=json.optString("time")
                                nearUpcomingCLassModel.start_end=json.optString("start_end")
                                nearUpcomingCLassModel.price=json.optString("price")
                                nearUpcomingCLassModel.trained_by=json.optString("trained_by")
                                nearUpcomingCLassModel.trainer_image=json.optString("trainer_image")
                                nearUpcomingCLassModel.studio_name=json.optString("studio_name")
                                nearUpcomingCLassModel.schedule_id=json.optString("schedule_id")
                                upcomingClassArraylist.add(nearUpcomingCLassModel)
                            }

                            upcomingClassRecyclerView.adapter = UpcomingClassAdapter(context,upcomingClassArraylist,latitude,longitude)

                            imUpcomingBlur.visibility= View.GONE
                            upcomingClassRecyclerView.visibility= View.VISIBLE
                            allUpcomingClass.setOnClickListener {
                                var intent=Intent(activity, UpComingClassActivity::class.java)
                                startActivity(intent)
                            }
                        }else{
                            imUpcomingBlur.visibility= View.VISIBLE
                            upcomingClassRecyclerView.visibility= View.GONE
                            allUpcomingClass.setOnClickListener {
                            }
                        }






                    }else{

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

    private fun getchecktype() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()

        var api=""
        api= ApiURL.checktype

        Log.e("checkTypeUrl",api)
        GetMethod(api
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("checkTypeResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonObjectvalue=jsonObj.optJSONObject("data").optJSONObject("package").optString("getTier")
                        if (jsonObjectvalue.equals("")){
//                            tvmember.visibility=View.GONE
                        }else{
//                            tvmember.visibility=View.VISIBLE
//                            tvmember.setText(jsonObjectvalue+" Member")

                        }


                        if (!jsonObj.optJSONObject("data").optBoolean("userHaveRequest")){
                            red_circle.visibility=View.GONE
                        }else{
                            red_circle.visibility=View.VISIBLE

                        }

                    }else{

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