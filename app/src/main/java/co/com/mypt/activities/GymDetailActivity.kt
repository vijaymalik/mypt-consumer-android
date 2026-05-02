package co.com.mypt.activities

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager.widget.ViewPager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants.HAS_GYM
import co.com.mypt.Api.Constants.HAS_HOME
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withoutTrainer.GymValidityActivity
import co.com.mypt.PlanRenewal.Renew.RenewHomeGymSessionActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.GalleryAdapter
import co.com.mypt.adapter.GymEquipmentAdapter
import co.com.mypt.adapter.GymOfferAdapter
import co.com.mypt.adapter.ReviewAdapter
import co.com.mypt.adapter.ViewPagerImageAdapter
import co.com.mypt.curvedBottomNavigation.dpToPx
import co.com.mypt.model.GalleryModel
import co.com.mypt.model.GymEquipmentModel
import co.com.mypt.model.GymOfferModel
import co.com.mypt.model.ReviewModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class GymDetailActivity : AppCompatActivity() , ViewTreeObserver.OnScrollChangedListener,
    OnMapReadyCallback {
    lateinit var gymOfferRecyclerView:RecyclerView
    lateinit var equipmentRecyclerView:RecyclerView
    lateinit var reviewRecycle:RecyclerView
    lateinit var tvReviewLabel: TextView
    lateinit var bookSlot:TextView
    lateinit var back: ImageView
    private lateinit var uiSettings: UiSettings
    lateinit var back_1: ImageView
    lateinit var relative: ConstraintLayout
    var gymOfferModelList : ArrayList<GymOfferModel> = ArrayList()
    var gymEquipmentModelList : ArrayList<GymEquipmentModel> = ArrayList()
    lateinit var galleryRecyclerView : RecyclerView
    var galleryArrayList = ArrayList<GalleryModel>()
    var reviewArrayList = ArrayList<ReviewModel>()
    var imageList = ArrayList<String>()
    lateinit var scrollView : NestedScrollView
    lateinit var headerLayout : LinearLayout
    lateinit var linearWhatthisGym : LinearLayout
    lateinit var linearEquipment : LinearLayout
    lateinit var linearGallery : LinearLayout
    private lateinit var mMap: GoogleMap
    lateinit var userName : TextView
    lateinit var aboutTrainerText : TextView
    lateinit var tv : TextView
    lateinit var trainerName_1 : TextView
    lateinit var place : TextView
    lateinit var distance : TextView
    lateinit var avgRating : TextView
    lateinit var avgRating1 : TextView
    lateinit var totalRatings : TextView
    lateinit var tvEquipment : TextView
    lateinit var totalRatings1 : TextView
    lateinit var tvlocation : TextView
    lateinit var tvReview : TextView
    lateinit var imShare : ImageView
    lateinit var imShare1 : ImageView
    lateinit var gymTempImage : ImageView
    lateinit var card : CardView
    lateinit var linear_gymfeature : LinearLayout
    lateinit var mediaGallery : LinearLayout
    lateinit var Equipment : LinearLayout
    lateinit var aboutMe : LinearLayout
    lateinit var viewPager: ViewPager
    lateinit var dots_indicator: DotsIndicator
    lateinit var viewPagerAdapter: ViewPagerImageAdapter

    var text = ""
    val spannableStringBuilder = SpannableStringBuilder()
    private val maxLines = 2
    private var isExpanded = false
    lateinit var sharedPreferences:SharedPreferences
    var REQUEST_ID_MULTIPLE_PERMISSIONS=101
    var studio_id=""
    var flowType=""
    var canbookstring=""
    var canMembershipString=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gym_detail)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        gymOfferRecyclerView=findViewById(R.id.gymOfferRecyclerView)
        back=findViewById(R.id.back)
        card=findViewById(R.id.card)
        gymTempImage=findViewById(R.id.gymTempImage)
        back_1=findViewById(R.id.back_1)
        imShare=findViewById(R.id.imShare)
        imShare1=findViewById(R.id.imShare1)
        aboutMe=findViewById(R.id.aboutMe)
        Equipment=findViewById(R.id.Equipment)
        mediaGallery=findViewById(R.id.mediaGallery)
        linear_gymfeature=findViewById(R.id.linear_gymfeature)
        equipmentRecyclerView=findViewById(R.id.equipmentRecyclerView)
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView)
        reviewRecycle = findViewById(R.id.reviewRecycle)
        tvReviewLabel = findViewById(R.id.tvReviewLabel)
        scrollView = findViewById(R.id.scrollView)
        bookSlot = findViewById(R.id.bookSlot)
        headerLayout = findViewById(R.id.headerLayout)
        relative = findViewById(R.id.relative)
        viewPager = findViewById(R.id.idViewPager)
        userName = findViewById(R.id.userName)
        place = findViewById(R.id.place)
        totalRatings = findViewById(R.id.totalRatings)
        linearWhatthisGym = findViewById(R.id.linearWhatthisGym)
        aboutTrainerText = findViewById(R.id.aboutTrainerText)
        avgRating = findViewById(R.id.avgRating)
        avgRating1 = findViewById(R.id.avgRating1)
        distance = findViewById(R.id.distance)
        dots_indicator = findViewById(R.id.dots_indicator)
        linearEquipment = findViewById(R.id.linearEquipment)
        linearGallery = findViewById(R.id.linearGallery)
        trainerName_1 = findViewById(R.id.trainerName_1)
        totalRatings1 = findViewById(R.id.totalRatings1)
        tvlocation = findViewById(R.id.tvlocation)
        tvEquipment = findViewById(R.id.tvEquipment)
        tvReview = findViewById(R.id.tvReview)
        tv = findViewById(R.id.tv)
        studio_id=""+intent.getStringExtra("studio_id")
        flowType=""+intent.getStringExtra("type")

        bookSlot.text = "SELECT THIS GYM"
        Log.e("flowType",flowType)
        //checkAndRequestPermissions()
        back.setOnClickListener {
            finish()
        }

        back_1.setOnClickListener {
            finish()
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        scrollView.viewTreeObserver.addOnScrollChangedListener(this)

        (galleryRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        tvEquipment.setOnClickListener{
            if(tvEquipment.text.contains("SHOW ALL")){
                tvEquipment.text = "SHOW LESS"

                var gymEquipmentAdapter= GymEquipmentAdapter(applicationContext,gymEquipmentModelList,"Full")
                equipmentRecyclerView.adapter=gymEquipmentAdapter
            }else{
                var gymEquipmentAdapter= GymEquipmentAdapter(applicationContext,gymEquipmentModelList,"limited")
                equipmentRecyclerView.adapter=gymEquipmentAdapter
                tvEquipment.text = "SHOW ALL ${gymEquipmentModelList.size} EQUIPMENTS"
            }


        }
        aboutMe.setOnClickListener{
            aboutMe.setBackgroundResource(R.drawable.category_border_bg)
            linear_gymfeature.setBackgroundDrawable(null)
            Equipment.setBackgroundDrawable(null)
            mediaGallery.setBackgroundDrawable(null)
        }
        imShare.setOnClickListener{
            shareImageAndText(this, gymTempImage)
        }



        imShare1.setOnClickListener{
            shareImageAndText(this, gymTempImage)

        }
        linear_gymfeature.setOnClickListener{
            linear_gymfeature.setBackgroundResource(R.drawable.category_border_bg)
            aboutMe.setBackgroundDrawable(null)
            Equipment.setBackgroundDrawable(null)
            mediaGallery.setBackgroundDrawable(null)
        }
        mediaGallery.setOnClickListener{
            mediaGallery.setBackgroundResource(R.drawable.category_border_bg)
            aboutMe.setBackgroundDrawable(null)
            linear_gymfeature.setBackgroundDrawable(null)
            Equipment.setBackgroundDrawable(null)
            val y = linearGallery.y.toInt()-200 // Get Y position of the layout
            ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                duration = 1200  // Animation duration in milliseconds
                start()
            }
        }
        Equipment.setOnClickListener{
            Equipment.setBackgroundResource(R.drawable.category_border_bg)
            aboutMe.setBackgroundDrawable(null)
            linear_gymfeature.setBackgroundDrawable(null)
            mediaGallery.setBackgroundDrawable(null)
            val y = linearEquipment.y.toInt()-100  // Get Y position of the layout
            ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                duration = 1500  // Animation duration in milliseconds
                start()
            }
        }





        try {
            val appLinkIntent: Intent = intent
            val appLinkAction: String? = appLinkIntent.action
            val appLinkData: Uri? = appLinkIntent.data
            Log.e("appLinkData",""+appLinkData)
            // Split the path segments
            val segments = appLinkData!!.pathSegments
            studio_id = segments[1]
            flowType = segments[2]

        }catch (e:Exception){
            e.printStackTrace()
        }
        getGymDetailData()



    }

    private fun getGymDetailData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var api=""
   /*     if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.trainer_Detail+intent.getStringExtra("trainer_id")+"&studio_id="+studio_id+"&type="+type+"&long="+longitude+"&lat="+latitude
        }else{
            api= ApiURL.trainer_Detail+intent.getStringExtra("trainer_id")+"&studio_id="+studio_id+"&type="+"gym"+"&long="+longitude+"&lat="+latitude
        }*/

        api= ApiURL.studiodetails+studio_id+"&long="+intent.getDoubleExtra("long",0.0)+"&lat="+intent.getDoubleExtra("lat",0.0)
        Log.e("gymDetailApi",""+api)
        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {

            override fun response(data: String?) {
                progressDialog.dismiss()
                gymOfferModelList.clear()
                gymEquipmentModelList.clear()
                Log.e("gymDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        bookSlot.visibility=View.VISIBLE
                        var jsonData=jsonObj.optJSONObject("data")
                        userName.text = jsonData.optString("name")
                        trainerName_1.text = jsonData.optString("name")
                        tv.text = jsonData.optString("timing")
                        canbookstring=jsonData.optString("canBook")
                        canMembershipString=jsonData.optString("canMembership")
                        if (sharedPreferences.getString("typewithout","").equals("withoutTrainer") || sharedPreferences.getString("typewithout","").equals("withTrainer")){
                            if (canMembershipString.equals("true")){
                                bookSlot.setOnClickListener{
                                    /* if (intent.getStringExtra("type").equals("withoutTrainer")){
                                         val intent = Intent(this, GymValidityActivity::class.java)
                                         startActivity(intent)
                                     }else{

                                     }*/
                                    if (flowType == "renew"){
                                        val intent = Intent(this@GymDetailActivity, RenewHomeGymSessionActivity::class.java)
                                        intent.putExtra("studio_id",studio_id)
                                        startActivity(intent)
                                        return@setOnClickListener
                                    }
                                    if ((flowType.equals("withoutTrainer")) || (flowType.equals("withTrainer"))){
                                        if (flowType.equals("withoutTrainer")){
                                            val intent = Intent(applicationContext, GymValidityActivity::class.java)
                                            intent.putExtra("studio_id",studio_id)
                                            startActivity(intent)
                                        }else{
                                            val intent = Intent(applicationContext, TrainersListActivity::class.java)
                                            intent.putExtra("studio_id",studio_id)
                                            intent.putExtra("type","withTrainer")
                                            startActivity(intent)
                                        }
                                    }else{
                                        val intent1 = Intent(this@GymDetailActivity, TrainersListActivity::class.java)
                                        if (sharedPreferences.getString("typeWorkout","").equals("work")){
                                            intent1.putExtra("studio_id", studio_id)
                                        }
                                        startActivity(intent1)
                                    }


                                }
                                bookSlot.setTextColor(resources.getColor(R.color.text_color_primary_black,null))
                                bookSlot.background = resources.getDrawable(R.drawable.primary_btn_gradient,null)

                            }
                            else {
                                bookSlot.setTextColor(resources.getColor(R.color.white))
                                bookSlot.background = resources.getDrawable(R.drawable.grey_rectangle_rounded,null)
                                bookSlot.setOnClickListener {
                                }
                            }
                        }
                        else if (sharedPreferences.getString("typeWorkout","").equals("home") || sharedPreferences.getString("typeWorkout","").equals("work")){
                            if (canbookstring.equals("true")){
                                bookSlot.setOnClickListener{
                                    /* if (intent.getStringExtra("type").equals("withoutTrainer")){
                                         val intent = Intent(this, GymValidityActivity::class.java)
                                         startActivity(intent)
                                     }else{

                                     }*/
                                    if (flowType == "renew"){
                                        val intent = Intent(this@GymDetailActivity, RenewHomeGymSessionActivity::class.java)
                                        intent.putExtra("studio_id",studio_id)
                                        startActivity(intent)
                                        return@setOnClickListener
                                    }
                                    if ((flowType.equals("withoutTrainer")) || (flowType.equals("withTrainer"))){
                                        if (flowType.equals("withoutTrainer")){
                                            val intent = Intent(applicationContext, GymValidityActivity::class.java)
                                            intent.putExtra("studio_id",studio_id)
                                            startActivity(intent)
                                        }else{
                                            val intent = Intent(applicationContext, TrainersListActivity::class.java)
                                            intent.putExtra("studio_id",studio_id)
                                            intent.putExtra("type","withTrainer")
                                            startActivity(intent)
                                        }
                                    }else{
                                        val type = sharedPreferences.getString("typeWorkout", "")
                                        val hasPlan = when (type) {
                                            "work" -> intent.getBooleanExtra(HAS_GYM, false)
                                            else -> false
                                        }

                                        val targetActivity = if (hasPlan) {
                                            TrainersListActivity::class.java
                                        } else {
                                            CreatePackagectivity::class.java
                                        }
                                        val intent1 = Intent(this@GymDetailActivity, targetActivity)
                                        intent1.putExtra("longitude",intent.getDoubleExtra("long",0.0))
                                        intent1.putExtra("latitude",intent.getDoubleExtra("lat",0.0))
                                        if (sharedPreferences.getString("typeWorkout","").equals("work")){
                                            intent1.putExtra("studio_id", studio_id)
                                        }
                                        startActivity(intent1)
                                    }


                                }
                                bookSlot.setTextColor(resources.getColor(R.color.text_color_primary_black,null))
                                bookSlot.background = resources.getDrawable(R.drawable.primary_btn_gradient,null)

                            }
                            else {
                                bookSlot.setTextColor(resources.getColor(R.color.white))
                                bookSlot.background = resources.getDrawable(R.drawable.grey_rectangle_rounded,null)
                                bookSlot.setOnClickListener {
                                }
                            }
                        }



                        if (jsonData.optString("averageRating").equals("") || jsonData.optString("averageRating").equals("null") ){
                            avgRating.text = "0"
                            avgRating1.text = "0"
                        }else{
                            avgRating1.text = jsonData.optString("averageRating")
                        }

                        place.text = jsonData.optString("address")
                        tvlocation.text = jsonData.optString("address")
                        distance.text = jsonData.optString("distance")
                        aboutTrainerText.text = jsonData.optString("description")
                        totalRatings.text = jsonData.optString("noOfRating")+" ratings"
                        totalRatings1.text = jsonData.optString("noOfRating")+" ratings"

                        if (jsonData.optJSONArray("facility").length()>0){
                            linearWhatthisGym.visibility=View.VISIBLE
                            for(i in 0 until jsonData.optJSONArray("facility").length()){
                                var json=jsonData.optJSONArray("facility").optJSONObject(i)
                                var gymOfferModel= GymOfferModel()
                                gymOfferModel.name=json.optString("name")
                                gymOfferModel.icon = json.optString("icon")
                                gymOfferModelList.add(gymOfferModel)

                            }
                            var gymOfferAdapter= GymOfferAdapter(applicationContext,gymOfferModelList)
                            gymOfferRecyclerView.adapter=gymOfferAdapter
                        }else{
                            linearWhatthisGym.visibility=View.GONE
                        }

                        if (jsonData.optJSONArray("amenity").length()>0){
                            if (jsonData.optJSONArray("amenity").length()>4){
                                tvEquipment.visibility=View.VISIBLE

                            }else{
                                tvEquipment.visibility=View.GONE

                            }
                            linearEquipment.visibility=View.VISIBLE

                            for(i in 0 until jsonData.optJSONArray("amenity").length()){
                                var gymEquipmentModel= GymEquipmentModel()
                                gymEquipmentModel.name= jsonData.optJSONArray("amenity").get(i).toString()

                                gymEquipmentModelList.add(gymEquipmentModel)
                            }
                            var gymEquipmentAdapter= GymEquipmentAdapter(applicationContext,gymEquipmentModelList,"limited")
                            equipmentRecyclerView.adapter=gymEquipmentAdapter
                            tvEquipment.text = "SHOW ALL ${gymEquipmentModelList.size} EQUIPMENTS"
                        }else{
                            linearEquipment.visibility=View.GONE
                        }
                        for(i in 0 until jsonData.optJSONArray("profile").length()){
                            imageList.add(jsonData.optJSONArray("profile").get(i).toString())
                        }
                        viewPagerAdapter = ViewPagerImageAdapter(this@GymDetailActivity, imageList)
                        viewPager.adapter = viewPagerAdapter
                        dots_indicator.attachTo(viewPager)
                        Glide.with(applicationContext!!).load(imageList.get(0)).fitCenter().into(gymTempImage)



                        if (jsonData.optJSONArray("gallery").length()>0){
                            linearGallery.visibility=View.VISIBLE
                            for(i in 0 until jsonData.optJSONArray("gallery").length()){
                                var json=jsonData.optJSONArray("gallery").optJSONObject(i)
                                var galleryModel=GalleryModel()
                                galleryModel.media_path=json.optString("media_path")
                                galleryModel.is_video=json.optString("is_video")
                                galleryArrayList.add(galleryModel)
                            }
                            galleryRecyclerView.adapter = GalleryAdapter(applicationContext,galleryArrayList)
                        }else{
                            linearGallery.visibility=View.GONE
                        }
                        if (jsonData.optJSONArray("reviews").length()>0){
                            reviewRecycle.visibility=View.VISIBLE
                            tvReviewLabel.visibility=View.VISIBLE
                            for(i in 0 until jsonData.optJSONArray("reviews").length()){
                                var json=jsonData.optJSONArray("reviews").optJSONObject(i)
                                var reviewModel=ReviewModel()
                                reviewModel.name=json.optString("name")
                                reviewModel.image=json.optString("image")
                                reviewModel.description=json.optString("description")
                                reviewModel.time=json.optString("time")
                                reviewModel.rating=json.optString("rating")
                                reviewArrayList.add(reviewModel)
                            }

                            reviewRecycle.adapter = ReviewAdapter(applicationContext,reviewArrayList)
                        }else{
                            reviewRecycle.visibility=View.GONE
                            tvReviewLabel.visibility=View.GONE
                        }

                        aboutTrainerText.text = jsonData.optString("description")

                        text = aboutTrainerText.text.toString()

                        aboutTrainerText.post {
                            if (aboutTrainerText.lineCount > maxLines) {
                                setupCollapsedText()
                            }
                        }

                        // Set Latitude and Longitude for Marker
                        val location = LatLng(jsonData.optDouble("latitude",0.0), jsonData.optDouble("longitude",0.0)) // Example: New Delhi

                        // Add Marker
                        mMap.addMarker(
                            MarkerOptions().position(location).icon(
                                resizedMarker(
                                    this@GymDetailActivity,
                                    R.drawable.marker,
                                    60.dpToPx(this@GymDetailActivity),
                                    60.dpToPx(this@GymDetailActivity)
                                )
                            ).anchor(0.5f, 0.8f)
                        )

                        // Move Camera to the Marker
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                        uiSettings = mMap.uiSettings

                        // Hide the map toolbar (and the navigation icon)
                        uiSettings.isMapToolbarEnabled = true
                        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${jsonData.optDouble("latitude",0.0)},${jsonData.optDouble("longitude",0.0)}")
                        mMap.setOnMapClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            startActivity(intent)
                        }
                        mMap.setOnMarkerClickListener { marker ->
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@GymDetailActivity, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
                            }

                            true // return true if you handled the click
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

    override fun onScrollChanged() {
        val scrollBounds = Rect()
        scrollView.getHitRect(scrollBounds)
        if (relative.getLocalVisibleRect(scrollBounds)) {
            headerLayout.visibility = View.GONE
        } else {
            headerLayout.visibility = View.VISIBLE
        }
    }
    private fun setupCollapsedText() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                setupExpandedText()
            }
        }

        spannableStringBuilder.clear()
        spannableStringBuilder.append(text.substring(0, Math.min(text.length, maxLines * 45)))
        spannableStringBuilder.append("... Read More")

        // Set color for "Read More"
        val readMoreSpan = ForegroundColorSpan(Color.parseColor("#F6AA54"))
        val start = spannableStringBuilder.length - 9 // Length of "... Read More"
        val end = spannableStringBuilder.length

        // Set clickable span for "Read More"
        spannableStringBuilder.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableStringBuilder.setSpan(readMoreSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        aboutTrainerText.text = spannableStringBuilder
        aboutTrainerText.maxLines = maxLines // Set maxLines for collapsed state
        aboutTrainerText.movementMethod = LinkMovementMethod.getInstance()
        aboutTrainerText.isClickable = true
        isExpanded = false
    }

    private fun setupExpandedText() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                setupCollapsedText()
            }
        }

        spannableStringBuilder.clear()
        spannableStringBuilder.append(text)
        spannableStringBuilder.append(" Read Less")

        // Set color for "Read Less"
        val readLessSpan = ForegroundColorSpan(Color.parseColor("#F6AA54"))
        val start = spannableStringBuilder.length - 9 // Length of " Read Less"
        val end = spannableStringBuilder.length

        // Set clickable span for "Read Less"
        spannableStringBuilder.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableStringBuilder.setSpan(readLessSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        aboutTrainerText.text = spannableStringBuilder
        aboutTrainerText.maxLines = Int.MAX_VALUE // Set maxLines to unlimited for expanded state
        aboutTrainerText.movementMethod = LinkMovementMethod.getInstance()
        aboutTrainerText.isClickable = true
        isExpanded = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map))
        getGymDetailData()
    }


    fun shareImageAndText(context: Context, imageView: ImageView) {
        val drawable = imageView.drawable ?: return

        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return

        // Save bitmap to cache directory
        val file = File(context.cacheDir, "share_image_${System.currentTimeMillis()}.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        if (contentUri != null){
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, contentUri) // image
                putExtra(Intent.EXTRA_TEXT,userName.text.toString()+"\n\n"+"https://mobileapp.mypt-me.com/gym/${studio_id}/${flowType.replace("null","")}")
                // text
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(context.contentResolver, "Image", contentUri)
            }

            // Grant URI permission to all potential receivers
            val resInfoList = context.packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resInfo in resInfoList) {
                val packageName = resInfo.activityInfo.packageName
                context.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }else{
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_TEXT,userName.text.toString()+"\n\n"+"https://mobileapp.mypt-me.com/gym/${studio_id}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

    }

    fun resizedMarker(context: Context, drawableId: Int, width: Int, height: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}