package co.com.mypt.fragments.CreatePackageScreen

import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.model.ActivityModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray


class TotalMonthFragment(var studio_id: String?) : Fragment() {
    lateinit var switchCompat: SwitchCompat
    lateinit var tvTotalcost: TextView
    lateinit var tvValidityMsg: TextView
    lateinit var tvPerSession: TextView

    lateinit var recycler: RecyclerView
    lateinit var tvRealPrice:TextView
    lateinit var tvAEDSession:TextView
    lateinit var sessionValue : TextView
    lateinit var maxValue : TextView
    lateinit var tvTrainer_name : TextView
    lateinit var cardEdit : CardView
    lateinit var imtrainer : ImageView
    lateinit var seekBar : SeekBar
    lateinit var view1 : View
    lateinit var view2 : View
    lateinit var sessionValueCard : LinearLayout
    lateinit var relaticeSave : RelativeLayout
    lateinit var endLayout : LinearLayout
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()

    private lateinit var textSwitcher: TextSwitcher
    private var lastProgress = 0
    private var lastDragTime = System.currentTimeMillis()
    lateinit var sharedPreferences:SharedPreferences

    lateinit var greenLine : ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       var view=inflater.inflate(R.layout.fragment_total_month, container, false)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(requireActivity())
        greenLine=view.findViewById(R.id.greenLine)
        relaticeSave=view.findViewById(R.id.relaticeSave)
        recycler=view.findViewById(R.id.recycler)
        tvRealPrice=view.findViewById(R.id.tvRealPrice)
        tvAEDSession=view.findViewById(R.id.tvAEDSession)
        switchCompat=view.findViewById(R.id.switchCompat)
        tvTotalcost=view.findViewById(R.id.tvTotalcost)
        tvValidityMsg=view.findViewById(R.id.tvValidityMsg)
        tvPerSession=view.findViewById(R.id.tvPerSession)
        view2=view.findViewById(R.id.view2)
        view1=view.findViewById(R.id.view1)
        seekBar=view.findViewById(R.id.seekBar)
        maxValue=view.findViewById(R.id.maxValue)
        sessionValue=view.findViewById(R.id.sessionValue)
        sessionValueCard=view.findViewById(R.id.sessionValueCard)

        tvTrainer_name=view.findViewById(R.id.tvTrainer_name)
        cardEdit=view.findViewById(R.id.cardEdit)
        imtrainer=view.findViewById(R.id.imTrainer)
        textSwitcher = view.findViewById(R.id.textSwitcher)
        seekBar.post {
            val seekBarWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight
            val thumbOffset = seekBar.thumbOffset

            // Calculate 80% position
            val valueRatio = 0.8f
            val positionX = seekBar.paddingLeft + valueRatio * seekBarWidth

            // Center the label horizontally
            relaticeSave.x = positionX - relaticeSave.width / 2
        }
        cardEdit.setOnClickListener{
            val intent = Intent(activity, TrainersListActivity::class.java)
            intent.putExtra("studio_id",studio_id)
            startActivity(intent)
        }
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            tvValidityMsg.setText(R.string.the_validity_of_the_package_also_determines_the_access_period_for_mypt_gym)
        }else{
            tvValidityMsg.setText(R.string.the_validity_of_the_package_also_determines_the_access_period_for_mypt_gym_)

        }


        textSwitcher.setFactory {
            val textView = TextView(context)
            textView.textSize = 40f
            textView.setTextColor(resources.getColor(R.color.headingcolor))
            textView.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.clash_display_medium))
            textView.gravity = Gravity.CENTER
            textView
        }
        val currentTextView = textSwitcher.currentView as TextView
        //textSwitcher.setText("100")



        val startColor = Color.parseColor("#3DD772")
        val middleColor = Color.parseColor("#42AB98")
        val endColor = Color.parseColor("#4981F2")

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private val valueAnimator: ValueAnimator? = null

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
              //  sessionValue.text = if(progress > 1) {"$progress Months" } else {"$progress Month"}
                val bounds: Rect = seekBar!!.thumb.bounds

                /*when (progress) {
                    0 -> sessionValueCard.x = (seekBar.left + bounds.left).toFloat()
                    12 -> {
                        endLayout.visibility = View.VISIBLE
                        view1.visibility = View.INVISIBLE
                        view2.visibility = View.INVISIBLE
                        sessionValueCard.x = (seekBar.left + bounds.left - 40).toFloat()
                    }
                    else -> {
                        endLayout.visibility = View.GONE
                        view1.visibility = View.VISIBLE
                        view2.visibility = View.VISIBLE
                        sessionValueCard.x = (seekBar.left + bounds.left).toFloat()
                    }
                }*/

                if(progress > 300){
                    sessionValueCard.x = (seekBar.left+bounds.left - 80).toFloat()
                }else{
                    sessionValueCard.x = (seekBar.left + bounds.left-10).toFloat()
                }

                greenLine.x = (seekBar.left + bounds.left-5).toFloat()

                val maxProgress = seekBar.max ?: 366
                val gradientColor = calculateGradientColor(progress, maxProgress, startColor, middleColor, endColor)

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
                tvPerSession.setTextColor(requireActivity().resources.getColor(R.color.booked))
                tvTotalcost.setTextColor(requireActivity().resources.getColor(R.color.orangecolor))
            } else {
                // Handle switch OFF
                tvPerSession.setTextColor(requireActivity().resources.getColor(R.color.orangecolor))
                tvTotalcost.setTextColor(requireActivity().resources.getColor(R.color.booked))
            }
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

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            tvTrainer_name.text = sharedPreferences.getString("trainer_name","")
             Glide.with(requireActivity()).load(sharedPreferences.getString("trainer_image","")).fitCenter().into(imtrainer)
            if (sharedPreferences.getString("costType","").equals("Per_session")){
                tvPerSession.setTextColor(requireActivity().resources.getColor(R.color.orangecolor))
                tvTotalcost.setTextColor(requireActivity().resources.getColor(R.color.booked))
                switchCompat.isChecked=false
            }else{
                switchCompat.isChecked=true

                tvPerSession.setTextColor(requireActivity().resources.getColor(R.color.booked,null))
                tvTotalcost.setTextColor(requireActivity().resources.getColor(R.color.orangecolor,null))
            }
            sessionValue.text = sharedPreferences.getString("validity","")

            if(sharedPreferences.getString("validity","")?.contains("months") == true){
                maxValue.text = "12"
            }
            else
                maxValue.text = "365"

            textSwitcher.setText(sharedPreferences.getString("price",""))
            textShader(textSwitcher.currentView as TextView)

            val jsonArray=JSONArray(sharedPreferences.getString("tagsArray","[]"))
            activitiesModelList.clear()
            for(i in 0 until jsonArray.length())
            {
                var jsonTags=jsonArray.optJSONObject(i)
                var activityModel=ActivityModel()
                activityModel.name=jsonTags.optString("name")
                activitiesModelList.add(activityModel)
            }
            var activityAdapter = ActivityAdapter(requireActivity(), activitiesModelList)
            recycler.adapter = activityAdapter
            seekBar.setOnTouchListener { _, _ -> true }
            lifecycleScope.launch {
                delay(100)
                seekBar.progress = sharedPreferences.getString("totalDays","")?.toIntOrNull()?:0

            }
        }
    }
}