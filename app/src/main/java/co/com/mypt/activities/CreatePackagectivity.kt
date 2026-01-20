package co.com.mypt.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import co.com.mypt.GymWorkout.withoutTrainer.JoiningFragment
import co.com.mypt.R
import co.com.mypt.adapter.ViewPagerAdapter1
import co.com.mypt.fragments.CreatePackageScreen.CreatePackageFragment
import co.com.mypt.fragments.CreatePackageScreen.MarkJourneyFragment
import co.com.mypt.fragments.CreatePackageScreen.TotalMonthFragment
import co.com.mypt.fragments.CreatePackageScreen.TotalSessionFragment
import co.com.mypt.utils.SharedDuringSessionViewModel
import co.com.mypt.utils.SharedSessionvalueViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.edit
import co.com.mypt.fragments.CreatePackageScreen.BestPlanTotalSessionFragment

class CreatePackagectivity : AppCompatActivity() {
    var session_value=""
    var apiend_date=""
    var setend_days=""
    var setend_dates=""
    var apistart_date=""
    var setstart_days=""
    private val viewModel: SharedDuringSessionViewModel by viewModels()
    private val viewModelSessionvalue: SharedSessionvalueViewModel by viewModels()
    lateinit var viewPager: ViewPager2
    lateinit var back: ImageView

    lateinit var tvcontinue: TextView
    lateinit var yourPlan: TextView
    lateinit var validDay: TextView
    lateinit var freeMsg: TextView
    lateinit var upArrow: ImageView
    lateinit var downArrow: ImageView
    lateinit var devider: ImageView
    lateinit var tvcontinueView: LinearLayout
    lateinit var skip: TextView
    lateinit var p_Bar: ProgressBar
//    lateinit var im:ImageView
    var selectedCount = 0
    var selectedPage = 0
    var selectedOption = 0
    var createPackagectivity: CreatePackagectivity? = null
    lateinit var viewPagerAdapter: ViewPagerAdapter1
    var setstart_dates=""
    lateinit var sharedPreferences : SharedPreferences
    var needRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_packagectivity)
        createPackagectivity=this
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putInt("selectedPackageType",-1).apply()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        window.statusBarColor = Color.TRANSPARENT

        viewPager = findViewById(R.id.view_pager)
        back = findViewById(R.id.back)
        skip = findViewById(R.id.skip)
        tvcontinue = findViewById(R.id.tvcontinue)
        tvcontinueView = findViewById(R.id.tvcontinueView)

        yourPlan = findViewById(R.id.yourPlan)
        validDay = findViewById(R.id.validDay)
        freeMsg = findViewById(R.id.freeMsg)
        upArrow = findViewById(R.id.upArrow)
        downArrow = findViewById(R.id.downArrow)
        devider = findViewById(R.id.devider)

        p_Bar = findViewById(R.id.p_Bar)
        upArrow.setOnClickListener {
            upArrowClick(true)
        }
        downArrow.setOnClickListener {
            upArrowClick(false)
        }
//        im = findViewById(R.id.im)
        viewModel.setstart_dates.observe(this) { data ->
            setstart_dates = data
        }
        viewModel.setstart_days.observe(this) { data ->
            setstart_days = data
        }
        viewModel.apistart_date.observe(this) { data ->
            apistart_date = data
        }
        viewModel.setend_dates.observe(this) { data ->
            setend_dates = data
        }
        viewModel.setend_days.observe(this) { data ->
            setend_days = data
        }
        viewModel.apiend_date.observe(this) { data ->
            apiend_date = data
        }
        viewModelSessionvalue.data.observe(this) { data ->
            session_value = data
        }

        viewPagerAdapter = ViewPagerAdapter1(this)

        // add the fragments

        viewPagerAdapter.add(CreatePackageFragment(),"",0)
        viewPagerAdapter.add(
            BestPlanTotalSessionFragment(
                intent.getStringExtra("type"),
                intent.getStringExtra("slot_id"),
                intent.getStringExtra("address_id"),
                intent.getStringExtra("trainer_id"),
                intent.getStringExtra("studio_id"),
                intent.getIntExtra("month",0)
            ), "",1
        )
        viewPagerAdapter.add(TotalMonthFragment(intent.getStringExtra("studio_id")),"",2)
        viewPagerAdapter.add(MarkJourneyFragment(
            intent.getStringExtra("trainer_id"),
        ),"",3)
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())
            }
        })

        viewPager.setUserInputEnabled(false)
        //viewPager.setOffscreenPageLimit(0)

        tvcontinueView.setOnClickListener {
            if(selectedCount == 1){
                if(selectedPage ==3 && (selectedOption == 0 || selectedOption ==1)){
                    var intent=Intent(this,DuringSeesionActivity::class.java)
                    intent.putExtra("setstart_dates",setstart_dates)
                    intent.putExtra("setstart_days",setstart_days)
                    intent.putExtra("apistart_date",apistart_date)

                    intent.putExtra("setend_dates",setend_dates)
                    intent.putExtra("setend_days",setend_days)
                    intent.putExtra("apiend_date",apiend_date)

                    intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                    intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                    intent.putExtra("address_id", getIntent().getStringExtra("address_id"))
                    intent.putExtra("session_value",session_value)
                    startActivity(intent)
                    sharedPreferences.edit().putString("classCall","0").apply()
                    sharedPreferences.edit().putString("classGymCall","0").apply()

                    return@setOnClickListener
                }
                else if(selectedPage ==4){
                    var intent=Intent(this,DuringSeesionActivity::class.java)
                    intent.putExtra("setstart_dates",setstart_dates)
                    intent.putExtra("setstart_days",setstart_days)
                    intent.putExtra("apistart_date",apistart_date)

                    intent.putExtra("setend_dates",setend_dates)
                    intent.putExtra("setend_days",setend_days)
                    intent.putExtra("apiend_date",apiend_date)

                    intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                    intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                    intent.putExtra("session_value",session_value)
                    intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                    intent.putExtra("type",getIntent().getStringExtra("type"))

                    startActivity(intent)

                    sharedPreferences.edit().putString("classCall","0").apply()
                    sharedPreferences.edit().putString("classGymCall","0").apply()
                    return@setOnClickListener
                }
                selectedPage += 1
                if(selectedPage == 3 && (selectedOption == 0 || selectedOption ==1)){
                    setButtonUnselected()
                }else if(selectedPage ==4){
                    setButtonUnselected()
                }
                //viewPager.currentItem = selectedPage
                viewPager.setCurrentItem(selectedPage,true)
                p_Bar.setProgress(25*(selectedPage+1),true)
            }
        }

        back.setOnClickListener {
            if(selectedPage >0){
                selectedPage -= 1
                viewPager.currentItem = selectedPage
                p_Bar.setProgress(25*(selectedPage+1),true)
                if(selectedPage == 0){
                    selectedCount = 1
                    tvcontinue!!.isClickable = true
                    tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle, null)
                    tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor,null))
//                    tvcontinue.setTypeface(null, Typeface.BOLD)
                }
            }else {
                sharedPreferences.edit().putString("classCall","0").apply()
                sharedPreferences.edit().putString("classGymCall","0").apply()
                unregisterCloseClassReceiver()
                unregisterCloseGymClassReceiver()
                finish()
            }

            if(selectedPage ==3 || selectedPage == 2|| selectedPage == 1){
                selectedCount = 1
                tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
//                tvcontinue.setTypeface(null, Typeface.BOLD)
            }
        }

        skip.setOnClickListener {
            val name = intent.getStringExtra("name").toString()
            val intent = Intent(this,MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("type","newUser")
            intent.putExtra("name", name)
            startActivity(intent)
            finish()
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(selectedPage >0){
                    selectedPage -= 1
                    viewPager.currentItem = selectedPage
                    p_Bar.setProgress(25*(selectedPage+1),true)
                    if(selectedPage == 0){
                        selectedCount = 1
                        tvcontinue!!.isClickable = true
                        tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle, null)
//                        tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor,null))
//                        tvcontinue.setTypeface(null, Typeface.BOLD)
                    }
                }else {
                    sharedPreferences.edit { putString("classCall", "0") }
                    sharedPreferences.edit { putString("classGymCall", "0") }
                    unregisterCloseClassReceiver()
                    unregisterCloseGymClassReceiver()
                    finish()
                }

                if(selectedPage ==3 || selectedPage == 2|| selectedPage == 1){
                    selectedCount = 1
                    tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle)
                    tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
//                    tvcontinue.setTypeface(null, Typeface.BOLD)
                }
            }
        })
    }

    private fun upArrowClick(isUpArrow: Boolean) {
        yourPlan.visibility=if (!isUpArrow)View.VISIBLE else View.GONE
        upArrow.visibility=if (!isUpArrow)View.VISIBLE else View.GONE
        downArrow.visibility=if (isUpArrow)View.VISIBLE else View.GONE
        validDay.visibility=if (!isUpArrow)View.VISIBLE else View.GONE
        freeMsg.visibility=if (!isUpArrow)View.VISIBLE else View.GONE
        devider.visibility=if (!isUpArrow)View.VISIBLE else View.GONE
    }
    private fun setButtonUnselected() {
        selectedCount = 0
        tvcontinueView.background = resources.getDrawable(R.drawable.rectangle_btn)
        tvcontinue.setTextColor(resources.getColor(R.color.white))
//        tvcontinue.setTypeface(null, Typeface.NORMAL)
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(selectedCountFromMarkJourney, IntentFilter("selectedCountFromMarkJourney"), RECEIVER_EXPORTED)
            registerReceiver(countReceiver, IntentFilter("selectedCountworking"), RECEIVER_EXPORTED)
            registerReceiver(closeClass, IntentFilter("closeClass"), RECEIVER_EXPORTED)
            registerReceiver(closegymClass, IntentFilter("closegymClass"), RECEIVER_EXPORTED)
        }
        else {
            registerReceiver(selectedCountFromMarkJourney, IntentFilter("selectedCountFromMarkJourney"))
            registerReceiver(countReceiver, IntentFilter("selectedCountworking"))
            registerReceiver(closeClass, IntentFilter("closeClass"))
            registerReceiver(closegymClass, IntentFilter("closegymClass"))
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(countReceiver)
    }

    val closeClass = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(sharedPreferences.getString("classCall","") == "0"){
                sharedPreferences.edit().putString("classCall","1").apply()

                val intent1 = Intent(this@CreatePackagectivity, CreatePackagectivity::class.java).apply {
                    putExtra("slot_id", getIntent().getStringExtra("slot_id"))
                    putExtra("address_id", getIntent().getStringExtra("address_id"))
                    putExtra("trainer_id", getIntent().getStringExtra("trainer_id"))
                    putExtra("studio_id", getIntent().getStringExtra("studio_id"))
                    putExtra("month", getIntent().getIntExtra("month", 0))
                }
                unregisterCloseClassReceiver()

                GlobalScope.launch(Dispatchers.Main) {
                    finish()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
                    } else {
                        overridePendingTransition(0, 0)
                    }
                    delay(20)
                    startActivity(intent1)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
                    } else {
                        overridePendingTransition(0, 0)
                    }// Delay for 2 seconds (2000 ms)
                }
            }
        }
    }

    val closegymClass = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val intent1 = Intent(this@CreatePackagectivity, CreatePackagectivity::class.java)
            intent1.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
            intent1.putExtra("address_id",getIntent().getStringExtra("address_id"))
            intent1.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
            intent1.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
            intent1.putExtra("month",getIntent().getIntExtra("month",0))

            unregisterCloseGymClassReceiver()

            GlobalScope.launch(Dispatchers.Main) {
                finish()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                    overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
                } else {
                    overridePendingTransition(0, 0)
                }
                delay(20)
                startActivity(intent1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                    overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
                } else {
                    overridePendingTransition(0, 0)
                }// Delay for 2 seconds (2000 ms)
            }

        }
    }

    private fun unregisterCloseClassReceiver() {
        unregisterReceiver(closeClass)
    }
    private fun unregisterCloseGymClassReceiver() {
        unregisterReceiver(closegymClass)
    }

    val countReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.getStringExtra("count") == "1"){
                selectedCount = 1
                tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
//                tvcontinue.setTypeface(null, Typeface.BOLD)


                var count = viewPagerAdapter.itemCount -1
                while (count > 0 ){
                    viewPagerAdapter.removeFragment(count)
                    count = viewPagerAdapter.itemCount -1
                }
                selectedOption = intent.getIntExtra("selectedPosition",0)

                if (intent.getIntExtra("selectedPosition",0)==0
                    || intent.getIntExtra("selectedPosition",0)==1){

                    viewPagerAdapter.add(BestPlanTotalSessionFragment(
                        getIntent().getStringExtra("type"),
                        getIntent().getStringExtra("slot_id"),
                        getIntent().getStringExtra("address_id"),
                        getIntent().getStringExtra("trainer_id"),
                        getIntent().getStringExtra("studio_id"),
                        getIntent().getIntExtra("month",0)
                    ),"",1)
                    viewPagerAdapter.add(TotalMonthFragment(getIntent().getStringExtra("studio_id")),"",2)
                    viewPagerAdapter.add(MarkJourneyFragment(
                        getIntent().getStringExtra("trainer_id")
                    ),"",3)

                }
                else if(intent.getIntExtra("selectedPosition",0)==2){
                    viewPagerAdapter.add(JoiningFragment( getIntent().getStringExtra("trainer_id"),
                        getIntent().getStringExtra("studio_id"),CreatePackagectivity()),"",1)
                    viewPagerAdapter.add(BestPlanTotalSessionFragment(
                        getIntent().getStringExtra("type"),
                        getIntent().getStringExtra("slot_id"),
                        getIntent().getStringExtra("address_id"),
                        getIntent().getStringExtra("trainer_id"),
                        getIntent().getStringExtra("studio_id"),
                        getIntent().getIntExtra("month",0)
                    ),"",2)
                    viewPagerAdapter.add(TotalMonthFragment(getIntent().getStringExtra("studio_id")),"",3)
                    viewPagerAdapter.add(MarkJourneyFragment(
                        getIntent().getStringExtra("trainer_id"),
                    ),"",4)
                }

            }else{
                selectedCount = 0
                tvcontinueView.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvcontinue.setTextColor(resources.getColor(R.color.white))
//                tvcontinue.setTypeface(null, Typeface.NORMAL)
            }
        }

    }

    val selectedCountFromMarkJourney = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.getStringExtra("count") == "1"){
                selectedCount = 1
                tvcontinueView.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
//                tvcontinue.setTypeface(null, Typeface.BOLD)
                /*var count = viewPagerAdapter.itemCount -1
                while (count > 0 ){
                    viewPagerAdapter.removeFragment(count)
                    count = viewPagerAdapter.itemCount -1
                }

                if (intent.getIntExtra("selectedPosition",0)==0
                    || intent.getIntExtra("selectedPosition",0)==1){

                    viewPagerAdapter.add(TotalSessionFragment(),"",1)
                    viewPagerAdapter.add(TotalMonthFragment(),"",2)
                    viewPagerAdapter.add(MarkJourneyFragment(),"",3)

                }else if(intent.getIntExtra("selectedPosition",0)==2){
                    viewPagerAdapter.add(JoiningFragment(),"",1)
                    viewPagerAdapter.add(TotalSessionFragment(),"",2)
                    viewPagerAdapter.add(MarkJourneyFragment(),"",3)
                }*/

            }else{
                selectedCount = 0
                tvcontinueView.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvcontinue.setTextColor(resources.getColor(R.color.white))
//                tvcontinue.setTypeface(null, Typeface.NORMAL)
            }
        }

    }
}