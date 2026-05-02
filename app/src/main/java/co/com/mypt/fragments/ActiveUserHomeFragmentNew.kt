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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import co.com.mypt.Api.Constants.HAS_GYM
import co.com.mypt.Api.Constants.HAS_HOME
import co.com.mypt.Api.Constants.PASS_DATA
import co.com.mypt.Api.Constants.delayMillis
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.BookingScreen.RescheduledUpComingBookingDetailActivity
import co.com.mypt.BookingScreen.UpcomingBookingDetails
import co.com.mypt.ComingSoonViewMode
import co.com.mypt.Profile.NewUserProfileActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.ChooseLocationActivity
import co.com.mypt.activities.ComingSoonActivity
import co.com.mypt.activities.ComingSoonActivity.Companion.KEY_VIEW_MODE
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.activities.TrainerGroupActivity
import co.com.mypt.adapter.GetPlanDetailAdapter
import co.com.mypt.adapter.HomeCertificateBannerAdapter
import co.com.mypt.adapter.HomeSmartSuggestionsAdapter
import co.com.mypt.adapter.StoryAdapter
import co.com.mypt.adapter.SubscriptionDateSlotAdapter
import co.com.mypt.adapter.UpcomingSessionsAdapter
import co.com.mypt.curvedBottomNavigation.dpToPx
import co.com.mypt.databinding.FragmentActiveUserHomeNewBinding
import co.com.mypt.fragments.viewModels.GuestUserViewModel
import co.com.mypt.model.GetPlansResponse
import co.com.mypt.model.Group
import co.com.mypt.model.NearByGymModel
import co.com.mypt.model.RenewalUpgradeModel
import co.com.mypt.model.Slot
import co.com.mypt.model.SubscriptionSlotsResponse
import co.com.mypt.model.TrainerGroupDetail
import co.com.mypt.model.TrainerStudiosResponse
import co.com.mypt.model.UpcomingClassModel
import co.com.mypt.model.UpcomingSessionsModel
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
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ActiveUserHomeFragmentNew : Fragment(), View.OnTouchListener,
    ViewTreeObserver.OnScrollChangedListener {
    var upcomingClassArraylist = ArrayList<UpcomingClassModel>()
    var nearByGymArraylist = ArrayList<NearByGymModel>()
//    lateinit var red_circle: ImageView
    lateinit var imUpcomingBlur: ImageView
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
    lateinit var bindingView: FragmentActiveUserHomeNewBinding
    var renewalUpgradeArraylist = ArrayList<RenewalUpgradeModel>()
    var plansResponse: GetPlansResponse?=null
    var group: Group?=null
    private var progressDialog: Dialog? = null
    private lateinit var homeCertificateBannerAdapter: HomeCertificateBannerAdapter
    private var autoScrollJob: Job? = null

    private  var addressId: String= ""

    companion object {
        private const val KEY_LAT = "lat"
        private const val KEY_LNG = "lng"
        private const val HOME_BACKGROUND_ID = 4
        private const val BOOK_SESSION_ID = 9
        private const val TOP_UP_PLAN_ID = 10
        private const val RENEW_PLAN_ID = 11
        private const val UPGRADE_PLAN_ID = 12
        const val BACKGROUND_ADDRESS_ID = 13
        private const val OFFER_BANNER_ID = 14
         const val BOOK_FREE_ASSESSMENT = 15
        private const val KEY_ADD = "add"

        fun newInstance(param1: String, param2: String, param3: String): ActiveUserHomeFragmentNew {
            val fragment = ActiveUserHomeFragmentNew()
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
        bindingView = FragmentActiveUserHomeNewBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        edit = sharedPreferences.edit()
        tvlocation = bindingView.location
        tvProfile = bindingView.tvProfile

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
                openHomeGymActivity()
            } else {
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }

        }

        bindingView.topUpPlan.setOnClickListener { openComingSoonScreen(ComingSoonViewMode.TOPU_UP) }
        bindingView.renewPlan.setOnClickListener { openComingSoonScreen(ComingSoonViewMode.RENEW_PLAN) }
        bindingView.upgradePlan.setOnClickListener { openComingSoonScreen(ComingSoonViewMode.UPGRADE_PLAN) }
        bindingView.tvBookingViewAll.setOnClickListener {
            (activity as? MainActivity)?.selectBottomItem(
                R.id.bookings
            )
        }
        bindingView.profileView.setOnClickListener {
            val intent = Intent(requireContext(), NewUserProfileActivity::class.java)
            startActivity(intent)
        }

        childFragmentManager.setFragmentResultListener(
            TrainerSlotsBottomSheet.TRAINER_SLOT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            viewModel.slotId = bundle.getInt(TrainerSlotsBottomSheet.KEY_SELECTED_SLOT_ID, 0)
            viewModel.trainerId = bundle.getInt(TrainerSlotsBottomSheet.KEY_TRAINER_ID, 0)
           viewModel.workType = bundle.getString(TrainerSlotsBottomSheet.KEY_WORK_TYPE,"")
            if(viewModel.workType.equals("home", ignoreCase = true)){
                openQuickBookReviewAssessmentBottomSheet(viewModel.trainerId,viewModel.slotId,viewModel.workType)
            }else{
                fetchTrainerStudios(viewModel.trainerId.toString())
            }
        }
        childFragmentManager.setFragmentResultListener(
            HomeTrainerBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            viewModel.studioId = bundle.getInt(HomeTrainerBottomSheet.KEY_SELECTED_STUDIO_ID, 0)
            val isQuickBookFlow = bundle.getBoolean(HomeTrainerBottomSheet.KEY_IS_QUICK_BOOK_FLOW, false)

            if(isQuickBookFlow) {
                openQuickBookReviewAssessmentBottomSheet(viewModel.trainerId, viewModel.slotId, viewModel.workType,viewModel.studioId)
            }
        }

        getAddressData()
        return bindingView.root
    }

    private fun openQuickBookReviewAssessmentBottomSheet(
        trainerId: Int,
        slotId: Int,
        workType: String,
        studioId:Int?=null
    ) {
        QuickBookReviewBottomSheet.newInstance(slotId, trainerId, workType,addressId,studioId).show(childFragmentManager,"QuickBookReviewBottomSheet")
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

    private fun openComingSoonScreen(viewMode:Int){
        val intent = Intent(context, ComingSoonActivity::class.java)
        intent.putExtra(KEY_VIEW_MODE,viewMode)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = UserViewModelFactory()

        viewModel = ViewModelProvider(this, factory).get(GuestUserViewModel::class.java)
        collectUsers()
        viewModel.getStories("Bearer " + sharedPreferences.getString("token", ""))
        viewModel.getContent("Bearer " + sharedPreferences.getString("token", ""))
        getBookingData()
        getRenewalPlanList()
        val dates = getNextSevenDays()
        val dateAdapter = SubscriptionDateSlotAdapter(dates) { date, position ->
            getSubscriptionSlots(date)
        }
        bindingView.dateSlotRecyclerView.adapter = dateAdapter
        bindingView.homeSuggestionsRecyclerView.addItemDecoration(
            HorizontalSpaceItemDecoration(20.dpToPx(requireContext()), middleSpace = 15.dpToPx(requireContext()) )
        )
        getSubscriptionSlots(dates.first())

        bindingView.btnViewTeam.setOnClickListener {
            group?.id?.let { groupId -> getGroupDetail(groupId) }
        }
        bindingView.upcomingSessionsRecyclerView.addItemDecoration(
            HorizontalSpaceItemDecoration(20.dpToPx(requireContext()))
        )
        bindingView.homePlansRecyclerView.addItemDecoration(
            HorizontalSpaceItemDecoration(20.dpToPx(requireContext()),middleSpace = 15.dpToPx(requireContext()))
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
                            bindingView.viewPagerBanner.visibility = View.GONE
                            bindingView.dotsIndicator.visibility = View.GONE
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
                            //binding.progressBar.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            println("DDLDLDLDLDL")
                            // binding.progressBar.visibility = View.GONE
                            if (state.data?.isNullOrEmpty() == true) {
                                bindingView.tvNodatMyPt.visibility = View.VISIBLE
                                bindingView.storiesListRecyclerView.visibility = View.GONE
                            } else {
                                bindingView.storiesListRecyclerView.adapter =
                                    StoryAdapter(requireContext(), state.data, longitude, latitude)
                                bindingView.tvNodatMyPt.visibility = View.GONE
                                bindingView.storiesListRecyclerView.visibility = View.VISIBLE
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

                viewModel.contentState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            println("1111")
                        }

                        is UiState.Success -> {
                            println("11112222")
                            if (state.data?.isNullOrEmpty() == true) {

                            } else {

                                val home_background =
                                    state.data?.firstOrNull { it?.id == HOME_BACKGROUND_ID }
                                val book_session = state.data?.firstOrNull { it?.id == BOOK_SESSION_ID }
                                val topup_plan =
                                    state.data?.firstOrNull { it?.id == TOP_UP_PLAN_ID }
                                val renew_plan =
                                    state.data?.firstOrNull { it?.id == RENEW_PLAN_ID }
                                val upgrade_plan =
                                    state.data?.firstOrNull { it?.id == UPGRADE_PLAN_ID }
                                val offer_banner =
                                    state.data?.firstOrNull { it?.id == OFFER_BANNER_ID }
                                    val bgAddress = state.data?.firstOrNull { it?.id == BACKGROUND_ADDRESS_ID }

                                if (home_background != null)
                                    Glide.with(requireContext())
                                        .load(home_background?.image).fitCenter()
                                        .into(bindingView.backgroundImg)
                                if (offer_banner != null)
                                    Glide.with(requireContext()).load(offer_banner?.image)
                                        .fitCenter().into(bindingView.homeBanner)
                                // else bindingView.homeBanner.visibility=View.GONE
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

                                Glide.with(requireContext()).load(book_session?.image).fitCenter()
                                    .into(bindingView.homePt)
                                Glide.with(requireContext()).load(renew_plan?.image)
                                    .fitCenter().into(bindingView.renewPlan)
                                Glide.with(requireContext()).load(topup_plan?.image).fitCenter()
                                    .into(bindingView.topUpPlan)
                                Glide.with(requireContext()).load(upgrade_plan?.image).fitCenter()
                                    .into(bindingView.upgradePlan)
                            }
                        }

                        is UiState.Error -> {
                            println("11113333")
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
    }

    private fun getgymList(latitude: Double, longitude: Double) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(), "")
        progressDialog.show()
        Log.e(
            "gymListApi",
            "" + ApiURL.getTrainer + "0" + "&tag_id=" + "0" + "&type=" + "gym" + "&long=" + longitude + "&lat=" + latitude
        )
        GetMethod(
            ApiURL.getTrainer + "0" + "&tag_id=" + "0" + "&type=" + "gym" + "&long=" + longitude + "&lat=" + latitude,
            activity
        ).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                nearByGymArraylist.clear()
                Log.e("getGymListResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        var jsonArrayList = jsonObj.optJSONObject("data").optJSONArray("studios")
                        if (jsonArrayList.length() > 0) {
                            for (i in 0 until jsonArrayList.length()) {
                                var jsonObject1 = jsonArrayList.optJSONObject(i)
                                val nearByGymModel = NearByGymModel()
                                nearByGymModel.name = jsonObject1.optString("name")
                                nearByGymModel.id = jsonObject1.optString("id")
                                nearByGymModel.distance = jsonObject1.optString("distance")
                                nearByGymModel.slot = jsonObject1.optString("slot")
                                nearByGymModel.noOfRating = jsonObject1.optString("noOfRating")
                                nearByGymModel.averageRating =
                                    jsonObject1.optString("averageRating")
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
//                            nearByGymRecyclerView.adapter = NearByGymAdapter(context,nearByGymArraylist,latitude,longitude,"gym")
//                            nearByGymRecyclerView.visibility= View.VISIBLE

                        } else {
//                            nearByGymRecyclerView.visibility= View.GONE

                        }
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
//                            getTrainerList()
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
//                    getTrainerList()
//                    getgymList(latitude!!, longitude!!)
//                    getClasses(latitude!!, longitude!!)
                }
            } else {
                tvlocation.visibility = View.VISIBLE
                tvlocation.text = chooseAddress
                latitude = lat.toDouble()
                longitude = long.toDouble()
//                getTrainerList()
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
//                getTrainerList()
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

    var upcomingSessionsArraylist = ArrayList<UpcomingSessionsModel>()
    private fun getBookingData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(), "")
        progressDialog.show()
        var api = ""
        api = ApiURL.getbooking + "2" + "&date=" + "" + "&session_type=" + "" + "&location=" + ""


        GetMethod(api, activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                upcomingSessionsArraylist.clear()
                Log.e("getBookingResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        var jsonArray = jsonObj.optJSONArray("data")
                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                var jsonObject = jsonArray.optJSONObject(i)
                                var upcomingbokingModel = UpcomingSessionsModel()

                                upcomingbokingModel.id = jsonObject.optString("id")
                                upcomingbokingModel.type = jsonObject.optString("type")
                                upcomingbokingModel.timing = jsonObject.optString("timing")
                                upcomingbokingModel.distance = jsonObject.optString("distance")
                                upcomingbokingModel.selected_slot =
                                    jsonObject.optString("selected_slot")
                                upcomingbokingModel.session_type =
                                    jsonObject.optString("session_type")
                                upcomingbokingModel.duration = jsonObject.optString("duration")
                                upcomingbokingModel.trainer = jsonObject.optString("trainer")
                                upcomingbokingModel.trainer_image =
                                    jsonObject.optString("trainer_image")
                                upcomingbokingModel.location = jsonObject.optString("location")
                                upcomingbokingModel.is_reschedule =
                                    jsonObject.optString("is_reschedule")
                                upcomingbokingModel.msg = jsonObject.optString("msg")
                                upcomingbokingModel.is_Trainer = jsonObject.optString("isTrainer")
                                upcomingbokingModel.workout_focus =
                                    jsonObject.optJSONArray("workout_focus")
                                upcomingSessionsArraylist.add(upcomingbokingModel)
                            }
                           // if(upcomingSessionsArraylist.isNotEmpty()){

                            bindingView.upcomingSessionsRecyclerView.adapter =
                                UpcomingSessionsAdapter(context, upcomingSessionsArraylist, onCheckInClick = { model ->
                                    if (model.is_Trainer.equals("false")) {
                                        val intent = Intent(requireContext(), UpcomingBookingDetails::class.java)
                                        intent.putExtra("bookingid",model.id)
                                        intent.putExtra("type",model.type)
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(activity, RescheduledUpComingBookingDetailActivity::class.java)
                                        intent.putExtra("bookingid",model.id)
                                        intent.putExtra("type",model.type)
                                        startActivity(intent)
                                    }
                                })
                            bindingView.llNodataMyBooking.visibility = View.GONE
                            bindingView.upcomingSessionsRecyclerView.visibility = View.VISIBLE

                        } else {
                            bindingView.upcomingSessionsRecyclerView.visibility = View.GONE
                            bindingView.llNodataMyBooking.visibility = View.VISIBLE
                        }
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

    private fun getRenewalPlanList() {
        GetMethod(ApiURL.getRenewalPlanDetails, activity).startMethod(object : ResponseData {
            override fun response(data: String?) {
               // renewalUpgradeArraylist.clear()
                try {
                     plansResponse = Gson().fromJson(
                        data,
                        GetPlansResponse::class.java
                    )
                    plansResponse?.let {
                        bindingView.homePlansRecyclerView.adapter = GetPlanDetailAdapter(
                            context,
                            it.data,
                            onUseSessionClick = { planDetail ->
                                openHomeGymActivity(planDetail.type)
                            },
                            onBuyMoreClick = {
                                openComingSoonScreen(ComingSoonViewMode.TOPU_UP)
                            }
                        )
                    }
                   /* val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        val jsonArray = jsonObj.optJSONArray("data")
                        if (jsonArray.length() > 0) {
                            val subscriptionTypeArrayList = ArrayList<String>()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.optJSONObject(i)
                                val renewalUpgradeModel = RenewalUpgradeModel()
                                renewalUpgradeModel.id = jsonObject.optString("id")
                                renewalUpgradeModel.type = jsonObject.optString("type")
                                renewalUpgradeModel.remSession =
                                    jsonObject.optString("remaining_sessions")
                                renewalUpgradeModel.validity = jsonObject.optString("validity_days")
                                renewalUpgradeModel.sessions = jsonObject.optString("sessions")
                                renewalUpgradeModel.name = jsonObject.optString("name")
                                renewalUpgradeModel.amount = jsonObject.optString("amount")
                                renewalUpgradeModel.remainingDays =
                                    jsonObject.optString("remaining_days")
                                renewalUpgradeModel.msg = jsonObject.optString("msg")
                                renewalUpgradeModel.isUpgrade = jsonObject.optBoolean("isUpgrade")
                                renewalUpgradeModel.isShow = jsonObject.optBoolean("isShow")
                                renewalUpgradeModel.isRenew = jsonObject.optBoolean("renew_new")
                                renewalUpgradeArraylist.add(renewalUpgradeModel)
                                if (jsonObject.optBoolean("renew_new"))
                                    subscriptionTypeArrayList.add(jsonObject.optString("type"))

                            }
                            bindingView.renewUpgradeRecyclerView.adapter = RenewUpgradeAdapter(
                                context,
                                renewalUpgradeArraylist,
                                subscriptionTypeArrayList
                            )
                            bindingView.renewUpgradeRecyclerView.visibility = View.VISIBLE
                            bindingView.smartSuggestion.visibility= View.VISIBLE
                            val snapHelper = LinearSnapHelper()
                            snapHelper.attachToRecyclerView(bindingView.renewUpgradeRecyclerView)
                        } else {
                            bindingView.smartSuggestion.visibility= View.GONE
                            bindingView.renewUpgradeRecyclerView.visibility = View.GONE
                        }
                    }*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                error!!.printStackTrace()
            }
        })
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


                        if (!jsonObj.optJSONObject("data").optBoolean("userHaveRequest")) {
//                            red_circle.visibility = View.GONE
                        } else {
//                            red_circle.visibility = View.VISIBLE

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

    private fun getNextSevenDays(): List<String> {
        val days = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val calendar = Calendar.getInstance()

        repeat(7) {
            days.add(sdf.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }
    private fun getSubscriptionSlots(date: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(), "")
        progressDialog.show()

        val api = ApiURL.getSubscriptionSlots + date

        GetMethod(api, requireContext()).startMethod(object : ResponseData {

            override fun response(data: String?) {
                progressDialog.dismiss()

                try {
                    val subscriptionSlotsResponse = Gson().fromJson(data, SubscriptionSlotsResponse::class.java)
                    val trainers = subscriptionSlotsResponse.data.trainers

                    val hasTrainers = !trainers.isNullOrEmpty()

                    bindingView.apply {

                        // Trainers list
                        if (hasTrainers) {
                            homeSuggestionsRecyclerView.adapter =
                                HomeSmartSuggestionsAdapter(
                                    requireContext(),
                                    trainers!!,
                                    listenerQuickBook = { trainer ->
                                        TrainerSlotsBottomSheet.newInstance(
                                            workType = subscriptionSlotsResponse.data.type,
                                            trainerId = trainer.id,
                                            slotList = trainer.slots as ArrayList<Slot>,
                                            subscriptionSlotsResponse.data.dateFormatted
                                        )
                                            .show(childFragmentManager, "TrainerSlotsBottomSheet")
                                    },
                                    listenerFullSchedule = { trainer ->
                                        Toast.makeText(
                                            requireContext(),
                                            "FullSchedule: ${trainer.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                        }

                        homeSuggestionsRecyclerView.visibility =
                            if (hasTrainers) View.VISIBLE else View.GONE

                        llNodataSubscription.visibility =
                            if (hasTrainers) View.GONE else View.VISIBLE

                        // Group info
                        subscriptionSlotsResponse.data.group?.let { grp ->
                            tvGroupName.text = grp.name
                            tvGroupSubTitle.text = grp.msg

                            Glide.with(requireContext())
                                .load(grp.image)
                                .fitCenter()
                                .placeholder(R.drawable.train_group_img)
                                .error(R.drawable.train_group_img)
                                .into(ivGroup)
                        }
                        group = subscriptionSlotsResponse.data.group

                        // Training team visibility
                        rlTrainingTeam.visibility =
                            if (subscriptionSlotsResponse.data.isGroup) View.VISIBLE else View.GONE
                        llTrainingTeamHeader.visibility =
                            if (subscriptionSlotsResponse.data.isGroup) View.VISIBLE else View.GONE
                        //llNodataTeam.visibility = if (!subscriptionSlotsResponse.data.isGroup) View.VISIBLE else View.GONE
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error?.printStackTrace()
            }
        })
    }

    private fun showComingSoonMsg() {
        Toast.makeText(
            requireContext(), "Coming soon", Toast.LENGTH_SHORT
        ).show()
    }

    private fun getGroupDetail(groudId: Int) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()
        val finalPath = ApiURL.getGroupDetail + groudId
        GetMethod(finalPath, requireContext()).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                try {
                    val groupDetail = Gson().fromJson(data, TrainerGroupDetail::class.java)

                    if (groupDetail.status == true) {
                        data?.let { openGroupDetailScreen(it, groupDetail.data?.group?.type) }
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
    private fun openGroupDetailScreen(data: String, type:String?){

        edit.putString("typeWorkout",type).apply()
        val intent= Intent(requireContext(), TrainerGroupActivity::class.java)
        intent.putExtra(PASS_DATA,data)
       // intent.putExtra("studio_id",studio_id) // need this to be discussed
        intent.putExtra("long",longitude)
        intent.putExtra("lat",latitude)
        intent.putExtra("isFromHome",true)
       // intent.putExtra("trainerId",trainerId) // need this to be discussed
        startActivity(intent)
    }

    private fun openHomeGymActivity(type: String? = null) {
        val intent = Intent(requireContext(), HomeGymTrainerActivity::class.java)
        val typeSet = plansResponse?.data?.map { it.type }?.toSet() ?: emptySet()
        val hasHome = "home" in typeSet
        val hasGym = "gym" in typeSet
        intent.putExtra(HAS_HOME, hasHome)
        intent.putExtra(HAS_GYM, hasGym)
        type?.let {
            intent.putExtra(
                HomeGymTrainerActivity.KEY_PT_TYPE,
                if (it == HomeGymTrainerActivity.KEY_HOME) HomeGymTrainerActivity.KEY_HOME else HomeGymTrainerActivity.KEY_WORK
            )
        }
        startActivity(intent)
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

    private fun fetchTrainerStudios(trainerId: String){
        viewModel.getTrainerStudios("Bearer " + sharedPreferences.getString("token", ""), latitude,longitude,trainerId)
    }

    private fun openTrainerStudioBottomSheet(response: TrainerStudiosResponse.Data?) {
        response?.let {
            HomeTrainerBottomSheet.newInstance(it,true)
                .show(childFragmentManager, "HomeTrainerBottomSheet")
        }
    }

    override fun onStop() {
        super.onStop()
        hideProgress()
    }
}
