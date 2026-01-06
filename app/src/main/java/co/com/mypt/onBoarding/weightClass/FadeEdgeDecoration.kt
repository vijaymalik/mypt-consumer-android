package co.com.mypt.onBoarding.weightClass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.recyclerview.widget.RecyclerView

class FadeEdgeDecoration(private val context: Context) : RecyclerView.ItemDecoration() {
    private val fadeWidth = 280 // Width of the fade effect
    private val paint = Paint()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // Draw fade effect on the left edge
        drawFade(c, parent, isLeft = true)

        // Draw fade effect on the right edge
        drawFade(c, parent, isLeft = false)
    }

    private fun drawFade(canvas: Canvas, parent: RecyclerView, isLeft: Boolean) {
        val height = parent.height

        // Use absolute screen edge for left gradient
        paint.shader = LinearGradient(
            if (isLeft) 0f else parent.width - fadeWidth.toFloat(), // Start X
            0f, // Start Y
            if (isLeft) fadeWidth.toFloat() else parent.width.toFloat(), // End X
            0f, // End Y
            if (isLeft) Color.BLACK else Color.TRANSPARENT, // Start color
            if (isLeft) Color.TRANSPARENT else Color.BLACK, // End color
            Shader.TileMode.CLAMP
        )

        // Draw rectangle for fade effect
        canvas.drawRect(
            if (isLeft) 0f else (parent.width - fadeWidth).toFloat(),
            0f,
            if (isLeft) fadeWidth.toFloat() else parent.width.toFloat(),
            height.toFloat(),
            paint
        )
    }
}