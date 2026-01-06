package co.com.mypt.WorkoutLibrary

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.BadgeAdapter
import co.com.mypt.adapter.StreakAdapter
import co.com.mypt.model.BadgeModel
import co.com.mypt.model.StreakModel
import co.com.mypt.utils.CarouselIndicatorView
import co.com.mypt.utils.CarouselRecyclerview
import java.io.File
import java.io.FileOutputStream

class BadgeActivity : AppCompatActivity() {
    lateinit var carouselRecyclerview: CarouselRecyclerview
    lateinit var badgeAdapter: BadgeAdapter
    var badgeArrayList = ArrayList<BadgeModel>()

    private lateinit var streakRecyclerView : RecyclerView
    private lateinit var badgeEarnedName : TextView
    private lateinit var shareAchievement : TextView
    private lateinit var headerLayout : LinearLayout
    private lateinit var back_1 : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_badge)
        streakRecyclerView = findViewById(R.id.streakRecyclerView)
        shareAchievement = findViewById(R.id.shareAchievement)
        back_1 = findViewById(R.id.back_1)
        headerLayout = findViewById(R.id.headerLayout)
        carouselRecyclerview = findViewById(R.id.carouselRecyclerview)
        badgeEarnedName = findViewById(R.id.badgeEarnedName)

        val indicatorView = findViewById<CarouselIndicatorView>(R.id.dots_indicator)
        badgeAdapter = BadgeAdapter(applicationContext, badgeArrayList)
        carouselRecyclerview.adapter = badgeAdapter
        carouselRecyclerview.set3DItem(false)
        carouselRecyclerview.setInfinite(false)
        carouselRecyclerview.setAlpha(false)
        carouselRecyclerview.setFlat(false)
        carouselRecyclerview.setIsScrollingEnabled(true)
        carouselRecyclerview.setIntervalRatio(0.8f)
        //indicatorView.setPageCount(badgeArrayList.size)

        // Listen for page changes
        indicatorView.setPageCount(5)
        carouselRecyclerview.attachIndicator(indicatorView,5)
        carouselRecyclerview.attachSmoothIndicator(indicatorView)

        headerLayout.setOnClickListener {
            finish()
        }
        val streakList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("streak_list", StreakModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("streak_list")
        }

        carouselRecyclerview.visibility = View.VISIBLE
        carouselRecyclerview.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.bottom_sheet_slide_up
            )
        )
        val streakAdapter= StreakAdapter(streakList!!,applicationContext)
        streakRecyclerView.adapter=streakAdapter

        shareAchievement.setOnClickListener {
            shareImageAndText(this,back_1)
        }
    }

    fun shareImageAndText(context: Context, imageView: ImageView) {
        val drawable = imageView.drawable ?: return

        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return

        // Save bitmap to cache directory
        val file = File(context.cacheDir, "share_image_${System.currentTimeMillis()}.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        if (contentUri != null){
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, contentUri) // image
                putExtra(Intent.EXTRA_TEXT,badgeEarnedName.text.toString()+"\n\n"+"https://mobileapp.mypt-me.com/gym/")
                // text
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(context.contentResolver, "Image", contentUri)
            }

            // Grant URI permission to all potential receivers
            val resInfoList = context.packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resInfo in resInfoList) {
                val packageName = resInfo.activityInfo.packageName
                context.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }else{
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_TEXT,badgeEarnedName.text.toString()+"\n\n"+"https://mobileapp.mypt-me.com/gym/")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

    }

}