package co.com.mypt.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import co.com.mypt.R
import co.com.mypt.adapter.SearchTrainerListAdapter
import co.com.mypt.databinding.ActivitySearchTrainerBinding
import co.com.mypt.model.TrainersModel
import co.com.mypt.utils.TrainerListData

private lateinit var binding: ActivitySearchTrainerBinding
private var trainerList: List<TrainersModel> = emptyList()
private var adapter: SearchTrainerListAdapter? = null
lateinit var sharedPreferences:SharedPreferences

class SearchTrainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchTrainerBinding.inflate(layoutInflater)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = SearchTrainerListAdapter { trainersModel ->
            val newIntent = Intent(this, TrainerDetails::class.java)
            newIntent.putExtra("trainer_id",trainersModel.id)
            newIntent.putExtra("studio_id",intent.getStringExtra("studio_id"))
            newIntent.putExtra("haveSlot",trainersModel.slot)
            newIntent.putExtra("type",sharedPreferences.getString("typeWorkout", ""))
            newIntent.putExtra("long",intent.getDoubleExtra("long",0.0))
            newIntent.putExtra("lat",intent.getDoubleExtra("lat",0.0))
            newIntent.putExtra("address_id",intent.getStringExtra("address_id"))
            startActivity(newIntent)
        }
        binding.rvSearchResult.adapter = adapter

        trainerList = TrainerListData.trainerList ?: emptyList()

        adapter?.updateList(trainerList.take(3))

        binding.tvSearchResult.text = getString(R.string.top_trainers_curated_for_you)

        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if (query.isNotEmpty()) {
                    val filteredList = trainerList.filter {
                        it.name.contains(query, ignoreCase = true)
                    }

                    adapter?.updateList(filteredList)

                    binding.tvSearchResult.text = getString(R.string.search_results)

                    if (filteredList.isEmpty()) {
                        binding.rvDataLay.visibility = View.GONE
                        binding.ivNoData.visibility = View.VISIBLE
                    } else {
                        binding.rvDataLay.visibility = View.VISIBLE
                        binding.ivNoData.visibility = View.GONE
                    }

                } else {
                    adapter?.updateList(trainerList.take(3))

                    binding.tvSearchResult.text = getString(R.string.top_trainers_curated_for_you)

                    binding.rvDataLay.visibility = View.VISIBLE
                    binding.ivNoData.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TrainerListData.clear()
    }
}
