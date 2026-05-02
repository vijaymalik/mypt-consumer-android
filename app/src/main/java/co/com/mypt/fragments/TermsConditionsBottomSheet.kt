package co.com.mypt.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import co.com.mypt.Api.Constants.TERMS_BUNDLE_KEY
import co.com.mypt.Api.Constants.TERMS_REQUEST_KEY
import co.com.mypt.R
import co.com.mypt.model.TermsConditionsResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class TermsConditionsBottomSheet : BottomSheetDialogFragment() {
    private var termsConditionsResponse: TermsConditionsResponse? = null

    companion object {
        const val ARG_CONTENT = "arg_content"
        fun newInstance(termsConditionsResponse: TermsConditionsResponse): TermsConditionsBottomSheet {
            val sheet = TermsConditionsBottomSheet()
            val bundle = Bundle()
            bundle.putParcelable(ARG_CONTENT, termsConditionsResponse)
            sheet.arguments = bundle
            return sheet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetTheme)
        termsConditionsResponse = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable(ARG_CONTENT, TermsConditionsResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<TermsConditionsResponse>(ARG_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottomsheet_terms_conditions, container, false)

        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        val btnAgree = view.findViewById<MaterialButton>(R.id.btnAgree)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tabEnglish = view.findViewById<TextView>(R.id.tabEnglish)
        val tabArabic = view.findViewById<TextView>(R.id.tabArabic)
        setContent(tvContent, termsConditionsResponse?.data?.content_en ?: "")

        tabEnglish.setOnClickListener {
            selectTab(tabEnglish, tabArabic)
            setContent(tvContent, termsConditionsResponse?.data?.content_en ?: "")
        }

        tabArabic.setOnClickListener {
            selectTab(tabArabic, tabEnglish)
            setContent(tvContent, termsConditionsResponse?.data?.content_ar ?: "")
        }
        btnAgree.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                TERMS_REQUEST_KEY,
                bundleOf(TERMS_BUNDLE_KEY to true)
            )
            dismiss()
        }
        btnClose.setOnClickListener { dismiss() }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            // This makes the internal container transparent
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    private fun selectTab(
        selectedTab: TextView,
        unselectedTab: TextView
    ) {
        selectedTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        selectedTab.background = ContextCompat.getDrawable(requireContext(), R.drawable.feet_button)

        unselectedTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightgreycolor))
        unselectedTab.background = null
    }

    private fun setContent(tvContent: TextView, data: String) {
        tvContent.text = Html.fromHtml(
            data,
            Html.FROM_HTML_MODE_COMPACT
        )
        tvContent.movementMethod = LinkMovementMethod.getInstance()
    }
}