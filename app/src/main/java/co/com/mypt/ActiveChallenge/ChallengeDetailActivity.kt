package co.com.mypt.ActiveChallenge

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.ActiveChallengeActivity
import co.com.mypt.adapter.ChallengeListAdapter
import co.com.mypt.model.ActiveModel
import co.com.mypt.model.ChallengeModel

class ChallengeDetailActivity : AppCompatActivity() {
    lateinit var linear: LinearLayout
    lateinit var tvAccept: CardView
    lateinit var strength_recycle: RecyclerView
    var activechallengelist = ArrayList<ChallengeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_challenge_detail)
        linear=findViewById(R.id.linear)
        tvAccept=findViewById(R.id.tvAccept)
        strength_recycle=findViewById(R.id.strength_recycle)
        linear.setOnClickListener { finish() }

        var activeChallengeAdapter =
            ChallengeListAdapter(activechallengelist, this@ChallengeDetailActivity)
        strength_recycle.adapter=activeChallengeAdapter
        tvAccept.setOnClickListener {
            var intent= Intent(applicationContext, ActiveChallengeActivity::class.java)
          startActivity(intent)
        }

    }
}