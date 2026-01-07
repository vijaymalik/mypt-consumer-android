package co.com.mypt.GymWorkout.withoutTrainer

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextPaint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.model.ActivityModel
import co.com.mypt.utils.SharedPriceViewModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class GymTotalMonthFragment(var studio_id: kotlin.String?) : Fragment() {
    private val viewModel: SharedPriceViewModel by activityViewModels()

    lateinit var switchCompat: SwitchCompat
    lateinit var tvTotalcost: TextView
    lateinit var tvPerSession: TextView
    lateinit var imtrainer: ImageView

    lateinit var recycler: RecyclerView
    lateinit var tvRealPrice:TextView
    lateinit var tvAEDSession:TextView
    lateinit var tvTrainer_name:TextView
    lateinit var sessionValue : TextView
    lateinit var seekBar : SeekBar
    lateinit var view1 : View
    lateinit var view2 : View
    lateinit var sessionValueCard : LinearLayout
    lateinit var endLayout : LinearLayout
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()

    private lateinit var textSwitcher: TextSwitcher
    private var lastProgress = 0
    private var lastDragTime = System.currentTimeMillis()
    var checkedType="Per_session"
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_gym_total_month, container, false)
        recycler=view.findViewById(R.id.recycler)
        tvRealPrice=view.findViewById(R.id.tvRealPrice)
        tvAEDSession=view.findViewById(R.id.tvAEDSession)
        switchCompat=view.findViewById(R.id.switchCompat)
        tvTotalcost=view.findViewById(R.id.tvTotalcost)
        tvPerSession=view.findViewById(R.id.tvPerSession)
        view2=view.findViewById(R.id.view2)
        view1=view.findViewById(R.id.view1)
        seekBar=view.findViewById(R.id.seekBar)
        sessionValue=view.findViewById(R.id.sessionValue)
        sessionValueCard=view.findViewById(R.id.sessionValueCard)
        tvTrainer_name=view.findViewById(R.id.tvTrainer_name)
        endLayout=view.findViewById(R.id.endLayout)
        imtrainer=view.findViewById(R.id.imtrainer)

        textSwitcher = view.findViewById(R.id.textSwitcher)

        textSwitcher.setFactory {
            val textView = TextView(context)
            textView.textSize = 50f
            textView.setTextColor(resources.getColor(R.color.headingcolor))
            textView.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.clash_display_medium))
            textView.gravity = Gravity.CENTER
            textView
        }
        val currentTextView = textSwitcher.currentView as TextView
        //textSwitcher.setText("100")

        lifecycleScope.launch {
            delay(2000)
            seekBar.progress = 1
            textShader(currentTextView)
        }

        val startColor = Color.parseColor("#3DD772")
            val middleColor = Color.parseColor("#42AB98")
        val endColor = Color.parseColor("#4981F2")

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private val valueAnimator: ValueAnimator? = null

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    return
                }
                sessionValue.text = if(progress > 1) {"$progress Days" } else {"$progress Days"}
                val bounds: Rect = seekBar!!.thumb.bounds

                when (progress) {
                    365 -> {
                        endLayout.visibility = View.VISIBLE
                        view1.visibility = View.INVISIBLE
                        view2.visibility = View.INVISIBLE
                        sessionValueCard.x = (seekBar.left + bounds.left - 40).toFloat()
                    }
                    else -> {
                        endLayout.visibility = View.GONE
                        view1.visibility = View.VISIBLE
                        view2.visibility = View.VISIBLE
                        sessionValueCard.x = (seekBar.left + bounds.left-10).toFloat()
                    }
                }

                val maxProgress = seekBar?.max ?: 365
                val gradientColor = calculateGradientColor(progress, maxProgress, startColor, middleColor, endColor)

                view1.setBackgroundColor(gradientColor)
                view2.setBackgroundColor(gradientColor)

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
              //  textSwitcher.setText(String.valueOf(progress))

                // Update the last progress value
                lastProgress = progress

                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

                debounceRunnable = Runnable {
                    getValidityData(checkedType)
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
        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedType="Total_cost"
                tvPerSession.setTextColor(requireActivity().resources.getColor(R.color.booked))
                tvTotalcost.setTextColor(requireActivity().resources.getColor(R.color.orangecolor))
            } else {
                // Handle switch OFF
                checkedType="Per_session"

                tvPerSession.setTextColor(requireActivity().resources.getColor(R.color.orangecolor))
                tvTotalcost.setTextColor(requireActivity().resources.getColor(R.color.booked))
            }
            getValidityData(checkedType)

        }



        textShader(tvAEDSession)
        tvRealPrice.paintFlags = tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        return view
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
        val g = Color.green(color1) + ((Color.green(color2) - Color.green(color1)) * fraction).toInt()
        val b = Color.blue(color1) + ((Color.blue(color2) - Color.blue(color1)) * fraction).toInt()
        return Color.rgb(r, g, b)
    }
    private fun getValidityData(checkedType: kotlin.String) {

        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        var api=""
        api= ApiURL.membershipvalidity+studio_id+"&days="+sessionValue.text.toString().replace("Days","").replace("Day","").replace("Sessions","")
        Log.e("ValidityApi",api)
        GetMethod(api
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: kotlin.String?) {
                progressDialog.dismiss()

                Log.e("SessionPackageResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){

                        sessionValue.text=resp.optJSONObject("data")!!.optJSONObject("packageDetail").optString("validity")+" Days"
                        tvTrainer_name.setText(resp.optJSONObject("data")!!.optJSONObject("studio").optString("name"))
                        var price=""
                        if (checkedType.equals("Per_session")){
                            textSwitcher.setText(resp.optJSONObject("data")!!.optJSONObject("packageDetail").optString("price"))
                            price=resp.optJSONObject("data")!!.optJSONObject("packageDetail").optString("price")
                            viewModel.data.value =price
                            viewModel.days.value =resp.optJSONObject("data")!!.optJSONObject("packageDetail").optString("validity")

                        }else{
                            textSwitcher.setText(resp.optJSONObject("data")!!.optJSONObject("packageDetail").optString("price"))
                            price=resp.optJSONObject("data")!!.optJSONObject("packageDetail").optString("price")
                          //  sharedPreferences.edit().putString("price",price).apply()
                          //  Log.e("price",""+sharedPreferences.edit().putString("price",price))

                        }

                        Glide.with(requireActivity()).load(resp.optJSONObject("data")!!.optJSONObject("studio").optString("image")).fitCenter().into(imtrainer)
                        activitiesModelList.clear()
                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("studio").optJSONArray("tags").length())
                        {
                          //  var jsonTags=resp.optJSONObject("data")!!.optJSONObject("studio").optJSONArray("tags").optJSONObject(i)
                            var activityModel=ActivityModel()
                            activityModel.name=
                                resp.optJSONObject("data")!!.optJSONObject("studio").optJSONArray("tags").get(i)
                                    .toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = ActivityAdapter(activity!!, activitiesModelList)
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

    }

    override fun onResume() {
        super.onResume()
        getValidityData(checkedType)
    }

}