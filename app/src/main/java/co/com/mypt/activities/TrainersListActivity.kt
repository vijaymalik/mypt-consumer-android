package co.com.mypt.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.FilterListAdapter
import co.com.mypt.adapter.GenderFilterAdapter
import co.com.mypt.adapter.LanguageAdapter
import co.com.mypt.adapter.NationalitiesAdapter
import co.com.mypt.adapter.TimeSLotAdapter
import co.com.mypt.adapter.TrainerListAdapter
import co.com.mypt.adapter.TrainerListExerciseAdapter
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.GenderModel
import co.com.mypt.model.LanguageModel
import co.com.mypt.model.NationModel
import co.com.mypt.model.TimeSLotModel
import co.com.mypt.model.TrainersModel
import co.com.mypt.utils.AdaptiveSpacingItemDecoration
import com.android.volley.VolleyError
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import org.json.JSONObject
import java.util.Locale


class TrainersListActivity : AppCompatActivity() {
    var nationId=""
    var languageId=""
    var timeSlotId=""
    var genderId=""
    private var pendingDialogType: String? = null
    lateinit var tvTimeslot: TextView
    lateinit var linearTime: LinearLayout
    lateinit var linearNationality: LinearLayout
    lateinit var tvLanguage: TextView
    lateinit var tvNationality: TextView
    lateinit var orangeViewNation: View
    lateinit var viewNationality: View
    lateinit var orangeViewLanguage: View
    lateinit var viewLanguage: View
    lateinit var viewTime: View
    lateinit var orangeViewTime: View
    lateinit var orangeViewGender: View
    lateinit var viewGender: View
    lateinit var exerciseRecyclerView : RecyclerView
    lateinit var trainerRecyclerView : RecyclerView
//    lateinit var filterRecyclerView : RecyclerView
    lateinit var genderRecycler : RecyclerView
    lateinit var nationRecycler : RecyclerView
    lateinit var timeSlotRecycler : RecyclerView
    lateinit var LanguageRecycler : RecyclerView

    lateinit var gridList : ImageView
    lateinit var linearList : ImageView
    lateinit var back : ImageView
    lateinit var tvNodata : TextView
    lateinit var tvgender : TextView

    var exerciseList = ArrayList<ExerciseModel>()
    var trainerList = ArrayList<TrainersModel>()
    var genderList = ArrayList<GenderModel>()
    var nationrList = ArrayList<NationModel>()
    var languageList = ArrayList<LanguageModel>()
    var timeSLotModelList = ArrayList<TimeSLotModel>()
    private val selectedgenderIds = mutableSetOf<String>()
    private val selectedtimeSLotIds = mutableSetOf<String>()
    private val selectedLanguageIds = mutableSetOf<String>()
    private val selectedNationIds = mutableSetOf<String>()
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

    var typeLayout="Linear"
    var filter="0"
    var tag_id=""
    var studio_id=""
    lateinit var standard_bottom_sheet: LinearLayout

    lateinit var sharedPreferences:SharedPreferences
//    lateinit var searchTrainer : ImageView
    lateinit var searchEditText : EditText
    lateinit var filterTextView : MaterialTextView
    var trainerListAdapter: TrainerListAdapter? = null
    val filternames = ArrayList<String>()
    private var selectedType=""

    var filterBottomSheetDialog: BottomSheetDialog? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainers_list)

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        searchEditText = findViewById(R.id.searchEditText)
        filterTextView = findViewById(R.id.filterTextView)
//        filterRecyclerView = findViewById(R.id.filterRecyclerView)
//        searchTrainer = findViewById(R.id.searchTrainer)
        gridList = findViewById(R.id.gridList)
        linearList = findViewById(R.id.linearList)
        back = findViewById(R.id.back)
        tvNodata = findViewById(R.id.tvNodata)

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        trainerRecyclerView = findViewById(R.id.trainerRecyclerView)
        filternames.add(applicationContext.resources.getString(R.string.timeslot))
        filternames.add(resources.getString(R.string.gender))
        filternames.add(resources.getString(R.string.Language))
        filternames.add(resources.getString(R.string.Nationality))
        try {
           studio_id=""+intent.getStringExtra("studio_id")
        }catch (e: Exception){
            e.printStackTrace()
        }
//        var filterAdapter= FilterListAdapter(this@TrainersListActivity, filternames)
//        filterRecyclerView.adapter=filterAdapter
        getFilterData()

        filterTextView.setOnClickListener {
            selectedType="timeslot"
            filterBottomSHeet(selectedType){

            }
        }
        back.setOnClickListener{
            finish()
        }


        trainerListAdapter = TrainerListAdapter(
            this,
            trainerList,
            "linear",
            sharedPreferences.getString("typeWorkout",""),
            latitude,
            longitude,
            studio_id
        )
        trainerRecyclerView.adapter = trainerListAdapter

        linearList.setOnClickListener {
            typeLayout="Linear"
            linearList.setImageResource(R.drawable.selected_linear_list)
            gridList.setImageResource(R.drawable.grid_layout)
            val lmLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            trainerRecyclerView.setLayoutManager(lmLayoutManager)
            while (trainerRecyclerView.itemDecorationCount > 0) {
                trainerRecyclerView.removeItemDecorationAt(0)
            }
            trainerRecyclerView.addItemDecoration(AdaptiveSpacingItemDecoration(0,false))
            trainerListAdapter = TrainerListAdapter(
                this,
                trainerList,
                typeLayout,
                sharedPreferences.getString("typeWorkout",""),
                latitude,
                longitude,
                studio_id
            )
            trainerRecyclerView.adapter = trainerListAdapter
        }
        gridList.setOnClickListener {
            typeLayout="grid"
            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
            trainerRecyclerView.setLayoutManager(mLayoutManager)
            while (trainerRecyclerView.itemDecorationCount > 0) {
                trainerRecyclerView.removeItemDecorationAt(0)
            }
            trainerRecyclerView.addItemDecoration(AdaptiveSpacingItemDecoration(30,true))
            linearList.setImageResource(R.drawable.linear_list)
            gridList.setImageResource(R.drawable.selected_grid_layout)
            trainerListAdapter = TrainerListAdapter(
                this,
                trainerList,
                typeLayout,
                sharedPreferences.getString("typeWorkout",""),
                latitude,
                longitude,
                studio_id
            )
            trainerRecyclerView.adapter = trainerListAdapter
        }

        /*searchTrainer.setOnClickListener {
            searchTrainer.visibility = View.GONE
            searchEditText.visibility = View.VISIBLE
            searchEditText.requestFocus()
            searchEditText.setSelection(searchEditText.text.length)

            // Open keyboard with delay to ensure focus is applied
            searchEditText.post {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }*/
        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    _filter(s.toString())
                }else{
                    trainerListAdapter = TrainerListAdapter(
                        this@TrainersListActivity,
                        trainerList,
                        typeLayout,
                        sharedPreferences.getString("typeWorkout",""),
                        latitude,
                        longitude,
                        studio_id
                    )
                    trainerRecyclerView.adapter = trainerListAdapter
                    trainerRecyclerView.visibility= View.VISIBLE
                    tvNodata.visibility= View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = searchEditText.compoundDrawables[2] // Right drawable

                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    val clickAreaStart = searchEditText.width - searchEditText.paddingEnd - drawableWidth

                    if (event.x >= clickAreaStart) {
                        trainerListAdapter = TrainerListAdapter(
                            this,
                            trainerList,
                            typeLayout,
                            sharedPreferences.getString("typeWorkout",""),
                            latitude,
                            longitude,
                            studio_id
                        )
                        trainerRecyclerView.adapter = trainerListAdapter
                        trainerRecyclerView.visibility= View.VISIBLE
                        tvNodata.visibility= View.GONE
                        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)


//                        searchTrainer.visibility = View.VISIBLE
                        searchEditText.visibility = View.INVISIBLE
                        searchEditText.text.clear() // Clear text, or do your action
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }



    private fun _filter(text: String) {
        val filteredList: MutableList<TrainersModel> = ArrayList()
        for (item in trainerList) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            trainerRecyclerView.visibility = View.GONE
            tvNodata.visibility = View.VISIBLE
        } else {
            trainerRecyclerView.visibility= View.VISIBLE
            tvNodata.visibility= View.GONE
            trainerListAdapter!!.filterList(filteredList)
        }
    }

    private fun getTagData(latitude: Double?, longitude: Double?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@TrainersListActivity,"")
        progressDialog.show()
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api=ApiURL.getTrainer+0+"&tag_id=&type="+sharedPreferences.getString("typeWorkout","")+"&long="+longitude+"&lat="+latitude
            Log.e("trainerTagListApi",""+ApiURL.getTrainer+0+"&tag_id=&type="+sharedPreferences.getString("typeWorkout","")+"&long="+longitude+"&lat="+latitude)
        }else{
            api=ApiURL.gym_select_trainer+intent.getStringExtra("studio_id")+"&long="+longitude+"&lat="+latitude+"&tag_id="
            Log.e("trainerGymTagListApi",""+api)

        }
        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                exerciseList.clear()
                Log.e("getTagResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayTag=jsonObj.optJSONObject("data").optJSONArray("tags")
                        val exerciseModel = ExerciseModel()
                        exerciseModel.name = "All Workouts"
                        exerciseModel.id =""
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
                        exerciseRecyclerView.adapter = TrainerListExerciseAdapter(applicationContext,exerciseList)

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
                            getTagData(latitude,longitude)
                            getTrainerList(tag_id,filter)

                            /*tvlocation.text = address
                            edaddress.setText(address)*/

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(TrainerList, IntentFilter("tag"), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(TrainerList, IntentFilter("tag"))
        }
    }

    var TrainerList: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                filter= intent.getStringExtra("filter")!!
                tag_id= intent.getStringExtra("tag_id")!!
                getTrainerList(tag_id,filter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getTrainerList(tag_id: String?, filter: String?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@TrainersListActivity,"")
        if (progressDialog!=null)
            progressDialog.show()
        var api=""
        val param: MutableMap<String, String> = HashMap()
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api=ApiURL.gettrainersList
            param["is_filter"] = ""+filter
            param["type"] = ""+sharedPreferences.getString("typeWorkout","")
            Log.e("trainerListApi",""+api)

        }else{
            api=ApiURL.gymtrainers
            Log.e("trainerGymListApi",""+api)
            param["id"] = ""+intent.getStringExtra("studio_id")

        }
        param["gender"] = ""+genderId
        param["language"] = ""+languageId
        param["lat"] = ""+latitude
        param["long"] = ""+longitude
        param["time_slot"] = ""+timeSlotId
        param["tag_id"] = ""+tag_id
        param["nationality"] = ""+nationId
        Log.e("trainerListParam", param.toString())

        PostMethod(api,param,applicationContext).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                trainerList.clear()
                Log.e("getTrainerListResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayList=jsonObj.optJSONObject("data").optJSONArray("trainers")
                        if (jsonArrayList.length()>0){
                            for(i in 0 until jsonArrayList.length()){
                                var jsonObject1 = jsonArrayList.optJSONObject(i)
                                val trainerModel = TrainersModel()
                                trainerModel.name = jsonObject1.optString("name")
                                trainerModel.id = jsonObject1.optString("id")
                                trainerModel.distance = jsonObject1.optString("distance")
                                trainerModel.slot = jsonObject1.optString("slot")
                                trainerModel.noOfRating = jsonObject1.optString("noOfRating")
                                trainerModel.averageRating = jsonObject1.optString("averageRating")
                                trainerModel.location = jsonObject1.optString("location")
                                trainerModel.profile = jsonObject1.optString("profile")
                                trainerModel.is_verified = jsonObject1.optString("is_verified")
                                trainerModel.activity = jsonObject1.optJSONArray("tags")
                                trainerList.add(trainerModel)
                            }
                            trainerListAdapter = TrainerListAdapter(applicationContext,trainerList,typeLayout,sharedPreferences.getString("typeWorkout",""),latitude,longitude,studio_id)
                            trainerRecyclerView.adapter = trainerListAdapter
                            trainerRecyclerView.visibility= View.VISIBLE
                            tvNodata.visibility= View.GONE
                        }else{
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

    fun filterBottomSHeet(selectedType: String,selectedTypeCall:(String)->Unit) {
        if (filterBottomSheetDialog?.isShowing == true) {
            // Store the pending type and wait for dismissal
            pendingDialogType = selectedType
            filterBottomSheetDialog?.dismiss()
            return
        }
        filterBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.filter_bottom_sheet_dialog, null)
        filterBottomSheetDialog!!.setContentView(bottomSheet)

        filterBottomSheetDialog?.setOnDismissListener {
            filterBottomSheetDialog = null
            filterBottomSheetDialog = null

            // If another dialog is queued, show it now
            pendingDialogType?.let {
                val nextType = it
                pendingDialogType = null
                filterBottomSHeet(nextType){

                }
            }
        }
        standard_bottom_sheet = bottomSheet.findViewById(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        tvgender =bottomSheet.findViewById<TextView>(R.id.tvgender)
        var tvApplyFilter =bottomSheet.findViewById<TextView>(R.id.tvApplyFilter)
        var tvReset =bottomSheet.findViewById<TextView>(R.id.tvReset)
        tvNationality =bottomSheet.findViewById<TextView>(R.id.tvNationality)
        tvLanguage =bottomSheet.findViewById<TextView>(R.id.tvLanguage)
        tvTimeslot =bottomSheet.findViewById<TextView>(R.id.tvTimeslot)
        linearTime =bottomSheet.findViewById<LinearLayout>(R.id.linearTime)
        linearNationality =bottomSheet.findViewById<LinearLayout>(R.id.linearNationality)
        genderRecycler =bottomSheet.findViewById<RecyclerView>(R.id.genderRecycler)
        nationRecycler =bottomSheet.findViewById<RecyclerView>(R.id.nationRecycler)
        LanguageRecycler =bottomSheet.findViewById<RecyclerView>(R.id.LanguageRecycler)
        timeSlotRecycler =bottomSheet.findViewById<RecyclerView>(R.id.timeSlotRecycler)
        LanguageRecycler =bottomSheet.findViewById<RecyclerView>(R.id.LanguageRecycler)
        val linearLanguage =bottomSheet.findViewById<LinearLayout>(R.id.linearLanguage)
        val linearGender =bottomSheet.findViewById<LinearLayout>(R.id.linearGender)
        viewGender =bottomSheet.findViewById<View>(R.id.viewGender)
        orangeViewGender =bottomSheet.findViewById<View>(R.id.orangeViewGender)

        orangeViewTime =bottomSheet.findViewById<View>(R.id.orangeViewTime)
        viewTime =bottomSheet.findViewById<View>(R.id.viewTime)

        viewLanguage =bottomSheet.findViewById<View>(R.id.viewLanguage)
        orangeViewLanguage =bottomSheet.findViewById<View>(R.id.orangeViewLanguage)

        viewNationality =bottomSheet.findViewById<View>(R.id.viewNationality)
        orangeViewNation =bottomSheet.findViewById<View>(R.id.orangeViewNation)

        var genderAdapter= GenderFilterAdapter(applicationContext, genderList,selectedgenderIds)
        genderRecycler.adapter=genderAdapter

        var nationalitiesAdapter= NationalitiesAdapter(applicationContext, nationrList,selectedNationIds)
        nationRecycler.adapter=nationalitiesAdapter

        var languageAdapter= LanguageAdapter(applicationContext, languageList,selectedLanguageIds)
        LanguageRecycler.adapter=languageAdapter

        var timeSLotAdapter=
            TimeSLotAdapter(applicationContext, timeSLotModelList, selectedtimeSLotIds)
        timeSlotRecycler.adapter=timeSLotAdapter

        if (selectedType.equals("Gender")){
            orangeViewGender.visibility=View.VISIBLE
            viewGender.visibility=View.GONE
            genderRecycler.visibility=View.VISIBLE
            tvgender.setTextColor(resources.getColor(R.color.orangecolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.tabbackground))

            orangeViewTime.visibility=View.GONE
            viewTime.visibility=View.VISIBLE
            timeSlotRecycler.visibility=View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility=View.GONE
            viewLanguage.visibility=View.VISIBLE
            LanguageRecycler.visibility=View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility=View.GONE
            viewNationality.visibility=View.VISIBLE
            nationRecycler.visibility=View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))

        }
        else if (selectedType.equals("timeslot")){
            orangeViewGender.visibility=View.GONE
            viewGender.visibility=View.VISIBLE
            genderRecycler.visibility=View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility=View.VISIBLE
            viewTime.visibility=View.GONE
            timeSlotRecycler.visibility=View.VISIBLE
            tvTimeslot.setTextColor(resources.getColor(R.color.orangecolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.tabbackground))

            orangeViewLanguage.visibility=View.GONE
            viewLanguage.visibility=View.VISIBLE
            LanguageRecycler.visibility=View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility=View.GONE
            viewNationality.visibility=View.VISIBLE
            nationRecycler.visibility=View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))

        }
        else if (selectedType.equals("Language")){
            orangeViewGender.visibility=View.GONE
            viewGender.visibility=View.VISIBLE
            genderRecycler.visibility=View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility=View.GONE
            viewTime.visibility=View.VISIBLE
            timeSlotRecycler.visibility=View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility=View.VISIBLE
            viewLanguage.visibility=View.GONE
            LanguageRecycler.visibility=View.VISIBLE
            tvLanguage.setTextColor(resources.getColor(R.color.orangecolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.tabbackground))

            orangeViewNation.visibility=View.GONE
            viewNationality.visibility=View.VISIBLE
            nationRecycler.visibility=View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))

        }else{
            orangeViewGender.visibility=View.GONE
            viewGender.visibility=View.VISIBLE
            genderRecycler.visibility=View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewTime.visibility=View.GONE
            viewTime.visibility=View.VISIBLE
            timeSlotRecycler.visibility=View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility=View.GONE
            viewLanguage.visibility=View.VISIBLE
            LanguageRecycler.visibility=View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewNation.visibility=View.VISIBLE
            viewNationality.visibility=View.GONE
            nationRecycler.visibility=View.VISIBLE
            tvNationality.setTextColor(resources.getColor(R.color.orangecolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.tabbackground))

        }
        linearGender.setOnClickListener {
            orangeViewGender.visibility=View.VISIBLE
            viewGender.visibility=View.GONE
            genderRecycler.visibility=View.VISIBLE
            tvgender.setTextColor(resources.getColor(R.color.orangecolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.tabbackground))

            orangeViewTime.visibility=View.GONE
            viewTime.visibility=View.VISIBLE
            timeSlotRecycler.visibility=View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility=View.GONE
            viewLanguage.visibility=View.VISIBLE
            LanguageRecycler.visibility=View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility=View.GONE
            viewNationality.visibility=View.VISIBLE
            nationRecycler.visibility=View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))
        }
        linearTime.setOnClickListener {
            orangeViewGender.visibility=View.GONE
            viewGender.visibility=View.VISIBLE
            genderRecycler.visibility=View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility=View.VISIBLE
            viewTime.visibility=View.GONE
            timeSlotRecycler.visibility=View.VISIBLE
            tvTimeslot.setTextColor(resources.getColor(R.color.orangecolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.tabbackground))

            orangeViewLanguage.visibility=View.GONE
            viewLanguage.visibility=View.VISIBLE
            LanguageRecycler.visibility=View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility=View.GONE
            viewNationality.visibility=View.VISIBLE
            nationRecycler.visibility=View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))
        }
        linearLanguage.setOnClickListener {
            orangeViewGender.visibility=View.GONE
            viewGender.visibility=View.VISIBLE
            genderRecycler.visibility=View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility=View.GONE
            viewTime.visibility=View.VISIBLE
            timeSlotRecycler.visibility=View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility=View.VISIBLE
            viewLanguage.visibility=View.GONE
            LanguageRecycler.visibility=View.VISIBLE
            tvLanguage.setTextColor(resources.getColor(R.color.orangecolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.tabbackground))

            orangeViewNation.visibility=View.GONE
            viewNationality.visibility=View.VISIBLE
            nationRecycler.visibility=View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))
        }
        linearNationality.setOnClickListener {
            orangeViewGender.visibility=View.GONE
            viewGender.visibility=View.VISIBLE
            genderRecycler.visibility=View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewTime.visibility=View.GONE
            viewTime.visibility=View.VISIBLE
            timeSlotRecycler.visibility=View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility=View.GONE
            viewLanguage.visibility=View.VISIBLE
            LanguageRecycler.visibility=View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewNation.visibility=View.VISIBLE
            viewNationality.visibility=View.GONE
            nationRecycler.visibility=View.VISIBLE
            tvNationality.setTextColor(resources.getColor(R.color.orangecolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.tabbackground))
        }
        tvApplyFilter.setOnClickListener {

            genderId = genderAdapter.getSelectedIdString()
            timeSlotId = timeSLotAdapter.getSelectedIdString()
            languageId = languageAdapter.getSelectedIdString()
            nationId = nationalitiesAdapter.getSelectedIdString()
            getTrainerList(tag_id,filter)
            filterBottomSheetDialog!!.dismiss()

        }
        tvReset.setOnClickListener {
            genderId = ""
            timeSlotId = ""
            languageId = ""
            nationId = ""
            selectedgenderIds.clear()
            selectedtimeSLotIds.clear()
            selectedLanguageIds.clear()
            selectedNationIds.clear()

            getTrainerList(tag_id,filter)
            filterBottomSheetDialog!!.dismiss()
        }
        filterBottomSheetDialog!!.show()
        val window = filterBottomSheetDialog!!.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(filterBottomSheetDialog!!)
        /*  addCalorieBottomSheetDialog.setOnShowListener {
              val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
              bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
              bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
          }*/
        filterBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

    }
    private fun getFilterData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api= ApiURL.get_filter_data
        Log.e("getfilterAPi",""+api)

        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                genderList.clear()
                nationrList.clear()
                languageList.clear()
                timeSLotModelList.clear()
                Log.e("GetfilterResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonGender=jsonObj.optJSONObject("data").optJSONArray("gender")
                        var jsonlanguages=jsonObj.optJSONObject("data").optJSONArray("languages")
                        var jsonnationalities=jsonObj.optJSONObject("data").optJSONArray("nationalities")
                        var jsontime_slots=jsonObj.optJSONObject("data").optJSONArray("time_slots")
                        for(i in 0 until jsonGender.length()){
                            var jsonObject1 = jsonGender.optJSONObject(i)
                            val genderModel1 = GenderModel()
                            genderModel1.name = jsonObject1.optString("name")
                            genderModel1.id = jsonObject1.optString("id")
                            Log.e("genderListId",""+jsonObject1.optString("id"))

                            genderList.add(genderModel1)
                        }

                        for(i in 0 until jsonnationalities.length()){
                            var jsonObject1 = jsonnationalities.optJSONObject(i)
                            val genderModel2 = NationModel()
                            genderModel2.name = jsonObject1.optString("name")
                            genderModel2.id = jsonObject1.optString("id")
                            nationrList.add(genderModel2)
                        }

                        for(i in 0 until jsonlanguages.length()){
                            var jsonObject1 = jsonlanguages.optJSONObject(i)
                            val genderModel = LanguageModel()
                            genderModel.name = jsonObject1.optString("name")
                            genderModel.id = jsonObject1.optString("id")
                            languageList.add(genderModel)
                        }
                        for(i in 0 until jsontime_slots.length()){
                            var jsonObject1 = jsontime_slots.optJSONObject(i)
                            val timeSLotModel = TimeSLotModel()
                            timeSLotModel.name = jsonObject1.optString("name")
                            timeSLotModel.id = jsonObject1.optString("id")
                            timeSLotModelList.add(timeSLotModel)
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