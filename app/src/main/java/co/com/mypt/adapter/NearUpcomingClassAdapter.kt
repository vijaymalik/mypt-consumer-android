package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.UpComingClasses.ClassDescriptionActivity

import co.com.mypt.model.NearUpcomingCLassModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class NearUpcomingClassAdapter(
    var applicationContext: Context?,
    var upcomingClassesModelList: ArrayList<NearUpcomingCLassModel>,
    var latitude: Double,
    var longitude: Double
) : RecyclerView.Adapter<NearUpcomingClassAdapter.NearUpcomingClassHolder>() {
    class NearUpcomingClassHolder(view: View):RecyclerView.ViewHolder(view) {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NearUpcomingClassAdapter.NearUpcomingClassHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcomingclasses_nearyou, parent, false)
        return NearUpcomingClassHolder(view)
    }

    override fun onBindViewHolder(
        holder: NearUpcomingClassAdapter.NearUpcomingClassHolder,
        position: Int
    ) {
        var nearUpcomingCLassModel=upcomingClassesModelList[position]
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
            var j=it.tag
            var nearUpcomingCLassModel=upcomingClassesModelList[j as Int]
            var intent= Intent(applicationContext, ClassDescriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("schedule_id",nearUpcomingCLassModel.schedule_id)
            intent.putExtra("latitude",latitude)
            intent.putExtra("longitude",longitude)
            applicationContext?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return upcomingClassesModelList.size
    }

}
