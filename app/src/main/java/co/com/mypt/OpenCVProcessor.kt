/*
package co.com.mypt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.opencv.core.Rect


import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.IOException
object OpenCVProcessor {

    fun detectBodyParts(context: Context, imageResId: Int): List<Rect> {
        // Load the image from resources
        val bitmap = BitmapFactory.decodeResource(context.resources, imageResId)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Convert to grayscale
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

        // Apply thresholding
        Imgproc.threshold(mat, mat, 50.0, 255.0, Imgproc.THRESH_BINARY)

        // Find contours
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        val boundingBoxes = mutableListOf<Rect>()

        // Extract bounding boxes
        for (contour in contours) {
            val rect = Imgproc.boundingRect(contour)
            boundingBoxes.add(rect)
            Log.d("BodyPart", "Detected at: x1=${rect.x}, y1=${rect.y}, x2=${rect.x + rect.width}, y2=${rect.y + rect.height}")
        }

        return boundingBoxes
    }
}
*/
