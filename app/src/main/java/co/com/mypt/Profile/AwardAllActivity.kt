package co.com.mypt.Profile

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.AchievementListActivity
import co.com.mypt.R
import co.com.mypt.adapter.AwardBadgeAdapter
import co.com.mypt.adapter.LockBadgeAdapter
import co.com.mypt.model.BadgeModel
import co.com.mypt.model.LockBadgeModel
import co.com.mypt.utils.CarouselRecyclerview

class AwardAllActivity : AppCompatActivity() {
    lateinit var carouselRecyclerview: CarouselRecyclerview
    lateinit var unlockBadgesCount: TextView
    lateinit var recyclerBadge: RecyclerView
    lateinit var headerLayout: LinearLayout
    lateinit var linearNextBadge: LinearLayout
    lateinit var badgeAdapter: AwardBadgeAdapter
    var badgeArrayList = ArrayList<BadgeModel>()
    var lockbadgeArrayList = ArrayList<LockBadgeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_award_all)
        carouselRecyclerview = findViewById(R.id.carouselRecyclerview)
        headerLayout = findViewById(R.id.headerLayout)
        linearNextBadge = findViewById(R.id.linearNextBadge)
        recyclerBadge = findViewById(R.id.recyclerBadge)
        unlockBadgesCount = findViewById(R.id.unlockBadgesCount)

        badgeAdapter = AwardBadgeAdapter(applicationContext, badgeArrayList)
        carouselRecyclerview.adapter = badgeAdapter
        carouselRecyclerview.set3DItem(false)
        carouselRecyclerview.setInfinite(false)
        carouselRecyclerview.setAlpha(false)
        carouselRecyclerview.setFlat(false)
        carouselRecyclerview.setIsScrollingEnabled(true)
        carouselRecyclerview.setIntervalRatio(0.8f)
        headerLayout.setOnClickListener{
            finish()
        }
        linearNextBadge.setOnClickListener{
            var intent=Intent(this,AchievementListActivity::class.java)
            startActivity(intent)
        }
        var lockAdapter = LockBadgeAdapter(applicationContext, lockbadgeArrayList)
        recyclerBadge.adapter = lockAdapter
        
        textShader(unlockBadgesCount)
    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF")
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
}