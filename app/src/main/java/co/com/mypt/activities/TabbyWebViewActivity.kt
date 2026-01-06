package co.com.mypt.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R

class TabbyWebViewActivity : AppCompatActivity() {
    lateinit var headerLayout:LinearLayout
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabby_web_view)
        webView=findViewById(R.id.web)
        headerLayout=findViewById(R.id.headerLayout)
        headerLayout.setOnClickListener{
            finish()
        }
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        Log.e("tabbyurl",""+intent.getStringExtra("tabbyurl"))
        webView.loadUrl(intent.getStringExtra("tabbyurl")!!)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url!!)
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

    }
}