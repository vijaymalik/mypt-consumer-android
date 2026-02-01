package co.com.mypt.fragments.CreatePackageScreen

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
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
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.Plans
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.CreatePackagectivity
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.adapter.ActivityAdapter
import co.com.mypt.fragments.adapter.CarouselAdapter
import co.com.mypt.fragments.adapter.CenterRaiseTransformer
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.BestPlanList
import co.com.mypt.model.BestPlanList.BestPlanData
import co.com.mypt.model.TrainerGroupDetail
import co.com.mypt.rulerHeight.SessionRulerViewHorizontal
import co.com.mypt.rulerHeight.onViewUpdateListenerWeight
import co.com.mypt.utils.SharedSessionvalueViewModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class BestPlanTotalSessionFragment(
    var type: String?,
    var slot_id: String?,
    var address_id: String?,
    var trainer_id: String?,
    var studio_id: String?,
    var month: Int?
) : Fragment() {
    var checkedType="Per_session"
    private val viewModel: SharedSessionvalueViewModel by activityViewModels()

//    lateinit var switchCompat: SwitchCompat
//    lateinit var tvTotalcost: TextView
//    lateinit var tvPerSession: TextView
//    lateinit var tvTrainer_name: TextView
//    lateinit var recycler:RecyclerView
    lateinit var tvRealPrice:TextView
    lateinit var validFor:TextView
    lateinit var totalSession:TextView
    lateinit var sessionSelected:TextView
    lateinit var tvAEDSession:TextView
//    lateinit var sessionValue : TextView
//    lateinit var imTrainer : ImageView
//    lateinit var greenLine : ImageView
//    lateinit var seekBar : SeekBar
//    lateinit var view1 : View
//    lateinit var view2 : View
//    lateinit var sessionValueCard : LinearLayout
//    lateinit var relaticeSave : RelativeLayout
    lateinit var endLayout : LinearLayout
//    lateinit var cardEdit : CardView
//    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()

    private lateinit var textSwitcher: TextSwitcher
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
    private var carouselRecycler: DiscreteScrollView?=null
    private var isBestPlanAvailable=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_best_plan_total_session, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())

//        greenLine=view.findViewById(R.id.greenLine)
//        relaticeSave=view.findViewById(R.id.relaticeSave)
//        recycler=view.findViewById(R.id.recycler)
//        tvTrainer_name=view.findViewById(R.id.tvTrainer_name)
        tvRealPrice=view.findViewById(R.id.tvRealPrice)
        tvAEDSession=view.findViewById(R.id.tvAEDSession)
//        switchCompat=view.findViewById(R.id.switchCompat)
//        tvTotalcost=view.findViewById(R.id.tvTotalcost)
//        tvPerSession=view.findViewById(R.id.tvPerSession)
//        view2=view.findViewById(R.id.view2)
//        view1=view.findViewById(R.id.view1)
//        imTrainer=view.findViewById(R.id.imTrainer)
//        seekBar=view.findViewById(R.id.seekBar)
//        sessionValue=view.findViewById(R.id.sessionValue)
//        sessionValueCard=view.findViewById(R.id.sessionValueCard)
        customization=view.findViewById(R.id.customization)
        bestPlan=view.findViewById(R.id.bestPlan)
//        cardEdit=view.findViewById(R.id.cardEdit)
        textSwitcher = view.findViewById(R.id.textSwitcher)
        customPlanParentView = view.findViewById(R.id.customPlanParentView)
        bestPlanParentView = view.findViewById(R.id.bestPlanParentView)
        carouselRecycler = view.findViewById(R.id.carouselView)
        validFor = view.findViewById(R.id.validFor)
//        totalSession = view.findViewById(R.id.totalSession)
        sessionSelected = view.findViewById(R.id.sessionSelected)

        /*seekBar.post {
            val seekBarWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight
            val thumbOffset = seekBar.thumbOffset

            // Calculate 80% position
            val valueRatio = 0.8f
            val positionX = seekBar.paddingLeft + valueRatio * seekBarWidth

            // Center the label horizontally
            relaticeSave.x = positionX - relaticeSave.width / 2
        }*/
        /*cardEdit.setOnClickListener{
            val intent = Intent(activity, TrainersListActivity::class.java)
            intent.putExtra("studio_id",studio_id)
            startActivity(intent)
        }*/
        textSwitcher.setFactory {
            val textView = TextView(context)
            textView.textSize = 40f
            textView.setTextColor(resources.getColor(R.color.headingcolor))
            textView.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.clash_display_medium))
            textView.gravity = Gravity.CENTER
            textView
        }
        bestPlan.setOnClickListener {
            selectTab(bestPlan, customization,bestPlanParentView,customPlanParentView)
            activity?.let {
                (it as CreatePackagectivity).selectedPlan(if (isBestPlanAvailable) Plans.IS_BEST_AVAILABLE else Plans.IS_BEST_NOT_AVAILABLE)
            }
        }

        customization.setOnClickListener {
            selectTab(customization, bestPlan,customPlanParentView,bestPlanParentView)
            activity?.let {
                (it as CreatePackagectivity).selectedPlan(Plans.CUSTOMIZE)

            }
        }

        val rulerWeight = view.findViewById<SessionRulerViewHorizontal>(R.id.ruler_session)

        rulerWeight.setUpdateListenerWeight(object : onViewUpdateListenerWeight {
            override fun onViewUpdate(value: Float) {
                val valueL = if (value>.1)value.minus(.1).toFloat() else value
                println("===== $value")
                val updatedValue=value.toInt()
                if(updatedValue == 0){
                    //seekBar!!.progress = 1
                    return
                }
                if(updatedValue > 1)
                    sessionSelected.text = "$updatedValue Sessions"
                else
                    sessionSelected.text = "$updatedValue Session"

                viewModel.data.value =updatedValue.toString()

                textShader(textSwitcher.currentView as TextView)



                // Determine the direction of the progress change (increase or decrease)
                var inAnimation: Animation? = null
                var outAnimation: Animation? = null

                if (updatedValue > lastProgress) {
                    // Slide from top to bottom (increasing)
                    inAnimation = createSlideInAnimation(1.0f, 0.0f) // from up to down
                    outAnimation = createSlideOutAnimation(0.0f, -1.0f) // slide out upward
                } else if (updatedValue < lastProgress) {
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
                lastProgress = updatedValue
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textSwitcher.setLayoutParams(layoutParams)

                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

                debounceRunnable = Runnable {
                    getSessionData(checkedType)
                }
                debounceHandler.postDelayed(debounceRunnable!!, 300)
            }
        })
        rulerWeight.setDefaultValue(21F)
        getSessionData(checkedType)
        //textSwitcher.setText("100")

        val startColor = Color.parseColor("#3DD772")
        val middleColor = Color.parseColor("#42AB98")
        val endColor = Color.parseColor("#4981F2")

        /*seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private val valueAnimator: ValueAnimator? = null

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    seekBar!!.progress = 1
                    return
                }
               *//* if(progress > 1)
                    sessionValue.text = "$progress Sessions"
                else
                    sessionValue.text = "$progress Session"*//*

                viewModel.data.value =progress.toString()

                val bounds: Rect = seekBar!!.thumb.bounds

                when (progress) {

                    100 -> {
                        sessionValueCard.x = (seekBar.left + bounds.left - 90).toFloat()
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
                greenLine.setColorFilter(gradientColor, PorterDuff.Mode.MULTIPLY);

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

                debounceRunnable = Runnable {
                    getSessionData(checkedType)
                }
                debounceHandler.postDelayed(debounceRunnable!!, 300)
                //getSessionData(checkedType)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                lastProgress = seekBar!!.progress
                lastDragTime = System.currentTimeMillis()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })*/
        /*switchCompat.setOnCheckedChangeListener { _, isChecked ->
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
            getSessionData(checkedType)
        }*/

        textShader(tvAEDSession)
        tvRealPrice.paintFlags = tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
      //  getSessionData()

        return view
    }
    private val snapHelper = PagerSnapHelper()

    fun initializeCarousel( data: List<BestPlanData?>) {
        carouselRecycler?.adapter = CarouselAdapter(data)

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
                    val position = rv.layoutManager?.getPosition(snapView!!)?:-1
                    if (position !=-1){

                        activity?.let {
                            (it as CreatePackagectivity).updateSelectedItem(data[position])
                        }

                    }


                }
            }
        })
        if (!data.isNullOrEmpty()){
            isBestPlanAvailable=true
            activity?.let {
                (it as CreatePackagectivity).selectedPlan(Plans.IS_BEST_AVAILABLE)
                (it as CreatePackagectivity).updateSelectedItem(data[0])
            }
        }else{
            isBestPlanAvailable=false
            activity?.let {
                (it as CreatePackagectivity).selectedPlan(Plans.IS_BEST_NOT_AVAILABLE)
            }
        }


//        carouselRecycler?.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//        carouselRecycler?.adapter = CarouselAdapter()
//        carouselRecycler?.itemAnimator = null

//        snapHelper.attachToRecyclerView(carouselRecycler)

        /*carouselRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val centerX = rv.width / 2

                for (i in 0 until rv.childCount) {
                    val child = rv.getChildAt(i) as MotionLayout

                    val childCenterX = (child.left + child.right) / 2
                    val distance = kotlin.math.abs(centerX - childCenterX)

                    val itemWidth = child.width
                    val progress = 1f - (distance.toFloat() / itemWidth)

                    child.progress = progress.coerceIn(0f, 1f)
                }
            }
        })*/
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

    fun getBestPlanApi(){
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        var type=sharedPreferences.getString("typeWorkout","")
        type=if (type == "home") "home" else "gym"
        val packageType=sharedPreferences.getInt("selectedPackageType",0)
        val api=ApiURL.getBestPlan+"package_type=$packageType&type=$type"
        GetMethod(api,activity).startMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                val data: BestPlanList = Gson().fromJson(data, BestPlanList::class.java)
                initializeCarousel(data?.data?:emptyList())
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
            }

        })
    }


    private fun getSessionData(checkedType: String) {
        count++
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.packagecreate+sharedPreferences.getInt("selectedPackageType",0)+"&sessions="+sessionSelected.text.toString().replace("Sessions","").replace(" Session","").trim()+"&type="+"home"+
                    "&trainer_id="+trainer_id+"&studio_id="+""+"&month="+month+"&address_id="+address_id
        }else{
            api= ApiURL.packagecreate+sharedPreferences.getInt("selectedPackageType",0)+"&sessions="+sessionSelected.text.toString().replace("Sessions","").replace("Session","").trim()+"&type="+"gym"+
                    "&trainer_id="+trainer_id+"&studio_id="+studio_id+"&month="+month+"&address_id="+address_id
        }
        Log.e("SessionPackageAPi",api)
        GetMethod(api,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("SessionPackageResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
//                        sharedPreferences.edit().putString("trainer_image",resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).apply()
//                        sharedPreferences.edit().putString("trainer_name",resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")).apply()
                        sharedPreferences.edit().putString("costType",checkedType).apply()
                        sharedPreferences.edit().putString("validity",resp.optJSONObject("data").optString("validity")).apply()
                        sharedPreferences.edit().putString("totalDays",resp.optJSONObject("data").optString("totalDays")).apply()
                        /*sharedPreferences.edit().putString("tagsArray",
                            resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags")
                                .toString()
                        ).apply()*/
//                        tvTrainer_name.text = resp.optJSONObject("data")!!.optJSONObject("trainer").optString("name")
                        var price=""
                        if (checkedType == "Per_session"){
                            textSwitcher.setText(resp.optJSONObject("data").optString("pricePerSession"))
                            price=resp.optJSONObject("data").optString("pricePerSession")
                            val validForTxt=resp.optJSONObject("data").optString("validity")
                            sharedPreferences.edit().putString("price",price).apply()
                            tvAEDSession.text = "AED / Session"
                            validFor.text="Valid for $validForTxt"

                        }else{
                            textSwitcher.setText(resp.optJSONObject("data").optString("totalPrice"))
                            price=resp.optJSONObject("data").optString("totalPrice")

                            sharedPreferences.edit().putString("price",price).apply()
                            tvAEDSession.text = "AED"

                        }
                        tvAEDSession.visibility = View.VISIBLE
                        textShader(textSwitcher.currentView as TextView)
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        textSwitcher.setLayoutParams(layoutParams)

//                        Glide.with(requireActivity()).load(resp.optJSONObject("data")!!.optJSONObject("trainer").optString("image")).fitCenter().into(imTrainer)
                        /*activitiesModelList.clear()
                        for(i in 0 until resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").length())
                        {
                            var jsonTags=resp.optJSONObject("data")!!.optJSONObject("trainer").optJSONArray("tags").optJSONObject(i)
                            var activityModel=ActivityModel()
                            activityModel.name=jsonTags.optString("name")
                            activitiesModelList.add(activityModel)
                        }*/
//                        var activityAdapter = ActivityAdapter(activity!!, activitiesModelList)
//                        recycler.adapter = activityAdapter
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
        if(isVisible) {
                getBestPlanApi()
            /*activity?.let {
                (it as CreatePackagectivity).selectedPlan(if (isBestPlanAvailable) Plans.IS_BEST_AVAILABLE else Plans.IS_BEST_NOT_AVAILABLE)
            }*/
            val currentTextView = textSwitcher.currentView as TextView
            lifecycleScope.launch {
                delay(100)
                //seekBar.progress = 1
                textShader(currentTextView)
            }
            //getSessionData(checkedType)
        }
    }
}