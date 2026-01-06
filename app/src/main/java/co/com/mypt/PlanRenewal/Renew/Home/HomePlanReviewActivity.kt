package co.com.mypt.PlanRenewal.Renew.Home

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.PlanRenewal.CommonPaymentSelectionActivity
import co.com.mypt.R

class HomePlanReviewActivity : AppCompatActivity() {
    lateinit var tvpackage: TextView
    lateinit var tvstartDate: TextView
    lateinit var tvendDtae: TextView
    lateinit var trainingPreference: TextView
    lateinit var totalSession: TextView
    lateinit var newPackageEndDate: TextView
    lateinit var validity: TextView
    lateinit var tvPayment: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_plan_review)
        trainingPreference=findViewById(R.id.trainingPreference)
        validity=findViewById(R.id.validity)
        totalSession=findViewById(R.id.totalSession)
        tvpackage=findViewById(R.id.currentPackage)
        tvstartDate=findViewById(R.id.newPackageStartDate)
        newPackageEndDate=findViewById(R.id.newPackageEndDate)
        tvendDtae=findViewById(R.id.currentEndDate)
        tvPayment=findViewById(R.id.tvPayment)

        tvPayment.setOnClickListener {
            val intent= Intent(
                this,
                CommonPaymentSelectionActivity::class.java
            )
            startActivity(intent)
        }
    }
}