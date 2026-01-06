package co.com.mypt.WorkoutLibrary

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.WorkoutBodyPartsAdapter
import co.com.mypt.adapter.WorkoutSearchLibraryAdapter
import co.com.mypt.adapter.WorkoutTypeAdapter
import co.com.mypt.model.BodyPartsModel
import co.com.mypt.model.CityModel
import co.com.mypt.model.DoubleRangeSlider
import co.com.mypt.model.SearchWorkoutModel
import co.com.mypt.model.SingleRangeBar
import co.com.mypt.model.WorkoutLevelModel
import co.com.mypt.model.WorkoutTypeModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class SearchWorkoutActivity : AppCompatActivity() {
    lateinit var tvApplyFilter: TextView
    lateinit var headerLayout: LinearLayout
    var filterby_id=""
    lateinit var filterBy: AutoCompleteTextView
    lateinit var bodyPartsRecyclerView: RecyclerView
    lateinit var tvBody: TextView
    lateinit var tvWorkout: TextView
    private lateinit var workoutLevelRecyclerView: RecyclerView
    lateinit var workoutTypeRecyclerView: RecyclerView
    lateinit var featuredLibraryRecycler:RecyclerView
    lateinit var linearSearch:LinearLayout
    var searchWorkoutArrayList = ArrayList<SearchWorkoutModel>()
    var workoutTypeArrayList = ArrayList<WorkoutTypeModel>()
    var bodypartsArrayList = ArrayList<BodyPartsModel>()
    var workoutLvelArrayList = ArrayList<WorkoutLevelModel>()
    lateinit var filter : TextView
    lateinit var tvcount : TextView
    lateinit var tvNOdata : ImageView
    lateinit var imfilter : ImageView
    lateinit var filterBottomSheetDialog: BottomSheetDialog
    var filterByArrayList=ArrayList<String>()
    var filterList = ArrayList<CityModel>()
    var workout_type_id=ArrayList<Int>()
    var workoutTypeseparted_id=""
    var bodyPart_id=""
    var min_duration=-1
    var max_duration=-1
    var calorie_value=-1
    lateinit var BodyPartsAdapter:WorkoutBodyPartsAdapter
    lateinit var searchEditText : EditText

    lateinit var doubleRangeSlider: DoubleRangeSlider
    lateinit var singleRangeSlider: SingleRangeBar
    var filtercount=""
    lateinit var workoutAdapter: WorkoutSearchLibraryAdapter
    lateinit var progressBar: ProgressBar
    var page = 1
    var limit = 2
    var isLoading = false
    var isSearching = false
    private var loadMoreJob: Job? = null

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_workout)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        progressBar=findViewById(R.id.progressBar)
        featuredLibraryRecycler=findViewById(R.id.featuredLibraryRecycler)
        linearSearch=findViewById(R.id.linearSearch)
        headerLayout=findViewById(R.id.headerLayout)
        filter=findViewById(R.id.filter)
        tvcount=findViewById(R.id.tvcount)
        tvNOdata=findViewById(R.id.tvNOdata)
        imfilter=findViewById(R.id.imfilter)
        searchEditText=findViewById(R.id.searchEditText)

        workoutAdapter = WorkoutSearchLibraryAdapter(this@SearchWorkoutActivity,searchWorkoutArrayList)
        featuredLibraryRecycler.adapter = workoutAdapter

        headerLayout.setOnClickListener {
            finish()
        }


      //  filterBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        createFilterBottomSheet()
        filter.setOnClickListener {
            filterBottomSheetDialog.show()
        }
        getWorkoutType()
        getBodyPartsList()
        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                page=1
                isSearching = true
                searchJob?.cancel()

                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500) // wait for 500ms after typing stops
                    isLoading=true
                    getWorkoutList(page,false)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // adding scroll listener to recycler view for pagination
        featuredLibraryRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isSearching) return

                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    page++
                    isLoading=true
                    progressBar.visibility =View.VISIBLE
                    loadMoreData(page)
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createFilterBottomSheet() {

        filterBottomSheetDialog = BottomSheetDialog(this, R.style.FullScreenBottomSheet)

        val bottomSheet = layoutInflater.inflate(R.layout.filter_bottomsheet_layout, null)
        filterBottomSheetDialog.setContentView(bottomSheet)
        filterBottomSheetDialog.setOnShowListener {
            val bottomSheet = filterBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isHideable = true
                it.setBackgroundColor(Color.TRANSPARENT)
            }
        }

     /*   var standard_bottom_sheet = bottomSheet.findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.skipCollapsed = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 50*/

        workoutTypeRecyclerView =bottomSheet.findViewById<RecyclerView>(R.id.workoutTypeRecyclerView)
        bodyPartsRecyclerView =bottomSheet.findViewById<RecyclerView>(R.id.bodyPartsRecyclerView)
        workoutLevelRecyclerView =bottomSheet.findViewById<RecyclerView>(R.id.workoutLevelRecyclerView)
        doubleRangeSlider =bottomSheet.findViewById<DoubleRangeSlider>(R.id.doubleRangeSlider)
        singleRangeSlider =bottomSheet.findViewById<SingleRangeBar>(R.id.singleRangeSlider)
        tvWorkout =bottomSheet.findViewById<TextView>(R.id.tvWorkout)
        tvBody =bottomSheet.findViewById<TextView>(R.id.tvBody)
        tvApplyFilter =bottomSheet.findViewById<TextView>(R.id.tvApplyFilter)
        filterBy =bottomSheet.findViewById<AutoCompleteTextView>(R.id.filterBy)
       /* val min = doubleRangeSlider.currentMinValue
        val max = doubleRangeSlider.currentMaxValue
        Log.d("SliderRange", "Selected range: $min - $max")*/

        val min = doubleRangeSlider.getSelectedMinValue()
        val max = doubleRangeSlider.getSelectedMaxValue()
        Log.d("SliderRange", "Selected range: $min - $max")
        doubleRangeSlider.setOnRangeChangeListener(object : DoubleRangeSlider.OnRangeChangeListener {

            override fun onRangeChanged(min: Float, max: Float) {
                Log.d("Slider", "Selected range: $min - $max")
                min_duration=min.toInt()
                max_duration=max.toInt()
                updateFilterCount()
                // You can update a TextView here if needed
            }
        })
        singleRangeSlider.setOnValueChangeListener(object : SingleRangeBar.OnValueChangeListener {
            override fun onValueChanged(value: Float) {
                Log.d("SingleRangeSlider", "Slider value: $value")
                calorie_value=value.toInt()
                updateFilterCount()

                // Update your UI (TextView etc.) here
            }
        })

// Optional: Get value any time
        singleRangeSlider.getCurrentValue()
        tvApplyFilter.setOnClickListener {
            bodyPart_id = BodyPartsAdapter.getSelectedId().toString()
            getWorkoutList(page, true)
            filterBottomSheetDialog.dismiss()
        }

        filterBy.setOnTouchListener { v, event ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            filterBy.showDropDown()
            true
        }
        filterBy.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selection = parent.getItemAtPosition(position) as String
                var pos = -1
                for (i in filterList.indices) {
                    if (selection.contains(filterList[i].name)) {
                        pos = i
                        break
                    }
                }

                filterby_id = filterList[pos].id
                updateFilterCount()
            }

        val window = filterBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(filterBottomSheetDialog)
        filterBottomSheetDialog.setOnShowListener {
            val bottomSheet = filterBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        filterBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    fun getWorkoutList(page1: Int, showLoader: Boolean) {
        val param: MutableMap<String, String> = HashMap()
        param["type"] = workoutTypeseparted_id
        param["page"] = ""+page1
        param["name"] = ""+searchEditText.text.toString()
        param["per_page"] = ""
        if (bodyPart_id != "null"){
            param["muscle_id"] =bodyPart_id

        }else{
            param["muscle_id"] =""

        }
        param["level"] = ""
        if (filterby_id.equals("")){
        }else{
            param["filter_by"] = filterby_id
        }
        if(calorie_value==-1){
            param["calories"] =""
        }else{
            param["calories"] =""+calorie_value
        }
        if (min_duration==-1){
            param["duration"] =""
        }else{
            param["duration"] =min_duration.toString()+"-"+max_duration.toString()
        }

        Log.e("WorkoutListParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        if(showLoader)
            progressDialog.show()

        PostMethod(ApiURL.getworkouts,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                searchWorkoutArrayList.clear()
                try {
                    Log.e("SearchWorkoutListRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        val jsonData=resp.optJSONObject("data").optJSONArray("workouts")
                        if (jsonData.length()>0){
                            for (i in 0 until jsonData.length()){
                                val jsonObject=jsonData.optJSONObject(i)
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
                            featuredLibraryRecycler.visibility=View.VISIBLE
                            tvNOdata.visibility=View.GONE
                            imfilter.visibility=View.VISIBLE
                            filter.visibility=View.VISIBLE
                            tvcount.text = ""+searchWorkoutArrayList.size+" Results  Found"

                            loadMoreJob?.cancel()
                            loadMoreJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                isSearching = false
                                isLoading = false
                            }

                        }else{
                            tvcount.text = "0"+" Results  Found"
                            featuredLibraryRecycler.visibility=View.GONE
                            tvNOdata.visibility=View.VISIBLE
                            imfilter.visibility=View.GONE
                            filter.visibility=View.GONE

                        }

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
    fun loadMoreData(page1: Int) {
        val param: MutableMap<String, String> = HashMap()
        param["type"] = workoutTypeseparted_id
        param["page"] = ""+page1
        param["name"] = ""+searchEditText.text.toString()
        param["per_page"] = ""
        if (bodyPart_id != "null"){
            param["muscle_id"] =bodyPart_id

        }else{
            param["muscle_id"] =""

        }
        param["level"] = ""
        if (filterby_id.equals("")){
        }else{
            param["filter_by"] = filterby_id
        }
        if(calorie_value==-1){
            param["calories"] =""
        }else{
            param["calories"] =""+calorie_value
        }
        if (min_duration==-1){
            param["duration"] =""
        }else{
            param["duration"] =min_duration.toString()+"-"+max_duration.toString()
        }

        Log.e("LoadWorkoutListParam", param.toString())

        /*val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()*/

        PostMethod(ApiURL.getworkouts,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                //progressDialog.dismiss()
                progressBar.visibility =View.GONE
                try {
                    Log.e("SearchWorkoutListRes",data.toString())
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
                            isSearching = false
                            isLoading = false
                            tvcount.text = ""+searchWorkoutArrayList.size+" Results  Found"
                        }
                    }
                    // Toast.makeText(this@PhoneNumberScreenActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressBar.visibility =View.GONE
                error!!.printStackTrace()
            }

        })
    }
    private fun getWorkoutType() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@SearchWorkoutActivity,"")
        progressDialog.show()
        Log.e("APi,",""+ApiURL.workout_types)
        GetMethod(ApiURL.workout_types,this).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                workoutTypeArrayList.clear()
                workoutLvelArrayList.clear()
                filterByArrayList.clear()
                Log.e("TypeLevelResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayCategory=jsonObj.optJSONObject("data").optJSONArray("allcategory")
                        var jsonArrayfilters=jsonObj.optJSONObject("data").optJSONArray("filters")
                        jsonObj.optJSONObject("data").optJSONArray("workoutLevels")
                        for (i in 0 until jsonArrayCategory.length()) {
                            var jsonObject1 = jsonArrayCategory.optJSONObject(i)
                            var workoutModel= WorkoutTypeModel()
                            workoutModel.image = ""+jsonObject1.optString("icon")
                            workoutModel.id = ""+jsonObject1.optString("id")
                            workoutTypeArrayList.add(workoutModel)
                        }
                        var workoutTypeAdapter = WorkoutTypeAdapter(applicationContext, workoutTypeArrayList)
                        workoutTypeRecyclerView.adapter = workoutTypeAdapter


                        for(i in 0 until jsonArrayfilters.length()){
                            var jsonObject1=jsonArrayfilters.optJSONObject(i)
                            var cityModel= CityModel()

                            cityModel.id=jsonObject1.optString("id")
                            cityModel.name=jsonObject1.optString("name")
                            filterList.add(cityModel)
                            filterByArrayList.add(jsonObject1.optString("name"))

                        }
                        val adapter = ArrayAdapter(applicationContext,R.layout.custom_dropdown_item, filterByArrayList)
                        filterBy.setAdapter(adapter)
//                        if (jsonArrayWorkoutLevel.length()>0){
//                            for (i in 0 until jsonArrayWorkoutLevel.length()) {
//                                var jsonObject1 = jsonArrayWorkoutLevel.optJSONObject(i)
//                                var workoutLevelModel= WorkoutLevelModel()
//                                workoutLevelModel.name = ""+jsonObject1.optString("name")
//                                workoutLevelModel.id = ""+jsonObject1.optString("id")
//                                workoutLvelArrayList.add(workoutLevelModel)
//                            }
//                            var WorkoutLevelAdapter = WorkoutLevelAdapter(applicationContext, workoutLvelArrayList)
//                            workoutLevelRecyclerView.adapter = WorkoutLevelAdapter
//                            tvWorkout.visibility=View.VISIBLE
//                            workoutTypeRecyclerView.visibility=View.VISIBLE
//                        }else{
//                            tvWorkout.visibility=View.GONE
//                            workoutTypeRecyclerView.visibility=View.GONE
//                        }

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
    private fun getBodyPartsList() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@SearchWorkoutActivity,"")
        progressDialog.show()
        Log.e("APiBodyParts,",""+ApiURL.bodyparts)

        GetMethod(ApiURL.bodyparts,this).startMethod(object :
            ResponseData {

            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("BodyPartResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("success")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                var jsonObject1 = jsonArray.optJSONObject(i)
                                var BodyPartsModel= BodyPartsModel()
                                BodyPartsModel.name = jsonObject1.optString("name")
                                BodyPartsModel.id = jsonObject1.optString("id")
                                BodyPartsModel.image = jsonObject1.optString("image")
                                bodypartsArrayList.add(BodyPartsModel)
                            }
                            BodyPartsAdapter = WorkoutBodyPartsAdapter(this@SearchWorkoutActivity, bodypartsArrayList)
                            bodyPartsRecyclerView.adapter = BodyPartsAdapter



                            tvBody.visibility=View.VISIBLE
                            bodyPartsRecyclerView.visibility=View.VISIBLE

                        }else{
                            tvBody.visibility=View.GONE
                            bodyPartsRecyclerView.visibility=View.GONE
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        getWorkoutList(page, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.registerReceiver(selectedWorkoutType, IntentFilter("selectedWorkoutType"),
                RECEIVER_EXPORTED)
        }else{
            applicationContext.registerReceiver(selectedWorkoutType, IntentFilter("selectedWorkoutType"),RECEIVER_EXPORTED)

        }
    }
    val selectedWorkoutType = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            workout_type_id= intent!!.getIntegerArrayListExtra("selectedPositions")!!
            workoutTypeseparted_id = workout_type_id.joinToString(",")
            Log.e("workout_type",""+workout_type_id)
            updateFilterCount()

        }

    }

    fun updateFilterCount() {
        bodyPart_id = BodyPartsAdapter.getSelectedId().toString()
        Log.e("body_part_idupdate",bodyPart_id)
        var filterCount = 0

        // 1. Filter By (AutoCompleteTextView)
        if (!filterby_id.isNullOrEmpty()) filterCount++


        if (!workout_type_id.isNullOrEmpty()) filterCount=filterCount+workout_type_id.size

        if (bodyPart_id != "null") filterCount++

        if (min_duration > -1 || max_duration > -1) filterCount++ // assuming default range is 0-60 mins

        if (calorie_value > -1) filterCount++ // assuming default is 0

        tvApplyFilter.text = "Apply Filter ($filterCount)"
        filter.text = "Filter($filterCount)"

    }
    private fun _filter(text: String) {
        val filteredList: MutableList<SearchWorkoutModel> = ArrayList()
        for (item in searchWorkoutArrayList) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            featuredLibraryRecycler.visibility = View.GONE
            tvNOdata.visibility = View.VISIBLE
        } else {
            featuredLibraryRecycler.visibility= View.VISIBLE
            tvNOdata.visibility= View.GONE
            workoutAdapter!!.filterList(filteredList)
        }
    }
}