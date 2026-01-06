package co.com.mypt.RenewSubscription

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.PlanRenewal.UpgradeTopUpReviewPackageActivity
import co.com.mypt.R
import co.com.mypt.adapter.PrefferedTrainerAdapter
import co.com.mypt.model.PreferedTrainerModel

class PrefferedTrainerListActivity : AppCompatActivity() {
    lateinit var trainerRecycler: RecyclerView
    var preferedTrainerArraylist = ArrayList<PreferedTrainerModel>()
    lateinit var cardSelectTrainer: CardView
    lateinit var cardContinue: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferredtrainer_list)
        trainerRecycler=findViewById(R.id.trainerRecycler)
        cardSelectTrainer=findViewById(R.id.cardSelectTrainer)
        cardContinue=findViewById(R.id.cardContinue)
        var preferedTrainerAdapter=
            PrefferedTrainerAdapter(applicationContext, preferedTrainerArraylist)
        trainerRecycler.adapter=preferedTrainerAdapter
        cardContinue.setOnClickListener {
            var intent=
                Intent(this@PrefferedTrainerListActivity, UpgradeTopUpReviewPackageActivity::class.java)
            startActivity(intent)
        }
    }
}