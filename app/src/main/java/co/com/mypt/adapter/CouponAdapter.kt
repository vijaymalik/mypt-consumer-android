package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.AvailablePromo
import org.w3c.dom.Text

class CouponAdapter(var context: Context, var selectTimeModelList: ArrayList<AvailablePromo?>?,var callback:(String,String)->Unit) : RecyclerView.Adapter<CouponAdapter.CouponHolder>() {
    var selectedPosition = -1
    class CouponHolder(view: View):RecyclerView.ViewHolder(view){
        var tvcouponName=view.findViewById<TextView>(R.id.tvcouponName)
        val applyCoupon: TextView = view.findViewById(R.id.applyCoupon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coupon_list, parent, false)
        return CouponHolder(view)
    }

    override fun getItemCount(): Int {
        return selectTimeModelList?.size?:0
    }

    override fun onBindViewHolder(holder: CouponHolder, position: Int) {
        val couponsModel= selectTimeModelList?.get(position)
        holder.tvcouponName.text = couponsModel?.name

//        holder.checkBox.isChecked = position == selectedPosition
        holder.applyCoupon.tag = position

        holder.applyCoupon.setOnClickListener {
            val pos = it.tag as Int

//            if (holder.checkBox.isChecked) {
                // Update the selected position
//                val previousPosition = selectedPosition
//                selectedPosition = position

                // Notify RecyclerView to update the views
//                notifyItemChanged(previousPosition)
//                notifyItemChanged(selectedPosition)

//                intent.putExtra("couponName", selectTimeModelList?.get(pos)?.name)
//                intent.putExtra("saving", selectTimeModelList?.get(pos)?.offer_details)
//            } else {
//                // Deselect the checkbox if clicked again
//                selectedPosition = -1
//                notifyItemChanged(position)
//                intent.putExtra("selectedPosition", selectedPosition)
//            }

            callback(couponsModel?.id.toString(),couponsModel?.name?:"")
        }
    }

}
