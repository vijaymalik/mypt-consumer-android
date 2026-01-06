package co.com.mypt.activities


import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withoutTrainer.GymReviewPackageActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.SessionTimeSlotAdapter
import co.com.mypt.model.SelectTImeModel
import com.android.volley.VolleyError
import org.json.JSONObject

class DuringSeesionActivity : AppCompatActivity() {
    var days=""
    var setstart_dates=""
    private lateinit var recyclerViewTime: RecyclerView
    private lateinit var im_night: ImageView
    private lateinit var linearmrng: LinearLayout
    private lateinit var linernight: LinearLayout
    private lateinit var linearRecycler: LinearLayout
    private lateinit var im_mrng: ImageView
    private lateinit var linearpay: LinearLayout
    private lateinit var tvPayment: TextView
    private lateinit var tvcontinue: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvstartday: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvendday: TextView
    private lateinit var tvNodata: TextView
    var isSelected = false
    var selectTimeModelList : ArrayList<SelectTImeModel> = ArrayList()
    lateinit var sharedPreferences:SharedPreferences
    var slotshift="morning"
    lateinit var headerLayout:LinearLayout
    var slot_id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_during_seesion)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        im_night = findViewById(R.id.im_night)
        linearmrng = findViewById(R.id.linearmrng)
        linernight = findViewById(R.id.linernight)
        im_mrng = findViewById(R.id.im_mrng)
        linearpay = findViewById(R.id.linearpay)
        tvcontinue = findViewById(R.id.tvcontinue)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvEndDate = findViewById(R.id.tvEndDate)
        tvstartday = findViewById(R.id.tvstartday)
        tvendday = findViewById(R.id.tvendday)
        headerLayout=findViewById(R.id.headerLayout)
        tvNodata=findViewById(R.id.tvNodata)
        linearRecycler=findViewById(R.id.linearRecycler)

        tvStartDate.text = intent.getStringExtra("setstart_dates")
        tvstartday.text = intent.getStringExtra("setstart_days")
        tvEndDate.text = intent.getStringExtra("setend_dates")
        tvendday.text = intent.getStringExtra("setend_days")

        recyclerViewTime = findViewById(R.id.recyclerViewTime)
        headerLayout.setOnClickListener{
            finish()
        }
        im_night.setOnClickListener{
            slotshift="night"
            linernight.visibility= View.VISIBLE
            linearmrng.visibility= View.GONE
            im_night.visibility= View.GONE
            im_mrng.visibility= View.VISIBLE
            getAllSlot()

        }
        im_mrng.setOnClickListener{
            slotshift="morning"

            linernight.visibility= View.GONE
            linearmrng.visibility= View.VISIBLE
            im_night.visibility= View.VISIBLE
            im_mrng.visibility= View.GONE
            getAllSlot()

        }
        linearpay.setOnClickListener{
            if (sharedPreferences.getString("typeWorkout","").equals("home")){
                val intent = Intent(this, ReviewPackageActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, GymReviewPackageActivity::class.java)
                startActivity(intent)
            }

        }
        tvcontinue.setOnClickListener{
            if(isSelected){
                if (sharedPreferences.getString("typeWorkout","").equals("home")){
                    val intent = Intent(this, ReviewPackageActivity::class.java)
                    intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
                    intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                    intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                    intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
                    intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
                    intent.putExtra("address_id",getIntent().getStringExtra("address_id"))

                    intent.putExtra("setstart_dates",getIntent().getStringExtra("setstart_dates"))
                    intent.putExtra("setstart_days",getIntent().getStringExtra("setstart_days"))
                    intent.putExtra("setend_dates",getIntent().getStringExtra("setend_dates"))
                    intent.putExtra("setend_days",getIntent().getStringExtra("setend_days"))
                    intent.putExtra("days",days)
                    intent.putExtra("slot_id",slot_id)
                    startActivity(intent)
                }else{
                    val intent = Intent(this, GymReviewPackageActivity::class.java)
                    intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
                    intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                    intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                    intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
                    intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
                    intent.putExtra("address_id",getIntent().getStringExtra("address_id"))

                    intent.putExtra("setstart_dates",getIntent().getStringExtra("setstart_dates"))
                    intent.putExtra("setstart_days",getIntent().getStringExtra("setstart_days"))
                    intent.putExtra("setend_dates",getIntent().getStringExtra("setend_dates"))
                    intent.putExtra("setend_days",getIntent().getStringExtra("setend_days"))
                    intent.putExtra("days",days)
                    intent.putExtra("slot_id",slot_id)
                    startActivity(intent)
                }
            }
        }

        getAllSlot()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            registerReceiver(slot, IntentFilter("sessionselect_time"), RECEIVER_EXPORTED)
            registerReceiver(closeClass, IntentFilter("closeClass"), RECEIVER_EXPORTED)
            registerReceiver(closegymClass, IntentFilter("closegymClass"), RECEIVER_EXPORTED)
        }
        else{
            registerReceiver(slot, IntentFilter("sessionselect_time"))
            registerReceiver(closeClass, IntentFilter("closeClass"))
            registerReceiver(closegymClass, IntentFilter("closegymClass"))
        }
    }

    val slot = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.getStringExtra("selectposition").equals("slot")) {
                slot_id=""+intent.getStringExtra("slot_id")
                tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                tvcontinue.setTypeface(null, Typeface.BOLD)
                isSelected = true
            }
            else{
                tvcontinue.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvcontinue.setTextColor(resources.getColor(R.color.white))
                tvcontinue.setTypeface(null, Typeface.NORMAL)
                isSelected = false
            }

        }
    }

    val closeClass = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            stopBroadCast()
            sendBroadcast(Intent("closeClass"))
            finish()
        }
    }
    val closegymClass = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            stopGymBroadCast()
            sendBroadcast(Intent("closegymClass"))
            finish()
        }
    }

    private fun stopGymBroadCast() {
        unregisterReceiver(closegymClass)
    }
    private fun stopBroadCast() {
        unregisterReceiver(closeClass)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(slot)
    }

    fun getAllSlot()  {
        val param: MutableMap<String, String> = HashMap()
        param["package_type"] = ""+sharedPreferences.getInt("selectedPackageType",0)
        param["sessions"] = ""+ intent.getStringExtra("session_value")
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            param["type"] = ""+sharedPreferences.getString("typeWorkout","")
        }else{
            param["type"] = "gym"
        }
        param["trainer_id"] =""+ intent.getStringExtra("trainer_id")
        param["studio_id"] = ""+ intent.getStringExtra("studio_id")
        param["date"] =""+ intent.getStringExtra("apistart_date")
        param["end_date"] =""+ intent.getStringExtra("apiend_date")
        param["address_id"] =""+ intent.getStringExtra("address_id")
        param["timing"] = slotshift

        android.util.Log.e("getPackageSlotParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.package_setdate,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                selectTimeModelList.clear()
                try {
                    android.util.Log.e("GetPackageSlotRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonArraySlots=resp.optJSONObject("data").optJSONArray("slots")
                        if (jsonArraySlots.length()>0) {
                            for(i in 0 until jsonArraySlots.length()){
                                val jsonObject1 = jsonArraySlots.optJSONObject(i)
                                var selectTImeModel=SelectTImeModel()
                                selectTImeModel.timeslot=jsonObject1.optString("time")
                                selectTImeModel.isBooked=jsonObject1.optString("isBooked")
                                selectTImeModel.id=jsonObject1.optString("id")
                                selectTimeModelList.add(selectTImeModel)
                            }
                            var sessionTimeAdapter= SessionTimeSlotAdapter(this@DuringSeesionActivity,selectTimeModelList)
                            days=resp.optJSONObject("data").optString("days")
                            recyclerViewTime.adapter=sessionTimeAdapter
                            recyclerViewTime.visibility=View.VISIBLE
                            tvNodata.visibility=View.GONE
                        }
                        else{
                            tvNodata.visibility=View.VISIBLE
                            recyclerViewTime.visibility=View.GONE

                        }
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

}