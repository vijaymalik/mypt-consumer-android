package co.com.mypt.activities

import co.com.mypt.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetAnimation   {
    companion object {
        fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
            val window = bottomSheetDialog.window
            window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        }
    }
}