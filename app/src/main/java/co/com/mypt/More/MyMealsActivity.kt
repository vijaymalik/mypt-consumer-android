package co.com.mypt.More

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.MealDateModel
import co.com.mypt.adapter.MealListAdapter
import co.com.mypt.adapter.MealVerticalListAdapter
import co.com.mypt.model.MealListModel
import com.android.volley.VolleyError
import org.json.JSONObject

class MyMealsActivity : AppCompatActivity() {
    var mealdate=""
    lateinit var dateRecyclerView:RecyclerView
    lateinit var linear:LinearLayout
    lateinit var imCreateMeal:ImageView
    lateinit var relative:RelativeLayout
    lateinit var mealRecyclerView:RecyclerView
    var dateArrayList = ArrayList<MealDateModel>()
    var mealArrayList = ArrayList<MealListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_meals)
        dateRecyclerView=findViewById(R.id.dateRecyclerView)
        mealRecyclerView=findViewById(R.id.mealecyclerView)
        relative=findViewById(R.id.relative)
        linear=findViewById(R.id.linear)
        imCreateMeal=findViewById(R.id.imCreateMeal)
        linear.setOnClickListener{
            finish()
        }
        relative.setOnClickListener{

        }


        getMealsDateList()


    }

    private fun getMealsData(mealdate: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@MyMealsActivity,"")
        progressDialog.show()

        var api=""
        api= ApiURL.user_meals+"?date="+mealdate

        Log.e("UserMealUrl",api)
        GetMethod(api
            ,this).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                mealArrayList.clear()
                Log.e("MealListDatewiseResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArray=jsonObj.optJSONArray("data")

                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                var json=jsonArray.optJSONObject(i)
                                var mealListModel= MealListModel()
                                mealListModel.meal_name=json.optString("meal_name")
                                mealListModel.id=json.optString("id")
                                mealListModel.meal_type=json.optString("meal_type")
                                mealListModel.calories=json.optString("calories")
                                mealListModel.proteins=json.optString("proteins")
                                mealListModel.carbs=json.optString("carbs")
                                mealListModel.fats=json.optString("fats")
                                mealListModel.meal_time=json.optString("meal_time")
                                mealListModel.fitness_goal=json.optString("fitness_goal")
                                mealArrayList.add(mealListModel)
                            }
                            mealRecyclerView.adapter = MealVerticalListAdapter(applicationContext,mealArrayList)
                            mealRecyclerView.visibility= View.VISIBLE


                        }else{
                            mealRecyclerView.visibility= View.GONE


                        }




                    }else{

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
    private fun getMealsDateList() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@MyMealsActivity,"")
        progressDialog.show()

        var api= ApiURL.mealsdate

        Log.e("UserMealDateList",api)
        GetMethod(api
            ,this).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                mealArrayList.clear()
                Log.e("MealListDatewiseResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){

                        var jsonArray=jsonObj.optJSONArray("data")

                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                var json=jsonArray.optJSONObject(i)
                                val model = MealDateModel()
                                model.date=json.optString("date")
                                model.is_today=json.optString("is_today")
                                if (i==0)
                                    getMealsData(json.optString("date"))
                                dateArrayList.add(model)
                            }
                            dateRecyclerView.adapter = MealListAdapter(applicationContext,dateArrayList)
                            imCreateMeal.visibility=View.GONE
                            dateRecyclerView.visibility=View.VISIBLE

                        }else{
                            dateRecyclerView.visibility= View.GONE
                            imCreateMeal.visibility=View.VISIBLE


                        }




                    }else{

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
    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.registerReceiver(selectedDate, IntentFilter("selectDate"),
                RECEIVER_EXPORTED)
        }else{
            applicationContext.registerReceiver(selectedDate, IntentFilter("selectDate"))

        }
    }
    val selectedDate = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            mealdate=""+intent!!.getStringExtra("selected_date")
            getMealsData(mealdate)

        }

    }
}