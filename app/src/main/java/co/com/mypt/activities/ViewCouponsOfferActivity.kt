package co.com.mypt.activities

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.CouponAdapter
import co.com.mypt.model.CouponsModel

class ViewCouponsOfferActivity : AppCompatActivity() {
    lateinit var recyclerCoupon:RecyclerView
    var couponsModelList :ArrayList<CouponsModel> = ArrayList()
    lateinit var tvPayment : TextView
    lateinit var headerLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_coupons_offer)
        tvPayment=findViewById(R.id.tvPayment)
        recyclerCoupon=findViewById(R.id.recyclerCoupon)
        headerLayout=findViewById(R.id.headerLayout)
        headerLayout.setOnClickListener {
            finish()
        }
        couponsModelList.clear()
        for (i in 0..4) {
            var activityModel= CouponsModel()
            activityModel.name="FIRST${i+1}00"
            activityModel.saving="Save AED ${i+1}00"
            couponsModelList.add(activityModel)
        }
        var couponAdapter= CouponAdapter(this,couponsModelList)
        recyclerCoupon.adapter=couponAdapter

        tvPayment.setOnClickListener {

            finish()
        }
    }
}