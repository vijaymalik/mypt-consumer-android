package co.com.mypt.Webview

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.Notification.NotificationBookingConfirmActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.BookingConfirmActivity
import com.android.volley.VolleyError

class CCavenueWebViewActivity : AppCompatActivity() {
    lateinit var webView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ccavenue_web_view)
        webView=findViewById(R.id.web)
        getCcavenueWeb()


    }
    private fun getCcavenueWeb() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        val api = ApiURL.payamount+intent.getStringExtra("price")
        Log.e("payCcavenueDataApi",""+api)
        GetMethod(api,this).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("payCcavenueResponse",data.toString())
                try {

//                        val jsonObj = JSONObject(data!!)
                    webView.settings.javaScriptEnabled = true
                    webView.webChromeClient = WebChromeClient()

                    val htmlData = data
                    webView.loadDataWithBaseURL(
                        null,
                        htmlData.toString(),
                        "text/html",
                        "UTF-8",
                        null
                    )
                    webView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            Log.e("url",""+url)
                            url?.let {
                                when {
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
                                }
                            }
                            return false // Load normally
                        }

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            Log.e("onPageStarted","$url");
                        }
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

