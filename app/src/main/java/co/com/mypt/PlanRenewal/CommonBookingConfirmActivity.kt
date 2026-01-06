package co.com.mypt.PlanRenewal

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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
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
import co.com.mypt.activities.MainActivity
import co.com.mypt.model.ActivityModel
import com.android.volley.VolleyError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class CommonBookingConfirmActivity : AppCompatActivity() {
    lateinit var recyclerActivity:RecyclerView
    lateinit var im:ImageView
    lateinit var detailLL:LinearLayout

    lateinit var tvPackage:TextView
    lateinit var tvTrainerDetail:TextView
    lateinit var tvValid:TextView
    lateinit var imTrainer:ImageView
    lateinit var im_background:ImageView

    lateinit var tvStartDate:TextView
    lateinit var tvBackToHome:TextView
    lateinit var title:TextView
    lateinit var header:LinearLayout
    lateinit var headerMain:LinearLayout
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    var animUpDown: Animation? = null
    lateinit var sharedPreferences:SharedPreferences
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()
        setContentView(R.layout.activity_common_booking_confirm)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        recyclerActivity=findViewById(R.id.recyclerActivity)
        detailLL=findViewById(R.id.detailLL)
        title=findViewById(R.id.title)
        header=findViewById(R.id.header)
        headerMain=findViewById(R.id.headerMain)
        im=findViewById(R.id.im)
        tvPackage=findViewById(R.id.tvPackage)

        tvTrainerDetail=findViewById(R.id.tvTrainerDetail)
        tvStartDate=findViewById(R.id.tvStartDate)
        tvValid=findViewById(R.id.tvValid)
        imTrainer=findViewById(R.id.imTrainer)

        tvBackToHome=findViewById(R.id.tvBackToHome)

        im.visibility = View.VISIBLE
        mediaPlayer = MediaPlayer.create(this, R.raw.booking_confirm_sound)

        if(intent.getStringExtra("subscriptionType")!!.lowercase()=="topup"){
            title.text = "Booking Confirmed"
        }else if(intent.getStringExtra("subscriptionType")!!.lowercase()=="renew"){
            title.text = "Package Renewed"
        }else{
            title.text = "Upgrade Successful"
        }

        tvBackToHome.setOnClickListener{
            val intent=Intent(this,MainActivity::class.java)
            TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
        }
        val scaleDownX = ObjectAnimator.ofFloat(im, "scaleX", 0.65f)
        val scaleDownY = ObjectAnimator.ofFloat(im, "scaleY", 0.65f)
        scaleDownX.duration = 1500
        scaleDownY.duration = 1500

        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
        headerMain.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_bottom))

        // load the animation
        animUpDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_bottom)
        sendBookingData()

        onBackPressedDispatcher.addCallback(this) {
            val intent=Intent(this@CommonBookingConfirmActivity,MainActivity::class.java)
            TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
        }
    }
    private fun sendBookingData() {
        val param: MutableMap<String, String> = HashMap()
        param["id"] = intent.getStringExtra("id").toString()
        param["sessions"] = intent.getStringExtra("sessions").toString()
        param["days"] = intent.getStringExtra("days").toString()
        param["price"] = intent.getStringExtra("price").toString()
        param["type"] = intent.getStringExtra("subscriptionType").toString().lowercase()
        param["transaction_id"] = ""+ intent.getStringExtra("transaction_id")
        param["payment_type"] = ""+ intent.getStringExtra("selectedPaymentOption")


        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        Log.e("upgradeParam", param.toString())

        PostMethod(ApiURL.upgradeTopUpMakePayment,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("upgradeResp",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        animUpDown?.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(p0: Animation?) {
                                lifecycleScope.launch {
                                    delay(500)
                                    header.visibility= View.VISIBLE
                                    detailLL.visibility= View.VISIBLE
                                    detailLL.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_top))


                                    delay(1370L)
                                    mediaPlayer?.start()
                                }
                            }

                            override fun onAnimationRepeat(p0: Animation?) {
                            }

                            override fun onAnimationEnd(p0: Animation?) {

                            }
                        })
                        header.startAnimation(animUpDown)

                        tvPackage.text = resp.optJSONObject("data")!!.optString("package")

                        tvStartDate.text = resp.optJSONObject("data").optString("start_date")
                        tvValid.text = resp.optJSONObject("data").optString("new_end_date")
                        /*tvTrainerDetail.text =
                            resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")
                        Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).fitCenter().into(imTrainer)
                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                        {
                            var activityModel=ActivityModel()
                            activityModel.name=resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter(applicationContext, activitiesModelList)
                        recyclerActivity.adapter = activityAdapter*/


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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}