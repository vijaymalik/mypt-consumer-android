package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.GymWorkout.withoutTrainer.GymValidityActivity
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.NearByGymModel
import co.com.mypt.R
import co.com.mypt.activities.GymDetailActivity
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.bumptech.glide.Glide
import java.util.ArrayList

class NearByGymAdapter(
    val context: Context?,
    val nearByGymArraylist: ArrayList<NearByGymModel>,
    var latitude: Double,
   var  longitude: Double,
    s: String
) :
    RecyclerView.Adapter<NearByGymAdapter.ViewHolder>() {
    lateinit var sharedPreferences: SharedPreferences

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val exerciseRecyclerView : RecyclerView = itemView.findViewById(R.id.exerciseRecyclerView)
        val tvname : TextView = itemView.findViewById(R.id.tvname)
        val distance : TextView = itemView.findViewById(R.id.distance)
        val place : TextView = itemView.findViewById(R.id.place)
        val time : TextView = itemView.findViewById(R.id.time)
        val content : TextView = itemView.findViewById(R.id.content)
        val rating : TextView = itemView.findViewById(R.id.rating)
        val tvtag : TextView = itemView.findViewById(R.id.tvtag)
        val gymImage : ImageView = itemView.findViewById(R.id.gymImage)
        val availableVoucher : TextView = itemView.findViewById(R.id.availableVoucher)
        val relative : LinearLayout = itemView.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.near_by_gym_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  nearByGymArraylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exerciseList = ArrayList<ExerciseModel>()
        var nearByGymModel=nearByGymArraylist[position]
        holder.content.setText(nearByGymModel.description)
        for(i in 0 until nearByGymModel.activity!!.length()){
            var jsonObject= nearByGymModel.activity!!.optJSONObject(i)
            var exerciseModel=ExerciseModel()
            exerciseModel.id=jsonObject.optString("id")
            exerciseModel.name=jsonObject.optString("name")
            exerciseList.add(exerciseModel)
        }
        holder.exerciseRecyclerView.adapter = GymExerciseAdapter(context,exerciseList)
//        Log.e("exercise",""+exerciseList[0].name)
        holder.relative.tag = position

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context!!)



        holder.relative.setOnClickListener {
            val pos = it.tag as Int
            var nearByGymModel=nearByGymArraylist[pos]

            if (sharedPreferences.getString("token", "").equals("")){
                val intent= Intent(context, PhoneNumberScreenActivity::class.java)
                context?.startActivity(intent)
            }else{
                val intent = Intent(context, GymDetailActivity::class.java)
                sharedPreferences.edit().putString("typeWorkout","work").apply()

               // intent.putExtra("type","withTrainer")
                intent.putExtra("lat",latitude)
                intent.putExtra("long",longitude)
                intent.putExtra("studio_id",nearByGymModel.id)
                context!!.startActivity(intent)
            }



        }
        holder.tvname.setText(nearByGymModel.name)
        holder.rating.setText(nearByGymModel.averageRating)

        holder.distance.setText(nearByGymModel.distance)
        holder.place.setText(nearByGymModel.location)
        holder.time.setText(nearByGymModel.timing)
        holder.tvtag.setText(nearByGymModel.tag)

        Glide.with(context).load(nearByGymModel.profile).fitCenter().into(holder.gymImage)


    }

}
