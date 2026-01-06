package co.com.mypt.UpComingClasses

import ai.tabby.android.data.Buyer
import ai.tabby.android.data.BuyerHistory
import ai.tabby.android.data.Currency
import ai.tabby.android.data.Lang
import ai.tabby.android.data.Order
import ai.tabby.android.data.OrderHistory
import ai.tabby.android.data.OrderItem
import ai.tabby.android.data.PaymentMethod
import ai.tabby.android.data.ShippingAddress
import ai.tabby.android.data.Status
import ai.tabby.android.data.TabbyPayment
import ai.tabby.android.data.TabbyResult
import ai.tabby.android.data.tabbyResult
import ai.tabby.android.factory.TabbyFactory
import ai.tabby.android.internal.network.TabbyEnvironment
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import co.com.mypt.Api.Constants
import co.com.mypt.R
import co.com.mypt.Webview.CCavenueWebViewActivity
import co.com.mypt.activities.BookingConfirmActivity
import co.com.mypt.activities.TabbyWebViewActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar

class ClassPaymentScreenActivity : AppCompatActivity() {
    lateinit var tvTotalPrice:TextView
    lateinit var tvPaymentPrice:TextView
    var payment_id=""
    lateinit var back_1: ImageView

    lateinit var imarrow: ImageView
    lateinit var linearBill: LinearLayout
    lateinit var linearPay: LinearLayout
    lateinit var billBottomSheetDialog: BottomSheetDialog
    lateinit var checkTabby: CheckBox
    lateinit var checkDebit: CheckBox
    lateinit var checkTamara: CheckBox
    val TABBY_CHECKOUT_REQUEST_CODE = 1001
    var selectedPaymentOption=""
    lateinit var sharedPreferences: SharedPreferences
    var isSelected=false
    lateinit var im_Info:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_class_payment_screen)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(applicationContext)

        tvTotalPrice=findViewById(R.id.tvTotalPrice)
        tvPaymentPrice=findViewById(R.id.tvPaymentPrice)
        linearBill=findViewById(R.id.linearBill)
        back_1=findViewById(R.id.back_1)
        checkTabby=findViewById(R.id.checkTabby)
        linearPay=findViewById(R.id.linearPay)
        checkDebit=findViewById(R.id.checkDebit)
        checkTamara=findViewById(R.id.checkTamara)
        imarrow=findViewById(R.id.imarrow)
        im_Info=findViewById(R.id.im_Info)

        tvTotalPrice.text = "Total: AED ${intent.getStringExtra("total_price")}"
        tvPaymentPrice.text = "Pay AED ${intent.getStringExtra("total_price")}"

        im_Info.setOnClickListener{
            var intent=Intent(this, TabbyWebViewActivity::class.java)
            val amount = getIntent().getStringExtra("total_price")?.replace(" ", "")
            Log.e("amount",""+amount)
            intent.putExtra("tabbyurl","https://checkout.tabby.ai/promos/product-page/installments/en/?price=${amount}.00&currency=AED&merchant_code=AE&public_key=pk_test_c0dfc062-d62c-4723-a699-8710812c1847")
            startActivity(intent)
        }
        back_1.setOnClickListener{
            finish()
        }
        TabbyFactory.setup(
            this,
            "pk_test_c0dfc062-d62c-4723-a699-8710812c1847",
            TabbyEnvironment.Prod
        )

       // billBottomSheet()
        /*linearBill.setOnClickListener{
            billBottomSheetDialog.show()
        }*/
        checkTabby.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // startTabbyCheckout()
                checkTabby.isChecked=true
                checkTamara.isChecked = false
                checkDebit.isChecked = false
                linearPay.background = ContextCompat.getDrawable(this, R.drawable.update_plan_drawable)
                tvPaymentPrice.setTextColor(resources.getColor(R.color.buttontextcolor))
                imarrow.setColorFilter(ContextCompat.getColor(this, R.color.buttontextcolor), PorterDuff.Mode.SRC_IN)
                isSelected = true
                selectedPaymentOption="tabby_pay"

            }else{
                selectedPaymentOption=""
                isSelected = false
                checkTabby.isChecked=false
                linearPay.background = ContextCompat.getDrawable(this, R.drawable.grey_payment_drawable)
                tvPaymentPrice.setTextColor(resources.getColor(R.color.headingcolor))
                imarrow.setColorFilter(ContextCompat.getColor(this, R.color.headingcolor), PorterDuff.Mode.SRC_IN)
            }
        }
        checkTamara.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkTabby.isChecked=false
                checkTamara.isChecked = true
                checkDebit.isChecked = false
                linearPay.background = ContextCompat.getDrawable(this, R.drawable.update_plan_drawable)
                tvPaymentPrice.setTextColor(resources.getColor(R.color.buttontextcolor))
                imarrow.setColorFilter(ContextCompat.getColor(this, R.color.buttontextcolor), PorterDuff.Mode.SRC_IN)
                isSelected = true
                selectedPaymentOption="tamara_pay"

            }else{

                selectedPaymentOption=""
                isSelected = false
                checkTamara.isChecked = false
                linearPay.background = ContextCompat.getDrawable(this, R.drawable.grey_payment_drawable)
                tvPaymentPrice.setTextColor(resources.getColor(R.color.headingcolor))
                imarrow.setColorFilter(ContextCompat.getColor(this, R.color.headingcolor), PorterDuff.Mode.SRC_IN)
            }
        }
        checkDebit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkTabby.isChecked=false
                checkTamara.isChecked = false
                checkDebit.isChecked = true
                linearPay.background = ContextCompat.getDrawable(this, R.drawable.update_plan_drawable)
                tvPaymentPrice.setTextColor(resources.getColor(R.color.buttontextcolor))
                imarrow.setColorFilter(ContextCompat.getColor(this, R.color.buttontextcolor), PorterDuff.Mode.SRC_IN)
                isSelected = true
                selectedPaymentOption="debit"

            }else{
                selectedPaymentOption=""
                isSelected = false
                checkDebit.isChecked = false
                linearPay.background = ContextCompat.getDrawable(this, R.drawable.grey_payment_drawable)
                tvPaymentPrice.setTextColor(resources.getColor(R.color.headingcolor))
                imarrow.setColorFilter(ContextCompat.getColor(this, R.color.headingcolor), PorterDuff.Mode.SRC_IN)

            }
        }
        linearPay.setOnClickListener{
            if (isSelected){

                if (selectedPaymentOption == "debit"){
                    val intent = Intent(this, CCavenueWebCLassActivity::class.java)
                    intent.putExtra("schedule_id",getIntent().getStringExtra("schedule_id"))
                    intent.putExtra("totalPrice",getIntent().getStringExtra("total_price"))
                    intent.putExtra("selectedPaymentOption","ccavenue")

                    startActivity(intent)

                }
                else if (selectedPaymentOption.equals("tamara_pay")){
                }else{
                    startTabbyCheckout()
                }
            }

        }

    }
   /* private fun billBottomSheet() {

        billBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.bill_detail_bottomsheet, null)
        billBottomSheetDialog.setContentView(bottomSheet)


        val imclose =bottomSheet.findViewById<ImageView>(R.id.imclose)
        val tvSessionCost =bottomSheet.findViewById<TextView>(R.id.tvSessionCost)
        val tvTax =bottomSheet.findViewById<TextView>(R.id.tvTax)
        val tvPayable =bottomSheet.findViewById<TextView>(R.id.tvPayable)
        tvSessionCost.text = "AED "+intent.getStringExtra("main_price")
        tvTax.text = "AED "+intent.getStringExtra("tax_rate")
        tvPayable.text = "AED "+intent.getStringExtra("price")
        val window = billBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        imclose.setOnClickListener {
            billBottomSheetDialog.dismiss()
        }
        animateBottomSheet(billBottomSheetDialog)
        *//*  addCalorieBottomSheetDialog.setOnShowListener {
              val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
              bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
              bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
          }*//*
        billBottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }*/
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    fun generateUniqueAlphanumericId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..10)
            .map { chars.random() }
            .joinToString("")
    }
    private fun startTabbyCheckout() {
        val uniqueId = generateUniqueAlphanumericId()
        val tabbyPayment = TabbyPayment(
            amount = BigDecimal(intent.getStringExtra("total_price").toString().toDouble()),
            currency = Currency.AED,
            description = intent.getStringExtra("selectBookOption"),
            buyer = Buyer(
                email = "otp.success@tabby.ai",  // Always successful
                phone = "+971500000001",
                name = sharedPreferences.getString(Constants.name,"").toString()
            ),
            buyerHistory = BuyerHistory(
                registeredSince = Calendar.getInstance().time,
                loyaltyLevel = 0,
            ),
            order = Order(
                refId = uniqueId,
            ),
            orderHistory = listOf(
                OrderHistory(
                    purchasedAt = Calendar.getInstance().time,
                    amount = BigDecimal(intent.getStringExtra("total_price").toString().toDouble()),
                    paymentMethod = PaymentMethod.CARD,
                    status = Status.NEW,
                    buyer = Buyer(
                        email = sharedPreferences.getString(Constants.email,"").toString(),
                        phone = sharedPreferences.getString(Constants.phone,"").toString(),
                        name = sharedPreferences.getString(Constants.name,"").toString(),
                    ),
                    shippingAddress = ShippingAddress(
                        address = "",
                        city = "",
                        zip = "",
                    ),
                    items = listOf(
                        OrderItem(
                            refId = uniqueId,
                            title = "Gym Session/Package Booking"
                        )
                    ),
                )
            ),
            meta = emptyMap(),
        )
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session = TabbyFactory.tabby.createSession(
                    merchantCode = "ae", // or your region
                    lang = Lang.EN,
                    payment = tabbyPayment,
                )
                Log.e("PaymentId--->",session.paymentId)
                payment_id=session.paymentId
                val product = session.availableProducts.firstOrNull()
                if (product != null) {
                    val intent = TabbyFactory.tabby.createCheckoutIntent(product = product)
                    checkoutContract.launch(intent)
                    /*val intent = TabbyFactory.tabby.createCheckoutIntent(product)
                    startActivityForResult(intent, TABBY_CHECKOUT_REQUEST_CODE)*/
                } else {
                    Toast.makeText(applicationContext, "No products available", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("Tabby", "Session creation failed", e)
                Toast.makeText(applicationContext, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }


    }

    private val checkoutContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    result.tabbyResult?.let { tabbyResult ->
                        onCheckoutResult(tabbyResult)
                    } ?: Log.e("Error","Error") // Tabby result is null
                }
                else -> {
                    // Result is not OK
                }
            }
        }

    private fun onCheckoutResult(tabbyResult: TabbyResult) {
        Log.e("tabbyResult","$tabbyResult")
        when (tabbyResult.result) {
            TabbyResult.Result.AUTHORIZED -> {
                // showToast("Payment Authorized")
                val intent = Intent(this@ClassPaymentScreenActivity, UpcomingConfirmClassActivity::class.java)
                intent.putExtra("schedule_id",getIntent().getStringExtra("schedule_id"))
                intent.putExtra("transaction_id",payment_id)
                intent.putExtra("selectedPaymentOption","tabby")
                startActivity(intent)
            }
            TabbyResult.Result.REJECTED -> {
                alerDialog("Payment Rejected")
                //showToast("Payment Rejected")
            }
            TabbyResult.Result.CLOSED -> {
                alerDialog("Checkout Closed")
                //showToast("Checkout Closed")
            }
            TabbyResult.Result.EXPIRED -> {
                alerDialog("Session Expired")
                // showToast("Session Expired")
            }
            else -> {
                alerDialog("Unknown result")
                // showToast("Unknown result")
            }
            //    TabbyFactory.tabby.createSession(...) again
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun alerDialog(message:String){
        val builder = AlertDialog.Builder(this@ClassPaymentScreenActivity)
        builder.setMessage(message)
        builder.setIcon(R.drawable.app_icon)

        //performing positive action
        builder.setPositiveButton("OK"){dialogInterface, which ->
            //finish()
            dialogInterface.dismiss()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}