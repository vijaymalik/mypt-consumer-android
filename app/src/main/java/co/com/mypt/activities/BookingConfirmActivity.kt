package co.com.mypt.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.model.ActivityModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class BookingConfirmActivity : AppCompatActivity() {
    lateinit var recyclerActivity:RecyclerView
    lateinit var im:ImageView
    lateinit var imQr:ImageView
    lateinit var qrWebView:WebView
    lateinit var detailLL:LinearLayout
    lateinit var tvPackage:TextView
    lateinit var tvTrainerDetail:TextView
    lateinit var tvValid:TextView
    lateinit var imTrainer:ImageView
    lateinit var tvTime:TextView
    lateinit var tvStartDate:TextView
    lateinit var tvLocation:TextView
    lateinit var tvBackToHomeLL: LinearLayout
    lateinit var header:LinearLayout
    lateinit var headerMain:LinearLayout
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    var animUpDown: Animation? = null
    lateinit var sharedPreferences:SharedPreferences
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirm)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        recyclerActivity=findViewById(R.id.recyclerActivity)
        detailLL=findViewById(R.id.detailLL)
        header=findViewById(R.id.header)
        headerMain=findViewById(R.id.headerMain)
        im=findViewById(R.id.im)
        tvPackage=findViewById(R.id.tvPackage)
        tvTime=findViewById(R.id.tvTime)
        tvLocation=findViewById(R.id.tvLocation)
        tvTrainerDetail=findViewById(R.id.tvTrainerDetail)
        tvStartDate=findViewById(R.id.tvStartDate)
        tvValid=findViewById(R.id.tvValid)
        imTrainer=findViewById(R.id.imTrainer)
        imQr=findViewById(R.id.imQr)
        tvBackToHomeLL=findViewById(R.id.tvBackToHomeLL)
        qrWebView=findViewById(R.id.qrWebView)
        im.visibility = View.VISIBLE
        mediaPlayer = MediaPlayer.create(this, R.raw.booking_confirm_sound)

        tvBackToHomeLL.setOnClickListener{
            sharedPreferences.edit().remove("typeWorkout").apply()
            var intent=Intent(this,MainActivity::class.java)
            TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
        }
        val scaleDownX = ObjectAnimator.ofFloat(im, "scaleX", 0.65f)
        val scaleDownY = ObjectAnimator.ofFloat(im, "scaleY", 0.65f)
        scaleDownX.setDuration(1500)
        scaleDownY.setDuration(1500)

        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
        headerMain.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_bottom))

        // load the animation
        animUpDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_bottom)
        sendBookingData()
    }
    private fun sendBookingData() {
        val param: MutableMap<String, String> = HashMap()
        if (intent.getStringExtra("selectBookOption").equals("normalBookSlot")){
            param["is_package"] = ""
            param["package_type"] = ""
            param["date"] = ""
            param["end_date"] = ""
            param["sessions"] = ""
            param["price"] = ""
            param["days"] = ""
        }else{
            param["is_package"] = "1"
            param["package_type"] = ""+sharedPreferences.getInt("selectedPackageType",0)
            param["date"] = ""+ intent.getStringExtra("apistart_date")
            param["end_date"] =""+ intent.getStringExtra("apiend_date")
            param["sessions"] =""+ intent.getStringExtra("session_value")
            param["price"] = ""+ intent.getStringExtra("price")
            param["days"] = ""+ intent.getStringExtra("days")
        }
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            param["studio_id"] =""
            param["type"] = ""+sharedPreferences.getString("typeWorkout","")
            param["address_id"] = ""+ intent.getStringExtra("address_id")
        }else{
            param["studio_id"] =""+ intent.getStringExtra("studio_id")
            param["type"] = "gym"
            param["address_id"] = ""
        }
        param["trainer_id"] =""+ intent.getStringExtra("trainer_id")
        param["slot_id"] = ""+ intent.getStringExtra("slot_id")
        param["transaction_id"] = ""+ intent.getStringExtra("transaction_id")
        param["payment_type"] = ""+ intent.getStringExtra("selectedPaymentOption")


        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        Log.e("bookSLotParam", param.toString())

        PostMethod(ApiURL.bookslot,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("BookingConfirmResp",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        animUpDown?.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(p0: Animation?) {
                                lifecycleScope.launch {
                                    delay(500)
                                    header.visibility= View.VISIBLE


                                    delay(1370L)
                                    detailLL.visibility= View.VISIBLE
                                    detailLL.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_top))

                                    mediaPlayer?.start()
                                }
                            }

                            override fun onAnimationRepeat(p0: Animation?) {
                            }

                            override fun onAnimationEnd(p0: Animation?) {

                            }
                        })
                        header.startAnimation(animUpDown)

                        sharedPreferences.edit().remove("typeWorkout").apply()
                        tvPackage.text = resp.optJSONObject("data")!!.optString("package")
                        tvTime.text = resp.optJSONObject("data")!!.optString("timing")
                        tvLocation.text = resp.optJSONObject("data")!!.optString("location")
                        tvStartDate.text = resp.optJSONObject("data")!!.optJSONObject("date").optString("start_date")
                        tvValid.text = resp.optJSONObject("data")!!.optJSONObject("date").optString("valid_till")
                        tvTrainerDetail.text =
                            resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")
                        Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optString("qr")).fitCenter().into(imQr)
                        Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).fitCenter().into(imTrainer)
                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                        {
                            var activityModel=ActivityModel()
                            activityModel.name=resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter(applicationContext, activitiesModelList)
                        recyclerActivity.adapter = activityAdapter

                        qrWebView.getSettings().loadWithOverviewMode = true
                        qrWebView.getSettings().useWideViewPort = true
                        qrWebView.getSettings().builtInZoomControls = true
                        qrWebView.getSettings().displayZoomControls = false
                        qrWebView.loadUrl(resp.optJSONObject("data")!!.optString("qr"))
                    }
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"), Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        sharedPreferences.edit().remove("typeWorkout").apply()
        var intent=Intent(this,MainActivity::class.java)
        TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
        //startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}