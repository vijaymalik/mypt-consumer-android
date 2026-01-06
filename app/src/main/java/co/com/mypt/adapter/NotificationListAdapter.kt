package co.com.mypt.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.BookingScreen.UpcomingBookingDetails
import co.com.mypt.CalendarSelectedDate.SelectedDateDecorator
import co.com.mypt.Notification.AcceptBookingActivity
import co.com.mypt.Notification.NotificationListActivity
import co.com.mypt.Notification.SelectNotificationAddressActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.calendarUtils.DisabledOtherDatesDecorator
import co.com.mypt.calendarUtils.EventMultiColorDecorator
import co.com.mypt.interfaces.OnTimeSlotSelectedListener
import co.com.mypt.model.NotificationListModel
import co.com.mypt.model.SelectTImeModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.json.JSONObject
import org.threeten.bp.DayOfWeek
import java.text.DateFormatSymbols
import java.util.Calendar

class NotificationListAdapter(
   var applicationContext: Context,
    var notificationArrayList: ArrayList<NotificationListModel>): RecyclerView.Adapter<NotificationListAdapter.NotificationHolder>() {
    lateinit  var linearReason : LinearLayout
    lateinit  var linearSelectDate : LinearLayout
    lateinit  var linearSelectSLot : LinearLayout
    lateinit  var tvRescheduleBooking : TextView
    lateinit  var tv : TextView
    lateinit var calendarView : MaterialCalendarView
    lateinit var recyclerViewTime : RecyclerView
    var slotshift="morning"
    var currentMonth=-1
    var selectDate=""
    var reaonse_value=""
    var slot_id=""

    lateinit var reschedulingSheetDialog: BottomSheetDialog
    lateinit var cancelBottomSheetDialog: BottomSheetDialog
    var selectTimeModelList : ArrayList<SelectTImeModel> = ArrayList()

    class NotificationHolder (view: View):RecyclerView.ViewHolder(view){
        var rescheduleBooking=view.findViewById<TextView>(R.id.rescheduleBooking)
        var tvAccept=view.findViewById<TextView>(R.id.tvAccept)
        var tvsessionDetail=view.findViewById<TextView>(R.id.tvsessionDetail)
        var tvTrainer_name=view.findViewById<TextView>(R.id.tvTrainer_name)
        var tvLocation=view.findViewById<TextView>(R.id.tvLocation)
        var tvdistance=view.findViewById<TextView>(R.id.tvdistance)
        var avgRating=view.findViewById<TextView>(R.id.avgRating)
        var tvAddress=view.findViewById<TextView>(R.id.tvAddress)
        var imTrainer=view.findViewById<ImageView>(R.id.imTrainer)
        var linearAddress=view.findViewById<LinearLayout>(R.id.linearAddress)
        var rescheduleCancelLayout=view.findViewById<LinearLayout>(R.id.rescheduleCancelLayout)
        var linearDeny=view.findViewById<LinearLayout>(R.id.linearDeny)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_list, parent, false)
        return NotificationHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotificationHolder,
        position: Int
    ) {
        var notificationListModel=notificationArrayList.get(position)
        Glide.with(applicationContext).load(notificationListModel.trainer_image).fitCenter().error(R.drawable.guest_user).into(holder.imTrainer)
        holder.tvTrainer_name.text = notificationListModel.trainer
        holder.tvdistance.text = notificationListModel.distance
        holder.tvLocation.text = notificationListModel.location
        holder.avgRating.text = notificationListModel.averageRating
        holder.tvsessionDetail.text = notificationListModel.scheduleMsg
        holder.tvAccept.tag = position
        holder.tvAddress.tag = position
        holder.linearDeny.tag = position
        holder.rescheduleBooking.tag = position
        if (notificationListModel.location == ""){
            holder.linearAddress.visibility=View.VISIBLE
            holder.rescheduleCancelLayout.alpha = 0.3f
            holder.rescheduleBooking.isEnabled = false
            holder.tvAccept.isEnabled = false
        }else{
            holder.linearAddress.visibility=View.GONE
            holder.rescheduleCancelLayout.alpha = 1f
            holder.rescheduleBooking.isEnabled = true
            holder.tvAccept.isEnabled = true
        }
        holder.tvAccept.setOnClickListener {
            var h=it.tag
            var notificationListModel=notificationArrayList.get(h as Int)
            var intent= Intent(applicationContext, AcceptBookingActivity::class.java)
            intent.putExtra("id",notificationListModel.id)
            Log.e("idnotif",""+notificationListModel.id)
            applicationContext.startActivity(intent)
        }
        holder.tvAddress.setOnClickListener {
            var intent= Intent(applicationContext, SelectNotificationAddressActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            applicationContext.startActivity(intent)
        }
        holder.linearDeny.setOnClickListener {
           var h=it.tag
            var notificationListModel= notificationArrayList[h as Int]
            cancelBookingBottomSheet(notificationListModel)

        }


        holder.rescheduleBooking.setOnClickListener {
            var h=it.tag
            var notificationListModel=notificationArrayList.get(h as Int)
            reasonForReschedulingAlert(notificationListModel.id,notificationListModel.type)
        }

    }

    private fun cancelBookingBottomSheet(notificationListModel: NotificationListModel) {
        cancelBottomSheetDialog = BottomSheetDialog((applicationContext as NotificationListActivity), R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = LayoutInflater.from(applicationContext as NotificationListActivity).inflate(R.layout.cancel_new_booking_bottom_sheet_dialog, null)
        //cancelBottomSheetDialog.setCancelable(false)

        val close=bottomSheet.findViewById<ImageView>(R.id.close)
        val dateTime=bottomSheet.findViewById<TextView>(R.id.dateTime)
        val tvTrainer_name=bottomSheet.findViewById<TextView>(R.id.tvTrainer_name)
        val tvdistance=bottomSheet.findViewById<TextView>(R.id.tvdistance)
        val tvLocation=bottomSheet.findViewById<TextView>(R.id.tvLocation)
        val tvCancel=bottomSheet.findViewById<TextView>(R.id.tvCancel)
        val imTrainer=bottomSheet.findViewById<ImageView>(R.id.imTrainer)

        dateTime.text = notificationListModel.scheduleMsg
        tvTrainer_name.text = notificationListModel.trainer
        tvdistance.text = notificationListModel.distance
        tvLocation.text = notificationListModel.location
        dateTime.text = notificationListModel.scheduleMsg

        Glide.with(applicationContext).load(notificationListModel.trainer_image).fitCenter().error(R.drawable.guest_user).into(imTrainer)

        close.setOnClickListener {
            cancelBottomSheetDialog.dismiss()
        }

        tvCancel.setOnClickListener {
            (applicationContext as NotificationListActivity).sendCancelData(notificationListModel.id,cancelBottomSheetDialog)
        }
        cancelBottomSheetDialog.setContentView(bottomSheet)
        cancelBottomSheetDialog.show()
        animateBottomSheet(cancelBottomSheetDialog)
    }

    private fun reasonForReschedulingAlert(id: String, type: String) {
        var isSelected = false
        reschedulingSheetDialog = BottomSheetDialog((applicationContext as NotificationListActivity), R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = LayoutInflater.from(applicationContext as NotificationListActivity).inflate(R.layout.reasonfor_rescheduling_option, null)
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

        im_night.setOnClickListener{
            slotshift="night"
            slot_id=""
            linernight.visibility= View.VISIBLE
            linearmrng.visibility= View.GONE
            im_night.visibility= View.GONE
            im_mrng.visibility= View.VISIBLE
            tvRescheduleBooking.setBackgroundResource(R.drawable.rectangle_btn)
            tvRescheduleBooking.setTextColor(applicationContext.resources.getColor(R.color.white))
            getSLotList(id,type)

        }
        im_mrng.setOnClickListener{
            slotshift="morning"
            slot_id=""
            linernight.visibility= View.GONE
            linearmrng.visibility= View.VISIBLE
            im_night.visibility= View.VISIBLE
            im_mrng.visibility= View.GONE
            tvRescheduleBooking.setBackgroundResource(R.drawable.rectangle_btn)
            tvRescheduleBooking.setTextColor(applicationContext.resources.getColor(R.color.white))
            getSLotList(id, type)

        }

        //SelectDateFunctinality
        val today = Calendar.getInstance()
        currentMonth = 0 + today.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        getAvailableSLots(id)

        var currentYear = today.get(Calendar.YEAR)
        monthName.text = DateFormatSymbols().months[currentMonth-1] +" $currentYear"

        // Create a custom drawable for the rectangle
        val rectangleDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.rectangle_background)

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
            applicationContext.resources.getColor(R.color.smallTextcolor),
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
                    applicationContext.resources.getColor(R.color.smallTextcolor),
                    date.day
                )
            ) // Update disabled dates
            Log.e("currentMonth",""+currentMonth)
            getAvailableSLots(id)
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
            getSLotList(id, type)

        }


        //Reason Functionality
        tvResaonReschedule.setOnClickListener{
            if(isSelected){
                linearSelectDate.visibility=View.VISIBLE
                tvRescheduleBooking.visibility=View.VISIBLE
                linearReason.visibility=View.GONE
            }
           /* var intent= Intent(applicationContext, RescheduleBookScreenActivity::class.java)
            applicationContext.startActivity(intent)*/
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
           // reasonForReschedulingAlert()
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
                        tvRescheduleBooking.setTextColor(applicationContext.resources.getColor(R.color.white))
                        slot_id=""
                    }else if (linearSelectDate.visibility==View.VISIBLE){
                        linearSelectDate.visibility=View.GONE
                        tvRescheduleBooking.visibility=View.GONE
                        linearReason.visibility=View.VISIBLE
                    }else{
                        reschedulingSheetDialog.dismiss()
                    }
                }else{
                    //applicationContext.finish()
                }
                true // consume the event
            } else {
                false
            }
        }
        reschedulingSheetDialog.setContentView(bottomSheet)
        reschedulingSheetDialog.show()

        animateBottomSheet(reschedulingSheetDialog)
    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    override fun getItemCount(): Int {
       return  notificationArrayList.size
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
        tvResaonReschedule!!.background = applicationContext.resources.getDrawable(R.drawable.white_rectangle)
        tvResaonReschedule.setTextColor(applicationContext.resources.getColor(R.color.buttontextcolor))
        tvResaonReschedule.setTypeface(null, Typeface.BOLD)
    }
    private fun getAvailableSLots(id: String) {
        var selectedMonth=""
        selectedMonth = if (currentMonth<10){
            "0$currentMonth" // Calendar.MONTH is 0-based
        }else{
            "$currentMonth"// Calendar.MONTH is 0-based
        }

        val progressDialog: Dialog = ProgressDialog.progressDialog(applicationContext,"")
        progressDialog.show()
        var api=""
        api= ApiURL.gettrainerslot+ id+"&month="+selectedMonth

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
                                    applicationContext.resources.getColor(R.color.smallTextcolor)
                                }else if (jsonObject1.optString("status").equals("Available")){
                                    applicationContext.resources.getColor(R.color.available)
                                }else if (jsonObject1.optString("status").equals("Fully Booked")){
                                   applicationContext.resources.getColor(R.color.fully_booked)
                                }else{
                                    applicationContext.resources.getColor(R.color.fast_filling)
                                }

                                val fulldate=jsonObject1.optString("date")
                                val (year, month, day) = fulldate.split("-")

                                val eventDecorator = EventMultiColorDecorator(selectColor, listOf(
                                    CalendarDay.from(year.toInt(), month.toInt(), day.toInt())))
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
 private fun getSLotList(id: String, type: String) {
     val progressDialog: Dialog = ProgressDialog.progressDialog(applicationContext,"")
     progressDialog.show()
     var api=""
     api= ApiURL.getallslots+id+"&timing="+slotshift+"&date="+selectDate

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
                     var selectTimeAdapter =PendingRescheduleTimeAdapter(applicationContext, selectTimeModelList, object :
                         OnTimeSlotSelectedListener {
                         override fun onTimeSlotSelected(timeSlot: String) {
                             tvRescheduleBooking.background = ResourcesCompat.getDrawable(applicationContext!!.resources,R.drawable.white_rectangle,applicationContext.theme)
                             tvRescheduleBooking.setTextColor(ResourcesCompat.getColor(applicationContext.resources,R.color.buttontextcolor,applicationContext.theme))
                             tvRescheduleBooking.setTypeface(null, Typeface.BOLD)
                             slot_id=timeSlot

                             tvRescheduleBooking.setOnClickListener {
                                 if (slot_id!="")
                                     sendRescheduleData(id,type)
                             }
                         }
                     })
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

    private fun sendRescheduleData(id: String, type: String) {

        val param: MutableMap<String, String> = HashMap()
        param["id"] = ""+id
        param["new_slot_id"] = ""+slot_id
        param["reason"] = ""+reaonse_value

        Log.e("reschedulePendingParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(applicationContext,"")
        progressDialog.show()

        PostMethod(ApiURL.reschedulesession,param, applicationContext).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("reschedulePeningRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("success")){
                        reschedulingSheetDialog.dismiss()
                        val intent = Intent(applicationContext, UpcomingBookingDetails::class.java)
                        intent.putExtra("bookingid",id)
                        intent.putExtra("type",type)
                        applicationContext.startActivity(intent)
                        (applicationContext as NotificationListActivity).finish()
                    }else{
                        Toast.makeText(applicationContext,resp.optString("message"),Toast.LENGTH_SHORT).show()
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

}
