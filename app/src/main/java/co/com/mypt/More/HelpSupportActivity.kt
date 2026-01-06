package co.com.mypt.More

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R

class HelpSupportActivity : AppCompatActivity() {
    lateinit var linearheader:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)
        linearheader=findViewById(R.id.linearheader)
        linearheader.setOnClickListener{
            finish()
        }

    }
}