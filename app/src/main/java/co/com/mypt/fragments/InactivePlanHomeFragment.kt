package co.com.mypt.fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
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
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.ActiveChallenge.MyActivityChallengeActivity
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.Goals.HydrationActivity
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.GymWorkout.withTrainer.SelectGymWorkoutActivity
import co.com.mypt.More.MyMealsActivity
import co.com.mypt.Notification.NotificationListActivity
import co.com.mypt.Profile.NewUserProfileActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.UpComingClasses.UpComingClassActivity
import co.com.mypt.activities.ChooseLocationActivity
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.adapter.NearByGymAdapter
import co.com.mypt.adapter.RenewUpgradeAdapter
import co.com.mypt.adapter.ShopCategoryAdapter
import co.com.mypt.adapter.ShopProductsAdapter
import co.com.mypt.adapter.StreakAdapter
import co.com.mypt.adapter.StreakBadgeAdapter
import co.com.mypt.adapter.TransformationAdapter
import co.com.mypt.adapter.UpcomingClassAdapter
import co.com.mypt.adapter.UpcomingMealsAdapter
import co.com.mypt.adapter.UpcomingSessionsAdapter
import co.com.mypt.adapter.WorkoutAdapter
import co.com.mypt.adapter.WorkoutLibraryDateAdapter
import co.com.mypt.model.AllWorkoutTypeModel
import co.com.mypt.model.NearByGymModel
import co.com.mypt.model.RenewalUpgradeModel
import co.com.mypt.model.ShopCategoryModel
import co.com.mypt.model.ShopProductsModel
import co.com.mypt.model.StreakBadgeModel
import co.com.mypt.model.StreakModel
import co.com.mypt.model.TransformationModel
import co.com.mypt.model.UpcomingClassModel
import co.com.mypt.model.UpcomingMealsModel
import co.com.mypt.model.UpcomingSessionsModel
import co.com.mypt.model.WorkoutDateListModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import co.com.mypt.utils.CircularFillView
import co.com.mypt.utils.CustomMarkerView
import co.com.mypt.utils.DashedCircularIndicatorView
import co.com.mypt.utils.SemiCircleProgressViewHome
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
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
import com.google.android.material.progressindicator.LinearProgressIndicator
import co.com.mypt.utils.CarouselRecyclerview
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class InactivePlanHomeFragment : Fragment(), View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener {

    var datename=""
    var calorie_burn_id=""
    var calorie_intake_id=""
    var waterIntake_id=""
    lateinit var upcomingSessionsRecyclerView : RecyclerView
    lateinit var upcomingMealsRecyclerView : RecyclerView
    lateinit var upcomingClassRecyclerView : RecyclerView
    lateinit var transformationRecyclerView : RecyclerView
    lateinit var shopProductsRecyclerView : RecyclerView
    lateinit var shopCategoryRecyclerView : RecyclerView
    lateinit var nearByGymRecyclerView : RecyclerView
    lateinit var workoutDateRecyclerView : RecyclerView
    lateinit var workoutRecyclerView : RecyclerView
    lateinit var carouselRecyclerview: CarouselRecyclerview

    var renewalUpgradeArraylist = ArrayList<RenewalUpgradeModel>()
    var upcomingSessionsArraylist = ArrayList<UpcomingSessionsModel>()
    var upcomingMealsArraylist = ArrayList<UpcomingMealsModel>()
    var upcomingClassArraylist = ArrayList<UpcomingClassModel>()
    var transformationArraylist = ArrayList<TransformationModel>()
    var shopProductsArraylist = ArrayList<ShopProductsModel>()
    var shopCategoryArraylist = ArrayList<ShopCategoryModel>()
    var nearByGymArraylist = ArrayList<NearByGymModel>()
    var workoutDateArraylist = ArrayList<WorkoutDateListModel>()
    var workoutArraylist = ArrayList<AllWorkoutTypeModel>()
    var streakBadgeArrayList = ArrayList<StreakBadgeModel>()

    lateinit var streakRecyclerView : RecyclerView

    var streakArrayList = ArrayList<StreakModel>()

    lateinit var streakBadgeAdapter: StreakBadgeAdapter

    private lateinit var connectWatch: ImageView
    private lateinit var bookATrainer: ImageView
    private lateinit var improfile: ImageView
    private lateinit var im_membership: ImageView
    private lateinit var imUpcomingBlur: ImageView
    private lateinit var notification: ImageView

    private lateinit var userName: TextView
    private lateinit var linearWaterIntake: LinearLayout
    private lateinit var intakeBurnCalories: TextView
    private lateinit var d1: TextView
    private lateinit var w1: TextView
    private lateinit var m1: TextView
    private lateinit var y1: TextView
    private lateinit var all: TextView
    private lateinit var intakeTotalCalories: TextView

    private lateinit var totalWaterIntake: TextView
    private lateinit var calorieIntakeProgressBar: LinearProgressIndicator

    private lateinit var mScrollView: ScrollView

    private lateinit var pointsLayout: LinearLayout
    private lateinit var activeChallenge: LinearLayout
    private lateinit var headerLayout: LinearLayout
    private lateinit var calorieLayout: CardView

    lateinit var sharedPreferences: SharedPreferences

    lateinit var circularBlueView : CircularFillView
    lateinit var circularOrangeView : CircularFillView

    lateinit var completedPercentage : TextView
    lateinit var intakeCalories : TextView
    lateinit var caloriesBurn : TextView

    lateinit var allUpcomingMeals : TextView
    lateinit var tvlocation : TextView
    lateinit var steps : TextView
    lateinit var distance : TextView
    lateinit var allUpcomingClass : TextView
    lateinit var tvnoWorkout : TextView
    lateinit var waterIntake : TextView
    lateinit var addWaterIntake : TextView
    lateinit var upcomingSessions : TextView
    lateinit var allNearByGym : TextView
    lateinit var totalSteps : TextView
    lateinit var waterIntakeProgress : ProgressBar

    private lateinit var lineChart: LineChart
    private lateinit var stepsLineChart: LineChart

    private lateinit var semiProgress: SemiCircleProgressViewHome
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
    var lat =""
    var chooseAddress = ""
    var long = ""
    var username = ""
    var isPermissionDialogOpen = false
    private lateinit var linearProfile: LinearLayout
    private lateinit var linearCalorieBurn: LinearLayout
    private lateinit var linearCalorieIntake: LinearLayout
    private lateinit var imCreateMeal: ImageView
    private lateinit var logout: ImageView
    lateinit var edit:SharedPreferences.Editor

    lateinit var tvmember : TextView
    lateinit var renewUpgradeRecyclerView : RecyclerView
    lateinit var red_circle : ImageView
    lateinit var imWorkoutblur : ImageView
    var workouttypeArraylist = ArrayList<AllWorkoutTypeModel>()
    var date_number=""

    companion object {
        private const val KEY_Name = "username"
        private const val KEY_LAT = "lat"
        private const val KEY_LNG = "lng"
        private const val KEY_ADD = "add"

        fun newInstance(param1: String, param2: String, param3: String, param4: String): InactivePlanHomeFragment {
            val fragment = InactivePlanHomeFragment()
            val args = Bundle()
            args.putString(KEY_Name, param1)
            args.putString(KEY_LAT, param2)
            args.putString(KEY_LNG, param3)
            args.putString(KEY_ADD, param4)
            fragment.arguments = args
            return fragment
        }
    }

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_inactive_plan_home, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.edit().putString("typewithout", "").apply()
        edit=sharedPreferences.edit()
        red_circle = rootView.findViewById(R.id.red_circle)
        tvnoWorkout = rootView.findViewById(R.id.tvnoWorkout)
        activeChallenge = rootView.findViewById(R.id.activeChallenge)

        renewUpgradeRecyclerView = rootView.findViewById(R.id.renewUpgradeRecyclerView)
        logout = rootView.findViewById(R.id.logout)
        tvmember = rootView.findViewById(R.id.tvmember)
        imWorkoutblur = rootView.findViewById(R.id.imWorkoutblur)
        notification = rootView.findViewById(R.id.notification)

        allUpcomingMeals = rootView.findViewById(R.id.allUpcomingMeals)
        improfile = rootView.findViewById(R.id.improfile)
        allNearByGym = rootView.findViewById(R.id.allNearByGym)
        pointsLayout = rootView.findViewById(R.id.pointsLayout)
        linearWaterIntake = rootView.findViewById(R.id.linearWaterIntake)
        linearCalorieIntake = rootView.findViewById(R.id.linearCalorieIntake)
        linearCalorieBurn = rootView.findViewById(R.id.linearCalorieBurn)
        waterIntakeProgress = rootView.findViewById(R.id.waterIntakeProgress)
        calorieIntakeProgressBar = rootView.findViewById(R.id.calorieIntakeProgressBar)
        imCreateMeal = rootView.findViewById(R.id.imCreateMeal)
        tvlocation = rootView.findViewById(R.id.location)
        intakeBurnCalories = rootView.findViewById(R.id.intakeBurnCalories)
        intakeTotalCalories = rootView.findViewById(R.id.intakeTotalCalories)
        intakeCalories = rootView.findViewById(R.id.intakeCalories)
        totalWaterIntake = rootView.findViewById(R.id.totalWaterIntake)
        addWaterIntake = rootView.findViewById(R.id.addWaterIntake)
        headerLayout = rootView.findViewById(R.id.headerLayout)
        imUpcomingBlur = rootView.findViewById(R.id.imUpcomingBlur)
        mScrollView = rootView.findViewById(R.id.scrollView)
        allUpcomingClass = rootView.findViewById(R.id.allUpcomingClass)
        im_membership = rootView.findViewById(R.id.im_membership)
        carouselRecyclerview = rootView.findViewById(R.id.carouselRecyclerview)
        totalSteps = rootView.findViewById(R.id.totalSteps)
        calorieLayout = rootView.findViewById(R.id.calorieLayout)
        connectWatch = rootView.findViewById(R.id.connectWatch)
        semiProgress = rootView.findViewById(R.id.semiProgress)
        linearProfile = rootView.findViewById(R.id.linearProfile)
        d1 = rootView.findViewById(R.id.d1)
        w1 = rootView.findViewById(R.id.w1)
        m1 = rootView.findViewById(R.id.m1)
        y1 = rootView.findViewById(R.id.y1)
        all = rootView.findViewById(R.id.all)
        waterIntake = rootView.findViewById(R.id.waterIntake)
        upcomingSessions = rootView.findViewById(R.id.upcomingSessions)

        mScrollView.setOnTouchListener(this)
        mScrollView.viewTreeObserver.addOnScrollChangedListener(this)

        workoutRecyclerView = rootView.findViewById(R.id.workoutRecyclerView)
        workoutDateRecyclerView = rootView.findViewById(R.id.workoutDateRecyclerView)
        upcomingMealsRecyclerView = rootView.findViewById(R.id.upcomingMealsRecyclerView)


        upcomingSessionsRecyclerView = rootView.findViewById(R.id.upcomingSessionsRecyclerView)
        transformationRecyclerView = rootView.findViewById(R.id.transformationRecyclerView)
        upcomingClassRecyclerView = rootView.findViewById(R.id.upcomingClassRecyclerView)
        shopCategoryRecyclerView = rootView.findViewById(R.id.shopCategoryRecyclerView)
        shopProductsRecyclerView = rootView.findViewById(R.id.shopProductsRecyclerView)
        nearByGymRecyclerView = rootView.findViewById(R.id.nearByGymRecyclerView)
        streakRecyclerView = rootView.findViewById(R.id.streakRecyclerView)
        completedPercentage = rootView.findViewById(R.id.completedPercentage)

        distance = rootView.findViewById(R.id.distance)
        caloriesBurn = rootView.findViewById(R.id.caloriesBurn)
        steps = rootView.findViewById(R.id.steps)
        circularBlueView = rootView.findViewById(R.id.circularBlueView)
        circularOrangeView = rootView.findViewById(R.id.circularOrangeView)
        lineChart = rootView.findViewById(R.id.lineChart)
        stepsLineChart = rootView.findViewById(R.id.stepsLineChart)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        activeChallenge.setOnClickListener {
            var intent= Intent(activity, MyActivityChallengeActivity::class.java)
            startActivity(intent)
            /*var intent= Intent(activity, ActiveChallengeActivity::class.java)
            startActivity(intent)*/
        }
        if (!Places.isInitialized()) {
            Places.initialize(activity, "AIzaSyBVhnjhV5pynJXnSN2VTn-zhLGeIc7VcRw")
        }

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
        linearProfile.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(Constants.token,"").toString() != ""){
                val intent = Intent(context, NewUserProfileActivity::class.java)
                startActivity(intent)

            }else{
                val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }
        //getUserGoal()
        //getMealsData()

        tvlocation.setOnClickListener{
            var intent=Intent(activity, ChooseLocationActivity::class.java)
            startActivity(intent)
        }
        linearWaterIntake.setOnClickListener{
            var intent=Intent(activity, HydrationActivity::class.java)
            intent.putExtra("waterIntake_id",waterIntake_id)
            startActivity(intent)
        }
        linearCalorieIntake.setOnClickListener{
            var intent=Intent(activity, ChooseLocationActivity::class.java)
            startActivity(intent)
        }
        linearCalorieBurn.setOnClickListener{
            var intent=Intent(activity, ChooseLocationActivity::class.java)
            startActivity(intent)
        }

        transformationRecyclerView.adapter = TransformationAdapter(context,transformationArraylist)

        shopProductsRecyclerView.adapter = ShopProductsAdapter(context,shopProductsArraylist)
        shopCategoryRecyclerView.adapter = ShopCategoryAdapter(context,shopCategoryArraylist)



        var k = 0
        for(i in 0 until 4){
            val c: Calendar = Calendar.getInstance()
            val df = SimpleDateFormat("dd MMM, EEE")
            val sdf = SimpleDateFormat("YYYY-MM-dd")
            val currentDate =  df.format(c.time)
            var tempDate = sdf.format(c.time)
            val model = WorkoutDateListModel()
            when (i) {
                0 -> {
                    c.add(Calendar.DATE, -1)
                    model.workoutDate = df.format(c.time)
                    model.name = df.format(c.time)
                    model.senddate = sdf.format(c.time)

                }
                1 -> {
                    model.workoutDate = currentDate
                    model.name = "Today"
                    date_number= tempDate
                    model.senddate = tempDate
                    datename="Today"
                }
                else -> {
                    k++
                    c.add(Calendar.DATE, k)
                    model.workoutDate = df.format(c.time)
                    model.name = df.format(c.time)
                    model.senddate = sdf.format(c.time)

                }
            }
            workoutDateArraylist.add(model)
        }
        workoutDateRecyclerView.adapter = WorkoutLibraryDateAdapter(context,workoutDateArraylist)
        getMyWorkout(date_number)

        userName = rootView.findViewById(R.id.userName)
        bookATrainer = rootView.findViewById(R.id.bookATrainer)

        bookATrainer.setOnClickListener {
            val intent = Intent(context,HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }
        im_membership.setOnClickListener {
            val intent = Intent(context,SelectGymWorkoutActivity::class.java)
            startActivity(intent)
        }
        notification.setOnClickListener {
            val intent = Intent(context, NotificationListActivity::class.java)
            startActivity(intent)
        }

        streakBadgeAdapter = StreakBadgeAdapter(context, streakBadgeArrayList)
        carouselRecyclerview.adapter = streakBadgeAdapter
        carouselRecyclerview.set3DItem(false)
        carouselRecyclerview.setInfinite(false)
        carouselRecyclerview.setAlpha(false)
        carouselRecyclerview.setFlat(false)
        carouselRecyclerview.setIsScrollingEnabled(true)
        carouselRecyclerview.setIntervalRatio(0.8f)

        circularBlueView.progressPaint.color = resources.getColor(R.color.progressBlue,null)
        circularBlueView.cornerRadius = 80f
        val animator = ObjectAnimator.ofFloat(circularBlueView, "progress", 0f, .60f)
        animator.duration = 5000 // 5 seconds animation
        animator.start()

        circularOrangeView.progressPaint.color = resources.getColor(R.color.orangecolor,null)
        circularOrangeView.cornerRadius = 60f
        val animator1 = ObjectAnimator.ofFloat(circularOrangeView, "progress", 0f, .80f)
        animator1.duration = 5000 // 5 seconds animation
        animator1.start()

        textShader(completedPercentage)
        textShader(intakeCalories)
        textShader(caloriesBurn)
        textShader(steps)
        textShader(distance)


        createCalorieLineChart()
        createStepsLineChart()

        val progressView = rootView.findViewById<DashedCircularIndicatorView>(R.id.progressView)
        progressView.setProgress(75f)

        d1.setOnClickListener {
            changeBackground(d1,w1,m1,y1,all)
        }

        w1.setOnClickListener {
            changeBackground(w1,d1,m1,y1,all)
        }

        m1.setOnClickListener {
            changeBackground(m1,w1,d1,y1,all)
        }

        y1.setOnClickListener {
            changeBackground(y1,w1,m1,d1,all)
        }

        all.setOnClickListener {
            changeBackground(all,w1,m1,y1,d1)
        }

        connectWatch.setOnClickListener {
            connectWatch.visibility = View.GONE
            calorieLayout.visibility = View.VISIBLE
        }

        semiProgress.viewTreeObserver.addOnGlobalLayoutListener {
            semiProgress.setProgressDrawable(R.drawable.progress_gradient)
        }

        pointsLayout.setOnClickListener {

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

        return rootView
    }

    private fun getRenewalPlanList() {
        GetMethod(ApiURL.getRenewalPlanDetails,activity).startMethod(object : ResponseData {
            override fun response(data: String?) {
                renewalUpgradeArraylist.clear()
                Log.e("getRenewalPlanResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        val jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            val subscriptionTypeArrayList = ArrayList<String>()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject=jsonArray.optJSONObject(i)
                                val renewalUpgradeModel= RenewalUpgradeModel()
                                renewalUpgradeModel.id=jsonObject.optString("id")
                                renewalUpgradeModel.type=jsonObject.optString("type")
                                renewalUpgradeModel.remSession=jsonObject.optString("remaining_sessions")
                                renewalUpgradeModel.validity=jsonObject.optString("validity_days")
                                renewalUpgradeModel.sessions=jsonObject.optString("sessions")
                                renewalUpgradeModel.name=jsonObject.optString("name")
                                renewalUpgradeModel.amount=jsonObject.optString("amount")
                                renewalUpgradeModel.remainingDays=jsonObject.optString("remaining_days")
                                renewalUpgradeModel.msg=jsonObject.optString("msg")
                                renewalUpgradeModel.isUpgrade=jsonObject.optBoolean("isUpgrade")
                                renewalUpgradeModel.isShow=jsonObject.optBoolean("isShow")
                                renewalUpgradeModel.isRenew=jsonObject.optBoolean("renew_new")
                                renewalUpgradeArraylist.add(renewalUpgradeModel)
                                if(jsonObject.optBoolean("renew_new"))
                                    subscriptionTypeArrayList.add(jsonObject.optString("type"))

                            }
                            renewUpgradeRecyclerView.adapter = RenewUpgradeAdapter(context, renewalUpgradeArraylist,subscriptionTypeArrayList)
                            renewUpgradeRecyclerView.visibility=View.VISIBLE

                            val snapHelper = LinearSnapHelper()
                            snapHelper.attachToRecyclerView(renewUpgradeRecyclerView)
                        }
                        else{
                            renewUpgradeRecyclerView.visibility=View.GONE
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            override fun error(error: VolleyError?) {
                error!!.printStackTrace()
            }
        })
    }


    private fun createStepsLineChart() {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 100f))
        entries.add(Entry(1f, 100f))
        entries.add(Entry(2f, 300f))
        entries.add(Entry(3f, 300f))
        entries.add(Entry(4f, 500f))
        entries.add(Entry(5f, 150f))

        // 2. Create a LineDataSet from the data
        val dataSet = LineDataSet(entries, "Steps Count")
        dataSet.color = Color.parseColor("#F38D1B")// Line color
        dataSet.mode = LineDataSet.Mode.STEPPED
        dataSet.setDrawCircles(false)

        dataSet.setDrawValues(false)
        dataSet.setValueTextColor(Color.WHITE)

        dataSet.lineWidth = 2f
        /*dataSet.setDrawCircles(true)  // Show circles on data points
        dataSet.setCircleColor(Color.BLUE)
        dataSet.setCircleRadius(5f)
        dataSet.setDrawValues(false)*/   // Hide values above the data points

        // 3.  Apply the gradient fill
        if (Utils.getSDKInt() >= 18) {
            val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.orange_green_gradient)
            dataSet.fillDrawable = drawable
            dataSet.setDrawFilled(true)
        } else {
            dataSet.fillColor = Color.parseColor("#F38D1A")
            dataSet.setDrawFilled(true) // Still need to enable filling even with a solid color
        }

        // 4. Create a LineData object with the DataSet
        val lineData = LineData(dataSet)

        /*stepsLineChart.renderer = RoundedSteppedRenderer(
            stepsLineChart,
            stepsLineChart.animator, // Correct animator
            stepsLineChart.viewPortHandler, // Correct ViewPortHandler
        )*/

        // 5. Set the data to the chart
        stepsLineChart.data = lineData

        // 6.  Customize the chart (optional)
        stepsLineChart.isHighlightPerTapEnabled = true
        stepsLineChart.description.isEnabled = false // Remove description label
        stepsLineChart.xAxis.setDrawGridLines(false) // Remove x-axis grid lines

        stepsLineChart.axisLeft.gridColor = Color.parseColor("#545454")

        stepsLineChart.axisLeft.setDrawGridLines(true)
        stepsLineChart.axisLeft.setDrawAxisLine(false)
        stepsLineChart.axisRight.isEnabled = false  // Remove right y-axis
        stepsLineChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM // X-Axis at the bottom


        val markerView = CustomMarkerView(context, R.layout.custom_step_marker_view)
        stepsLineChart.marker = markerView

// Automatically highlight the last value
        val lastEntry = entries.last()
        val highlight = Highlight(lastEntry.x, lastEntry.y, 0)
        stepsLineChart.highlightValue(highlight) // This ensures the marker stays visible

// Disable user interactions that remove the marker
        stepsLineChart.setOnTouchListener { _, _ -> true }

        /*stepsLineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                // Show the marker when a value is selected
                if (e == entries.last()){
                    val mv = CustomMarkerView(context, R.layout.custom_step_marker_view) // Replace YourActivity with your activity's name
                    mv.chartView = stepsLineChart
                    stepsLineChart.marker = mv
                    stepsLineChart.highlightValue(h) // Highlight the selected value
                }
                else{
                    stepsLineChart.highlightValue(null)
                }
            }

            override fun onNothingSelected() {
                // Hide the marker when nothing is selected
                stepsLineChart.highlightValue(null)
                stepsLineChart.marker = null
            }
        })*/

        val xAxis = stepsLineChart.xAxis
        xAxis.axisMinimum = 0f      // Set the minimum value to 0
        xAxis.axisMaximum = 10f    // Set the maximum value to 500
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return ""
            }
        }

        //Y-Axis Values
        val yAxis = stepsLineChart.axisLeft // Or chart.axisRight for the right Y-axis
        yAxis.textColor = Color.parseColor("#606060")
        yAxis.axisMinimum = 0f      // Set the minimum value to 0
        yAxis.axisMaximum = 1500f    // Set the maximum value to 500
        yAxis.granularity = 500f
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}" // Example: Add a currency symbol
            }
        }

        val legend = stepsLineChart.legend
        legend.isEnabled = false
        // 7. Refresh the chart
        stepsLineChart.invalidate()

    }

    private fun changeBackground(
        selectedTV: TextView?,
        tv1: TextView?,
        tv2: TextView?,
        tv3: TextView?,
        tv4: TextView?
    ) {
        selectedTV?.setTextColor(context?.resources?.getColor(R.color.black,null)!!)
        selectedTV?.background = context?.resources?.getDrawable(R.drawable.white_rectangle_8dp,null)

        tv1?.setTextColor(context?.resources?.getColor(R.color.headingcolor,null)!!)
        tv1?.background = null

        tv2?.setTextColor(context?.resources?.getColor(R.color.headingcolor,null)!!)
        tv2?.background = null

        tv3?.setTextColor(context?.resources?.getColor(R.color.headingcolor,null)!!)
        tv3?.background = null

        tv4?.setTextColor(context?.resources?.getColor(R.color.headingcolor,null)!!)
        tv4?.background = null
    }

    private fun createCalorieLineChart() {
        // 1. Generate chart data
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 200f))
        entries.add(Entry(1f, 250f))
        entries.add(Entry(2f, 180f))
        entries.add(Entry(3f, 300f))
        entries.add(Entry(4f, 220f))
        entries.add(Entry(5f, 350f))

        // 2. Create a LineDataSet from the data
        val dataSet = LineDataSet(entries, "My Activity")
        dataSet.color = Color.parseColor("#00C1AA")// Line color
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawCircles(false)

        dataSet.setDrawValues(false)
        dataSet.setValueTextColor(Color.WHITE)

        dataSet.lineWidth = 2f
        /*dataSet.setDrawCircles(true)  // Show circles on data points
        dataSet.setCircleColor(Color.BLUE)
        dataSet.setCircleRadius(5f)
        dataSet.setDrawValues(false)*/   // Hide values above the data points

        // 3.  Apply the gradient fill
        if (Utils.getSDKInt() >= 18) {
            val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.sea_green_gradient)
            dataSet.fillDrawable = drawable
            dataSet.setDrawFilled(true)
        } else {
            dataSet.fillColor = Color.parseColor("#00C1AA")
            dataSet.setDrawFilled(true) // Still need to enable filling even with a solid color
        }

        // 4. Create a LineData object with the DataSet
        val lineData = LineData(dataSet)

        // 5. Set the data to the chart
        lineChart.data = lineData

        // 6.  Customize the chart (optional)
        lineChart.isHighlightPerTapEnabled = true
        lineChart.description.isEnabled = false // Remove description label
        lineChart.xAxis.setDrawGridLines(false) // Remove x-axis grid lines

        lineChart.axisLeft.gridColor = Color.parseColor("#343739")

        lineChart.axisLeft.setDrawGridLines(true)
        lineChart.axisLeft.setDrawAxisLine(false)
        lineChart.axisRight.isEnabled = false  // Remove right y-axis
        lineChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM // X-Axis at the bottom

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                // Show the marker when a value is selected
                val mv = CustomMarkerView(context, R.layout.custom_marker_view) // Replace YourActivity with your activity's name
                mv.chartView = lineChart
                lineChart.marker = mv
                lineChart.highlightValue(h) // Highlight the selected value
            }

            override fun onNothingSelected() {
                // Hide the marker when nothing is selected
                lineChart.highlightValue(null)
                lineChart.marker = null
            }
        })

        //X-Axis Values
        val xAxis = lineChart.xAxis
        xAxis.textColor = Color.parseColor("#959595")
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Set the position of the X-axis
        xAxis.granularity = 1f // Optional: set to 1f to avoid displaying fractional values
        xAxis.valueFormatter = object : ValueFormatter() {
            private val labels = arrayOf("M", "T", "W", "T", "F", "S", "S") // Your custom labels

            override fun getFormattedValue(value: Float): String {
                return if (value >= 0 && value < labels.size) {
                    labels[value.toInt()]
                } else {
                    "" // Or a default value for out-of-range indices
                }
            }
        }

        //Y-Axis Values
        val yAxis = lineChart.axisLeft // Or chart.axisRight for the right Y-axis
        yAxis.textColor = Color.parseColor("#50535B")
        yAxis.axisMinimum = 0f      // Set the minimum value to 0
        yAxis.axisMaximum = 500f    // Set the maximum value to 500
        yAxis.granularity = 100f
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}" // Example: Add a currency symbol
            }
        }

        val legend = lineChart.legend
        legend.isEnabled = false
        // 7. Refresh the chart
        lineChart.invalidate() // Refreshes the chart to display the data
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    override fun onScrollChanged() {
        val view = mScrollView.getChildAt(mScrollView.childCount - 1)
        val topDetector = mScrollView.scrollY
        val bottomDetector: Int = view.bottom - (mScrollView.height + mScrollView.scrollY)
        if (bottomDetector > 0) {
            headerLayout.setBackgroundColor(resources.getColor(R.color.buttontextcolor))
        }
        if (topDetector <= 0) {
            headerLayout.background = null
        }
    }

    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF")
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
    private fun getData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        var api=""
        api= ApiURL.getbooking+"2"+"&date="+""+"&session_type="+""+"&location="+""

        Log.e("UpComingbookingApi",""+api)

        GetMethod(api,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                upcomingSessionsArraylist.clear()
                Log.e("getBookingResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                var jsonObject=jsonArray.optJSONObject(i)
                                var upcomingbokingModel= UpcomingSessionsModel()

                                upcomingbokingModel.id=jsonObject.optString("id")
                                upcomingbokingModel.type=jsonObject.optString("type")
                                upcomingbokingModel.timing=jsonObject.optString("timing")
                                upcomingbokingModel.distance=jsonObject.optString("distance")
                                upcomingbokingModel.selected_slot=jsonObject.optString("selected_slot")
                                upcomingbokingModel.session_type=jsonObject.optString("session_type")
                                upcomingbokingModel.duration=jsonObject.optString("duration")
                                upcomingbokingModel.trainer=jsonObject.optString("trainer")
                                upcomingbokingModel.trainer_image=jsonObject.optString("trainer_image")
                                upcomingbokingModel.location=jsonObject.optString("location")
                                upcomingbokingModel.is_reschedule=jsonObject.optString("is_reschedule")
                                upcomingbokingModel.msg=jsonObject.optString("msg")
                                upcomingbokingModel.is_Trainer=jsonObject.optString("isTrainer")
                                upcomingbokingModel.workout_focus=jsonObject.optJSONArray("workout_focus")
                                upcomingSessionsArraylist.add(upcomingbokingModel)
                            }

                            upcomingSessionsRecyclerView.adapter = UpcomingSessionsAdapter(context, upcomingSessionsArraylist)
                            upcomingSessionsRecyclerView.visibility=View.VISIBLE
                            upcomingSessions.visibility=View.VISIBLE

                        }else{
                            upcomingSessionsRecyclerView.visibility=View.GONE
                            upcomingSessions.visibility=View.GONE
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
    private fun getUserGoal() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        var api=""
        api= ApiURL.getgoalsdata

        Log.e("goalsDataApi",""+api)

        GetMethod(api,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("getGoalsResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonObjectWaterIntake=jsonObj.optJSONObject("data").optJSONObject("water_intake")
                        var jsonObjectcalorie_intake=jsonObj.optJSONObject("data").optJSONObject("calorie_intake")
                        var jsonObjectcalorie_burn=jsonObj.optJSONObject("data").optJSONObject("calorie_burn")
                        var jsonObjectsteps=jsonObj.optJSONObject("data").optJSONObject("steps")

                        waterIntake_id=jsonObjectWaterIntake.optString("id")
                        calorie_intake_id=jsonObjectcalorie_intake.optString("id")
                        calorie_burn_id=jsonObjectcalorie_burn.optString("id")
                        var steps_id=jsonObjectsteps.optString("id")

                        waterIntake.setText(jsonObjectWaterIntake.optString("progress"))
                        waterIntakeProgress.setProgress(jsonObjectWaterIntake.optInt("progress"))
                        waterIntakeProgress.max=(jsonObjectWaterIntake.optInt("target"))
                        totalWaterIntake.setText(" /"+jsonObjectWaterIntake.optString("target")+jsonObjectWaterIntake.optString("unit"))
                        addWaterIntake.setText("+ "+jsonObjectWaterIntake.optString("glass_value")+jsonObjectWaterIntake.optString("glass_type"))

                        intakeCalories.setText(jsonObjectcalorie_intake.optString("progress"))
                        intakeTotalCalories.setText(" /"+jsonObjectcalorie_intake.optString("target")+jsonObjectWaterIntake.optString("unit"))
                        calorieIntakeProgressBar.setProgress(jsonObjectcalorie_intake.optInt("progress"))
                        calorieIntakeProgressBar.max=(jsonObjectcalorie_intake.optInt("target"))

                        caloriesBurn.setText(jsonObjectcalorie_burn.optString("progress"))
                        intakeBurnCalories.setText(" /"+jsonObjectcalorie_burn.optString("target")+jsonObjectWaterIntake.optString("unit"))
                        semiProgress.setProgressWithAnimation(jsonObjectcalorie_burn.optString("progress").toFloat())

                        steps.setText(jsonObjectsteps.optString("progress"))
                        distance.setText(jsonObjectsteps.optString("distance_km"))
                        totalSteps.setText(" /"+jsonObjectsteps.optString("target")+jsonObjectsteps.optString("unit"))

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
    private fun getgymList(latitude: Double, longitude: Double) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        Log.e("gymListApi",""+ApiURL.getTrainer+"0"+"&tag_id="+"0"+"&type="+"gym"+"&long="+longitude +"&lat="+ latitude)
        GetMethod(ApiURL.getTrainer+"0"+"&tag_id="+"0"+"&type="+"gym"+"&long="+ longitude +"&lat="+ latitude,activity).startMethod(object : ResponseData {
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
            .setPositiveButton(R.string.EnableGPS
            ) { dialog, id ->
                showProgress = true
                val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(callGPSSettingIntent)
            }
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
                            Handler(Looper.getMainLooper()).postDelayed({
                                tvlocation.visibility=View.VISIBLE
                                tvlocation.text = address
                            }, 500)

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
        userName.text = "$msg ${sharedPreferences.getString(Constants.name,"")}!"
        Glide.with(requireActivity()!!).load(sharedPreferences.getString(Constants.profile_image,"")).fitCenter().error(R.drawable.guest_user).into(improfile)
        getchecktype()

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            showGPSDisabledAlertToUser()
            return
        }
        getRenewalPlanList()
        getData()
        getStreakData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(getDatereceiver, IntentFilter("sendDate"), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            requireContext().registerReceiver(getDatereceiver, IntentFilter("sendDate"))
        }


    }

    private fun getMealsData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()

        var api=""
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)

        api= ApiURL.user_meals+"?date="+currentDate

        Log.e("UserMealUrl",api)
        GetMethod(api
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                upcomingMealsArraylist.clear()
                Log.e("UserMealResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArray=jsonObj.optJSONArray("data")

                        if (jsonArray.length()>0){

                            for(i in 0 until jsonArray.length()){
                                var json=jsonArray.optJSONObject(i)
                                var upcomingMealsModel= UpcomingMealsModel()
                                upcomingMealsModel.meal_name=json.optString("meal_name")
                                upcomingMealsModel.id=json.optString("id")
                                upcomingMealsModel.meal_type=json.optString("meal_type")
                                upcomingMealsModel.calories=json.optString("calories")
                                upcomingMealsModel.proteins=json.optString("proteins")
                                upcomingMealsModel.carbs=json.optString("carbs")
                                upcomingMealsModel.fats=json.optString("fats")
                                upcomingMealsModel.meal_time=json.optString("meal_time")
                                upcomingMealsModel.fitness_goal=json.optString("fitness_goal")
                                upcomingMealsArraylist.add(upcomingMealsModel)
                            }
                            upcomingMealsRecyclerView.adapter = UpcomingMealsAdapter(context, upcomingMealsArraylist)
                            imCreateMeal.visibility= View.GONE
                            upcomingMealsRecyclerView.visibility= View.VISIBLE
                            allUpcomingMeals.visibility= View.VISIBLE
                            allUpcomingMeals.setOnClickListener{
                                var intent=Intent(activity,MyMealsActivity::class.java)
                                startActivity(intent)
                            }

                        }else{
                            imCreateMeal.visibility= View.VISIBLE
                            upcomingMealsRecyclerView.visibility= View.GONE
                            allUpcomingMeals.visibility= View.GONE
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
                            tvmember.visibility=View.GONE
                        }else{
                            tvmember.visibility=View.VISIBLE
                            tvmember.setText(jsonObjectvalue+" Member")

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
    private fun getMyWorkout(date_number1: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext()!!,"")
        progressDialog.show()
        Log.e("APi,",""+ApiURL.myworkouts+date_number1)
        GetMethod(ApiURL.myworkouts+date_number1,requireContext()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                workouttypeArraylist.clear()

                Log.e("MyWorkoutResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){

                            for (i in 0 until jsonArray.length()) {
                                var jsonObject1 = jsonArray.optJSONObject(i)
                                val workoutModel = AllWorkoutTypeModel()
                                workoutModel.category = ""+jsonObject1.optString("category")
                                workoutModel.id = ""+jsonObject1.optString("id")
                                workoutModel.assigned_id = ""+jsonObject1.optString("assigned_id")
                                workoutModel.title = ""+jsonObject1.optString("title")
                                workoutModel.percentage = ""+jsonObject1.optString("percentage")
                                workoutModel.isCompleted =jsonObject1.optBoolean("isCompleted")
                                workoutModel.previewImage = ""+jsonObject1.optString("previewImage")
                                workoutModel.status = ""+jsonObject1.optString("status")
                                workoutModel.pt_score = ""+jsonObject1.optString("pt_score")
                                workouttypeArraylist.add(workoutModel)
                            }
                            workoutRecyclerView.adapter = WorkoutAdapter(context,workouttypeArraylist,datename,"inactive")
                            workoutRecyclerView.visibility=View.VISIBLE
                            tvnoWorkout.visibility=View.GONE
                        }
                        else{
                            workoutRecyclerView.visibility=View.GONE
                            tvnoWorkout.visibility=View.VISIBLE
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
    var getDatereceiver: BroadcastReceiver = object : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            try {
                date_number= intent.getStringExtra("date")!!
                datename= intent.getStringExtra("name")!!
                getMyWorkout(date_number)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getStreakData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()
        Log.e("StreakDataApi",ApiURL.getuserstreak)
        GetMethod(ApiURL.getuserstreak,requireContext()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                streakArrayList.clear()
                Log.e("streakResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                       /* totalStreakDays.setText(resp.optJSONObject("data").optString("week_number"))
                        tvmsg.setText(resp.optString("msg"))
*/

                        var jsonArray=resp.optJSONObject("data").optJSONArray("week_days")
                        for(i in 0 until jsonArray.length()){
                            var jsonObject=jsonArray.optJSONObject(i)
                            val streakModel= StreakModel()
                            streakModel.day=jsonObject.optString("day")
                            streakModel.date=jsonObject.optString("date")
                            streakModel.status=jsonObject.optString("status")

                            streakModel.completed=jsonObject.optString("completed")
                            streakArrayList.add(streakModel)
                        }
                        val streakAdapter= StreakAdapter(streakArrayList,requireContext())
                        streakRecyclerView.adapter=streakAdapter

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
