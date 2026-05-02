package co.com.mypt.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import co.com.mypt.R
import co.com.mypt.adapter.TrainerSlotsAdapter
import co.com.mypt.databinding.BottomsheetTrainerSlotsBinding
import co.com.mypt.fragments.HomeTrainerBottomSheet.Companion.KEY_SELECTED_STUDIO_ID
import co.com.mypt.fragments.HomeTrainerBottomSheet.Companion.KEY_SELECTED_TRAINER_ID
import co.com.mypt.model.Slot
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrainerSlotsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var bindingView: BottomsheetTrainerSlotsBinding
    private var slotList: java.util.ArrayList<Slot>? = ArrayList<Slot>()
    private var adapter: TrainerSlotsAdapter? = null
    private var date:String = ""

    private var slotId = 0

    companion object {
        const val ARG_CONTENT = "arg_content"
        const val KEY_DATE = "date"
        const val KEY_TRAINER_ID = "trainer_id"
        const val KEY_WORK_TYPE = "workType"
        const val KEY_SELECTED_SLOT_ID = "selected_slot_id"

        val TRAINER_SLOT_REQUEST_KEY = "trainer_slot_result"

        fun newInstance(workType:String,trainerId:Int,slotList: ArrayList<Slot>, date: String): TrainerSlotsBottomSheet {
            val sheet = TrainerSlotsBottomSheet()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_CONTENT, slotList)
            bundle.putString(KEY_DATE, date)
            bundle.putInt(KEY_TRAINER_ID, trainerId)
            bundle.putString(KEY_WORK_TYPE, workType)
            sheet.arguments = bundle
            return sheet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetTheme)
        slotList = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelableArrayList(
                ARG_CONTENT,
                Slot::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(ARG_CONTENT)
        }
        date = arguments?.getString(KEY_DATE)?:""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView = BottomsheetTrainerSlotsBinding.inflate(inflater, container, false)
        adapter = TrainerSlotsAdapter { slot ->
            slotId = slot?.id?:0
            continueButtonClick(slot!=null)
        }
        bindingView.slotsRecyclerView.adapter = adapter
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = sortSlots(true)
        list?.let {
            adapter?.updateData(it)
        }

        bindingView.tvDate.text = date

        bindingView.tvAM.setOnClickListener {
            val list = sortSlots(true)
            list?.let {
                adapter?.updateData(list)
            }
            bindingView.tvAM.setBackgroundResource(R.drawable.category_border_bg)
            bindingView.tvPM.background =null
            bindingView.tvAM.setTextColor(resources.getColor(R.color.neon_color, null))
            bindingView.tvPM.setTextColor(resources.getColor(R.color.white, null))

        }

        bindingView.tvPM.setOnClickListener {
            val list = sortSlots(false)
            list?.let {
                adapter?.updateData(it)
            }
            bindingView.tvPM.setBackgroundResource(R.drawable.category_border_bg)
            bindingView.tvPM.setTextColor(resources.getColor(R.color.neon_color, null))
            bindingView.tvAM.setTextColor(resources.getColor(R.color.white, null))
            bindingView.tvAM.background=null
        }


        bindingView.btnContinue.setOnClickListener {
            if(slotId!=0) {
                val bundle = Bundle().apply {
                    putInt(KEY_SELECTED_SLOT_ID, slotId)
                    arguments?.getInt(KEY_TRAINER_ID, 0)
                        ?.let { value -> putInt(KEY_TRAINER_ID, value) }
                    putString(KEY_WORK_TYPE, arguments?.getString(KEY_WORK_TYPE))
                    putInt(KEY_SELECTED_SLOT_ID, slotId)
                }
                parentFragmentManager.setFragmentResult(TRAINER_SLOT_REQUEST_KEY,bundle)
            }
            dismiss()
        }

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
        bindingView.btnContinue.isEnabled = isEnable
        if (isEnable) {
            bindingView.btnContinue.setTextColor(resources.getColor(R.color.buttontextcolor, null))
            bindingView.btnContinue.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.primary_btn_gradient)
            bindingView.btnContinue.iconTint= null
        } else {
            bindingView.btnContinue.setTextColor(resources.getColor(R.color.white, null))
            bindingView.btnContinue.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_btn)
            bindingView.btnContinue.iconTint= ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                R.color.white
            ))
        }
    }

    fun sortSlots(amFirst: Boolean): List<Slot>? {
        return slotList?.sortedWith(compareBy {
            val hour = it.startTime.substringBefore(":").toInt()
            if (amFirst) {
                if (hour < 12) 0 else 1
            } else {
                if (hour >= 12) 0 else 1
            }
        })
    }
}