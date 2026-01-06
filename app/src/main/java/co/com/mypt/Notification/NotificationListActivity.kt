package co.com.mypt.Notification

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.NotificationListAdapter
import co.com.mypt.model.NotificationListModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject

class NotificationListActivity : AppCompatActivity() {
    lateinit var notificationListAdapter: NotificationListAdapter
    var notificationArrayList = ArrayList<NotificationListModel>()
    lateinit var linear: LinearLayout
    lateinit var tvnodata: TextView
    lateinit var recyclerNotification: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        recyclerNotification=findViewById(R.id.recyclerNotification)
        linear=findViewById(R.id.linear)
        tvnodata=findViewById(R.id.tvnodata)

        linear.setOnClickListener{
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        getData()
    }
    private fun getData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@NotificationListActivity,"")
        progressDialog.show()
        var api=""
        api= ApiURL.getbooking+"3"+"&date="+""+"&session_type="+""+"&location="+""

        Log.e("getnotificationlistApi",""+api)

        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                notificationArrayList.clear()
                Log.e("getNotificationResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                var jsonObject=jsonArray.optJSONObject(i)
                                var notificationModel= NotificationListModel()
                                notificationModel.id=jsonObject.optString("id")
                                notificationModel.type=jsonObject.optString("type")
                                notificationModel.timing=jsonObject.optString("timing")
                                notificationModel.distance=jsonObject.optString("distance")
                                notificationModel.selected_slot=jsonObject.optString("selected_slot")
                                notificationModel.session_type=jsonObject.optString("session_type")
                                notificationModel.duration=jsonObject.optString("duration")
                                notificationModel.trainer=jsonObject.optString("trainer")
                                notificationModel.trainer_image=jsonObject.optString("trainer_image")
                                notificationModel.location=jsonObject.optString("location")
                                notificationModel.is_reschedule=jsonObject.optString("is_reschedule")
                                notificationModel.msg=jsonObject.optString("msg")
                                notificationModel.is_Trainer=jsonObject.optString("isTrainer")
                                notificationModel.scheduleMsg=jsonObject.optString("scheduleMsg")
                                notificationModel.averageRating=jsonObject.optString("averageRating")
                                notificationModel.workout_focus=jsonObject.optJSONArray("workout_focus")
                                notificationArrayList.add(notificationModel)
                            }
                            notificationListAdapter = NotificationListAdapter(this@NotificationListActivity, notificationArrayList)
                            recyclerNotification.adapter = notificationListAdapter
                            recyclerNotification.visibility=View.VISIBLE
                            tvnodata.visibility=View.GONE

                        }else{
                            tvnodata.visibility=View.VISIBLE
                            recyclerNotification.visibility=View.GONE

                        }


                    }else{


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

    fun sendCancelData(id: String, cancelBottomSheetDialog: BottomSheetDialog) {
        val param: MutableMap<String, String> = HashMap()
        param["id"] = id
        param["reason"] = "Yes, Cancel"

        Log.e("cancelBookingParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.cancelsession, param, applicationContext).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("cancelBooingRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        cancelBottomSheetDialog.dismiss()
                        getData()
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