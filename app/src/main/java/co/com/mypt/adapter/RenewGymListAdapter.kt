package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.PlanRenewal.Renew.RenewHomeGymSessionActivity
import co.com.mypt.R
import co.com.mypt.activities.GymDetailActivity
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.bumptech.glide.Glide

class RenewGymListAdapter(
    var context: Context,
    var trainerList: ArrayList<TrainersModel>,
    var type: String?,
    var gym: String?,
    var latitude: Double?,
    var longitude: Double?

) : RecyclerView.Adapter<RenewGymListAdapter.GymlistHolder>() {
    lateinit var sharedPreferences: SharedPreferences

    class GymlistHolder(view: View):RecyclerView.ViewHolder(view) {
        val exerciseRecyclerView : RecyclerView = view.findViewById(R.id.exerciseRecyclerView)
        val bookSlot : TextView = view.findViewById(R.id.bookSlot)
        val tvConfirmGym : CardView = view.findViewById(R.id.tvConfirmGym)
        val tvDetail : TextView = view.findViewById(R.id.tvDetail)
        val trainerImage : ImageView = view.findViewById(R.id.trainerImage)
        val userName : TextView = view.findViewById(R.id.userName)
        val avgRating : TextView = view.findViewById(R.id.avgRating)
        val totalRatings : TextView = view.findViewById(R.id.totalRatings)
        val distance : TextView = view.findViewById(R.id.distance)
        val place : TextView = view.findViewById(R.id.place)
        val time : TextView = view.findViewById(R.id.time)
        val tvtag : TextView = view.findViewById(R.id.tvtag)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GymlistHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gymlist_layout, parent, false)
        return GymlistHolder(view)
    }

    override fun onBindViewHolder(holder: GymlistHolder, position: Int) {
        val exerciseList = ArrayList<ExerciseModel>()
        var trainersModel=trainerList[position]
        for(i in 0 until trainersModel.activity.length()){
            var jsonObject=trainersModel.activity.optJSONObject(i)
            var exerciseModel=ExerciseModel()
            exerciseModel.id=jsonObject.optString("id")
            exerciseModel.name=jsonObject.optString("name")
            exerciseList.add(exerciseModel)
        }
        holder.exerciseRecyclerView.adapter = ExerciseAdapter(context,exerciseList)
        holder.bookSlot.tag = position
        holder.tvDetail.tag = position
        holder.tvConfirmGym.tag = position
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context)

        holder.tvConfirmGym.setOnClickListener {
            if (sharedPreferences.getString("token", "").equals("")){
                val intent= Intent(context, PhoneNumberScreenActivity::class.java)
                context.startActivity(intent)
            }else{
                val pos = it.tag as Int
                val trainersModel=trainerList.get(pos)
                val intent = Intent(context, RenewHomeGymSessionActivity::class.java)
                intent.putExtra("studio_id",trainersModel.id)
                context.startActivity(intent)
            }
        }
        holder.tvDetail.setOnClickListener {
            if (sharedPreferences.getString("token", "").equals("")){
                val intent= Intent(context, PhoneNumberScreenActivity::class.java)
                context?.startActivity(intent)
            }else{
                val pos = it.tag as Int
                var  trainersModel=trainerList.get(pos)
                val intent = Intent(context, GymDetailActivity::class.java)
                intent.putExtra("type",type)
                Log.e("flowTpe",""+type)
                intent.putExtra("lat",latitude)
                intent.putExtra("long",longitude)
                intent.putExtra("studio_id",trainersModel.id)
                context.startActivity(intent)
            }


        }
        holder.userName.text = trainersModel.name
        holder.avgRating.text = trainersModel.averageRating
        holder.totalRatings.text = trainersModel.noOfRating+""+" ratings"

        val tempDistance = trainersModel.distance.split(" ")
        val intDist = tempDistance[0].split(".")
        holder.distance.text = "${intDist[0]} ${tempDistance[1]}"
        //holder.distance.setText(trainersModel.distance)

        holder.place.text = trainersModel.location
        holder.time.text = trainersModel.timing
        holder.tvtag.text = trainersModel.tag

        Glide.with(context).load(trainersModel.profile).fitCenter().into(holder.trainerImage)
    }

    override fun getItemCount(): Int {
        return trainerList.size
    }

}
