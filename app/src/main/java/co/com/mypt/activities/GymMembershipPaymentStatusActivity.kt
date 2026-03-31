package co.com.mypt.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import co.com.mypt.R
import co.com.mypt.model.PaymentResponse
import java.text.SimpleDateFormat
import java.util.Locale

class GymMembershipPaymentStatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_membership_payment_status)
        val paymentDetails = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("payment_response", PaymentResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<PaymentResponse>("payment_response")
        }

        val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        val tvPlanName = findViewById<TextView>(R.id.tvPlanName)
        val tvValidity = findViewById<TextView>(R.id.tvValidity)
        val tvStartDate = findViewById<TextView>(R.id.tvStartDate)
        val tvEndDate = findViewById<TextView>(R.id.tvEndDate)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitle)
        val paymentCard = findViewById<RelativeLayout>(R.id.paymentCard)
        val tvReasonLabel = findViewById<TextView>(R.id.tvReasonLabel)
        val tvReason = findViewById<TextView>(R.id.tvReason)
        val tvFailureNote = findViewById<TextView>(R.id.tvFailureNote)
        val planCard = findViewById<LinearLayout>(R.id.planCard)
        val imgSuccess = findViewById<ImageView>(R.id.imgSuccess)

        val llSuccessBtmlay = findViewById<LinearLayout>(R.id.llSuccessBtmlay)
        val llFailureBtmlay = findViewById<LinearLayout>(R.id.llFailureBtmlay)
        val llGotoHome = findViewById<LinearLayout>(R.id.llGoHomePage)
        val llRetry = findViewById<LinearLayout>(R.id.llRetry)
        val llChangePaymentMethod = findViewById<LinearLayout>(R.id.llChangePaymentMethod)
        val llGoBackReview = findViewById<LinearLayout>(R.id.llGoBackReview)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        llRetry.setOnClickListener { goToReviewScreen() }
        llGoBackReview.setOnClickListener { goToReviewScreen() }
        llChangePaymentMethod.setOnClickListener { goToReviewScreen() }

        btnBack.setOnClickListener {
            if (paymentDetails?.data?.is_success == true) {
                goToHomePage()
            } else {
                finish()
            }
        }
        llGotoHome.setOnClickListener {
            goToHomePage()
        }

        paymentDetails?.let { response ->

            val data = response.data
            val plan = data.plan_details
            val isSuccess = data.is_success ?: false

            val topMarginDp = if (isSuccess) 12 else 20
            planCard.setTopMargin(topMarginDp)
            plan?.let {
                tvPlanName.text = it.plan_name.toString()
                tvValidity.text = it.validity_display
                it.start_date?.let { input -> tvStartDate.text = convertDateFormat(input) }
                it.end_date?.let { input -> tvEndDate.text = convertDateFormat(input) }
            }
            planCard.setBackgroundResource(
                if (isSuccess) R.drawable.black_background
                else R.drawable.details_gradient
            )

            imgSuccess.setImageResource(
                if (isSuccess) R.drawable.successicon
                else R.drawable.failure_icon
            )

            mainLayout.setBackgroundResource(if (isSuccess) R.drawable.success_background else R.drawable.error_background)

            paymentCard.visibility = if (isSuccess) View.VISIBLE else View.GONE
            llSuccessBtmlay.visibility = if (isSuccess) View.VISIBLE else View.GONE
            llFailureBtmlay.visibility = if (isSuccess) View.GONE else View.VISIBLE

            val failureVisibility = if (isSuccess) View.GONE else View.VISIBLE
            tvReasonLabel.visibility = failureVisibility
            tvReason.visibility = failureVisibility
            tvFailureNote.visibility = failureVisibility

            tvTitle.text = getString(
                if (isSuccess) R.string.payment_success
                else R.string.payment_failed
            )

            tvSubtitle.text = getString(
                if (isSuccess) R.string.your_membership_plan_is_active
                else R.string.payment_not_charged
            )

            if (!isSuccess) {
                tvReason.text = plan?.failure_reason.orEmpty()
                tvFailureNote.text = plan?.failure_note.orEmpty()
            }
        }
    }

    private fun goToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToReviewScreen() {
        finish()
    }

    fun View.setTopMargin(dp: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        val px = (dp * resources.displayMetrics.density).toInt()
        params.topMargin = px
        layoutParams = params
    }

    fun convertDateFormat(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }
}