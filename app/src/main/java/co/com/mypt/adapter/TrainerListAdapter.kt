package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.AddressListForTrainerActivity
import co.com.mypt.activities.BookSlot
import co.com.mypt.activities.TrainerDetails
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel
import com.bumptech.glide.Glide

class TrainerListAdapter(
    val context: Context,
    val trainerList: ArrayList<TrainersModel>,
    val type: String,
    var typeWorkout: String?,
    var latitude: Double?,
    var longitude: Double?,
    var studio_id: String,
    var clickListener:(Boolean,String, String)->Unit
) : RecyclerView.Adapter<TrainerListAdapter.ViewHolder>() {
    lateinit var sharedPreferences:SharedPreferences
    private var trainerListModels: List<TrainersModel>

    class ViewHolder(item:View) : RecyclerView.ViewHolder(item) {
        val exerciseRecyclerView : RecyclerView = item.findViewById(R.id.exerciseRecyclerView)
        val bookSlot : TextView = item.findViewById(R.id.bookSlot)
//        val im_verified : ImageView = item.findViewById(R.id.im_verified)
        val avgRating : TextView = item.findViewById(R.id.avgRating)
        val totalRatings : TextView = item.findViewById(R.id.totalRatings)
        val userName : TextView = item.findViewById(R.id.userName)
        val distance : TextView = item.findViewById(R.id.distance)
        val place : TextView = item.findViewById(R.id.place)
        val viewProfile : TextView = item.findViewById(R.id.viewProfile)
        //val hurryUp : TextView = item.findViewById(R.id.hurryUp)
//        val availableSlots : TextView = item.findViewById(R.id.availableSlots)
        val trainerImage : ImageView = item.findViewById(R.id.trainerImage)
        val relative : RelativeLayout = item.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if(type == "Linear")
            LayoutInflater.from(parent.context).inflate(R.layout.trainer_list_linear_adapter_layout, parent, false)
        else
            LayoutInflater.from(parent.context).inflate(R.layout.trainer_list_grid_adapter_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trainerListModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var trainersModel=trainerListModels[position]
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context)
        val exerciseList = ArrayList<ExerciseModel>()
        /*if (type=="grid"){
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.rightMargin = 15 // Set left margin in pixels
            holder.itemView.layoutParams = layoutParams
        }*/
       /* if (trainersModel.is_verified.equals("true")){
            holder.im_verified.visibility = View.VISIBLE
        }else{
            holder.im_verified.visibility = View.GONE

        }*/
        for(i in 0 until trainersModel.activity.length()){
            var jsonObject=trainersModel.activity.optJSONObject(i)
            var exerciseModel=ExerciseModel()
            exerciseModel.id=jsonObject.optString("id")
            exerciseModel.name=jsonObject.optString("name")
            exerciseList.add(exerciseModel)
        }
        holder.exerciseRecyclerView.adapter = TrainerTagAdapter(context,exerciseList,type)

        holder.bookSlot.tag = position
        holder.relative.tag = position
        holder.relative.setOnClickListener {
            val pos = it.tag as Int
            var trainersModel=trainerListModels[pos]
            val intent = Intent(context, TrainerDetails::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("trainer_id",trainersModel.id)
            intent.putExtra("studio_id",studio_id)
            intent.putExtra("haveSlot",trainersModel.slot)
            intent.putExtra("type",typeWorkout)
            intent.putExtra("long",longitude)
            Log.e("longitiude",""+longitude)
            intent.putExtra("lat",latitude)
            Log.e("lati",""+latitude)
            Log.e("studio_id",""+studio_id)
            context.startActivity(intent)
        }
        holder.viewProfile.setOnClickListener {
            var trainersModel=trainerListModels[position]
            val intent = Intent(context, TrainerDetails::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("trainer_id",trainersModel.id)
            intent.putExtra("studio_id",studio_id)
            intent.putExtra("haveSlot",trainersModel.slot)
            intent.putExtra("type",typeWorkout)
            intent.putExtra("long",longitude)
            Log.e("longitiude",""+longitude)
            intent.putExtra("lat",latitude)
            Log.e("lati",""+latitude)
            Log.e("studio_id",""+studio_id)
            context.startActivity(intent)
        }
        holder.userName.text = trainersModel.name

        if(trainersModel.averageRating == "null")
            holder.avgRating.text = "0"
        else
            holder.avgRating.text = trainersModel.averageRating
        holder.totalRatings.text = trainersModel.noOfRating+" ratings"
        holder.distance.text = trainersModel.distance
        holder.place.text = trainersModel.location
        if(trainersModel.slot == "no"){
//            holder.hurryUp.text = "No slots available"
            holder.bookSlot.setOnClickListener {}
            holder.bookSlot.background
//            holder.availableSlots.visibility = View.GONE
            holder.bookSlot.setBackgroundColor(context.resources.getColor(R.color.buttongreycolor,null))
            holder.bookSlot.setTextColor(context.resources.getColor(R.color.white,null))
        }
        else {
//            holder.availableSlots.text = "Only ${trainersModel.slot} slots available"
            holder.bookSlot.setOnClickListener {
                val pos = it.tag as Int
                var trainersModel=trainerListModels[pos]

//                if (trainersModel.is_group==false){
                    val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                    clickListener(/*trainersModel.is_group*/true?:false,query,trainersModel.id)
//                    return@setOnClickListener
//                }
                /*if (sharedPreferences.getString("typeWorkout","").equals("home")){
                    var intent = Intent(context, AddressListForTrainerActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("trainer_id",trainersModel.id)
                    intent.putExtra("studio_id",studio_id)
                    intent.putExtra("type",typeWorkout)
                    intent.putExtra("long",longitude)
                    Log.e("longitiude",""+longitude)
                    intent.putExtra("lat",latitude)
                    Log.e("lati",""+latitude)
                    context.startActivity(intent)
                }else{
                    var intent = Intent(context, BookSlot::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("trainer_id",trainersModel.id)
                    intent.putExtra("studio_id",studio_id)
                    intent.putExtra("type",typeWorkout)
                    intent.putExtra("long",longitude)
                    Log.e("longitiude",""+longitude)
                    intent.putExtra("lat",latitude)
                    Log.e("lati",""+latitude)
                    context.startActivity(intent)

                }*/

            }
            holder.bookSlot.setBackgroundColor(context.resources.getColor(R.color.headingcolor,null))
//            holder.hurryUp.text = context.resources.getString(R.string.hurry_up)
//            holder.availableSlots.visibility = View.VISIBLE
            holder.bookSlot.setTextColor(context.resources.getColor(R.color.buttontextcolor,null))
        }
        Glide.with(context).load(trainersModel.profile).fitCenter().into(holder.trainerImage)

    }

    fun filterList(filteredList: MutableList<TrainersModel>) {
        trainerListModels = filteredList
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    init {
        this.trainerListModels = trainerList
    }
}
