package co.com.mypt.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog

import co.com.mypt.R
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.adapter.CancelBookingAdapter
import co.com.mypt.adapter.CertificateAdapter
import co.com.mypt.adapter.ClassGalleryAdapter
import co.com.mypt.adapter.CompletedBookingAdapter
import co.com.mypt.adapter.SpecialitiesAdapter
import co.com.mypt.adapter.UpcomingAdapter
import co.com.mypt.model.CancelbokingModel
import co.com.mypt.model.CertificateModel
import co.com.mypt.model.CompletedbokingModel
import co.com.mypt.model.GalleryModel
import co.com.mypt.model.SpecialitiesModel
import co.com.mypt.model.UpcomingbokingModel
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.util.Calendar


class BookingFragment : Fragment() {
    var locationType=""
    var session_Type=""
    var selectedfulldate=""
    var selectedType="2"
    var msg=""
    lateinit var tvUpcoming:TextView
    lateinit var tvCacel:TextView
    lateinit var tvFilter:TextView
    lateinit var tvCompleted:TextView
    lateinit var tvMonth:TextView
    lateinit var tvBookSession:TextView
    lateinit var standard_bottom_sheet:LinearLayout
    lateinit var recycler:RecyclerView
    lateinit var linearNoSession:LinearLayout
    var upComingBookkingModelList :ArrayList<UpcomingbokingModel> = ArrayList()
    var cancelBookkingModelList :ArrayList<CancelbokingModel> = ArrayList()
    var completedBookkingModelList :ArrayList<CompletedbokingModel> = ArrayList()
    lateinit var filterBottomSheetDialog:BottomSheetDialog
    lateinit var monthBottomSheetDialog:BottomSheetDialog
    var selectedMonth = ""
    var selectedYear=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       var view=inflater.inflate(R.layout.fragment_booking, container, false)
        tvUpcoming=view.findViewById(R.id.tvUpcoming)
        tvCacel=view.findViewById(R.id.tvCacel)
        tvBookSession=view.findViewById(R.id.tvBookSession)
        tvCompleted=view.findViewById(R.id.tvCompleted)
        recycler =view.findViewById(R.id.recycler)
        tvMonth =view.findViewById(R.id.tvMonth)
        tvFilter =view.findViewById(R.id.tvFilter)
        linearNoSession =view.findViewById(R.id.linearNoSession)
        monthBottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        filterBottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)

        createDatePicker()
        createFilterDialog()

        tvMonth.setOnClickListener{
            monthBottomSheetDialog.show()
        }
        tvFilter.setOnClickListener{
            filterBottomSheetDialog.show()
        }
        tvBookSession.setOnClickListener{
            var intent= Intent(activity, HomeGymTrainerActivity::class.java)

            startActivity(intent)
        }


        tvUpcoming.setOnClickListener{
            selectedType="2"
            session_Type=""
            selectedfulldate=""
            locationType=""

            tvUpcoming.setTextColor(resources.getColor(R.color.black))
            tvCacel.setTextColor(resources.getColor(R.color.white))
            tvCompleted.setTextColor(resources.getColor(R.color.white))
            tvCacel.background = null
            tvCompleted.background = null
            tvUpcoming.background = resources.getDrawable(R.drawable.white_stroke_rectangle)
            createDatePicker()
            createFilterDialog()
            getData()

        }
        tvCacel.setOnClickListener{
            selectedType="0"
            session_Type=""
            selectedfulldate=""
            locationType=""

            tvCacel.setTextColor(resources.getColor(R.color.black))
            tvUpcoming.setTextColor(resources.getColor(R.color.white))
            tvCompleted.setTextColor(resources.getColor(R.color.white))
            tvUpcoming.background = null
            tvCompleted.background = null
            tvCacel.background = resources.getDrawable(R.drawable.white_stroke_rectangle)
            createDatePicker()
            createFilterDialog()
            getData()

        }
        tvCompleted.setOnClickListener{
            selectedType="1"
            session_Type=""
            locationType=""
            selectedfulldate=""
            tvCompleted.setTextColor(resources.getColor(R.color.black))
            tvCacel.setTextColor(resources.getColor(R.color.white))
            tvUpcoming.setTextColor(resources.getColor(R.color.white))
            tvUpcoming.background = null
            tvCacel.background = null
            tvCompleted.background = resources.getDrawable(R.drawable.white_stroke_rectangle)
            createDatePicker()
            createFilterDialog()
            getData()

        }
        return view
    }

    private fun getData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()
        var api=""
        api= ApiURL.getbooking+selectedType+"&date="+selectedfulldate+"&session_type="+session_Type+"&location="+locationType

        android.util.Log.e("getbookingApi",""+api)

        GetMethod(api,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                upComingBookkingModelList.clear()
                cancelBookkingModelList.clear()
                completedBookkingModelList.clear()
                android.util.Log.e("getBookingResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            if (selectedType=="2"){
                                for (i in 0 until jsonArray.length()) {
                                    var jsonObject=jsonArray.optJSONObject(i)
                                    var upcomingbokingModel= UpcomingbokingModel()
                                    upcomingbokingModel.id=jsonObject.optString("id")
                                    upcomingbokingModel.type=jsonObject.optString("type")
                                    upcomingbokingModel.timing=jsonObject.optString("timing")
                                    upcomingbokingModel.session_type=jsonObject.optString("session_type")
                                    upcomingbokingModel.duration=jsonObject.optString("duration")
                                    upcomingbokingModel.trainer=jsonObject.optString("trainer")
                                    upcomingbokingModel.location=jsonObject.optString("location")
                                    upcomingbokingModel.is_reschedule=jsonObject.optString("is_reschedule")
                                    upcomingbokingModel.msg=jsonObject.optString("msg")
                                    upcomingbokingModel.is_Trainer=jsonObject.optString("isTrainer")
                                    upcomingbokingModel.workout_focus=jsonObject.optJSONArray("workout_focus")
                                    upComingBookkingModelList.add(upcomingbokingModel)
                                }
                                var activityAdapter = UpcomingAdapter(activity, upComingBookkingModelList)
                                recycler.adapter = activityAdapter
                            }else if (selectedType=="0"){
                                for (i in 0 until jsonArray.length()) {
                                    var jsonObject=jsonArray.optJSONObject(i)
                                    var cancelbokingModel= CancelbokingModel()
                                    cancelbokingModel.id=jsonObject.optString("id")
                                    cancelbokingModel.type=jsonObject.optString("type")
                                    cancelbokingModel.timing=jsonObject.optString("timing")
                                    cancelbokingModel.session_type=jsonObject.optString("session_type")
                                    cancelbokingModel.duration=jsonObject.optString("duration")
                                    cancelbokingModel.trainer=jsonObject.optString("trainer")
                                    cancelbokingModel.location=jsonObject.optString("location")
                                    cancelbokingModel.is_reschedule=jsonObject.optString("is_reschedule")
                                    cancelbokingModel.msg=jsonObject.optString("msg")
                                    cancelbokingModel.is_Trainer=jsonObject.optString("isTrainer")
                                    cancelbokingModel.workout_focus=jsonObject.optJSONArray("workout_focus")
                                    cancelBookkingModelList.add(cancelbokingModel)
                                }
                                var cancelAdapter = CancelBookingAdapter(activity, cancelBookkingModelList)
                                recycler.adapter = cancelAdapter
                            }else{
                                for (i in 0 until jsonArray.length()) {
                                    var jsonObject=jsonArray.optJSONObject(i)
                                    var completedbokingModel= CompletedbokingModel()
                                    completedbokingModel.id=jsonObject.optString("id")
                                    completedbokingModel.type=jsonObject.optString("type")
                                    completedbokingModel.timing=jsonObject.optString("timing")
                                    completedbokingModel.session_type=jsonObject.optString("session_type")
                                    completedbokingModel.duration=jsonObject.optString("duration")
                                    completedbokingModel.trainer=jsonObject.optString("trainer")
                                    completedbokingModel.location=jsonObject.optString("location")
                                    completedbokingModel.is_reschedule=jsonObject.optString("is_reschedule")
                                    completedbokingModel.msg=jsonObject.optString("msg")
                                    completedbokingModel.is_Trainer=jsonObject.optString("isTrainer")
                                    completedbokingModel.workout_focus=jsonObject.optJSONArray("workout_focus")
                                    completedBookkingModelList.add(completedbokingModel)
                                }
                                var completedAdapter = CompletedBookingAdapter(activity, completedBookkingModelList)
                                recycler.adapter = completedAdapter
                            }
                            recycler.visibility=View.VISIBLE
                            linearNoSession.visibility=View.GONE

                        }else{
                            linearNoSession.visibility=View.VISIBLE
                            recycler.visibility=View.GONE

                        }


                    }else{


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

    private fun createFilterDialog() {
        val bottomSheet = layoutInflater.inflate(R.layout.fliter_layout, null)
        filterBottomSheetDialog.setContentView(bottomSheet)
        standard_bottom_sheet = bottomSheet.findViewById(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        var trainerSession =bottomSheet.findViewById<CheckBox>(R.id.trainerSession)
        var gymSession =bottomSheet.findViewById<CheckBox>(R.id.gymSession)
        var home =bottomSheet.findViewById<CheckBox>(R.id.home)
        var myPt =bottomSheet.findViewById<CheckBox>(R.id.myPt)
        var tvApply =bottomSheet.findViewById<TextView>(R.id.tvApply)
        var tvclear =bottomSheet.findViewById<TextView>(R.id.tvclear)
        tvclear.setOnClickListener{
            session_Type=""
            locationType=""
            trainerSession.isChecked=false
            gymSession.isChecked=false
            home.isChecked=false
            myPt.isChecked=false
        }
        tvApply.setOnClickListener{
           getData()
            filterBottomSheetDialog.dismiss()
        }
        trainerSession.setOnClickListener{
            session_Type="home"
            trainerSession.isChecked=true
            gymSession.isChecked=false
        }
        gymSession.setOnClickListener{
            session_Type="gym"
            trainerSession.isChecked=false
            gymSession.isChecked=true
        }
        home.setOnClickListener{
            locationType="home"
            home.isChecked=true
            myPt.isChecked=false
        }
        myPt.setOnClickListener{
            locationType="gym"
            home.isChecked=false
            myPt.isChecked=true
        }



        val window = filterBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun createDatePicker() {
        val bottomSheet = layoutInflater.inflate(R.layout.select_month_bottomsheet_layout, null)
        var datePicker1=bottomSheet.findViewById<DatePicker>(R.id.datePicker1)
        var tvOk=bottomSheet.findViewById<TextView>(R.id.tvOk)
        var tvclear=bottomSheet.findViewById<TextView>(R.id.tvclear)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        val monthPicker: NumberPicker = bottomSheet.findViewById(R.id.monthPicker)
        val yearPicker: NumberPicker = bottomSheet.findViewById(R.id.yearPicker)


        // Configure the Month Picker
        val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        monthPicker.minValue = 0
        monthPicker.maxValue = months.size - 1
        monthPicker.displayedValues = months
        monthPicker.value = Calendar.getInstance().get(Calendar.MONTH)
        monthPicker.wrapSelectorWheel = true
        // Configure the Year Picker
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 50
        yearPicker.maxValue = currentYear + 50
        yearPicker.value = currentYear
        yearPicker.wrapSelectorWheel = false

        tvOk.setOnClickListener{
            selectedYear =""+yearPicker.value
            Log.e("month",""+selectedMonth+1)
            if ( monthPicker.value+1<10){
                selectedMonth= "0"+ (monthPicker.value+1)
            }else{
                selectedMonth=""+(monthPicker.value+1)
            }
            Log.e("year",""+selectedYear)
           // onDateSelected(selectedMonth, selectedYear)
            selectedfulldate=selectedYear+"-"+selectedMonth
            getData()
            monthBottomSheetDialog.dismiss()
        }
        tvclear.setOnClickListener{
            selectedfulldate=""
            getData()
            monthBottomSheetDialog.dismiss()
            createDatePicker()
        }
        monthBottomSheetDialog.setContentView(bottomSheet)
        val window = monthBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    override fun onResume() {
        super.onResume()
        getData()

    }

}