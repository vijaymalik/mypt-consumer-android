package co.com.mypt.Profile

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.AddressListForTrainerActivity
import co.com.mypt.activities.BookSlot
import co.com.mypt.adapter.CertificateAdapter
import co.com.mypt.adapter.GalleryAdapter
import co.com.mypt.adapter.SpecialitiesAdapter
import co.com.mypt.model.CertificateModel
import co.com.mypt.model.GalleryModel
import co.com.mypt.model.SpecialitiesModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class ProfileTrainerDetailActivity : AppCompatActivity() , ViewTreeObserver.OnScrollChangedListener{
    lateinit var trainerImage : ImageView
    lateinit var play : ImageView
    lateinit var back : ImageView
    lateinit var back_1 : ImageView

    lateinit var scrollView : NestedScrollView
    lateinit var headerLayout : LinearLayout
    lateinit var LinearWhyTrainWithMe : LinearLayout
    lateinit var linearMedia : LinearLayout
    lateinit var linearCertificate : LinearLayout
    lateinit var mediaGallery : LinearLayout
    lateinit var certificateRecycler : RecyclerView


    lateinit var trainerName_1 : TextView
    lateinit var follow_1 : TextView
    lateinit var follow : TextView
    lateinit var userName : TextView
    lateinit var clientsCoached : TextView
    lateinit var totalExp : TextView
    lateinit var avgRating1 : TextView
//    lateinit var bookSlot : TextView
    lateinit var distance : TextView
    lateinit var place : TextView
    lateinit var followersCount : TextView
    lateinit var avgRating : TextView
    lateinit var tvQuote : TextView
    lateinit var tvreadMore : TextView

    lateinit var totalRatings : TextView
    lateinit var whyTrain : LinearLayout
    lateinit var aboutMe : LinearLayout
    lateinit var review : LinearLayout
    lateinit var aboutTrainerText : TextView

    lateinit var videoView: VideoView

    lateinit var specialitiesRecyclerView : RecyclerView
    lateinit var galleryRecyclerView : RecyclerView

    var specialitiesArrayList = ArrayList<SpecialitiesModel>()
    var galleryArrayList = ArrayList<GalleryModel>()
    var certificateArrayList = ArrayList<CertificateModel>()
    private var isExpanded = false
    private val maxLines = 2
    val spannableStringBuilder = SpannableStringBuilder()
    var text = ""
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_trainer_detail)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView)
        trainerName_1 = findViewById(R.id.trainerName_1)
        followersCount = findViewById(R.id.followersCount)
        userName = findViewById(R.id.userName)
        distance = findViewById(R.id.distance)
        place = findViewById(R.id.place)
        back = findViewById(R.id.back)
        back_1 = findViewById(R.id.back_1)
        whyTrain = findViewById(R.id.whyTrain)
        mediaGallery = findViewById(R.id.mediaGallery)
        linearCertificate = findViewById(R.id.linearCertificate)
        LinearWhyTrainWithMe = findViewById(R.id.LinearWhyTrainWithMe)
        avgRating = findViewById(R.id.avgRating)
        aboutTrainerText = findViewById(R.id.aboutTrainerText)
        totalRatings = findViewById(R.id.totalRatings)
        totalExp = findViewById(R.id.totalExp)
        tvreadMore = findViewById(R.id.tvreadMore)
        certificateRecycler = findViewById(R.id.certificateRecycler)
        specialitiesRecyclerView = findViewById(R.id.specialitiesRecyclerView)
        aboutMe = findViewById(R.id.aboutMe)
        review = findViewById(R.id.review)
        follow_1 = findViewById(R.id.follow_1)
        follow = findViewById(R.id.follow)
        (galleryRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

//        bookSlot = findViewById(R.id.bookSlot)
        play = findViewById(R.id.play)
        videoView = findViewById(R.id.videoView)

        clientsCoached = findViewById(R.id.clientsCoached)
        avgRating1 = findViewById(R.id.avgRating1)
        totalExp = findViewById(R.id.totalExp)

        headerLayout = findViewById(R.id.headerLayout)
        trainerImage = findViewById(R.id.trainerImage)
        scrollView = findViewById(R.id.scrollView)
        tvQuote = findViewById(R.id.tvQuote)
        linearMedia = findViewById(R.id.linearMedia)

        scrollView.viewTreeObserver.addOnScrollChangedListener(this)

        aboutMe.setOnClickListener{
            aboutMe.setBackgroundResource(R.drawable.category_border_bg)
            review.setBackgroundDrawable(null)
            whyTrain.setBackgroundDrawable(null)
            mediaGallery.setBackgroundDrawable(null)
        }
        review.setOnClickListener{
            review.setBackgroundResource(R.drawable.category_border_bg)
            aboutMe.setBackgroundDrawable(null)
            whyTrain.setBackgroundDrawable(null)
            mediaGallery.setBackgroundDrawable(null)
        }
        whyTrain.setOnClickListener{
            whyTrain.setBackgroundResource(R.drawable.category_border_bg)
            aboutMe.setBackgroundDrawable(null)
            review.setBackgroundDrawable(null)
            mediaGallery.setBackgroundDrawable(null)
            val y = LinearWhyTrainWithMe.y.toInt()-150 // Get Y position of the layout
            ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                duration = 1200  // Animation duration in milliseconds
                start()
            }
        }
        mediaGallery.setOnClickListener{
            mediaGallery.setBackgroundResource(R.drawable.category_border_bg)
            aboutMe.setBackgroundDrawable(null)
            review.setBackgroundDrawable(null)
            whyTrain.setBackgroundDrawable(null)
            val y = linearMedia.y.toInt()  // Get Y position of the layout
            ObjectAnimator.ofInt(scrollView, "scrollY", y).apply {
                duration = 1500  // Animation duration in milliseconds
                start()
            }
        }
        getTrainerDetail(intent.getStringExtra("trainer_id"),intent.getStringExtra("studio_id"),intent.getStringExtra("type"),
            intent.getDoubleExtra("long",0.0).toString(),
            intent.getDoubleExtra("lat",0.0).toString()
        )
        back_1.setOnClickListener {
            finish()
        }
        follow.setOnClickListener {
            sendFolowData(intent.getStringExtra("trainer_id"))
        }
        follow_1.setOnClickListener {
            sendFolowData(intent.getStringExtra("trainer_id"))
        }


        back.setOnClickListener {
            finish()
        }
        play.setOnClickListener {
            play.visibility = View.GONE
            videoView.start()
        }
        videoView.setOnPreparedListener {
            videoView.seekTo(1)
        }
        videoView.setOnCompletionListener {
            play.visibility = View.VISIBLE
        }
        videoView.setOnClickListener {
            if(videoView.isPlaying) {
                play.visibility = View.VISIBLE
                videoView.pause()
            }
            else{
                play.visibility = View.GONE
                videoView.resume()
            }
        }


        textShader(clientsCoached)
        textShader(avgRating1)
        textShader(totalExp)
    }
    private fun sendFolowData(trainer_id: String?) {
        val param: MutableMap<String, String> = HashMap()
        param["id"] = ""+trainer_id

        Log.e("trainerFollowParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.trainer_follow,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("TrainerFoloowRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){

                        if(resp.optJSONObject("data").optBoolean("isFollowed") == false){
                            follow.text = resources.getString(R.string.follow)
                            follow_1.text = resources.getString(R.string.follow)
                        }else{
                            follow.text = resources.getString(R.string.unfollow)
                            follow_1.text = resources.getString(R.string.unfollow)
                        }
                    }else{

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

    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }

    override fun onScrollChanged() {
        val scrollBounds = Rect()
        scrollView.getHitRect(scrollBounds)
        if (trainerImage.getLocalVisibleRect(scrollBounds)) {
            headerLayout.visibility = View.GONE
        } else {
            headerLayout.visibility = View.VISIBLE
        }
    }
    private fun getTrainerDetail(
        trainer_id: String?,
        studio_id: String?,
        type: String?,
        longitude: String?,
        latitude: String?
    ) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        // Log.e("trainerListApi",""+ ApiURL.trainer_Detail+trainer_id+"&studio_id="+studio_id+"&type="+type+"&long="+longitude+"&lat="+latitude)
        var api=""
       /* if (sharedPreferences.getString("typeWorkout","").equals("home")){
        }else{
            api= ApiURL.trainer_Detail+intent.getStringExtra("trainer_id")+"&studio_id="+studio_id+"&type="+"gym"+"&long="+longitude+"&lat="+latitude
        }*/
        api= ApiURL.trainer_Detail+intent.getStringExtra("trainer_id")+"&studio_id="+studio_id+"&type="+"home"+"&long="+longitude+"&lat="+latitude
        Log.e("trainerDetailGymApi",""+api)

        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                scrollView.visibility = View.VISIBLE
                //bookSlot.visibility = View.VISIBLE

                Log.e("TrainerDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        val jsonData=jsonObj.optJSONObject("data")
                        Glide.with(this@ProfileTrainerDetailActivity).load(jsonData!!.optString("profile")).fitCenter().into(trainerImage)
                        userName.text = jsonData.optString("name")
                        trainerName_1.text = jsonData.optString("name")
                        tvQuote.text = jsonData.optString("quote")
                        followersCount.text = jsonData.optString("follower")
                        if (!jsonData.optBoolean("isFollowing")){
                            follow.text = resources.getString(R.string.follow)
                            follow_1.text = resources.getString(R.string.follow)
                        }
                        else{
                            follow.text = resources.getString(R.string.unfollow)
                            follow_1.text = resources.getString(R.string.unfollow)
                        }
                        if (jsonData.optString("train_with_me").equals("")){
                            LinearWhyTrainWithMe.visibility=View.GONE
                        }
                        else{
                            LinearWhyTrainWithMe.visibility=View.VISIBLE
                            val uri = Uri.parse(jsonData.optString("train_with_me"))
                            videoView.setVideoURI(uri)
                        }

                        if (jsonData.optString("averageRating").equals("")){
                            avgRating.text = "0"
                            avgRating1.text = "0"
                        }
                        else{
                            avgRating.text = jsonData.optString("averageRating")
                            avgRating1.text = jsonData.optString("averageRating")
                        }

                        place.text = jsonData.optString("location")
                        distance.text = jsonData.optString("distance")
                        aboutTrainerText.text = jsonData.optString("description")

                        totalRatings.text = jsonData.optString("noOfRating")+" ratings"
                        totalRatings.paintFlags = totalRatings.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                        totalExp.text = jsonData.optString("experience")
                        val client1: List<String> = jsonData.optString("clientCoached").split(" ")
                        clientsCoached.text = client1[0]
                        for(i in 0 until jsonData.optJSONArray("tags").length()){
                            val json=jsonData.optJSONArray("tags").optJSONObject(i)
                            val specialitiesModel= SpecialitiesModel()
                            specialitiesModel.name=json.optString("name")
                            specialitiesArrayList.add(specialitiesModel)

                        }
                        specialitiesRecyclerView.adapter = SpecialitiesAdapter(this@ProfileTrainerDetailActivity,specialitiesArrayList)

                        if (jsonData.optJSONArray("certificates").length()>0){
                            linearCertificate.visibility=View.VISIBLE
                            for(i in 0 until jsonData.optJSONArray("certificates").length()){
                                var json=jsonData.optJSONArray("certificates").optJSONObject(i)
                                var certificateModel= CertificateModel()
                                certificateModel.name=json.optString("name")
                                certificateModel.level=json.optString("level")
                                certificateArrayList.add(certificateModel)
                            }
                            certificateRecycler.adapter = CertificateAdapter(applicationContext,certificateArrayList)
                        }
                        else{
                            linearCertificate.visibility=View.GONE

                        }

                        if (jsonData.optJSONArray("galleries").length()>0){
                            linearMedia.visibility=View.VISIBLE
                            for(i in 0 until jsonData.optJSONArray("galleries").length()){
                                var json=jsonData.optJSONArray("galleries").optJSONObject(i)
                                var galleryModel= GalleryModel()
                                galleryModel.media_path=json.optString("media_path")
                                galleryModel.is_video=json.optString("is_video")
                                galleryArrayList.add(galleryModel)
                            }
                            galleryRecyclerView.adapter = GalleryAdapter(this@ProfileTrainerDetailActivity,galleryArrayList)
                        }
                        else{
                            linearMedia.visibility=View.GONE
                        }

                        aboutTrainerText.text = jsonData.optString("description")

                        text = aboutTrainerText.text.toString()

                        aboutTrainerText.post {
                            if (aboutTrainerText.lineCount > maxLines) {
                                setupCollapsedText()
                            }
                        }

                        /*if (intent.getStringExtra("haveSlot")=="no"){
                            bookSlot.background = resources.getDrawable(R.drawable.grey_rectangle_rounded,null)
                            bookSlot.setTextColor(resources.getColor(R.color.white,null))
                        }*/

                        /*bookSlot.setOnClickListener {
                            if (intent.getStringExtra("haveSlot")!="no"){
                                if (sharedPreferences.getString("typeWorkout","").equals("home")){
                                    val intent1 = Intent(this@ProfileTrainerDetailActivity, AddressListForTrainerActivity::class.java)
                                    intent1.putExtra("type",intent.getStringExtra("type"))
                                    intent1.putExtra("trainer_id",intent.getStringExtra("trainer_id"))
                                    intent1.putExtra("studio_id",intent.getStringExtra("studio_id"))
                                    Log.e("type",""+intent.getStringExtra("type"))
                                    Log.e("trainer_id",""+intent.getStringExtra("trainer_id"))
                                    Log.e("studio_id",""+intent.getStringExtra("studio_id"))
                                    startActivity(intent1)
                                }
                                else{
                                    val intent1 = Intent(this@ProfileTrainerDetailActivity, BookSlot::class.java)
                                    intent1.putExtra("type",intent.getStringExtra("type"))
                                    intent1.putExtra("trainer_id",intent.getStringExtra("trainer_id"))
                                    intent1.putExtra("studio_id",intent.getStringExtra("studio_id"))


                                    Log.e("type",""+intent.getStringExtra("type"))
                                    Log.e("trainer_id",""+intent.getStringExtra("trainer_id"))
                                    Log.e("studio_id",""+intent.getStringExtra("studio_id"))
                                    startActivity(intent1)
                                }
                            }
                        }*/

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
}