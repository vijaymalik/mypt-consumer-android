package co.com.mypt.WorkoutLibrary


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import co.com.mypt.R
import com.google.android.material.button.MaterialButton

class WorkoutLibraryBodyPartsActivity : AppCompatActivity() {

    private lateinit var btn_neck: RelativeLayout
    private lateinit var neck_img: ImageView
    private lateinit var btn_neck_withtext: LinearLayout
    private lateinit var btn_neck_withouttext: RelativeLayout
    private lateinit var btnObliques: RelativeLayout
    private lateinit var ObliquesImg: ImageView
    private lateinit var btnObliquesWithText: LinearLayout
    private lateinit var btnObliquesWithoutText: RelativeLayout
    private lateinit var btnHipFlexor: RelativeLayout
    private lateinit var HipFlexorImg: ImageView
    private lateinit var btnHipFlexorWithText: LinearLayout
    private lateinit var btnHipFlexorWithoutText: RelativeLayout
    private lateinit var btnRearDelts: RelativeLayout
    private lateinit var RearDeltsImg: ImageView
    private lateinit var btnRearDeltsWithText: LinearLayout
    private lateinit var btnRearDeltsWithoutText: RelativeLayout
    private lateinit var btnSpine: RelativeLayout
    private lateinit var spineImg: ImageView
    private lateinit var btnSpineWithText: LinearLayout
    private lateinit var btnSpineWithoutText: RelativeLayout
    private lateinit var btnGlutes: RelativeLayout
    private lateinit var glutesImg: ImageView
    private lateinit var btnGlutesWithText: LinearLayout
    private lateinit var btnGlutesWithoutText: RelativeLayout
    private lateinit var btnHamstring: RelativeLayout
    private lateinit var HamstringImg: ImageView
    private lateinit var btnHamstringWithText: LinearLayout
    private lateinit var btnHamstringWithoutText: RelativeLayout
    private lateinit var turnaroundBtn: RelativeLayout
    private var frontBodyCheck = false

    private lateinit var frontBody: ImageView
    private lateinit var backBody: ImageView
    private lateinit var backImg: ImageView
    private lateinit var tricepsImg: ImageView
    private lateinit var forearmImg: ImageView
    private lateinit var bicepsImg: ImageView
    private lateinit var shoulderImg: ImageView
    private lateinit var chestImg: ImageView
    private lateinit var absImg: ImageView
    private lateinit var thighsImg: ImageView
    private lateinit var calvesImg: ImageView

    private lateinit var btnBack: RelativeLayout
    private lateinit var btnBackWithoutText: RelativeLayout
    private lateinit var btnTricepsWithoutText: RelativeLayout
    private lateinit var btnTriceps: RelativeLayout
    private lateinit var btnForearm: RelativeLayout
    private lateinit var btnBiceps: RelativeLayout
    private lateinit var btnChestWithoutText: RelativeLayout
    private lateinit var btnAbsWithoutText: RelativeLayout
    private lateinit var btnThighWithoutText: RelativeLayout
    private lateinit var btnCalvesWithoutText: RelativeLayout
    private lateinit var btnForearmWithoutText: RelativeLayout
    private lateinit var btnBicepsWithoutText: RelativeLayout

    private lateinit var btnShoulderWithoutText: RelativeLayout
    private lateinit var btnShoulder: RelativeLayout
    private lateinit var btnChest: RelativeLayout
    private lateinit var btnAbs: RelativeLayout
    private lateinit var btnThigh: RelativeLayout
    private lateinit var btnCalves: RelativeLayout

    private lateinit var btnShoulderWithText: LinearLayout
    private lateinit var btnBicepsWithText: LinearLayout
    private lateinit var btnForearmWithText: LinearLayout
    private lateinit var btnChestWithText: LinearLayout
    private lateinit var btnAbsWithText: LinearLayout
    private lateinit var btnThighWithText: LinearLayout
    private lateinit var btnCalvesWithText: LinearLayout

    private lateinit var frontBodyContainer: ConstraintLayout
    private lateinit var backBodyContainer: ConstraintLayout

    private lateinit var btnBackWithText: LinearLayout
    private lateinit var btnTricepsWithText: LinearLayout
    private lateinit var linearHeader: LinearLayout
    private lateinit var viewworkout: MaterialButton
    var body_type=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_workout_library_body_parts)
        linearHeader = findViewById(R.id.linearHeader)
        turnaroundBtn = findViewById(R.id.turnaround_btn)
        frontBody = findViewById(R.id.frontbody_img)
        backBody = findViewById(R.id.backbody_img)
        frontBodyContainer = findViewById(R.id.frontbody_container)
        backBodyContainer = findViewById(R.id.backbody_container)

        chestImg = findViewById(R.id.chest_img)
        viewworkout = findViewById(R.id.viewworkout)
        btnChest = findViewById(R.id.btn_chest)
        btnChestWithText = findViewById(R.id.btn_chest_withtext)
        btnChestWithoutText = findViewById(R.id.btn_chest_withouttext)

        btn_neck = findViewById(R.id.btn_neck)
        neck_img = findViewById(R.id.neck_img)
        btn_neck_withtext = findViewById(R.id.btn_neck_withtext)
        btn_neck_withouttext = findViewById(R.id.btn_neck_withouttext)

        btnCalves = findViewById(R.id.btn_calves)
        calvesImg = findViewById(R.id.calves_img)
        btnCalvesWithText = findViewById(R.id.btn_calves_withtext)
        btnCalvesWithoutText = findViewById(R.id.btn_calves_withouttext)

        btnObliques = findViewById(R.id.btn_obliques)
        ObliquesImg = findViewById(R.id.obliques_img)
        btnObliquesWithText = findViewById(R.id.btn_obliques_withtext)
        btnObliquesWithoutText = findViewById(R.id.btn_obliques_withouttext)

        btnHipFlexor = findViewById(R.id.btn_hip_flexor)
        HipFlexorImg = findViewById(R.id.hip_flexor_img)
        btnHipFlexorWithText = findViewById(R.id.btn_hip_flexor_withtext)
        btnHipFlexorWithoutText = findViewById(R.id.btn_hip_flexor_withouttext)

        btnRearDelts = findViewById(R.id.btn_rear_delts)
        RearDeltsImg = findViewById(R.id.rear_delts_img)
        btnRearDeltsWithText = findViewById(R.id.btn_rear_delts_withtext)
        btnRearDeltsWithoutText = findViewById(R.id.btn_rear_delts_withouttext)

        btnHamstring = findViewById(R.id.btn_hamstring)
        HamstringImg = findViewById(R.id.hamstring_img)
        btnHamstringWithText = findViewById(R.id.btn_hamstring_withtext)
        btnHamstringWithoutText = findViewById(R.id.btn_hamstring_withouttext)

        btnShoulder = findViewById(R.id.btn_shoulder)
        btnShoulderWithText = findViewById(R.id.btn_shoulder_withtext)
        btnShoulderWithoutText = findViewById(R.id.btn_shoulder_withouttext)
        shoulderImg = findViewById(R.id.sholder_img)

        btnThigh = findViewById(R.id.btn_thigh)
        btnThighWithText = findViewById(R.id.btn_thigh_withtext)
        btnThighWithoutText = findViewById(R.id.btn_thigh_withouttext)
        thighsImg = findViewById(R.id.thighs_img)

        btnAbs = findViewById(R.id.btn_abs)
        btnAbsWithText = findViewById(R.id.btn_abs_withtext)
        btnAbsWithoutText = findViewById(R.id.btn_abs_withouttext)
        absImg = findViewById(R.id.abs_img)

        btnBack = findViewById(R.id.btn_back)
        btnBackWithText = findViewById(R.id.btn_back_withtext)
        btnBackWithoutText = findViewById(R.id.btn_back_withouttext)
        backImg = findViewById(R.id.back_img)

        btnSpine = findViewById(R.id.btn_spine)
        btnSpineWithText = findViewById(R.id.btn_spine_withtext)
        btnSpineWithoutText = findViewById(R.id.btn_spine_withouttext)
        spineImg = findViewById(R.id.spine_img)

        btnGlutes = findViewById(R.id.btn_glutes)
        btnGlutesWithText = findViewById(R.id.btn_glutes_withtext)
        btnGlutesWithoutText = findViewById(R.id.btn_glutes_withouttext)
        glutesImg = findViewById(R.id.glutes_img)

        btnBiceps = findViewById(R.id.btn_biceps)
        btnBicepsWithText = findViewById(R.id.btn_biceps_withtext)
        btnBicepsWithoutText = findViewById(R.id.btn_biceps_withouttext)
        bicepsImg = findViewById(R.id.biceps_img)

        btnForearm = findViewById(R.id.btn_forearm)
        btnForearmWithText = findViewById(R.id.btn_forearm_withtext)
        btnForearmWithoutText = findViewById(R.id.btn_forearm_withouttext)
        forearmImg = findViewById(R.id.forearm_img)

        btnTriceps = findViewById(R.id.btn_triceps)
        btnTricepsWithText = findViewById(R.id.btn_triceps_withtext)
        btnTricepsWithoutText = findViewById(R.id.btn_triceps_withouttext)
        tricepsImg = findViewById(R.id.triceps_img)

        linearHeader.setOnClickListener {
            finish()
        }
        turnaroundBtn.setOnClickListener {
            if (!frontBodyCheck) {
                frontBodyCheck = true
                frontBodyContainer.visibility = View.GONE
                backBodyContainer.visibility = View.VISIBLE
            } else {
                frontBodyCheck = false
                frontBodyContainer.visibility = View.VISIBLE
                backBodyContainer.visibility = View.GONE
            }
        }

        viewworkout.setOnClickListener {
            if (body_type.equals("")){
                Toast.makeText(applicationContext,"Select a body part to view workouts", Toast.LENGTH_SHORT).show()
            }else{
                var intent= Intent(applicationContext, CategoryWiseWorkoutActivity::class.java)
                intent.putExtra("selectedBodyPart",body_type)
                intent.putExtra("selectedScreen","bodyFigure")
                startActivity(intent)
            }

        }
        btnBack.setOnClickListener {
            body_type="back"
            showBackBodyPart("back")
        }
        btnSpine.setOnClickListener {
            body_type="spine"
            showBackBodyPart("spine")
        }
        btnHamstring.setOnClickListener {
            body_type="hamstring"
            showBackBodyPart("hamstring")
        }
        btnGlutes.setOnClickListener {
            body_type="glutes"
            showBackBodyPart("glutes")
        }
        btnTriceps.setOnClickListener {
            body_type="triceps"
            showBackBodyPart("triceps")
        }
        btnObliques.setOnClickListener {
            body_type="obliques"
            showBodyPart("obliques")
        }

        btnForearm.setOnClickListener {
            body_type="forearm"
            showBodyPart("forearm")
        }
        btnHipFlexor.setOnClickListener {
            body_type="hip flexor"
            showBodyPart("hip_flexor")
        }
        btnRearDelts.setOnClickListener {
            body_type="rear delts"
            showBackBodyPart("rear_delts")
        }

        btnBiceps.setOnClickListener {
            body_type="biceps"
            showBodyPart("biceps")
        }

        btnShoulder.setOnClickListener {
            body_type="shoulder"
            showBodyPart("shoulder")
        }
        btnChest.setOnClickListener {
            body_type="chest"
            showBodyPart("chest")
        }
        btnAbs.setOnClickListener {
            body_type="abs"
            showBodyPart("abs")
        }
        btnThigh.setOnClickListener {
            body_type="quadriceps"
            showBodyPart("thigh")
        }
        btnCalves.setOnClickListener {
            body_type="calves"
            showBodyPart("calves")
        }
        btn_neck.setOnClickListener {
            body_type = "neck"
            showBodyPart("neck")
        }

    }

    private fun showBackBodyPart(part: String) {
        backBody.visibility = View.VISIBLE
        backImg.visibility = View.GONE
        tricepsImg.visibility = View.GONE
        RearDeltsImg.visibility = View.GONE
        spineImg.visibility = View.GONE
        glutesImg.visibility = View.GONE
        HamstringImg.visibility = View.GONE

        btnHamstringWithText.visibility = View.GONE
        btnHamstringWithoutText.visibility = View.VISIBLE
        btnBackWithText.visibility = View.GONE
        btnBackWithoutText.visibility = View.VISIBLE
        btnTricepsWithText.visibility = View.GONE
        btnTricepsWithoutText.visibility = View.VISIBLE
        btnRearDeltsWithText.visibility = View.GONE
        btnRearDeltsWithoutText.visibility = View.VISIBLE
        btnSpineWithText.visibility = View.GONE
        btnSpineWithoutText.visibility = View.VISIBLE
        btnGlutesWithText.visibility = View.GONE
        btnGlutesWithoutText.visibility = View.VISIBLE


        when (part) {
            "back" -> {
                backImg.visibility = View.VISIBLE
                btnBackWithText.visibility = View.VISIBLE
                btnBackWithoutText.visibility = View.GONE

                hideFrontParts()
            }
            "spine" -> {
                spineImg.visibility = View.VISIBLE
                btnSpineWithText.visibility = View.VISIBLE
                btnSpineWithoutText.visibility = View.GONE
                hideFrontParts()
            }
            "hamstring" -> {
                HamstringImg.visibility = View.VISIBLE
                btnHamstringWithText.visibility = View.VISIBLE
                btnHamstringWithoutText.visibility = View.GONE
                hideFrontParts()
            }
            "glutes" -> {
                glutesImg.visibility = View.VISIBLE
                btnGlutesWithText.visibility = View.VISIBLE
                btnGlutesWithoutText.visibility = View.GONE
                hideFrontParts()
            }

            "rear_delts" -> {
                RearDeltsImg.visibility = View.VISIBLE
                btnRearDeltsWithText.visibility = View.VISIBLE
                btnRearDeltsWithoutText.visibility = View.GONE
                hideFrontParts()
            }

            "triceps" -> {
                tricepsImg.visibility = View.VISIBLE
                btnTricepsWithText.visibility = View.VISIBLE
                btnTricepsWithoutText.visibility = View.GONE

                hideFrontParts()
            }
        }
    }

    private fun showBodyPart(part: String) {
        frontBody.visibility = View.VISIBLE

        bicepsImg.visibility = View.GONE
        forearmImg.visibility = View.GONE
        shoulderImg.visibility = View.GONE
        chestImg.visibility = View.GONE
        absImg.visibility = View.GONE
        thighsImg.visibility = View.GONE
        calvesImg.visibility = View.GONE
        neck_img.visibility = View.GONE
        ObliquesImg.visibility = View.GONE
        HipFlexorImg.visibility = View.GONE

        btnBicepsWithText.visibility = View.GONE
        btnBicepsWithoutText.visibility = View.VISIBLE
        btnForearmWithText.visibility = View.GONE
        btnForearmWithoutText.visibility = View.VISIBLE
        btnShoulderWithText.visibility = View.GONE
        btnShoulderWithoutText.visibility = View.VISIBLE
        btnChestWithText.visibility = View.GONE
        btnChestWithoutText.visibility = View.VISIBLE
        btnAbsWithText.visibility = View.GONE
        btnAbsWithoutText.visibility = View.VISIBLE
        btnThighWithText.visibility = View.GONE
        btnThighWithoutText.visibility = View.VISIBLE
        btnCalvesWithText.visibility = View.GONE
        btnCalvesWithoutText.visibility = View.VISIBLE
        btnObliquesWithText.visibility = View.GONE
        btnObliquesWithoutText.visibility = View.VISIBLE
        btnHipFlexorWithText.visibility = View.GONE
        btnHipFlexorWithoutText.visibility = View.VISIBLE
        btn_neck_withtext.visibility = View.GONE
        btn_neck_withouttext.visibility = View.VISIBLE

        when (part) {
            "neck" -> {
                neck_img.visibility = View.VISIBLE
                btn_neck_withtext.visibility = View.VISIBLE
                btn_neck_withouttext.visibility = View.GONE
                resetBackParts()
            }

           "biceps" -> {
                bicepsImg.visibility = View.VISIBLE
                btnBicepsWithText.visibility = View.VISIBLE
                btnBicepsWithoutText.visibility = View.GONE
                resetBackParts()
            }

           "forearm" -> {
                forearmImg.visibility = View.VISIBLE
                btnForearmWithText.visibility = View.VISIBLE
                btnForearmWithoutText.visibility = View.GONE
                resetBackParts()
            }

           "shoulder" -> {
                shoulderImg.visibility = View.VISIBLE
                btnShoulderWithText.visibility = View.VISIBLE
                btnShoulderWithoutText.visibility = View.GONE
                resetBackParts()
            }

            "chest" -> {
                chestImg.visibility = View.VISIBLE
                btnChestWithText.visibility = View.VISIBLE
                btnChestWithoutText.visibility = View.GONE
                resetBackParts()
            }
            "obliques" -> {
                ObliquesImg.visibility = View.VISIBLE
                btnObliquesWithText.visibility = View.VISIBLE
                btnObliquesWithoutText.visibility = View.GONE
                resetBackParts()
            }
            "hip_flexor" -> {
                HipFlexorImg.visibility = View.VISIBLE
                btnHipFlexorWithText.visibility = View.VISIBLE
                btnHipFlexorWithoutText.visibility = View.GONE
                resetBackParts()
            }

            "abs" -> {
                absImg.visibility = View.VISIBLE
                btnAbsWithText.visibility = View.VISIBLE
                btnAbsWithoutText.visibility = View.GONE
                resetBackParts()
            }

            "thigh" -> {
                thighsImg.visibility = View.VISIBLE
                btnThighWithText.visibility = View.VISIBLE
                btnThighWithoutText.visibility = View.GONE
                resetBackParts()
            }

            "calves" -> {
                calvesImg.visibility = View.VISIBLE
                btnCalvesWithText.visibility = View.VISIBLE
                btnCalvesWithoutText.visibility = View.GONE
                resetBackParts()
            }
        }
    }

    private fun hideFrontParts() {
        shoulderImg.visibility = View.GONE
        chestImg.visibility = View.GONE
        absImg.visibility = View.GONE
        thighsImg.visibility = View.GONE
        calvesImg.visibility = View.GONE

        btnShoulderWithText.visibility = View.GONE
        btnShoulderWithoutText.visibility = View.VISIBLE
        btnChestWithText.visibility = View.GONE
        btnChestWithoutText.visibility = View.VISIBLE
        btnAbsWithText.visibility = View.GONE
        btnAbsWithoutText.visibility = View.VISIBLE
        btnThighWithText.visibility = View.GONE
        btnThighWithoutText.visibility = View.VISIBLE
        btnCalvesWithText.visibility = View.GONE
        btnCalvesWithoutText.visibility = View.VISIBLE
    }

    private fun resetBackParts() {
        backImg.visibility = View.GONE
        tricepsImg.visibility = View.GONE
        btnBackWithText.visibility = View.GONE
        btnBackWithoutText.visibility = View.VISIBLE
        btnTricepsWithText.visibility = View.GONE
        btnTricepsWithoutText.visibility = View.VISIBLE
    }
    }
