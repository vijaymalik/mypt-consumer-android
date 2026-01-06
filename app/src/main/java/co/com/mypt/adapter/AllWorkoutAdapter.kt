package co.com.mypt.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.WorkoutLibraryActivity
import co.com.mypt.model.FeaturedWorkoutModel
import co.com.mypt.model.SearchWorkoutModel
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class AllWorkoutAdapter(val context: Context?, val allWorkoutArrayList: ArrayList<SearchWorkoutModel>) :
    RecyclerView.Adapter<AllWorkoutAdapter.RowHolder>() {
    lateinit var sharedPreferences: SharedPreferences

    class RowHolder(view:View) : RecyclerView.ViewHolder(view) {
        val play: ImageView = view.findViewById(R.id.play)
        val im: ImageView = view.findViewById(R.id.im)
        val totalWorkouts: TextView = view.findViewById(R.id.totalWorkouts)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvkcal: TextView = view.findViewById(R.id.tvkcal)
        val tvCalorie: TextView = view.findViewById(R.id.tvCalorie)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val imheart: ImageView = view.findViewById(R.id.imheart)
        val linear: LinearLayout = view.findViewById(R.id.linear)
        val linearLayout: LinearLayout = view.findViewById(R.id.linearLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.featured_library_adapter, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return allWorkoutArrayList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val displayMetrics = Resources.getSystem().displayMetrics
        val dpWidth = displayMetrics.widthPixels

        holder.linear.layoutParams = LinearLayout.LayoutParams(
            dpWidth-100,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val layoutParams = RelativeLayout.LayoutParams(
            dpWidth/2,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,holder.linearLayout.id)
        layoutParams.addRule(RelativeLayout.ALIGN_TOP,holder.linearLayout.id)

        holder.im.layoutParams = layoutParams

        var featuredWorkoutModel=allWorkoutArrayList[position]
        holder.imheart.setTag(position)
        holder.totalWorkouts.visibility=View.GONE
        holder.tvTime.setText(featuredWorkoutModel.time)
        holder.tvCalorie.setText(featuredWorkoutModel.exercises)
        holder.tvkcal.setText(" Exercises")
        holder.tvName.setText(featuredWorkoutModel.name)
        holder.tvType.setText(featuredWorkoutModel.category_name)


        Glide.with(context!!).load(featuredWorkoutModel.image).fitCenter().error(R.drawable.gymgirl).into(holder.im)

        if (featuredWorkoutModel.isFavourite.equals("false")){
            holder.imheart.setImageResource(R.drawable.heart)
        }else{
            holder.imheart.setImageResource(R.drawable.red_heart)
        }


        holder.play.tag = position
        holder.play.setOnClickListener {
            var h=it.tag
            var featuredWorkoutModel=allWorkoutArrayList[h as Int]
            val intent= Intent(context, WorkoutLibraryActivity::class.java)
            intent.putExtra("wokout_id",featuredWorkoutModel.id)
            context?.startActivity(intent)
        }
        holder.imheart.setOnClickListener {
            if (sharedPreferences.getString("token", "").equals("")){
                val intent= Intent(context, PhoneNumberScreenActivity::class.java)
                context?.startActivity(intent)
            }else{
                AddToLike(featuredWorkoutModel.id,holder.imheart)
            }

        }
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


}
