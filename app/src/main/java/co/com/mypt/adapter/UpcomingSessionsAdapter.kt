package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.ActiveSession.BeforeArrivingActivity
import co.com.mypt.BookingScreen.RescheduledUpComingBookingDetailActivity
import co.com.mypt.BookingScreen.UpcomingBookingDetails
import co.com.mypt.R
import co.com.mypt.model.UpcomingSessionsModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class UpcomingSessionsAdapter(
    val context: Context?,
    val upcomingSessionsArraylist: ArrayList<UpcomingSessionsModel>,
    private val onCheckInClick: (UpcomingSessionsModel) -> Unit = {}
) : RecyclerView.Adapter<UpcomingSessionsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card : CardView = itemView.findViewById(R.id.card)
        val sessionWith : TextView = itemView.findViewById(R.id.sessionWith)
        val sessionNumber : TextView = itemView.findViewById(R.id.sessionNumber)
        val time : TextView = itemView.findViewById(R.id.time)
        val place : TextView = itemView.findViewById(R.id.place)
        val im : ImageView = itemView.findViewById(R.id.im)
        val imTrack : ImageView = itemView.findViewById(R.id.imTrack)
        val relative : RelativeLayout = itemView.findViewById(R.id.relative)
        val llCheckIn : LinearLayout = itemView.findViewById(R.id.llCheckIn)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_session_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return upcomingSessionsArraylist.size
    }

    @UnstableApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var upcomingSessionsModel=upcomingSessionsArraylist.get(position)

        holder.card.setTag(position)
        holder.sessionWith.setText("Session with "+upcomingSessionsModel.trainer)
        holder.sessionNumber.setText(""+(position+1)+" th Session")
        holder.place.setText(upcomingSessionsModel.location)
//        holder.distance.setText(upcomingSessionsModel.distance)
        holder.time.setText(upcomingSessionsModel.selected_slot)
        holder.relative.setTag(position)
        holder.imTrack.setTag(position)
        Glide.with(context!!).load(upcomingSessionsModel.trainer_image).fitCenter().error(R.drawable.dummy_trainer).into(holder.im)

        holder.llCheckIn.setOnClickListener {
            onCheckInClick(upcomingSessionsModel)
        }

        var input=upcomingSessionsModel.timing
        val inputFormat = SimpleDateFormat("MMM dd, yyyy, h:mm a", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

//        val date = inputFormat.parse(input)
//        val formatted = outputFormat.format(date)  // "21/05/25"
//        holder.date.setText(formatted)


     /*   if (upcomingbokingModel.is_reschedule == "false"){
            holder.linear.setBackgroundResource(R.drawable.booking_rounded_green)
        }else{
            holder.linear.setBackgroundResource(R.drawable.booking_rounded)
            holder.rescheduleLinear.visibility=View.VISIBLE
            holder.im_dot.visibility=View.VISIBLE
            holder.tvPayment.setText(upcomingbokingModel.msg)
        }*/
       /* holder.imTrack.setOnClickListener{
            var k = it.tag as Int
            var upcomingbokingModel= upcomingSessionsArraylist.get(k)
            val intent = Intent(context, BeforeArrivingActivity::class.java)
            intent.putExtra("bookingid",upcomingbokingModel.id)
            context.startActivity(intent)
        }*/
        /*holder.relative.setOnClickListener {
            var k = it.tag as Int
            var upcomingbokingModel= upcomingSessionsArraylist.get(k)
            Log.e("bookiing_id",upcomingSessionsModel.id)
            if (upcomingbokingModel.is_Trainer.equals("false")) {
                var intent = Intent(context, UpcomingBookingDetails::class.java)
                intent.putExtra("bookingid",upcomingbokingModel.id)
                intent.putExtra("type",upcomingbokingModel.type)
                context!!.startActivity(intent)
                Log.e("booking_id",upcomingbokingModel.id)

            } else {
                var intent = Intent(context, RescheduledUpComingBookingDetailActivity::class.java)
                intent.putExtra("bookingid",upcomingbokingModel.id)
                intent.putExtra("type",upcomingbokingModel.type)

                context!!.startActivity(intent)
                Log.e("booking_id",upcomingbokingModel.id)

            }
        }*/

    }

}
