package co.com.mypt.Notification

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R

class RescheduleBookScreenActivity : AppCompatActivity() {
    lateinit var home_workout: TextView
    lateinit var tvBookingDate: TextView
    lateinit var tvdistance: TextView
    lateinit var tvLocation: TextView
    lateinit var tvTrainer_name: TextView
    lateinit var rescheduleBookingText: TextView
    lateinit var cancelRequest: TextView
    lateinit var tvContact: TextView
    lateinit var tvamountPaid: TextView
    lateinit var tvTrainingLocation: TextView
    lateinit var tvTrainingDate: TextView
    lateinit var cancellationPolicyDescription: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reschedule_book_screen)
        home_workout=findViewById(R.id.home_workout)
        tvBookingDate=findViewById(R.id.tvBookingDate)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        tvdistance=findViewById(R.id.tvdistance)
        tvLocation=findViewById(R.id.tvLocation)
        rescheduleBookingText=findViewById(R.id.rescheduleBookingText)
        cancelRequest=findViewById(R.id.cancelRequest)
        tvContact=findViewById(R.id.tvContact)
        tvamountPaid=findViewById(R.id.tvamountPaid)
        tvTrainingLocation=findViewById(R.id.tvTrainingLocation)
        tvTrainingDate=findViewById(R.id.tvTrainingDate)
        cancellationPolicyDescription=findViewById(R.id.cancellationPolicyDescription)
    }
}