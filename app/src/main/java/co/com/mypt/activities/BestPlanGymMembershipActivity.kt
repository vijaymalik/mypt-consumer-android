package co.com.mypt.activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextPaint
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.Plans
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.GymMembershipCarouselAdapter
import co.com.mypt.fragments.adapter.CenterRaiseTransformer
import co.com.mypt.model.BestPlanList
import co.com.mypt.model.GymMembershipValidityResponse
import co.com.mypt.rulerHeight.SessionRulerViewHorizontal
import co.com.mypt.rulerHeight.onViewUpdateListenerWeight
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer

class BestPlanGymMembershipActivity : AppCompatActivity() {
    var checkedType = "Per_session"

    lateinit var tvRealPrice: TextView
    lateinit var totalSession: TextView
    lateinit var tvDays: TextView
    lateinit var endLayout: LinearLayout
    private var lastProgress = 0
    private var lastDragTime = System.currentTimeMillis()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    var count = 0

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null
    lateinit var bestPlan: TextView
    lateinit var customization: TextView
    lateinit var bestPlanParentView: LinearLayout
    lateinit var customPlanParentView: LinearLayout
    private var carouselRecycler: DiscreteScrollView? = null
    private var isBestPlanAvailable = false
    private var isBestPlanSelected = true
    private lateinit var back: ImageView
    private lateinit var pBar: ProgressBar
    private lateinit var tvcontinue: TextView
    private lateinit var freeMsgCustom: TextView
    lateinit var topView: ConstraintLayout
    lateinit var bottomView: ConstraintLayout
    lateinit var tvcontinueView: LinearLayout
    lateinit var yourPlan: TextView
    lateinit var packagePrice: TextView
    lateinit var sessionCount: TextView
    lateinit var upArrow: ImageView
    lateinit var downArrow: ImageView
    lateinit var forwardArrow: ImageView
    lateinit var freeMsg: TextView
    lateinit var devider: ImageView
    var bestPlanDaysCount = 0
    var customDaysCount = 0
    var bestPlanId = ""
    var studioId: String? = null
    lateinit var tvCustomPlanTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_best_plan_gym_membership)
        studioId = intent.getStringExtra("studio_id")
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        tvRealPrice = findViewById(R.id.tvRealPrice)
        customization = findViewById(R.id.customization)
        bestPlan = findViewById(R.id.bestPlan)
        customPlanParentView = findViewById(R.id.customPlanParentView)
        bestPlanParentView = findViewById(R.id.bestPlanParentView)
        carouselRecycler = findViewById(R.id.carouselView)
        tvDays = findViewById(R.id.tvDays)
        back = findViewById(R.id.back)
        pBar = findViewById(R.id.p_Bar)
        tvcontinue = findViewById(R.id.tvcontinue)
        freeMsgCustom = findViewById(R.id.freeMsgCustom)
        topView = findViewById(R.id.topView)
        bottomView = findViewById(R.id.bottomView)
        tvcontinueView = findViewById(R.id.tvcontinueView)
        packagePrice = findViewById(R.id.packagePrice)
        sessionCount = findViewById(R.id.sessionCount)
        yourPlan = findViewById(R.id.yourPlan)
        upArrow = findViewById(R.id.upArrow)
        downArrow = findViewById(R.id.downArrow)
        forwardArrow = findViewById(R.id.forwardArrow)
        freeMsg = findViewById(R.id.freeMsg)
        devider = findViewById(R.id.devider)
        tvCustomPlanTitle = findViewById(R.id.tvCustomPlanTitle)
        pBar.setProgress(90, true)
        upArrow.setOnClickListener {
            upArrowClick(true)
        }
        downArrow.setOnClickListener {
            upArrowClick(false)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        back.setOnClickListener {
            finish()
        }

        tvcontinue.setOnClickListener {
            reviewPackage()
        }

        bestPlan.setOnClickListener {
            isBestPlanSelected = true
            selectTab(bestPlan, customization, bestPlanParentView, customPlanParentView)
            selectedPlan(if (isBestPlanAvailable) Plans.IS_BEST_AVAILABLE else Plans.IS_BEST_NOT_AVAILABLE)
        }

        customization.setOnClickListener {
            isBestPlanSelected = false
            selectTab(customization, bestPlan, customPlanParentView, bestPlanParentView)
            selectedPlan(Plans.CUSTOMIZE)
        }

        val rulerWeight = findViewById<SessionRulerViewHorizontal>(R.id.ruler_session)

        rulerWeight.setUpdateListenerWeight(object : onViewUpdateListenerWeight {
            override fun onViewUpdate(value: Float) {
                val valueL = if (value > .1) value.minus(.1).toFloat() else value
                println("===== $value")
                val updatedValue = value.toInt()
                if (updatedValue == 0) {
                    //seekBar!!.progress = 1
                    return
                }
                if (updatedValue > 1)
                    tvDays.text = "$updatedValue Days"
                else
                    tvDays.text = "$updatedValue Day"

                // Update the last progress value
                lastProgress = updatedValue

                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

                debounceRunnable = Runnable {
                    getGymMembershipValidityData(updatedValue)
                }
                debounceHandler.postDelayed(debounceRunnable!!, 300)
            }
        })
        rulerWeight.setMaxValue(365f)
        rulerWeight.setDefaultValue(10f)
        getGymMembershipValidityData(10)
    }

    private val snapHelper = PagerSnapHelper()

    fun initializeCarousel(data: List<BestPlanList.BestPlanData?>) {
        carouselRecycler?.adapter = GymMembershipCarouselAdapter(data)

        carouselRecycler?.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.85f)
                .build()
        )

        carouselRecycler?.setItemTransformer(CenterRaiseTransformer())
        carouselRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(rv.layoutManager)
                    val position = rv.layoutManager?.getPosition(snapView!!) ?: -1
                    if (position != -1) {
                        updateBestPlanSession(data[position])
                    }

                }
            }
        })
        if (isBestPlanSelected) {
            if (!data.isNullOrEmpty()) {
                isBestPlanAvailable = true
                selectedPlan(Plans.IS_BEST_AVAILABLE)
                updateBestPlanSession(data[0])
            } else {
                isBestPlanAvailable = false
                selectedPlan(Plans.IS_BEST_NOT_AVAILABLE)
            }
        }
    }

    private fun selectTab(
        selectedTab: TextView,
        unselectedTab: TextView,
        selectedContent: View,
        unselectedContent: View
    ) {
        // Tab UI
        selectedTab.setTextColor(resources.getColor(R.color.black))
        selectedTab.background = resources.getDrawable(R.drawable.feet_button)

        unselectedTab.setTextColor(resources.getColor(R.color.lightgreycolor))
        unselectedTab.background = null

        // Content visibility
        selectedContent.visibility = View.VISIBLE
        unselectedContent.visibility = View.GONE
    }

    fun getBestPlanApi() {
        val progressDialog: Dialog = ProgressDialog.Companion.progressDialog(this, "")
        progressDialog.show()
        val type = "gym"
        val packageType = 4
        val api = ApiURL.getBestPlan + "package_type=4&type=gym"
        GetMethod(api, this).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                val data: BestPlanList = Gson().fromJson(data, BestPlanList::class.java)
                initializeCarousel(data?.data ?: emptyList())
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
            }

        })
    }

    private fun getGymMembershipValidityData(days: Int = 1) {
        val progressDialog: Dialog = ProgressDialog.Companion.progressDialog(this, "")
        progressDialog.show()
        val api = ApiURL.membershipvalidity + studioId + "&days=" + days
        GetMethod(
            api, this
        ).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                try {
                    val response = Gson().fromJson(data, GymMembershipValidityResponse::class.java)
                    tvCustomPlanTitle.text = response.data.packageDetail.name
                    tvRealPrice.text = "${response.data.packageDetail.price}"
                    customPlanDays(response.data.packageDetail.validity.toInt())

                } catch (e: Exception) {
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
            }
        })
    }

    private fun createSlideInAnimation(fromY: Float, toY: Float): Animation {
        val slideIn = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, fromY,
            Animation.RELATIVE_TO_SELF, toY
        )
        slideIn.setDuration(300) // Adjust duration for smooth transition
        return slideIn
    }

    // Create Slide Out Animation (top to bottom or bottom to top)
    private fun createSlideOutAnimation(fromY: Float, toY: Float): Animation {
        val slideOut = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, fromY,
            Animation.RELATIVE_TO_SELF, toY
        )
        slideOut.setDuration(300) // Adjust duration for smooth transition
        return slideOut
    }

    private fun textShader(tv: TextView) {
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

    private fun calculateGradientColor(
        progress: Int,
        max: Int,
        startColor: Int,
        middleColor: Int,
        endColor: Int
    ): Int {
        val fraction = progress.toFloat() / max

        return when {
            fraction <= 0.5f -> {
                // Interpolate between startColor and middleColor
                interpolateColor(startColor, middleColor, fraction * 2)
            }

            else -> {
                // Interpolate between middleColor and endColor
                interpolateColor(middleColor, endColor, (fraction - 0.5f) * 2)
            }
        }
    }

    private fun interpolateColor(color1: Int, color2: Int, fraction: Float): Int {
        val r = Color.red(color1) + ((Color.red(color2) - Color.red(color1)) * fraction).toInt()
        val g =
            Color.green(color1) + ((Color.green(color2) - Color.green(color1)) * fraction).toInt()
        val b =
            Color.blue(color1) + ((Color.blue(color2) - Color.blue(color1)) * fraction).toInt()
        return Color.rgb(r, g, b)
    }

    override fun onResume() {
        super.onResume()
        getBestPlanApi()
    }

    fun selectedPlan(isBestPlanSelected: Plans) {
        when (isBestPlanSelected) {
            Plans.IS_BEST_AVAILABLE -> {
                this.isBestPlanSelected = true
                tvcontinue.text = getString(R.string.continue_summery)
                topView.visibility = View.VISIBLE
                tvcontinueView.visibility = View.VISIBLE
                freeMsgCustom.visibility = View.GONE
                bottomView.background =
                    resources.getDrawable(R.drawable.blue_border_container, null)
            }

            Plans.IS_BEST_NOT_AVAILABLE -> {
                hideCustomText()
            }

            else -> {
                this.isBestPlanSelected = false
                topView.visibility = View.GONE
                bottomView.background =
                    resources.getDrawable(R.drawable.blue_border_container, null)
                freeMsgCustom.visibility = View.VISIBLE
                tvcontinueView.visibility = View.VISIBLE
                tvcontinue.text = getString(R.string.continue_summery)
            }
        }
        continueButtonClick(true)
    }

    fun hideCustomText() {
        topView.visibility = View.GONE
        bottomView.background = null
        freeMsgCustom.visibility = View.GONE
        tvcontinueView.visibility = View.GONE
    }

    fun updateBestPlanSession(bestPlan: BestPlanList.BestPlanData?) {
        packagePrice.text = "AED ${bestPlan?.price}"
        val days = bestPlan?.validity_days?.toIntOrNull() ?: 0
        sessionCount.text = "$days ${if (days == 1) "Day" else "Days"}"
        freeMsg.text = "YOU ${bestPlan?.badge_text}"
        bestPlanDaysCount = days
        bestPlanId = bestPlan?.id ?: ""
    }

    fun customPlanDays(days: Int) {
        customDaysCount = days
    }

    private fun upArrowClick(isUpArrow: Boolean) {
        yourPlan.visibility = if (!isUpArrow) View.VISIBLE else View.GONE
        upArrow.visibility = if (!isUpArrow) View.VISIBLE else View.GONE
        downArrow.visibility = if (isUpArrow) View.VISIBLE else View.GONE
        freeMsg.visibility = if (!isUpArrow) View.VISIBLE else View.GONE
        devider.visibility = if (!isUpArrow) View.VISIBLE else View.GONE
    }

    fun continueButtonClick(isEnable: Boolean) {
        if (isEnable) {
            forwardArrow.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor, null))
            tvcontinueView.background =
                ContextCompat.getDrawable(this, R.drawable.white_rectangle)
        } else {
            forwardArrow.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            tvcontinue.setTextColor(resources.getColor(R.color.white, null))
            tvcontinueView.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_btn)
        }
    }

    fun reviewPackage() {
        val intent =
            Intent(this@BestPlanGymMembershipActivity, ReviewGymMembershipActivity::class.java)
        intent.putExtra("studio_id", studioId)
        intent.putExtra("package_type", 4)
        if (isBestPlanSelected) {
            intent.putExtra("days", bestPlanDaysCount)
            intent.putExtra(Constants.BEST_PLAN_ID, bestPlanId)
        } else {
            intent.putExtra("days", customDaysCount)
        }

        startActivity(intent)
    }
}