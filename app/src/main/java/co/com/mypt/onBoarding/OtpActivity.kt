package co.com.mypt.onBoarding

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CommonFunctionMethods.Companion.formatPhone
import co.com.mypt.CommonFunctionMethods.Companion.formatPhoneForTextView
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.MainActivity
import co.com.mypt.broadcastReceiverforMessage.MySMSBroadcastReceiver
import co.com.mypt.broadcastReceiverforMessage.MySMSBroadcastReceiver.OTPReceiveListener
import co.com.mypt.utils.PTLog
import com.android.volley.VolleyError
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class OtpActivity : AppCompatActivity() {
//    lateinit var videoView: VideoView
    lateinit var tvseconds: TextView
    lateinit var phoneNumber: TextView
    lateinit var email_id: TextView
    lateinit var tvResendOtp: TextView
    lateinit var tvOtpError: TextView
    lateinit var imarrow: ImageView
    lateinit var imEdit: ImageView
    lateinit var linearresend: LinearLayout
    lateinit var ed1: EditText
    lateinit var ed2: EditText
    lateinit var ed3: EditText
    lateinit var ed4: EditText
    lateinit var editTexts: Array<EditText>
    var OTP = ""
    lateinit var mySMSBroadcastReceiver: MySMSBroadcastReceiver
//    lateinit var main: View
    var device_token = ""

    companion object {
        private const val TAG = "OtpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_otp)
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener { token ->
            if (!TextUtils.isEmpty(token)) {
                PTLog.d(TAG, "retrieve token successful : $token")
                device_token = token

            } else {
                PTLog.w(TAG, "token should not be null...")
            }
        }.addOnFailureListener { e -> }
            .addOnCanceledListener {}.addOnCompleteListener { task ->
            }
//        videoView = findViewById(R.id.idVideoView)
        phoneNumber = findViewById(R.id.phoneNumber)
        linearresend = findViewById(R.id.linearresend)
        tvseconds = findViewById(R.id.tvseconds)
        tvResendOtp = findViewById(R.id.tvResendOtp)
        imarrow = findViewById(R.id.imarrow)
        imEdit = findViewById(R.id.imEdit)
        email_id = findViewById(R.id.email_id)
        ed1 = findViewById(R.id.ed1)
        ed2 = findViewById(R.id.ed2)
        ed3 = findViewById(R.id.ed3)
        ed4 = findViewById(R.id.ed4)
        tvOtpError = findViewById(R.id.tv_otp_error)
//        main = findViewById(R.id.main)
        editTexts = arrayOf(ed1, ed2, ed3, ed4)

        startSMSRetrieverClient() // Already implemented above.
        val mySMSBroadcastReceiver = MySMSBroadcastReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.registerReceiver(
                mySMSBroadcastReceiver,
                IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), RECEIVER_EXPORTED
            )
        }

        mySMSBroadcastReceiver.init(object : OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                // OTP Received
                // Toast.makeText(applicationContext,""+otp,Toast.LENGTH_LONG).show()
                val otpget = otp?.toCharArray()
                ed1.setText(otpget?.get(0).toString())
                ed2.setText(otpget?.get(1).toString())
                ed3.setText(otpget?.get(2).toString())
                ed4.setText(otpget?.get(3).toString())
            }

            override fun onOTPTimeOut() {
            }
        })

        ed1.addTextChangedListener(PinTextWatcher(0))
        ed2.addTextChangedListener(PinTextWatcher(1))
        ed3.addTextChangedListener(PinTextWatcher(2))
        ed4.addTextChangedListener(PinTextWatcher(3))

        ed1.setOnKeyListener(PinOnKeyListener(0))
        ed2.setOnKeyListener(PinOnKeyListener(1))
        ed3.setOnKeyListener(PinOnKeyListener(2))
        ed4.setOnKeyListener(PinOnKeyListener(3))


        imarrow.setOnClickListener {
            finish()
        }
        imEdit.setOnClickListener {
            finish()
        }
        tvResendOtp.setOnClickListener {
            resendOTP()
        }
       /* val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.launcher_video)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnPreparedListener { mp -> mp.isLooping = true }*/

        "${intent.getStringExtra("country_code").toString()} ${
            intent.getStringExtra("phone").toString()
        }".also { phoneNumber.text = formatPhone(it) }
        email_id.setText(intent.getStringExtra("email"))
        try {
            object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                    if (seconds == 0L) {
                        linearresend.visibility = View.GONE
                        tvResendOtp.visibility = View.VISIBLE
                    }
                    tvseconds.text = " $seconds ${resources.getString(R.string.Seconds)}"

                }

                override fun onFinish() {
                    linearresend.visibility = View.GONE
                    tvResendOtp.visibility = View.VISIBLE
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*main.viewTreeObserver.addOnGlobalLayoutListener {
            // Get the visible area of the screen
            val rect = Rect()
            main.getWindowVisibleDisplayFrame(rect)
            val screenHeight = main.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // Check if the keyboard is visible (threshold can be adjusted)
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is open, change drawable
                main.background = ResourcesCompat.getDrawable(resources,R.drawable.open_number_screen_gradient,null)

            } else {
                // Keyboard is closed, change to the original drawable
                main.setBackgroundResource(R.drawable.number_screen_gradient)

            }
        }*/
    }

    private fun resendOTP() {
        val param: MutableMap<String, String> = HashMap()
        param["phone"] = intent.getStringExtra("phone").toString()
        param["country_code"] = intent.getStringExtra("country_code").toString()
        //param["email"] = intent.getStringExtra("email").toString()
        param["type"] = intent.getStringExtra("type").toString()

        val progressDialog: Dialog = ProgressDialog.progressDialog(this@OtpActivity, "")
        progressDialog.show()

        PTLog.e("resendOTPParam", param.toString())

        PostMethod(ApiURL.resendOTP, param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                try {
                    progressDialog.dismiss()
                    PTLog.e("resendOTPResp", data.toString())
                    val resp = JSONObject(data!!)
                    if (resp.optBoolean("status")) {
                        linearresend.visibility = View.VISIBLE
                        tvResendOtp.visibility = View.GONE
                        startSMSRetrieverClient()
                        try {
                            object : CountDownTimer(60000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val seconds =
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                    if (seconds == 0L) {
                                        linearresend.visibility = View.GONE
                                        tvResendOtp.visibility = View.VISIBLE
                                    }
                                    tvseconds.text =
                                        " $seconds ${resources.getString(R.string.Seconds)}"

                                }

                                override fun onFinish() {
                                    linearresend.visibility = View.GONE
                                    tvResendOtp.visibility = View.VISIBLE
                                }
                            }.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    Toast.makeText(this@OtpActivity, resp.optString("msg"), Toast.LENGTH_SHORT)
                        .show()
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

    inner class PinTextWatcher internal constructor(private val currentIndex: Int) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""

        init {
            if (currentIndex == 0) isFirst =
                true else if (currentIndex == editTexts.size - 1) isLast = true
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
            PTLog.e("OS", "" + newTypedString)
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            if (text.length > 1) text = text[0].toString() // TODO: We can fill out other EditTexts
            val edtTxt = editTexts[currentIndex]
            edtTxt.removeTextChangedListener(this)
            edtTxt.setText(text)
            edtTxt.setSelection(text.length)
            edtTxt.addTextChangedListener(this)

            // On any text change, reset all states and then re-apply the correct
            // state based on whether each box has content. This ensures the UI is always consistent.
            editTexts.forEach { editText ->
                editText.isSelected = false // Clear any previous error state.
                editText.isActivated =
                    editText.text.isNotEmpty() // Set to green if it has text, grey if not.
            }

            if (text.length == 1) moveToNext() else if (text.isEmpty()) moveToPrevious()
            OTP = editTexts.joinToString("") { it.text }
            PTLog.e("OTP", "" + OTP)
            if (OTP.length == 4) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(ed4.windowToken, 0)
                verifyOTP(OTP)
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts[currentIndex + 1].requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts[currentIndex - 1].requestFocus()
            tvOtpError.visibility = View.GONE
        }

        private val isAllEditTextsFilled: Boolean
            private get() {
                for (editText in editTexts) if (editText.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) return false
                return true
            }

        private fun hideKeyboard() {
            if (currentFocus != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
    }

    inner class PinOnKeyListener internal constructor(private val currentIndex: Int) :
        View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts.get(currentIndex).getText().toString()
                        .isEmpty() && currentIndex != 0
                ) editTexts.get(
                    currentIndex - 1
                ).requestFocus()
            }
            return false
        }
    }

    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid: Void? -> }
        task.addOnFailureListener { e: java.lang.Exception? -> }
    }
   /* public override fun onDestroy() {
        super.onDestroy()
        if (mySMSBroadcastReceiver != null) applicationContext.unregisterReceiver(mySMSBroadcastReceiver)
    }*/

    private fun verifyOTP(otp: String) {
        val param: MutableMap<String, String> = HashMap()
        param["phone"] = intent.getStringExtra("phone").toString()
        //param["email"] = intent.getStringExtra("email").toString()
        param["country_code"] = intent.getStringExtra("country_code").toString()
        param["type"] = intent.getStringExtra("type").toString()
        param["otp"] = otp
        param["device_type"] = "andriod"
        param["device_token"] = device_token
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@OtpActivity, "")
        progressDialog.show()

        PTLog.e("verifyOTPParam", param.toString())

        PostMethod(ApiURL.submitOTP, param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                try {
                    PTLog.e("verifyOTPResp", data.toString())
                    val resp = JSONObject(data!!)

                    if (resp.optBoolean("status")) {

                        val sharedPreferences: SharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(this@OtpActivity)
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

                        PTLog.e("Token", sharedPreferences.getString("token", "")!!)
                        if (resp.optJSONObject("data").optJSONObject("user")
                                .optString("is_completed") == "1"
                        ) {
                            val i = Intent(this@OtpActivity, MainActivity::class.java)
                            TaskStackBuilder.create(this@OtpActivity)
                                .addNextIntentWithParentStack(i).startActivities()
                            finish()
                            return
                        }
                        if (resp.optJSONObject("data").optJSONObject("user")
                                .optString("name") != "null"
                        ) {
                            val i = Intent(this@OtpActivity, PersonalizedActivity2::class.java)
                            i.putExtra("name", "")
                            TaskStackBuilder.create(this@OtpActivity)
                                .addNextIntentWithParentStack(i).startActivities()
                            finish()
                            return
                        }
                        val i = Intent(this@OtpActivity, NameActivity::class.java)
                        TaskStackBuilder.create(this@OtpActivity).addNextIntentWithParentStack(i)
                            .startActivities()
                        finish()
                    } else {
                        progressDialog.dismiss()
                        editTexts.forEach { edtTxt ->
                            edtTxt.isActivated = false
                            edtTxt.isSelected = true
                        }
                        //Todo Aashish
//                        val i = Intent(this@OtpActivity, NameActivity::class.java)
//                        TaskStackBuilder.create(this@OtpActivity).addNextIntentWithParentStack(i)
//                            .startActivities()
//                        finish()
                    }
                    tvOtpError.visibility = View.VISIBLE
                    val msg = resp.optString("msg")
                    tvOtpError.text = msg
                    PTLog.d(TAG, msg)
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