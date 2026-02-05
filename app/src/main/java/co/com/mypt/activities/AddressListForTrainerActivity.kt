package co.com.mypt.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.AddressListAdapter
import co.com.mypt.model.AddressModel
import com.android.volley.VolleyError
import org.json.JSONObject

class AddressListForTrainerActivity : AppCompatActivity() {
    var address_id=""
    var checkType=""
    lateinit var recyclerAddress:RecyclerView
    var addressList = ArrayList<AddressModel>()
    lateinit var linearNoAddress:LinearLayout
    lateinit var linearSelect:LinearLayout
    lateinit var nested:NestedScrollView

    lateinit var addAddress2:TextView
    lateinit var bookSlot:TextView
    lateinit var im:ImageView
    lateinit var addAddress1:TextView
    lateinit var headerLayout:LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list_for_trainer)
        recyclerAddress=findViewById(R.id.recyclerAddress)
        linearNoAddress=findViewById(R.id.linearNoAddress)
        addAddress2=findViewById(R.id.addAddress2)
        addAddress1=findViewById(R.id.addAddress1)
        nested=findViewById(R.id.nested)
        bookSlot=findViewById(R.id.bookSlot)
        im=findViewById(R.id.im)
        headerLayout=findViewById(R.id.headerLayout)
        linearSelect=findViewById(R.id.linearSelect)

        addAddress2.setOnClickListener {
            var intent= Intent(applicationContext, SelectCurrentLocationActivity::class.java)
            intent.putExtra("link","add")
            startActivity(intent)
            //addressBottomSheetDialog.show()
        }
        headerLayout.setOnClickListener {
         finish()
        }
        addAddress1.setOnClickListener {
            var intent= Intent(applicationContext, SelectCurrentLocationActivity::class.java)
            intent.putExtra("link","add")
            startActivity(intent)
            //addressBottomSheetDialog.show()
        }

        linearSelect.setOnClickListener {
            Log.e("address_idclick",""+address_id)

            if (address_id.equals("")){
              return@setOnClickListener
            }else{

                var intent=Intent(applicationContext,BookSlot::class.java)
                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                intent.putExtra("type",getIntent().getStringExtra("type"))
                intent.putExtra("address_id",address_id)
                Log.e("address_id",""+address_id)
                Log.e("studio_id",""+getIntent().getStringExtra("studio_id"))
                startActivity(intent)
                address_id=""
                linearSelect.setBackgroundResource(R.drawable.add_address_drawable)
                bookSlot.setTextColor(getColor(R.color.subheadingcolor))
                im.setColorFilter(ContextCompat.getColor(applicationContext, R.color.subheadingcolor), PorterDuff.Mode.SRC_IN)
            }
        }



    }

    override fun onResume() {
        super.onResume()
        getAddressData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(selectAddress, IntentFilter("selectAddress"),
                RECEIVER_EXPORTED
            )
        }else{
            registerReceiver(selectAddress, IntentFilter("selectAddress"))

        }

    }
    private fun getAddressData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        GetMethod(ApiURL.getaddress,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                addressList.clear()
                Log.e("getAddressResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        val jsonArray=jsonObj.optJSONArray("data")

                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                val jsonObject1=jsonArray.optJSONObject(i)
                                val addressModel= AddressModel()
                                addressModel.building_name=jsonObject1.optString("building_name")
                                addressModel.street=jsonObject1.optString("street")
                                addressModel.landmark=jsonObject1.optString("landmark")
                                addressModel.type=jsonObject1.optString("type")
                                addressModel.city_id=jsonObject1.optString("city_id")
                                addressModel.country_id=jsonObject1.optString("country_id")
                                addressModel.mobile_no=jsonObject1.optString("mobile_no")
                                addressModel.country_name=jsonObject1.optString("country_name")
                                addressModel.city_name=jsonObject1.optString("city_name")
                                addressModel.lat=jsonObject1.optString("lat")
                                addressModel.long=jsonObject1.optString("long")
                                addressModel.id=jsonObject1.optString("id")

                                addressList.add(addressModel)

                            }
                            address_id=jsonArray.optJSONObject(0).optString("id")
                            linearSelect.setBackgroundResource(R.drawable.white_rectangle)
                            bookSlot.setTextColor(getColor(R.color.buttontextcolor))
                            im.setColorFilter(ContextCompat.getColor(applicationContext, R.color.buttontextcolor), PorterDuff.Mode.SRC_IN)

                            recyclerAddress.adapter = AddressListAdapter(addressList,applicationContext){}
                            linearNoAddress.visibility=View.GONE
                            nested.visibility=View.VISIBLE

                        }else{
                            linearNoAddress.visibility=View.VISIBLE
                            nested.visibility=View.GONE
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

    val selectAddress = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            address_id=""+intent!!.getStringExtra("address_id")
            checkType=""+intent!!.getStringExtra("typeselect")
            Log.e("address_idBroadcast",address_id)
            if (address_id.equals("")){
                linearSelect.setBackgroundResource(R.drawable.add_address_drawable)
                bookSlot.setTextColor(getColor(R.color.subheadingcolor))
                im.setColorFilter(ContextCompat.getColor(applicationContext, R.color.subheadingcolor), PorterDuff.Mode.SRC_IN)

            }else{
                linearSelect.setBackgroundResource(R.drawable.white_rectangle)
                bookSlot.setTextColor(getColor(R.color.buttontextcolor))
                im.setColorFilter(ContextCompat.getColor(applicationContext, R.color.buttontextcolor), PorterDuff.Mode.SRC_IN)

            }

        }
    }

}