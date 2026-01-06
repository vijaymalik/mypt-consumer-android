package co.com.mypt.onBoarding

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.curvedBottomNavigation.dp
import com.android.volley.VolleyError
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class NameActivity : AppCompatActivity() {
    lateinit var scrollview: ScrollView
    lateinit var im: ImageView
    lateinit var userFirstName: TextInputEditText
    lateinit var flBtn: FrameLayout
    lateinit var tvcontinue: TextView
    lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_name)
        linearLayout = findViewById(R.id.linearLayout)
        //scrollview = findViewById(R.id.scrollview)
        im = findViewById(R.id.im)
        flBtn = findViewById(R.id.flBtn)
        tvcontinue = findViewById(R.id.tvcontinue)
        userFirstName = findViewById(R.id.userFirstName)
        userFirstName.addTextChangedListener(object : TextWatcher {
            private var currentText = ""
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    flBtn.setBackgroundResource(R.drawable.bg_btn_type_1)
                    tvcontinue.setTextColor(application.resources.getColor(R.color.buttontextcolor))
                    tvcontinue.setTextColor(application.resources.getColor(R.color.text_color_primary_black))
                    tvcontinue.compoundDrawableTintList =
                        ColorStateList.valueOf(application.resources.getColor(R.color.text_color_primary_black))

                } else {
                    flBtn.setBackgroundResource(R.drawable.bg_shape_btn_disabled)
                    tvcontinue.setTextColor(application.resources.getColor(R.color.text_color_neutral_200))
                    tvcontinue.compoundDrawableTintList =
                        ColorStateList.valueOf(application.resources.getColor(R.color.text_color_neutral_200))

                }
            }
        })

        flBtn.setOnClickListener {
            if (userFirstName.text.toString().isNotEmpty()) {
                addName()
            }
        }

        linearLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Get the visible area of the screen
                val rect = Rect()
                linearLayout.getWindowVisibleDisplayFrame(rect)
                val screenHeight = linearLayout.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                // Check if the keyboard is visible (threshold can be adjusted)
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is open, change drawable
                    im.setImageDrawable(getDrawable(R.drawable.man_gradient))
                    linearLayout.setPadding(
                        linearLayout.paddingStart,
                        linearLayout.paddingTop,
                        linearLayout.paddingEnd,
                        120.dp(linearLayout.context)
                    )// linearLayout.paddingBottom)
                } else {
                    // Keyboard is closed, change to the original drawable
                    im.setImageDrawable(getDrawable(R.drawable.man))
                    linearLayout.setPadding(
                        linearLayout.paddingStart,
                        linearLayout.paddingTop,
                        linearLayout.paddingEnd,
                        24.dp(linearLayout.context)
                    )// linearLayout.paddingBottom)
                }
            }
        })

    }

    private fun addName() {
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this@NameActivity)
        val param: MutableMap<String, String> = HashMap()
        param["name"] = userFirstName.text.toString()
        param["id"] = sharedPreferences.getString(Constants.userId, "").toString()

        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.show()

        Log.e("addNameParam", param.toString())

        PostMethod(ApiURL.addName, param, this).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("addNameResp", data.toString())
                    val resp = JSONObject(data!!)
                    if (resp.optBoolean("status")) {
                        sharedPreferences.edit().putString(
                            Constants.name,
                            resp.optJSONObject("data").optString("name").replace("null", "")
                        ).apply()

                        val i = Intent(this@NameActivity, PersonalizedActivity2::class.java)
                        i.putExtra("name", userFirstName.text.toString())
                        startActivity(i)

                    }
                    Toast.makeText(this@NameActivity, resp.optString("msg"), Toast.LENGTH_SHORT)
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
}