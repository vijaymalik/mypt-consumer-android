package co.com.mypt.ActiveSession

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.coroutineScope
import co.com.mypt.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProgressBarActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var textProgress: TextView
    private lateinit var progressLayout: LinearLayout
    private lateinit var topView: ImageView
    private lateinit var bottomView: ImageView
    private var progress = 0
    private var lastProgress = 0

    private lateinit var textSwitcher: TextSwitcher
    private val handler = Handler(Looper.getMainLooper())
    var booking_id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_progress_bar)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        booking_id=getIntent().getStringExtra("bookingid").toString()
        progressLayout = findViewById(R.id.progressLayout)
        bottomView = findViewById(R.id.bottomView)
        topView = findViewById(R.id.topView)
        progressBar = findViewById(R.id.progressBar)
        textProgress = findViewById(R.id.textProgress)
        textSwitcher = findViewById(R.id.textSwitcher)

        textSwitcher.setFactory {
            val textView = TextView(this)
            textView.textSize = 18f
            textView.setTextColor(resources.getColor(R.color.headingcolor,null))
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.clashdisplay_medium))
            textView.gravity = Gravity.CENTER
            textView
        }

        textSwitcher.inAnimation = createSlideInAnimation(1.0f, 0.0f)
        textSwitcher.outAnimation = createSlideOutAnimation(0.0f, -1.0f)

        val dropDown = AnimationUtils.loadAnimation(this, R.anim.slide_to_bottom)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_to_top)
        val fade = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (progress <= 100) {
                    progressBar.progress = progress
                    textProgress.text = "$progress"
                    textSwitcher.setText("$progress")
                    progress++
                    if(progress == 100){
                        lifecycle.coroutineScope.launch {
                            delay(1200)
                            val intent = Intent(this@ProgressBarActivity, ActiveSessionDetails::class.java)
                            intent.putExtra("bookingid",booking_id)
                            startActivity(intent)
                            finish()
                        }
                        topView.startAnimation(slideUp)
                        progressLayout.startAnimation(fade)
                        bottomView.startAnimation(dropDown)
                    }
                    handler.postDelayed(this, 100)
                }
            }
        }, 100)
    }

    private fun createSlideInAnimation(fromY: Float, toY: Float): Animation {
        val slideIn = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, fromY,
            Animation.RELATIVE_TO_SELF, toY
        )
        slideIn.duration = 100 // Adjust duration for smooth transition
        return slideIn
    }

    private fun createSlideOutAnimation(fromY: Float, toY: Float): Animation {
        val slideOut = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, fromY,
            Animation.RELATIVE_TO_SELF, toY
        )
        slideOut.duration = 100 // Adjust duration for smooth transition
        return slideOut
    }
}