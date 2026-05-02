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
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants.PASS_DATA
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
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
import co.com.mypt.utils.TrainerListData
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
import com.google.gson.Gson
import org.json.JSONObject
import java.util.Locale
import kotlin.let


class TrainersListActivity : AppCompatActivity() {
    var nationId = ""
    var languageId = ""
    var timeSlotId = ""
    var genderId = ""
    var address_id = ""
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
    lateinit var exerciseRecyclerView: RecyclerView
    lateinit var trainerRecyclerView: RecyclerView

    //    lateinit var filterRecyclerView : RecyclerView
    lateinit var genderRecycler: RecyclerView
    lateinit var nationRecycler: RecyclerView
    lateinit var timeSlotRecycler: RecyclerView
    lateinit var LanguageRecycler: RecyclerView

    lateinit var gridList: ImageView
    lateinit var linearList: ImageView
    lateinit var back: ImageView
    lateinit var tvNodata: TextView
    lateinit var tvgender: TextView

    var exerciseList = ArrayList<ExerciseModel>()
    var trainerList = ArrayList<TrainersModel>()
    private val searchTrainerFullList = mutableListOf<TrainersModel>()
    var genderList = ArrayList<GenderModel>()
    var nationrList = ArrayList<NationModel>()
    var languageList = ArrayList<LanguageModel>()
    var timeSLotModelList = ArrayList<TimeSLotModel>()
    private val selectedgenderIds = mutableSetOf<String>()
    private val selectedtimeSLotIds = mutableSetOf<String>()
    private val selectedLanguageIds = mutableSetOf<String>()
    private val selectedNationIds = mutableSetOf<String>()
    var isResumed1 = true
    var isclick = 0
    var showProgress = false
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null

    var typeLayout = "Linear"
    var filter = "0"
    var tag_id = ""
    var studio_id = ""
    lateinit var standard_bottom_sheet: LinearLayout

    lateinit var sharedPreferences: SharedPreferences

    //    lateinit var searchTrainer : ImageView
    lateinit var searchEditText: TextView
    lateinit var filterTextView: MaterialTextView
    var trainerListAdapter: TrainerListAdapter? = null
    val filternames = ArrayList<String>()
    private var selectedType = ""

    var filterBottomSheetDialog: BottomSheetDialog? = null
    private var exoPlayer: ExoPlayer? = null
    private var currentPlayingPosition = RecyclerView.NO_POSITION
    private var isMuted = true

    private var existingListener: Player.Listener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainers_list)
        setupPlayer()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick == 0) {
//            getCurrentLocation()
        }
        longitude =intent?.getDoubleExtra("longitude",0.0)
        latitude =intent?.getDoubleExtra("latitude",0.0)
        getTagData(latitude, longitude)
        getTrainerList(tag_id, filter)

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
            studio_id = "" + intent.getStringExtra("studio_id")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(intent.getStringExtra("address_id")!=null) {
            address_id = intent.getStringExtra("address_id")?:""
        }
//        var filterAdapter= FilterListAdapter(this@TrainersListActivity, filternames)
//        filterRecyclerView.adapter=filterAdapter
        getFilterData()

        filterTextView.setOnClickListener {
            selectedType = "timeslot"
            filterBottomSHeet(selectedType) {

            }
        }
        back.setOnClickListener {
            finish()
        }
        setTrainerListAdapter()
        linearList.setOnClickListener {
            typeLayout = "Linear"
            linearList.setImageResource(R.drawable.selected_linear_list)
            gridList.setImageResource(R.drawable.grid_layout)
            val lmLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            trainerRecyclerView.setLayoutManager(lmLayoutManager)
            while (trainerRecyclerView.itemDecorationCount > 0) {
                trainerRecyclerView.removeItemDecorationAt(0)
            }
            trainerRecyclerView.addItemDecoration(AdaptiveSpacingItemDecoration(0, false))
            exoPlayer?.pause()
            currentPlayingPosition = RecyclerView.NO_POSITION
            setTrainerListAdapter()
            trainerRecyclerView.post {
                autoPlayVisibleVideo()
            }
        }
        gridList.setOnClickListener {
            typeLayout = "grid"
            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
            trainerRecyclerView.setLayoutManager(mLayoutManager)
            while (trainerRecyclerView.itemDecorationCount > 0) {
                trainerRecyclerView.removeItemDecorationAt(0)
            }
            trainerRecyclerView.addItemDecoration(AdaptiveSpacingItemDecoration(30, true))
            linearList.setImageResource(R.drawable.linear_list)
            gridList.setImageResource(R.drawable.selected_grid_layout)
            exoPlayer?.pause()
            currentPlayingPosition = RecyclerView.NO_POSITION
            setTrainerListAdapter()
        }

        trainerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    autoPlayVisibleVideo()
//                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                    autoPlayVisibleVideo()
            }
        })

        searchEditText.setOnClickListener {
            TrainerListData.clear()
            TrainerListData.trainerList = searchTrainerFullList
            val intent = Intent(this, SearchTrainerActivity::class.java)
            intent.putExtra("studio_id",studio_id)
            intent.putExtra("long",longitude)
            intent.putExtra("lat",latitude)
            intent.putExtra("address_id",address_id)
            startActivity(intent)
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
//        searchEditText.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                if (s.toString().isNotEmpty()) {
//                    _filter(s.toString())
//                } else {
//                    setTrainerListAdapter()
//                    trainerRecyclerView.visibility = View.VISIBLE
//                    tvNodata.visibility = View.GONE
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {}
//        })

//        searchEditText.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                val drawableEnd = searchEditText.compoundDrawables[2] // Right drawable
//
//                if (drawableEnd != null) {
//                    val drawableWidth = drawableEnd.bounds.width()
//                    val clickAreaStart =
//                        searchEditText.width - searchEditText.paddingEnd - drawableWidth
//
//                    if (event.x >= clickAreaStart) {
//                        setTrainerListAdapter()
//                        trainerRecyclerView.visibility = View.VISIBLE
//                        tvNodata.visibility = View.GONE
//                        val inputMethodManager =
//                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//
//
////                        searchTrainer.visibility = View.VISIBLE
//                        searchEditText.visibility = View.INVISIBLE
//                        searchEditText.text.clear() // Clear text, or do your action
//                        return@setOnTouchListener true
//                    }
//                }
//            }
//            false
//        }
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    val holder = trainerRecyclerView
                        .findViewHolderForAdapterPosition(currentPlayingPosition)
                            as? TrainerListAdapter.ViewHolder

                    holder?.let {
                        it.playerView.visibility = View.GONE
                        it.trainerImage.visibility = View.VISIBLE
                        it.btnSoundToggle.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun autoPlayVisibleVideo() {
        // Guard: if player is not initialized, exit early
        if (exoPlayer == null) return

        val layoutManager = trainerRecyclerView.layoutManager

        if (layoutManager is GridLayoutManager) {
            exoPlayer?.pause()
            currentPlayingPosition = RecyclerView.NO_POSITION
            return
        }
        if (layoutManager !is LinearLayoutManager) return

        // Reset all holders
        for (i in 0 until trainerRecyclerView.childCount) {
            val child = trainerRecyclerView.getChildAt(i)
            when (val holder = trainerRecyclerView.getChildViewHolder(child)) {
                is TrainerListAdapter.ListViewHolder -> {
                    holder.binding.playerView.player = null
                    holder.binding.playerView.visibility = View.GONE
                    holder.binding.btnSoundToggle.visibility = View.GONE
                    holder.binding.trainerImage.visibility = View.VISIBLE
                }
            }
        }

        // Find the one fully visible item
        val first = layoutManager.findFirstCompletelyVisibleItemPosition()
        val last = layoutManager.findLastCompletelyVisibleItemPosition()

        if (first == last && first != RecyclerView.NO_POSITION) {
            val holder = trainerRecyclerView.findViewHolderForAdapterPosition(first)
            if (holder is TrainerListAdapter.ListViewHolder) {
                val trainer = trainerListAdapter?.trainerList[first]
                val videoUrl = trainer?.trainWithMe

                if (!videoUrl.isNullOrEmpty()) {
                    exoPlayer?.stop()
                    exoPlayer?.clearMediaItems()
                    exoPlayer?.clearVideoSurface()

                    existingListener?.let {
                        exoPlayer?.removeListener(it)
                    }
                    currentPlayingPosition = first
                    val mediaItem = MediaItem.fromUri(videoUrl.toUri())
                    exoPlayer?.apply {
                       setMediaItem(mediaItem)
                       prepare()
                        play()
                       volume = if (isMuted) 0f else 1f
                    }

                    holder.binding.trainerImage.visibility = View.GONE
                    holder.binding.playerView.visibility = View.VISIBLE
                    holder.binding.btnSoundToggle.visibility = View.VISIBLE
                    holder.binding.playerView.player = exoPlayer
                    setupSoundToggle(holder)

                    val listener = object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                holder.binding.playerView.visibility = View.GONE
                                holder.binding.trainerImage.visibility = View.VISIBLE
                                holder.binding.btnSoundToggle.visibility = View.GONE
                            }
                        }
                    }
                    existingListener = listener
                    exoPlayer?.addListener(listener)
                } else {
                    holder.binding.playerView.player = null
                    holder.binding.playerView.visibility = View.GONE
                    holder.binding.trainerImage.visibility = View.VISIBLE
                    holder.binding.btnSoundToggle.visibility = View.GONE
                }
            }
        } else {
            exoPlayer?.pause()
            currentPlayingPosition = RecyclerView.NO_POSITION
        }
    }

    override fun onStop() {
        super.onStop()
        existingListener?.let {
            exoPlayer?.removeListener(it)
        }
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun setupSoundToggle(holder: TrainerListAdapter.ListViewHolder) {
        holder.binding.btnSoundToggle.setOnClickListener {
            isMuted = !isMuted
            exoPlayer?.volume = if (isMuted) 0f else 1f
            holder.binding.btnSoundToggle.setImageResource(
                if (isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_on
            )
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
            trainerRecyclerView.visibility = View.VISIBLE
            tvNodata.visibility = View.GONE
            trainerListAdapter?.filterList(filteredList)
        }
    }

    private fun getTagData(latitude: Double?, longitude: Double?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@TrainersListActivity, "")
        if (!isFinishing && !isDestroyed) {
            progressDialog.show()
        }
        var api = ""
        if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
            api = ApiURL.getTrainer + 0 + "&tag_id=&type=" + sharedPreferences.getString(
                "typeWorkout",
                ""
            ) + "&long=" + longitude + "&lat=" + latitude
            Log.e(
                "trainerTagListApi",
                "" + ApiURL.getTrainer + 0 + "&tag_id=&type=" + sharedPreferences.getString(
                    "typeWorkout",
                    ""
                ) + "&long=" + longitude + "&lat=" + latitude
            )
        } else {
            api =
                ApiURL.gym_select_trainer + intent.getStringExtra("studio_id") + "&long=" + longitude + "&lat=" + latitude + "&tag_id="
            Log.e("trainerGymTagListApi", "" + api)

        }
        GetMethod(api, applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                exerciseList.clear()
                Log.e("getTagResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        var jsonArrayTag = jsonObj.optJSONObject("data").optJSONArray("tags")
                        val exerciseModel = ExerciseModel()
                        exerciseModel.name = "All Workouts"
                        exerciseModel.id = ""
                        exerciseModel.icon = ""
                        exerciseList.add(exerciseModel)
                        for (i in 0 until jsonArrayTag.length()) {
                            var jsonObject1 = jsonArrayTag.optJSONObject(i)
                            val exerciseModel = ExerciseModel()
                            exerciseModel.name = jsonObject1.optString("name")
                            exerciseModel.id = jsonObject1.optString("id")
                            exerciseModel.icon = jsonObject1.optString("icon")
                            exerciseList.add(exerciseModel)
                        }
                        exerciseRecyclerView.adapter =
                            TrainerListExerciseAdapter(applicationContext, exerciseList){ tagId, filter->
                                getTrainerList(tagId,filter)
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
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val location =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
//                getCurrentLocation()
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
                } else checkLocationPermission()
                Log.i("DEBUG", "permission denied")
            }
        }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(
            this
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
    /*private fun getCurrentLocation() {
        //mMap.clear();

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED *//*&& ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED*//*
        ) {

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
                            getTagData(latitude, longitude)
                            getTrainerList(tag_id, filter)

//                            tvlocation.text = address
//                            edaddress.setText(address)

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
    }*/

    override fun onResume() {
        super.onResume()
        isResumed1 = true
        if (exoPlayer == null) {
            setupPlayer()
        }
        autoPlayVisibleVideo()

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            showGPSDisabledAlertToUser()
            return
        }
    }


    private fun getTrainerList(tag_id: String?, filter: String?) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@TrainersListActivity, "")
        if (!isFinishing && !isDestroyed) {
            progressDialog.show()
        }
        var api = ""
        val param: MutableMap<String, String> = HashMap()
        if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
            api = ApiURL.gettrainersList
            param["is_filter"] = "" + filter
            param["type"] = "" + sharedPreferences.getString("typeWorkout", "")
            Log.e("trainerListApi", "" + api)

        } else {
            api = ApiURL.gymtrainers
            Log.e("trainerGymListApi", "" + api)
            param["id"] = "" + intent.getStringExtra("studio_id")

        }
        param["gender"] = "" + genderId
        param["language"] = "" + languageId
        param["lat"] = "" + latitude
        param["long"] = "" + longitude
        param["time_slot"] = "" + timeSlotId
        param["tag_id"] = "" + tag_id
        param["nationality"] = "" + nationId
        Log.e("trainerListParam", param.toString())

        PostMethod(api, param, this@TrainersListActivity).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                trainerList.clear()
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        var jsonArrayList = jsonObj.optJSONObject("data").optJSONArray("trainers")
                        if (jsonArrayList.length() > 0) {
                            for (i in 0 until jsonArrayList.length()) {
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
                                trainerModel.is_group = jsonObject1.optBoolean("is_group")
                                trainerModel.studio_id = jsonObject1.optString("studio_id")
                                trainerModel.isPackage = jsonObject1.optBoolean("is_package")
                                trainerModel.trainWithMe = jsonObject1.optString("train_with_me")
                                trainerList.add(trainerModel)
                            }
                            if ((tag_id.isNullOrEmpty()) && (filter.isNullOrEmpty() || filter == "0")) {
                                searchTrainerFullList.clear()
                                searchTrainerFullList.addAll(trainerList)
                            }
                            trainerListAdapter?.updateData(trainerList)
                            trainerRecyclerView.visibility = View.VISIBLE
                            tvNodata.visibility = View.GONE
                        } else {
                            trainerRecyclerView.visibility = View.GONE
                            tvNodata.visibility = View.VISIBLE
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

    private fun setTrainerListAdapter(){
        trainerListAdapter = TrainerListAdapter(
            this@TrainersListActivity,
            trainerList,
            typeLayout,
            sharedPreferences.getString("typeWorkout", ""),
            latitude,
            longitude,
            studio_id,
            onProfileClick = { trainersModel ->
                val intent = Intent(this, TrainerDetails::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("trainer_id",trainersModel.id)
                intent.putExtra("studio_id",studio_id)
                intent.putExtra("haveSlot",trainersModel.slot)
                intent.putExtra("type",sharedPreferences.getString("typeWorkout", ""))
                intent.putExtra("long",longitude)
                intent.putExtra("lat",latitude)
                intent.putExtra("address_id",address_id)
                startActivity(intent)
            }
        ) { isPackage,isGrp, type, id ->
            if(isPackage){
                // book slot
                val intent = Intent(this@TrainersListActivity, BookSlot::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("trainer_id",id)
                intent.putExtra("studio_id",studio_id)
                intent.putExtra("type",sharedPreferences.getString("typeWorkout", ""))
                intent.putExtra("long",longitude)
                intent.putExtra("lat",latitude)
                intent.putExtra("address_id",address_id)
                startActivity(intent)
            }else{
                if (isGrp) {
                    getPrimaryTrainer(type, id)
                } else {
                    getPrimaryTrainerRedirect(id)
                }
            }
        }
        trainerRecyclerView.adapter = trainerListAdapter
    }

    fun getPrimaryTrainerRedirect(id: String) {
        if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
            val intent = Intent(this, BestPlanTotalSessionWrapperActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("trainer_id", id)
            intent.putExtra("address_id", address_id)
            intent.putExtra("studio_id", studio_id)
            intent.putExtra("type", sharedPreferences.getString("typeWorkout", ""))
            intent.putExtra("long", longitude)
            intent.putExtra("lat", latitude)
            startActivity(intent)
        } else {
            val intent = Intent(this, BestPlanTotalSessionWrapperActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("trainer_id", id)
            intent.putExtra("address_id", address_id)
            intent.putExtra("studio_id", studio_id)
            intent.putExtra("type", sharedPreferences.getString("typeWorkout", ""))
            intent.putExtra("long", longitude)
            intent.putExtra("lat", latitude)
            startActivity(intent)

        }
    }

    fun getPrimaryTrainer(type: String, id: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.setContentView(R.layout.delay_view)
        progressDialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        progressDialog.setCancelable(false)
        progressDialog.show()

        val finalPath = ApiURL.getTrainerGroup + type
        GetMethod(finalPath, applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
//                        getPrimaryTrainerRedirect(id)
                        startActivityNew(data,id)
                    } else {
                        getPrimaryTrainerRedirect(id)

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
    fun startActivityNew(data:String,trainerId:String){
        val intent= Intent(this, TrainerGroupActivity::class.java)
        intent.putExtra(PASS_DATA,data)
        intent.putExtra("studio_id",studio_id)
        intent.putExtra("long",longitude)
        intent.putExtra("lat",latitude)
        intent.putExtra("address_id",address_id)
        intent.putExtra("trainerId",trainerId)
        startActivity(intent)
    }

    fun filterBottomSHeet(selectedType: String, selectedTypeCall: (String) -> Unit) {
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
                filterBottomSHeet(nextType) {

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

        tvgender = bottomSheet.findViewById<TextView>(R.id.tvgender)
        var tvApplyFilter = bottomSheet.findViewById<TextView>(R.id.tvApplyFilter)
        var tvReset = bottomSheet.findViewById<TextView>(R.id.tvReset)
        tvNationality = bottomSheet.findViewById<TextView>(R.id.tvNationality)
        tvLanguage = bottomSheet.findViewById<TextView>(R.id.tvLanguage)
        tvTimeslot = bottomSheet.findViewById<TextView>(R.id.tvTimeslot)
        linearTime = bottomSheet.findViewById<LinearLayout>(R.id.linearTime)
        linearNationality = bottomSheet.findViewById<LinearLayout>(R.id.linearNationality)
        genderRecycler = bottomSheet.findViewById<RecyclerView>(R.id.genderRecycler)
        nationRecycler = bottomSheet.findViewById<RecyclerView>(R.id.nationRecycler)
        LanguageRecycler = bottomSheet.findViewById<RecyclerView>(R.id.LanguageRecycler)
        timeSlotRecycler = bottomSheet.findViewById<RecyclerView>(R.id.timeSlotRecycler)
        LanguageRecycler = bottomSheet.findViewById<RecyclerView>(R.id.LanguageRecycler)
        val linearLanguage = bottomSheet.findViewById<LinearLayout>(R.id.linearLanguage)
        val linearGender = bottomSheet.findViewById<LinearLayout>(R.id.linearGender)
        viewGender = bottomSheet.findViewById<View>(R.id.viewGender)
        orangeViewGender = bottomSheet.findViewById<View>(R.id.orangeViewGender)

        orangeViewTime = bottomSheet.findViewById<View>(R.id.orangeViewTime)
        viewTime = bottomSheet.findViewById<View>(R.id.viewTime)

        viewLanguage = bottomSheet.findViewById<View>(R.id.viewLanguage)
        orangeViewLanguage = bottomSheet.findViewById<View>(R.id.orangeViewLanguage)

        viewNationality = bottomSheet.findViewById<View>(R.id.viewNationality)
        orangeViewNation = bottomSheet.findViewById<View>(R.id.orangeViewNation)

        var genderAdapter = GenderFilterAdapter(applicationContext, genderList, selectedgenderIds)
        genderRecycler.adapter = genderAdapter

        var nationalitiesAdapter =
            NationalitiesAdapter(applicationContext, nationrList, selectedNationIds)
        nationRecycler.adapter = nationalitiesAdapter

        var languageAdapter = LanguageAdapter(applicationContext, languageList, selectedLanguageIds)
        LanguageRecycler.adapter = languageAdapter

        var timeSLotAdapter =
            TimeSLotAdapter(applicationContext, timeSLotModelList, selectedtimeSLotIds)
        timeSlotRecycler.adapter = timeSLotAdapter

        if (selectedType.equals("Gender")) {
            orangeViewGender.visibility = View.VISIBLE
            viewGender.visibility = View.GONE
            genderRecycler.visibility = View.VISIBLE
//            tvgender.setTextColor(resources.getColor(R.color.orangecolor))
            tvgender.setBackgroundResource(R.drawable.nean_gradient_horizontal)

            orangeViewTime.visibility = View.GONE
            viewTime.visibility = View.VISIBLE
            timeSlotRecycler.visibility = View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility = View.GONE
            viewLanguage.visibility = View.VISIBLE
            LanguageRecycler.visibility = View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility = View.GONE
            viewNationality.visibility = View.VISIBLE
            nationRecycler.visibility = View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))

        } else if (selectedType.equals("timeslot")) {
            orangeViewGender.visibility = View.GONE
            viewGender.visibility = View.VISIBLE
            genderRecycler.visibility = View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility = View.VISIBLE
            viewTime.visibility = View.GONE
            timeSlotRecycler.visibility = View.VISIBLE
//            tvTimeslot.setTextColor(resources.getColor(R.color.orangecolor))
            tvTimeslot.setBackgroundResource(R.drawable.nean_gradient_horizontal)

            orangeViewLanguage.visibility = View.GONE
            viewLanguage.visibility = View.VISIBLE
            LanguageRecycler.visibility = View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility = View.GONE
            viewNationality.visibility = View.VISIBLE
            nationRecycler.visibility = View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))

        } else if (selectedType.equals("Language")) {
            orangeViewGender.visibility = View.GONE
            viewGender.visibility = View.VISIBLE
            genderRecycler.visibility = View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility = View.GONE
            viewTime.visibility = View.VISIBLE
            timeSlotRecycler.visibility = View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility = View.VISIBLE
            viewLanguage.visibility = View.GONE
            LanguageRecycler.visibility = View.VISIBLE
//            tvLanguage.setTextColor(resources.getColor(R.color.orangecolor))
            tvLanguage.setBackgroundResource(R.drawable.nean_gradient_horizontal)

            orangeViewNation.visibility = View.GONE
            viewNationality.visibility = View.VISIBLE
            nationRecycler.visibility = View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))

        } else {
            orangeViewGender.visibility = View.GONE
            viewGender.visibility = View.VISIBLE
            genderRecycler.visibility = View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewTime.visibility = View.GONE
            viewTime.visibility = View.VISIBLE
            timeSlotRecycler.visibility = View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility = View.GONE
            viewLanguage.visibility = View.VISIBLE
            LanguageRecycler.visibility = View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewNation.visibility = View.VISIBLE
            viewNationality.visibility = View.GONE
            nationRecycler.visibility = View.VISIBLE
//            tvNationality.setTextColor(resources.getColor(R.color.orangecolor))
            tvNationality.setBackgroundResource(R.drawable.nean_gradient_horizontal)

        }
        linearGender.setOnClickListener {
            orangeViewGender.visibility = View.VISIBLE
            viewGender.visibility = View.GONE
            genderRecycler.visibility = View.VISIBLE
            // tvgender.setTextColor(resources.getColor(R.color.orangecolor))
            tvgender.setBackgroundResource(R.drawable.nean_gradient_horizontal)

            orangeViewTime.visibility = View.GONE
            viewTime.visibility = View.VISIBLE
            timeSlotRecycler.visibility = View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility = View.GONE
            viewLanguage.visibility = View.VISIBLE
            LanguageRecycler.visibility = View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility = View.GONE
            viewNationality.visibility = View.VISIBLE
            nationRecycler.visibility = View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))
        }
        linearTime.setOnClickListener {
            orangeViewGender.visibility = View.GONE
            viewGender.visibility = View.VISIBLE
            genderRecycler.visibility = View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility = View.VISIBLE
            viewTime.visibility = View.GONE
            timeSlotRecycler.visibility = View.VISIBLE
            // tvTimeslot.setTextColor(resources.getColor(R.color.orangecolor))
//            tvTimeslot.setBackgroundColor(resources.getColor(R.color.tabbackground))
            tvTimeslot.setBackgroundResource(R.drawable.nean_gradient_horizontal)

            orangeViewLanguage.visibility = View.GONE
            viewLanguage.visibility = View.VISIBLE
            LanguageRecycler.visibility = View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewNation.visibility = View.GONE
            viewNationality.visibility = View.VISIBLE
            nationRecycler.visibility = View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))
        }
        linearLanguage.setOnClickListener {
            orangeViewGender.visibility = View.GONE
            viewGender.visibility = View.VISIBLE
            genderRecycler.visibility = View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))

            orangeViewTime.visibility = View.GONE
            viewTime.visibility = View.VISIBLE
            timeSlotRecycler.visibility = View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility = View.VISIBLE
            viewLanguage.visibility = View.GONE
            LanguageRecycler.visibility = View.VISIBLE
            //tvLanguage.setTextColor(resources.getColor(R.color.orangecolor))
//            tvLanguage.setBackgroundColor(resources.getColor(R.color.tabbackground))
            tvLanguage.setBackgroundResource(R.drawable.nean_gradient_horizontal)

            orangeViewNation.visibility = View.GONE
            viewNationality.visibility = View.VISIBLE
            nationRecycler.visibility = View.GONE
            tvNationality.setTextColor(resources.getColor(R.color.headingcolor))
            tvNationality.setBackgroundColor(resources.getColor(R.color.filter_unselected))
        }
        linearNationality.setOnClickListener {
            orangeViewGender.visibility = View.GONE
            viewGender.visibility = View.VISIBLE
            genderRecycler.visibility = View.GONE
            tvgender.setTextColor(resources.getColor(R.color.headingcolor))
            tvgender.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewTime.visibility = View.GONE
            viewTime.visibility = View.VISIBLE
            timeSlotRecycler.visibility = View.GONE
            tvTimeslot.setTextColor(resources.getColor(R.color.headingcolor))
            tvTimeslot.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewLanguage.visibility = View.GONE
            viewLanguage.visibility = View.VISIBLE
            LanguageRecycler.visibility = View.GONE
            tvLanguage.setTextColor(resources.getColor(R.color.headingcolor))
            tvLanguage.setBackgroundColor(resources.getColor(R.color.filter_unselected))


            orangeViewNation.visibility = View.VISIBLE
            viewNationality.visibility = View.GONE
            nationRecycler.visibility = View.VISIBLE
//            tvNationality.setTextColor(resources.getColor(R.color.orangecolor))
            tvNationality.setBackgroundResource(R.drawable.nean_gradient_horizontal)
        }
        tvApplyFilter.setOnClickListener {

            genderId = genderAdapter.getSelectedIdString()
            timeSlotId = timeSLotAdapter.getSelectedIdString()
            languageId = languageAdapter.getSelectedIdString()
            nationId = nationalitiesAdapter.getSelectedIdString()
            getTrainerList(tag_id, filter)
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

            getTrainerList(tag_id, filter)
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
        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.show()
        var api = ""
        api = ApiURL.get_filter_data
        Log.e("getfilterAPi", "" + api)

        GetMethod(api, applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                genderList.clear()
                nationrList.clear()
                languageList.clear()
                timeSLotModelList.clear()
                Log.e("GetfilterResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        var jsonGender = jsonObj.optJSONObject("data").optJSONArray("gender")
                        var jsonlanguages = jsonObj.optJSONObject("data").optJSONArray("languages")
                        var jsonnationalities =
                            jsonObj.optJSONObject("data").optJSONArray("nationalities")
                        var jsontime_slots =
                            jsonObj.optJSONObject("data").optJSONArray("time_slots")
                        for (i in 0 until jsonGender.length()) {
                            var jsonObject1 = jsonGender.optJSONObject(i)
                            val genderModel1 = GenderModel()
                            genderModel1.name = jsonObject1.optString("name")
                            genderModel1.id = jsonObject1.optString("id")
                            Log.e("genderListId", "" + jsonObject1.optString("id"))

                            genderList.add(genderModel1)
                        }

                        for (i in 0 until jsonnationalities.length()) {
                            var jsonObject1 = jsonnationalities.optJSONObject(i)
                            val genderModel2 = NationModel()
                            genderModel2.name = jsonObject1.optString("name")
                            genderModel2.id = jsonObject1.optString("id")
                            nationrList.add(genderModel2)
                        }

                        for (i in 0 until jsonlanguages.length()) {
                            var jsonObject1 = jsonlanguages.optJSONObject(i)
                            val genderModel = LanguageModel()
                            genderModel.name = jsonObject1.optString("name")
                            genderModel.id = jsonObject1.optString("id")
                            languageList.add(genderModel)
                        }
                        for (i in 0 until jsontime_slots.length()) {
                            var jsonObject1 = jsontime_slots.optJSONObject(i)
                            val timeSLotModel = TimeSLotModel()
                            timeSLotModel.name = jsonObject1.optString("name")
                            timeSLotModel.id = jsonObject1.optString("id")
                            timeSLotModelList.add(timeSLotModel)
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
}