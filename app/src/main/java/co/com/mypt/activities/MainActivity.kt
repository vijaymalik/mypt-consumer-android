package co.com.mypt.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.curvedBottomNavigation.CurvedBottomNavigation
import co.com.mypt.curvedBottomNavigation.NavigationType
import co.com.mypt.databinding.ActivityMainBinding
import co.com.mypt.fragments.BookingFragment
import co.com.mypt.fragments.CalendarFragment
import co.com.mypt.fragments.GuestUserHomeFragment
import co.com.mypt.fragments.InactivePlanHomeFragment
import co.com.mypt.fragments.LibraryFragment
import co.com.mypt.fragments.MoreFragment
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.android.volley.VolleyError
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    var progressDialog: Dialog? = null
    var selectedId = R.id.guestUserHomeFragment
    var userType = ""
    var lat = ""
    var long = ""
    var chooseAddress = ""
    var name = ""
    lateinit var sharedPreferences : SharedPreferences
    lateinit var purchaseGymPass:ImageView
    var doubleBackToExitPressedOnce: Boolean = false

    companion object {
        private val ID_HOME =  R.id.homeFragment
        private val ID_BOOKING = R.id.bookingFragment
        private val ID_Library= R.id.libraryFragment
        private val ID_CALENDAR = R.id.calendarFragment
        private val ID_CART = R.id.moreFragment
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        window.statusBarColor = Color.TRANSPARENT

        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)
        try {
            userType = intent.getStringExtra("type").toString()
            chooseAddress = intent.getStringExtra("chooseAdreess").toString()
            lat = intent.getStringExtra("lati").toString()
            long = intent.getStringExtra("longi").toString()
            name = intent.getStringExtra("name").toString()
        }catch (e:Exception){
            e.printStackTrace()
        }
        with(binding) {
            setContentView(root)
            initNavHost()
            setUpBottomNavigation()
        }
        handleTabSelection(R.id.homeFragment)
        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("back","back")
                //super.onBackPressed();
                val currentId = navController.currentDestination?.id

                if (currentId != R.id.homeFragment) {
                    // Navigate to home tab
//                    binding.bottomNavigation.show(R.id.homeFragment)
                    handleTabSelection(R.id.homeFragment) // optional if you want to trigger logic manually
                } else {
                    // Already on home, double back to exit
                    if (doubleBackToExitPressedOnce) {
                        finish()
                        return
                    }

                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.PleaseclickBACKagaintoexit),
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
                /*val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment is GuestUserHomeFragment && fragment.isVisible || fragment is InactivePlanHomeFragment && fragment.isVisible) {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                        System.exit(0)
                        return
                    }

                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.PleaseclickBACKagaintoexit),
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)

                    return
                }
                getData(true,false)*/
            }
        })
    }

    private fun setUpBottomNavigation() {
        progressDialog = ProgressDialog.progressDialog(this, "")

        /*val bottomNavigationItems = listOf(
            CurvedBottomNavigation.Model(ID_HOME, getString(R.string.home), R.drawable.home),
            CurvedBottomNavigation.Model(ID_BOOKING, getString(R.string.bookings), R.drawable.book),
            CurvedBottomNavigation.Model(ID_Library, getString(R.string.library), R.drawable.dumbbell),
            CurvedBottomNavigation.Model(ID_CALENDAR, getString(R.string.calendar), R.drawable.calendar),
            CurvedBottomNavigation.Model(ID_CART, getString(R.string.more), R.drawable.menu)
        )*/

        /*binding.bottomNavigation.apply {
           *//* bottomNavigationItems.forEachIndexed { index, item ->
                add(item, index)
            }*//*

            setOnClickMenuListener { item ->
                *//*if (item.id==R.id.homeFragment)
                    progressDialog?.show()*//*

                selectedId = item.id

                // Add delay to sync with animation
                Handler(Looper.getMainLooper()).postDelayed({
                    handleTabSelection(item.id)
                }, 300) // Match with your bottom nav animation duration
            }
            setupNavController(navController)
            navigationType = NavigationType.LABELED
        }*/
        binding.bottomNav.setOnItemSelectedListener {item->
            when (item.itemId) {
                R.id.home->{
                    handleTabSelection(ID_HOME)
                    true
                }
                R.id.plans->{
                    handleTabSelection(ID_BOOKING)
                    true
                }
                R.id.bookings->{
                    handleTabSelection(ID_CALENDAR)
                    true
                }
                R.id.menu->{
                    handleTabSelection(ID_CART)
                    true
                }

                else ->{false}
            }

        }


        getData(false,true)
    }

    private fun handleTabSelection(id: Int) {
        /*if (!isUserLoggedIn()) {
            startActivity(Intent(this, PhoneNumberScreenActivity::class.java))
            return
        }*/
        changeFragment(id)
    }

    private fun isUserLoggedIn(): Boolean {
        val token = sharedPreferences.getString(Constants.token, "") ?: ""
        return token.isNotEmpty() && token != "-1"
    }

    lateinit var frag : Fragment
    private fun changeFragment(id: Int) {
        println("Change Fragment")
        if (id==R.id.homeFragment){
            getData(false,false)
        }else{
            if (!isUserLoggedIn()) {
                startActivity(Intent(this, PhoneNumberScreenActivity::class.java))
                return
            }
            Handler(Looper.getMainLooper()).postDelayed({
                navController.navigate(id)
            }, 200)
        }
    }

    private fun initNavHost() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(finishActivity, IntentFilter("finish"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(finishActivity, IntentFilter("finish"))
        }
    }

    val finishActivity = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }
    private fun getData(isBack: Boolean,isInit: Boolean) {
        progressDialog?.show()
        //val api = ApiURL.getbooking + "2&date=&session_type=&location="
        val api = ApiURL.checktype

        GetMethod(api, this).startMethod(object : ResponseData {
            override fun response(data: String?) {
                try {
                    val jsonObj = JSONObject(data ?: "")
                    var isActiveUser = false
                    Log.e("jsonObj", "$jsonObj")

                    //val isActiveUser = jsonObj.optBoolean("status") && (jsonObj.optJSONArray("data")?.length() ?: 0) > 0
                    if(jsonObj.optBoolean("status"))
                        isActiveUser = jsonObj.optJSONObject("data").optBoolean("isActive")

                    // Navigate to placeholder HomeFragment
                    Handler(Looper.getMainLooper()).post {
                        val bundle = Bundle().apply {
                            putBoolean("isActiveUser", isActiveUser)
                            putString("name", name)
                            putString("lat", lat)
                            putString("long", long)
                            putString("chooseAddress", chooseAddress)
                        }
                        navController.navigate(R.id.homeFragment, bundle)
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressDialog?.dismiss()
                    }, 400)
                } catch (e: Exception) {
                    progressDialog?.dismiss()
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog?.dismiss()
                error?.printStackTrace()
            }
        })
    }

}