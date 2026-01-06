package co.com.mypt.UpComingClasses

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.MainActivity
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.model.ActivityModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class UpcomingConfirmClassActivity : AppCompatActivity() {
    lateinit var recyclerActivity: RecyclerView
    lateinit var im: ImageView
    lateinit var imQr: ImageView
    lateinit var detailLL: LinearLayout
    lateinit var tvPackage: TextView
    lateinit var tvTrainerDetail: TextView
    lateinit var tvValid: TextView
    lateinit var imTrainer: ImageView
    lateinit var tvTime: TextView
    lateinit var tvStartDate: TextView
    lateinit var tvLocation: TextView
    lateinit var tvBackToHome: TextView
    lateinit var tvDate: TextView
    lateinit var header: LinearLayout
    lateinit var tvBackToHomeLL: LinearLayout
    lateinit var headerMain: LinearLayout
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    var animUpDown: Animation? = null
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upcoming_confirm_class)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)
        recyclerActivity=findViewById(R.id.recyclerActivity)
        detailLL=findViewById(R.id.detailLL)
        header=findViewById(R.id.header)
        headerMain=findViewById(R.id.headerMain)
        im=findViewById(R.id.im)
        tvBackToHomeLL=findViewById(R.id.tvBackToHomeLL)
        tvTime=findViewById(R.id.tvTime)
        tvLocation=findViewById(R.id.tvLocation)
        tvTrainerDetail=findViewById(R.id.tvTrainerDetail)
        imTrainer=findViewById(R.id.imTrainer)
        imQr=findViewById(R.id.imQr)
        tvBackToHome=findViewById(R.id.tvBackToHome)
        tvDate=findViewById(R.id.tvDate)
        im.visibility = View.VISIBLE


        tvBackToHomeLL
            .setOnClickListener{
            //   sharedPreferences.edit().remove("typewithout").apply()
            var intent= Intent(this, MainActivity::class.java)
            TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
            //startActivity(intent)
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

        param["schedule_id"] = ""+getIntent().getStringExtra("schedule_id")
        param["transaction_id"] = ""+getIntent().getStringExtra("transaction_id")
        param["payment_type"] = ""+getIntent().getStringExtra("selectedPaymentOption")

        Log.e("book_classParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        PostMethod(ApiURL.bookclass,param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("bookclassRes",data.toString())
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
                                }
                            }

                            override fun onAnimationRepeat(p0: Animation?) {
                            }

                            override fun onAnimationEnd(p0: Animation?) {

                            }
                        })

                        header.startAnimation(animUpDown)
                        tvTime.setText(resp.optJSONObject("data")!!.optString("timing"))
                        tvLocation.setText(resp.optJSONObject("data")!!.optString("location"))
                        tvDate.setText(resp.optJSONObject("data")!!.optString("date"))

                        tvTrainerDetail.setText(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name"))
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

        var intent=Intent(this,MainActivity::class.java)
        TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
        //startActivity(intent)
    }
}