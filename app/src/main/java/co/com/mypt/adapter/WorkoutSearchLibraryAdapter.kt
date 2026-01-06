package co.com.mypt.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.WorkoutLibraryActivity
import co.com.mypt.model.SearchWorkoutModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class WorkoutSearchLibraryAdapter(
    var context: Context?,
   var searchWorkoutArrayList: ArrayList<SearchWorkoutModel>
) : RecyclerView.Adapter<WorkoutSearchLibraryAdapter.WorkoutHolder>() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var searchworkoutModels: List<SearchWorkoutModel>

    class WorkoutHolder(view: View):RecyclerView.ViewHolder(view) {
        val play: ImageView = view.findViewById(R.id.play)
        val category_name: TextView = view.findViewById(R.id.category_name)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val exercise: TextView = view.findViewById(R.id.exercise)
        val im: ImageView = view.findViewById(R.id.im)
        val imheart: ImageView = view.findViewById(R.id.imheart)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_search_layout, parent, false)
        return WorkoutHolder(view)
    }

    override fun onBindViewHolder(
        holder: WorkoutHolder,
        position: Int) {
        var searchWorkoutModel=searchworkoutModels.get(position)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context)
        holder.play.tag = position
        holder.imheart.tag = position

        holder.play.setOnClickListener {
            var h=it.tag
            var searchWorkoutModel=searchworkoutModels.get(h as Int)
            val intent= Intent(context, WorkoutLibraryActivity::class.java)
            intent.putExtra("wokout_id",searchWorkoutModel.id)
            context?.startActivity(intent)
        }
        holder.tvName.text = searchWorkoutModel.name
        holder.category_name.text = searchWorkoutModel.category_name
        holder.tvTime.text = searchWorkoutModel.time
        holder.exercise.text = searchWorkoutModel.exercises
        Glide.with(context!!).load(searchWorkoutModel.image).fitCenter().into(holder.im)
        if (searchWorkoutModel.isFavourite.equals("false")){
            holder.imheart.setImageResource(R.drawable.heart)
        }else{
            holder.imheart.setImageResource(R.drawable.red_heart)
        }
        holder.imheart.setOnClickListener {
            if (sharedPreferences.getString("token", "").equals("")){
                val intent= Intent(context, PhoneNumberScreenActivity::class.java)
                context?.startActivity(intent)
            }else{
                AddToLike(searchWorkoutModel.id,holder.imheart)
            }

        }
    }

    override fun getItemCount(): Int {
        return searchworkoutModels.size
    }
    private fun AddToLike(id: String, imheart: ImageView) {
        val param: MutableMap<String, String> = HashMap()
        param["featured_id"] = id
        val progressDialog: Dialog = ProgressDialog.progressDialog(context!!,"")
        progressDialog.show()

        Log.e("AddTolikeParam", param.toString())

        PostMethod(ApiURL.make_favourite,param, context).startPostMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("AddToLikeRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        Toast.makeText(context,resp.optString("msg"),Toast.LENGTH_LONG).show()
                        var addToFavourite=resp.optJSONArray("data").optJSONObject(0).optString("isFavourite")
                        if (addToFavourite.equals("true")){
                            imheart.setImageResource(R.drawable.red_heart)
                        }else{
                            imheart.setImageResource(R.drawable.heart)

                        }

                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })
    }
    fun filterList(filteredList: MutableList<SearchWorkoutModel>) {
        searchworkoutModels = filteredList
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    init {
        this.searchworkoutModels = searchWorkoutArrayList
    }

    fun clearData() {
        searchWorkoutArrayList.clear()
        notifyDataSetChanged()
    }
}
