package co.com.mypt.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ReviewActivityAdapter
import co.com.mypt.model.ActivityModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReviewPackageBookTrainerCalendarActivity : AppCompatActivity() {
    lateinit var linearpay: LinearLayout
    lateinit var linearrepeat: LinearLayout
    lateinit var tvPayment: TextView
    lateinit var recycler: RecyclerView
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    lateinit var repeatBottomSheetDialog:BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_package_book_trainer_calendar)
        repeatBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        linearpay=findViewById(R.id.linearpay)
        tvPayment=findViewById(R.id.tvPayment)
        linearrepeat=findViewById(R.id.linearRepeat)
        recycler=findViewById(R.id.recycler)
        createRepeatSessionAlert()
        for (i in 0..4) {
            var activityModel= ActivityModel()
            activityModel.name="Cardio"
            activitiesModelList.add(activityModel)
        }
        var activityAdapter = ReviewActivityAdapter(this, activitiesModelList)
        recycler.adapter = activityAdapter
        linearpay.setOnClickListener{

            val intent = Intent(this, BookingConfirmActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createRepeatSessionAlert() {

        val bottomSheet = layoutInflater.inflate(R.layout.repeat_session_layout, null)
        var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

      //  val imclose=bottomSheet.findViewById<ImageView>(R.id.close)



        repeatBottomSheetDialog.setContentView(bottomSheet)
        animateBottomSheet(repeatBottomSheetDialog)

    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
}