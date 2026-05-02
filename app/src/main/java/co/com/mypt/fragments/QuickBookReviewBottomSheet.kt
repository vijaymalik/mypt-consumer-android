package co.com.mypt.fragments

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.BookAssessmentSuccessActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.adapter.AddressListAdapter
import co.com.mypt.databinding.QuickBookReviewBottomsheetBinding
import co.com.mypt.model.AddressModel
import co.com.mypt.model.ReviewAssessmentResponse
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import org.json.JSONObject

class QuickBookReviewBottomSheet : BottomSheetDialogFragment() {

    private lateinit var bindingView: QuickBookReviewBottomsheetBinding
    private var reviewAssessmentResponse: ReviewAssessmentResponse? = null
    private var addressList = ArrayList<AddressModel>()
    private var updatedAddress = ""

    private var slotId =  0
    private var trainerId =  0
    private var studioId =  0
    private var addressId = ""
    private var workType =  ""

    companion object {
        const val KEY_SLOT_ID = "slot_id"
        const val KEY_TRAINER_ID = "trainer_id"
        const val KEY_WORK_TYPE = "work_type"
        const val KEY_ADDRESS_ID = "address_id"
        const val KEY_STUDIO_ID = "studio_id"
        const val KEY_REVIEW_ASSESSMENT_RESPONSE = "review_assessment_response"

        fun newInstance(
            slotId: Int,
            trainerId: Int,
            workType: String,
            addressId: String,
            studioId: Int?=null
        ): QuickBookReviewBottomSheet {
            val sheet = QuickBookReviewBottomSheet()
            val bundle = Bundle()
            bundle.putInt(KEY_SLOT_ID, slotId)
            bundle.putInt(KEY_TRAINER_ID, trainerId)
            bundle.putString(KEY_WORK_TYPE, workType)
            bundle.putString(KEY_ADDRESS_ID, addressId)
            if (studioId != null) {
                bundle.putInt(KEY_STUDIO_ID, studioId)
            }
            sheet.arguments = bundle
            return sheet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView = QuickBookReviewBottomsheetBinding.inflate(inflater, container, false)

         slotId = arguments?.getInt(KEY_SLOT_ID, 0) ?: 0
         trainerId = arguments?.getInt(KEY_TRAINER_ID, 0) ?: 0
         studioId = arguments?.getInt(KEY_STUDIO_ID, 0) ?: 0
         addressId = arguments?.getString(KEY_ADDRESS_ID)?:""
         workType = arguments?.getString(KEY_WORK_TYPE) ?: ""
        getReviewAssessmentData(
            workType = workType,
            trainerId = trainerId,
            slotId = slotId,
            addressId = addressId,
            studioId=studioId
        )
        bindingView.btnConfirm.setOnClickListener {
            bookAssessment(workType, slotId, addressId)
        }

        bindingView.rlAddress.setOnClickListener {
            showAddresListDialog()
        }

        getAddressData()
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bindingView.backBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            // This makes the internal container transparent
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }


    fun continueButtonClick(isEnable: Boolean) {
        bindingView.btnConfirm.isEnabled = isEnable
        if (isEnable) {
            bindingView.btnConfirm.setTextColor(resources.getColor(R.color.buttontextcolor, null))
            bindingView.btnConfirm.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.primary_btn_gradient)
            bindingView.btnConfirm.iconTint = null
        } else {
            bindingView.btnConfirm.setTextColor(resources.getColor(R.color.white, null))
            bindingView.btnConfirm.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_btn)
            bindingView.btnConfirm.iconTint = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
    }

    private fun getReviewAssessmentData(
        workType: String,
        trainerId: Int,
        slotId: Int,
        addressId: String? = null,
        studioId: Int? = null
    ) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(), "")
        if (!(activity as MainActivity).isFinishing && !(activity as MainActivity).isDestroyed) {
            progressDialog.show()
        }
        val studioParam = if (workType.equals("home", true)) "" else ""+studioId

        val api = ApiURL.reviewAssessment +
                "trainer_id=$trainerId" +
                "&slot_id=$slotId" +
                "&type=$workType" +
                "&address_id=${updatedAddress.ifEmpty { addressId }}" +
                "&studio_id=$studioParam" +
                "&booking_type=pt"

        GetMethod(api, requireContext()).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                     reviewAssessmentResponse =
                        Gson().fromJson(data, ReviewAssessmentResponse::class.java)
                    reviewAssessmentResponse?.let {
                        setData(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
            }
        })
    }

    private fun setData(reviewAssessmentResponse: ReviewAssessmentResponse) {
        if(reviewAssessmentResponse.status) {
            bindingView.workType.text =
                reviewAssessmentResponse.data.type.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            bindingView.address.text = reviewAssessmentResponse.data.location_name
            bindingView.tvTrainerName.text = reviewAssessmentResponse.data.trainer_name
            bindingView.tvDate.text = reviewAssessmentResponse.data.date
            bindingView.tvSlot.text = reviewAssessmentResponse.data.timing
            val remaining = reviewAssessmentResponse.data.remaining_session
            val afterBooking = (remaining - 1).coerceAtLeast(0)

            bindingView.tvPTSessionRemain.text =
                "$remaining sessions remaining → $afterBooking after this booking"
        }else{
            continueButtonClick(false)
        }
    }

    private fun bookAssessment(
        workType: String,
        slotId: Int,
        addressId: String? = null,
        studioId: String? = null
    ) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(), "")
        progressDialog.show()
        val param: MutableMap<String, String> = HashMap()
        param["slot_id"] = "" + slotId
        param["type"] = workType
        if (addressId != null)
            param["address_id"] = addressId
        if (studioId != null)
            param["studio_id"] = studioId
        param["booking_type"] = "pt"

        PostMethod(ApiURL.bookAssessment, param, requireContext()).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    val intent = Intent(requireContext(), BookAssessmentSuccessActivity::class.java)
                    reviewAssessmentResponse?.let {
                        intent.putExtra(KEY_REVIEW_ASSESSMENT_RESPONSE,it)
                    }
                    requireContext().startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dismiss()

            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
            }
        })
    }

    fun showAddresListDialog() {
        val dialog = BottomSheetDialog(requireContext()) // Fragment -> requireContext()
        dialog.setContentView(R.layout.adress_list_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val recyclerAddress = dialog.findViewById<RecyclerView>(R.id.recyclerAddress)
        recyclerAddress?.adapter = AddressListAdapter(addressList, requireContext()) {
            dialog.dismiss()
            updatedAddress = it
            getReviewAssessmentData(
                workType = workType,
                trainerId = trainerId,
                slotId = slotId,
                addressId = addressId,
                studioId=studioId
            )
        }
        val closeBtn = dialog.findViewById<ImageView>(R.id.btnClose)
        closeBtn?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getAddressData() {

        showProgress()


        GetMethod(ApiURL.getaddress, requireContext()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                hideProgress()

                addressList.clear()
                Log.e("getAddressResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        val jsonArray = jsonObj.optJSONArray("data")

                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject1 = jsonArray.optJSONObject(i)
                                val addressModel = AddressModel()

                                addressModel.building_name = jsonObject1.optString("building_name")
                                addressModel.street = jsonObject1.optString("street")
                                addressModel.landmark = jsonObject1.optString("landmark")
                                addressModel.type = jsonObject1.optString("type")
                                addressModel.city_id = jsonObject1.optString("city_id")
                                addressModel.country_id = jsonObject1.optString("country_id")
                                addressModel.mobile_no = jsonObject1.optString("mobile_no")
                                addressModel.country_name = jsonObject1.optString("country_name")
                                addressModel.city_name = jsonObject1.optString("city_name")
                                addressModel.lat = jsonObject1.optString("lat")
                                addressModel.long = jsonObject1.optString("long")
                                addressModel.id = jsonObject1.optString("id")

                                addressList.add(addressModel)

                            }
                            //                            linearNoAddress.visibility=View.GONE
//                            nested.visibility=View.VISIBLE

                        } else {
//                            linearNoAddress.visibility=View.VISIBLE
//                            nested.visibility=View.GONE
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                hideProgress()
                error!!.printStackTrace()
            }

        })
    }

    private var progressDialog: Dialog? = null

    private fun showProgress() {
        if (isAdded && context != null) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.progressDialog(requireContext(), "")
            }

            if (progressDialog?.isShowing == false) {
                progressDialog?.show()
            }
        }
    }

    private fun hideProgress() {
        if (isAdded) {
            progressDialog?.dismiss()
        }
    }
}