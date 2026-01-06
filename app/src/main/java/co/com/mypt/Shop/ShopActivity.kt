package co.com.mypt.Shop

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import co.com.mypt.R
import co.com.mypt.adapter.CategoryAdapter
import co.com.mypt.adapter.FeatureAdapter
import co.com.mypt.adapter.FrequentlyAdapter
import co.com.mypt.adapter.ImageRecycleAdapter
import co.com.mypt.adapter.ImageSliderAdapter
import co.com.mypt.adapter.ShopCarouselAdapter
import co.com.mypt.adapter.TrendingAdapter
import co.com.mypt.model.CategoryModel
import co.com.mypt.model.FeatureModel
import co.com.mypt.model.FrequentlyModel
import co.com.mypt.model.ImageModel
import co.com.mypt.model.ImageRecycleModel
import co.com.mypt.model.ShopCarouselModel
import co.com.mypt.model.TrendingModel
import co.com.mypt.utils.CarouselRecyclerview
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ShopActivity : AppCompatActivity() {
    lateinit var edsearch: EditText
    lateinit var videoViewLayout: RelativeLayout
    lateinit var headerLayout: LinearLayout
    lateinit var tvdeal: TextView
    lateinit var tvhours: TextView
    lateinit var tvminute: TextView
    lateinit var tvseconds: TextView
    lateinit var tvFeatured: TextView
    private var player: ExoPlayer? = null
    lateinit var videoview:PlayerView
    lateinit var viewPager: ViewPager
    lateinit var videoView: VideoView
    lateinit var nestedScroll: NestedScrollView
    lateinit var categoryRecycler: RecyclerView
    lateinit var TrendingRecycler: RecyclerView
    lateinit var FeatureRecycler: RecyclerView
    lateinit var ImageRecycler: RecyclerView
    lateinit var FrequentlyRecycler: RecyclerView
    lateinit var carouselRecyclerview: CarouselRecyclerview
    var categoryArrayList :ArrayList<CategoryModel> = ArrayList()
    var imageArrayList :ArrayList<ImageModel> = ArrayList()
    var imageRecycleArrayList :ArrayList<ImageRecycleModel> = ArrayList()
    var trendingArrayList :ArrayList<TrendingModel> = ArrayList()
    var featureModelArrayList :ArrayList<FeatureModel> = ArrayList()
    var FrequentlyModelArrayList :ArrayList<FrequentlyModel> = ArrayList()
    var shopCarouselArrayList :ArrayList<ShopCarouselModel> = ArrayList()
    private lateinit var adapter: ImageSliderAdapter
    private val handler = Handler(Looper.getMainLooper())
    lateinit var dots_indicator: DotsIndicator
    private var mediaPlayer: MediaPlayer? = null
    private var timeLeftInMillis: Long = 3600000 // Default 1 Hour
    private var countDownTimer: CountDownTimer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        edsearch=findViewById(R.id.edsearch)
        FrequentlyRecycler=findViewById(R.id.FrequentlyRecycler)
        viewPager=findViewById(R.id.idViewPager)
        headerLayout=findViewById(R.id.headerLayout)
        dots_indicator = findViewById(R.id.dots_indicator)
        TrendingRecycler = findViewById(R.id.TrendingRecycler)
        FeatureRecycler = findViewById(R.id.FeatureRecycler)
        ImageRecycler = findViewById(R.id.ImageRecycler)
        tvdeal = findViewById(R.id.tvdeal)
        tvhours = findViewById(R.id.tvhours)
        tvminute = findViewById(R.id.tvminute)
        tvseconds = findViewById(R.id.tvseconds)
        tvFeatured = findViewById(R.id.tvFeatured)
        videoView = findViewById(R.id.videoView)
        nestedScroll = findViewById(R.id.nestedScroll)
        videoViewLayout = findViewById(R.id.videoViewLayout)

        carouselRecyclerview = findViewById(R.id.carouselRecyclerview)
        categoryRecycler=findViewById(R.id.categoryRecycler)
        headerLayout.setOnClickListener{
            finish()
        }
        val videoPath = "android.resource://" + packageName + "/" + R.raw.shop_rays
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.start()
        setVideoViewToLoop(videoView)

        val text = "<font color=#959595>Search for </font> <font color=#FAFAFA>Products</font>"
        edsearch.setHint(Html.fromHtml(text))

        for (i in 0..6) {
            var categoryModel= CategoryModel()
            categoryModel.name="Fitness Equipment"
            categoryArrayList.add(categoryModel)
        }
        var categoryAdapter = CategoryAdapter(applicationContext, categoryArrayList)
         categoryRecycler.adapter = categoryAdapter

        for (i in 0..6) {
            var trendingModel= TrendingModel()
            trendingModel.price="299"
            trendingArrayList.add(trendingModel)
        }
        var trendingAdapter = TrendingAdapter(applicationContext, trendingArrayList)
        TrendingRecycler.adapter = trendingAdapter

        for (i in 0..6) {
            var featureModel= FeatureModel()
            featureModel.price="299"
            featureModelArrayList.add(featureModel)
        }
        var featureAdapter = FeatureAdapter(applicationContext, featureModelArrayList)
        FeatureRecycler.adapter = featureAdapter

        for (i in 0..6) {
            var imageModel= ImageModel()
            imageModel.image= R.drawable.gymgirl

            imageArrayList.add(imageModel)
        }
        adapter = ImageSliderAdapter(this,imageArrayList)

        for (i in 0..6) {
            var imageRecycleModel= ImageRecycleModel()
            imageRecycleModel.image= R.drawable.gymgirl

            imageRecycleArrayList.add(imageRecycleModel)
        }
        var imageRecycleAdapter = ImageRecycleAdapter(this,imageRecycleArrayList)
        ImageRecycler.adapter=imageRecycleAdapter

        for (i in 0..6) {
            var frequentlyModel= FrequentlyModel()
            frequentlyModel.price="299"
            FrequentlyModelArrayList.add(frequentlyModel)
        }
        var freqentlyAdapter = FrequentlyAdapter(this,FrequentlyModelArrayList)
        FrequentlyRecycler.adapter=freqentlyAdapter
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        dots_indicator.attachTo(viewPager)


        // Attach TabLayout with ViewPager2
       //TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // Start auto-slide
       // startAutoSlide()
        textShader(tvdeal)
        textShader1(tvhours)
        textShader1(tvminute)
        textShader1(tvseconds)
        startCountdownTimer(timeLeftInMillis)
        for (i in 0..6) {
            var shopCarouselModel= ShopCarouselModel()
            shopCarouselModel.price="299"
            shopCarouselArrayList.add(shopCarouselModel)
        }
        var shopCarouselAdapter = ShopCarouselAdapter(applicationContext, shopCarouselArrayList)
        carouselRecyclerview.adapter = shopCarouselAdapter
        carouselRecyclerview.set3DItem(false)
        carouselRecyclerview.setInfinite(false)
        carouselRecyclerview.setAlpha(false)
        carouselRecyclerview.setFlat(false)
        carouselRecyclerview.setIsScrollingEnabled(true)
        carouselRecyclerview.setIntervalRatio(0.8f)
        carouselRecyclerview.visibility = View.VISIBLE
        carouselRecyclerview.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.bottom_sheet_slide_up
            )
        )

        tvFeatured.setOnClickListener{
            var intent = Intent(this,MyPTProductListActivity::class.java)
            startActivity(intent)
        }

        nestedScroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > (v.getChildAt(0).measuredHeight - v.measuredHeight)-500) {
                    videoViewLayout.visibility = View.VISIBLE
                    //Toast.makeText(this,"Bottom---> ${(v.getChildAt(0).measuredHeight - v.measuredHeight)}",Toast.LENGTH_SHORT).show()
                }else{
                    videoViewLayout.visibility = View.GONE
                }
            }
        )
    }
    private fun startAutoSlide() {
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager.currentItem + 1) % imageArrayList.size
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // Change image every 3 seconds
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val height = tv.textSize

        val textShader = LinearGradient(
            0f, 0f, 0f, height, // Top to Bottom Gradient
            intArrayOf(
                Color.parseColor("#FFFFFF"),  // Start color (Top)
                Color.parseColor("#00FFFFFF"), // End color (Bottom)
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.shader = textShader
    }
    private fun textShader1(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }

    private fun startCountdownTimer(totalTime: Long) {
        countDownTimer?.cancel() // Cancel any existing timer

        countDownTimer = object : CountDownTimer(totalTime, 1000) { // Tick every second
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
              //  tvTimer.text = "00:00:00"
            }
        }.start()
    }

    private fun updateTimerText(millisUntilFinished: Long) {
        val hours = (millisUntilFinished / 1000) / 3600
        val minutes = ((millisUntilFinished / 1000) % 3600) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        Log.e("hour,minutes,seconds",""+hours+","+ minutes+","+seconds)
        if (hours<10)
            tvhours.text = "0"+hours+"h"
        else
            tvhours.text = ""+hours+"h"

        tvminute.text = ""+minutes+"m"
        tvseconds.text = ""+seconds+"s"

       //* tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds) // Format as HH:MM:SS
    }
    private fun setVideoViewToLoop(Videoview : VideoView){
        Videoview.setOnCompletionListener {
            Videoview.start()
        }
    }
}