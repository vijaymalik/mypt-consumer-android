package co.com.mypt.Webview

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.CreatePackagePaymentStatusActivity
import co.com.mypt.activities.GymMembershipPaymentStatusActivity
import co.com.mypt.model.PackageDetails
import co.com.mypt.model.PaymentResponse
import co.com.mypt.model.PaymentSessionResponse
import com.android.volley.VolleyError
import com.google.gson.Gson

class CreatePackageCCavenueWebViewActivity : AppCompatActivity() {
    private var isGymMembershipFlow = false
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ccavenue_web_view)
        webView = findViewById(R.id.web)
        getCcavenueWeb()

    }

    private fun getCcavenueWeb() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.show()
        val packageDetails = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("package_data", PackageDetails::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<PackageDetails>("package_data")
        }
        val params = mutableMapOf<String, String>()

        packageDetails?.let { it ->
            isGymMembershipFlow = intent.getBooleanExtra(Constants.IS_GYM_MEMBERSHIP_FLOW, false)

            params["gateway"] = "ccavenue"
            params["amount"] = it.price?.toString().orEmpty()
            params["payment_for"] = "subscription"
            params["type"] = it.type?.lowercase().orEmpty()
            params["package_type"] = it.package_type.orEmpty()
            params["best_plan_id"] = it.best_plan_id.orEmpty()
            params["offer_id"] = it.applied_offer_id?.toString().orEmpty()
            params["validity_days"] = it.validity_days?.toString().orEmpty()

            if (isGymMembershipFlow) {
                params["studio_id"] = it.studio_id.orEmpty()
            } else {
                params["sessions"] = it.sessions.orEmpty()
                params["bonus_sessions"] = it.bonus_sessions?.toString().orEmpty()
            }
        }

        PostMethod(ApiURL.paymentSession, params, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("payCcavenueResponse", data.toString())
                try {
                    val paymentSessionResponse = Gson().fromJson(
                        data.toString(),
                        PaymentSessionResponse::class.java
                    )

//                        val jsonObj = JSONObject(data!!)
                    webView.settings.javaScriptEnabled = true
                    webView.webChromeClient = WebChromeClient()

                    val htmlData = paymentSessionResponse.data.html
                    webView.loadDataWithBaseURL(
                        null,
                        htmlData,
                        "text/html",
                        "UTF-8",
                        null
                    )
                    webView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            Log.e("url", "" + url)
                            url?.let {
                                if (it.contains(
                                        "payment/success",
                                        ignoreCase = true
                                    ) || it.contains("payment/failure", ignoreCase = true)
                                ) {
                                    verifyPayment(paymentSessionResponse.data.payment_id)
                                }
                                /*when {
                                    it.contains("payment/success", ignoreCase = true) -> {
                                        var id = url.substringAfterLast("/")
                                        var intent = Intent(this@CCavenueWebViewActivity, BookingConfirmActivity::class.java)
                                        try {
                                            if(getIntent().getStringExtra("booking_id") != ""){
                                                intent = Intent(this@CCavenueWebViewActivity,
                                                    NotificationBookingConfirmActivity::class.java)
                                                intent.putExtra("booking_id",getIntent().getStringExtra("booking_id"))
                                                intent.putExtra("price",getIntent().getStringExtra("booking_id"))
                                                intent.putExtra("transaction_id",getIntent().getStringExtra("transaction_id"))
                                                intent.putExtra("payment_type",getIntent().getStringExtra("payment_type"))
                                                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))

                                            }
                                        }catch (e: Exception){
                                            e.printStackTrace()
                                            if (getIntent().getStringExtra("selectBookOption").equals("normalBookSlot")){
                                                intent.putExtra("type",getIntent().getStringExtra("type"))
                                                intent.putExtra("selectBookOption","normalBookSlot")
                                                intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
                                                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                                                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                                                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                                intent.putExtra("transaction_id",id)
                                                intent.putExtra("selectedPaymentOption","ccavenue")
                                            }
                                            else{
                                                intent.putExtra("selectBookOption","BookCreatedPackage")
                                                intent.putExtra("type",getIntent().getStringExtra("type"))
                                                intent.putExtra("slot_id",getIntent().getStringExtra("slot_id"))
                                                intent.putExtra("address_id",getIntent().getStringExtra("address_id"))
                                                intent.putExtra("trainer_id",getIntent().getStringExtra("trainer_id"))
                                                intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                                intent.putExtra("session_value",getIntent().getStringExtra("session_value"))
                                                intent.putExtra("days",getIntent().getStringExtra("days"))
                                                intent.putExtra("price",getIntent().getStringExtra("price"))
                                                intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
                                                intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
                                                intent.putExtra("transaction_id",id)
                                                intent.putExtra("selectedPaymentOption","ccavenue")

                                            }
                                        }

                                        startActivity(intent)
                                        return true
                                    }
                                    it.contains("payment/cancelled", ignoreCase = true) -> {
                                        val builder = AlertDialog.Builder(this@CCavenueWebViewActivity)
                                        builder.setMessage(R.string.TransactionFailed)
                                        builder.setIcon(R.drawable.app_icon)

                                        //performing positive action
                                        builder.setPositiveButton("OK"){dialogInterface, which ->
                                            finish()
                                            dialogInterface.dismiss()
                                        }

                                        // Create the AlertDialog
                                        val alertDialog: AlertDialog = builder.create()
                                        // Set other dialog properties
                                        alertDialog.setCancelable(false)
                                        alertDialog.show()
                                        // Toast.makeText(applicationContext,"Payment failed",Toast.LENGTH_LONG).show()
                                        // finish()
                                        return true
                                    }

                                    else -> {

                                    }
                                }*/
                            }
                            return false // Load normally
                        }

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            Log.e("onPageStarted", "$url");
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }
        })
    }

    private fun verifyPayment(paymentId: Int) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this, "")
        progressDialog.show()
        val params = mutableMapOf<String, String>()
        params["payment_id"] = paymentId.toString()
        PostMethod(ApiURL.verifyPayment, params, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    val paymentResponse = Gson().fromJson(
                        data.toString(),
                        PaymentResponse::class.java

                    )
                    moveToPaymentStatusScreen(paymentResponse)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun error(error: VolleyError?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun moveToPaymentStatusScreen(paymentResponse: PaymentResponse) {
        val targetActivity = if (isGymMembershipFlow) {
            GymMembershipPaymentStatusActivity::class.java
        } else {
            CreatePackagePaymentStatusActivity::class.java
        }
        val intent = Intent(this, targetActivity)
        intent.putExtra("payment_response", paymentResponse)
        startActivity(intent)
        finish()
    }
}

