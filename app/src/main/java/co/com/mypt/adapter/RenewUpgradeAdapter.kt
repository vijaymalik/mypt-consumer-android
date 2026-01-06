package co.com.mypt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.PlanRenewal.Renew.RenewCreatePackageActivity
import co.com.mypt.PlanRenewal.SelectedUpgragationActivity
import co.com.mypt.R
import co.com.mypt.model.RenewalUpgradeModel

class RenewUpgradeAdapter(
    val context: Context?,
    val renewalUpgradeArraylist: ArrayList<RenewalUpgradeModel>,
    val subscriptionTypeArrayList: ArrayList<String>
) : RecyclerView.Adapter<RenewUpgradeAdapter.RowHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.renew_upgrade_layout, parent, false)
        return RowHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RowHolder,
        position: Int
    ) {
        val renewUpgradeModel = renewalUpgradeArraylist[position]

        holder.tvSubscription.text = "Subscription Ending in ${renewUpgradeModel.remainingDays} Days!"
        holder.tvRenew.text = renewUpgradeModel.msg
        holder.tvValidity.text = "${renewUpgradeModel.validity} Days"
        holder.tvSession.text = renewUpgradeModel.sessions
        holder.tvamount.text = "${renewUpgradeModel.amount} AED"
        holder.title.text = "${renewUpgradeModel.type.replaceFirstChar { it.uppercase() }} Subscription"

        if(renewUpgradeModel.isRenew)
            holder.tvR_enew.visibility = View.VISIBLE
        else
            holder.tvR_enew.visibility = View.GONE

        holder.tvTopUp.tag = position
        holder.tvTopUp.setOnClickListener{
            val pos = it.tag as Int
            val intent= Intent(context, SelectedUpgragationActivity::class.java)
            intent.putExtra("id",renewalUpgradeArraylist[pos].id)
            intent.putExtra("isUpgrade",renewalUpgradeArraylist[pos].isUpgrade)
            context!!.startActivity(intent)
        }

        holder.tvR_enew.tag = position
        holder.tvR_enew.setOnClickListener {
            val pos = it.tag as Int
            val intent=Intent(context, RenewCreatePackageActivity::class.java)
            intent.putExtra("id",renewalUpgradeArraylist[pos].id)
            intent.putExtra("typeArrayList",subscriptionTypeArrayList)
            context?.startActivity(intent)
        }

        textShader(holder.tvValidity)
        textShader(holder.tvSubscription)
        textShader(holder.tvSession)
        textShader(holder.tvamount)

        holder.tvR_enew.text = "Renew"
        holder.tvTopUp.text = "Top-up or upgrade"
    }

    override fun getItemCount(): Int {
        return  renewalUpgradeArraylist.size
    }

    class RowHolder (view: View):RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var tvRenew: TextView = view.findViewById(R.id.tvRenew)
        var tvTopUp: TextView = view.findViewById(R.id.tvTopUp)
        var tvR_enew: TextView = view.findViewById(R.id.tvR_enew)
        var tvamount: TextView = view.findViewById(R.id.tvamount)
        var tvSession: TextView = view.findViewById(R.id.tvSession)
        var tvValidity: TextView = view.findViewById(R.id.tvValidity)
        var tvSubscription: TextView = view.findViewById(R.id.tvSubscription)
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