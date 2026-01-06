package co.com.mypt.WorkoutLibrary

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.StreakAdapter
import co.com.mypt.model.StreakModel
import com.android.volley.VolleyError
import org.json.JSONObject

class StreakActivity : AppCompatActivity() {

    lateinit var topView : LinearLayout
    lateinit var streakCard : CardView
    lateinit var tv_complete : TextView
    lateinit var userName : TextView
    lateinit var tvmsg : TextView
    lateinit var totalStreakDays : TextView
    lateinit var streakRecyclerView : RecyclerView
    var streakArrayList = ArrayList<StreakModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_streak)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        tv_complete=findViewById(R.id.tv_complete)
        topView=findViewById(R.id.topView)
        streakCard=findViewById(R.id.streakCard)
        tvmsg=findViewById(R.id.tvmsg)
        totalStreakDays=findViewById(R.id.totalStreakDays)
        streakRecyclerView=findViewById(R.id.streakRecyclerView)


        streakCard.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.slide_from_bottom_slow
            )
        )
        topView.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.slide_from_bottom_slow
            )
        )

        tv_complete.setOnClickListener {
            val intent = Intent(this,BadgeActivity::class.java)
            intent.putParcelableArrayListExtra("streak_list", streakArrayList)
            Log.e("streakArraylist",""+streakArrayList[0].status)
            startActivity(intent)
        }
    }
    private fun getStreakData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@StreakActivity,"")
        progressDialog.show()
        Log.e("StreakDataApi",ApiURL.getuserstreak)
        GetMethod(ApiURL.getuserstreak,this@StreakActivity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                streakArrayList.clear()
                Log.e("streakResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        totalStreakDays.text =
                            resp.optJSONObject("data").optString("week_number").replace("null","0")
                        tvmsg.text = resp.optJSONObject("data").optString("message")


                        var jsonArray=resp.optJSONObject("data").optJSONArray("week_days")
                        for(i in 0 until jsonArray.length()){
                            var jsonObject=jsonArray.optJSONObject(i)
                            val streakModel= StreakModel()
                            streakModel.day=jsonObject.optString("day")
                            streakModel.date=jsonObject.optString("date")
                            streakModel.status=jsonObject.optString("status")
                            streakModel.completed=jsonObject.optString("completed")
                            streakArrayList.add(streakModel)
                        }
                        val streakAdapter= StreakAdapter(streakArrayList,applicationContext)
                        streakRecyclerView.adapter=streakAdapter





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

    override fun onResume() {
        super.onResume()
        getStreakData()
    }
}