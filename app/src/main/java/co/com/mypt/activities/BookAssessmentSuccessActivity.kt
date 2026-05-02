package co.com.mypt.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.R
import co.com.mypt.databinding.ActivityBookAssessmentSuccessBinding
import co.com.mypt.fragments.QuickBookReviewBottomSheet.Companion.KEY_REVIEW_ASSESSMENT_RESPONSE
import co.com.mypt.model.ReviewAssessmentResponse

class BookAssessmentSuccessActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookAssessmentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookAssessmentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnViewBooking.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.KEY_TAB, R.id.bookings)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        val response = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                KEY_REVIEW_ASSESSMENT_RESPONSE,
                ReviewAssessmentResponse::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ReviewAssessmentResponse>(KEY_REVIEW_ASSESSMENT_RESPONSE)
        }

        val timing = response?.data?.timing

        val startTime = timing?.substringBefore("-")?.trim()
        val amPm = timing?.substringAfter(" ")?.trim()

        val formattedTime = "$startTime $amPm"
        binding.tvTrainerName.text = response?.data?.trainer_name
        binding.tvDate.text = response?.data?.date
        binding.tvTime.text = formattedTime
    }
}