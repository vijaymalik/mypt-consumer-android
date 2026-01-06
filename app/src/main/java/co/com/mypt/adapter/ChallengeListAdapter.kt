package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.ActiveChallenge.ChallengeDetailActivity
import co.com.mypt.R
import co.com.mypt.model.ChallengeModel

class ChallengeListAdapter(
    var activechallengelist: ArrayList<ChallengeModel>,
    var activity: ChallengeDetailActivity) : RecyclerView.Adapter<ChallengeListAdapter.ChallengeHolder>() {
    class ChallengeHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChallengeListAdapter.ChallengeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_session_workout, parent, false)
        return ChallengeHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChallengeListAdapter.ChallengeHolder,
        position: Int
    ) {
        //var activemodel=activechallengelist[position]

    }

    override fun getItemCount(): Int {
        return 4
    }

}
