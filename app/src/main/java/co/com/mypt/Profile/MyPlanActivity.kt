package co.com.mypt.Profile

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.PlanRenewal.TopUp.SessionTopUpActivity
import co.com.mypt.PlanRenewal.Upgrade.ChooseSessionActivity
import co.com.mypt.R

class MyPlanActivity : AppCompatActivity() {
    lateinit var tv:TextView
    lateinit var tvTopUp:TextView
    lateinit var linearUpgrade: LinearLayout
    lateinit var imSilverText:ImageView
    lateinit var silver:ImageView
    lateinit var platinum_plan:ImageView
    lateinit var gold:ImageView
    lateinit var vip_plan:ImageView
    lateinit var imGoldPlanText:ImageView
    lateinit var imPlatiumPlanText:ImageView
    lateinit var imViptext:ImageView
    lateinit var tvsession:TextView
    lateinit var tvSessionDetail:TextView
    lateinit var tvDays:TextView
    lateinit var linearUpdatePlan:LinearLayout
    lateinit var sessionProgress:ProgressBar
    lateinit var DaysprogressBar:ProgressBar
    lateinit var back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_plan)
        imSilverText=findViewById(R.id.imSilverText)
        silver=findViewById(R.id.silver)
        gold=findViewById(R.id.gold)
        platinum_plan=findViewById(R.id.platinum_plan)
        imGoldPlanText=findViewById(R.id.imGoldPlanText)
        vip_plan=findViewById(R.id.vip_plan)
        imPlatiumPlanText=findViewById(R.id.imPlatiumPlanText)
        imViptext=findViewById(R.id.imViptext)
        tvsession=findViewById(R.id.tvsession)
        tvDays=findViewById(R.id.tvDays)
        tvSessionDetail=findViewById(R.id.tvSessionDetail)
        sessionProgress=findViewById(R.id.sessionProgress)
        DaysprogressBar=findViewById(R.id.DaysprogressBar)
        tvTopUp=findViewById(R.id.tvTopUp)
        linearUpgrade=findViewById(R.id.linearUpgrade)
        linearUpdatePlan=findViewById(R.id.linearUpdatePlan)
        back=findViewById(R.id.back_1)
        back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        linearUpgrade.setOnClickListener {
            val intent1=Intent(this,ChooseSessionActivity::class.java)
            intent1.putExtra("id",intent.getStringExtra("plan_id"))
            intent1.putExtra("typeSubsctiption","Upgrade")
            startActivity(intent1)
        }
        tvTopUp.setOnClickListener {
            val intent1=Intent(this,SessionTopUpActivity::class.java)
            intent1.putExtra("id",intent.getStringExtra("plan_id"))
            intent1.putExtra("typeSubsctiption","topup")
            startActivity(intent1)
        }
        Log.e("plan_name",""+intent.getStringExtra("plan_name"))
        if (intent.getStringExtra("plan_name").equals("Silver")){
            imSilverText.visibility= View.VISIBLE
            imGoldPlanText.visibility= View.GONE
            imPlatiumPlanText.visibility= View.GONE
            imViptext.visibility= View.GONE
            silver.visibility=View.VISIBLE
        }
        else if (intent.getStringExtra("plan_name").equals("Gold")){
            imGoldPlanText.visibility= View.VISIBLE
            imSilverText.visibility= View.GONE
            imPlatiumPlanText.visibility= View.GONE
            imViptext.visibility= View.GONE
            gold.visibility=View.VISIBLE

        }
        else if (intent.getStringExtra("plan_name").equals("Platinum")){
            imPlatiumPlanText.visibility= View.VISIBLE
            imGoldPlanText.visibility= View.GONE
            imSilverText.visibility= View.GONE
            imViptext.visibility= View.GONE
            platinum_plan.visibility=View.VISIBLE

        }else{
            imViptext.visibility=View.VISIBLE
            imGoldPlanText.visibility= View.GONE
            imPlatiumPlanText.visibility= View.GONE
            imSilverText.visibility= View.GONE
            vip_plan.visibility=View.VISIBLE

        }
        tvSessionDetail.text = intent.getStringExtra("remaining_sessions")+" Sessions remaining ending in "+intent.getStringExtra("remaining_days")+" days"
        tvsession.text = intent.getStringExtra("remaining_sessions")+"/"+intent.getStringExtra("total_sessions")+" sessions"
        tvDays.text = intent.getStringExtra("remaining_days")+"/"+intent.getStringExtra("total_days")+" days"
        sessionProgress.max = intent.getStringExtra("total_sessions")!!.toInt()
        DaysprogressBar.max = intent.getStringExtra("total_days")!!.toInt()
        sessionProgress.progress = intent.getStringExtra("remaining_sessions")!!.toInt()
        DaysprogressBar.progress = intent.getStringExtra("remaining_days")!!.toInt()
        if (intent.getStringExtra("isUpgrade")!!.equals("true")){
            linearUpgrade.visibility=View.VISIBLE
        }else{
            linearUpgrade.visibility=View.GONE

        }

       // textShader(tv)


    }

   private fun textShader(tv: TextView) {
       tv.post {
           val paint: TextPaint = tv.paint

           // Get actual width and height AFTER layout
           val width = tv.width.toFloat()
           val height = tv.height.toFloat()

           // Create a 135° diagonal gradient
           val textShader = LinearGradient(
               0f, 0f, // Start (top-left)
               width, height, // End (bottom-right)
               intArrayOf(
                   Color.parseColor("#CFAB68"), // Start color
                   Color.parseColor("#FFF1D8"), // Middle color
                   Color.parseColor("#AD8236")  // End color
               ),
               floatArrayOf(0f, 0.5f, 1f), // Color stops
               Shader.TileMode.CLAMP
           )

           paint.shader = textShader
           tv.invalidate()
       }
   }


}