package co.com.mypt.PlanRenewal.Renew

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.PlanRenewal.CommonPaymentSelectionActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.GymActivityAdapter
import co.com.mypt.model.GymActivityModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject

class RenewHomeGymReviewActivity : AppCompatActivity() {
    lateinit var tvpackage: TextView
    lateinit var tvstartDate: TextView
    lateinit var tvendDtae: TextView
    lateinit var trainingPreference: TextView
    lateinit var totalSession: TextView
    lateinit var newPackageEndDate: TextView
    lateinit var validity: TextView
    lateinit var tvPrice: TextView
    lateinit var tvPayment: TextView
    lateinit var linearBill: LinearLayout

    lateinit var recycler1:RecyclerView
    var gymActivitiesModelList :ArrayList<GymActivityModel> = ArrayList()
    lateinit var billBottomSheetDialog:BottomSheetDialog
    var main_price = ""
    var tax_price = ""
    var price = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gym_plan_review)
        trainingPreference=findViewById(R.id.trainingPreference)
        validity=findViewById(R.id.validity)
        linearBill=findViewById(R.id.linearBill)
        tvPrice=findViewById(R.id.tvPrice)
        totalSession=findViewById(R.id.totalSession)
        tvpackage=findViewById(R.id.currentPackage)
        tvstartDate=findViewById(R.id.newPackageStartDate)
        newPackageEndDate=findViewById(R.id.newPackageEndDate)
        tvendDtae=findViewById(R.id.currentEndDate)
        tvPayment=findViewById(R.id.tvPayment)
        recycler1=findViewById(R.id.recycler1)

        val gymActivityAdapter = GymActivityAdapter(applicationContext, gymActivitiesModelList)
        recycler1.adapter = gymActivityAdapter

        tvPayment.setOnClickListener {
            val intent= Intent(
                this,
                CommonPaymentSelectionActivity::class.java
            )
            intent.putExtra("id",getIntent().getStringExtra("id").toString())
            intent.putExtra("sessions",getIntent().getStringExtra("sessions").toString())
            intent.putExtra("days",getIntent().getStringExtra("days").toString())
            intent.putExtra("type","renew")
            intent.putExtra("subscriptionType","renew")
            intent.putExtra("price",price)
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
        param["type"] = "renew"

        Log.e("renewReviewPkgParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.reviewUpgradeTopUpPkg,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("renewReviewPkgRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        val data = resp.optJSONObject("data")
                        tvpackage.text = data.optString("currentPackage")
                        tvendDtae.text = data.optString("currentEndDate")
                        tvstartDate.text = data.optString("newPackageStart")
                        newPackageEndDate.text = data.optString("newPackageEnd")
                        trainingPreference.text = data.optString("trainingPreference")
                        totalSession.text = data.optString("totalSessions")
                        validity.text = data.optString("validity")
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