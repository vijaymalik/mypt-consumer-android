package co.com.mypt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.BottomSheetAnimation
import co.com.mypt.activities.SelectCurrentLocationActivity
import co.com.mypt.model.AddressModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.text.replace

class AddressListAdapter(var addressList: ArrayList<AddressModel>,var activity: Context,val selectedAddressId:String="",val callBack:(String)->Unit) :
    RecyclerView.Adapter<AddressListAdapter.AddressListHolder>() {
    private var selectedPosition = 0 // Track selected position
    class AddressListHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvlocation=view.findViewById<TextView>(R.id.tvlocation)
//        var tvEdit=view.findViewById<TextView>(R.id.tvEdit)
        var tvHome=view.findViewById<TextView>(R.id.tvHome)
//        var tvmobile=view.findViewById<TextView>(R.id.tvmobile)
        var checkname=view.findViewById<CheckBox>(R.id.checkname)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressListAdapter.AddressListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_list, parent, false)
        return AddressListHolder(view)
    }

    override fun onBindViewHolder(holder: AddressListAdapter.AddressListHolder, @SuppressLint("RecyclerView") position: Int) {
       var addressModel=addressList[position]
        val landmark = if (!addressModel.landmark.isNullOrEmpty()) "${addressModel.landmark}," else ""

        holder.tvlocation.text=(addressModel.building_name+","+addressModel.street+","+landmark+addressModel.city_name+addressModel.country_name).replace("null,","")
        holder.tvHome.setText(addressModel.type)
//        holder.tvmobile.text=(addressModel.mobile_no).replace("null","")
        holder.checkname.isChecked = (position == selectedPosition)
//        holder.tvEdit.setTag(position)
        holder.checkname.setTag(position)
     /*   holder.tvEdit.setOnClickListener{
            val pos = it.tag as Int
            var addressModel=addressList[pos]
            var intent= Intent(activity,SelectCurrentLocationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.putExtra("link","edit")
            intent.putExtra("lat",addressModel.lat.toDouble())
            intent.putExtra("long",addressModel.long.toDouble())
            intent.putExtra("mobile",addressModel.mobile_no)
            intent.putExtra("street",addressModel.street)
            intent.putExtra("city",addressModel.city_name)
            intent.putExtra("city_id",addressModel.city_id)
            intent.putExtra("country_id",addressModel.country_id)
            intent.putExtra("type",addressModel.type)
            intent.putExtra("building_name",addressModel.building_name)
            intent.putExtra("landmark",addressModel.landmark)
            intent.putExtra("address_id",addressModel.id)
            activity.startActivity(intent)
            //editaddressBottomSheet(addressModel.building_name,addressModel.street,addressModel.city_name,addressModel.landmark,addressModel.mobile_no)
        }*/
        holder.checkname.setOnClickListener {
            var j=it.tag
            var addressModel=addressList.get(j as Int)
            var intent=Intent("selectAddress")
            if (selectedPosition == j) {
                selectedPosition = -1
                intent.putExtra("address_id", "")
                intent.putExtra("typeselect", "uncheck")
            }
            else{
                // Select new checkbox
                selectedPosition = j
                intent.putExtra("address_id", addressModel.id)
                intent.putExtra("typeselect", "check")
                android.util.Log.e("address_id",""+addressModel.id)

            }
            activity.sendBroadcast(intent)
            callBack( addressModel.id)

        }


    }

    override fun getItemCount(): Int {
        return addressList.size
    }


  /*  @SuppressLint("ClickableViewAccessibility")
    private fun editaddressBottomSheet(
        buildingName: String,
        street: String,
        cityName: String,
        landmark: String,
        mobileNo: String
    ) {
        var  editaddressBottomSheetDialog = BottomSheetDialog(activity, R.style.CustomBottomSheetDialogTheme)
        val bottomSheet = LayoutInflater.from(activity).inflate(R.layout.edit_address_bottomsheet, null)
        var autoCompleteCity=bottomSheet.findViewById<AutoCompleteTextView>(R.id.autoCompleteCity)
        var stateLinearLayout=bottomSheet.findViewById<LinearLayout>(R.id.stateLinearLayout)
        var tvCity1=bottomSheet.findViewById<TextInputEditText>(R.id.tvCity1)
        var tvCountry1=bottomSheet.findViewById<TextInputEditText>(R.id.tvCountry1)
        var tvBuildingName1=bottomSheet.findViewById<TextInputEditText>(R.id.tvBuildingName1)
        var tvStreetName1=bottomSheet.findViewById<TextInputEditText>(R.id.tvStreetName1)
        var tvLandmark1=bottomSheet.findViewById<TextInputEditText>(R.id.tvLandmark1)
        var stateTextInputLayout=bottomSheet.findViewById<TextInputLayout>(R.id.stateTextInputLayout)
        var checkHome1=bottomSheet.findViewById<CheckBox>(R.id.checkHome1)
        var checkOffice1=bottomSheet.findViewById<CheckBox>(R.id.checkOffice1)
        var checkOthers1=bottomSheet.findViewById<CheckBox>(R.id.checkOthers1)

        val cities = listOf("New York", "Los Angeles", "Chicago", "Houston", "San Francisco")
        val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, cities)

        autoCompleteCity.setAdapter(adapter)
        autoCompleteCity.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cities[position]
            tvCity1.setText(selectedCity)
            Toast.makeText(activity, "Selected: $selectedCity", Toast.LENGTH_SHORT).show()
        }

        tvCity1.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        stateTextInputLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        stateLinearLayout.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            autoCompleteCity.showDropDown()
            true
        })
        *//*autoCompleteCity.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val selection = parent.getItemAtPosition(position) as String
            var pos = -1
            for (i in countryModelList.indices) {
                if (selection.contains(countryModelList[i].phonecode)) {
                    pos = i
                    break
                }
            }

            phone_code = countryModelList[pos].phonecode
            phone_iso = countryModelList[pos].sortname
            phone_length = Integer.parseInt(countryModelList[pos].phone_length)
            countryimage = countryModelList[pos].country_flag
            Glide.with(applicationContext).load(countryModelList[pos].country_flag).into(im)
            userPhoneno.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(phone_length))

            Log.e("phonecode", phone_code)
            Log.e("phone_iso", phone_iso)
            Log.e("countryimage", countryimage)
        })
*//*
        editaddressBottomSheetDialog.setContentView(bottomSheet)
        BottomSheetAnimation.animateBottomSheet(editaddressBottomSheetDialog)
        editaddressBottomSheetDialog.show()

    }*/
}
