package co.com.mypt.fragments

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.WorkoutLibrary.ActiveChallengeActivity
import co.com.mypt.WorkoutLibrary.SearchWorkoutActivity
import co.com.mypt.WorkoutLibrary.WorkoutLibraryBodyPartsActivity
import co.com.mypt.adapter.AllWorkoutAdapter
import co.com.mypt.adapter.AllWorkoutTypeAdapter
import co.com.mypt.adapter.WorkoutAdapter
import co.com.mypt.adapter.WorkoutCategoryAdapter
import co.com.mypt.adapter.WorkoutFeaturedLibraryAdapter
import co.com.mypt.adapter.WorkoutLibraryDateAdapter
import co.com.mypt.adapter.WorkoutModel
import co.com.mypt.model.AllWorkoutTypeModel
import co.com.mypt.model.FeaturedWorkoutModel
import co.com.mypt.model.SearchWorkoutModel
import co.com.mypt.model.WorkoutCatModel
import co.com.mypt.model.WorkoutDateListModel
import co.com.mypt.model.WorkoutTypeModel
import co.com.mypt.utils.CircularFillView
import com.android.volley.VolleyError
import org.json.JSONObject
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar


class LibraryFragment : Fragment() {

    var datename=""
    lateinit var featuredLibraryRecycler : RecyclerView
    lateinit var workoutCategoryRecycler : RecyclerView
    lateinit var dateRecyclerView : RecyclerView
    lateinit var workoutRecyclerView : RecyclerView
    lateinit var allWorkoutRecyclerView : RecyclerView
    lateinit var WorkoutTypeRecyclerView : RecyclerView

    lateinit var circularBlueView : CircularFillView
    lateinit var circularOrangeView : CircularFillView

    lateinit var searchTv : TextView
    lateinit var completedPercentage : TextView
    lateinit var tvnoWorkout : TextView
    lateinit var tvNoFeaturedWorkout : TextView
    lateinit var imWorkout : ImageView
    lateinit var im_no_data : ImageView

    lateinit var activeChallenge : LinearLayout
    lateinit var linearSearch : LinearLayout
    var tag_id=""
    var searchWorkoutArrayList = ArrayList<SearchWorkoutModel>()

    var workoutCatArrayList = ArrayList<WorkoutCatModel>()
    var featuredWorkoutArrayList = ArrayList<FeaturedWorkoutModel>()
    var allWorkoutArrayList = ArrayList<SearchWorkoutModel>()
    var dateArrayList = ArrayList<WorkoutDateListModel>()
    var workoutArraylist = ArrayList<WorkoutModel>()
    var workouttypeArraylist = ArrayList<AllWorkoutTypeModel>()
    lateinit var sharedPreferences: SharedPreferences
    var workoutCategoryArrayList = ArrayList<WorkoutTypeModel>()
    var date_number=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_library, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        featuredLibraryRecycler = view.findViewById(R.id.featuredLibraryRecycler)
        workoutCategoryRecycler = view.findViewById(R.id.workoutCategoryRecycler)
        dateRecyclerView = view.findViewById(R.id.dateRecyclerView)
        workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView)
        WorkoutTypeRecyclerView = view.findViewById(R.id.WorkoutTypeRecyclerView)
        allWorkoutRecyclerView = view.findViewById(R.id.allWorkoutRecyclerView)
        completedPercentage = view.findViewById(R.id.completedPercentage)
        activeChallenge = view.findViewById(R.id.activeChallenge)
        linearSearch = view.findViewById(R.id.linearSearch)
        imWorkout = view.findViewById(R.id.imWorkout)
        tvNoFeaturedWorkout = view.findViewById(R.id.tvNoFeaturedWorkout)
        im_no_data = view.findViewById(R.id.im_no_data)
        tvnoWorkout = view.findViewById(R.id.tvnoWorkout)

        searchTv = view.findViewById(R.id.searchTv)
        val htmlString = "<font color=#959595>Search for </font><font color=#FAFAFA>Workout</font>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        searchTv.text = spanned


        var k = 0
        val dfs = DateFormatSymbols()
        dfs.shortMonths = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        val df = SimpleDateFormat("dd MMM, EEE")
        df.dateFormatSymbols = dfs
        val sdf = SimpleDateFormat("YYYY-MM-dd")

        for(i in 0 until 4){
            val c: Calendar = Calendar.getInstance()
            val currentDate =  df.format(c.time)
            var tempDate = sdf.format(c.time)
            val model = WorkoutDateListModel()
            when (i) {
                0 -> {
                    c.add(Calendar.DATE, -1)
                    model.workoutDate = df.format(c.time)
                    model.name = df.format(c.time)
                    model.senddate = sdf.format(c.time)
                }
                1 -> {
                    model.workoutDate = currentDate
                    model.name = "Today"
                    date_number= tempDate
                    model.senddate = tempDate
                    datename="Today"

                }
                else -> {
                    k++
                    c.add(Calendar.DATE, k)
                    model.workoutDate = df.format(c.time)
                    model.name = df.format(c.time)
                    model.senddate = sdf.format(c.time)

                }
            }
            dateArrayList.add(model)
        }
        dateRecyclerView.adapter = WorkoutLibraryDateAdapter(context,dateArrayList)

        imWorkout.setOnClickListener{
            var intent=Intent(activity, WorkoutLibraryBodyPartsActivity::class.java)
            startActivity(intent)
        }

        linearSearch.setOnClickListener{
            var intent=Intent(context,SearchWorkoutActivity::class.java)
            startActivity(intent)
        }


        circularBlueView = view.findViewById(R.id.circularBlueView)
        circularOrangeView = view.findViewById(R.id.circularOrangeView)

        circularBlueView.progressPaint.color = resources.getColor(R.color.progressBlue,null)
        circularBlueView.cornerRadius = 60f
        val animator = ObjectAnimator.ofFloat(circularBlueView, "progress", 0f, .60f)
        animator.duration = 5000 // 5 seconds animation
        animator.start()

        circularOrangeView.progressPaint.color = resources.getColor(R.color.orangecolor,null)
        circularOrangeView.cornerRadius = 40f
        val animator1 = ObjectAnimator.ofFloat(circularOrangeView, "progress", 0f, .80f)
        animator1.duration = 5000 // 5 seconds animation
        animator1.start()


        textShader(completedPercentage)

        activeChallenge.setOnClickListener {
            val intent= Intent(context, ActiveChallengeActivity::class.java)
            context?.startActivity(intent)
        }

        return view
    }
    private fun getWorkoutType() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext()!!,"")
        progressDialog.show()
        Log.e("APi,",""+ApiURL.workout_types)
        GetMethod(ApiURL.workout_types,requireContext()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                workoutCategoryArrayList.clear()

                Log.e("TypeLevelResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayCategory=jsonObj.optJSONObject("data").optJSONArray("allcategory")

                        for (i in 0 until jsonArrayCategory.length()) {
                            var jsonObject1 = jsonArrayCategory.optJSONObject(i)
                            var workoutModel= WorkoutTypeModel()
                            workoutModel.image = ""+jsonObject1.optString("icon")
                            workoutModel.id = ""+jsonObject1.optString("id")
                            workoutModel.name = ""+jsonObject1.optString("name")
                            if (i==0){
                                tag_id=jsonObject1.optString("id")
                                getWorkoutList()

                            }
                            workoutCategoryArrayList.add(workoutModel)
                        }
                        var allWorkoutTypeAdapter = AllWorkoutTypeAdapter(activity, workoutCategoryArrayList)
                        WorkoutTypeRecyclerView.adapter = allWorkoutTypeAdapter




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
    private fun getMyWorkout(date_number1: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext()!!,"")
        progressDialog.show()
        Log.e("APi,",""+ApiURL.myworkouts+date_number1)
        GetMethod(ApiURL.myworkouts+date_number1,requireContext()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                workouttypeArraylist.clear()

                Log.e("MyWorkoutResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArray=jsonObj.optJSONArray("data")
                        if (jsonArray.length()>0){

                            for (i in 0 until jsonArray.length()) {
                                var jsonObject1 = jsonArray.optJSONObject(i)
                                val workoutModel = AllWorkoutTypeModel()
                                workoutModel.category = ""+jsonObject1.optString("category")
                                workoutModel.id = ""+jsonObject1.optString("id")
                                workoutModel.assigned_id = ""+jsonObject1.optString("assigned_id")
                                workoutModel.title = ""+jsonObject1.optString("title")
                                workoutModel.percentage = ""+jsonObject1.optString("percentage")
                                workoutModel.isCompleted =jsonObject1.optBoolean("isCompleted")
                                workoutModel.previewImage = ""+jsonObject1.optString("previewImage")
                                workoutModel.status = ""+jsonObject1.optString("status")
                                workoutModel.pt_score = ""+jsonObject1.optString("pt_score")
                                workouttypeArraylist.add(workoutModel)
                            }

                            workoutRecyclerView.adapter = WorkoutAdapter(context,workouttypeArraylist,datename,"library")
                            workoutRecyclerView.visibility=View.VISIBLE
                            tvnoWorkout.visibility=View.GONE
                        }
                      else{
                            workoutRecyclerView.visibility=View.GONE
                            tvnoWorkout.visibility=View.VISIBLE
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

    private fun getCategoriesAndFeatureData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()

        GetMethod(ApiURL.workouts,context).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                featuredWorkoutArrayList.clear()
                workoutCatArrayList.clear()
                Log.e("WorkoutsResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        var jsonArrayCategory=jsonObj.optJSONObject("data").optJSONArray("categories")
                        var jsonArrayFeatured=jsonObj.optJSONObject("data").optJSONArray("featured")
                       /* if (!sharedPreferences.getString("token", "").equals("")){
                            var jsonArraymyworkouts=jsonObj.optJSONObject("data").optJSONArray("myworkouts")
                            var jsonArraychallanges=jsonObj.optJSONObject("data").optJSONArray("challanges")
                        }
*/
                        for(i in 0 until jsonArrayCategory.length()){
                            var jsonObject1 = jsonArrayCategory.optJSONObject(i)
                            val model = WorkoutCatModel()
                            model.image  = jsonObject1.optString("image")
                            model.name = jsonObject1.optString("name")
                            model.id = jsonObject1.optString("id")
                            workoutCatArrayList.add(model)
                        }
                        workoutCategoryRecycler.adapter = WorkoutCategoryAdapter(context,workoutCatArrayList)

                        for (i in 0 until jsonArrayFeatured.length()){
                            val featuremodel = FeaturedWorkoutModel()
                            var jsonObjectfeature = jsonArrayFeatured.optJSONObject(i)

                            featuremodel.image =jsonObjectfeature.optString("image")
                            featuremodel.id = jsonObjectfeature.optString("id")
                            featuremodel.time = jsonObjectfeature.optString("duration")
                            featuremodel.calorie =jsonObjectfeature.optString("calories")
                            featuremodel.totalWorkout = jsonObjectfeature.optString("series_name")
                            featuremodel.title = jsonObjectfeature.optString("title")
                            featuremodel.description = jsonObjectfeature.optString("description")
                            featuremodel.isFavourite = jsonObjectfeature.optString("isFavourite")
                            featuremodel.level = jsonObjectfeature.optString("level")
                            featuremodel.category = jsonObjectfeature.optString("category")
                            featuredWorkoutArrayList.add(featuremodel)
                        }
                        featuredLibraryRecycler.adapter =   WorkoutFeaturedLibraryAdapter(context,featuredWorkoutArrayList,sharedPreferences)

                        if(featuredWorkoutArrayList.isEmpty()){
                            tvNoFeaturedWorkout.visibility = View.VISIBLE
                            featuredLibraryRecycler.visibility = View.GONE
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
    fun getWorkoutList()  {
        val param: MutableMap<String, String> = HashMap()
        param["type"] = tag_id
        param["page"] = ""
        param["muscle_id"] =""
        param["level"] = ""
        param["calories"] =""
        param["duration"] =""


        Log.e("WorkoutListParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()

        PostMethod(ApiURL.getworkouts,param, requireContext()).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                allWorkoutArrayList.clear()
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
                                model.level = jsonObject.optString("level")
                                model.isFavourite = jsonObject.optString("isFeatured")

                                allWorkoutArrayList.add(model)
                            }
                            allWorkoutRecyclerView.adapter = AllWorkoutAdapter(activity,allWorkoutArrayList)
                            allWorkoutRecyclerView.visibility=View.VISIBLE
                            im_no_data.visibility=View.GONE

                        }else{
                            allWorkoutRecyclerView.visibility=View.GONE
                            im_no_data.visibility=View.VISIBLE


                        }

                    }else{

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

    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF")
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.shader = textShader
    }
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(ExerciseTagList, IntentFilter("tagExercise"), RECEIVER_EXPORTED)
            requireContext().registerReceiver(getDatereceiver, IntentFilter("sendDate"), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            requireContext().registerReceiver(ExerciseTagList, IntentFilter("tagExercise"))

            requireContext().registerReceiver(getDatereceiver, IntentFilter("sendDate"))
        }
        getCategoriesAndFeatureData()
        getWorkoutType()
        getMyWorkout(date_number)
    }

    var ExerciseTagList: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            try {
                tag_id= intent.getStringExtra("tag_id")!!
                getWorkoutList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    var getDatereceiver: BroadcastReceiver = object : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            try {
                date_number= intent.getStringExtra("date")!!
                datename= intent.getStringExtra("name")!!
                getMyWorkout(date_number)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}