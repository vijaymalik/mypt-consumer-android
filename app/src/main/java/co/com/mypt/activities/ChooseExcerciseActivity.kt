package co.com.mypt.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.AddExcerciseAdapter
import co.com.mypt.adapter.EquipmentAdapter
import co.com.mypt.adapter.MuscleAdapter
import co.com.mypt.adapter.SelectExcerciseAdapter
import co.com.mypt.model.AddExcerciseModel
import co.com.mypt.model.EquipmentModel
import co.com.mypt.model.MusclesModel
import co.com.mypt.model.SelectExcerciseModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.text.replace

class ChooseExcerciseActivity : AppCompatActivity() {
    lateinit var recyclerExercise: RecyclerView
    lateinit var linearMuscle: LinearLayout
    lateinit var headerLayout: LinearLayout
    lateinit var linearEquipment: LinearLayout
    lateinit var linearNoExercise: LinearLayout
    lateinit var nested: NestedScrollView
    lateinit var tvAddExercise: TextView
    lateinit var recyclerAddedExercise: RecyclerView
    lateinit var recyclerEqupment: RecyclerView
    lateinit var recyclerMuscle: RecyclerView
    lateinit var chooseExcerciseAdapter:SelectExcerciseAdapter
    lateinit var addExcerciseAdapter:AddExcerciseAdapter
    var excerxiseModelList :ArrayList<SelectExcerciseModel> = ArrayList()
    var alreadySelectedExcerxiseModelList :ArrayList<SelectExcerciseModel> = ArrayList()
    var addexcerxiseModelList :ArrayList<AddExcerciseModel> = ArrayList()
    var musclesModelList :ArrayList<MusclesModel> = ArrayList()
    var equipmentModelList :ArrayList<EquipmentModel> = ArrayList()
    var addexcerxiseArrayList :ArrayList<String> = ArrayList()
    lateinit var standard_bottom_sheet:LinearLayout
    lateinit var musleBottomSheetDialog:BottomSheetDialog
    lateinit var equipmentBottomSheetDialog:BottomSheetDialog
    var page = 1
    lateinit var searchEditText : EditText
    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null
    var limit = 2
    var isLoading = false
    lateinit var progressBar: ProgressBar
    var isSearching = false
    var selectedMusclesIds = ArrayList<Int>()
    var selectedEquipmentIds = ArrayList<Int>()
    var selectedExercisemodelLists = ArrayList<SelectExcerciseModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose_excercise)
        progressBar=findViewById(R.id.progressBar)

        musleBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        equipmentBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        tvAddExercise=findViewById(R.id.tvAddExercise)
        headerLayout=findViewById(R.id.headerLayout)
        nested=findViewById(R.id.nested)
        linearNoExercise=findViewById(R.id.linearNoExercise)
        recyclerExercise=findViewById(R.id.recyclerExercise)
        linearMuscle=findViewById(R.id.linearMuscle)
        recyclerAddedExercise=findViewById(R.id.recyclerAddedExercise)
        linearEquipment=findViewById(R.id.linearEquipment)
        searchEditText=findViewById(R.id.searchEditText)
        val htmlString = "<font color=#959595>Search for </font><font color=#FAFAFA>Exercise</font>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        searchEditText.hint = spanned

        createMuscleLayout()
        createEquipmentLayout()
        linearMuscle.setOnClickListener{
            musleBottomSheetDialog.show()

        }

        tvAddExercise.setOnClickListener{
           if (selectedExercisemodelLists.size>0){
               if (intent.getStringExtra("type").equals("superset")){
                   var intent=Intent("selectedSupersetExerciseList")
                   intent.putParcelableArrayListExtra("exercise_list", selectedExercisemodelLists)
                   Log.e("selectedExerciseModel",""+selectedExercisemodelLists.size)
                   sendBroadcast(intent)
                   finish()
               }else{
                   var intent=Intent("selectedExerciseList")
                   intent.putParcelableArrayListExtra("exercise_list", selectedExercisemodelLists)
                   Log.e("selectedExerciseModel",""+selectedExercisemodelLists.size)
                   sendBroadcast(intent)
                   finish()
               }


           }

        }

        headerLayout.setOnClickListener{
          finish()
        }
        linearEquipment.setOnClickListener{
            equipmentBottomSheetDialog.show()

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            alreadySelectedExcerxiseModelList =
                intent.getParcelableArrayListExtra("exercise_listfromAdd", SelectExcerciseModel::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            alreadySelectedExcerxiseModelList = intent.getParcelableArrayListExtra("exercise_listfromAdd")!!
        }
        if (alreadySelectedExcerxiseModelList.size>0){
            if (intent.getStringExtra("type").equals("regular")){
                tvAddExercise.setText("ADD Exercise (${alreadySelectedExcerxiseModelList.size})")
            }
            tvAddExercise.background = resources.getDrawable(R.drawable.white_rectangle)
            tvAddExercise.setTextColor(resources.getColor(R.color.buttontextcolor))
            tvAddExercise.setTypeface(null, Typeface.BOLD)
        }else{
            if (intent.getStringExtra("type").equals("regular")){
                tvAddExercise.setText("ADD Exercise")
            }
            tvAddExercise.background = resources.getDrawable(R.drawable.rectangle_btn)
            tvAddExercise.setTextColor(resources.getColor(R.color.subheadingcolor))
            tvAddExercise.setTypeface(null, Typeface.NORMAL)
        }
        selectedExercisemodelLists=alreadySelectedExcerxiseModelList
        chooseExcerciseAdapter= SelectExcerciseAdapter(excerxiseModelList,this@ChooseExcerciseActivity,alreadySelectedExcerxiseModelList)
        recyclerExercise.adapter=chooseExcerciseAdapter

        for (i in 0..4) {
            var addexerciseModel= AddExcerciseModel()
            addexerciseModel.name="Cardio"
            addexcerxiseModelList.add(addexerciseModel)
        }
        addExcerciseAdapter= AddExcerciseAdapter(addexcerxiseModelList,this)
        recyclerAddedExercise.adapter=addExcerciseAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchJob?.cancel()

                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    isSearching = true
                    delay(500)
                    page=1
                    isLoading=true
                    getExercise(page,false)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

       /* recyclerExercise.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isSearching) return

                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    page++
                    isLoading=true
                    progressBar.visibility =View.VISIBLE
                   // loadMoreData(page)
                }
            }
        })
*//*
        nested.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                Toast.makeText(applicationContext,"check", Toast.LENGTH_SHORT).show()
                if (isSearching)
                    return@setOnScrollChangeListener
                Toast.makeText(applicationContext,"check1", Toast.LENGTH_SHORT).show()
                if (scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)
                    && scrollY > oldScrollY //&& !isLoading
                ) {
                    Toast.makeText(applicationContext,"check2", Toast.LENGTH_SHORT).show()

                    page++
                    isLoading=true
                    progressBar.visibility =View.VISIBLE
                    loadMoreData(page)
                }
            }
        }*/
        nested.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, _ ->

            val child = v.getChildAt(0) ?: return@setOnScrollChangeListener
            val diff = child.measuredHeight - v.height - scrollY
            if (isSearching)
                return@setOnScrollChangeListener

            if (diff <= 50 && !isLoading) { // 50px tolerance
                page++
                isLoading=true
                progressBar.visibility =View.VISIBLE
                loadMoreData(page)

            }
        }
    }
    fun loadMoreData(page1: Int) {
        val param: MutableMap<String, String> = HashMap()
        param["page"] = ""+page1
        param["level"] = ""+searchEditText.text.toString()
        Log.e("LoadExerciseListParam", param.toString())

        /*val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()*/

        PostMethod(ApiURL.getexercises,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                //progressDialog.dismiss()
                progressBar.visibility =View.GONE
                try {
                    Log.e("LoadMoreExerciseRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonArray=resp.optJSONObject("data").optJSONArray("data")
                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                var exerciseModel= SelectExcerciseModel()
                                var jsonObject=jsonArray.optJSONObject(i)
                                exerciseModel.id=jsonObject.optString("id")
                                exerciseModel.name=jsonObject.optString("name")
                                exerciseModel.image=jsonObject.optString("image")
                                exerciseModel.calories=jsonObject.optString("calories")
                                exerciseModel.video_type=jsonObject.optString("video_type")
                                exerciseModel.video_path=jsonObject.optString("video_path")
                                exerciseModel.raps=jsonObject.optString("raps")
                                exerciseModel.category=jsonObject.optString("category")
                                exerciseModel.type="exercise"
                                exerciseModel.rest_duration=jsonObject.optString("rest_duration")
                                exerciseModel.duration=jsonObject.optString("duration")
                                excerxiseModelList.add(exerciseModel)
                            }
                            chooseExcerciseAdapter.notifyDataSetChanged()
                            isSearching = false
                            isLoading = false
                            recyclerExercise.visibility=View.VISIBLE
                            linearNoExercise.visibility=View.GONE
                        }else{
                            recyclerExercise.visibility=View.GONE
                            linearNoExercise.visibility=View.VISIBLE
                        }


                    }else{

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

    private fun createEquipmentLayout() {
        val bottomSheet = layoutInflater.inflate(R.layout.equipment_bottomsheet, null)
        recyclerEqupment =bottomSheet.findViewById<RecyclerView>(R.id.recyclerEqupment)
        var tvclear =bottomSheet.findViewById<TextView>(R.id.tvclear)
        var tvApply =bottomSheet.findViewById<TextView>(R.id.tvApply)

        tvclear.setOnClickListener {
            selectedEquipmentIds.clear()
            getExercise(page,true)
            equipmentBottomSheetDialog.dismiss()
            getWorkoutType()

        }
        tvApply.setOnClickListener {
           /* if (selectedEquipmentIds.size>0){
                page=1
                getExercise(page,true)
            }else{
                Toast.makeText(applicationContext,"Please select at least one equipment", Toast.LENGTH_SHORT).show()
            }*/
            page=1
            getExercise(page,true)
            equipmentBottomSheetDialog.dismiss()
        }

        equipmentBottomSheetDialog.setContentView(bottomSheet)

        val window = equipmentBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun createMuscleLayout() {
        val bottomSheet = layoutInflater.inflate(R.layout.muscle_layout, null)

        recyclerMuscle =bottomSheet.findViewById<RecyclerView>(R.id.recyclerMuscle)
        var tvClear =bottomSheet.findViewById<TextView>(R.id.tvClear)
        var tvApply =bottomSheet.findViewById<TextView>(R.id.tvApply)
        tvClear.setOnClickListener {
            selectedMusclesIds.clear()
            musleBottomSheetDialog.dismiss()
            getExercise(page,true)
            getBodyPartsList()

        }
        tvApply.setOnClickListener {
            /*if (selectedMusclesIds.size>0){
                page=1
                getExercise(page,true)
            }else{
                Toast.makeText(applicationContext,"Please select at least one muscle", Toast.LENGTH_SHORT).show()
            }*/
            page=1
            getExercise(page,true)
            musleBottomSheetDialog.dismiss()
        }

        musleBottomSheetDialog.setContentView(bottomSheet)

        val window = musleBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(countReceiver, IntentFilter("selectedExercise"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(countReceiver, IntentFilter("selectedExercise"))

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(selectedMuscle, IntentFilter("selectedMuscleId"), RECEIVER_EXPORTED)
        }
        else{
            registerReceiver(selectedMuscle, IntentFilter("selectedMuscleId"))

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(selectedEquipment, IntentFilter("selectedEquipmentId"), RECEIVER_EXPORTED)
        }
        else{
            registerReceiver(selectedEquipment, IntentFilter("selectedEquipmentId"))

        }

        getExercise(page, true)
        getBodyPartsList()
        getWorkoutType()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

    }
    private fun getExercise(page1: Int, showLoader: Boolean) {
        val param: MutableMap<String, String> = HashMap()
        param["page"] =""+page1
        param["level"] = ""+searchEditText.text.toString()
        param["category_id"] = selectedEquipmentIds.toString().replace("[","").replace("]","").replace(" ","")

        param["group_ids"] = selectedMusclesIds.toString().replace("[","").replace("]","").replace(" ","")

        Log.e("getExerciseParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        if(showLoader)
            progressDialog.show()


        PostMethod(ApiURL.getexercises,param, this).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                excerxiseModelList.clear()
                try {
                    Log.e("getexerciseRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonArray=resp.optJSONObject("data").optJSONArray("data")
                        if (jsonArray.length()>0){
                            for(i in 0 until jsonArray.length()){
                                var exerciseModel= SelectExcerciseModel()
                                var jsonObject=jsonArray.optJSONObject(i)
                                exerciseModel.id=jsonObject.optString("id")
                                exerciseModel.name=jsonObject.optString("name")
                                exerciseModel.image=jsonObject.optString("image")
                                exerciseModel.calories=jsonObject.optString("calories")
                                exerciseModel.video_type=jsonObject.optString("video_type")
                                exerciseModel.video_path=jsonObject.optString("video_path")
                                exerciseModel.raps=jsonObject.optString("raps")
                                exerciseModel.category=jsonObject.optString("category")
                                exerciseModel.sets=jsonObject.optString("sets")
                                exerciseModel.type="exercise"
                                exerciseModel.rest_duration=jsonObject.optString("rest_duration")
                                exerciseModel.duration=jsonObject.optString("duration")
                                excerxiseModelList.add(exerciseModel)
                            }
                            chooseExcerciseAdapter.notifyDataSetChanged()

                            recyclerExercise.visibility=View.VISIBLE
                            linearNoExercise.visibility=View.GONE

                            loadMoreJob?.cancel()
                            loadMoreJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                isSearching = false
                                isLoading = false
                            }
                        }else{
                            recyclerExercise.visibility=View.GONE
                            linearNoExercise.visibility=View.VISIBLE
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
    override fun onStop() {
        super.onStop()
        unregisterReceiver(countReceiver)
    }

    val countReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent1: Intent?) {
            selectedExercisemodelLists = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent1?.getParcelableArrayListExtra("exercise_list", SelectExcerciseModel::class.java)
            } else {
                @Suppress("DEPRECATION")
                (intent1?.getParcelableArrayListExtra("exercise_list"))
            })!!

            if (selectedExercisemodelLists.size>0) {
                tvAddExercise.setText("ADD Exercise")
                if (getIntent().getStringExtra("type").equals("regular")){
                    tvAddExercise.setText("ADD Exercise (${selectedExercisemodelLists.size})")
                }
                tvAddExercise.background = resources.getDrawable(R.drawable.white_rectangle)
                tvAddExercise.setTextColor(resources.getColor(R.color.buttontextcolor))
                tvAddExercise.setTypeface(null, Typeface.BOLD)

            }else{
                tvAddExercise.setText("ADD Exercise")
                if (getIntent().getStringExtra("type").equals("regular")){
                    tvAddExercise.setText("ADD Exercise")
                }
                tvAddExercise.background = resources.getDrawable(R.drawable.rectangle_btn)
                tvAddExercise.setTextColor(resources.getColor(R.color.subheadingcolor))
                tvAddExercise.setTypeface(null, Typeface.NORMAL)
            }



        }

    }

    val selectedMuscle = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            selectedMusclesIds = intent?.getIntegerArrayListExtra("selectedPositions")!!

        }
    }
    val selectedEquipment = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            selectedEquipmentIds = intent?.getIntegerArrayListExtra("selectedPositions")!!

        }
    }
    private fun getBodyPartsList() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@ChooseExcerciseActivity,"")
        progressDialog.show()
        Log.e("APiBodyParts,",""+ApiURL.bodyparts)

        GetMethod(ApiURL.bodyparts,this).startMethod(object :
            ResponseData {

            override fun response(data: String?) {
                progressDialog.dismiss()
                musclesModelList.clear()
                Log.e("BodyPartResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("success")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                var jsonObject1 = jsonArray.optJSONObject(i)
                                var exerciseModel= MusclesModel()
                                exerciseModel.name = jsonObject1.optString("name")
                                exerciseModel.id = jsonObject1.optString("id")
                                exerciseModel.image = jsonObject1.optString("image")
                                musclesModelList.add(exerciseModel)

                            }
                            var muscleAdapter= MuscleAdapter(musclesModelList,this@ChooseExcerciseActivity)
                            recyclerMuscle.adapter=muscleAdapter




                        }else{
                            /*tvBody.visibility=View.GONE
                            bodyPartsRecyclerView.visibility=View.GONE*/
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
    private fun getWorkoutType() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this@ChooseExcerciseActivity,"")
        progressDialog.show()
        Log.e("APi,",""+ApiURL.workout_types)
        GetMethod(ApiURL.workout_types,this).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                equipmentModelList.clear()
                Log.e("TypeLevelResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayCategory=jsonObj.optJSONObject("data").optJSONArray("allcategory")

                        for (i in 0 until jsonArrayCategory.length()) {
                            var jsonObject1 = jsonArrayCategory.optJSONObject(i)
                            var workoutModel= EquipmentModel()
                            workoutModel.image = ""+jsonObject1.optString("icon")
                            workoutModel.id = ""+jsonObject1.optString("id")
                            workoutModel.name = ""+jsonObject1.optString("name")
                            equipmentModelList.add(workoutModel)
                        }
                        var equipmentAdapter= EquipmentAdapter(equipmentModelList,this@ChooseExcerciseActivity)
                        recyclerEqupment.adapter=equipmentAdapter

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