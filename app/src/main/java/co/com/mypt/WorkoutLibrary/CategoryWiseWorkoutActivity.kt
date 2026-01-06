package co.com.mypt.WorkoutLibrary

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.WorkoutSearchLibraryAdapter
import co.com.mypt.model.SearchWorkoutModel
import com.android.volley.VolleyError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class CategoryWiseWorkoutActivity : AppCompatActivity() {
    lateinit var recycler_workout: RecyclerView
    var searchWorkoutArrayList = ArrayList<SearchWorkoutModel>()
    lateinit var tvNOdata : ImageView
    lateinit var searchEditText : EditText
    lateinit var headerLayout : LinearLayout
    lateinit var workoutAdapter: WorkoutSearchLibraryAdapter

    var page = 1
    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null
    var isLoading = false
    lateinit var progressBar: ProgressBar
    var isSearching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_category_wise_workout)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        progressBar=findViewById(R.id.progressBar)
        recycler_workout=findViewById(R.id.recycler_workout)
        headerLayout=findViewById(R.id.headerLayout)
        tvNOdata=findViewById(R.id.tvNOdata)
        searchEditText=findViewById(R.id.searchEditText)
        getWorkoutList(true)
        headerLayout.setOnClickListener {
            finish()
        }
        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchJob?.cancel()

                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    isSearching = true
                    delay(500)
                    page=1
                    isLoading=true
                    getWorkoutList(false)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        recycler_workout.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isSearching) return

                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    page++
                    isLoading=true
                    progressBar.visibility =View.VISIBLE
                    loadMore()
                }
            }
        })


    }

    private fun _filter(text: String) {
        val filteredList: MutableList<SearchWorkoutModel> = ArrayList()
        for (item in searchWorkoutArrayList) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            recycler_workout.visibility = View.GONE
            tvNOdata.visibility = View.VISIBLE
        } else {
            recycler_workout.visibility= View.VISIBLE
            tvNOdata.visibility= View.GONE
            workoutAdapter!!.filterList(filteredList)
        }
    }

    fun loadMore(){
        val param: MutableMap<String, String> = HashMap()
        if (intent.getStringExtra("selectedScreen").equals("categoryWise")){
            param["type"] = ""+intent.getStringExtra("category_id")
            param["body_name"] = ""
        }else{
            param["type"] = ""
            param["body_name"] = ""+intent.getStringExtra("selectedBodyPart")
        }
        param["name"] = searchEditText.text.toString().trim()
        param["page"] = "1"
        param["muscle_id"] =""
        param["level"] = ""
        param["filter_by"] = ""
        param["calories"] =""
        param["duration"] =""

        Log.e("WorkoutListParam", param.toString())


        PostMethod(ApiURL.getworkouts,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                searchWorkoutArrayList.clear()
                try {
                    Log.e("CatWiseWorkoutListRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonData=resp.optJSONObject("data").optJSONArray("workouts")
                        if (jsonData.length()>0){
                            for (i in 0 until jsonData.length()){
                                var jsonObject=jsonData.optJSONObject(i)
                                val model = SearchWorkoutModel()
                                model.id = jsonObject.optString("id")
                                model.image = jsonObject.optString("image")
                                model.name = jsonObject.optString("name")
                                model.type = jsonObject.optString("type")
                                model.category_name = jsonObject.optString("category_name")
                                model.exercises = jsonObject.optString("exercises")
                                model.time = jsonObject.optString("time")
                                model.isFavourite = jsonObject.optString("isFeatured")
                                searchWorkoutArrayList.add(model)
                            }
                            workoutAdapter.notifyDataSetChanged()
                        }
                    }
                    progressBar.visibility = View.GONE
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    progressBar.visibility = View.GONE
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressBar.visibility = View.GONE
                error!!.printStackTrace()
            }

        })
    }
    fun getWorkoutList(showProgress: Boolean) {
        val param: MutableMap<String, String> = HashMap()
        if (intent.getStringExtra("selectedScreen").equals("categoryWise")){
            param["type"] = ""+intent.getStringExtra("category_id")
            param["body_name"] = ""
        }else{
            param["type"] = ""
            param["body_name"] = ""+intent.getStringExtra("selectedBodyPart")
        }
        param["name"] = searchEditText.text.toString().trim()
        param["page"] = ""
        param["muscle_id"] =""
        param["level"] = ""
        param["filter_by"] = ""
        param["calories"] =""
        param["duration"] =""

        Log.e("WorkoutListParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        if(showProgress)
            progressDialog.show()

        PostMethod(ApiURL.getworkouts,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                searchWorkoutArrayList.clear()
                try {
                    Log.e("CatWiseWorkoutListRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonData=resp.optJSONObject("data").optJSONArray("workouts")
                        if (jsonData.length()>0){
                            for (i in 0 until jsonData.length()){
                                var jsonObject=jsonData.optJSONObject(i)
                                val model = SearchWorkoutModel()
                                model.id = jsonObject.optString("id")
                                model.image = jsonObject.optString("image")
                                model.name = jsonObject.optString("name")
                                model.type = jsonObject.optString("type")
                                model.category_name = jsonObject.optString("category_name")
                                model.exercises = jsonObject.optString("exercises")
                                model.time = jsonObject.optString("time")
                                model.isFavourite = jsonObject.optString("isFeatured")
                                searchWorkoutArrayList.add(model)
                            }
                            workoutAdapter= WorkoutSearchLibraryAdapter(this@CategoryWiseWorkoutActivity,searchWorkoutArrayList)
                            recycler_workout.adapter=workoutAdapter
                            recycler_workout.visibility=View.VISIBLE
                            tvNOdata.visibility=View.GONE

                            loadMoreJob?.cancel()
                            loadMoreJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                isSearching = false
                                isLoading = false
                            }
                        }
                        else{
                            recycler_workout.visibility=View.GONE
                            tvNOdata.visibility=View.VISIBLE
                        }
                    }else{
                        recycler_workout.visibility=View.GONE
                        tvNOdata.visibility=View.VISIBLE
                    }
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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