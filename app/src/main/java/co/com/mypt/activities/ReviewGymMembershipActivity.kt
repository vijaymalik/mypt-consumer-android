package co.com.mypt.activities


import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.Constants.BEST_PLAN_ID
import co.com.mypt.Api.Constants.REVIEW_ADDRESS_ID
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.Webview.CreatePackageCCavenueWebViewActivity
import co.com.mypt.databinding.ActivityReviewGymMembershipBinding
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.AvailablePromo
import co.com.mypt.model.JoinModel
import co.com.mypt.model.PackageDetails
import co.com.mypt.model.ReviewPackageCheckout
import co.com.mypt.model.ReviewPackageCheckout.Data.UpgradePlan
import com.android.volley.VolleyError
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


class ReviewGymMembershipActivity : AppCompatActivity() {

    var activitiesModelList: ArrayList<ActivityModel> = ArrayList()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    var updatedAddress = ""

    //    lateinit var nested:NestedScrollView
    var membersList = ArrayList<JoinModel>()
    var tax_rate = ""
    var main_price = ""
    var selectedCouponId: String? = ""
    var upgradIdText: UpgradePlan? = null
    var available_promos: List<AvailablePromo?>? = null
    var isupgreadClick = false
    var selectedAddressId = ""
    private var progressDialog: Dialog? = null

    private var packageDetails: PackageDetails? = null
    private var isPaymentChecked = false
    lateinit var binding: ActivityReviewGymMembershipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewGymMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        edit = sharedPreferences.edit()
        continueButtonClick(false)
        binding.viewAllCoupon.setOnClickListener {
            val intent = Intent(this, ViewCouponsOfferActivity::class.java)
            intent.putParcelableArrayListExtra(
                "couponList",
                available_promos as java.util.ArrayList<out Parcelable?>?
            )
            resultLauncher.launch(intent)
        }
        binding.appliedCoupon.visibility = View.GONE
        binding.back1.setOnClickListener {
            finish()
        }

        binding.rlCCAvenue.setOnClickListener {
            isPaymentChecked = !isPaymentChecked
            continueButtonClick(isPaymentChecked)
            binding.ccvenueRadio.setImageResource(if (isPaymentChecked) R.drawable.radio_select_light_green else R.drawable.radio_unselect)
        }

        binding.tvPayment.setOnClickListener {
            if (!binding.tvPayment.isEnabled) return@setOnClickListener
            val intent = Intent(this, CreatePackageCCavenueWebViewActivity::class.java)

            intent.putExtra("package_data", packageDetails)
            intent.putExtra(Constants.IS_GYM_MEMBERSHIP_FLOW, true)

            if (sharedPreferences.getString("typeWorkout", "").equals("home")) {
                intent.putExtra("type", sharedPreferences.getString("typeWorkout", "").toString())
            } else {
                intent.putExtra("type", "gym")
                intent.putExtra("studio_id", intent.getStringExtra("studio_id").toString())
            }
            val finalAddressId = if (updatedAddress != "")
                updatedAddress
            else
                sharedPreferences.getString(REVIEW_ADDRESS_ID, "").toString()

            intent.putExtra("address_id", finalAddressId)
            val best_plan_id_Final = if (isupgreadClick) {
                upgradIdText?.id ?: ""
            } else {
                intent.getStringExtra(BEST_PLAN_ID).toString()
            }
            intent.putExtra("address_id", best_plan_id_Final)

            intent.putExtra(
                "package_type",
                sharedPreferences.getInt("selectedPackageType", 0).toString()
            )
            intent.putExtra("session_value", intent.getStringExtra("session_value").toString())
            intent.putExtra("trainer_id", intent.getStringExtra("trainer_id").toString())
//        param["skip_offer"] = "true"
            intent.putExtra("offer_id", selectedCouponId ?: "")

            startActivity(intent)
        }

        getData()
    }

    fun continueButtonClick(isEnable: Boolean) {
        binding.tvPayment.isEnabled = isEnable
        if (isEnable) {
            binding.tvPayment.setTextColor(resources.getColor(R.color.buttontextcolor, null))
            binding.tvPayment.background =
                ContextCompat.getDrawable(this, R.drawable.white_rectangle)
        } else {
            binding.tvPayment.setTextColor(resources.getColor(R.color.white, null))
            binding.tvPayment.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_btn)
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            selectedCouponId = data?.getStringExtra("couponId")
            binding.offerTitle.text = data?.getStringExtra("couponName")
            binding.appliedCoupon.visibility = View.VISIBLE
            binding.applyCoupon.visibility = View.GONE
            getData()
        }
    }

    fun getData() {
        showProgress()

        val param: MutableMap<String, String> = HashMap()
        param["type"] = "gym"
        param["studio_id"] = intent.getStringExtra("studio_id") ?: ""
        param["days"] = ""+intent.getIntExtra("days",0)
        val bestPlanId = intent.getStringExtra(BEST_PLAN_ID)
        if (bestPlanId != null)
            param["best_plan_id"] = bestPlanId

        param["package_type"] = ""+intent.getIntExtra("package_type",4)
        if (!selectedCouponId.isNullOrEmpty())
            param["offer_id"] = selectedCouponId ?: ""

        Log.e("PackageCheckoutParam", param.toString())

        PostMethod(ApiURL.reviewPackageCheckout, param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                hideProgress()

                try {
                    Log.e("PackageCheckoutRes", data.toString())

                    val data: ReviewPackageCheckout =
                        Gson().fromJson(data, ReviewPackageCheckout::class.java)
                    if (data.status == true) {
                        packageDetails = data.data?.package_details
                        available_promos = data.data?.available_promos
                        available_promos?.let {
                            if (it.isNotEmpty()) {
                                binding.offerTitle.text = it.firstOrNull()?.name ?: "Select Coupon"
                            }
                        }
                        binding.tvSelectedGym.text = data.data?.studio?.name?:""
                        binding.tvSelectedPlan.text = packageDetails?.package_name?:""
                        packageDetails?.start_date?.let { binding.tvStartDate.text = convertDateFormat(it) }
                        packageDetails?.end_date?.let { binding.tvEndDate.text = convertDateFormat(it) }
                        binding.packagePrice.text = "AED ${packageDetails?.main_price}"
                        binding.paymentTimeMsg.text = data.data?.payment_msg
//                        if (data.data?.upgrade_plan != null) {
//                            binding.title.text = data.data.upgrade_plan.title
//                            binding.subTitle.text =
//                                "Avail at ${data.data.upgrade_plan.badge_text} for ${data.data.upgrade_plan.sessions} sessions"
//                        } else {
//                            binding.upgradePlanView.visibility = View.GONE
//                        }
//                        binding.mode.text = "${data.data?.package_details?.type} Training"
//                        binding.typeWorkOut.text = "${data.data?.package_details?.type} "
//                        binding.tvSession.text = "${data.data?.package_details?.sessions}"
//                        binding.validity.text = "${data.data?.package_details?.validity}"
//                        binding.packagePrice.text = "AED ${data.data?.package_details?.price}"
//                        binding.tvSessionMain.text =
//                            "AED ${data.data?.package_details?.price_per_session}/session"
//                        binding.trainerName.text =
//                            "${data.data?.trainer_detail?.primary_trainer?.name}"
//                        binding.selectedGym.text = "${data.data?.studio?.name}"
//                        if (data.data?.package_details?.type == "Home") {
//                            binding.home.text = "Home"
//                            binding.address.text =
//                                "${data.data?.address?.building_name},${data.data?.address?.street},${data.data?.address?.landmark},${data.data?.address?.city_name},${data.data?.address?.country_name}"
//                            selectedAddressId = data.data?.address?.id.toString()
//                        } else {
//                            binding.home.text = "Gym"
//                            binding.address.text = "${data.data?.studio?.address}"
//                        }
                        binding.paymentTimeMsg.text = "${data.data?.payment_msg}"
                    } else {
                        Toast.makeText(
                            this@ReviewGymMembershipActivity,
                            "Package not available.",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.tvPayment.isClickable = false
                        binding.tvPayment.background =
                            resources.getDrawable(R.drawable.grey_rectangle, null)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                hideProgress()
                error!!.printStackTrace()
            }

        })
    }

    private fun showProgress() {
        if (!isFinishing && !isDestroyed) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.progressDialog(this, "")
            }

            if (progressDialog?.isShowing == false) {
                progressDialog?.show()
            }
        }
    }

    private fun hideProgress() {
        if (!isFinishing && !isDestroyed) {
            progressDialog?.dismiss()
        }
    }

    fun convertDateFormat(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }
}