package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.UpComingClasses.ClassDescriptionActivity
import co.com.mypt.model.FullNearUpcomingCLassModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class FullNearUpcomingClassAdapter(
    var applicationContext: Context?,
    var fullupcomingClassesModelList: ArrayList<FullNearUpcomingCLassModel>,
    var latitude: Double?,
    var longitude: Double?
) : RecyclerView.Adapter<FullNearUpcomingClassAdapter.FullNearHolder>() {
    class FullNearHolder(view: View) :RecyclerView.ViewHolder(view){
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvgymName=view.findViewById<TextView>(R.id.tvgymName)
        var tvTime=view.findViewById<TextView>(R.id.tvTime)
        var tvTrainerName=view.findViewById<TextView>(R.id.tvTrainerName)
        var tvPrice=view.findViewById<TextView>(R.id.tvPrice)
        var tvType=view.findViewById<TextView>(R.id.tvType)
        var imTrainer=view.findViewById<ImageView>(R.id.imTrainer)
        var imclass=view.findViewById<ImageView>(R.id.imclass)
        var card=view.findViewById<CardView>(R.id.card)
        var cardStatus=view.findViewById<CardView>(R.id.cardStatus)
        var tvStatus=view.findViewById<TextView>(R.id.tvStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullNearHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.full_upcoming_class_near_you_list, parent, false)
        return FullNearHolder(view)
    }

    override fun getItemCount(): Int {
       return fullupcomingClassesModelList.size
    }

    override fun onBindViewHolder(holder: FullNearHolder, position: Int) {
        var nearUpcomingCLassModel=fullupcomingClassesModelList[position]
        holder.tvname.setText(nearUpcomingCLassModel.cla_ss)
        holder.tvType.setText(nearUpcomingCLassModel.type)
        holder.tvTime.setText(nearUpcomingCLassModel.time)
        holder.tvTrainerName.setText(nearUpcomingCLassModel.trained_by)
        holder.tvPrice.setText("AED"+nearUpcomingCLassModel.price)
        holder.tvgymName.setText(nearUpcomingCLassModel.studio_name)
        Glide.with(applicationContext!!).load(nearUpcomingCLassModel.trainer_image).fitCenter().error(R.drawable.dummy_trainer).
        placeholder(R.drawable.dumbbell).into(holder.imTrainer)
        Glide.with(applicationContext!!).load(nearUpcomingCLassModel.image).fitCenter().error(R.drawable.dummy_trainer).
        placeholder(R.drawable.dumbbell).into(holder.imclass)
        if (nearUpcomingCLassModel.status.equals("status").equals("Almost Full")){
            holder.cardStatus.setCardBackgroundColor(applicationContext!!.getColor(R.color.AlmostfullColor))
        }
        else if (nearUpcomingCLassModel.status.equals("status").equals("Fast Filling")){
            holder.cardStatus.setCardBackgroundColor(applicationContext!!.getColor(R.color.progress_clr))

        }
        else if (nearUpcomingCLassModel.status.equals("status").equals("Available")){
            holder.cardStatus.setCardBackgroundColor(applicationContext!!.getColor(R.color.available))
        }
        else{
            holder.cardStatus.setCardBackgroundColor(applicationContext!!.getColor(R.color.progress_track_color_1))
        }
        holder.card.setTag(position)
        holder.card.setOnClickListener{
            var intent=Intent(applicationContext,ClassDescriptionActivity::class.java)
            intent.putExtra("schedule_id",nearUpcomingCLassModel.schedule_id)
            intent.putExtra("latitude",latitude)
            intent.putExtra("longitude",longitude)
            Log.e("latitude",""+latitude)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            applicationContext?.startActivity(intent)
        }
    }

}
