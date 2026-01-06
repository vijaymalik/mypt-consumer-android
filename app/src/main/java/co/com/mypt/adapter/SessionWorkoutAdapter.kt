package co.com.mypt.adapter

import android.animation.TimeAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.ActiveessionWorkoutActivity
import co.com.mypt.model.SessionWorkoutModel
import co.com.mypt.utils.CenterLinearLayoutManager
import com.android.volley.VolleyError
import org.json.JSONObject
import kotlin.math.abs


class SessionWorkoutAdapter(
    var context_: ActiveessionWorkoutActivity,
    var sessionWorkoutList: ArrayList<SessionWorkoutModel>,
    var session_id: String
):
    RecyclerView.Adapter<SessionWorkoutAdapter.SessionHolder>() {
    var selectedIndex = 0
    lateinit var mAnimator: TimeAnimator
    var mCurrentLevel: Int = 0

    class SessionHolder (view:View):RecyclerView.ViewHolder(view){
        var userName=view.findViewById<TextView>(R.id.userName)
        var tvrestTime=view.findViewById<TextView>(R.id.tvrestTime)
        var swipeLayout=view.findViewById<LinearLayout>(R.id.swipeLayout)
        var imAddMinimumReps=view.findViewById<ImageView>(R.id.imAddMinimumReps)
        var tvMinimum=view.findViewById<TextView>(R.id.tvMinimum)
        var selectedValueTextView=view.findViewById<TextView>(R.id.selectedValueTextView)
        //var rulerview=view.findViewById<RulerView>(R.id.rulerView)
        val videoView : VideoView = view.findViewById(R.id.videoView)
        val cardSwipe : CardView = view.findViewById(R.id.cardSwipe)
        val fullCard30sLL : LinearLayout = view.findViewById(R.id.fullCard30sLL)
        var layerDrawable : LayerDrawable = fullCard30sLL.background as LayerDrawable
        var mClipDrawable:ClipDrawable =
            layerDrawable.findDrawableByLayerId(R.id.clip_drawable) as ClipDrawable
        val textView : TextView = view.findViewById(R.id.textView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.numberRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionHolder {
        val view = LayoutInflater.from(context_).inflate(R.layout.acttive_session_list, parent, false)
        return SessionHolder(view)
    }

    override fun getItemCount(): Int {
        return sessionWorkoutList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: SessionHolder, @SuppressLint("RecyclerView") position: Int) {
        var sessionHolder=sessionWorkoutList[position]


        holder.userName.text=sessionHolder.name
        holder.tvMinimum.text=sessionHolder.sets
        holder.selectedValueTextView.text=sessionHolder.reps
        //holder.rulerview.value=sessionHolder.reps.toInt()
        holder.selectedValueTextView.setEms(holder.selectedValueTextView.text.toString().length)

        //holder.rulerview.tag = position
        holder.videoView.tag = position
        holder.cardSwipe.tag = position
        val uri = Uri.parse(sessionHolder.video)
        holder.videoView.setVideoURI(uri)
        holder.videoView.setOnErrorListener { mp, what, extra ->
            Log.e("VideoPlayerError", "Error with ORIGINAL video. What: $what, Extra: $extra, Original URI: $uri")

            val fallbackVideoName = "gym_video"
            val fallbackVideoUri = Uri.parse("android.resource://${context_.packageName}/raw/$fallbackVideoName")

            Log.d("VideoPlayerFallback", "Attempting to play fallback video: $fallbackVideoUri")

            holder.videoView.setVideoURI(fallbackVideoUri)

            holder.videoView.setOnPreparedListener { fallbackMediaPlayer ->
                try {
                    Log.d("VideoPlayerFallback", "Fallback video prepared. Seeking and starting.")
                    fallbackMediaPlayer.seekTo(1)
                    if (selectedIndex == holder.bindingAdapterPosition && holder.videoView.isAttachedToWindow) {
                        fallbackMediaPlayer.start()
                        setVideoViewToLoop(holder.videoView)
                    }
                } catch (e: IllegalStateException) {
                    Log.e("VideoPlayerFallback", "IllegalStateException for fallback video in onPrepared", e)
                }
            }
            true
        }

        holder.videoView.setOnPreparedListener {
            holder.videoView.seekTo(1)
            if (selectedIndex == position) {
                holder.videoView.start()
                setVideoViewToLoop( holder.videoView)
            }
        }

        val numbers = (1..500).toList()

        val innerAdapter = InnerAdapter(numbers)
        holder.recyclerView.adapter = innerAdapter
        val layoutManager = CenterLinearLayoutManager(context_)
        holder.recyclerView.layoutManager = layoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(holder.recyclerView)
        holder.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                holder.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val recyclerWidth = holder.recyclerView.width
                val itemWidthPx = (100 * holder.recyclerView.resources.displayMetrics.density).toInt()
                val padding = (recyclerWidth / 2) - (itemWidthPx / 2)
                holder.recyclerView.setPadding(padding, 0, padding, 0)
                holder.recyclerView.clipToPadding = false
                // Scroll to initialPosition with offset = padding
                layoutManager.scrollToPositionWithOffsetAfterLayout(sessionHolder.reps.toInt()-1, padding)
                innerAdapter.updateCenteredPosition(sessionHolder.reps.toInt()-1)
            }
        })
        layoutManager.scrollToPositionWithOffsetAfterLayout(sessionHolder.reps.toInt()-1, (20* holder.recyclerView.resources.displayMetrics.density).toInt())
        innerAdapter.updateCenteredPosition(sessionHolder.reps.toInt()-1)

        holder.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val recyclerCenterX = recyclerView.width / 2f
                val fadeDistance = recyclerCenterX // max distance for fading effect

                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    val childCenterX = (child.left + child.right) / 2f
                    val distanceFromCenter = abs(recyclerCenterX - childCenterX)

                    // Calculate alpha: 1.0 at center, down to 0.3 at max distance
                    val alpha = 1f - (distanceFromCenter / fadeDistance) * 0.85f
                    child.alpha = alpha.coerceIn(0.2f, 1f)
                }
            }
        })

        holder.cardSwipe.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var lastAction = 0

            private val maxX: Float
                get() = holder.swipeLayout.width - holder.cardSwipe.width.toFloat()

            private fun updateTextColor(x: Float) {
                val colorRes = if (x > 0) R.color.swipe_textcolor else R.color.white
                holder.textView.setTextColor(ContextCompat.getColor(context_, colorRes))
            }

            private fun resetSwipePosition(v: View) {
                v.animate()
                    .translationX(0f)
                    .setDuration(300)
                    .withEndAction {
                        v.visibility = View.VISIBLE
                        holder.textView.visibility = View.VISIBLE
                        holder.textView.setTextColor(ContextCompat.getColor(context_, R.color.white))
                    }
                    .start()
            }

            private fun startRestAnimation(restDuration: Long) {
                holder.swipeLayout.visibility = View.GONE
                val LEVEL_INCREMENT = 8
                val MAX_LEVEL = 10000
                mAnimator = TimeAnimator()
                mAnimator.setTimeListener { _, totalTime, _ ->
                    val fraction = (totalTime.toFloat() / (restDuration * 1000)).coerceAtMost(1f)
                    mCurrentLevel = (fraction * MAX_LEVEL).toInt()
                    holder.mClipDrawable.level = mCurrentLevel

                    holder.tvrestTime.text = "Rest ${sessionHolder.totalRest}s"

                    val intentStart = Intent("start30s").apply { putExtra("type", "start") }
                    context_.sendBroadcast(intentStart)

                    if (fraction >= 1f) {
                        mAnimator.cancel()
                        val selectedIndex = holder.cardSwipe.tag as Int
                        /*val intentComplete = Intent("exerciseComplete").apply { putExtra("position", selectedIndex) }
                        context_.sendBroadcast(intentComplete)*/
                        (context_).onCompleteExercise(selectedIndex)

                        val intentEnd = Intent("start30s").apply { putExtra("type", "end") }
                        context_.sendBroadcast(intentEnd)
                    }
                    /*else {
                        mCurrentLevel = min(MAX_LEVEL, mCurrentLevel + LEVEL_INCREMENT)
                    }*/
                }
                if (!mAnimator.isRunning) {
                    mCurrentLevel = 0
                    mAnimator.start()
                } else {
                    mAnimator.cancel()
                }
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        lastAction = MotionEvent.ACTION_DOWN
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX + dX
                        if (newX in 0f..maxX) {
                            v.animate()
                                .x(newX)
                                .setDuration(0)
                                .start()
                            updateTextColor(newX)
                        }
                        lastAction = MotionEvent.ACTION_MOVE
                    }
                    MotionEvent.ACTION_UP -> {
                        val newX = v.x
                        if (newX >= maxX - 30) {
                            completeExercise(session_id, sessionHolder.workout_exercise_id, sessionHolder.set_round)
                            if (sessionHolder.totalRest == "0") {
                                (context_).handleRestTime()
                                return true
                            }
                            v.animate()
                                .translationX(maxX)
                                .withEndAction { startRestAnimation(sessionHolder.totalRest.toLong()) }
                                .start()
                        } else {
                            resetSwipePosition(v)
                        }
                        lastAction = MotionEvent.ACTION_UP
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        lastAction = MotionEvent.ACTION_CANCEL
                    }
                    else -> return false
                }
                return true
            }
        })

        if(selectedIndex == position){
            holder.videoView.start()
            setVideoViewToLoop(holder.videoView)
        }else{
            holder.videoView.stopPlayback()
        }


        holder.imAddMinimumReps.setOnClickListener {
            return@setOnClickListener
            var count = holder.tvMinimum.text.toString().toInt()
            count += 1
            holder.tvMinimum.text = "$count"
            holder.tvMinimum.requestLayout()
            holder.tvMinimum.invalidate()
        }

        /*holder.rulerview.onRulerValueChangeListener = object : OnRulerValueChangeListener {
            override fun onRulerValueChanged(value: Int, displayValue: String) {
                holder.selectedValueTextView.text = displayValue
                holder.selectedValueTextView.setEms(holder.selectedValueTextView.text.toString().length)
                //holder.selectedValueTextView.requestLayout()
            }
        }*/
    }

    private fun setVideoViewToLoop(Videoview : VideoView){
        Videoview.setOnCompletionListener {
            Videoview.start()
        }
    }
    override fun getItemViewType(position: Int): Int {
        // Return a unique view type for each item return position
        return  position
    }

    class InnerAdapter(
        private val items: List<Int>
    ) : RecyclerView.Adapter<InnerAdapter.InnerViewHolder>() {

        var centeredPosition = RecyclerView.NO_POSITION

        inner class InnerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.numberText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.ruler_adapter, parent, false)
            return InnerViewHolder(view)
        }

        override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
            holder.textView.text = items[position].toString()

            if (position == centeredPosition) {
                holder.view.scaleX = 1.2f
                holder.view.scaleY = 1.2f
                holder.textView.setTextColor(Color.WHITE)
                holder.textView.textSize = 36f
            } else {
                holder.view.scaleX = 1f
                holder.view.scaleY = 1f
                holder.textView.setTextColor("#494C4D".toColorInt())
                holder.textView.textSize = 20f
            }
        }

        override fun getItemCount(): Int = items.size

        fun updateCenteredPosition(position: Int) {
            centeredPosition = position
            notifyItemChanged(centeredPosition)
        }
    }
    private fun completeExercise(session_id1: String, id: String, setRound: String) {
        val param: MutableMap<String, String> = HashMap()
        param["session_id"] =""+session_id1
        param["workout_exercise_id"] = id
        param["set_round"] = setRound
        Log.e("completeExerciseParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(context_,"")
        progressDialog.show()

        PostMethod(ApiURL.exercisecomplete,param, context_).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("completeExerciseRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        context_.progressValueUpdate(resp.optJSONObject("data").optInt("percentage"))
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
}


