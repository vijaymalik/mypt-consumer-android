package co.com.mypt.Profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.CityModel
import co.com.mypt.utils.ImageFilePath
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.cleaningservice.handler.MultipartUtility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Locale

class PersonInfoActivity : AppCompatActivity() {
    var coverimage=""
    var profileimage=""
    lateinit var im_editname:ImageView
    lateinit var imedit_email:ImageView
    lateinit var back_1:ImageView
    lateinit var imeditLocation:ImageView
    lateinit var imedit_gender:ImageView
    lateinit var imeditAddress:ImageView
    lateinit var imeditPostal:ImageView
    lateinit var CUserImage:ImageView
    lateinit var im_profile_edit:ImageView
    lateinit var im_cover_edit:ImageView
    lateinit var coverImage:ImageView
    lateinit var nested: NestedScrollView
    lateinit var tvAddressINfo:TextView
    lateinit var tvCard:TextView
    lateinit var locationLayout:LinearLayout

    lateinit var textName:TextInputEditText
    lateinit var textEmail:TextInputEditText
    lateinit var textphone:TextInputEditText
    lateinit var textdate:TextInputEditText
    lateinit var textGender:MaterialAutoCompleteTextView
    lateinit var textAddress:TextInputEditText
    lateinit var textPostal:TextInputEditText
    lateinit var textCountry:TextInputEditText
    lateinit var genderInputLayout:TextInputLayout
    lateinit var stateTextInputLayout:TextInputLayout
    lateinit var locationInputLayout:TextInputLayout
    lateinit var tvCity:TextInputEditText
    lateinit var autoCompleteCity:AutoCompleteTextView
    lateinit var textCity:TextInputEditText
    lateinit var stateLinearLayout:LinearLayout
    lateinit var cardSaveCHanges:CardView
    var REQUEST_ID_MULTIPLE_PERMISSIONS=123
    private val GALLERY = 1
    private val CAMERA = 2
    private val IMAGE_DIRECTORY = "/MyPT"
    var Photofile : File? =null
    var Coverfile : File? =null
    var cityList = ArrayList<CityModel>()
    var country_name=""
    var country_id=""
    var city_id=""
    var cityArrayList=ArrayList<String>()
    var type=""
    var click=""
    var address=""
    var longitude= 0.0
    var latitude= 0.0
    lateinit var sharedPreferences: SharedPreferences
    lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var selectPhotoBottomSheetDialog:BottomSheetDialog
    lateinit var tvRemove:TextView
    val genderList = listOf("Male", "Female", "Other")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        locationInputLayout=findViewById(R.id.locationInputLayout)
        locationLayout=findViewById(R.id.locationLayout)
        genderInputLayout=findViewById(R.id.genderInputLayout)
        tvCard=findViewById(R.id.tvCard)

        im_editname=findViewById(R.id.im_editname)
        cardSaveCHanges=findViewById(R.id.cardSaveCHanges)
        autoCompleteCity=findViewById(R.id.autoCompleteCity)
        tvCity=findViewById(R.id.tvCity)
        stateTextInputLayout=findViewById(R.id.stateTextInputLayout)
        stateLinearLayout=findViewById(R.id.stateLinearLayout)
        back_1=findViewById(R.id.back_1)
        nested=findViewById(R.id.nested)
        textName=findViewById(R.id.textName)
        im_cover_edit=findViewById(R.id.im_cover_edit)
        coverImage=findViewById(R.id.coverImage)
        textAddress=findViewById(R.id.textAddress)
        textPostal=findViewById(R.id.textPostal)
        textCountry=findViewById(R.id.textCountry)
        tvAddressINfo=findViewById(R.id.tvAddressINfo)
        textEmail=findViewById(R.id.textEmail)
        textphone=findViewById(R.id.textphone)
        textdate=findViewById(R.id.textdate)
        textGender=findViewById(R.id.textGender)
        imedit_email=findViewById(R.id.imedit_email)
        imedit_gender=findViewById(R.id.imedit_gender)
        imeditLocation=findViewById(R.id.imeditLocation)
        imeditAddress=findViewById(R.id.imeditAddress)
        imeditPostal=findViewById(R.id.imeditPostal)
        CUserImage=findViewById(R.id.CUserImage)
        im_profile_edit=findViewById(R.id.im_profile_edit)
        im_editname.setOnClickListener{
            click="editfield"
            textName.isEnabled=true
            textName.requestFocus()
            textName.setSelection(textName.text.toString().length)
            changebuttonColor()
            val imm =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textName, InputMethodManager.SHOW_IMPLICIT)
        }
        imedit_email.setOnClickListener{
            click="editfield"
            textEmail.isEnabled=true
            textEmail.requestFocus()
            textEmail.setSelection(textEmail.text.toString().length)
            changebuttonColor()
            val imm =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textEmail, InputMethodManager.SHOW_IMPLICIT)
        }
        imedit_gender.setOnClickListener{
            click="editfield"
            textGender.isEnabled=true
            textGender.showDropDown()
            changebuttonColor()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
        }
        imeditAddress.setOnClickListener{
            click="editfield"
            textAddress.isEnabled=true
            textAddress.requestFocus()
            textAddress.setSelection(textAddress.text.toString().length)
            changebuttonColor()
            val imm =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textAddress, InputMethodManager.SHOW_IMPLICIT)
        }
        imeditLocation.setOnClickListener{
            var intent=Intent(this,ChangeLocationActivity::class.java)
            startActivity(intent)
        }
        locationLayout.setOnClickListener{
            val intent=Intent(this,ChangeLocationActivity::class.java)
            startActivity(intent)
        }

        locationInputLayout.setOnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            val intent=Intent(this,ChangeLocationActivity::class.java)
            startActivity(intent)
            true
        }

        cardSaveCHanges.setOnClickListener{
           if (click == "editfield"){
               if (textName.text.toString().trim()==""){
                   textName.error = "Please enter name"
                   return@setOnClickListener
               }
               if (textEmail.text.toString().trim()==""){
                   textEmail.error = "Please enter email address"
                   return@setOnClickListener
               }
               if (latitude == 0.0 && city_id!=""){
                   Toast.makeText(applicationContext,"Please select location to update profile",Toast.LENGTH_LONG).show()
                   return@setOnClickListener
               }
               if (latitude>0 && city_id==""){
                   Toast.makeText(applicationContext,"Please select City",Toast.LENGTH_LONG).show()
                   return@setOnClickListener
               }
               saveData()
           }
        }

        val adapter = ArrayAdapter(this, R.layout.custom_dropdown_item, genderList)
        textGender.setAdapter(adapter)
        textGender.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            textGender.showDropDown()
            true
        })

        textGender.dropDownVerticalOffset = 10
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        textGender.post {
            textGender.dropDownWidth = screenWidth - 20
        }

        getCityListData()
        OptionBottomSheetforpic()
        back_1.setOnClickListener{
            finish()
        }
        im_profile_edit.setOnClickListener {
            type="profile"
            Log.d("permissions", "onCreateView: ${checkAndRequestPermissions()}")
            if (checkAndRequestPermissions()) {
                selectPhotoBottomSheetDialog.show()
            }

            if (profileimage.equals("")){
                tvRemove.visibility=View.GONE
            }else{
                tvRemove.visibility=View.VISIBLE

            }
        }
        im_cover_edit.setOnClickListener {
            type="cover"
            Log.d("permissions", "onCreateView: ${checkAndRequestPermissions()}")
            if (checkAndRequestPermissions()) {
                selectPhotoBottomSheetDialog.show()

            }
            if (coverimage == ""){
                tvRemove.visibility=View.GONE
            }else{
                tvRemove.visibility=View.VISIBLE

            }
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val byteArrayOutputStream = ByteArrayOutputStream()
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        uri
                    )
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val realPath: String = ImageFilePath.getPath(this, uri)
                if(type == "profile"){
                    Photofile = File(realPath)
                    Glide.with(this).load(Photofile).into(CUserImage)
                }else if(type == "cover"){
                    Coverfile = File(realPath)
                    Glide.with(this).load(Coverfile).into(coverImage)
                }
                click="editfield"
                changebuttonColor()
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


        getUserDetail()

        tvCity.setOnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        }
        stateTextInputLayout.setOnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        }
        stateLinearLayout.setOnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        }

        autoCompleteCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
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


            }
    }

    private fun saveData() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        Thread{
            try {
                val multipartUtility = MultipartUtility(
                    ApiURL.updateinformation,
                    "UTF-8",
                    "Bearer " + sharedPreferences.getString("token", "")
                )
                multipartUtility.addFormField("name",textName.text.toString())
                multipartUtility.addFormField("email", textEmail.text.toString())
                multipartUtility.addFormField("gender",textGender.text.toString().lowercase(Locale.getDefault()))
                multipartUtility.addFormField("address",""+textAddress.text.toString())
                multipartUtility.addFormField("city_id",city_id)
                multipartUtility.addFormField("address_id","")
                if (latitude >0.0){
                    multipartUtility.addFormField("long",""+longitude)
                    multipartUtility.addFormField("lat",""+latitude)
                    multipartUtility.addFormField("country_id","231")

                }else{
                    multipartUtility.addFormField("long","")
                    multipartUtility.addFormField("lat","")
                    multipartUtility.addFormField("country_id","")

                }


                if (Photofile!=null)
                    multipartUtility.addFilePart("profile", Photofile!!)

                if (Coverfile!=null)
                    multipartUtility.addFilePart("cover_image", Coverfile!!)

                val response = multipartUtility.finish() // response from server

                Log.e("UpdateProfileResp", response)
                try {
                    val jsonObject1 = JSONObject(response)

                   runOnUiThread {
                       progressDialog.dismiss()
                        if (jsonObject1.optBoolean("status")) {
                            var json=jsonObject1.optJSONObject("data")
                            Toast.makeText(applicationContext,""+jsonObject1.optString("msg"),Toast.LENGTH_LONG).show()
                            sharedPreferences.edit().putString(Constants.name,json.optString("name")).apply()
                            sharedPreferences.edit().putString(Constants.profile_image,json.optString("profile")).apply()
                            cardSaveCHanges.setCardBackgroundColor(resources.getColor(R.color.progress_track_color_1))
                            tvCard.setTextColor(resources.getColor(R.color.subheadingcolor))
                            click=""
                            finish()

                        }else{
                            Toast.makeText(applicationContext,""+jsonObject1.optString("msg"),Toast.LENGTH_LONG).show()

                        }
                    }


                } catch (e: JSONException) {
                    progressDialog.dismiss()
                    e.printStackTrace()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
    private fun checkAndRequestPermissions(): Boolean {
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val gps =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            if (gps != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            val gps =
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if (gps != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }*/
        if (listPermissionsNeeded.isNotEmpty()) {
            requestPermissions(listPermissionsNeeded.toTypedArray(), 123)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> { // Ensure this matches your request
                val perms = HashMap<String, Int>()
                // Initialize with CAMERA if it was requested
                if (permissions.contains(Manifest.permission.CAMERA)) {
                    perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                }

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                    }

                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("Permission", "Camera Permission Granted")
                    } else {
                        Log.e("Permission", "Camera permission was not granted.")
                        // Handle camera permission denial (e.g., explain why it's needed for taking photos)
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CAMERA
                            )
                        ) {
                            showDialogOK(getString(R.string.ServicePermissions)) { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                    DialogInterface.BUTTON_NEGATIVE -> finish()
                                }
                            }
                        } else {
                            explain("You need to give camera permission to take photos. Do you want to go to app settings?")
                        }
                    }
                }
            }
            // Handle other request codes if any
        }
    }

    fun choosePhotoFromGallary() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@PersonInfoActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
    private fun explain(msg: String) {
        val dialog = AlertDialog.Builder(this@PersonInfoActivity)
        dialog.setMessage(msg)
            .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                //  permissionsclass.requestPermission(type,code);
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.tech.healthesick")))
            }
            .setNegativeButton("Cancel")
            { paramDialogInterface, paramInt ->
                this.finish()
            }
        dialog.show()
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Log.e("path",""+path)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            // imageview!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
        }
    }
    private fun showAlert() {
        val items = arrayOf<CharSequence>(
            "" + resources.getString(R.string.Gallery),
            "" + resources.getString(R.string.Camera),
            "" + resources.getString(R.string.Cancel)
        )

        val builder = AlertDialog.Builder(this@PersonInfoActivity)
        builder.setTitle("" + resources.getString(R.string.addphoto))
        builder.setItems(
            items
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> try {
                    takePhotoFromCamera()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        builder.show()
    }
    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        if (type.equals("profile")){
            Glide.with(this).load(myBitmap).into(CUserImage)
        }else{
            Glide.with(this).load(myBitmap).into(coverImage)
        }

        val wallpaperDirectory = File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }

        try
        {
            val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            if (type.equals("profile")){
                Photofile=f
            }else{
                Coverfile=f
            }
            click="editfield"
            changebuttonColor()
            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    private fun getUserDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api= ApiURL.userinformation
        Log.e("UserInformation",""+api)

        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("getUserDataResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonObjectUserData=jsonObj.optJSONObject("data").optJSONObject("userInformation")
                        var jsonObjectUserAddress=jsonObj.optJSONObject("data").optJSONObject("userAddress")
                        profileimage=jsonObjectUserData.optString("profile")
                        coverimage=jsonObjectUserData.optString("cover_image")

                        Glide.with(applicationContext!!).load(jsonObjectUserData.optString("profile")).fitCenter().error(R.drawable._no_image).into(CUserImage)
                        Glide.with(applicationContext!!).load(jsonObjectUserData.optString("cover_image")).fitCenter().error(R.drawable.profile_background).into(coverImage)
                        textName.setText(jsonObjectUserData.optString("name"))
                        textEmail.setText(jsonObjectUserData.optString("email"))
                        textphone.setText(jsonObjectUserData.optString("phone"))
                        textdate.setText(jsonObjectUserData.optString("dob"))
                        textGender.setText(jsonObjectUserData.optString("gender"))
                        textAddress.setText(jsonObjectUserAddress.optString("address"))
                       // textPostal.setText(jsonObjectUserAddress.optString("gender"))
                        //textCountry.setText(jsonObjectUserAddress.optString("country_name"))
                        tvCity.setText(jsonObjectUserAddress.optString("city_name"))
                        city_id=jsonObjectUserAddress.optString("city_id")
                        longitude=jsonObjectUserAddress.optDouble("long",0.0)
                        latitude=jsonObjectUserAddress.optDouble("lat",0.0)
                        address=jsonObjectUserAddress.optString("address")

                        val adapter = ArrayAdapter(this@PersonInfoActivity, R.layout.custom_dropdown_item, genderList)
                        textGender.setAdapter(adapter)

                        if(address != ""){
                            textCountry.setText("United Arab Emirates")
                        }
                    }

                    try {
                        if (intent.getStringExtra("updateLocation").equals("imageLocation")){
                            nested.postDelayed(
                                Runnable {
                                    nested.smoothScrollTo(0, nested.height)
                                },
                                500
                            )
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
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
    private fun removeProfileImage() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        if (type.equals("profile")){
            api= ApiURL.deleteprofileImage+"profile"
        }else{
            api= ApiURL.deleteprofileImage+"cover_image"
        }
        Log.e("removeprofileImage",""+api)

        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                selectPhotoBottomSheetDialog.dismiss()

                Log.e("deleteProfileResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        getUserDetail()
                        Toast.makeText(applicationContext,jsonObj.optString("msg"),Toast.LENGTH_LONG).show()
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
    private fun changebuttonColor(){
        cardSaveCHanges.setCardBackgroundColor(resources.getColor(R.color.headingcolor))
        tvCard.setTextColor(resources.getColor(R.color.buttontextcolor))
    }

    override fun onResume() {
        super.onResume()
        if(sharedPreferences.getString("tempaddress","") != ""){
            latitude= sharedPreferences.getString("templat","0.0")!!.toDouble()
            longitude= sharedPreferences.getString("templong","0.0")!!.toDouble()
            address= ""+sharedPreferences.getString("tempaddress","")

            sharedPreferences.edit().remove("templat").apply()
            sharedPreferences.edit().remove("templong").apply()
            sharedPreferences.edit().remove("tempaddress").apply()

            textAddress.setText(address)

            textCountry.setText("United Arab Emirates")
            click="editfield"
            textAddress.isEnabled=true
            changebuttonColor()
        }
    }
    private fun OptionBottomSheetforpic() {

        selectPhotoBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.selectoption_bottom, null)
        selectPhotoBottomSheetDialog.setContentView(bottomSheet)


        var tvChangePhoto =bottomSheet.findViewById<TextView>(R.id.tvChangePhoto)
        tvRemove =bottomSheet.findViewById<TextView>(R.id.tvRemove)
        val tvViewPhoto =bottomSheet.findViewById<TextView>(R.id.tvViewPhoto)


        tvChangePhoto.setOnClickListener{
            selectPhotoBottomSheetDialog.dismiss()

           showAlert()
        }
        tvRemove.setOnClickListener{
            removeProfileImage()
        }
        tvViewPhoto.setOnClickListener{
            selectPhotoBottomSheetDialog.dismiss()
            var intent=Intent(this,ViewProfileImageActivity::class.java)
            if (type.equals("profile")){
                intent.putExtra("profileim",profileimage)
            }else{
                intent.putExtra("profileim",coverimage)
            }
            startActivity(intent)
        }
        val window = selectPhotoBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(selectPhotoBottomSheetDialog)
        /*  addCalorieBottomSheetDialog.setOnShowListener {
              val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
              bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
              bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
          }*/
        selectPhotoBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
}