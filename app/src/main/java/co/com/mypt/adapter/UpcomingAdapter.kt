package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.BookingScreen.RescheduledUpComingBookingDetailActivity
import co.com.mypt.BookingScreen.UpcomingBookingDetails
import co.com.mypt.R
import co.com.mypt.model.UpcomingbokingModel

class UpcomingAdapter(var activity: FragmentActivity?, var upComingBookkingModelList: ArrayList<UpcomingbokingModel>) : RecyclerView.Adapter<UpcomingAdapter.UpcomingHolder>() {
    class UpcomingHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var tvWorkoutFocus=view.findViewById<TextView>(R.id.tvWorkoutFocus)
        var tvSessionType=view.findViewById<TextView>(R.id.tvSessionType)
        var tvDuration=view.findViewById<TextView>(R.id.tvDuration)
        var tvTrainer_name=view.findViewById<TextView>(R.id.tvTrainer_name)
        var tvLocation=view.findViewById<TextView>(R.id.tvLocation)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
        var im_dot=view.findViewById<ImageView>(R.id.im_dot)
        var tvPayment=view.findViewById<TextView>(R.id.tvPayment)
        var rescheduleLinear=view.findViewById<LinearLayout>(R.id.rescheduleLinear)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_list, parent, false)
        return UpcomingHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingHolder, position: Int) {
        var upcomingbokingModel= upComingBookkingModelList[position]
        holder.tvSessionType.text = upcomingbokingModel.session_type
        holder.tvTime.text = upcomingbokingModel.timing
        holder.tvDuration.text = upcomingbokingModel.duration
        holder.tvTrainer_name.text = upcomingbokingModel.trainer
        holder.tvLocation.text = upcomingbokingModel.location
        holder.tvPayment.text = upcomingbokingModel.location
        var j=""
        for (i in 0 until upcomingbokingModel.workout_focus!!.length()){
         /*    j= j + ","+ upcomingbokingModel.workout_focus!!.get(i)*/
            j += if (i == 0) upcomingbokingModel.workout_focus!!.get(i) else ", " + upcomingbokingModel.workout_focus!!.get(i)
        }
        holder.tvWorkoutFocus.text = j
        holder.linear.tag = position
        if (upcomingbokingModel.is_reschedule == "false"){
            holder.linear.setBackgroundResource(R.drawable.booking_rounded_green)
            holder.rescheduleLinear.visibility=View.GONE
            holder.im_dot.visibility=View.GONE
        }else{
            holder.linear.setBackgroundResource(R.drawable.booking_rounded)
            holder.rescheduleLinear.visibility=View.VISIBLE
            holder.im_dot.visibility=View.VISIBLE
            holder.tvPayment.text = upcomingbokingModel.msg
        }
        holder.linear.setOnClickListener {
            var k = it.tag as Int
            var upcomingbokingModel= upComingBookkingModelList[k]

            if (upcomingbokingModel.is_Trainer.equals("false")) {
                var intent = Intent(activity, UpcomingBookingDetails::class.java)
                intent.putExtra("bookingid",upcomingbokingModel.id)
                intent.putExtra("type",upcomingbokingModel.type)
                activity!!.startActivity(intent)
            } else {
                var intent = Intent(activity, RescheduledUpComingBookingDetailActivity::class.java)
                intent.putExtra("bookingid",upcomingbokingModel.id)
                intent.putExtra("type",upcomingbokingModel.type)
                activity!!.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return upComingBookkingModelList.size
    }

}
