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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.BookMemberShipActivity
import co.com.mypt.activities.BookingConfirmActivity
import com.android.volley.VolleyError

class MembershipCCavenueWebActivity : AppCompatActivity() {
    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_membership_ccavenue_web)
        webView=findViewById(R.id.web)
        getCcavenueWeb()
    }
    private fun getCcavenueWeb() {

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        var  api= ApiURL.payamount+intent.getStringExtra("total_price")
        // var  api= ApiURL.payamount+"1"

        Log.e("payMemberCcavenueDataApi",""+api)

        GetMethod(api,this).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("payMemberCcavenueResponse",data.toString())
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
                                        val intent = Intent(this@MembershipCCavenueWebActivity, BookMemberShipActivity::class.java)
                                        intent.putExtra("studio_id",getIntent().getStringExtra("studio_id"))
                                        intent.putExtra("days",getIntent().getStringExtra("days"))
                                        intent.putExtra("apistart_date",getIntent().getStringExtra("apistart_date"))
                                        intent.putExtra("apiend_date",getIntent().getStringExtra("apiend_date"))
                                        intent.putExtra("tax_rate",getIntent().getStringExtra("tax_rate"))
                                        intent.putExtra("main_price",getIntent().getStringExtra("main_price"))
                                        intent.putExtra("total_price",getIntent().getStringExtra("total_price"))
                                        intent.putExtra("selectedPaymentOption","ccavenue")
                                        intent.putExtra("transaction_id",id)

                                        startActivity(intent)

                                        startActivity(intent)
                                        return true
                                    }
                                    it.contains("payment/cancelled", ignoreCase = true) -> {
                                        val builder = AlertDialog.Builder(this@MembershipCCavenueWebActivity)
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