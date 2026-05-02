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
import android.graphics.drawable.Drawable
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
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringDef
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.Constants.ISFROMGYMWORKOUT
import co.com.mypt.Api.Constants.IS_GYM_MEMBERSHIP_FLOW
import co.com.mypt.Api.Constants.delayMillis
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ComingSoonViewMode
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.Profile.ChangeLocationActivity
import co.com.mypt.Profile.NewUserProfileActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.ChooseLocationActivity
import co.com.mypt.activities.ComingSoonActivity
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.SelectCurrentLocationActivity
import co.com.mypt.activities.TrainerDetails
import co.com.mypt.adapter.HomeCertificateBannerAdapter
import co.com.mypt.adapter.HomeTrainerTagAdapter
import co.com.mypt.adapter.StoryAdapter
import co.com.mypt.adapter.TrainerGridViewAdapter
import co.com.mypt.curvedBottomNavigation.dpToPx
import co.com.mypt.databinding.FragmentGuestUserHomeNewBinding
import co.com.mypt.fragments.viewModels.GuestUserViewModel
import co.com.mypt.model.NearByGymModel
import co.com.mypt.model.TrainerStudiosResponse
import co.com.mypt.model.TrainersModel
import co.com.mypt.model.UpcomingClassModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import co.com.mypt.retrofitApi.UiState
import co.com.mypt.retrofitApi.UserViewModelFactory
import co.com.mypt.utils.HorizontalSpaceItemDecoration
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class GuestUserHomeFragmentNew : Fragment(), View.OnTouchListener,
    ViewTreeObserver.OnScrollChangedListener {
    private var homeTrainersList: List<TrainersModel> = emptyList()
    private var gymTrainersList: List<TrainersModel> = emptyList()
    var upcomingClassArraylist = ArrayList<UpcomingClassModel>()
    var nearByGymArraylist = ArrayList<NearByGymModel>()
    lateinit var imUpcomingBlur: ImageView
    lateinit var bookAssesment: RelativeLayout
    lateinit var tvProfile: TextView
    lateinit var tvlocation: TextView
    lateinit var allNearByGym: TextView

    private lateinit var mScrollView: ScrollView
    private lateinit var headerLayout: LinearLayout
    var isResumed1 = true
    var isPermissionDialogOpen = false
    var isclick = 0
    var showProgress = false
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null
    var lat = ""
    var chooseAddress = ""
    var long = ""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    lateinit var bindingView: FragmentGuestUserHomeNewBinding
    private var progressDialog: Dialog? = null
    private lateinit var trainerGridAdapter: TrainerGridViewAdapter
    private lateinit var homeCertificateBannerAdapter: HomeCertificateBannerAdapter

    private var autoScrollJob: Job? = null
    private var selectedTab = TrainerTab.HOME
    private  var addressId: String= ""


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
        bindingView = FragmentGuestUserHomeNewBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        edit = sharedPreferences.edit()
        tvlocation = bindingView.location
        tvProfile = bindingView.tvProfile
        bookAssesment = bindingView.bookAssesment

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        tvlocation.setOnClickListener {
            var intent = Intent(activity, ChooseLocationActivity::class.java)
            startActivity(intent)
        }
        if (!Places.isInitialized()) {
            Places.initialize(activity, "AIzaSyBVhnjhV5pynJXnSN2VTn-zhLGeIc7VcRw")
        }

        bindingView.homePt.setOnClickListener {
            if (sharedPreferences.getString(
                    Constants.token,
                    "-1"
                ) != "-1" || sharedPreferences.getString(
                    Constants.token, ""
                ).toString() != ""
            ) {
                edit.putString("typeWorkout","home").apply()
                val intent= Intent(context, SelectCurrentLocationActivity::class.java)
                intent.putExtra(ISFROMGYMWORKOUT,true)
                startActivity(intent)
            } else {
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }

        }
        bindingView.gymPt.setOnClickListener {
            if (sharedPreferences.getString(
                    Constants.token,
                    "-1"
                ) != "-1" || sharedPreferences.getString(
                    Constants.token, ""
                ).toString() != ""
            ) {
                edit.putString("typeWorkout","work").apply()
                val intent= Intent(context,ChangeLocationActivity::class.java)
                intent.putExtra(ISFROMGYMWORKOUT,true)
                startActivity(intent)
            } else {
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }

        bindingView.memberShip.setOnClickListener {
            val intent=Intent(context, GymListActivity::class.java)
            intent.putExtra("longitude",longitude)
            intent.putExtra("latitude",latitude)
            intent.putExtra(IS_GYM_MEMBERSHIP_FLOW,true)
            startActivity(intent)
        }
        bookAssesment.setOnClickListener {
            openBookAssessment()
        }
        bindingView.bookDemoView.setOnClickListener {
            openBookAssessment()
        }
        bindingView.choosePlanView.setOnClickListener {
            if (sharedPreferences.getString(
                    Constants.token,
                    "-1"
                ) != "-1" || sharedPreferences.getString(
                    Constants.token, ""
                ).toString() != ""
            ) {
                val intent = Intent(context, HomeGymTrainerActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }
        bindingView.profileView.setOnClickListener {
            val intent = Intent(requireContext(), NewUserProfileActivity::class.java)
            startActivity(intent)
        }

        bindingView.homeTrainerTab.setOnClickListener {
            selectTab(bindingView.homeTrainerTab, bindingView.gymTrainerTab)
            selectedTab = TrainerTab.HOME
            updateUI(homeTrainersList)
         }

        bindingView.gymTrainerTab.setOnClickListener {
            selectTab(bindingView.gymTrainerTab, bindingView.homeTrainerTab)
            selectedTab = TrainerTab.GYM
            updateUI(gymTrainersList)
        }

        childFragmentManager.setFragmentResultListener(
            HomeTrainerBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val studioId = bundle.getInt(HomeTrainerBottomSheet.KEY_SELECTED_STUDIO_ID, 0)
            val trainerId = bundle.getInt(HomeTrainerBottomSheet.KEY_SELECTED_TRAINER_ID, 0)

            openTrainerDetailScreen(trainerId.toString(),studioId.toString())
        }

        getAddressData()

        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = UserViewModelFactory()

        viewModel = ViewModelProvider(this, factory).get(GuestUserViewModel::class.java)
        collectUsers()
        viewModel.getStories("Bearer " + sharedPreferences.getString("token", ""))
        viewModel.getContent("Bearer " + sharedPreferences.getString("token", ""))

        trainerGridAdapter = TrainerGridViewAdapter(
            requireContext(),
            "grid",
            sharedPreferences.getString("typeWorkout", ""),
            latitude,
            longitude, "",
            onProfileClick = { trainersModel ->
                if(selectedTab== TrainerTab.GYM) {
                    edit.putString("typeWorkout","work").apply()
                    fetchTrainerStudios(trainersModel.id)
                }else{
                    edit.putString("typeWorkout","home").apply()
                    openTrainerDetailScreen(trainersModel.id)
                }
            }
        )
        bindingView.nearByGymRecyclerView.adapter = trainerGridAdapter
        bindingView.nearByGymRecyclerView.addItemDecoration(
            HorizontalSpaceItemDecoration(20.dpToPx(requireContext()))
        )
        homeCertificateBannerAdapter = HomeCertificateBannerAdapter( requireContext())
        bindingView.viewPagerBanner.adapter = homeCertificateBannerAdapter
        bindingView.viewPagerBanner.pageMargin = 10.dpToPx(requireContext())
        bindingView.dotsIndicator.attachTo(bindingView.viewPagerBanner)
        startAutoScroll()

        // Reset timer when user swipes manually
        bindingView.viewPagerBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
            }
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    autoScrollJob?.cancel()
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    startAutoScroll()
                }
            }
        })
        viewModel.getBanners("Bearer " + sharedPreferences.getString("token", ""))

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
//        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                context?.sendBroadcast(Intent("finish"))
//                (context as MainActivity).finish()
//            }
//        })
        arguments?.let {
            lat = it.getString(KEY_LAT, "") ?: "" // Provide a default value
            long = it.getString(KEY_LNG, "") ?: "" // Provide a default value
            chooseAddress = it.getString(KEY_ADD, "") ?: "" // Provide a default value
        }

    }

    private fun openBookAssessment(){
        val intent = Intent(context, ComingSoonActivity::class.java)
        intent.putExtra(ComingSoonActivity.KEY_VIEW_MODE, ComingSoonViewMode.FREE_ASSESSMENT)
        startActivity(intent)
    }

    private fun startAutoScroll() {
        // Cancel any existing job to prevent multiple coroutines running simultaneously
        autoScrollJob?.cancel()
        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle handles the cleanup when the fragment goes to background
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(delayMillis)
                    val count = homeCertificateBannerAdapter.count
                    if (count > 0) {
                        val nextItem = (bindingView.viewPagerBanner.currentItem + 1) % count
                        bindingView.viewPagerBanner.setCurrentItem(nextItem, true)
                    }
                }
            }
        }
    }

    private fun collectUsers() {

        viewModel.exerciseList.observe(viewLifecycleOwner){ list ->
                bindingView.exerciseRecyclerView.adapter = HomeTrainerTagAdapter(requireContext(),
                    list?.filterNotNull() ?: emptyList()
                ){ tag ->
                    fetchHomeGymTrainers(tagId = tag.id)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bannerState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            showProgress()
                        }

                        is UiState.Success -> {
                            hideProgress()
                            if(state.data.isNullOrEmpty()){
                                bindingView.viewPagerBanner.visibility = View.GONE
                                bindingView.dotsIndicator.visibility = View.GONE
                            }else{
                                homeCertificateBannerAdapter.updateData(state.data.filterNotNull())
                                bindingView.viewPagerBanner.visibility = View.VISIBLE
                                bindingView.dotsIndicator.visibility = View.VISIBLE
                            }
                        }

                        is UiState.Error -> {
                            hideProgress()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.userState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            showProgress()
                        }

                        is UiState.Success -> {

                            if (state.data?.isNullOrEmpty() == true) {
                                bindingView.llNodataPt.visibility = View.VISIBLE
                                bindingView.storiesListRecyclerView.visibility = View.GONE
                            } else {
                                bindingView.llNodataPt.visibility = View.GONE
                                bindingView.storiesListRecyclerView.visibility = View.VISIBLE
                                bindingView.storiesListRecyclerView.adapter =
                                    StoryAdapter(requireContext(), state.data, latitude, longitude)
                            }
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

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.trainerListState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                             showProgress()
                        }

                        is UiState.Success -> {
                          hideProgress()
                                 homeTrainersList = state.data?.map {
                                    val trainerModel=TrainersModel()
                                    trainerModel.name = it?.name?:""
                                    trainerModel.id = it?.id?:""
                                    trainerModel.distance =it?.distance?:""
                                    trainerModel.slot = it?.slot?:""
                                    trainerModel.noOfRating = it?.noOfRating?:""
                                    trainerModel.averageRating =it?.averageRating.toString()
                                    trainerModel.location = it?.location?:""
                                    trainerModel.profile =  it?.profile?:""
                                    trainerModel.is_verified =it?.is_verified.toString()
                                    trainerModel.tags =it?.tags?:emptyList()
                                    trainerModel.is_group =it?.is_group
                                    trainerModel.name=it?.name?:""
                                    trainerModel
                                }?:emptyList()
                                if (selectedTab == TrainerTab.HOME) {
                                    updateUI(homeTrainersList)
                                }

                        }

                        is UiState.Error -> {
                             hideProgress()
                        }
                    }
                }


            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.allGymTrainerListState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            showProgress()
                        }

                        is UiState.Success -> {
                            hideProgress()
                                gymTrainersList = state.data?.map {
                                    val trainerModel=TrainersModel()
                                    trainerModel.name = it?.name?:""
                                    trainerModel.id = it?.id?:""
                                    trainerModel.distance =it?.distance?:""
                                    trainerModel.slot = it?.slot?:""
                                    trainerModel.noOfRating = it?.noOfRating?:""
                                    trainerModel.averageRating =it?.averageRating.toString()
                                    trainerModel.location = it?.location?:""
                                    trainerModel.profile =  it?.profile?:""
                                    trainerModel.is_verified =it?.is_verified.toString()
                                    trainerModel.tags =it?.tags?:emptyList()
                                    trainerModel.is_group =it?.is_group
                                    trainerModel.name=it?.name?:""
                                    trainerModel
                                }?:emptyList()
                                if (selectedTab == TrainerTab.GYM) {
                                    updateUI(gymTrainersList)
                                }
                        }

                        is UiState.Error -> {
                            hideProgress()
                        }
                    }
                }


            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.trainerStudiosState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            showProgress()
                        }

                        is UiState.Success -> {
                            hideProgress()
                            val response = state.data?.data
                            openTrainerStudioBottomSheet(response)
                            viewModel.resetTrainerStudioState()
                        }

                        is UiState.Error -> {
                            hideProgress()
                        }
                        else ->{

                        }
                    }
                }


            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.contentState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                         println("1111")
                        }

                        is UiState.Success -> {
                            println("11112222")
                            if (state.data?.isNullOrEmpty() == true) {

                            } else {

                             val home_background=
                                 state.data?.firstOrNull { it?.key == "home_background" }
                             val buy_gym_pt=state.data?.firstOrNull { it?.key =="buy_gym_pt" }
                             val buy_home_pt=state.data?.firstOrNull { it?.key =="buy_home_pt" }
                             val buy_gym_membership=state.data?.firstOrNull { it?.key =="buy_gym_membership" }
                             val offer_banner=state.data?.firstOrNull { it?.key =="offer_banner" }
                             val bgBookAssesment=state.data?.firstOrNull { it?.id ==ActiveUserHomeFragmentNew.BOOK_FREE_ASSESSMENT }
                             val bgAddress=state.data?.firstOrNull { it?.id == ActiveUserHomeFragmentNew.BACKGROUND_ADDRESS_ID }

                                if (home_background !=null)
                                Glide.with(requireContext()).load(home_background?.image).fitCenter().into(bindingView.backgroundImg)
                                if (offer_banner !=null)
                                    Glide.with(requireContext()).load(offer_banner?.image).fitCenter().into(bindingView.homeBanner)
                               // else bindingView.homeBanner.visibility=View.GONE

                                bgBookAssesment?.let {
                                    Glide.with(requireContext()).load(it.image).fitCenter().into(bindingView.ivBookAssessment)
                                }

                                Glide.with(requireContext()).load(bgAddress?.image)
                                    .into(object : CustomTarget<Drawable>() {
                                        override fun onResourceReady(
                                            resource: Drawable,
                                            transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                                        ) {
                                            bindingView.headerLayout.background = resource
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {
                                            bindingView.headerLayout.background = placeholder
                                        }
                                    })
                                Glide.with(requireContext()).load(buy_home_pt?.image).fitCenter().into(bindingView.homePt)
                                Glide.with(requireContext()).load(buy_gym_membership?.image).fitCenter().into(bindingView.memberShip)
                                Glide.with(requireContext()).load(buy_gym_pt?.image).fitCenter().into(bindingView.gymPt)
                            }
                        }

                        is UiState.Error -> {
                            println("11113333")
                        }
                    }
                }


            }
        }
    }

    private fun fetchHomeGymTrainers(tagId: Int?=null){
        viewModel.fetchHomeAndGymTrainers("Bearer " + sharedPreferences.getString("token", ""), latitude,longitude,tagId)
    }

    private fun fetchTrainerStudios(trainerId: String){
        viewModel.getTrainerStudios("Bearer " + sharedPreferences.getString("token", ""), latitude,longitude,trainerId)
    }

    private fun checkLocationPermission(): Boolean {
        val location = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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
                Log.e("Permission", "Granted")
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) /*&& !isPermissionDialogOpen*/) {
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
            } else {
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
            .setPositiveButton(
                R.string.EnableGPS,
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

        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, (20 * 1000).toLong())
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
                            LatLng(latitude!!, longitude!!)
                            Log.e("latitude", "" + latitude)
                            Log.e("longitude", "" + longitude)
                            var addresses: List<Address>? = null
                            var address = ""
                            var cityState = ""
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    geocoder.getFromLocation(
                                        latitude!!, longitude!!, 1
                                    ) { addressList: List<Address?>? ->
                                        address = addressList!![0]!!.getAddressLine(0)
                                        cityState =
                                            addressList[0]!!.subLocality + ", " + addressList[0]!!.locality
                                    }
                                } else {
                                    addresses =
                                        geocoder.getFromLocation(latitude!!, longitude!!, 1)!!
                                    address = addresses[0].getAddressLine(0)
                                    cityState =
                                        addresses[0].subLocality + ", " + addresses[0].locality
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (activity == null) return
                            Handler(Looper.getMainLooper()).postDelayed({

                                activity?.runOnUiThread(Runnable {
                                    tvlocation.visibility = View.VISIBLE
                                    tvlocation.text = address
                                })

                            }, 2000)
                           fetchHomeGymTrainers()
                            // getgymList(latitude!!, longitude!!)
//                            getClasses(latitude!!, longitude!!)
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
        if (sharedPreferences.getString(
                Constants.token,
                "-1"
            ) != "-1" || sharedPreferences.getString(
                Constants.token, ""
            ).toString() != ""
        ) {
//            userName.text = "$msg ${sharedPreferences.getString(Constants.name,"")}!"
            val nameInitial = sharedPreferences.getString(Constants.name,"")?.firstOrNull()
            nameInitial?.let {
                tvProfile.text = it.toString()
            }
            getchecktype()
        } else {
//            userName.text = msg
        }
        if (sharedPreferences.getString(
                Constants.token,
                "-1"
            ) != "-1" || sharedPreferences.getString(Constants.token, "").toString() != ""
        ) {
            if (chooseAddress == "" || chooseAddress == "null" || chooseAddress.equals(null)) {
                if (sharedPreferences.getString(Constants.address, "")
                        .equals("null") || sharedPreferences.getString(Constants.address, "")
                        .equals("")
                    || sharedPreferences.getString(Constants.lat, "")
                        .equals("") || sharedPreferences.getString(Constants.long, "").equals("")
                ) {
                    if (!isPermissionDialogOpen) {
                        isPermissionDialogOpen = true
                        if (checkLocationPermission()) {
                            getCurrentLocation()
                        }
                    }
                } else {

                    tvlocation.visibility = View.VISIBLE

                    tvlocation.text = sharedPreferences.getString(Constants.address, "")
                    latitude = sharedPreferences.getString(Constants.lat, "")!!.toDouble()
                    longitude = sharedPreferences.getString(Constants.long, "")!!.toDouble()
                    fetchHomeGymTrainers()
//                    getgymList(latitude!!, longitude!!)
//                    getClasses(latitude!!, longitude!!)
                }
            } else {
                tvlocation.visibility = View.VISIBLE
                tvlocation.text = chooseAddress
                latitude = lat.toDouble()
                longitude = long.toDouble()
               fetchHomeGymTrainers()
//                getgymList(latitude!!, longitude!!)
//                getClasses(latitude!!, longitude!!)
            }
        } else {
            if (chooseAddress == "" || chooseAddress == "null" || chooseAddress.equals(null)) {
                if (!isPermissionDialogOpen) {
                    isPermissionDialogOpen = true
                    Log.e("checkLocationPermission----->", "1")
                    if (checkLocationPermission()) {
                        getCurrentLocation()
                    }
                }
            } else {
                latitude = lat.toDouble()
                longitude = long.toDouble()
                tvlocation.visibility = View.VISIBLE
                tvlocation.text = chooseAddress
               fetchHomeGymTrainers()
//                getgymList(latitude!!, longitude!!)
//                getClasses(latitude!!, longitude!!)
            }
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            showGPSDisabledAlertToUser()
            return
        }
    }

    private fun getchecktype() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(), "")
        progressDialog.show()

        var api = ""
        api = ApiURL.checktype

        Log.e("checkTypeUrl", api)
        GetMethod(
            api, activity
        ).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("checkTypeResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {

                        var jsonObjectvalue = jsonObj.optJSONObject("data").optJSONObject("package")
                            .optString("getTier")
                        if (jsonObjectvalue.equals("")) {
//                            tvmember.visibility=View.GONE
                        } else {
//                            tvmember.visibility=View.VISIBLE
//                            tvmember.setText(jsonObjectvalue+" Member")

                        }

                    } else {

                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }

    private fun getAddressData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()

        GetMethod(ApiURL.getaddress,requireContext()).startMethod(object :
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

    private fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.progressDialog(requireContext(), "")
        }

        if (!(requireActivity().isFinishing) && progressDialog?.isShowing == false) {
            progressDialog?.show()
        }
    }

    private fun hideProgress() {
        progressDialog?.dismiss()
    }

    private fun selectTab(
        selectedTab: TextView,
        unselectedTab: TextView
    ) {
        // Tab UI
        selectedTab.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        selectedTab.background = ContextCompat.getDrawable(requireContext(),R.drawable.feet_button)

        unselectedTab.setTextColor(ContextCompat.getColor(requireContext(),R.color.lightgreycolor))
        unselectedTab.background = null
    }

    fun updateUI(list: List<TrainersModel>) {
        bindingView.nearByGymRecyclerView.scrollToPosition(0)
        if (list.isEmpty()) {
            bindingView.nearByGymRecyclerView.visibility = View.GONE
            bindingView.llNodataTrainers.visibility = View.VISIBLE
        } else {
            bindingView.nearByGymRecyclerView.visibility = View.VISIBLE
            bindingView.llNodataTrainers.visibility = View.GONE
            trainerGridAdapter.updateTrainerList(list)
        }
    }

    private fun openTrainerStudioBottomSheet(response: TrainerStudiosResponse.Data?) {
        response?.let {
            HomeTrainerBottomSheet.newInstance(it)
                .show(childFragmentManager, "HomeTrainerBottomSheet")
        }
    }

    private fun openTrainerDetailScreen(trainersId: String,studioId:String?=null){
        val intent = Intent(context, TrainerDetails::class.java)
        intent.putExtra("trainer_id", trainersId)
        intent.putExtra("studio_id",studioId )
        intent.putExtra("type", if(selectedTab== TrainerTab.HOME)"home" else "work")
        intent.putExtra("long", longitude)
        intent.putExtra("lat", latitude)
        intent.putExtra("isGuestHome", true)
        requireContext().startActivity(intent)
    }

}

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    TrainerTab.HOME, TrainerTab.GYM
)
annotation class TrainerTab {
    companion object {
        const val HOME = "home"
        const val GYM = "gym"
    }
}