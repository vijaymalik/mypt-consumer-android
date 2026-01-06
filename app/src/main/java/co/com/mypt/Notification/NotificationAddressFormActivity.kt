package co.com.mypt.Notification

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.CityModel
import com.android.volley.VolleyError
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class NotificationAddressFormActivity : AppCompatActivity() {
    var cityList = ArrayList<CityModel>()
    var country_name=""
    var country_id=""
    var city_id=""
    var type=""

    lateinit var stateLinearLayout: LinearLayout
    lateinit var linearSave: LinearLayout
    lateinit var tvCity:TextInputEditText
    lateinit var tvStreetName:TextInputEditText
    lateinit var tvCountry:TextInputEditText
    lateinit var tvBuildingName:TextInputEditText
    lateinit var tvLandmark:TextInputEditText
    lateinit var textphone:TextInputEditText
    lateinit var textName:TextInputEditText
    lateinit var stateTextInputLayout: TextInputLayout
    lateinit var autoCompleteCity:AutoCompleteTextView
    lateinit var checkHome: CheckBox
    lateinit var checkOffice: CheckBox
    lateinit var checkOthers: CheckBox

    lateinit var im_editname: ImageView
    lateinit var back_1: ImageView
    var cityArrayList=ArrayList<String>()
    var click=""
    var longitude: Double? = null
    var latitude: Double? = null
    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notification_address_form)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)

        back_1=findViewById(R.id.back_1)
        autoCompleteCity=findViewById(R.id.autoCompleteCity)
        stateLinearLayout=findViewById(R.id.stateLinearLayout)
        tvCity=findViewById<TextInputEditText>(R.id.tvCity)
        tvCountry=findViewById<TextInputEditText>(R.id.tvCountry)
        textphone=findViewById<TextInputEditText>(R.id.textphone)
        tvBuildingName=findViewById<TextInputEditText>(R.id.tvBuildingName)
        tvStreetName=findViewById<TextInputEditText>(R.id.tvStreetName)
        tvLandmark=findViewById<TextInputEditText>(R.id.tvLandmark)
        stateTextInputLayout=findViewById<TextInputLayout>(R.id.stateTextInputLayout)
        linearSave=findViewById<LinearLayout>(R.id.linearSave)
        checkHome=findViewById<CheckBox>(R.id.checkHome)
        checkOffice=findViewById<CheckBox>(R.id.checkOffice)
        checkOthers=findViewById<CheckBox>(R.id.checkOthers)
        im_editname=findViewById(R.id.im_editname)
        textName=findViewById(R.id.textName)
        tvBuildingName.setText(intent.getStringExtra("location"))
        latitude= intent.getDoubleExtra("latitude",0.0)
        longitude= intent.getDoubleExtra("longitude",0.0)
        textphone.setText(sharedPreferences.getString(Constants.phone,""))
        textName.setText(sharedPreferences.getString(Constants.name,""))
        getCityListData()

        back_1.setOnClickListener {
            finish()
        }
        checkHome.setOnClickListener{
            type="home"
            checkHome.isChecked=true
            checkOffice.isChecked=false
            checkOthers.isChecked=false
        }
        checkOffice.setOnClickListener{
            type="office"
            checkOffice.isChecked=true
            checkHome.isChecked=false
            checkOthers.isChecked=false
        }
        checkOthers.setOnClickListener{
            type="others"
            checkOthers.isChecked=true
            checkHome.isChecked=false
            checkOffice.isChecked=false
        }
        im_editname.setOnClickListener{
            click="editfield"
            textName.isEnabled=true
            textName.requestFocus()
            textName.setSelection(textName.text.toString().length)
            //changebuttonColor()
            val imm =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textName, InputMethodManager.SHOW_IMPLICIT)
        }
        tvCity.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        stateTextInputLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        stateLinearLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })

        autoCompleteCity.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val selection = parent.getItemAtPosition(position) as String
            var pos = -1
            for (i in cityList.indices) {
                if (selection.contains(cityList[i].name)) {
                    pos = i
                    break
                }
            }

            city_id = cityList[pos].id
            tvCity.setText(cityList[pos].name)


        })
        linearSave.setOnClickListener{
            if (textName.text.toString().trim()==""){
                textName.error = "Please enter name"
                return@setOnClickListener
            }else if (textphone.getText().toString().trim { it <= ' ' }.matches("".toRegex())) {
                textphone.setError(resources.getString(R.string.EnterContactNumber))
            }else if (textphone.getText().toString().length<9) {
                textphone.setError("The contact number field must not be greater than 10 characters.")
            }else if (tvBuildingName.getText().toString().trim { it <= ' ' }.matches("".toRegex())) {
                tvBuildingName.setError(resources.getString(R.string.enter_building_name))
            } else if (tvStreetName.getText().toString().trim { it <= ' ' }.matches("".toRegex())){
                tvStreetName.setError(resources.getString(R.string.enter_street_name))

            }else if (city_id.equals("")){
                Toast.makeText(applicationContext,R.string.select_city,Toast.LENGTH_LONG).show()
            }
            else if (type.equals("")){
                Toast.makeText(applicationContext,R.string.select_addressType,Toast.LENGTH_LONG).show()
            }
            else {
                sendAddaddressData(tvBuildingName,tvStreetName,tvLandmark,type,textphone,city_id)
            }
        }
    }
    private fun getCityListData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()


        GetMethod(ApiURL.get_cities,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                cityList.clear()
                Log.e("getCityListResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONObject("data").optJSONArray("cities")
                        country_id=jsonObj.optJSONObject("data").optString("id")
                        country_name=jsonObj.optJSONObject("data").optString("name")

                        for(i in 0 until jsonArray.length()){
                            var jsonObject1=jsonArray.optJSONObject(i)
                            var cityModel= CityModel()

                            cityModel.id=jsonObject1.optString("id")
                            cityModel.name=jsonObject1.optString("name")
                            cityList.add(cityModel)
                            cityArrayList.add(jsonObject1.optString("name"))

                        }
                        val adapter = ArrayAdapter(applicationContext,R.layout.custom_dropdown_item, cityArrayList)
                        autoCompleteCity.setAdapter(adapter)

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
    private fun sendAddaddressData(
        tvBuildingName: TextInputEditText,
        tvStreetName: TextInputEditText,
        tvLandmark: TextInputEditText,
        type: String,
        tvmobile: TextInputEditText,
        city_id: String,

        ) {
        val param: MutableMap<String, String> = HashMap()
        param["building_name"] = tvBuildingName.text.toString()
        param["street"] = tvStreetName.text.toString()
        param["city_id"] = city_id
        param["lat"] = ""+latitude
        param["long"] = ""+longitude
        param["country_id"] = country_id
        param["landmark"] = tvLandmark.text.toString()
        param["type"] = type
        param["mobile_no"] = tvmobile.text.toString()
        param["name"] = textName.text.toString()
        if (intent.getStringExtra("link").equals("edit")){
            param["id"] =""+intent.getStringExtra("address_id")

        }else{
            param["id"] = ""

        }
        Log.e("addAddressParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.addaddress,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("addressRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){

                        finish()

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