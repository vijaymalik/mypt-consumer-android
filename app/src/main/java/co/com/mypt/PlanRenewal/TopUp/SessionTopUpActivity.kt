package co.com.mypt.PlanRenewal.TopUp

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.PlanRenewal.UpgradeTopUpReviewPackageActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.ActivityModel
import com.android.volley.VolleyError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class SessionTopUpActivity : AppCompatActivity() {
    lateinit var switchCompat: SwitchCompat
    lateinit var tvTotalcost: TextView
    lateinit var tvPerSession: TextView
    var checkedType="Per_session"
    var sessions="0"
    var minimumSessions = 1
    var currentMaxSessions = 0
    var maxSession = 0

    private lateinit var textSwitcher: TextSwitcher
    lateinit var seekBar : SeekBar
    lateinit var tvAEDSession:TextView

    lateinit var relaticeSave : RelativeLayout
    lateinit var tvRealPrice:TextView
    lateinit var tvcontinue:TextView
    lateinit var sessionValue : TextView
    var price=""
    lateinit var maxSessionsTV : TextView
    lateinit var sessionValueCard : LinearLayout
    lateinit var greenLine : ImageView

    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()

    lateinit var view1 : View
    lateinit var view2 : View
    private var lastProgress = 0
    private var debounceRunnable: Runnable? = null
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var lastDragTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session_top_up)
        switchCompat=findViewById(R.id.switchCompat)
        tvTotalcost=findViewById(R.id.tvTotalcost)
        greenLine=findViewById(R.id.greenLine)
        tvPerSession=findViewById(R.id.tvPerSession)
        maxSessionsTV=findViewById(R.id.maxSessionsTV)

        view2=findViewById(R.id.view2)
        view1=findViewById(R.id.view1)
        tvcontinue=findViewById(R.id.tvcontinue)

        textSwitcher = findViewById(R.id.textSwitcher)
        seekBar=findViewById(R.id.seekBar)
        tvAEDSession=findViewById(R.id.tvAEDSession)

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
        tvcontinue.setOnClickListener{
            val intent1 = Intent(this@SessionTopUpActivity, UpgradeTopUpReviewPackageActivity::class.java)
            intent1.putExtra("typeSubsctiption",intent.getStringExtra("typeSubsctiption"))
            intent1.putExtra("id",intent.getStringExtra("id"))
            intent1.putExtra("sessions",sessions)
            intent1.putExtra("days","")
            intent1.putExtra("price",price)
            startActivity(intent1)
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

                /*if(progress >= currentMaxSessions && currentMaxSessions > 0){
                    seekBar!!.progress = minimumSessions+currentMaxSessions
                    setSeekBarPosition(seekBar,minimumSessions+currentMaxSessions)
                    return
                }*/

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

                // Update the last progress value
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
        getSessionData(checkedType)
        textShader(tvAEDSession)
        tvRealPrice.paintFlags = tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
     private fun getSessionData(checkedType: String) {
         val progressDialog: Dialog = ProgressDialog.progressDialog(this@SessionTopUpActivity,"")
         progressDialog.show()
         Log.e("api----->",ApiURL.upgradeTopUpPlan+"topup&sessions=$sessions&id=${intent.getStringExtra("id")}")
         GetMethod(ApiURL.upgradeTopUpPlan+"topup&sessions=$sessions&id=${intent.getStringExtra("id")}",
             this@SessionTopUpActivity).startMethod(object :
             ResponseData {
             override fun response(data: String?) {
                 progressDialog.dismiss()

                 Log.e("topUpPackageResponse",data.toString())
                 try {
                     val resp = JSONObject(data!!)
                     if(resp.optBoolean("status")){

                         //tvTrainer_name.text = resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")

                         if (checkedType == "Per_session"){
                             textSwitcher.setText(resp.optJSONObject("data").optString("per_session_cost"))
                             price=resp.optJSONObject("data").optString("price")
                             tvAEDSession.text = "AED / Session"

                         }else{
                             textSwitcher.setText(resp.optJSONObject("data").optString("price"))
                             price=resp.optJSONObject("data").optString("price")
                             tvAEDSession.text = "AED"
                         }

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
                     else{
                         if(sessions == "0"){
                             return
                         }
                         Toast.makeText(this@SessionTopUpActivity,resp.optJSONObject("errors").optString("msg"), Toast.LENGTH_SHORT).show()
                         currentMaxSessions = resp.optJSONObject("errors").optInt("days")
                         seekBar.progress = minimumSessions+resp.optJSONObject("errors").optInt("days")
                         setSeekBarPosition(seekBar, minimumSessions+resp.optJSONObject("errors").optInt("days"))
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

        greenLine.x = (seekBar.left + bounds.left-5).toFloat()

        val maxProgress = seekBar.max
        val gradientColor = calculateGradientColor(progress, maxProgress, startColor, middleColor, endColor)

        view1.setBackgroundColor(gradientColor)
        view2.setBackgroundColor(gradientColor)
        greenLine.setColorFilter(gradientColor, android.graphics.PorterDuff.Mode.MULTIPLY)
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