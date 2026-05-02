package co.com.mypt.More

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.More.Setting.LinkedDeviceActivity
import co.com.mypt.More.Setting.NotificationActivity
import co.com.mypt.More.Setting.PrivacyActivity
import co.com.mypt.More.Setting.SecuritySettingsActivity
import co.com.mypt.More.Setting.UnitOfMeasureActivity
import co.com.mypt.R

class SettingsActivity : AppCompatActivity() {
    lateinit var linearNotification:LinearLayout
    lateinit var linearSecurity:LinearLayout
    lateinit var linearPrivacy:LinearLayout
    lateinit var linearLinked:LinearLayout
    lateinit var linearUnits:LinearLayout
    lateinit var relative: RelativeLayout
    lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        linearNotification=findViewById(R.id.linearNotification)
        linearSecurity=findViewById(R.id.linearSecurity)
        linearPrivacy=findViewById(R.id.linearPrivacy)
        linearLinked=findViewById(R.id.linearLinked)
        linearUnits=findViewById(R.id.linearUnits)
        relative=findViewById(R.id.relative)
        back=findViewById(R.id.back_1)
        linearNotification.setOnClickListener{
            var intent= Intent(applicationContext,NotificationActivity::class.java)
            startActivity(intent)
        }
        relative.setOnClickListener{

        }
        linearSecurity.setOnClickListener{
            var intent= Intent(applicationContext,SecuritySettingsActivity::class.java)
            startActivity(intent)
        }
        linearPrivacy.setOnClickListener{
            var intent= Intent(applicationContext,PrivacyActivity::class.java)
            startActivity(intent)
        }
        linearLinked.setOnClickListener{
            var intent= Intent(applicationContext,LinkedDeviceActivity::class.java)
            startActivity(intent)
        }
        linearUnits.setOnClickListener{
            var intent= Intent(applicationContext,UnitOfMeasureActivity::class.java)
            startActivity(intent)
        }

        back.setOnClickListener {
            finish()
        }

    }
}