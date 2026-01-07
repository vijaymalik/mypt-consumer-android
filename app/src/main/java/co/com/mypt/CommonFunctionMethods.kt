package co.com.mypt

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CommonFunctionMethods {
    companion object{
        fun formatPhoneForTextView(rawNumber: String): String {
            val digits = rawNumber.replace("\\D".toRegex(), "")

            if (digits.length < 10) return "+91-$digits"

            return "+${digits.substring(0,3)}-${digits.substring(3,6)}-${digits.substring(6,10)}"
        }
        fun formatPhone(input: String): String {
            val digits = input.replace("\\D".toRegex(), "")

            // Expecting: country code (2) + number (10)
            if (digits.length < 12) return input

            val countryCode = digits.substring(0, 2)
            val number = digits.substring(2)

            return "+$countryCode " +
                    number.substring(0, 3) + "-" +
                    number.substring(3, 6) + "-" +
                    number.substring(6, 10)
        }
        fun enableEditText(editText: EditText, context: Context) {
            editText.isFocusableInTouchMode = true
            editText.isFocusable = true
            editText.isCursorVisible = true
            editText.setSelection(editText.text.length)
            editText.requestFocus()

            editText.post {
                val imm = ContextCompat.getSystemService(
                    context,
                    InputMethodManager::class.java
                )
                imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        fun disableEditText(editText: EditText, context: Context) {
            editText.clearFocus()
            editText.isCursorVisible = false
            editText.isFocusable = false
            editText.isFocusableInTouchMode = false

            val imm = ContextCompat.getSystemService(
                context,
                InputMethodManager::class.java
            )
            imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        }
        fun View.addKeyboardHeightListener(onHeightChanged: (Int) -> Unit) {
            val rootView = this
            rootView.viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)

                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                // Keyboard is open if height > 15% of screen
                if (keypadHeight > screenHeight * 0.15) {
                    onHeightChanged(keypadHeight)
                } else {
                    onHeightChanged(0)
                }
            }
        }
        fun View.listenKeyboardHeight(onHeightChanged: (Int) -> Unit) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                onHeightChanged(imeHeight)
                insets
            }
        }

    }


}