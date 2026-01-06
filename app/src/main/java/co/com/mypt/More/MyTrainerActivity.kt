package co.com.mypt.More

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
import android.graphics.Typeface
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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.adapter.ProfileTrainerListAdapter
import co.com.mypt.adapter.TrainerListExerciseAdapter
import co.com.mypt.model.ExerciseModel
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
import org.json.JSONObject
import java.util.Locale

class MyTrainerActivity : AppCompatActivity() {
    lateinit var exerciseRecyclerView : RecyclerView
    lateinit var trainerRecyclerView : RecyclerView

    lateinit var gridList : ImageView
    lateinit var linearFollower : LinearLayout
    lateinit var imTrainer : ImageView
    lateinit var cardExploreTrainer : CardView
    lateinit var linearList : ImageView
    lateinit var searchTrainer : ImageView
    lateinit var back : ImageView
    lateinit var relativeNoData : RelativeLayout
    lateinit var tvTrainer : TextView
    lateinit var tvFollowing : TextView
    lateinit var tvNodata : TextView

    var exerciseList = ArrayList<ExerciseModel>()
    var trainerList = ArrayList<TrainersModel>()
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
    var tag_id="0"
    var studio_id=""
    lateinit var sharedPreferences: SharedPreferences
    var selected_option="1"
    lateinit var searchEditText : EditText
    var trainerListAdapter: ProfileTrainerListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_trainer2)

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        gridList = findViewById(R.id.gridList)
        searchTrainer = findViewById(R.id.searchTrainer)
        linearList = findViewById(R.id.linearList)
        back = findViewById(R.id.back)
        tvNodata = findViewById(R.id.tvNodata)
        linearFollower = findViewById(R.id.linearFollower)
        imTrainer = findViewById(R.id.imTrainer)
        cardExploreTrainer = findViewById(R.id.cardExploreTrainer)
        relativeNoData = findViewById(R.id.relativeNoData)
        tvTrainer = findViewById(R.id.tvTrainer)
        tvFollowing = findViewById(R.id.tvFollowing)
        searchEditText = findViewById(R.id.searchEditText)

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        trainerRecyclerView = findViewById(R.id.trainerRecyclerView)
        back.setOnClickListener{
            finish()
        }
        imTrainer.setOnClickListener{
            var intent=Intent(applicationContext,HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }
        cardExploreTrainer.setOnClickListener{
            var intent=Intent(applicationContext,HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }
        tvTrainer.setOnClickListener {
            selected_option="1"
            tvNodata.visibility=View.GONE
            tvTrainer.setTextColor(resources.getColor(R.color.black))
            tvFollowing.setTextColor(resources.getColor(R.color.white))

            tvFollowing.setTypeface(null, Typeface.NORMAL)
            tvTrainer.setTypeface(null, Typeface.BOLD)

            tvFollowing.background = null
            tvTrainer.background = resources.getDrawable(R.drawable.white_rectangle_8dp)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            searchTrainer.visibility = View.VISIBLE
            searchEditText.visibility = View.INVISIBLE
            searchEditText.text.clear()
            getTrainerList(tag_id,filter)
        }

        tvFollowing.setOnClickListener {
            selected_option="2"
            tvNodata.visibility=View.GONE
            tvTrainer.setTextColor(resources.getColor(R.color.white))
            tvFollowing.setTypeface(null, Typeface.BOLD)
            tvTrainer.setTypeface(null, Typeface.NORMAL)
            tvFollowing.setTextColor(resources.getColor(R.color.black))

            tvFollowing.background = resources.getDrawable(R.drawable.white_rectangle_8dp)
            tvTrainer.background = null
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            searchTrainer.visibility = View.VISIBLE
            searchEditText.visibility = View.INVISIBLE
            searchEditText.text.clear()
            getTrainerList(tag_id,filter)

        }

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

            trainerListAdapter = ProfileTrainerListAdapter(
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
            trainerListAdapter = ProfileTrainerListAdapter(
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

        searchTrainer.setOnClickListener {
            searchTrainer.visibility = View.GONE
            searchEditText.visibility = View.VISIBLE
            searchEditText.requestFocus()
            searchEditText.setSelection(searchEditText.text.length)

            // Open keyboard with delay to ensure focus is applied
            searchEditText.post {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    _filter(s.toString())
                }else{
                    trainerListAdapter = ProfileTrainerListAdapter(
                        this@MyTrainerActivity,
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
                    relativeNoData.visibility= View.GONE
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
                        trainerListAdapter = ProfileTrainerListAdapter(
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
                        relativeNoData.visibility= View.GONE
                        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                        searchTrainer.visibility = View.VISIBLE
                        searchEditText.visibility = View.INVISIBLE
                        searchEditText.text.clear() // Clear text, or do your action
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun getTagData(latitude: Double?, longitude: Double?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
      /*  if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getTrainer+0+"&tag_id=0&type="+sharedPreferences.getString("typeWorkout","")+"&long="+longitude+"&lat="+latitude
            Log.e("trainerTagListApi",""+ ApiURL.getTrainer+0+"&tag_id=0&type="+sharedPreferences.getString("typeWorkout","")+"&long="+longitude+"&lat="+latitude)
        }else{

            api= ApiURL.usertrainer+latitude+"&long="+longitude+"&type="+selected_option+"&is_filter="+0+"&tag_id="+0
            Log.e("trainerTagListApi",""+api)
        }*/
        api= ApiURL.usertrainer+latitude+"&long="+longitude+"&type="+selected_option+"&is_filter="+0+"&tag_id="+0
        Log.e("trainerTagListApi",""+api)

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
        getTrainerList(tag_id,filter)



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
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
       /* if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getTrainer+filter+"&tag_id="+tag_id+"&type="+sharedPreferences.getString("typeWorkout","")+"&long="+longitude+"&lat="+latitude
            Log.e("trainerListApi",""+api)
        }else{
            api= ApiURL.gym_select_trainer+intent.getStringExtra("studio_id")+"&long="+longitude+"&lat="+latitude+"&tag_id="+tag_id
            Log.e("trainerGymListApi",""+api)
            studio_id= intent.getStringExtra("studio_id").toString()
        }*/
        api= ApiURL.usertrainer+latitude+"&long="+longitude+"&type="+selected_option+"&is_filter="+filter+"&tag_id="+tag_id

        Log.e("trainerListApi",""+api)

        GetMethod(api,applicationContext).startMethod(object : ResponseData {
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
                                trainerModel.activity = jsonObject1.optJSONArray("tags")
                                trainerModel.is_verified = jsonObject1.optString("is_verified")

                                trainerList.add(trainerModel)
                            }
                          //  trainerRecyclerView.adapter = TrainerListAdapter(applicationContext,trainerList,typeLayout,sharedPreferences.getString("typeWorkout",""),latitude,longitude,studio_id)
                            trainerListAdapter = ProfileTrainerListAdapter(applicationContext,trainerList,typeLayout,sharedPreferences.getString("typeWorkout",""),latitude,longitude,studio_id)
                            trainerRecyclerView.adapter = trainerListAdapter

                            trainerRecyclerView.visibility= View.VISIBLE

                            relativeNoData.visibility= View.GONE
                        }else{
                            trainerRecyclerView.visibility= View.GONE
                            relativeNoData.visibility= View.VISIBLE
                            if (selected_option.equals("1")){
                                imTrainer.visibility=View.VISIBLE
                                linearFollower.visibility=View.GONE

                            }else{
                                imTrainer.visibility=View.GONE
                                linearFollower.visibility=View.VISIBLE
                            }
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
    private fun _filter(text: String) {
        val filteredList: MutableList<TrainersModel> = ArrayList()
        for (item in trainerList) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            trainerRecyclerView.visibility = View.GONE
            relativeNoData.visibility = View.GONE
            tvNodata.visibility = View.VISIBLE
        } else {
            trainerRecyclerView.visibility= View.VISIBLE
            tvNodata.visibility= View.GONE
            relativeNoData.visibility= View.GONE
            trainerListAdapter!!.filterList(filteredList)
        }

    }
}