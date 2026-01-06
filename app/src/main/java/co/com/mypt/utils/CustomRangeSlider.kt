package co.com.mypt.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import co.com.mypt.R
import com.google.android.material.slider.RangeSlider

class CustomRangeSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RangeSlider(context, attrs, defStyleAttr) {

    private val textPaint = Paint().apply {
        color = context.getColor(R.color.txt_clr_2) // Set your desired text color
        textSize = 40f // Set your desired text size
        textAlign = Paint.Align.CENTER
    }

   /* override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get the number of thumbs based on values size
        val thumbCount = values.size

        for (thumbIndex in 0 until thumbCount) {
            val thumbX = getThumbX(thumbIndex)
            val thumbY = height / 2 // Center Y position of the slider

            // Get current value for this thumb
            val value = values[thumbIndex].toInt()

            // Draw value below thumb with a vertical offset (adjust as needed)
            canvas.drawText(value.toString(), (thumbX+30), (thumbY + 60).toFloat(), textPaint) // 60 is the offset below the thumb
        }
    }*/



   /* private fun getThumbX(thumbIndex: Int): Float {
        return if (thumbIndex < values.size) {
            (values[thumbIndex] - valueFrom) / (valueTo - valueFrom) * width
        } else {
            0f
        }
    }*/
   override fun onDraw(canvas: Canvas) {
       super.onDraw(canvas)

       for (thumbIndex in values.indices) {
           val thumbX = getThumbX(thumbIndex) // Calculate exact thumb position
           val thumbY = height.toFloat() - 40f // Adjust Y position for text

           // Draw text centered below the thumb
           canvas.drawText(values[thumbIndex].toInt().toString(), thumbX, thumbY, textPaint)
       }
   }
    private fun getThumbX(thumbIndex: Int): Float {
        if (thumbIndex >= values.size) return 0f

        val trackWidth = width - paddingLeft - paddingRight // Calculate actual track width
        val valueRatio = (values[thumbIndex] - valueFrom) / (valueTo - valueFrom)
            return       + (valueRatio * trackWidth) // Corrected thumb position
    }
}
