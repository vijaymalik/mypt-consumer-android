package co.com.mypt.PlanRenewal

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject

class UpgradeTopUpReviewPackageActivity : AppCompatActivity() {
    lateinit var tvcurrent_package: TextView
    lateinit var tvcurrent_end_date: TextView
    lateinit var tvnew_package_start: TextView
    lateinit var tvnew_package_end: TextView
    lateinit var tvtraining_preference: TextView
    lateinit var tvTotalSession: TextView
    lateinit var tvValidity: TextView
    lateinit var tvPayment: TextView
    lateinit var tvPrice: TextView
    lateinit var tvTopupSessionValue: TextView
    lateinit var linearPackage: LinearLayout
    lateinit var linearEndDate: LinearLayout
    lateinit var linearPackageStart: LinearLayout
    lateinit var linearPackageEnd: LinearLayout
    lateinit var linearPrefernce: LinearLayout
    lateinit var linearTotalSession: LinearLayout
    lateinit var linearValidity: LinearLayout
    lateinit var linearTopupSession: LinearLayout
    lateinit var linearBill: LinearLayout
    lateinit var billBottomSheetDialog:BottomSheetDialog
    var main_price = ""
    var tax_price = ""
    var price = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade_topup_package)
        linearBill=findViewById(R.id.linearBill)
        tvPrice=findViewById(R.id.tvPrice)
        tvcurrent_package=findViewById(R.id.tvcurrent_package)
        linearEndDate=findViewById(R.id.linearEndDate)
        linearPackage=findViewById(R.id.linearPackage)
        linearPackageStart=findViewById(R.id.linearPackageStart)
        linearTotalSession=findViewById(R.id.linearTotalSession)
        linearPrefernce=findViewById(R.id.linearPrefernce)
        linearPackageEnd=findViewById(R.id.linearPackageEnd)
        linearValidity=findViewById(R.id.linearValidity)
        tvcurrent_end_date=findViewById(R.id.tvcurrent_end_date)
        tvnew_package_start=findViewById(R.id.tvnew_package_start)
        tvnew_package_end=findViewById(R.id.tvnew_package_end)
        tvtraining_preference=findViewById(R.id.tvtraining_preference)
        tvTotalSession=findViewById(R.id.tvTotalSession)
        tvTopupSessionValue=findViewById(R.id.tvTopupSessionValue)
        linearTopupSession=findViewById(R.id.linearTopupSession)
        tvValidity=findViewById(R.id.tvValidity)
        tvPayment=findViewById(R.id.tvPayment)
        if (intent.getStringExtra("typeSubsctiption").equals("topup")){
            linearPackageStart.visibility= View.GONE
            linearPackageEnd.visibility= View.GONE
            linearTopupSession.visibility=View.VISIBLE
            linearTotalSession.visibility=View.GONE
        }else{
            linearTotalSession.visibility=View.VISIBLE
            linearTopupSession.visibility=View.GONE

            linearPackageStart.visibility= View.VISIBLE
            linearPackageEnd.visibility= View.VISIBLE

        }
        tvPayment.setOnClickListener {
            val intent= Intent(
                this@UpgradeTopUpReviewPackageActivity,
                CommonPaymentSelectionActivity::class.java
            )
            intent.putExtra("id",getIntent().getStringExtra("id").toString())
            intent.putExtra("sessions",getIntent().getStringExtra("sessions").toString())
            intent.putExtra("days",getIntent().getStringExtra("days").toString())
            intent.putExtra("price",price)
            intent.putExtra("subscriptionType",getIntent().getStringExtra("typeSubsctiption"))
            intent.putExtra("type",getIntent().getStringExtra("type").toString())
            intent.putExtra("totalPrice",price)
            intent.putExtra("tax_price",tax_price)
            intent.putExtra("main_price",main_price)
            startActivity(intent)
        }

        linearBill.setOnClickListener{
            //billBottomSheetDialog.show()
        }
        getData()
    }

    private fun getData() {

        val param: MutableMap<String, String> = HashMap()
        param["id"] = intent.getStringExtra("id").toString()
        param["sessions"] = intent.getStringExtra("sessions").toString()
        param["days"] = intent.getStringExtra("days").toString()
        param["price"] = intent.getStringExtra("price").toString()
        param["type"] = intent.getStringExtra("typeSubsctiption").toString().lowercase()

        Log.e("upgradeTopUpParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.reviewUpgradeTopUpPkg,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("upgradeTopUpRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        val data = resp.optJSONObject("data")
                        tvcurrent_package.text = data.optString("currentPackage")
                        tvcurrent_end_date.text = data.optString("currentEndDate")
                        tvnew_package_start.text = data.optString("newPackageStart")
                        tvnew_package_end.text = data.optString("newPackageEnd")
                        tvtraining_preference.text = data.optString("trainingPreference")
                        tvTotalSession.text = data.optString("totalSessions")
                        tvTopupSessionValue.text = data.optString("totalSessions")
                        tvValidity.text = data.optString("validity")
                        tvPrice.text = data.optString("price")

                        price = data.optString("price")
                        main_price = data.optString("main_price")
                        tax_price = data.optString("tax_price")

                        billBottomSheet( data.optString("main_price"),
                            data.optString("tax_price"),
                            data.optString("price")
                        )
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }

    private fun billBottomSheet(mainPrice: String, taxPrice: String, price: String) {

        billBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.bill_detail_bottomsheet, null)
        billBottomSheetDialog.setContentView(bottomSheet)


        val imclose =bottomSheet.findViewById<ImageView>(R.id.imclose)
        val tvSessionCost =bottomSheet.findViewById<TextView>(R.id.tvSessionCost)
        val tvTax =bottomSheet.findViewById<TextView>(R.id.tvTax)
        val tvPayable =bottomSheet.findViewById<TextView>(R.id.tvPayable)
        tvSessionCost.text = "AED $mainPrice"
        tvTax.text = "AED $taxPrice"
        tvPayable.text = "AED $price"
        val window = billBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        imclose.setOnClickListener {
            billBottomSheetDialog.dismiss()
        }
        animateBottomSheet(billBottomSheetDialog)
        /*  addCalorieBottomSheetDialog.setOnShowListener {
              val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
              bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
              bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
          }*/
        billBottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
}