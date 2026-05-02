package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.BookingScreen.CompletedBookingDetailActivity
import co.com.mypt.R
import co.com.mypt.adapter.CancelBookingAdapter.CancelBookingHolder
import co.com.mypt.model.CompletedbokingModel

class CompletedBookingAdapter(var activity: FragmentActivity?, var completedBookkingModelList: ArrayList<CompletedbokingModel>) : RecyclerView.Adapter<CompletedBookingAdapter.CompletedViewHolder>() {
    class CompletedViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var tvWorkoutFocus=view.findViewById<TextView>(R.id.tvWorkoutFocus)
        var tvSessionType=view.findViewById<TextView>(R.id.tvSessionType)
        var tvDuration=view.findViewById<TextView>(R.id.tvDuration)
        var tvTrainer_name=view.findViewById<TextView>(R.id.tvTrainer_name)
        var tvLocation=view.findViewById<TextView>(R.id.tvLocation)
        var linear=view.findViewById<LinearLayout>(R.id.linear)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedBookingAdapter.CompletedViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.cancel_booking_list, null)
        return CompletedViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CompletedBookingAdapter.CompletedViewHolder,
        position: Int
    ) {
        var upcomingbokingModel= completedBookkingModelList[position]
        holder.tvTime.setText(upcomingbokingModel.timing)

        holder.tvSessionType.setText(upcomingbokingModel.session_type)
        holder.tvDuration.setText(upcomingbokingModel.duration)
        holder.tvTrainer_name.setText(upcomingbokingModel.trainer)
        holder.tvLocation.setText(upcomingbokingModel.location)
        var j=""
        for (i in 0 until upcomingbokingModel.workout_focus!!.length()){
            j= j + ","+ upcomingbokingModel.workout_focus!!.get(i)
        }
        holder.tvWorkoutFocus.text=upcomingbokingModel.booking_type
        holder.linear.setBackgroundResource(R.drawable.completed_booking_rouded_rectangle_)
        holder.linear.setTag(position)
        holder.linear.setOnClickListener{
            val pos = it.tag as Int
            var upcomingbokingModel= completedBookkingModelList[pos]
            var intent= Intent(activity,CompletedBookingDetailActivity::class.java)
            intent.putExtra("bookingid",upcomingbokingModel.id)
            intent.putExtra("type",upcomingbokingModel.type)
            activity!!.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return completedBookkingModelList.size
    }

}
