package co.com.mypt.PlanRenewal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.com.mypt.PlanRenewal.TopUp.SessionTopUpActivity
import co.com.mypt.PlanRenewal.Upgrade.ChooseSessionActivity
import co.com.mypt.R

class SelectedUpgragationActivity : AppCompatActivity() {
    lateinit var im_Upgrade : ImageView
    lateinit var imTopUp : ImageView
    lateinit var back : ImageView
    lateinit var tvcontinue : TextView
    lateinit var im:ImageView
    var type=""
    lateinit var sharedPreferences:SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_upgragation)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor=sharedPreferences.edit()
        im_Upgrade = findViewById(R.id.im_Upgrade)
        imTopUp = findViewById(R.id.imTopUp)
        tvcontinue = findViewById(R.id.tvcontinue)
        back = findViewById(R.id.back)
        im = findViewById(R.id.im)
        back.setOnClickListener{
            finish()
        }


        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(30,25,30,0)
        im_Upgrade.setLayoutParams(layoutParams)

        val layoutParams1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams1.setMargins(30,0,30,0)
        imTopUp.layoutParams = layoutParams1

        im_Upgrade.setOnClickListener {
            if(intent.getBooleanExtra("isUpgrade",false)){
                type="upgrade"
                im_Upgrade.setImageResource(R.drawable.selected_upgrade)
                imTopUp.setImageResource(R.drawable.grey_topup)
                tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
                tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
            }
        }

        imTopUp.setOnClickListener {
            type="topup"
            im_Upgrade.setImageResource(R.drawable.grey_upgrade)
            imTopUp.setImageResource(R.drawable.selected_topup)

            tvcontinue.background = resources.getDrawable(R.drawable.white_rectangle)
            tvcontinue.setTextColor(resources.getColor(R.color.buttontextcolor))
        }
        tvcontinue.setOnClickListener {
            if (type.equals("topup",true) || type.equals("upgrade",true)){
                if(type.equals("topup",true)){
                    val intent1=Intent(this,SessionTopUpActivity::class.java)
                    intent1.putExtra("id",intent.getStringExtra("id"))
                    intent1.putExtra("typeSubsctiption","topup")
                    startActivity(intent1)

                }
                else{
                    val intent1=Intent(this,ChooseSessionActivity::class.java)
                    intent1.putExtra("id",intent.getStringExtra("id"))
                    intent1.putExtra("typeSubsctiption","Upgrade")
                    startActivity(intent1)


                }
            }

        }

    }
}