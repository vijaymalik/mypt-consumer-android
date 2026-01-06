package co.com.mypt.WorkoutLibrary

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.CompleteChallengeAdapter
import co.com.mypt.model.CompleteChallengeModel
import co.com.mypt.utils.CircularFillView

class ActiveChallengeActivity : AppCompatActivity() {

    lateinit var back : ImageView
    lateinit var headerLayout : LinearLayout
    lateinit var completedRecycle : RecyclerView

    lateinit var completedPercentage : TextView

    lateinit var circularBlueView : CircularFillView
    lateinit var circularOrangeView : CircularFillView
    var completeChallengelist = ArrayList<CompleteChallengeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.active_challange_activity)

        completedPercentage = findViewById(R.id.completedPercentage)
        back = findViewById(R.id.back)
        headerLayout = findViewById(R.id.headerLayout)
        circularBlueView = findViewById(R.id.circularBlueView)
        circularOrangeView = findViewById(R.id.circularOrangeView)
        completedRecycle = findViewById(R.id.completedRecycle)


        circularBlueView.progressPaint.color = resources.getColor(R.color.progressBlue,null)
        circularBlueView.progressPaint.strokeWidth = 30f
        circularBlueView.bgPaint.strokeWidth = 30f
        circularBlueView.cornerRadius = 140f
        val animator = ObjectAnimator.ofFloat(circularBlueView, "progress", 0f, .80f)
        animator.duration = 5000 // 5 seconds animation
        animator.start()

        circularOrangeView.progressPaint.color = resources.getColor(R.color.orangecolor,null)
        circularOrangeView.progressPaint.strokeWidth = 30f
        circularOrangeView.bgPaint.strokeWidth = 30f
        circularOrangeView.cornerRadius = 90f

        val animator1 = ObjectAnimator.ofFloat(circularOrangeView, "progress", 0f, .80f)
        animator1.duration = 5000 // 5 seconds animation
        animator1.start()


        var completeChallengeAdapter =
            CompleteChallengeAdapter(completeChallengelist, this@ActiveChallengeActivity)
        completedRecycle.adapter=completeChallengeAdapter

        textShader(completedPercentage)

        back.setOnClickListener {
            finish()
        }
        headerLayout.setOnClickListener {
            finish()
        }
    }

    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF")
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }

}