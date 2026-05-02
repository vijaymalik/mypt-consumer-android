package co.com.mypt.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.com.mypt.R
import co.com.mypt.activities.CreatePackagectivity
import co.com.mypt.adapter.HomePageTrainerStudioDetailAdapter
import co.com.mypt.adapter.TrainerHomeTagAdapter
import co.com.mypt.databinding.BottomsheetHomeTrainerBinding
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainerStudiosResponse
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeTrainerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var bindingView: BottomsheetHomeTrainerBinding
    private var trainerStudiosResponse: TrainerStudiosResponse.Data? = null
    private var adapter: HomePageTrainerStudioDetailAdapter? = null

    companion object {
        const val ARG_CONTENT = "arg_content"
            const val REQUEST_KEY = "trainer_result"
            const val KEY_SELECTED_STUDIO_ID = "selected_studio_id"
            const val KEY_SELECTED_TRAINER_ID = "selected_trainer_id"
            const val KEY_IS_QUICK_BOOK_FLOW = "is_quick_book_flow"
        fun newInstance(trainerStudiosResponse: TrainerStudiosResponse.Data,isQuickBookFlow: Boolean=false): HomeTrainerBottomSheet {
            val sheet = HomeTrainerBottomSheet()
            val bundle = Bundle()
            bundle.putParcelable(ARG_CONTENT, trainerStudiosResponse)
            bundle.putBoolean(KEY_IS_QUICK_BOOK_FLOW, isQuickBookFlow)
            sheet.arguments = bundle
            return sheet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetTheme)
        trainerStudiosResponse = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable(
                TermsConditionsBottomSheet.Companion.ARG_CONTENT,
                TrainerStudiosResponse.Data::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<TrainerStudiosResponse.Data>(TermsConditionsBottomSheet.Companion.ARG_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView = BottomsheetHomeTrainerBinding.inflate(inflater, container, false)
        adapter = HomePageTrainerStudioDetailAdapter()
        bindingView.gymDetailRecyclerView.adapter = adapter
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainerStudiosResponse?.trainer?.let {
            Glide.with(requireContext()).load(it.profile).into(bindingView.ivTrainer)
            bindingView.tvTrainerName.text = it.name.orEmpty()
            bindingView.avgRating.text = it.averageRating?.toString()
            bindingView.exerciseRecyclerView.setHasFixedSize(true)
            bindingView.exerciseRecyclerView.isNestedScrollingEnabled = false
            it.tags?.let { tagList ->
                bindingView.exerciseRecyclerView.adapter =
                    TrainerHomeTagAdapter(context, mapToExerciseModel(tagList), "")

            }
        }

        trainerStudiosResponse?.studios?.let {
            adapter?.updateData(it)
        }


        bindingView.btnContinue.setOnClickListener {
            val studioId = adapter?.getSelectedItem()?.id
            val trainerId = trainerStudiosResponse?.trainer?.id
            val isQuickBookFlow = arguments?.getBoolean(KEY_IS_QUICK_BOOK_FLOW,false)?:false

            if (studioId != null && trainerId != null) {
                val bundle = Bundle().apply {
                    putInt(KEY_SELECTED_STUDIO_ID, studioId)
                    putInt(KEY_SELECTED_TRAINER_ID, trainerId)
                    putBoolean(KEY_IS_QUICK_BOOK_FLOW, isQuickBookFlow)
                }

                parentFragmentManager.setFragmentResult(
                    HomeTrainerBottomSheet.REQUEST_KEY,
                    bundle
                )

                dismiss()
            }
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

    private fun mapToExerciseModel(
        tags: List<TrainerStudiosResponse.Data.Trainer.Tag>
    ): List<ExerciseModel> {

        return tags.map {
            ExerciseModel().apply {
                id = if (it.id != null) it.id.toString() else ""
                name = it.name.orEmpty()
                icon = ""
            }
        }
    }
}