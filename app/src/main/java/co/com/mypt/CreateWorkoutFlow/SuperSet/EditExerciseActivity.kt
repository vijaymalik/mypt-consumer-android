package co.com.mypt.CreateWorkoutFlow.SuperSet

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.SelectExcerciseModel
import co.com.mypt.model.SuperSetExerciseModel
import co.com.mypt.utils.QuarterCircleProgressBar
import co.com.mypt.utils.RulerView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import org.json.JSONObject

class EditExerciseActivity : AppCompatActivity() {
    var rulervalue=0
    lateinit var txt_exercise_name: TextView
    lateinit var tvDuration: TextView
    lateinit var linearReps: LinearLayout
    lateinit var linear_sets: LinearLayout
    lateinit var linear_no_of_sets: LinearLayout
    lateinit var duration_circuit_ll_circuit: LinearLayout
    lateinit var duration_circuit_ll: LinearLayout
    lateinit var linearRest: RelativeLayout
    lateinit var linear_circuit: LinearLayout
    lateinit var CardSave: CardView
    lateinit var txt_calorie: TextView
    lateinit var tvRepsExerciseCircuit: TextView
    lateinit var tvDurationCircuit: TextView
    lateinit var tvCaloriesCircuit: TextView
    lateinit var txt_no_of_sets: TextView
    lateinit var txt_no_of_reps: TextView
    lateinit var txt_no_of_reps_circuit: TextView
    lateinit var tvRepsExerciseName: TextView
    lateinit var img: ImageView
    lateinit var img_minus_sets: ImageView
    lateinit var back_1: ImageView
    lateinit var img_add_sets: ImageView
    lateinit var img_minus_reps: ImageView
    lateinit var img_add_reps: ImageView
    lateinit var img_minus_reps_circuit: ImageView
    lateinit var img_add_reps_circuit: ImageView
    lateinit var rulerviewbottom: RulerView
    lateinit var selectExerciseModel:SelectExcerciseModel
    private var count: Int = 3
    private var count1: Int = 12
    private var countcircuit: Int = 12
    lateinit var quarterCircularProgressBar: QuarterCircleProgressBar
    lateinit var quarterCaloriesProgressBar: QuarterCircleProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_exercise2)

        duration_circuit_ll_circuit=findViewById(R.id.duration_circuit_ll_circuit)
        duration_circuit_ll=findViewById(R.id.duration_circuit_ll)
        tvDurationCircuit=findViewById(R.id.tvDurationCircuit)
        tvCaloriesCircuit=findViewById(R.id.tvCaloriesCircuit)
        txt_exercise_name=findViewById(R.id.txt_exercise_name)
        img=findViewById(R.id.img)
        back_1=findViewById(R.id.back_1)
        tvDuration=findViewById(R.id.tvDuration)
        txt_calorie=findViewById(R.id.txt_calorie)
        txt_no_of_sets=findViewById(R.id.txt_no_of_sets)
        img_minus_sets=findViewById(R.id.img_minus_sets)
        img_add_sets=findViewById(R.id.img_add_sets)
        rulerviewbottom=findViewById(R.id.rulerviewbottom)
        img_minus_reps=findViewById(R.id.img_minus_reps)
        img_add_reps=findViewById(R.id.img_add_reps)
        txt_no_of_reps=findViewById(R.id.txt_no_of_reps)
        CardSave=findViewById(R.id.CardSave)
        linearReps=findViewById(R.id.linearReps)
        linear_sets=findViewById(R.id.linear_sets)
        linearRest=findViewById(R.id.linearRest)
        linear_no_of_sets=findViewById(R.id.linear_no_of_sets)
        txt_no_of_reps_circuit=findViewById(R.id.txt_no_of_reps_circuit)
        tvRepsExerciseCircuit=findViewById(R.id.tvRepsExerciseCircuit)
        linear_circuit=findViewById(R.id.linear_circuit)
        img_minus_reps_circuit=findViewById(R.id.img_minus_reps_circuit)
        img_add_reps_circuit=findViewById(R.id.img_add_reps_circuit)
        quarterCaloriesProgressBar=findViewById(R.id.quarterCaloriesProgressBar)
        quarterCircularProgressBar=findViewById(R.id.quarterDurationProgressBar)

        textShader(tvCaloriesCircuit)
        textShader(tvDurationCircuit)
        textShader(tvDuration)
        textShader(txt_calorie)

        back_1.setOnClickListener {
            finish()
        }

        if (intent.getStringExtra("workoutType").equals("Createdsuperset")){
            var superSetModel:SuperSetExerciseModel = intent.getParcelableExtra("item")!!
            Glide.with(applicationContext!!).load(superSetModel.image).fitCenter().into(img)
            tvDuration.text = superSetModel.duration+"s"
            txt_calorie.text = superSetModel.calories
            txt_no_of_sets.text = superSetModel.sets
            txt_exercise_name.text = superSetModel.name

            tvRepsExerciseCircuit.text = "Number of reps for "+superSetModel.name
            txt_no_of_reps.text = superSetModel.reps
            txt_no_of_reps_circuit.text = superSetModel.reps
            rulervalue=Integer.parseInt(superSetModel.rest_duration)
            rulerviewbottom.setInitialPosition(rulervalue)
            count=superSetModel.sets.toInt()
            count1=superSetModel.reps.toInt()

        }else if (intent.getStringExtra("workoutType").equals("superset") || intent.getStringExtra("workoutType").equals("regular")
            || intent.getStringExtra("workoutType").equals("circuit")){
            selectExerciseModel = intent.getParcelableExtra("item")!!
            Glide.with(applicationContext!!).load(selectExerciseModel.image).fitCenter().into(img)
            tvDurationCircuit.text = selectExerciseModel.duration+"s"
            tvDuration.text = selectExerciseModel.duration+"s"
            tvCaloriesCircuit.text = selectExerciseModel.calories
            txt_calorie.text = selectExerciseModel.calories
            txt_no_of_sets.text = selectExerciseModel.sets
            txt_exercise_name.text = selectExerciseModel.name

            quarterCaloriesProgressBar.setMaxValue(selectExerciseModel.calories.toFloat()+200f)
            quarterCaloriesProgressBar.setProgressWithAnimation(selectExerciseModel.calories.toFloat())
            quarterCircularProgressBar.setProgressWithAnimation(selectExerciseModel.duration.toFloat())

            tvRepsExerciseCircuit.text = "Number of reps for "+selectExerciseModel.name
            txt_no_of_reps.text = selectExerciseModel.raps
            txt_no_of_reps_circuit.text = selectExerciseModel.raps
            rulervalue=Integer.parseInt(selectExerciseModel.rest_duration)
            rulerviewbottom.setInitialPosition(rulervalue)
            count=selectExerciseModel.sets.toInt()
            count1=selectExerciseModel.raps.toInt()
            countcircuit=selectExerciseModel.raps.toInt()
        }


        if (intent.getStringExtra("workoutType").equals("superset") || intent.getStringExtra("workoutType").equals("Createdsuperset")){
            linearRest.visibility=View.VISIBLE
            linearReps.visibility=View.VISIBLE
            linear_no_of_sets.visibility=View.GONE
            linear_circuit.visibility=View.GONE
            duration_circuit_ll_circuit.visibility=View.GONE
            duration_circuit_ll.visibility=View.VISIBLE
        }else if(intent.getStringExtra("workoutType").equals("regular")){
            linearRest.visibility=View.VISIBLE
            linearReps.visibility=View.VISIBLE
            linear_no_of_sets.visibility=View.VISIBLE
            linear_circuit.visibility=View.GONE
            duration_circuit_ll_circuit.visibility=View.GONE
            duration_circuit_ll.visibility=View.VISIBLE
        }else{
            linear_sets.visibility=View.GONE
            linear_circuit.visibility=View.VISIBLE
            duration_circuit_ll_circuit.visibility=View.VISIBLE
            duration_circuit_ll.visibility=View.GONE
        }


        rulerviewbottom.onValueChangeListener = { value ->
            // React to centered value changes here
            println("Centered value: $value")
            rulervalue=value
            /* if (switchCompat.isChecked==false && excerxiseModelList.size>0){
                 linearActivityRest.visibility=View.VISIBLE
             }*/

        }

        img_add_sets.setOnClickListener { // Or findViewById(R.id.plusButton)
            count++
            updateQuantityDisplay()
        }
        // Set click listener for minus button
        img_minus_sets.setOnClickListener { // Or findViewById(R.id.minusButton)
            if (count > 1) { // Optional: Prevent negative values
                count--
                updateQuantityDisplay()
            }
        }
        img_minus_reps.setOnClickListener { // Or findViewById(R.id.minusButton)
            if (count1 > 1) { // Optional: Prevent negative values
                count1--
                updateRapsDisplay()
            }
        }
        img_add_reps.setOnClickListener { // Or findViewById(R.id.minusButton)
            count1++
            updateRapsDisplay()
        }
        img_minus_reps_circuit.setOnClickListener { // Or findViewById(R.id.minusButton)
            if (count1 > 1) { // Optional: Prevent negative values
                count1--
                updateRapsCircuit()
            }
        }
        img_add_reps_circuit.setOnClickListener { // Or findViewById(R.id.minusButton)
            count1++
            updateRapsCircuit()
        }
        CardSave.setOnClickListener {
            if (intent.getStringExtra("workoutType").equals("superset") ||intent.getStringExtra("workoutType").equals("regular")
                ||intent.getStringExtra("workoutType").equals("circuit")){
                val updated = selectExerciseModel.copy(
                    sets = txt_no_of_sets.text.toString(),
                    rest_duration = ""+rulervalue,
                    raps = ""+count1
                )
                val intent = Intent()
                intent.putExtra("updatedItem", updated)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else if (intent.getStringExtra("workoutType").equals("Createdsuperset")){
                sendEditData()
            }

        }
    }
    private fun sendEditData(
    ) {
        val param: MutableMap<String, String> = HashMap()
        param["id"] =""+intent.getStringExtra("workout_id")
        param["exercise_id"] = ""+intent.getStringExtra("exercise_id")
        param["reps"] = txt_no_of_reps.text.toString()
        param["sets"] = ""
        param["rest"] =""+rulervalue
        param["note"] =""
        param["sets_position"] =""+intent.getStringExtra("setposition")
        Log.e("SuperSetEditExerciseParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.editworkoutexercise,param, applicationContext).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("EditSuperSetExerciseRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        Toast.makeText(this@EditExerciseActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
                        var intent= Intent("updateSuperSet")
                        sendBroadcast(intent)
                        finish()

                    }else{
                       Toast.makeText(this@EditExerciseActivity,resp.optString("msg"),Toast.LENGTH_SHORT).show()
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
    private fun updateQuantityDisplay() {
        txt_no_of_sets.text = count.toString() // Or findViewById(R.id.quantityTextView).text = count.toString()
    }
    private fun updateRapsDisplay() {
        txt_no_of_reps.text = count1.toString() // Or findViewById(R.id.quantityTextView).text = count.toString()
    }
    private fun updateRapsCircuit() {
        txt_no_of_reps_circuit.text = count1.toString() // Or findViewById(R.id.quantityTextView).text = count.toString()
    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.shader = textShader
    }
}