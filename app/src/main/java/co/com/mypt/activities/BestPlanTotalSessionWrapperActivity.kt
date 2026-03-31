package co.com.mypt.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R
import co.com.mypt.fragments.CreatePackageScreen.BestPlanTotalSessionFragment

class BestPlanTotalSessionWrapperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_best_plan_total_session_wrapper)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragmentContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            val fragment = BestPlanTotalSessionFragment(
                intent.getStringExtra("type"),
                intent.getStringExtra("slot_id")?:"",
                intent.getStringExtra("address_id"),
                intent.getStringExtra("trainer_id"),
                intent.getStringExtra("studio_id"),
                intent.getIntExtra("month", 0)
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}