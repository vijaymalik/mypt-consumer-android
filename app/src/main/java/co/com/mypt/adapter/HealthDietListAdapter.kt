package co.com.mypt.adapter

import android.app.Dialog
import android.content.Intent
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
import co.com.mypt.More.HealthStatusActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.MealListModel
import com.android.volley.VolleyError
import org.json.JSONObject
import kotlin.collections.ArrayList

class HealthDietListAdapter(
    var healthStatusActivity: HealthStatusActivity,
    var mealArrayList: ArrayList<MealListModel>
) :
    RecyclerView.Adapter<HealthDietListAdapter.DietHolder>() {
    class DietHolder (view: View):RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)
        var tvcal=view.findViewById<TextView>(R.id.tvcal)
        var tvprotein=view.findViewById<TextView>(R.id.tvprotein)
        var tvFat=view.findViewById<TextView>(R.id.tvFat)
        var heart1=view.findViewById<ImageView>(R.id.heart1)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HealthDietListAdapter.DietHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.health_meal_list, parent, false)
        return DietHolder(view)
    }

    override fun onBindViewHolder(holder: HealthDietListAdapter.DietHolder, position: Int) {
        var mealModel=mealArrayList[position]
        holder.tv.setText(mealModel.meal_name)
        holder.tvcal.setText(mealModel.calories)
        holder.tvprotein.setText(mealModel.proteins+"g")
        holder.tvFat.setText(mealModel.fats+"g")

        if (mealModel.is_saved.equals("false")){
            holder.heart1.setImageResource(R.drawable.heart)
        }else{
            holder.heart1.setImageResource(R.drawable.red_heart)
        }
        holder.heart1.setTag(position)
        holder.heart1.setOnClickListener{
            var j=it.tag
            var mealModel=mealArrayList[j as Int]
            addToLike(mealModel.id)

        }
    }

    private fun addToLike(id: String) {
        val param: MutableMap<String, String> = HashMap()
        param["meal_id"] = id

        Log.e("MealFavouriteParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(healthStatusActivity!!,"")


        PostMethod(ApiURL.mealfavourite,param, healthStatusActivity).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("mealFavouriteRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var intent= Intent("LikeFavourite")
                        intent.putExtra("type","LikeUnlike")
                        Toast.makeText(healthStatusActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
                        healthStatusActivity!!.sendBroadcast(intent)
                        notifyDataSetChanged()
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

    override fun getItemCount(): Int {
       return mealArrayList.size
    }

}
