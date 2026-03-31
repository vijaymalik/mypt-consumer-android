package co.com.mypt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.PlanRenewal.Renew.RenewCreatePackageActivity
import co.com.mypt.PlanRenewal.SelectedUpgragationActivity
import co.com.mypt.R
import co.com.mypt.model.GetPlansResponse
import co.com.mypt.model.PlanDetail
import co.com.mypt.model.RenewalUpgradeModel
import co.com.mypt.model.Trainer

class GetPlanDetailAdapter(
    val context: Context?,
    val planDetailList: List<PlanDetail>,
    private val onUseSessionClick: (PlanDetail) -> Unit,
    private val onBuyMoreClick: (PlanDetail) -> Unit
) : RecyclerView.Adapter<GetPlanDetailAdapter.RowHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RowHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_plans_adapter_layout, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(
        holder: RowHolder,
        position: Int
    ) {
        val planModel = planDetailList[position]

        holder.tvMembershipType.text =
            "${planModel.type.replaceFirstChar { it.uppercase() }}"
        holder.tvMembershipValidity.text = planModel.end_date

        val total = planModel.sessions ?: 0
        val remaining = planModel.remaining_sessions ?: 0
        val used = total - remaining

        holder.tvRemainingSessions.text = "$remaining sessions remaining"
        holder.tvTotalSessions.text = "Total ${total} sessions"

        val progressPercent = if (total > 0) {
            (used * 100) / total
        } else {
            0
        }
        holder.progressBar.progress = progressPercent

        holder.itemView.layoutParams.width = getItemWidth()

        holder.btnUseSession.setOnClickListener {
            onUseSessionClick(planModel)
        }

        holder.btnBuyMore.setOnClickListener {
            onBuyMoreClick(planModel)
        }
    }

    override fun getItemCount(): Int {
        return planDetailList.size
    }

    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvMembershipType: TextView = view.findViewById(R.id.tvMembershipType)
        var tvMembershipValidity: TextView = view.findViewById(R.id.tvMembershipValidity)
        var tvRemainingSessions: TextView = view.findViewById(R.id.tvRemainingSessions)
        var tvTotalSessions: TextView = view.findViewById(R.id.tvTotalSessions)
        var progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        var btnUseSession: LinearLayout = view.findViewById(R.id.btnUseSession)
        var btnBuyMore: LinearLayout = view.findViewById(R.id.btnBuyMore)
    }

    private fun getItemWidth(): Int{
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        return (screenWidth * 0.85).toInt()
    }
}