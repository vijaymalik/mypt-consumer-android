package co.com.mypt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.ActiveChallengeActivity
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.CompleteChallengeModel

class CompleteChallengeAdapter(
    var completeChallengelist: ArrayList<CompleteChallengeModel>,
    var activity: ActiveChallengeActivity) : RecyclerView.Adapter<CompleteChallengeAdapter.CompleteHolder>() {
    class CompleteHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompleteChallengeAdapter.CompleteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.complete_challenge_list, parent, false)
        return CompleteHolder(view)
    }

    override fun onBindViewHolder(
        holder: CompleteChallengeAdapter.CompleteHolder,
        position: Int
    ) {

    }

    override fun getItemCount(): Int {
        return 5
    }

}
