package co.com.mypt.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import co.com.mypt.ComingSoonViewMode.Companion.FREE_ASSESSMENT
import co.com.mypt.ComingSoonViewMode.Companion.RENEW_PLAN
import co.com.mypt.ComingSoonViewMode.Companion.TOPU_UP
import co.com.mypt.ComingSoonViewMode.Companion.UPGRADE_PLAN
import co.com.mypt.R
import co.com.mypt.curvedBottomNavigation.dpToPx
import co.com.mypt.databinding.ActivityComingSoonBinding

class ComingSoonActivity : AppCompatActivity() {

    companion object {
        const val KEY_VIEW_MODE = "view_mode"
    }

    private lateinit var binding: ActivityComingSoonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComingSoonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoToHome.setOnClickListener { finish() }
        binding.back.setOnClickListener { finish() }

        val type = intent.getIntExtra(KEY_VIEW_MODE, FREE_ASSESSMENT)
        setComingSoonContent(type)
    }

    private fun setComingSoonContent(type: Int) {
        // Default values
        var imageRes = R.drawable.free_assessment_coming_soon
        var descText = ""
        var contentMargins = Margins(0, 10, 0, 10)
        var llTopMargin = 10
        var scaleType = ImageView.ScaleType.CENTER_CROP

        when (type) {
            FREE_ASSESSMENT -> {
                descText = "Your Body Assessment, \nBuilt for You"
            }

            TOPU_UP -> {
                imageRes = R.drawable.top_up_coming_soon
                descText = "More Sessions \nMore Progress"
                contentMargins = Margins(20, 20, 20, 20)
                llTopMargin = 30
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            RENEW_PLAN -> {
                imageRes = R.drawable.renew_plan_coming_soon
                descText = "Renew your plan easily "
                contentMargins = Margins(40, 50, 40, 0)
                llTopMargin = 50
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            UPGRADE_PLAN -> {
                imageRes = R.drawable.upgrade_plan_coming_soon
                descText = "Upgrade your plan easily "
                contentMargins = Margins(40, 50, 40, 0)
                llTopMargin = 50
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }

        // Apply content margins
        val contentParams =
            binding.content.layoutParams as ConstraintLayout.LayoutParams
        contentParams.setMargins(
            contentMargins.left.dpToPx(this),
            contentMargins.top.dpToPx(this),
            contentMargins.right.dpToPx(this),
            contentMargins.bottom.dpToPx(this)
        )
        binding.content.layoutParams = contentParams
        binding.content.setImageResource(imageRes)
        binding.content.scaleType = scaleType

        // Apply llComingSoon top margin
        val llParams =
            binding.llComingSoon.layoutParams as ConstraintLayout.LayoutParams
        llParams.topMargin = llTopMargin.dpToPx(this)
        binding.llComingSoon.layoutParams = llParams

        // Set description
        binding.tvDesc.text = descText
    }

    // Helper data class for margins
    private data class Margins(val left: Int, val top: Int, val right: Int, val bottom: Int)
}