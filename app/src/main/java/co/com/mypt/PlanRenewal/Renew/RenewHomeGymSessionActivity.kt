package co.com.mypt.PlanRenewal.Renew

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextPaint
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.ActivityModel
import com.android.volley.VolleyError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class RenewHomeGymSessionActivity : AppCompatActivity() {
    lateinit var switchCompat: SwitchCompat
    lateinit var tvTotalcost: TextView
    lateinit var tvPerSession: TextView
    var checkedType="Per_session"
    var sessions="0"
    var minimumSessions = 1
    var totalDays = 0
    var maxSession = 0
    lateinit var tvTrainer_name: TextView
    private lateinit var textSwitcher: TextSwitcher
    lateinit var seekBar : SeekBar
    lateinit var tvAEDSession:TextView
    lateinit var cardEdit : CardView
    lateinit var relaticeSave : RelativeLayout
    lateinit var tvRealPrice:TextView
    lateinit var tvcontinue:TextView
    lateinit var sessionValue : TextView
    lateinit var maxSessionsTV : TextView
    lateinit var sessionValueCard : LinearLayout
    lateinit var greenLine : ImageView
    lateinit var imTrainer : ImageView
    lateinit var back : ImageView
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    lateinit var recycler:RecyclerView
    lateinit var view1 : View
    lateinit var view2 : View
    private var lastProgress = 0
    private var debounceRunnable: Runnable? = null
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var lastDragTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_total_session)
        switchCompat=findViewById(R.id.switchCompat)
        back=findViewById(R.id.back)
        maxSessionsTV=findViewById(R.id.maxSessionsTV)
        tvTotalcost=findViewById(R.id.tvTotalcost)
        greenLine=findViewById(R.id.greenLine)
        tvPerSession=findViewById(R.id.tvPerSession)
        imTrainer=findViewById(R.id.imTrainer)
        recycler=findViewById(R.id.recycler)
        view2=findViewById(R.id.view2)
        view1=findViewById(R.id.view1)
        tvcontinue=findViewById(R.id.tvcontinue)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        textSwitcher = findViewById(R.id.textSwitcher)
        seekBar=findViewById(R.id.seekBar)
        tvAEDSession=findViewById(R.id.tvAEDSession)
        cardEdit=findViewById(R.id.cardEdit)
        relaticeSave=findViewById(R.id.relaticeSave)
        tvRealPrice=findViewById(R.id.tvRealPrice)
        sessionValue=findViewById(R.id.sessionValue)
        sessionValueCard=findViewById(R.id.sessionValueCard)

        textSwitcher.setFactory {
            val textView = TextView(this)
            textView.textSize = 40f
            textView.setTextColor(resources.getColor(R.color.headingcolor))
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.clashdisplay_medium))
            textView.gravity = Gravity.CENTER
            textView
        }

        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedType="Total_cost"
                tvPerSession.setTextColor(applicationContext.resources.getColor(R.color.booked))
                tvTotalcost.setTextColor(applicationContext.resources.getColor(R.color.orangecolor))
            } else {
                // Handle switch OFF
                checkedType="Per_session"
                tvPerSession.setTextColor(applicationContext.resources.getColor(R.color.orangecolor))
                tvTotalcost.setTextColor(applicationContext.resources.getColor(R.color.booked))
            }
            getSessionData(checkedType)
        }

        back.setOnClickListener {
            finish()
        }

        tvcontinue.setOnClickListener{
            val intent1 = Intent(this@RenewHomeGymSessionActivity, RenewHomeGymValidityActivity::class.java)
            intent1.putExtra("id",intent.getStringExtra("id"))
            intent1.putExtra("totalDays",totalDays)
            intent1.putExtra("sessions",sessions)
            startActivity(intent1)
        }

        cardEdit.setOnClickListener{
            /*val intent = Intent(this@ChooseSessionActivity, TrainersListActivity::class.java)
            intent.putExtra("studio_id",studio_id)
            startActivity(intent)*/
        }

        seekBar.post {
            val seekBarWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight
            seekBar.thumbOffset

            // Calculate 80% position
            val valueRatio = 0.8f
            val positionX = seekBar.paddingLeft + valueRatio * seekBarWidth

            // Center the label horizontally
            relaticeSave.x = positionX - relaticeSave.width / 2
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private val valueAnimator: ValueAnimator? = null

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress <= minimumSessions){
                    seekBar!!.progress = minimumSessions
                    setSeekBarPosition(seekBar,minimumSessions)
                    tvcontinue.background = resources.getDrawable(R.drawable.rectangle_btn,null)
                    tvcontinue.setTextColor(resources.getColor(R.color.subheadingcolor,null))
                    tvcontinue.setTypeface(null, Typeface.NORMAL)
                    tvcontinue.isEnabled=false
                    return
                }

                setSeekBarPosition(seekBar,progress)

                textShader(textSwitcher.currentView as TextView)

                if (valueAnimator != null && valueAnimator.isRunning) {
                    valueAnimator.cancel() // Cancel previous animator if running
                }

                // Determine the direction of the progress change (increase or decrease)
                var inAnimation: Animation? = null
                var outAnimation: Animation? = null

                if (progress > lastProgress) {
                    // Slide from top to bottom (increasing)
                    inAnimation = createSlideInAnimation(1.0f, 0.0f) // from up to down
                    outAnimation = createSlideOutAnimation(0.0f, -1.0f) // slide out upward
                } else if (progress < lastProgress) {
                    // Slide from bottom to top (decreasing)
                    inAnimation = createSlideInAnimation(-1.0f, 0.0f) // from down to up
                    outAnimation = createSlideOutAnimation(0.0f, 1.0f) // slide out downward
                }

                // Apply the animations to TextSwitcher
                textSwitcher.inAnimation = inAnimation
                textSwitcher.outAnimation = outAnimation

                lastProgress = progress
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textSwitcher.layoutParams = layoutParams

                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

                lifecycleScope.launch {
                    delay(200)
                    if(sessions != "0"){
                        tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                        tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                        tvcontinue.setTypeface(null, Typeface.BOLD)
                        tvcontinue.isEnabled=true
                    }
                }

                debounceRunnable = Runnable {
                    getSessionData(checkedType)
                }
                debounceHandler.postDelayed(debounceRunnable!!, 300)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                lastProgress = seekBar!!.progress
                lastDragTime = System.currentTimeMillis()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        textShader(tvAEDSession)
        tvRealPrice.paintFlags = tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun setSeekBarPosition(seekBar: SeekBar?, progress: Int) {
        val startColor = Color.parseColor("#3DD772")
        val middleColor = Color.parseColor("#42AB98")
        val endColor = Color.parseColor("#4981F2")

        val bounds: Rect = seekBar!!.thumb.bounds
        sessions = "${progress - minimumSessions}"

        if(progress > 1)
            sessionValue.text = "$progress Sessions"
        else
            sessionValue.text = "$progress Session"

        if(progress > maxSession - 10) {
            sessionValueCard.x = (seekBar.left + bounds.left - 70).toFloat()
        }
        else{
            sessionValueCard.x = (seekBar.left + bounds.left-10).toFloat()
        }
        /*when (progress) {

            maxSession -> {
                sessionValueCard.x = (seekBar.left + bounds.left - 60).toFloat()
            }
            else -> {
                sessionValueCard.x = (seekBar.left + bounds.left-10).toFloat()
            }
        }*/

        greenLine.x = (seekBar.left + bounds.left-5).toFloat()

        val maxProgress = seekBar.max
        val gradientColor = calculateGradientColor(progress, maxProgress, startColor, middleColor, endColor)

        view1.setBackgroundColor(gradientColor)
        view2.setBackgroundColor(gradientColor)
        greenLine.setColorFilter(gradientColor, PorterDuff.Mode.MULTIPLY)
    }

    private fun getSessionData(checkedType: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@RenewHomeGymSessionActivity,"")
        progressDialog.show()
        GetMethod(ApiURL.upgradeTopUpPlan+"renew&sessions=$sessions&id=${intent.getStringExtra("id")}",this@RenewHomeGymSessionActivity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("renewPackageResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){

                        //tvTrainer_name.text = resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")
                        var price=""
                        if (checkedType == "Per_session"){
                            textSwitcher.setText(resp.optJSONObject("data").optString("per_session_cost"))
                            price=resp.optJSONObject("data").optString("per_session_cost")
                            tvAEDSession.text = "AED / Session"

                        }else{
                            textSwitcher.setText(resp.optJSONObject("data").optString("price"))
                            price=resp.optJSONObject("data").optString("price")
                            tvAEDSession.text = "AED"
                        }
                        totalDays = resp.optJSONObject("data").optInt("totalDays")
                        if(maxSession == 0){
                            seekBar.max = resp.optJSONObject("data").optInt("max_sessions")
                            maxSession = resp.optJSONObject("data").optInt("max_sessions")
                            maxSessionsTV.text = "$maxSession"
                            minimumSessions = resp.optJSONObject("data").optInt("current_sessions")
                            val currentTextView = textSwitcher.currentView as TextView
                            lifecycleScope.launch {
                                delay(100)
                                seekBar.progress = minimumSessions
                                setSeekBarPosition(seekBar,minimumSessions)
                                textShader(currentTextView)
                            }
                        }

                        tvAEDSession.visibility = View.VISIBLE
                        textShader(textSwitcher.currentView as TextView)
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        textSwitcher.layoutParams = layoutParams

                        /*Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).fitCenter().into(imTrainer)
                        activitiesModelList.clear()
                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                        {
                            var jsonTags=resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").optJSONObject(i)
                            var activityModel=ActivityModel()
                            activityModel.name=jsonTags.optString("name")
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter(applicationContext!!, activitiesModelList)
                        recycler.adapter = activityAdapter*/
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }
    override fun onResume() {
        super.onResume()
        getSessionData(checkedType)
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
        tv.paint.shader = textShader
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
        val g = Color.green(color1) + ((Color.green(color2) - Color.green(color1)) * fraction).toInt()
        val b = Color.blue(color1) + ((Color.blue(color2) - Color.blue(color1)) * fraction).toInt()
        return Color.rgb(r, g, b)
    }
    private fun createSlideInAnimation(fromY: Float, toY: Float): Animation {
        val slideIn = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, fromY,
            Animation.RELATIVE_TO_SELF, toY
        )
        slideIn.duration = 300 // Adjust duration for smooth transition
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
        slideOut.duration = 300 // Adjust duration for smooth transition
        return slideOut
    }
}