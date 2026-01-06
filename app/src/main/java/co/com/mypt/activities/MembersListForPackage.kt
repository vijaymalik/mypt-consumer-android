package co.com.mypt.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.PostMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.MemberListAdapter
import co.com.mypt.model.JoinModel
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class MembersListForPackage : AppCompatActivity(){

    var checktype="male"
    var selectType=""
    var member_id=""
    var member_name=""
    var member_age=""
    var trainer_id=""
    var studio_id=""
    lateinit var Recycler: RecyclerView
    lateinit var linearAddMember: LinearLayout
    lateinit var tvPerson: TextView
    lateinit var tvcontinue: TextView
    lateinit var joinAdapter: MemberListAdapter
    var joinList = ArrayList<JoinModel>()
    lateinit var standard_bottom_sheet: LinearLayout
    lateinit var addMemberBottomSheetDialog: BottomSheetDialog
    lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.members_list_activity)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)

        tvcontinue=findViewById(R.id.tvcontinue)
        Recycler=findViewById(R.id.Recycler)
        tvPerson=findViewById(R.id.tvPerson)
        linearAddMember=findViewById(R.id.linearAddMember)
        addMemberBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        
        linearAddMember.setOnClickListener{
            selectType="add"
            addMemberBottomSheet()
        }
        tvcontinue.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        trainer_id = intent.getStringExtra("trainer_id").toString()
        studio_id = intent.getStringExtra("studio_id").toString()
        getMemberList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(editMember, IntentFilter("editMember"), RECEIVER_EXPORTED)
            registerReceiver(deleteMember, IntentFilter("deleteMember"), RECEIVER_EXPORTED)
        }else{
            registerReceiver(editMember, IntentFilter("editMember"))
            registerReceiver(deleteMember, IntentFilter("deleteMember"))

        }
    }

    fun addMemberData(name: String, age: String, checktype: String)  {
        val param: MutableMap<String, String> = HashMap()
        param["name"] = name
        param["age"] = age
        param["gender"] = checktype
        if (selectType.equals("add")){
            param["id"] =""

        }else{
            param["id"] = ""+member_id

        }
        Log.e("addMemberParam", param.toString())

        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()

        PostMethod(ApiURL.addmember,param, this).startPostMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                try {
                    Log.e("addMemberRes",data.toString())
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        addMemberBottomSheetDialog.dismiss()
                        getMemberList()
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

    private fun getMemberList() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""
        if (sharedPreferences.getString("typeWorkout","").equals("home")){
            api= ApiURL.getMember+sharedPreferences.getInt("selectedPackageType",0)+
                    "&type="+sharedPreferences.getString("typeWorkout","")+
                    "&trainer_id="+trainer_id+ "&studio_id="+""
        }else{
            api= ApiURL.getMember+sharedPreferences.getInt("selectedPackageType",0)+
                    "&type="+sharedPreferences.getString("typeWorkout","")+
                    "&trainer_id="+trainer_id+ "&studio_id="+studio_id
        }
        Log.e("GetMemberPackageAPi",api)
        GetMethod(api,this).startMethod(object :
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
                        joinAdapter= MemberListAdapter(joinList,applicationContext)
                        Recycler.adapter=joinAdapter
                        var minvalue=resp.optJSONObject("data").optString("min_member").toInt()
                        var maxvalue=resp.optJSONObject("data").optString("max_member").toInt()

                        if (joinList.size>=minvalue){
                            tvcontinue!!.background = resources.getDrawable(R.drawable.white_rectangle)
                            tvcontinue!!.setTextColor(resources.getColor(R.color.buttontextcolor))
                            tvcontinue!!.setTypeface(null, Typeface.BOLD)
                            tvcontinue!!.isClickable = true
                        }else{
                            tvcontinue!!.isClickable = false
                            tvcontinue!!.background = resources.getDrawable(R.drawable.rectangle_btn)
                            tvcontinue!!.setTextColor(resources.getColor(R.color.white))
                            tvcontinue!!.setTypeface(null, Typeface.NORMAL)

                        }
                        if (joinList.size>=maxvalue){
                            linearAddMember.visibility= View.GONE
                        }else{
                            linearAddMember.visibility= View.VISIBLE

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

    private fun addMemberBottomSheet() {
        val bottomSheet = layoutInflater.inflate(R.layout.addmember_bottomsheet, null)
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

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                checkvalue(tvname,tvAge,tvSave)
            }
        })
        tvAge.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                checkvalue(tvname, tvAge, tvSave)
            }
        })


        if (tvname!!.text.toString().trim()=="" || tvAge!!.text.toString().trim()==""){
            tvSave!!.setBackgroundResource(R.drawable.grey_rectangle_rounded)
            tvSave.setTextColor(resources.getColor(R.color.white,null))
            tvSave.setOnClickListener{
            }
        }else{
            tvSave!!.setBackgroundResource(R.drawable.apply_rectangle)
            tvSave.setTextColor(resources.getColor(R.color.buttontextcolor,null))
            tvSave.setOnClickListener{
                addMemberData(tvname.text.toString(), tvAge.text.toString(),checktype)
                addMemberBottomSheetDialog.dismiss()
            }
        }
        addMemberBottomSheetDialog.setContentView(bottomSheet)
        addMemberBottomSheetDialog.show()

        val window = addMemberBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    val editMember = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            member_id=""+intent!!.getStringExtra("id")
            member_name=""+ intent.getStringExtra("name")
            member_age=""+ intent.getStringExtra("age")
            checktype=""+ intent.getStringExtra("gender")
            selectType=""+ intent.getStringExtra("typeselect")
            addMemberBottomSheet()
        }

    }
    val deleteMember = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            getMemberList()
        }
    }

    private fun checkvalue(
        tvname: TextInputEditText?,
        tvAge: TextInputEditText?,
        tvSave: TextView?
    ) {
        if (tvname!!.text.toString().trim()=="" || tvAge!!.text.toString().trim()==""){
            tvSave!!.setBackgroundResource(R.drawable.grey_rectangle_rounded)
            tvSave.setTextColor(resources.getColor(R.color.white,null))
            tvSave.setOnClickListener{
            }
        }else{
            tvSave!!.setBackgroundResource(R.drawable.apply_rectangle)
            tvSave.setTextColor(resources.getColor(R.color.buttontextcolor,null))
            tvSave.setOnClickListener{
                addMemberData(tvname.text.toString(), tvAge.text.toString(),checktype)
                addMemberBottomSheetDialog.dismiss()
            }
        }
    }
}
