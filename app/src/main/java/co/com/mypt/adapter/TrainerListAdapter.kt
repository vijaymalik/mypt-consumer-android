package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.media3.ui.PlayerView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.AddressListForTrainerActivity
import co.com.mypt.activities.BookSlot
import co.com.mypt.activities.TrainerDetails
import co.com.mypt.databinding.TrainerListGridAdapterLayoutBinding
import co.com.mypt.databinding.TrainerListLinearAdapterLayoutBinding
import co.com.mypt.model.ExerciseModel
import co.com.mypt.model.TrainersModel
import com.bumptech.glide.Glide

class TrainerListAdapter(
    val context: Context,
    val trainerList: List<TrainersModel>,
    val type: String,
    var typeWorkout: String?,
    var latitude: Double?,
    var longitude: Double?,
    var studio_id: String,
    val onProfileClick:(TrainersModel)-> Unit,
    var clickListener:(Boolean,Boolean, String, String)->Unit
) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        val relative : LinearLayout = item.findViewById(R.id.relative)
        val playerView : PlayerView = item.findViewById(R.id.playerView)
        val btnSoundToggle : ImageView = item.findViewById(R.id.btnSoundToggle)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {
        return if(type == "Linear") {
            val binding = TrainerListLinearAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ListViewHolder(binding)
        } else {
            val binding = TrainerListGridAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            GridViewHolder(binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = trainerListModels[position]

        when (holder) {
            is ListViewHolder -> holder.bind(item)
            is GridViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return trainerListModels.size
    }

    inner class ListViewHolder(val binding: TrainerListLinearAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trainersModel: TrainersModel) {
            val exerciseList = ArrayList<ExerciseModel>()
            for(i in 0 until trainersModel.activity.length()){
                var jsonObject=trainersModel.activity.optJSONObject(i)
                var exerciseModel=ExerciseModel()
                exerciseModel.id=jsonObject.optString("id")
                exerciseModel.name=jsonObject.optString("name")
                exerciseList.add(exerciseModel)
            }
            binding.exerciseRecyclerView.adapter = TrainerTagAdapter(context,exerciseList,type)
            binding.viewProfile.setOnClickListener {
                onProfileClick(trainersModel)
            }
            binding.userName.text = trainersModel.name
            if(trainersModel.averageRating == "null")
                binding.avgRating.text = "0"
            else
                binding.avgRating.text = trainersModel.averageRating
            binding.totalRatings.text = trainersModel.noOfRating+" ratings"
            binding.distance.text = trainersModel.distance
            binding.place.text = trainersModel.location
            Glide.with(context).load(trainersModel.profile).fitCenter().into(binding.trainerImage)
            binding.playerView.visibility = View.GONE
            binding.btnSoundToggle.visibility = View.GONE
            binding.trainerImage.visibility = View.VISIBLE
            binding.playerView.player = null

            if(trainersModel.isPackage == true){
                binding.bookSlot.text ="Book Slot"
                if(trainersModel.slot == "no"){
                    binding.bookSlot.setOnClickListener {}
                    binding.bookSlot.background= context.resources.getDrawable(R.drawable.bg_shape_btn_disabled,null)
                    binding.bookSlot.setTextColor(context.resources.getColor(R.color.white,null))
                }
                else {
                    binding.bookSlot.setOnClickListener {

                        val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                        clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)

                    }
                    binding.bookSlot.background=context.resources.getDrawable(R.drawable.primary_btn_gradient,null)
                    binding.bookSlot.setTextColor(context.resources.getColor(R.color.black,null))
                }
            }
            else{
                binding.bookSlot.text ="Select this trainer"
                binding.bookSlot.setOnClickListener {
                    val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                    clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)
                }
                binding.bookSlot.background = context.resources.getDrawable(R.drawable.primary_btn_gradient,null)
                binding.bookSlot.setTextColor(context.resources.getColor(R.color.black,null))

            }
        }
    }

    inner class GridViewHolder(val binding: TrainerListGridAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trainersModel: TrainersModel) {
            val exerciseList = ArrayList<ExerciseModel>()
            for(i in 0 until trainersModel.activity.length()){
                var jsonObject=trainersModel.activity.optJSONObject(i)
                var exerciseModel=ExerciseModel()
                exerciseModel.id=jsonObject.optString("id")
                exerciseModel.name=jsonObject.optString("name")
                exerciseList.add(exerciseModel)
            }
            binding.exerciseRecyclerView.adapter = TrainerTagAdapter(context,exerciseList,type)

            binding.viewProfile.setOnClickListener {
                onProfileClick(trainersModel)
            }
            binding.userName.text = trainersModel.name

            if(trainersModel.averageRating == "null")
                binding.avgRating.text = "0"
            else
                binding.avgRating.text = trainersModel.averageRating
            binding.totalRatings.text = trainersModel.noOfRating+" ratings"
            binding.distance.text = trainersModel.distance
            Glide.with(context).load(trainersModel.profile).fitCenter().into(binding.trainerImage)

            if(trainersModel.isPackage == true){
               // binding.bookSlot.text ="Book Slot"
                if(trainersModel.slot == "no"){
                    binding.trainerImage.setOnClickListener {}
                   // binding.bookSlot.background= context.resources.getDrawable(R.drawable.bg_shape_btn_disabled,null)
                 //   binding.bookSlot.setTextColor(context.resources.getColor(R.color.white,null))
                }
                else {
                    binding.trainerImage.setOnClickListener {

                        val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                        clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)

                    }
                  //  binding.bookSlot.background=context.resources.getDrawable(R.drawable.primary_btn_gradient,null)
                  //  binding.bookSlot.setTextColor(context.resources.getColor(R.color.black,null))
                }
            }
            else{
              //  binding.bookSlot.text ="Select this trainer"
                binding.trainerImage.setOnClickListener {
                    val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                    clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)
                }
               // binding.bookSlot.background = context.resources.getDrawable(R.drawable.primary_btn_gradient,null)
              //  binding.bookSlot.setTextColor(context.resources.getColor(R.color.black,null))

            }
        }
    }

     fun onBindViewHolderold(holder: ViewHolder, position: Int) {
        var trainersModel=trainerListModels[position]
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context)
        val exerciseList = ArrayList<ExerciseModel>()
        for(i in 0 until trainersModel.activity.length()){
            var jsonObject=trainersModel.activity.optJSONObject(i)
            var exerciseModel=ExerciseModel()
            exerciseModel.id=jsonObject.optString("id")
            exerciseModel.name=jsonObject.optString("name")
            exerciseList.add(exerciseModel)
        }
        holder.exerciseRecyclerView.adapter = TrainerTagAdapter(context,exerciseList,type)

        holder.bookSlot.tag = position
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

        if(trainersModel.isPackage == true){
            holder.bookSlot.text ="Book Slot"
            if(trainersModel.slot == "no"){
//            holder.hurryUp.text = "No slots available"
                holder.bookSlot.setOnClickListener {}
//            holder.availableSlots.visibility = View.GONE
                holder.bookSlot.background= context.resources.getDrawable(R.drawable.bg_shape_btn_disabled,null)
                holder.bookSlot.setTextColor(context.resources.getColor(R.color.white,null))
            }
            else {
//            holder.availableSlots.text = "Only ${trainersModel.slot} slots available"
                holder.bookSlot.setOnClickListener {
                    val pos = it.tag as Int
                    var trainersModel=trainerListModels[pos]

//                if (trainersModel.is_group==false){
                    val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                    clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)
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
                holder.bookSlot.background=context.resources.getDrawable(R.drawable.primary_btn_gradient,null)
//            holder.hurryUp.text = context.resources.getString(R.string.hurry_up)
//            holder.availableSlots.visibility = View.VISIBLE
                holder.bookSlot.setTextColor(context.resources.getColor(R.color.black,null))
            }
        }
        else{
            holder.bookSlot.text ="Select this trainer"
            holder.bookSlot.setOnClickListener {
                val pos = it.tag as Int
                var trainersModel=trainerListModels[pos]

                val query=if (typeWorkout =="home") "type=home&trainer_id=${trainersModel.id}"  else "type=gym&trainer_id=${trainersModel.id}&studio_id=${trainersModel.studio_id}"

                clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)
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
            holder.bookSlot.background = context.resources.getDrawable(R.drawable.primary_btn_gradient,null)
//            holder.hurryUp.text = context.resources.getString(R.string.hurry_up)
//            holder.availableSlots.visibility = View.VISIBLE
            holder.bookSlot.setTextColor(context.resources.getColor(R.color.black,null))

        }
        /*if(trainersModel.slot == "no"){
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

                    clickListener(trainersModel.isPackage?:false,trainersModel.is_group?:false,query,trainersModel.id)
//                    return@setOnClickListener
//                }
                *//*if (sharedPreferences.getString("typeWorkout","").equals("home")){
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

                }*//*

            }
            holder.bookSlot.setBackgroundColor(context.resources.getColor(R.color.headingcolor,null))
//            holder.hurryUp.text = context.resources.getString(R.string.hurry_up)
//            holder.availableSlots.visibility = View.VISIBLE
            holder.bookSlot.setTextColor(context.resources.getColor(R.color.buttontextcolor,null))
        }*/
        Glide.with(context).load(trainersModel.profile).fitCenter().into(holder.trainerImage)
        holder.playerView.visibility = View.GONE
        holder.btnSoundToggle.visibility = View.GONE
        holder.trainerImage.visibility = View.VISIBLE
        holder.playerView.player = null
    }

    fun filterList(filteredList: MutableList<TrainersModel>) {
        trainerListModels = filteredList
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    fun updateData(list: MutableList<TrainersModel>) {
        trainerListModels = list
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    init {
        this.trainerListModels = trainerList
    }
}
