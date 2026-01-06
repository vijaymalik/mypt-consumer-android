package co.com.mypt.ActiveChallenge

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActiveChallengeAdapter
import co.com.mypt.model.ActiveModel

class MyActivityChallengeActivity : AppCompatActivity() {
    lateinit var recyleActive: RecyclerView
    lateinit var linear: LinearLayout
    var activeArraylist = ArrayList<ActiveModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.my_activity_challenge)
        linear=findViewById(R.id.linear)
        recyleActive=findViewById(R.id.recyleActive)
        linear.setOnClickListener {
            finish()
        }
        for (i in 0 until 5) {
            val activeModel = ActiveModel()
            activeModel.name = "Strength Challenge"
            activeArraylist.add(activeModel)
        }
        var activeChallengeAdapter =
            ActiveChallengeAdapter(activeArraylist, this@MyActivityChallengeActivity)
        recyleActive.adapter = activeChallengeAdapter

    }
}