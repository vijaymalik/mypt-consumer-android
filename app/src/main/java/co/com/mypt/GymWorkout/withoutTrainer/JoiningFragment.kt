package co.com.mypt.GymWorkout.withoutTrainer

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.CreatePackagectivity
import co.com.mypt.adapter.JoinAdapter
import co.com.mypt.model.JoinModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject


class JoiningFragment(
    var trainer_id: String?,
    var studio_id: String?,
   val selectedPosition:Int
) : Fragment() {
    var checktype="male"
    var selectType=""
    var member_id=""
    var member_name=""
    var member_age=""
    var buttonclick=""
    var member_gender=""
    lateinit var Recycler:RecyclerView
    lateinit var linearAddMember:LinearLayout
    lateinit var tvPerson:TextView
    lateinit var joinAdapter:JoinAdapter
    var joinList = ArrayList<JoinModel>()
    lateinit var standard_bottom_sheet:LinearLayout
    lateinit var addMemberBottomSheetDialog:BottomSheetDialog
    lateinit var sharedPreferences: SharedPreferences
    var maxvalue=0
   lateinit var context1:Context
    lateinit var bottomSheet:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_joining, container, false)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(requireContext())

        Recycler=view.findViewById(R.id.Recycler)
        tvPerson=view.findViewById(R.id.tvPerson)
        linearAddMember=view.findViewById(R.id.linearAddMember)
        addMemberBottomSheetDialog = BottomSheetDialog(context1, R.style.CustomBottomSheetDialogTheme)
        bottomSheet = layoutInflater.inflate(R.layout.addmember_bottomsheet, null)



        linearAddMember.setOnClickListener{
            selectType="add"
            addmembeBottomsheet(context1)

        }
        return view
    }


    fun addmembeBottomsheet(context: Context?) {
        standard_bottom_sheet = bottomSheet.findViewById(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        var checkmale =bottomSheet.findViewById<CheckBox>(R.id.checkmale)
        var tvname =bottomSheet.findViewById<TextInputEditText>(R.id.tvname)
        var imClose =bottomSheet.findViewById<ImageView>(R.id.imClose)
        var tvAge =bottomSheet.findViewById<TextInputEditText>(R.id.tvAge)
        var tvSave =bottomSheet.findViewById<TextView>(R.id.tvSave)
        var tvSaveAndNext =bottomSheet.findViewById<TextView>(R.id.tvSaveAndNext)
        var checkfemale =bottomSheet.findViewById<CheckBox>(R.id.checkfemale)
        var checkOther =bottomSheet.findViewById<CheckBox>(R.id.checkOther)



        if (selectType.equals("edit")){
            tvname.setText(member_name)
            tvAge.setText(member_age)
            if (checktype.equals("male"))
            {
                checktype="male"
                checkmale.isChecked=true
                checkfemale.isChecked=false
                checkOther.isChecked=false
            }else if (checktype.equals("female")){
                checktype="female"
                checkfemale.isChecked=true
                checkmale.isChecked=false
                checkOther.isChecked=false
            }else{
                checktype="others"
                checkOther.isChecked=true
                checkfemale.isChecked=false
                checkmale.isChecked=false
            }

            }
        else{
            tvname.setText("")
            tvAge.setText("")
            checktype="male"

        }

        checkmale.setOnClickListener{
            checktype="male"
            checkmale.isChecked=true
            checkfemale.isChecked=false
            checkOther.isChecked=false
            Log.e("checkType",""+checktype)
        }
        checkfemale.setOnClickListener{
            checktype="female"
            checkfemale.isChecked=true
            checkmale.isChecked=false
            checkOther.isChecked=false
            Log.e("checkType",""+checktype)

        }
        checkOther.setOnClickListener{
            checktype="others"
            checkOther.isChecked=true
            checkfemale.isChecked=false
            checkmale.isChecked=false
            Log.e("checkType",""+checktype)

        }
        imClose.setOnClickListener{
            addMemberBottomSheetDialog.dismiss()
        }

        tvname.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                checkvalue(tvname,tvAge,tvSave,tvSaveAndNext)

            }


        })
        tvAge.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                if(s.isNotEmpty() && s.toString().toInt()>150){
                    tvAge.setError("Please enter a valid age (1-150)")
//                    tvSave!!.setBackgroundResource(R.drawable.grey_rectangle_rounded)
//                    tvSave.setTextColor(context1.resources.getColor(R.color.white))
                    tvSave.setOnClickListener { /* Do nothing */ }
                    tvSaveAndNext.setOnClickListener{
                    }
                    return
                }
                checkvalue(tvname, tvAge, tvSave, tvSaveAndNext)

            }


        })



         if (tvname!!.text.toString().trim()=="" || tvAge!!.text.toString().trim()=="") {
//            tvSave!!.setBackgroundResource(R.drawable.grey_rectangle_rounded)
//            tvSave.setTextColor(context1.resources.getColor(R.color.white))
            tvSave.setOnClickListener { /* Do nothing */ }
        } else {
            if (tvAge.text.toString().toInt() > 150) {
//                tvSave!!.setBackgroundResource(R.drawable.grey_rectangle_rounded)
//                tvSave.setTextColor(context1.resources.getColor(R.color.white))
                tvAge.setError("Please enter a valid age (1-150)")
                tvSave.setOnClickListener {

                }
            } else {
//                tvSave!!.setBackgroundResource(R.drawable.apply_rectangle)
//                tvSave.setTextColor(context1.resources.getColor(R.color.buttontextcolor))
                tvSave.setOnClickListener{
                    addMemberData(tvname.text.toString(), tvAge.text.toString(),checktype)
                    addMemberBottomSheetDialog.dismiss()
                }
            }
        }

        addMemberBottomSheetDialog.setContentView(bottomSheet)
        addMemberBottomSheetDialog.show()

        val window = addMemberBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun checkvalue(
        tvname: TextInputEditText?,
        tvAge: TextInputEditText?,
        tvSave: TextView?,
        tvSaveAndNext: TextView
    ) {
        if (tvname!!.text.toString().trim()=="" || tvAge!!.text.toString().trim()==""){
//            tvSave!!.setBackgroundResource(R.drawable.grey_rectangle_rounded)
//            tvSave?.setTextColor(context1.resources.getColor(R.color.white))
            tvSave?.setOnClickListener{}
            tvSaveAndNext.setOnClickListener{}
        }else{
//            tvSave!!.setBackgroundResource(R.drawable.apply_rectangle)
//            tvSave?.setTextColor(context1.resources.getColor(R.color.buttontextcolor))
            tvSave?.setOnClickListener{
                buttonclick="save"
                addMemberData(tvname.text.toString(), tvAge.text.toString(),checktype)
                addMemberBottomSheetDialog.dismiss()
            }
            tvSaveAndNext.setOnClickListener{
                buttonclick="save_next"

                if (joinList.size<maxvalue){
                    addMemberData(tvname.text.toString(), tvAge.text.toString(),checktype)
                    addMemberBottomSheetDialog.dismiss()

                }
            }
        }
    }


    fun addMemberData(name: String, age: String, checktype: String)  {
        val param: MutableMap<String, String> = HashMap()
        param["name"] = name
        param["age"] = age
        param["gender"] = checktype
        if (selectedPosition ==1){
            param["is_group"] = "0"
        }else{
            param["is_group"] = "1"
        }
        if (selectType.equals("add")){
            param["id"] =""

        }else{
            param["id"] = ""+member_id

        }
        Log.e("addMemberParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(context1,"")
        progressDialog.show()

        PostMethod(ApiURL.addmember,param, requireActivity()).startPostMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("addMemberRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        if (selectedPosition ==2) {
                            getMemberList(context!!)
                        }else{
                            getBuddyList(context!!)
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

    private fun getMemberList(context: Context) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(context,"")
        progressDialog.show()
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getMember+sharedPreferences.getInt("selectedPackageType",0)+"&type="+sharedPreferences.getString("typeWorkout","")+"&trainer_id="+trainer_id+
                    "&studio_id="+""
        }else{
            api= ApiURL.getMember+sharedPreferences.getInt("selectedPackageType",0)+"&type="+"gym"+"&trainer_id="+trainer_id+
                    "&studio_id="+studio_id
        }
        Log.e("GetMemberPackageAPi",api)
        GetMethod(api
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                joinList.clear()
                Log.e("GetMemberPackageResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonArray=resp.optJSONObject("data").optJSONArray("members")
                        tvPerson.text = resp.optJSONObject("data").optString("limit")
                        for (i in 0 until jsonArray.length()) {
                            var jsonObject=jsonArray.optJSONObject(i)
                            var activityModel= JoinModel()
                            activityModel.id=jsonObject.optString("id")
                            activityModel.name=jsonObject.optString("name")
                            activityModel.age=jsonObject.optString("age")
                            activityModel.gender=jsonObject.optString("gender")
                            activityModel.self=jsonObject.optString("self")
                            joinList.add(activityModel)
                        }
                        joinAdapter= JoinAdapter(joinList,context)
                        Log.e("joinLIst",""+joinList.size)
                        Recycler.adapter=joinAdapter
                        var minvalue=resp.optJSONObject("data").optString("min_member").toInt()
                        maxvalue=resp.optJSONObject("data").optString("max_member").toInt()

                       /* if (joinList.size>=minvalue){
                            (activity as? CreatePackagectivity)?.tvcontinueView!!.background = resources.getDrawable(R.drawable.white_rectangle)
                            (activity as? CreatePackagectivity)?.tvcontinue!!.setTextColor(resources.getColor(R.color.buttontextcolor))
//                             (activity as? CreatePackagectivity)?.tvcontinue!!.setTypeface(null, Typeface.BOLD)
                             (activity as? CreatePackagectivity)?.tvcontinue!!.isClickable = true
                        }else{
                             (activity as? CreatePackagectivity)?.tvcontinue!!.isClickable = false
                             (activity as? CreatePackagectivity)?.tvcontinueView!!.background = resources.getDrawable(R.drawable.rectangle_btn)
                             (activity as? CreatePackagectivity)?.tvcontinue!!.setTextColor(resources.getColor(R.color.white))
//                             (activity as? CreatePackagectivity)?.tvcontinue!!.setTypeface(null, Typeface.NORMAL)

                        }*/
                        if (joinList.size>=maxvalue){
                            linearAddMember.visibility=View.GONE
                        }else{
                            linearAddMember.visibility=View.VISIBLE

                        }
                        if (buttonclick.equals("save_next")){
                            if (joinList.size<maxvalue){
                                addmembeBottomsheet(context1)
                        }
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
    private fun getBuddyList(context: Context) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(context,"")
        progressDialog.show()
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getBuddyMember
        }else{
            api= ApiURL.getBuddyMember
        }
        Log.e("GetMemberPackageAPi",api)
        GetMethod(api
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                joinList.clear()
                Log.e("GetMemberPackageResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var jsonArray=resp.optJSONObject("data").optJSONArray("members")
                        tvPerson.text = resp.optJSONObject("data").optString("limit")
                        for (i in 0 until jsonArray.length()) {
                            var jsonObject=jsonArray.optJSONObject(i)
                            var activityModel= JoinModel()
                            activityModel.id=jsonObject.optString("id")
                            activityModel.name=jsonObject.optString("name")
                            activityModel.age=jsonObject.optString("age")
                            activityModel.gender=jsonObject.optString("gender")
                            activityModel.self=jsonObject.optString("self")
                            joinList.add(activityModel)
                        }
                        joinAdapter= JoinAdapter(joinList,context)
                        Log.e("joinLIst",""+joinList.size)
                        Recycler.adapter=joinAdapter
                        var minvalue=resp.optJSONObject("data").optString("min_member").toInt()
                        maxvalue=resp.optJSONObject("data").optString("max_member").toInt()

                        /* if (joinList.size>=minvalue){
                             (activity as? CreatePackagectivity)?.tvcontinueView!!.background = resources.getDrawable(R.drawable.white_rectangle)
                             (activity as? CreatePackagectivity)?.tvcontinue!!.setTextColor(resources.getColor(R.color.buttontextcolor))
 //                             (activity as? CreatePackagectivity)?.tvcontinue!!.setTypeface(null, Typeface.BOLD)
                              (activity as? CreatePackagectivity)?.tvcontinue!!.isClickable = true
                         }else{
                              (activity as? CreatePackagectivity)?.tvcontinue!!.isClickable = false
                              (activity as? CreatePackagectivity)?.tvcontinueView!!.background = resources.getDrawable(R.drawable.rectangle_btn)
                              (activity as? CreatePackagectivity)?.tvcontinue!!.setTextColor(resources.getColor(R.color.white))
 //                             (activity as? CreatePackagectivity)?.tvcontinue!!.setTypeface(null, Typeface.NORMAL)

                         }*/
                        if (joinList.size>=maxvalue){
                            linearAddMember.visibility=View.GONE
                        }else{
                            linearAddMember.visibility=View.VISIBLE

                        }
                        if (buttonclick.equals("save_next")){
                            if (joinList.size<maxvalue){
                                addmembeBottomsheet(context1)
                            }
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

    override fun onResume() {
        super.onResume()
        if (isVisible){
            if (selectedPosition ==2)
            getMemberList(requireContext())
            else getBuddyList(requireContext())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context1.registerReceiver(editMember, IntentFilter("editMember"),
                RECEIVER_EXPORTED)
            context1.registerReceiver(deleteMember, IntentFilter("deleteMember"),
                RECEIVER_EXPORTED)
        }else{
            context1.registerReceiver(editMember, IntentFilter("editMember"))
            context1.registerReceiver(deleteMember, IntentFilter("deleteMember"))

        }
    }
    val editMember = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            member_id=""+intent!!.getStringExtra("id")
            member_name=""+ intent.getStringExtra("name")
            member_age=""+ intent.getStringExtra("age")
            checktype=""+ intent.getStringExtra("gender")
            selectType=""+ intent.getStringExtra("typeselect")
            Handler(Looper.getMainLooper()).post {
                if (isAdded && activity != null) {
                    addmembeBottomsheet(context1)
                }
            }
        }

    }
    val deleteMember = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            buttonclick=""
            if (isAdded && view != null) {
                if (selectedPosition == 2)
                    getMemberList(context!!)
                else
                    getBuddyList(context!!)
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context1=context
    }
}