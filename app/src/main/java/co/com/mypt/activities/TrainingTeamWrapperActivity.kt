package co.com.mypt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.R
import co.com.mypt.fragments.CreatePackageScreen.TrainingTeamFragment

class TrainingTeamWrapperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_team)
        if (savedInstanceState == null) {
            val fragment = TrainingTeamFragment()
            val bundle = Bundle()
            bundle.putString("address_id", intent.getStringExtra("address_id"))
            bundle.putDouble("longitude", intent.getDoubleExtra("longitude",0.0))
            bundle.putDouble("latitude", intent.getDoubleExtra("latitude",0.0))
            if(intent.hasExtra("studio_id")) {
                bundle.putString("studio_id", intent.getStringExtra("studio_id"))
            }
            if(intent.hasExtra("trainer_id")) {
                bundle.putString("trainer_id", intent.getStringExtra("trainer_id"))
            }
            if(intent.hasExtra("isGuestHome")){
                bundle.putBoolean("isGuestHome", intent.getBooleanExtra("isGuestHome",false))
            }
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}