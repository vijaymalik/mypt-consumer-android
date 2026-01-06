package co.com.mypt.UpComingClasses

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.BookingConfirmActivity
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.activities.PaymentSelectionActivity
import co.com.mypt.adapter.CertificateAdapter
import co.com.mypt.adapter.ClassGalleryAdapter
import co.com.mypt.adapter.GalleryAdapter
import co.com.mypt.adapter.SpecialitiesAdapter
import co.com.mypt.model.CertificateModel
import co.com.mypt.model.GalleryModel
import co.com.mypt.model.SpecialitiesModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class ClassDescriptionActivity : AppCompatActivity() , ViewTreeObserver.OnScrollChangedListener{
    var total_price=""
    var is_member=""
    var trainer_id=""
    lateinit var trainerImage : ImageView
    lateinit var play : ImageView

    lateinit var back : ImageView
    lateinit var back_1 : ImageView
    lateinit var imSHare : ImageView
    lateinit var imTrainer : ImageView

    lateinit var scrollView : NestedScrollView
    lateinit var headerLayout : LinearLayout
    lateinit var main : RelativeLayout

    lateinit var certificationTv1 : TextView

    lateinit var certificationTv2 : TextView
    lateinit var certificationTv3 : TextView
    lateinit var clientsCoached : TextView
    lateinit var totalExp : TextView
    lateinit var avgRating1 : TextView
    lateinit var bookSlot : TextView
    lateinit var tvFollowers : TextView
    lateinit var trainerName_1 : TextView
    lateinit var tvdate : TextView
    lateinit var userName : TextView
    lateinit var distance : TextView
    lateinit var place : TextView
    lateinit var follow : TextView
    lateinit var tvQuote : TextView
    lateinit var avgRating : TextView
    lateinit var trainer_name : TextView
    lateinit var aboutTrainerText : TextView
    lateinit var tvReserveSlot : TextView


    lateinit var tv_no_of_student : TextView
    lateinit var tvCertificate : TextView
    lateinit var tvMedia : TextView

    lateinit var tvSession : TextView
    lateinit var tvWHyTrain : TextView

    lateinit var videoView: VideoView
    var certificateArrayList = ArrayList<CertificateModel>()

    lateinit var specialitiesRecyclerView : RecyclerView
    lateinit var certificateRecycler : RecyclerView
    lateinit var galleryRecyclerView : RecyclerView

    var specialitiesArrayList = ArrayList<SpecialitiesModel>()
    var galleryArrayList = ArrayList<GalleryModel>()
    var text = "";
    private val maxLines = 2
    val spannableStringBuilder = SpannableStringBuilder()
    private var isExpanded = false
    lateinit var sharedPreferences:SharedPreferences
    var schedule_id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_class_description)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        imSHare = findViewById(R.id.imSHare)
        follow = findViewById(R.id.follow)
        main = findViewById(R.id.main)
        back = findViewById(R.id.back)
        back_1 = findViewById(R.id.back_1)
        trainerName_1 = findViewById(R.id.trainerName_1)
        tvCertificate = findViewById(R.id.tvCertificate)
        userName = findViewById(R.id.userName)
        tvdate = findViewById(R.id.tvdate)
        place = findViewById(R.id.place)
        tvQuote = findViewById(R.id.tvQuote)
        aboutTrainerText = findViewById(R.id.aboutTrainerText)
        tvFollowers = findViewById(R.id.tvFollowers)
        trainer_name = findViewById(R.id.trainer_name)
        tv_no_of_student = findViewById(R.id.tv_no_of_student)
        distance = findViewById(R.id.distance)
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView)
        specialitiesRecyclerView = findViewById(R.id.specialitiesRecyclerView)
        imTrainer = findViewById(R.id.imTrainer)
        tvSession = findViewById(R.id.tvSession)
        avgRating = findViewById(R.id.avgRating)
        play = findViewById(R.id.play)
        videoView = findViewById(R.id.videoView)
        tvWHyTrain = findViewById(R.id.tvWHyTrain)
//        certificationTv1 = findViewById(R.id.certificationTv1)
//        certificationTv2 = findViewById(R.id.certificationTv2)
//        certificationTv3 = findViewById(R.id.certificationTv3)
        clientsCoached = findViewById(R.id.clientsCoached)

        tvMedia = findViewById(R.id.tvMedia)

        tvReserveSlot = findViewById(R.id.tvReserveSlot)

        headerLayout = findViewById(R.id.headerLayout)
        trainerImage = findViewById(R.id.trainerImage)
        scrollView = findViewById(R.id.scrollView)
        certificateRecycler = findViewById(R.id.certificateRecycler)
        schedule_id=""+intent.getStringExtra("schedule_id")


        back.setOnClickListener { finish() }
        imSHare.setOnClickListener {
            shareImageAndText(this, trainerImage)

        }
        back_1.setOnClickListener { finish() }
        scrollView.viewTreeObserver.addOnScrollChangedListener(this)

        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.launcher_video)
        videoView.setVideoURI(uri)
        follow.setOnClickListener {
            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(
                    Constants.token,"").toString() != ""){
                sendFolowData()

            }else{
                val intent = Intent(this, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }
        }

        play.setOnClickListener {
            play.visibility = View.GONE
            videoView.start()
        }

        tvReserveSlot.setOnClickListener {

            if(sharedPreferences.getString(Constants.token,"-1") != "-1" || sharedPreferences.getString(
                    Constants.token,"").toString() != ""){
                if (is_member.equals("false")){
                    var intent1= Intent(this,ClassPaymentScreenActivity::class.java)
                    intent1.putExtra("schedule_id",schedule_id)
                    intent1.putExtra("total_price",total_price)

                    startActivity(intent1)
                }else{
                    var intent1= Intent(this,UpcomingConfirmClassActivity::class.java)
                    intent1.putExtra("schedule_id",schedule_id)
                    intent1.putExtra("transaction_id","")
                    intent1.putExtra("selectedPaymentOption","")
                    startActivity(intent1)
                }

            }else{
                val intent = Intent(this, PhoneNumberScreenActivity::class.java)
                startActivity(intent)
            }


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

        textShader(tvSession)
        textShader(avgRating)

        textShader(clientsCoached)
        // textShader(avgRating1)


        try {
            val appLinkIntent: Intent = intent
            val appLinkAction: String? = appLinkIntent.action
            val appLinkData: Uri? = appLinkIntent.data
            Log.e("appLinkData",""+appLinkData)
            // Split the path segments
            val segments = appLinkData!!.pathSegments
            schedule_id = segments[1]

        }catch (e:Exception){
            e.printStackTrace()
        }
        getClassDetail()

    }

    private fun getClassDetail() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api= ApiURL.classdetail+intent.getDoubleExtra("latitude",0.0)+"&long="+intent.getDoubleExtra("longitude",0.0)+"&schdule_id="+schedule_id

        Log.e("classDetailApi",""+api)

        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("classDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        main.visibility=View.VISIBLE

                        var jsonData=jsonObj.optJSONObject("data")
                        is_member=jsonData.optString("is_member")
                        userName.setText(jsonData.optString("class_name"))
                        trainer_id=jsonData.optString("trainer_id")
                        trainerName_1.setText(jsonData.optString("name"))
                        tvdate.setText(jsonData.optString("time"))
                        distance.setText(jsonData.optString("distance"))
                        place.setText(jsonData.optString("location"))
                        tv_no_of_student.setText("Class of ${jsonData.optString("capacity")} students")
                        tvQuote.setText(jsonData.optString("quote"))
                        tvFollowers.setText(jsonData.optString("followers"))
                        trainer_name.setText(jsonData.optString("name"))
                        total_price=jsonData.optString("price")
                        if (jsonData.optString("is_member").equals("false")){
                            tvReserveSlot.setText("Reserve Slot @ AED "+jsonData.optString("price"))
                        }
                        else{
                            tvReserveSlot.setText("Reserve Slot")
                        }
                        var getSession=jsonData.optString("sessions").split(" ")
                        tvSession.setText(getSession[0]+"+")
                        Glide.with(applicationContext).load(jsonData.optString("profile")).fitCenter().error(R.drawable.dummy_trainer).
                        placeholder(R.drawable.dummy_trainer).into(imTrainer)
                        Glide.with(applicationContext).load(jsonData.optString("class_profile")).fitCenter().error(R.drawable.im_class).
                        placeholder(R.drawable.im_class).into(trainerImage)
                        if (jsonData.optBoolean("isFollow") == false){
                            follow.setText(resources.getString(R.string.follow))
                           // follow_1.setText(resources.getString(R.string.follow))
                        }else{
                            follow.setText(resources.getString(R.string.unfollow))
                          //  follow_1.setText(resources.getString(R.string.unfollow))
                        }
                        if (jsonData.optString("train_with_me").equals("")){
                            tvWHyTrain.visibility=View.GONE
                        }else{
                            tvWHyTrain.visibility=View.VISIBLE
                            val uri = Uri.parse(jsonData.optString("train_with_me"))
                            videoView.setVideoURI(uri)
                        }
                        avgRating.setText(jsonData.optString("averageRating"))

                        place.setText(jsonData.optString("location"))

                      //  totalRatings.setText(jsonData.optString("noOfRating")+" k ratings")

                        var client1: List<String> = jsonData.optString("clientCoached").split(" ")
                        clientsCoached.setText(client1[0])
                        for(i in 0 until jsonData.optJSONArray("tags").length()){
                            var specialitiesModel=SpecialitiesModel()
                            specialitiesModel.name= jsonData.optJSONArray("tags").get(i).toString()
                            specialitiesArrayList.add(specialitiesModel)

                        }
                        specialitiesRecyclerView.adapter = SpecialitiesAdapter(this@ClassDescriptionActivity,specialitiesArrayList)


                        if (jsonData.optJSONArray("certificates").length()>0){
                            tvCertificate.visibility=View.VISIBLE
                            for(i in 0 until jsonData.optJSONArray("certificates").length()){
                                var json=jsonData.optJSONArray("certificates").optJSONObject(i)
                                var certificateModel= CertificateModel()
                                certificateModel.name=json.optString("name")
                                certificateModel.level=json.optString("level")
                                certificateArrayList.add(certificateModel)
                            }
                            certificateRecycler.adapter = CertificateAdapter(applicationContext,certificateArrayList)
                            certificateRecycler.visibility=View.VISIBLE
                        }else{
                            tvCertificate.visibility=View.GONE
                            certificateRecycler.visibility=View.GONE


                        }
                        if (jsonData.optJSONArray("media_gallery").length()>0){

                            for(i in 0 until jsonData.optJSONArray("media_gallery").length()){

                                var galleryModel=GalleryModel()
                                galleryModel.media_path= jsonData.optJSONArray("media_gallery").get(i).toString()
                                galleryArrayList.add(galleryModel)
                            }
                            galleryRecyclerView.adapter = ClassGalleryAdapter(this@ClassDescriptionActivity,galleryArrayList)
                            (galleryRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                            tvMedia.visibility=View.VISIBLE
                            galleryRecyclerView.visibility=View.VISIBLE

                        }else{
                            tvMedia.visibility=View.GONE
                            galleryRecyclerView.visibility=View.GONE

                        }

                        aboutTrainerText.text = jsonData.optString("class_description")

                        text = aboutTrainerText.text.toString()

                        aboutTrainerText.post {
                            if (aboutTrainerText.lineCount > maxLines) {
                                setupCollapsedText()
                            }
                        }

                    }else{
                        main.visibility=View.GONE


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
    private fun sendFolowData() {
        val param: MutableMap<String, String> = HashMap()
        param["id"] = ""+trainer_id

        Log.e("trainerFollowParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.trainer_follow,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("TrainerFoloowRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){

                        if(resp.optJSONObject("data").optBoolean("isFollowed") == false){
                            follow.setText(resources.getString(R.string.follow))
                        }else{
                            follow.setText(resources.getString(R.string.unfollow))
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
                putExtra(Intent.EXTRA_TEXT,userName.text.toString()+"\n\n"+"https://mobileapp.mypt-me.com/class/${schedule_id}")
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
                putExtra(Intent.EXTRA_TEXT,userName.text.toString()+"\n\n"+"https://mobileapp.mypt-me.com/class/${schedule_id}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

    }
}