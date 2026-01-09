package co.com.mypt.onBoarding

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import co.com.curved.CalendarArc
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.MainActivity
import co.com.mypt.adapter.ViewPagerAdapter
import co.com.mypt.onBoarding.personalize.GenderFragment
import co.com.mypt.onBoarding.personalize.GoalsFragment
import co.com.mypt.onBoarding.personalize.HeightFragment
import co.com.mypt.onBoarding.personalize.LocationSelectFragment
import co.com.mypt.onBoarding.personalize.WeightFragment
import co.com.mypt.utils.SharedDOBViewModel
import co.com.mypt.utils.SharedGenderViewModel
import co.com.mypt.utils.SharedHeightViewModel
import co.com.mypt.utils.SharedLocationViewModel
import co.com.mypt.utils.SharedWeightViewModel
import com.android.volley.VolleyError
import org.json.JSONObject
import java.util.Locale

class PersonalizedActivity2 : AppCompatActivity() {
    private val viewModel: SharedWeightViewModel by viewModels()
    private val viewHeightModel: SharedHeightViewModel by viewModels()
    private val viewLocationModel: SharedLocationViewModel by viewModels()
    private val genderModel: SharedGenderViewModel by viewModels()
    private val viewDOBModel: SharedDOBViewModel by viewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    //    lateinit var im:ImageView
    private lateinit var back: ImageView
    private lateinit var flBtn: FrameLayout
    private lateinit var tvcontinue: TextView
    private lateinit var skip: TextView
    private lateinit var p_Bar: ProgressBar
    private lateinit var personaliseLayout: RelativeLayout
    private lateinit var almostDoneLayout: RelativeLayout
    var selectedCount = 0
    private var selectedPage = 0
    private var dob = ""
    private var selectedIds = ArrayList<Int>()
    private var selectedValue = ""
    private var apiUrl = ""
    private var tempSelectedValue = ""
    private var tempSelectedHeightValue = ""
    private var lat = ""
    private var lng = ""
    private var address = ""
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personalized2)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        // window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use WindowInsetsController for transparent status bar on Android 11+
            val windowInsetsController = window.insetsController
            if (windowInsetsController != null) {
                // Hide the status bar
                windowInsetsController.hide(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
                // Set status bar color to transparent
                window.statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }
        else{
            // On Android 5.0 to 10, use the older system UI visibility
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }*/

        viewPager = findViewById(R.id.view_pager)
//        im = findViewById(R.id.im)
        back = findViewById(R.id.back)
        skip = findViewById(R.id.skip)
        flBtn = findViewById(R.id.flBtn)
        tvcontinue = findViewById(R.id.tvcontinue)
        p_Bar = findViewById(R.id.p_Bar)
        almostDoneLayout = findViewById(R.id.almostDoneLayout)
        personaliseLayout = findViewById(R.id.personaliseLayout)

        /*Picasso
            .get()
            .load(R.drawable.chestpressdark)
            .transform(BlurTransformation(this,20))
            .into(im)*/

        viewPagerAdapter = ViewPagerAdapter(this)

        val isFreshVisit = intent.getBooleanExtra("isFreshVisit", false)
        viewPagerAdapter.add(GenderFragment(), "")
        viewPagerAdapter.add(CalendarArc(), "")
        viewPagerAdapter.add(WeightFragment(), "")
        viewPagerAdapter.add(HeightFragment(), "")
        viewPagerAdapter.add(GoalsFragment.newInstance(isFreshVisit), "")
        viewPagerAdapter.add(LocationSelectFragment(), "")
//        viewPagerAdapter.add(PersonalizeScreenFragment(intent.getStringExtra("name").toString()),"")
//        viewPagerAdapter.add(PreferenceFragment(),"")

        // Set the adapter
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())
            }
        })

        viewPager.setUserInputEnabled(false)
        viewPager.setOffscreenPageLimit(1)

        flBtn.setOnClickListener {
            if (selectedCount == 1) {
                updateInfo()
            }
        }

        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        back.setOnClickListener {
            if (selectedPage == 0) {
                finish()
            }
            if (selectedPage > 0) {
                selectedPage -= 1
                viewPager.currentItem = selectedPage
                p_Bar.setProgress(15 * (selectedPage+1), true)
            }
            if (selectedPage == 1 || selectedPage == 2 || selectedPage == 3/*|| selectedPage == 6*/) {
                selectedCount = 1
                enableContinueBtn()
            }

        }

        skip.setOnClickListener {
            val name = intent.getStringExtra("name").toString()
            val intent = Intent(this, MainActivity::class.java)

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("type", "newUser")
            intent.putExtra("name", name)
            TaskStackBuilder.create(this).addNextIntentWithParentStack(intent).startActivities()
            finish()
        }

        viewModel.data.observe(this) { data ->
            tempSelectedValue = data
        }
        viewLocationModel.data.observe(this) { data ->
            val tempData = data.split("~")
            address = tempData[0]
            lat = tempData[1]
            lng = tempData[2]
        }
        viewHeightModel.data.observe(this) { data ->
            tempSelectedHeightValue = data
        }
        viewDOBModel.data.observe(this) { data ->
            dob = data
        }
        if ((sharedPreferences.getString(Constants.step, "2")?.toInt() ?: 0) > 1) {
            selectedPage = sharedPreferences.getString(Constants.step, "4")!!.toInt() - 3
            viewPager.currentItem = selectedPage
            p_Bar.setProgress(15 * (selectedPage+1), true)
            if (selectedPage == 0 || selectedPage == 4/*|| selectedPage == 5*/) {
                setButtonUnselected()
            } else {
                selectedCount = 1
                enableContinueBtn()
            }
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (selectedPage == 0) {
                finish()
            }
            if (selectedPage > 0) {
                selectedPage -= 1
                viewPager.currentItem = selectedPage
                p_Bar.setProgress(15 * (selectedPage+1), true)
            }
            if (selectedPage == 1 || selectedPage == 2 || selectedPage == 3/*|| selectedPage == 6*/) {
                selectedCount = 1
                enableContinueBtn()
            }
        }
    }

    private fun updateInfo() {
        Log.e("selectedPage===", "$selectedPage")

        val param: MutableMap<String, String> = HashMap()

        if (selectedPage == 0) {
            param["gender"] = (if (selectedValue == "others") {
                "other"
            } else {
                selectedValue
            })
            apiUrl = ApiURL.addGender
        }

        if (selectedPage == 1) {
            if (dob.equals("")) {
                Toast.makeText(this, "Date of birth is required", Toast.LENGTH_LONG).show()
                return
            }
            param["dob"] = dob
            apiUrl = ApiURL.addDOB
        }

        if (selectedPage == 2) {
            param["weight"] = tempSelectedValue
            apiUrl = ApiURL.addWeight
        }

        if (selectedPage == 3) {
            param["height"] = tempSelectedHeightValue
            apiUrl = ApiURL.addHeight
        }

        if (selectedPage == 4) {
            param["ids"] = selectedIds.toString().replace("[", "").replace("]", "").replace(" ", "")
            apiUrl = ApiURL.addGoals
        }

        if (selectedPage == 5) {
            param["address"] = address
            param["lat"] = lat
            param["long"] = lng
            apiUrl = ApiURL.addLocation
        }
        /* if (selectedPage == 0) {
            param["ids"] = selectedIds.toString().replace("[","").replace("]","").replace(" ","")
            apiUrl = ApiURL.addPreferences
        }
        if (selectedPage == 6) {
            param["name"] = selectedValue
            apiUrl = ApiURL.addPreferWork
        }*/
        Log.e("updateInfoParam", "$apiUrl------>$param")

        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.show()

        PostMethod(apiUrl, param, applicationContext).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                Log.e("updateInfoResp", "$data")
                val jsonObj = JSONObject(data!!)
                if (jsonObj.optBoolean("status")) {
                    sharedPreferences.edit().putString(
                        Constants.userInfo,
                        jsonObj.optJSONObject("data")?.optJSONObject("information").toString()
                    ).apply()


                    selectedPage += 1
                    p_Bar.setProgress(15 * (selectedPage+1), true)
                    viewPager.setCurrentItem(selectedPage, true)

                    if (selectedPage == 0) {
                        genderModel.data.value = "1"
                    }
                    if (selectedPage == 0 || selectedPage == 4/*|| selectedPage == 6*/) {
                        setButtonUnselected()
                    }
                    if (selectedPage == 2) {
                        personaliseLayout.visibility = View.GONE
                        almostDoneLayout.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            personaliseLayout.visibility = View.VISIBLE
                            almostDoneLayout.visibility = View.GONE
                        }, 4000)
                    }
                    if (selectedPage == 4) {
                        /*if (GoalsFragment().recycler != null) {
                            GoalsFragment().recycler?.adapter?.notifyDataSetChanged()
                        }*/
                    }
                    if (selectedPage == 5) {
                        viewLocationModel.refreshLocation.value = true
                    }

                    if (selectedPage >= 6) {
                        callToMainActivity()
                        return
                    }

                    /* if (selectedPage == 7) {
                       sharedPreferences.edit().putString(
                           Constants.address,
                           jsonObj.optJSONObject("data").optJSONObject("information")
                               .optString("address")
                       ).apply()

                       sharedPreferences.edit().putString(
                           Constants.lat,
                           jsonObj.optJSONObject("data").optJSONObject("information")
                               .optString("lat")
                       ).apply()
                       sharedPreferences.edit().putString(
                           Constants.long,
                           jsonObj.optJSONObject("data").optJSONObject("information")
                               .optString("long")
                       ).apply()
                       startActivity(
                           Intent(
                               this@PersonalizedActivity2,
                               GetStartedActivity::class.java
                           )
                       )
                       return
                   }

                    viewPager.currentItem = selectedPage
                    if (selectedPage == 6) {
                        if(PreferenceFragment().recycler != null)
                            PreferenceFragment().recycler?.adapter?.notifyDataSetChanged()
                    }*/
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error?.printStackTrace()
            }

        })
    }

    private fun callToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        TaskStackBuilder.create(this).addNextIntentWithParentStack(intent).startActivities()
        finish()
    }

    private fun setButtonUnselected() {
        selectedCount = 0
        disableContinueBtn()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(countReceiver, IntentFilter("selectedCount"), RECEIVER_EXPORTED)
        } else {
            registerReceiver(countReceiver, IntentFilter("selectedCount"))

        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(countReceiver)
    }

    fun enableContinueBtn() {
        flBtn.setBackgroundResource(R.drawable.bg_btn_type_1)
        tvcontinue.setTextColor(application.resources.getColor(R.color.text_color_primary_black))
        tvcontinue.compoundDrawableTintList =
            ColorStateList.valueOf(application.resources.getColor(R.color.text_color_primary_black))
    }

    fun disableContinueBtn() {
        if (selectedPage == 0) {
            flBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.gender_button))
        } else {
            flBtn.setBackgroundResource(R.drawable.bg_shape_btn_disabled)
        }

        tvcontinue.setTextColor(application.resources.getColor(R.color.text_color_neutral_200))
        tvcontinue.compoundDrawableTintList =
            ColorStateList.valueOf(application.resources.getColor(R.color.text_color_neutral_200))
    }

    val countReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getStringExtra("count") == "1") {
                selectedCount = 1
                enableContinueBtn()

                if (selectedPage == 0) {
                    selectedIds =
                        intent.getIntegerArrayListExtra("selectedPositions") ?: arrayListOf()
                    apiUrl = ApiURL.addPreferences
                }
                if (selectedPage == 0) {
                    selectedValue = intent.getStringExtra("selectedValue")!!.lowercase(Locale.ROOT)
                    apiUrl = ApiURL.addGender
                }
                if (selectedPage == 4) {
                    selectedIds = intent.getIntegerArrayListExtra("selectedGoals")!!
                    apiUrl = ApiURL.addGoals
                }
//                if(selectedPage == 6){
//                    selectedValue = intent.getStringExtra("selectedValue")!!.lowercase(Locale.ROOT)
//                    apiUrl = ApiURL.addPreferWork
//                }
            } else {
                selectedCount = 0
                disableContinueBtn()
            }
        }
    }
}