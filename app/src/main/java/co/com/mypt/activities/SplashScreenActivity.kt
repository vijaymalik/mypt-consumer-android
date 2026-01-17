package co.com.mypt.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.com.mypt.Api.Constants
import co.com.mypt.R
import co.com.mypt.onBoarding.PersonalizedActivity2
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import pl.droidsonroids.gif.GifImageView

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     /*   val imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }*/

        setContentView(R.layout.activity_splash)
        val gifImageView = findViewById<GifImageView>(R.id.gifImageView)
        gifImageView.setImageResource(R.drawable.sample_splash)

        val gifDrawable = pl.droidsonroids.gif.GifDrawable(
            resources, R.drawable.sample_splash
        )
        gifDrawable.loopCount = 1

        var hasStarted = false

        fun safeStartMain() {
            if (!hasStarted) {
                hasStarted = true
                startMainActivity()
            }
        }

        gifDrawable.addAnimationListener {
            Log.e("addAnimationListener","addAnimationListener")
            safeStartMain()
        }

        val fallbackTime = gifDrawable.duration + 2500 // extra buffer
        Handler(Looper.getMainLooper()).postDelayed({
            safeStartMain()
        }, fallbackTime.toLong())

        /*val totalDuration = gifDrawable.duration.toLong()
        Log.e("gifDrawable.loopCount","$totalDuration")

        Handler(Looper.getMainLooper()).postDelayed({
            startMainActivity()
        }, totalDuration+500)*/

        /*gifDrawable.addAnimationListener {
           startMainActivity()
        }*/

        /*Glide.with(this)
            .asGif()
            .load(R.drawable.sample_splash)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startMainActivity()
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.setLoopCount(1) // Play only once
                    resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            super.onAnimationEnd(drawable)
                            runOnUiThread {
                                startMainActivity()
                            }
                        }
                    })
                    resource?.start()
                    return false
                }
            })
            .into(imageView)*/

    }

    private fun startMainActivity() {
        val sharedPreferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim)
        if(sharedPreferences.getString(Constants.token,"-1") != "-1"
            || sharedPreferences.getString(Constants.token,"").toString() != ""){
            val intent= Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent, options.toBundle())
            //startActivity(intent)
        }else{
            startActivity(Intent(this@SplashScreenActivity, PhoneNumberScreenActivity::class.java),options.toBundle())
        }
        finish()
    }
}