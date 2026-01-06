package co.com.mypt.GymWorkout.withoutTrainer

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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import co.com.mypt.R
import co.com.mypt.activities.GymMemberReviewActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.activities.ReviewPackageActivity
import co.com.mypt.adapter.ViewPagerAdapter1
import co.com.mypt.fragments.CreatePackageScreen.MarkJourneyFragment
import co.com.mypt.utils.SharedDuringSessionViewModel
import co.com.mypt.utils.SharedPriceViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation

class GymValidityActivity : AppCompatActivity() {
    public var days=""
    var sprice=""
    var apiend_date=""
    private val viewModel: SharedDuringSessionViewModel by viewModels()
    private val priceviewModel: SharedPriceViewModel by viewModels()


    lateinit var viewPager: ViewPager2
    lateinit var back: ImageView
    lateinit var tvcontinue: TextView
    lateinit var tvPackage: TextView
    lateinit var skip: TextView
    lateinit var p_Bar: ProgressBar
    lateinit var im: ImageView
    var selectedCount = 1
    var selectedPage = 0
    var apistart_date=""

    lateinit var sharedPreferences:SharedPreferences
    lateinit var viewPagerAdapter: ViewPagerAdapter1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_validity)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
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
        tvPackage = findViewById(R.id.tvPackage)
        skip = findViewById(R.id.skip)
        tvcontinue = findViewById(R.id.tvcontinue)
        p_Bar = findViewById(R.id.p_Bar)
        im = findViewById(R.id.im)
        viewModel.apistart_date.observe(this) { data ->
            apistart_date = data
        }
        viewModel.apiend_date.observe(this) { data ->
            apiend_date = data
        }
        priceviewModel.data.observe(this) { data ->
            sprice = data
        }
        priceviewModel.days.observe(this) { data ->
            days = data
        }
        Picasso
            .get()
            .load(R.drawable.chestpressdark)
            .transform(BlurTransformation(this,20))
            .into(im)


        viewPagerAdapter = ViewPagerAdapter1(this)


        // add the fragments


        viewPagerAdapter.add(GymTotalMonthFragment(intent.getStringExtra("studio_id")),"",0)
        viewPagerAdapter.add(MarkJourneyForMembershipFragment(intent.getStringExtra("studio_id")),"",1)


        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        viewPager.setUserInputEnabled(false)
        viewPager.setOffscreenPageLimit(2)

        tvcontinue.setOnClickListener {
            if(selectedCount == 1){
                if(selectedPage ==1){
                    var intent=Intent(applicationContext,GymMemberReviewActivity::class.java)
                    intent.putExtra("apistart_date",apistart_date)
                    intent.putExtra("apiend_date",apiend_date)
                    intent.putExtra("price",sprice)
                    intent.putExtra("days",days)
                    intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                    intent.putExtra("type",getIntent().getStringExtra("type"))
                    startActivity(intent)
                    return@setOnClickListener
                }
                selectedPage += 1
                if(selectedPage == 1){
                    tvPackage.visibility=View.GONE
                    setButtonUnselected()
                }
                //viewPager.currentItem = selectedPage
                viewPager.setCurrentItem(selectedPage,true)
                p_Bar.setProgress(50*(selectedPage),true)
            }
        }

        back.setOnClickListener {
            if(selectedPage >0){
              //  tvPackage.visibility=View.VISIBLE
                selectedPage -= 1
                viewPager.currentItem = selectedPage
                p_Bar.setProgress(50*(selectedPage),true)
            }else
                finish()

            if(selectedPage == 0 || selectedPage == 1){
                selectedCount = 1
                tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                tvcontinue.setTypeface(null, Typeface.BOLD)
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
                  //  tvPackage.visibility=View.VISIBLE

                    selectedPage -= 1
                    viewPager.currentItem = selectedPage
                    p_Bar.setProgress(50*(selectedPage),true)
                }else
                    finish()

                if(selectedPage == 0 || selectedPage == 1){
                    selectedCount = 1
                    tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                    tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                    tvcontinue.setTypeface(null, Typeface.BOLD)
                }
            }
        })

    }
    private fun setButtonUnselected() {
        selectedCount = 0
        tvcontinue.background = resources.getDrawable(R.drawable.rectangle_btn)
        tvcontinue.setTextColor(resources.getColor(R.color.white))
        tvcontinue.setTypeface(null, Typeface.NORMAL)
    }


    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(selectedCountFromMarkJourney, IntentFilter("selectedCountFromMarkJourney"), RECEIVER_EXPORTED)
            registerReceiver(countReceiver, IntentFilter("selectedCountworking"), RECEIVER_EXPORTED)
//            registerReceiver(closeClass, IntentFilter("closeClass"), RECEIVER_EXPORTED)
//            registerReceiver(closegymClass, IntentFilter("closegymClass"), RECEIVER_EXPORTED)
        }
        else {
            registerReceiver(selectedCountFromMarkJourney, IntentFilter("selectedCountFromMarkJourney"))
            registerReceiver(countReceiver, IntentFilter("selectedCountworking"))
         /*   registerReceiver(closeClass, IntentFilter("closeClass"))
            registerReceiver(closegymClass, IntentFilter("closegymClass"))*/
        }
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(countReceiver)
    }

    val countReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.getStringExtra("count") == "1"){
                selectedCount = 1
                tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                tvcontinue.setTypeface(null, Typeface.BOLD)
            }else{
                selectedCount = 0
                tvcontinue.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvcontinue.setTextColor(resources.getColor(R.color.white))
                tvcontinue.setTypeface(null, Typeface.NORMAL)
            }
        }

    }
    val selectedCountFromMarkJourney = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.getStringExtra("count") == "1"){
                selectedCount = 1
                tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                tvcontinue.setTypeface(null, Typeface.BOLD)


            }else{
                selectedCount = 0
                tvcontinue.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvcontinue.setTextColor(resources.getColor(R.color.white))
                tvcontinue.setTypeface(null, Typeface.NORMAL)
            }
        }

    }

}