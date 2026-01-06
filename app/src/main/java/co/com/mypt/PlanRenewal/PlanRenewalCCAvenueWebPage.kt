package co.com.mypt.PlanRenewal

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
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import com.android.volley.VolleyError

class PlanRenewalCCAvenueWebPage : AppCompatActivity() {
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
                                        val intent = Intent(this@PlanRenewalCCAvenueWebPage, CommonBookingConfirmActivity::class.java)
                                        intent.putExtra("id",getIntent().getStringExtra("id").toString())
                                        intent.putExtra("sessions",getIntent().getStringExtra("sessions").toString())
                                        intent.putExtra("days",getIntent().getStringExtra("days").toString())
                                        intent.putExtra("price",getIntent().getStringExtra("price").toString())
                                        intent.putExtra("type",getIntent().getStringExtra("type").toString())
                                        intent.putExtra("subscriptionType",getIntent().getStringExtra("subscriptionType").toString())
                                        intent.putExtra("transaction_id",id)
                                        intent.putExtra("selectedPaymentOption","ccavenue")
                                        startActivity(intent)
                                        return true
                                    }
                                    it.contains("payment/cancelled", ignoreCase = true) -> {
                                        val builder = AlertDialog.Builder(this@PlanRenewalCCAvenueWebPage)
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