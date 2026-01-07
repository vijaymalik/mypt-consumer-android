package co.com.mypt.PlanRenewal.Renew.Home

import android.animation.ValueAnimator
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import co.com.mypt.R
import co.com.mypt.model.ActivityModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeValidityActivity : AppCompatActivity() {
    lateinit var switchCompat: SwitchCompat
    lateinit var tvTotalcost: TextView
    lateinit var tvPerSession: TextView
    var checkedType="Per_session"

    private lateinit var textSwitcher: TextSwitcher
    lateinit var seekBar : SeekBar
    lateinit var tvAEDSession:TextView

    lateinit var relaticeSave : RelativeLayout
    lateinit var tvRealPrice:TextView
    lateinit var tvcontinue:TextView
    lateinit var sessionValue : TextView
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

        setContentView(R.layout.activity_home_validity)
        switchCompat=findViewById(R.id.switchCompat)
        tvTotalcost=findViewById(R.id.tvTotalcost)
        greenLine=findViewById(R.id.greenLine)
        tvPerSession=findViewById(R.id.tvPerSession)
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
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.clash_display_medium))
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
            // getSessionData(checkedType)
        }
        tvcontinue.setOnClickListener{
            val intent1 = Intent(this@HomeValidityActivity, HomePlanReviewActivity::class.java)
            intent1.putExtra("typeSubsctiption",intent.getStringExtra("typeSubsctiption"))
            startActivity(intent1)
        }
     
        seekBar.post {
            val seekBarWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight
            val thumbOffset = seekBar.thumbOffset

            // Calculate 80% position
            val valueRatio = 0.8f
            val positionX = seekBar.paddingLeft + valueRatio * seekBarWidth

            // Center the label horizontally
            relaticeSave.x = positionX - relaticeSave.width / 2
        }
        val startColor = Color.parseColor("#3DD772")
        val middleColor = Color.parseColor("#42AB98")
        val endColor = Color.parseColor("#4981F2")

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private val valueAnimator: ValueAnimator? = null

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    seekBar!!.progress = 1
                    return
                }
                if(progress > 1)
                    sessionValue.text = "$progress months"
                else
                    sessionValue.text = "$progress month"

                //viewModel.data.value =progress.toString()

                val bounds: Rect = seekBar!!.thumb.bounds

                when (progress) {

                    12 -> {
                        sessionValueCard.x = (seekBar.left + bounds.left - 40).toFloat()
                    }
                    else -> {
                        sessionValueCard.x = (seekBar.left + bounds.left-10).toFloat()
                    }
                }

                greenLine.x = (seekBar.left + bounds.left-5).toFloat()

                val maxProgress = seekBar.max
                val gradientColor = calculateGradientColor(progress, maxProgress, startColor, middleColor, endColor)

                Log.e("gradientColor","$gradientColor")
                view1.setBackgroundColor(gradientColor)
                view2.setBackgroundColor(gradientColor)
                greenLine.setColorFilter(gradientColor, android.graphics.PorterDuff.Mode.MULTIPLY);

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

                // Update text in TextSwitcher
                // textSwitcher.setText(String.valueOf(progress))

                // Update the last progress value
                lastProgress = progress
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textSwitcher.setLayoutParams(layoutParams)

                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }
                tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
                tvcontinue.setTypeface(null, Typeface.BOLD)
                tvcontinue.isEnabled=true

                debounceRunnable = Runnable {
                    //getSessionData(checkedType)
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
    /* private fun getSessionData(checkedType: String) {
       val progressDialog: Dialog = ProgressDialog.progressDialog(this@ChooseSessionActivity,"")
       progressDialog.show()
       var api=""
       if (sharedPreferences.getString("typeWorkout","").equals("home")){
           api= ApiURL.packagecreate+sharedPreferences.getInt("selectedPackageType",0)+"&sessions="+sessionValue.text.toString().replace(" Sessions","").replace(" Session","")+"&type="+"home"+
                   "&trainer_id="+trainer_id+"&studio_id="+""+"&month="+month+"&address_id="+address_id
       }else{
           api= ApiURL.packagecreate+sharedPreferences.getInt("selectedPackageType",0)+"&sessions="+sessionValue.text.toString().replace("Sessions","").replace("Session","")+"&type="+"gym"+
                   "&trainer_id="+trainer_id+"&studio_id="+studio_id+"&month="+month+"&address_id="+address_id
       }
       Log.e("SessionPackageAPi",api)
       GetMethod(api,this@ChooseSessionActivity).startMethod(object :
           ResponseData {
           override fun response(data: String?) {
               progressDialog.dismiss()

               Log.e("SessionPackageResponse",data.toString())
               try {
                   val resp = JSONObject(data!!)
                   if(resp.optBoolean("status")){
                       sharedPreferences.edit().putString("trainer_image",resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).apply()
                       sharedPreferences.edit().putString("trainer_name",resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")).apply()
                       sharedPreferences.edit().putString("costType",checkedType).apply()
                       sharedPreferences.edit().putString("validity",resp.optJSONObject("data").optString("validity")).apply()
                       sharedPreferences.edit().putString("totalDays",resp.optJSONObject("data").optString("totalDays")).apply()
                       sharedPreferences.edit().putString("tagsArray",
                           resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags")
                               .toString()
                       ).apply()
                       tvTrainer_name.text = resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")
                       var price=""
                       if (checkedType == "Per_session"){
                           textSwitcher.setText(resp.optJSONObject("data").optString("pricePerSession"))
                           price=resp.optJSONObject("data").optString("pricePerSession")
                           sharedPreferences.edit().putString("price",price).apply()
                           Log.e("price",""+sharedPreferences.edit().putString("price",price))

                       }else{
                           textSwitcher.setText(resp.optJSONObject("data").optString("totalPrice"))
                           price=resp.optJSONObject("data").optString("totalPrice")

                           sharedPreferences.edit().putString("price",price).apply()
                           Log.e("price",""+sharedPreferences.edit().putString("price",price))

                       }
                       tvAEDSession.visibility = View.VISIBLE
                       textShader(textSwitcher.currentView as TextView)
                       val layoutParams = LinearLayout.LayoutParams(
                           LinearLayout.LayoutParams.WRAP_CONTENT,
                           LinearLayout.LayoutParams.WRAP_CONTENT
                       )
                       textSwitcher.setLayoutParams(layoutParams)

                       Glide.with(applicationContext).load(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).fitCenter().into(imTrainer)
                       activitiesModelList.clear()
                       for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                       {
                           var jsonTags=resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").optJSONObject(i)
                           var activityModel=ActivityModel()
                           activityModel.name=jsonTags.optString("name")
                           activitiesModelList.add(activityModel)
                       }
                       var activityAdapter = ActivityAdapter(applicationContext!!, activitiesModelList)
                       recycler.adapter = activityAdapter
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

   }*/
    override fun onResume() {
        super.onResume()
        val currentTextView = textSwitcher.currentView as TextView
        lifecycleScope.launch {
            delay(100)
            seekBar.progress = 1
            textShader(currentTextView)
        }
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
}