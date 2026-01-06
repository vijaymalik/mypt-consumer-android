package co.com.mypt.onBoarding

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import co.com.mypt.utils.FullScreenVideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.AppSignatureHelper
import co.com.mypt.AppSignatureHelper.Companion.TAG
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.MainActivity
import com.android.volley.VolleyError
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.facebook.ProfileTracker
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Base64
import kotlin.math.min


class PhoneNumberScreenActivity : AppCompatActivity() {
    var device_token = ""
    var phone_number = ""
    var email = ""
    var name = ""
    var id = ""
//    lateinit var videoView: FullScreenVideoView
    lateinit var edPhoneno: EditText
    lateinit var edemail: EditText
//    lateinit var flBtn: View
    lateinit var tvcontinue: MaterialButton
    lateinit var skipToHome: TextView
    lateinit var scrollview: ScrollView
    lateinit var blurImage: ImageView
    lateinit var im_gmail: ImageView
    lateinit var imfb: ImageView
    lateinit var linearLayout: LinearLayout
    lateinit var blurredView: View
    lateinit var emailInputLayout: TextInputLayout
    private lateinit var auth: FirebaseAuth
    lateinit var callbackManager: CallbackManager
    lateinit var country_code_picker: CountryCodePicker
    var countryCode = ""
    var countryNameCode = ""
    var sendOtp = false
    private val callback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(loginResult: LoginResult) {
            val accessToken = loginResult.accessToken
            Log.e("fbaccessToken", accessToken.toString())
            useLoginInformation(accessToken)
        }

        override fun onCancel() {
            // Handle cancel
        }

        override fun onError(e: FacebookException) {
            Log.e("fbe", e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number_screen)


        auth = FirebaseAuth.getInstance()
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener { token ->
            if (!TextUtils.isEmpty(token)) {
                Log.d(TAG, "retrieve token successful : $token")
                device_token = token
            } else {
                Log.w(TAG, "token should not be null...")
            }
        }.addOnFailureListener { e -> }
            .addOnCanceledListener {}.addOnCompleteListener { task -> }
        generateSSHKey(applicationContext)
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        val accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                currentAccessToken: AccessToken?
            ) {
                if (currentAccessToken != null) {
                    // AccessToken is not null implies user is logged in and hence we send the GraphRequest
                    useLoginInformation(currentAccessToken)
                } else {
                    Toast.makeText(applicationContext, "NotLoggedIn", Toast.LENGTH_LONG).show()
                }
            }
        }

        val profile1Tracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(oldProfile: Profile?, newProfile: Profile?) {
                // Handle profile change
            }
        }

        accessTokenTracker.startTracking()
        profile1Tracker.startTracking()

        country_code_picker = findViewById(R.id.country_code_picker)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        blurredView = findViewById(R.id.blurredView)
        linearLayout = findViewById(R.id.linearLayout)
        blurImage = findViewById(R.id.blurImage)
//        videoView = findViewById(R.id.idVideoView)
        im_gmail = findViewById(R.id.im_gmail)
        edPhoneno = findViewById(R.id.edPhoneno)
//        flBtn = findViewById(R.id.flBtn)
        tvcontinue = findViewById(R.id.btnContinue)
        skipToHome = findViewById(R.id.skipToHome)
        edemail = findViewById(R.id.edemail)
        imfb = findViewById(R.id.imfb)
        //scrollview = findViewById(R.id.scrollview)
        country_code_picker.registerCarrierNumberEditText(edPhoneno)

        countryNameCode = country_code_picker.defaultCountryNameCode

        country_code_picker.setOnCountryChangeListener {
            countryCode = country_code_picker.selectedCountryCodeWithPlus
            countryNameCode = country_code_picker.selectedCountryNameCode
        }

       /* val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.launcher_video)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
            val screenRatio = videoView.width / videoView.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                videoView.scaleX = scaleX
            } else {
                videoView.scaleY = 1f / scaleX
            }
        }*/
        val primary = ContextCompat.getColor(this, R.color.login_btn)
        val secondary = ContextCompat.getColor(this, R.color.text_color_neutral_200)

        edPhoneno.addTextChangedListener(object : TextWatcher {
            private var currentText = ""
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                val digits = s.toString().replace("[^\\d]".toRegex(), "") // Only keep digits
                val formatted = formatAsPhoneNumber(digits)

                if (formatted != currentText) {
                    isUpdating = true
                    edPhoneno.setText(formatted)
                    edPhoneno.setSelection(formatted.length) // Keep cursor at the end
                    currentText = formatted
                    isUpdating = false
                }
            }

            override fun afterTextChanged(s: Editable) {
                if ((s.length > 8)) {
                    sendOtp = true
                    tvcontinue.setTextColor(application.resources.getColor(R.color.text_color_primary_black))
                    tvcontinue.backgroundTintList  =
                        ColorStateList.valueOf(application.resources.getColor(R.color.white))

                } else {
                    sendOtp = false
                    tvcontinue.setTextColor(application.resources.getColor(R.color.text_color_neutral_200))
                    tvcontinue.backgroundTintList  =
                        ColorStateList.valueOf(application.resources.getColor(R.color.login_btn))
                }
            }

            private fun formatAsPhoneNumber(digits: String): String {
                val sb = StringBuilder()
                val length = digits.length

                if (length > 3) {
                    sb.append(digits.substring(0, 3)).append("-")
                    if (length > 6) {
                        sb.append(digits.substring(3, 6)).append("-")
                        sb.append(digits.substring(6, min(length.toDouble(), 10.0).toInt()))
                    } else {
                        sb.append(digits.substring(3))
                    }
                } else {
                    sb.append(digits)
                }
                return sb.toString()
            }
        })
        //edPhoneno.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))

        /*   edemail.addTextChangedListener(object : TextWatcher {
               override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

               override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

               override fun afterTextChanged(s: Editable?) {
                   val email = s.toString().trim()

                   when {
                       email.isEmpty() -> {
                           emailInputLayout.error = "Email cannot be empty"

                           tvcontinue.setBackgroundResource(R.drawable.rectangle_btn)
                           tvcontinue.setTextColor(application.resources.getColor(R.color.white))
                       }
                       !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                           emailInputLayout.error = "Enter a valid email"
                           tvcontinue.setBackgroundResource(R.drawable.rectangle_btn)
                           tvcontinue.setTextColor(application.resources.getColor(R.color.buttontextcolor))


                       }
                       else -> {
                           emailInputLayout.error = null
                           tvcontinue.setBackgroundResource(R.drawable.rectangle_white_btn)
                           tvcontinue.setTextColor(application.resources.getColor(R.color.buttontextcolor))

                       }
                   }
               }
           })*/

        tvcontinue.setOnClickListener {

            /* if (edemail.text.toString().trim() !=="" && Patterns.EMAIL_ADDRESS.matcher(edemail.text.toString()).matches()){
                 senOTP(edPhoneno.text.toString(),"3")
             }*/
            if (sendOtp) {
                senOTP(edPhoneno.text.toString(), "1")
            }
        }

        linearLayout.viewTreeObserver.addOnGlobalLayoutListener {
            // Get the visible area of the screen
            val rect = Rect()
            linearLayout.getWindowVisibleDisplayFrame(rect)
            val screenHeight = linearLayout.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // Check if the keyboard is visible (threshold can be adjusted)
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is open, change drawable
                blurredView.setBackgroundResource(R.drawable.open_number_screen_gradient)

            } else {
                // Keyboard is closed, change to the original drawable
                blurredView.setBackgroundResource(R.drawable.number_screen_gradient)

            }
        }

//        val appSignatureHelper = AppSignatureHelper(applicationContext)
//        appSignatureHelper.appSignatures

        skipToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("type", "guestUser")
            TaskStackBuilder.create(this).addNextIntentWithParentStack(intent).startActivities()
            //startActivity(intent)
            finish()
        }

        val credentialManager = CredentialManager.create(this@PhoneNumberScreenActivity)
        im_gmail.setOnClickListener {

            val googleIdOption = GetGoogleIdOption.Builder()
                // Your server's client ID, not your Android client ID.
                .setServerClientId("114794523993513944043")
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(false)
                .build()

            // Create the Credential Manager request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()


            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@PhoneNumberScreenActivity,
                    )
                    Log.e("result.credential", "" + result.credential)
                    handleGoogleSignIn(result.credential)
                } catch (e: GetCredentialException) {
                    Log.e("GetCredentialException", e.toString())
                }
            }

        }
        imfb.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, mutableListOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, callback)
        }
        getUserCountryCode()
    }

    private fun getUserCountryCode() {
        val url = "https://ipwho.is/"

        GetMethod(url, this@PhoneNumberScreenActivity).startMethod(object : ResponseData {
            override fun response(data: String?) {
                val response = JSONObject(data)
                val tempCountryCode = response.optString("country_code") // fallback to IN
                country_code_picker.setCountryForNameCode(tempCountryCode)
                countryCode = "+${response.optString("calling_code")}"
            }

            override fun error(error: VolleyError?) {
                error?.printStackTrace()
                countryCode = "+971"
                country_code_picker.setCountryForNameCode("UAE")
            }
        })
    }

    private fun handleGoogleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.e("UserDetails", "" + user?.displayName)
                    name = user!!.displayName!!
                    email = user!!.email!!
                    phone_number = user.phoneNumber.toString().replace("null", "")
                    id = user!!.uid!!
                    socialLogin()

                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    private fun senOTP(phone: String, type: String) {
        val param: MutableMap<String, String> = HashMap()
        if (type.equals("3")) {
            param["email"] = edemail.text.toString()
        } else {
            param["phone"] = phone.replace("-", "")
        }
        /* if (edPhoneno.text.toString().length==11){
             param["country_code"] = "+971"
         }else{
             param["country_code"] = "+91"

         }*/
        param["type"] = type
        param["country_code"] = countryCode

        //Toast.makeText(this@PhoneNumberScreenActivity,"$countryCode---${phone.replace("-","")}", Toast.LENGTH_LONG).show()
        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.show()

        Log.e("loginParam", param.toString())

        PostMethod(ApiURL.login, param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("loginRes", data.toString())
                    val resp = JSONObject(data!!)
                    if (resp.optBoolean("status")) {// Todo Aashish
                        val i = Intent(this@PhoneNumberScreenActivity, OtpActivity::class.java)
                        i.putExtra("phone", resp.optJSONObject("data")?.optString("number"))
                        i.putExtra("email", edemail.text.toString())
                        i.putExtra(
                            "country_code",
                            resp.optJSONObject("data")?.optString("county_code")
                        )
                        i.putExtra("type", type)
                        startActivity(i)
                    }
                    Toast.makeText(
                        this@PhoneNumberScreenActivity,
                        resp.optString("msg"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
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
//        videoView.start()
    }

    fun generateSSHKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String(Base64.getEncoder().encode(md.digest()))
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                Log.i("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }

    }

    private fun useLoginInformation(accessToken: AccessToken) {
        /**
         * Creating the GraphRequest to fetch user details
         * 1st Param - AccessToken
         * 2nd Param - Callback (which will be invoked once the request is successful)
         */
        Log.e("accessToken", accessToken.toString())

        val request = GraphRequest.newMeRequest(accessToken, object :
            GraphRequest.GraphJSONObjectCallback {
            //OnCompleted is invoked once the GraphRequest is successful
            override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                try {
                    Log.e("fbobject", `object`.toString())
                    id = `object`!!.getString("id")
                    name = `object`!!.getString("name")
                    email = `object`!!.getString("email")
                    Log.e("fmailid", "" + id)
                    Log.e("fpersonname", "" + name)
                    socialLogin()

                } catch (e: JSONException) {
                    email = ""
                    //addAlertEmail()
                    e.printStackTrace()
                }
            }
        })

        // We set parameters to the GraphRequest using a Bundle.
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        // Initiate the GraphRequest
        request.executeAsync()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun socialLogin() {
        val param: MutableMap<String, String> = HashMap()
        param["phone"] = phone_number
        param["email"] = email
        param["name"] = name
        param["unique_id"] = id
        param["device_type"] = "andriod"
        param["device_token"] = device_token

        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        if (!this@PhoneNumberScreenActivity.isFinishing) {
            progressDialog.dismiss()
        }


        Log.e("socialLoginParam", param.toString())

        PostMethod(ApiURL.sociallogin, param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                try {
                    Log.e("socialLoginResp", data.toString())
                    val resp = JSONObject(data!!)

                    if (resp.optBoolean("status")) {

                        val sharedPreferences: SharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(this@PhoneNumberScreenActivity)
                        sharedPreferences.edit().clear().apply()
                        sharedPreferences.edit().putString(
                            Constants.name,
                            resp.optJSONObject("data").optJSONObject("user").optString("name")
                                .replace("null", "")
                        ).apply()
                        sharedPreferences.edit().putString(
                            Constants.profile_image,
                            resp.optJSONObject("data").optJSONObject("user").optString("profile")
                                .replace("null", "")
                        ).apply()
                        sharedPreferences.edit().putString(
                            Constants.phone,
                            resp.optJSONObject("data").optJSONObject("user").optString("phone")
                        ).apply()
                        sharedPreferences.edit().putString(
                            Constants.address,
                            resp.optJSONObject("data").optJSONObject("user").optString("address")
                        ).apply()
                        sharedPreferences.edit().putString(
                            Constants.email,
                            resp.optJSONObject("data").optJSONObject("user").optString("email")
                                .replace("null", "")
                        ).apply()
                        sharedPreferences.edit().putString(
                            Constants.isProfileCompleted,
                            resp.optJSONObject("data").optJSONObject("user")
                                .optString("is_completed").replace("null", "")
                        ).apply()
                        sharedPreferences.edit()
                            .putString(Constants.step, resp.optJSONObject("data").optString("step"))
                            .apply()
                        sharedPreferences.edit().putString(
                            Constants.token,
                            resp.optJSONObject("data").optString("token")
                        ).apply()
                        sharedPreferences.edit()
                            .putString(Constants.userId, resp.optJSONObject("data").optString("id"))
                            .apply()
                        sharedPreferences.edit().putString(
                            Constants.lat,
                            resp.optJSONObject("data").optString("latitude")
                        ).apply()
                        sharedPreferences.edit().putString(
                            Constants.long,
                            resp.optJSONObject("data").optString("longitude")
                        ).apply()


                        Log.e("Token", sharedPreferences.getString("token", "")!!)
                        if (resp.optJSONObject("data").optJSONObject("user")
                                .optString("is_completed") == "1"
                        ) {
                            val i = Intent(this@PhoneNumberScreenActivity, MainActivity::class.java)
                            TaskStackBuilder.create(this@PhoneNumberScreenActivity)
                                .addNextIntentWithParentStack(i).startActivities()
                            finish()
                            return
                        }
                        if (resp.optJSONObject("data").optJSONObject("user")
                                .optString("name") != "null"
                        ) {
                            val i = Intent(
                                this@PhoneNumberScreenActivity,
                                PersonalizedActivity2::class.java
                            )
                            i.putExtra("name", "")
                            TaskStackBuilder.create(this@PhoneNumberScreenActivity)
                                .addNextIntentWithParentStack(i).startActivities()
                            finish()
                            return
                        }
                        val i = Intent(this@PhoneNumberScreenActivity, NameActivity::class.java)
                        TaskStackBuilder.create(this@PhoneNumberScreenActivity)
                            .addNextIntentWithParentStack(i).startActivities()
                        //startActivity(i)
                        finish()
                    } else
                        progressDialog.dismiss()

                    Toast.makeText(
                        this@PhoneNumberScreenActivity,
                        resp.optString("msg"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    progressDialog.dismiss()
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


