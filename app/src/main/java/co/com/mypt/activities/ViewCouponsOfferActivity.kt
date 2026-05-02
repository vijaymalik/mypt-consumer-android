package co.com.mypt.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.CouponAdapter
import co.com.mypt.model.AvailablePromo


class ViewCouponsOfferActivity : AppCompatActivity() {
    lateinit var recyclerCoupon:RecyclerView
//    var couponsModelList :ArrayList<CouponsModel> = ArrayList()
    lateinit var tvPayment : TextView
    lateinit var headerLayout : LinearLayout
    lateinit var etSearchCoupon : EditText
    lateinit var tvCouponError : TextView
    lateinit var tvApplyCoupon : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_coupons_offer)
        tvPayment=findViewById(R.id.tvPayment)
        etSearchCoupon=findViewById(R.id.etSearchCoupon)
        tvCouponError=findViewById(R.id.tvCouponError)
        tvApplyCoupon=findViewById(R.id.tvApplyCoupon)
        recyclerCoupon=findViewById(R.id.recyclerCoupon)
        headerLayout=findViewById(R.id.headerLayout)
        headerLayout.setOnClickListener {
            finish()
        }
        val couponsModelList: ArrayList<AvailablePromo?>? =
            intent.getParcelableArrayListExtra<AvailablePromo?>("couponList")
//        couponsModelList.clear()
        /*for (i in 0..4) {
            var activityModel= CouponsModel()
            activityModel.name="FIRST${i+1}00"
            activityModel.saving="Save AED ${i+1}00"
            couponsModelList.add(activityModel)
        }*/
        var couponAdapter= CouponAdapter(this,couponsModelList){it,name->

            val resultIntent = Intent()
            intent.putExtra("couponId", it)
            intent.putExtra("couponName", name)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerCoupon.adapter=couponAdapter

        tvPayment.setOnClickListener {

            finish()
        }

        etSearchCoupon.addTextChangedListener {
            tvCouponError.visibility = View.GONE
        }

        tvApplyCoupon.setOnClickListener {

            val enteredCode = etSearchCoupon.text.toString().trim()

            if (enteredCode.isEmpty()) {
                tvCouponError.text = "Please enter coupon code"
                tvCouponError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val matchedCoupon = couponsModelList?.find {
                it?.name.equals(enteredCode, ignoreCase = true)
            }

            if (matchedCoupon != null) {
                tvCouponError.visibility = View.GONE
                val resultIntent = Intent()
                resultIntent.putExtra("couponId", matchedCoupon.id)
                resultIntent.putExtra("couponName", matchedCoupon.name)
                setResult(RESULT_OK, resultIntent)
                finish()

            } else {
                tvCouponError.text = "Invalid coupon code"
                tvCouponError.visibility = View.VISIBLE
            }
        }
    }
}