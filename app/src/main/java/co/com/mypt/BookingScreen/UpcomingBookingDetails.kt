package co.com.mypt.BookingScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.ActiveSession.BeforeArrivingActivity
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.CalendarSelectedDate.SelectedDateDecorator
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.adapter.SelectRescheduleTimeAdapter
import co.com.mypt.calendarUtils.DisabledOtherDatesDecorator
import co.com.mypt.calendarUtils.EventMultiColorDecorator
import co.com.mypt.model.SelectTImeModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.json.JSONObject
import org.threeten.bp.DayOfWeek
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

class UpcomingBookingDetails : AppCompatActivity() {
    var reaonse_value=""
    var slot_id=""
    lateinit var cancelBookingSheetDialog: BottomSheetDialog
    lateinit var reschedulingSheetDialog: BottomSheetDialog
    lateinit var reschedulingDateSheetDialog: BottomSheetDialog
    lateinit var reschedulingTimeSheetDialog: BottomSheetDialog
    lateinit var cancelPolicyBottomSheetDialog:BottomSheetDialog
    lateinit var cancelReasonBottomSheetDialog:BottomSheetDialog
    lateinit var qrBottomSheetDialog:BottomSheetDialog

    lateinit var tvRescheduleBooking : TextView
    lateinit var rescheduleBookingText : TextView
    lateinit var rescheduleBooking : TextView
    lateinit var tvLearn:TextView
    lateinit var cancelRequest:TextView
    lateinit var cancelBooking:TextView
    lateinit var bookingStatus:TextView
    lateinit var tvTrainer_name:TextView
    lateinit var home_workout:TextView
    lateinit var tvBookingDate:TextView
    lateinit var tvLocation:TextView
    lateinit var tvdistance:TextView
    lateinit var avgRating:TextView
    lateinit var cancellationMsg:TextView
    lateinit var tvamountPaid:TextView
    lateinit var tvTrainingLocation:TextView
    lateinit var tvTrainingDate:TextView
    lateinit var nested:NestedScrollView
    lateinit var tvContact:TextView
    lateinit var imTrainer:ImageView
    lateinit var tv:TextView
    lateinit var tvTrack:TextView
    lateinit var cancelledDateTime:TextView
    lateinit var tvBookAgain:TextView
    lateinit var cancelTextMessage:TextView
    lateinit var cancellationPolicyDescription:TextView
    lateinit var context_ : Context
    lateinit var cancellationPolicyLayout : LinearLayout
    lateinit var rescheduleCancelLayout : LinearLayout
    lateinit var trainerDetailLayout : LinearLayout
    lateinit var cancelledDetailLayout : LinearLayout
    lateinit  var linearReason : LinearLayout
    lateinit  var headerLayout : LinearLayout
    lateinit  var linearSelectSLot : LinearLayout
    lateinit  var linearSelectDate : LinearLayout
    lateinit  var linearQRCode : LinearLayout

    lateinit var cancellationMsgCard:CardView
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var longitude: Double? = null
    var latitude: Double? = null
    var isResumed1 = true
    var isclick=0
    var showProgress = false
    var currentMonth=-1
    lateinit var calendarView : MaterialCalendarView
    var slotshift="morning"
    var selectDate=""
    var cancel_reason=""
    var selectTimeModelList : ArrayList<SelectTImeModel> = ArrayList()
    lateinit var recyclerViewTime:RecyclerView
    lateinit var  im:ImageView
    var bookingType=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming_booking_details)
        cancelTextMessage = findViewById(R.id.cancelTextMessage)
        cancellationPolicyDescription = findViewById(R.id.cancellationPolicyDescription)
        cancellationMsgCard = findViewById(R.id.cancellationMsgCard)
        tvTrack = findViewById(R.id.tvTrack)
        rescheduleBookingText = findViewById(R.id.rescheduleBookingText)
        rescheduleBooking = findViewById(R.id.rescheduleBooking)
        cancelledDetailLayout = findViewById(R.id.cancelledDetailLayout)
        trainerDetailLayout = findViewById(R.id.trainerDetailLayout)
        rescheduleCancelLayout = findViewById(R.id.rescheduleCancelLayout)
        cancellationPolicyLayout = findViewById(R.id.cancellationPolicyLayout)
        tvLearn = findViewById(R.id.tvLearn)
        linearQRCode = findViewById(R.id.linearQRCode)
        nested = findViewById(R.id.nested)
        headerLayout = findViewById(R.id.headerLayout)
        cancelRequest = findViewById(R.id.cancelRequest)
        cancelBooking = findViewById(R.id.cancelBooking)
        bookingStatus = findViewById(R.id.bookingStatus)
        home_workout = findViewById(R.id.home_workout)
        tvLocation = findViewById(R.id.tvLocation)
        tvBookingDate = findViewById(R.id.tvBookingDate)
        tvTrainer_name = findViewById(R.id.tvTrainer_name)
        tvdistance = findViewById(R.id.tvdistance)
        avgRating = findViewById(R.id.avgRating)
        tvamountPaid = findViewById(R.id.tvamountPaid)
        tvTrainingLocation = findViewById(R.id.tvTrainingLocation)
        cancellationMsg = findViewById(R.id.cancellationMsg)
        tvTrainingDate = findViewById(R.id.tvTrainingDate)
        imTrainer = findViewById(R.id.imTrainer)
        tvContact = findViewById(R.id.tvContact)
        cancelledDateTime = findViewById(R.id.cancelledDateTime)
        tvBookAgain = findViewById(R.id.tvBookAgain)

        context_ = this
        cancelBookingSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        reschedulingSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        reschedulingDateSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        reschedulingTimeSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        cancelPolicyBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        cancelReasonBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        qrBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission() && isclick==0) {
            getCurrentLocation()
        }
        cancelReasonsAlert()
        bookingQrAlert()
        cancelBookingAlert()
        reasonForReschedulingAlert()
       // reschedulingDateAlert()
       // reschedulingTimeAlert()

        rescheduleBooking.setOnClickListener {
            reschedulingSheetDialog.show()
        }
        linearQRCode.setOnClickListener {
            qrBottomSheetDialog.show()
        }
        headerLayout.setOnClickListener {
            finish()
        }
        tvLearn.setOnClickListener{
            cancelPolicyBottomSheetDialog.show()
        }
        cancelBooking.setOnClickListener{
            cancelBookingSheetDialog.show()
        }
        cancelRequest.setOnClickListener{
            sendCancelRequestData()
        }
        tvBookAgain.setOnClickListener{
            val intent = Intent(this, HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }
        tvTrack.setOnClickListener{
            val intent = Intent(this, BeforeArrivingActivity::class.java)
            intent.putExtra("bookingid",getIntent().getStringExtra("bookingid"))
            startActivity(intent)
        }
    }

    private fun sendCancelRequestData() {

        val param: MutableMap<String, String> = HashMap()
        param["id"] = ""+ intent.getStringExtra("bookingid")

        Log.e("cancelRequestParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.cancelrequest,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("cancelRequestRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        Toast.makeText(applicationContext,resp.optString("msg"),Toast.LENGTH_LONG).show()
                        finish()

                    }else{

                    }

                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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

    private fun getBookingDetail() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api = if (intent.getStringExtra("type").equals("home")){
            ApiURL.booking_detail+intent.getStringExtra("bookingid")+"&long="+""+"&lat="+""
        }else{
            ApiURL.booking_detail+intent.getStringExtra("bookingid")+"&long="+longitude+"&lat="+latitude

        }

        Log.e("getbookingDetailApi",""+api)

        GetMethod(api,applicationContext).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("getBookingResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        linearQRCode.visibility=View.VISIBLE
                       var jsonObjectData=jsonObj.optJSONObject("data")
                        tvBookingDate.text = jsonObjectData.optString("booked_at")
                        cancellationMsg.text = jsonObjectData.optString("cancellationPolicyMsg")
                        home_workout.text = jsonObjectData.optJSONObject("bookingDetail").optString("type")+" Workout"
                        bookingType=jsonObjectData.optJSONObject("bookingDetail").optString("type")
                        if (bookingType.equals("gym")){
                            tvTrack.visibility=View.GONE
                        }else{
                            tvTrack.visibility=View.VISIBLE

                        }
                        tvContact.text = jsonObjectData.optJSONObject("bookingDetail").optString("contact")
                        tvamountPaid.text = jsonObjectData.optJSONObject("bookingDetail").optString("price")
                        tvTrainingLocation.text = jsonObjectData.optJSONObject("bookingDetail").optString("location")
                        tvTrainingDate.text = jsonObjectData.optJSONObject("bookingDetail").optString("training_date")

                        tvTrainer_name.text = jsonObjectData.optJSONObject("trainerDetail").optString("name")
                        tvdistance.text = jsonObjectData.optJSONObject("trainerDetail").optString("distance")
                        tvLocation.text = jsonObjectData.optJSONObject("trainerDetail").optString("location")
                        avgRating.text = jsonObjectData.optJSONObject("trainerDetail").optString("averageRating")

                        val cancellationPolicy = jsonObjectData.optJSONObject("cancellationPolicy")
                        cancellationPolicyDescription.text = cancellationPolicy.optString("shortDescription")
                        createLearnMoreAlert(cancellationPolicy)

                        Glide.with(applicationContext).load(jsonObjectData.optJSONObject("trainerDetail").optString("profile")).fitCenter().error(R.drawable.dummy_trainer).
                        placeholder(R.drawable.dumbbell).into(imTrainer)

                        Glide.with(applicationContext).load(jsonObjectData.optJSONObject("bookingDetail").optString("qr")).fitCenter().into(im)
                        if (jsonObjectData.optString("isAction").equals("true")){
                            rescheduleCancelLayout.visibility=View.VISIBLE
                            cancellationMsgCard.visibility=View.VISIBLE
                        }

                        if (jsonObjectData.optString("can_cancel_request").equals("true")){
                            cancelRequest.visibility = View.VISIBLE
                            rescheduleBookingText.visibility = View.VISIBLE
                            tvTrack.visibility = View.GONE
                        }
                        nested.visibility=View.VISIBLE
                    }else{
                        nested.visibility=View.GONE
                        linearQRCode.visibility=View.GONE
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

    private fun bookingQrAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.booking_qrcode_layout, null)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        val imclose=bottomSheet.findViewById<ImageView>(R.id.close)

        im=bottomSheet.findViewById<ImageView>(R.id.im)
        imclose.setOnClickListener{
            qrBottomSheetDialog.dismiss()
        }

        qrBottomSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(qrBottomSheetDialog)
    }
    private fun cancelReasonsAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.cancel_reason_bottom_sheet_dialog, null)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        val imclose=bottomSheet.findViewById<ImageView>(R.id.close)

        var scheduleConflict=bottomSheet.findViewById<CheckBox>(R.id.scheduleConflict)
        var feelingUnwell=bottomSheet.findViewById<CheckBox>(R.id.feelingUnwell)
        var unexpectedCommitment=bottomSheet.findViewById<CheckBox>(R.id.unexpectedCommitment)
        var changeOfPlan=bottomSheet.findViewById<CheckBox>(R.id.changeOfPlan)
        var cancelBooking=bottomSheet.findViewById<TextView>(R.id.cancelBooking)

        var isSelected = false

        cancelBooking.setOnClickListener{
            if(isSelected){
                sendCancelData()
            }
        }
        imclose.setOnClickListener{
            reschedulingSheetDialog.dismiss()
            scheduleConflict.isChecked=false
            feelingUnwell.isChecked=false
            unexpectedCommitment.isChecked=false
            changeOfPlan.isChecked=false
            cancelBooking.setBackgroundResource(R.drawable.rectangle_btn)
            cancelBooking.setTextColor(application.resources.getColor(R.color.white))
        }

        scheduleConflict.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cancel_reason=scheduleConflict.text.toString()
                changecheckbox(scheduleConflict,feelingUnwell,unexpectedCommitment,changeOfPlan,cancelBooking)
                isSelected = true
            }
        }
        feelingUnwell.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cancel_reason=feelingUnwell.text.toString()
                changecheckbox(feelingUnwell,scheduleConflict,unexpectedCommitment,changeOfPlan,cancelBooking)
                isSelected = true
            }
        }
        unexpectedCommitment.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cancel_reason=unexpectedCommitment.text.toString()
                changecheckbox(unexpectedCommitment,feelingUnwell,scheduleConflict,changeOfPlan,cancelBooking)
                isSelected = true
            }
        }
        changeOfPlan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cancel_reason=changeOfPlan.text.toString()
                changecheckbox(changeOfPlan,feelingUnwell,unexpectedCommitment,scheduleConflict,cancelBooking)
                isSelected = true
            }
        }

        imclose.setOnClickListener{
            cancelReasonBottomSheetDialog.dismiss()
        }

        cancelReasonBottomSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(cancelReasonBottomSheetDialog)
    }

    private fun sendCancelData() {

        val param: MutableMap<String, String> = HashMap()
        param["id"] = ""+ intent.getStringExtra("bookingid")
        param["reason"] = ""+cancel_reason

        Log.e("cancelBookingParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.cancelsession,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("cancelBooingRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        cancelReasonBottomSheetDialog.dismiss()
                        bookingStatus.text = "Booking Cancelled"
                        trainerDetailLayout.visibility = View.GONE
                        cancellationMsgCard.visibility = View.GONE
                        tvTrack.visibility = View.GONE
                        rescheduleCancelLayout.visibility = View.GONE
                        cancellationPolicyLayout.visibility = View.GONE
                        cancelRequest.visibility = View.GONE
                        rescheduleBookingText.visibility = View.GONE
                        linearQRCode.visibility = View.GONE
                        cancelledDetailLayout.visibility = View.VISIBLE
                        cancelTextMessage.text = resp.optJSONObject("data").optJSONObject("bookingDetail").optString("msg")
                        cancelledDateTime.text = "On ${resp.optJSONObject("data").optString("cancelled_at")}"
                    }
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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

    private fun cancelBookingAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.cancel_booking_bottom_sheet_dialog, null)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        val imclose=bottomSheet.findViewById<ImageView>(R.id.close)
        val tvCancel=bottomSheet.findViewById<TextView>(R.id.tvCancel)
        val tvrescheduleBooking=bottomSheet.findViewById<TextView>(R.id.tvrescheduleBooking)

        imclose.setOnClickListener{
            cancelBookingSheetDialog.dismiss()
        }

        tvCancel.setOnClickListener{
            cancelBookingSheetDialog.dismiss()
            cancelReasonBottomSheetDialog.show()
        }
        tvrescheduleBooking.setOnClickListener{
            reschedulingSheetDialog.show()
            cancelBookingSheetDialog.dismiss()

        }

        cancelBookingSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(cancelBookingSheetDialog)
    }

    override fun onResume() {
        super.onResume()
        isResumed1 = true

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            showGPSDisabledAlertToUser()
            return
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            registerReceiver(slot, IntentFilter("rescheduleTime"), RECEIVER_EXPORTED)
        else
            registerReceiver(slot, IntentFilter("rescheduleTime"))
    }

    val slot = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            tvRescheduleBooking.background = ResourcesCompat.getDrawable(context!!.resources,R.drawable.white_rectangle,context.theme)
            tvRescheduleBooking.setTextColor(ResourcesCompat.getColor(context.resources,R.color.buttontextcolor,context.theme))
            tvRescheduleBooking.setTypeface(null, Typeface.BOLD)
            slot_id=""+ intent!!.getStringExtra("slot_id")

            tvRescheduleBooking.setOnClickListener {
                if (slot_id!="")
                    sendRescheduleData()
            }
        }
    }

    private fun createLearnMoreAlert(cancellationPolicy: JSONObject?) {
        val bottomSheet = layoutInflater.inflate(R.layout.cancelpolicy_bottomsheet_dialog, null)
        cancelPolicyBottomSheetDialog.setContentView(bottomSheet)
        val imclose=bottomSheet.findViewById<ImageView>(R.id.close)
        val tvOk=bottomSheet.findViewById<TextView>(R.id.tvOk)
        val freeMsg=bottomSheet.findViewById<TextView>(R.id.freeMsg)
        val cancellationMsg=bottomSheet.findViewById<TextView>(R.id.cancellationMsg)
        val freeTextMsg=bottomSheet.findViewById<TextView>(R.id.freeTextMsg)
        val cancelTextMsg=bottomSheet.findViewById<TextView>(R.id.cancelTextMsg)

        freeMsg.text = cancellationPolicy!!.optString("freeMsg")
        cancellationMsg.text = cancellationPolicy.optString("cancelMsg")
        freeTextMsg.text = cancellationPolicy.optString("freeMsgText")
        cancelTextMsg.text = cancellationPolicy.optString("cancelMsgText")

        imclose.setOnClickListener{
            cancelPolicyBottomSheetDialog.dismiss()
        }
        tvOk.setOnClickListener{
            cancelPolicyBottomSheetDialog.dismiss()
        }
        animateBottomSheet(cancelPolicyBottomSheetDialog)
    }

    private fun reasonForReschedulingAlert() {
        var isSelected = false
        val bottomSheet = layoutInflater.inflate(R.layout.reasonfor_rescheduling_option, null)
      // reschedulingSheetDialog.setCancelable(false)
        //ReasonSection
        val Availability=bottomSheet.findViewById<CheckBox>(R.id.Availability)
        var imclose=bottomSheet.findViewById<ImageView>(R.id.imclose)
        var checkHealth=bottomSheet.findViewById<CheckBox>(R.id.checkHealth)
        var checkTravelPlus=bottomSheet.findViewById<CheckBox>(R.id.checkTravelPlus)
        var checkPersonal=bottomSheet.findViewById<CheckBox>(R.id.checkPersonal)
        var tvResaonReschedule=bottomSheet.findViewById<TextView>(R.id.tvResaonReschedule)
        linearReason=bottomSheet.findViewById<LinearLayout>(R.id.linearReason)
        linearSelectDate=bottomSheet.findViewById<LinearLayout>(R.id.linearSelectDate)
        tvRescheduleBooking=bottomSheet.findViewById<TextView>(R.id.tvRescheduleBooking)
        linearSelectSLot=bottomSheet.findViewById<LinearLayout>(R.id.linearSelectSLot)

        //Date section
        calendarView=bottomSheet.findViewById<MaterialCalendarView>(R.id.calendarView)
        var monthName=bottomSheet.findViewById<TextView>(R.id.monthName)
        var imRight=bottomSheet.findViewById<ImageView>(R.id.imRight)
        var imLeft=bottomSheet.findViewById<ImageView>(R.id.imLeft)

        //selectSLot
        var im_night=bottomSheet.findViewById<ImageView>(R.id.im_night)
        var im_mrng=bottomSheet.findViewById<ImageView>(R.id.im_mrng)
        var linearmrng=bottomSheet.findViewById<LinearLayout>(R.id.linearmrng)
        var linernight=bottomSheet.findViewById<LinearLayout>(R.id.linernight)
        recyclerViewTime=bottomSheet.findViewById<RecyclerView>(R.id.recyclerViewTime)
        tv=bottomSheet.findViewById<TextView>(R.id.tv)

        //SlotFunctionality


        im_night.setOnClickListener{
            slotshift="night"
            slot_id=""
            linernight.visibility= View.VISIBLE
            linearmrng.visibility= View.GONE
            im_night.visibility= View.GONE
            im_mrng.visibility= View.VISIBLE
            tvRescheduleBooking.setBackgroundResource(R.drawable.rectangle_btn)
            tvRescheduleBooking.setTextColor(application.resources.getColor(R.color.white))
            getSLotList()

        }
        im_mrng.setOnClickListener{
            slotshift="morning"
            slot_id=""
            linernight.visibility= View.GONE
            linearmrng.visibility= View.VISIBLE
            im_night.visibility= View.VISIBLE
            im_mrng.visibility= View.GONE
            tvRescheduleBooking.setBackgroundResource(R.drawable.rectangle_btn)
            tvRescheduleBooking.setTextColor(application.resources.getColor(R.color.white))
            getSLotList()

        }

        //SelectDateFunctinality
        val today = Calendar.getInstance()
        currentMonth = 0 + today.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        getAvailableSLots()

        var currentYear = today.get(Calendar.YEAR)
        monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"

        // Create a custom drawable for the rectangle
        val rectangleDrawable = ContextCompat.getDrawable(this, R.drawable.rectangle_background)

        // Create the custom decorator
        val decorator = SelectedDateDecorator(rectangleDrawable!!)

        calendarView.setWeekDayFormatter { dayOfWeek ->
            when (dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
                else -> ""
            }
        }
        // Add the decorator to the calendar
        calendarView.addDecorator(decorator)

        calendarView.addDecorator(DisabledOtherDatesDecorator(
            currentMonth,
            currentYear,
            resources.getColor(R.color.smallTextcolor),
            today.get(Calendar.DAY_OF_MONTH)
        ))

        calendarView.topbarVisible = false

        calendarView.setOnMonthChangedListener { _, date ->
            // Update current month and year
            currentMonth = date.month
            currentYear = date.year
            monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"
            // Refresh decorators
            calendarView.removeDecorators() // Clear previous decorators
            calendarView.addDecorator(decorator) // Re-add event dots
            calendarView.addDecorator(
                DisabledOtherDatesDecorator(
                    currentMonth,
                    currentYear,
                    resources.getColor(R.color.smallTextcolor),
                    date.day
                )
            ) // Update disabled dates
            Log.e("currentMonth",""+currentMonth)
            getAvailableSLots()


        }
        imRight.setOnClickListener{
            calendarView.goToNext()

        }
        imLeft.setOnClickListener{
            calendarView.goToPrevious()

        }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            decorator.setSelectedDate(date)
            decorator.setSelectedDate(date)
            widget.invalidateDecorators()
            linearSelectDate.visibility=View.GONE
            linearSelectSLot.visibility=View.VISIBLE
            selectDate="${date.year}-${date.month}-${date.day}"
            getSLotList()

            /*if(sharedPreferences.getString("calendartype","")!!.equals("TraineratGym")){
                val intent = Intent(this, SelectTimefromBookaTrainerCalendarActivity2::class.java)
                intent.putExtra("selectedDate","${date.day}-${date.month}-${date.year}")
                intent.putExtra("day",date.day-1)
                intent.putExtra("month",date.month)
                startActivity(intent)
            }else{
                val intent = Intent(this, SelectTime::class.java)
                intent.putExtra("selectedDate","${date.day}-${date.month}-${date.year}")
                intent.putExtra("day",date.day-1)
                intent.putExtra("month",date.month)
                startActivity(intent)
            }
*/

        }


        //Reason Functionality
        tvResaonReschedule.setOnClickListener{
            if(isSelected){
                linearSelectDate.visibility=View.VISIBLE
                tvRescheduleBooking.visibility=View.VISIBLE
                linearReason.visibility=View.GONE
            }
        }

        imclose.setOnClickListener{
        /*    linearReason.visibility=View.VISIBLE
            linearSelectDate.visibility=View.GONE
            linearSelectSLot.visibility=View.GONE
            tvRescheduleBooking.visibility=View.GONE*/
            reschedulingSheetDialog.dismiss()
            reaonse_value=""
            isSelected = false
       /*     isSelected = false
            Availability.isChecked=false
            checkHealth.isChecked=false
            checkTravelPlus.isChecked=false
            checkPersonal.isChecked=false
            tvResaonReschedule.setBackgroundResource(R.drawable.rectangle_btn)
            tvResaonReschedule.setTextColor(application.resources.getColor(R.color.white))

            tvRescheduleBooking.setBackgroundResource(R.drawable.rectangle_btn)
            tvRescheduleBooking.setTextColor(application.resources.getColor(R.color.white))*/
            reasonForReschedulingAlert()
        }

        Availability.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isSelected = true
                reaonse_value=Availability.text.toString()
                changecheckbox(Availability,checkHealth,checkTravelPlus,checkPersonal,tvResaonReschedule)

            }
        }
        checkHealth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isSelected = true
                reaonse_value=checkHealth.text.toString()
                changecheckbox(checkHealth,Availability,checkTravelPlus,checkPersonal,tvResaonReschedule)

            }
        }
        checkTravelPlus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isSelected = true
                reaonse_value=checkTravelPlus.text.toString()
                changecheckbox(checkTravelPlus,checkHealth,Availability,checkPersonal,tvResaonReschedule)

            }
        }
        checkPersonal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isSelected = true
                reaonse_value=checkPersonal.text.toString()
                changecheckbox(checkPersonal,checkHealth,checkTravelPlus,Availability,tvResaonReschedule)

            }
        }
        reschedulingSheetDialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (reschedulingSheetDialog.isShowing){
                    if (linearSelectSLot.visibility==View.VISIBLE){
                        linearSelectSLot.visibility=View.GONE
                        linearSelectDate.visibility=View.VISIBLE
                        tvRescheduleBooking.setBackgroundResource(R.drawable.rectangle_btn)
                        tvRescheduleBooking.setTextColor(application.resources.getColor(R.color.white))
                        slot_id=""
                    }else if (linearSelectDate.visibility==View.VISIBLE){
                        linearSelectDate.visibility=View.GONE
                        tvRescheduleBooking.visibility=View.GONE
                        linearReason.visibility=View.VISIBLE
                    }else{
                        reschedulingSheetDialog.dismiss()
                    }
                }else{
                    finish()
                }
                true // consume the event
            } else {
                false
            }
        }
        reschedulingSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(reschedulingSheetDialog)
    }

    private fun sendRescheduleData() {

        val param: MutableMap<String, String> = HashMap()
        param["id"] = ""+ intent.getStringExtra("bookingid")
        param["new_slot_id"] = ""+slot_id
        param["reason"] = ""+reaonse_value

        Log.e("rescheduleconfirmParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.reschedulesession,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("rescheduleRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("success")){
                        reschedulingSheetDialog.dismiss()
                        cancelRequest.visibility = View.VISIBLE
                        rescheduleBookingText.visibility = View.VISIBLE
                        cancellationMsgCard.visibility = View.GONE
                        tvTrack.visibility = View.GONE
                        rescheduleCancelLayout.visibility = View.GONE

                    }else{

                    }

                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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

    private fun getSLotList() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api= ApiURL.getallslots+ intent.getStringExtra("bookingid")+"&timing="+slotshift+"&date="+selectDate

        Log.e("GetAllSlotsAPi",api)
        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                selectTimeModelList.clear()
                Log.e("getAllSlotsResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    var jsonArraySlots=jsonObj.optJSONObject("data").optJSONArray("slots")
                    Log.e("size",""+jsonArraySlots.length())
                    if (jsonArraySlots.length()>0) {

                        for(i in 0 until jsonArraySlots.length()){
                            val jsonObject1 = jsonArraySlots.optJSONObject(i)
                            var selectTImeModel=SelectTImeModel()

                            selectTImeModel.timeslot=jsonObject1.optString("time")
                            selectTImeModel.isBooked=jsonObject1.optString("isBooked")
                            selectTImeModel.id=jsonObject1.optString("id")
                            selectTimeModelList.add(selectTImeModel)
                        }
                        recyclerViewTime.visibility=View.VISIBLE
                        tv.visibility=View.GONE
                        var selectTimeAdapter = SelectRescheduleTimeAdapter(context_, selectTimeModelList)
                        recyclerViewTime.adapter = selectTimeAdapter

                    }
                    else{
                        tv.visibility=View.VISIBLE
                        recyclerViewTime.visibility=View.GONE


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

    private fun reschedulingDateAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.reasonfor_rescheduling_selectdate, null)
        reschedulingDateSheetDialog.setCancelable(false)

        calendarView=bottomSheet.findViewById<MaterialCalendarView>(R.id.calendarView)
        var tvcontinue=bottomSheet.findViewById<TextView>(R.id.tvcontinue)
        var monthName=bottomSheet.findViewById<TextView>(R.id.monthName)
        var imclose=bottomSheet.findViewById<ImageView>(R.id.imclose)
        var imRight=bottomSheet.findViewById<ImageView>(R.id.imRight)
        var imLeft=bottomSheet.findViewById<ImageView>(R.id.imLeft)
        imclose.setOnClickListener{
            reschedulingDateSheetDialog.dismiss()
            reschedulingSheetDialog.show()
            tvcontinue.setBackgroundResource(R.drawable.rectangle_btn)
            tvcontinue.setTextColor(application.resources.getColor(R.color.white))
        }
        tvcontinue.setOnClickListener{

        }


        val today = Calendar.getInstance()
        currentMonth = 0 + today.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        getAvailableSLots()

        var currentYear = today.get(Calendar.YEAR)
        monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"

        // Create a custom drawable for the rectangle
        val rectangleDrawable = ContextCompat.getDrawable(this, R.drawable.rectangle_background)

        // Create the custom decorator
        val decorator = SelectedDateDecorator(rectangleDrawable!!)

        calendarView.setWeekDayFormatter { dayOfWeek ->
            when (dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
                else -> ""
            }
        }
        // Add the decorator to the calendar
        calendarView.addDecorator(decorator)

        calendarView.addDecorator(DisabledOtherDatesDecorator(
            currentMonth,
            currentYear,
            resources.getColor(R.color.smallTextcolor),
            today.get(Calendar.DAY_OF_MONTH)
        ))

        calendarView.topbarVisible = false

        calendarView.setOnMonthChangedListener { _, date ->
            // Update current month and year
            currentMonth = date.month
            currentYear = date.year
            monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"
            // Refresh decorators
            calendarView.removeDecorators() // Clear previous decorators
            calendarView.addDecorator(decorator) // Re-add event dots
            calendarView.addDecorator(
                DisabledOtherDatesDecorator(
                    currentMonth,
                    currentYear,
                    resources.getColor(R.color.smallTextcolor),
                    date.day
                )
            ) // Update disabled dates
            Log.e("currentMonth",""+currentMonth)
            getAvailableSLots()


        }
        imRight.setOnClickListener{
            calendarView.goToNext()

        }
        imLeft.setOnClickListener{
            calendarView.goToPrevious()

        }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            decorator.setSelectedDate(date)
            decorator.setSelectedDate(date)
            widget.invalidateDecorators()
            reschedulingTimeSheetDialog.show()
            reschedulingDateSheetDialog.dismiss()
            /*if(sharedPreferences.getString("calendartype","")!!.equals("TraineratGym")){
                val intent = Intent(this, SelectTimefromBookaTrainerCalendarActivity2::class.java)
                intent.putExtra("selectedDate","${date.day}-${date.month}-${date.year}")
                intent.putExtra("day",date.day-1)
                intent.putExtra("month",date.month)
                startActivity(intent)
            }else{
                val intent = Intent(this, SelectTime::class.java)
                intent.putExtra("selectedDate","${date.day}-${date.month}-${date.year}")
                intent.putExtra("day",date.day-1)
                intent.putExtra("month",date.month)
                startActivity(intent)
            }
*/

        }
        reschedulingDateSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(reschedulingDateSheetDialog)
    }


    private fun getAvailableSLots() {
        var selectedMonth=""
        if (currentMonth<10){
            selectedMonth = "0$currentMonth" // Calendar.MONTH is 0-based
        }else{
            selectedMonth ="$currentMonth"// Calendar.MONTH is 0-based
        }

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        api= ApiURL.gettrainerslot+ intent.getStringExtra("bookingid")+"&month="+selectedMonth

        Log.e("getTrainerSlotAPi",api)
        GetMethod(api,applicationContext).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("trainerSlotRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        val eventDecorators = mutableListOf<EventMultiColorDecorator>()

                        var selectColor: Int
                        val jsonArray=resp.optJSONArray("data")

                        if (jsonArray != null) {
                            for(i in 0 until jsonArray.length()){
                                val jsonObject1 = jsonArray.optJSONObject(i)
                                selectColor = if (jsonObject1.optString("status").equals("Closed")){
                                    resources.getColor(R.color.smallTextcolor)
                                }else if (jsonObject1.optString("status").equals("Available")){
                                    resources.getColor(R.color.available)
                                }else if (jsonObject1.optString("status").equals("Fully Booked")){
                                    resources.getColor(R.color.fully_booked)
                                }else{
                                    resources.getColor(R.color.fast_filling)
                                }

                                val fulldate=jsonObject1.optString("date")
                                val (year, month, day) = fulldate.split("-")

                                val eventDecorator = EventMultiColorDecorator(selectColor, listOf(CalendarDay.from(year.toInt(), month.toInt(), day.toInt())))
                                eventDecorators.add(eventDecorator)
                            }
                        }

                        eventDecorators.forEach { calendarView.addDecorator(it) }

                    }
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"), Toast.LENGTH_SHORT).show()
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
    private fun reschedulingTimeAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.reasonfor_rescheduling_selecttime, null)
        var im_night=bottomSheet.findViewById<ImageView>(R.id.im_night)
        var im_mrng=bottomSheet.findViewById<ImageView>(R.id.im_mrng)
        var linearmrng=bottomSheet.findViewById<LinearLayout>(R.id.linearmrng)
        var linernight=bottomSheet.findViewById<LinearLayout>(R.id.linernight)
        var imclose=bottomSheet.findViewById<ImageView>(R.id.imclose)
        //tvcontinue=bottomSheet.findViewById(R.id.tvcontinue)
        var recyclerViewTime=bottomSheet.findViewById<RecyclerView>(R.id.recyclerViewTime)



        im_night.setOnClickListener{
            linernight.visibility= View.VISIBLE
            linearmrng.visibility= View.GONE
            im_night.visibility= View.GONE
            im_mrng.visibility= View.VISIBLE
        }
        im_mrng.setOnClickListener{
            linernight.visibility= View.GONE
            linearmrng.visibility= View.VISIBLE
            im_night.visibility= View.VISIBLE
            im_mrng.visibility= View.GONE
        }

        imclose.setOnClickListener{
            reschedulingTimeSheetDialog.dismiss()
            reschedulingDateSheetDialog.show()

           // tvcontinue.setBackgroundResource(R.drawable.rectangle_btn)
           // tvcontinue.setTextColor(application.resources.getColor(R.color.white))
        }
        for (i in 0..9) {
            var selectTimeMode= SelectTImeModel()
            selectTimeMode.timeslot="11:00 - 12:00"
            selectTimeModelList.add(selectTimeMode)
        }
        var selectTimeAdapter = SelectRescheduleTimeAdapter(context_, selectTimeModelList)
        recyclerViewTime.adapter = selectTimeAdapter
        reschedulingTimeSheetDialog.setContentView(bottomSheet)

        animateBottomSheet(reschedulingTimeSheetDialog)
    }

    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    private fun changecheckbox(
        availability: CheckBox?,
        checkHealth: CheckBox?,
        checkTravelPlus: CheckBox?,
        checkPersonal: CheckBox?,
        tvResaonReschedule: TextView?
    ) {
        availability!!.isChecked=true
        checkHealth!!.isChecked = false
        checkTravelPlus!!.isChecked = false
        checkPersonal!!.isChecked = false
        tvResaonReschedule!!.background = resources.getDrawable(R.drawable.white_rectangle)
        tvResaonReschedule.setTextColor(resources.getColor(R.color.buttontextcolor))
        tvResaonReschedule.setTypeface(null, Typeface.BOLD)
    }
    private fun checkLocationPermission(): Boolean {
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        /*  if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
             listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
         }*/
        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            //requestPermissions(listPermissionsNeeded.toTypedArray<String>(), 123)
            return false
        }
        return true
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
                Log.i("DEBUG", "permission granted")
            } else {
                // if permission denied then check whether never ask
                // again is selected or not by making use of
                // !ActivityCompat.shouldShowRequestPermissionRationale(
                // requireActivity(), Manifest.permission.CAMERA)
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setCancelable(false)
                    builder.setTitle(resources.getString(R.string.LocationPermission))
                    builder.setMessage(resources.getString(R.string.grantlocationpermission))
                    builder.setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        dialog.dismiss()
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("package:" + this.packageName)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(intent)
                    }
                    builder.create()
                    builder.show()

                    // User selected the Never Ask Again Option
                }
                else checkLocationPermission()
                Log.i("DEBUG", "permission denied")
            }
        }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(
            this
        )
        alertDialogBuilder.setMessage(resources.getString(R.string.gpsisenabled))
            .setCancelable(false)
            .setPositiveButton(R.string.EnableGPS,
                DialogInterface.OnClickListener { dialog, id ->
                    showProgress = true
                    val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                })
        /*
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        checkLocationPermission();
                    }
                });
*/
        val alert = alertDialogBuilder.create()
        alert.show()
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        //mMap.clear();

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED*/
        ) {

        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, (20 * 1000).toLong())
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        if (isResumed1) {
                            showProgress = false
                            longitude = location.longitude
                            latitude = location.latitude
                            LatLng(latitude!!, longitude!!)
                            Log.e("latitude",""+latitude)
                            Log.e("longitude",""+longitude)
                            var addresses: List<Address>? = null
                            var address = ""
                            var cityState = ""
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    geocoder.getFromLocation(
                                        latitude!!, longitude!!, 1
                                    ) { addressList: List<Address?>? ->
                                        address = addressList!![0]!!.getAddressLine(0)
                                        cityState = addressList!![0]!!.subLocality +", "+ addressList!![0]!!.locality
                                    }
                                } else {
                                    addresses =
                                        geocoder.getFromLocation(latitude!!, longitude!!, 1)!!
                                    address = addresses[0].getAddressLine(0)
                                    cityState = addresses[0].subLocality +", "+ addresses[0].locality
                                }
                                getBookingDetail()


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                //  tvLocation.text = address

                            }, 500)


                            mFusedLocationClient.removeLocationUpdates(locationCallback)
                            isResumed1 = false
                        }
                    }
                }
            }
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }



}