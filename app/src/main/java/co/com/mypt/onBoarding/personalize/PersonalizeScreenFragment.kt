package co.com.mypt.onBoarding.personalize

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.Constants
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.PersonalizedAdapter
import co.com.mypt.model.PreferencesModel
import com.android.volley.VolleyError
import org.json.JSONObject


class PersonalizeScreenFragment(val name: String) : Fragment() {
    lateinit var recycler: RecyclerView
    lateinit var userName: TextView
    lateinit var sharedPreferences: SharedPreferences
    var preferencesArrayList = ArrayList<PreferencesModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_personalize_screen, container, false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        recycler=view.findViewById(R.id.recycler)
        userName=view.findViewById(R.id.userName)

        val imageList = listOf(
            R.drawable.weight,
            R.drawable.boost,
            R.drawable.health,
            R.drawable.care,
            R.drawable.event,
            R.drawable.others,
        )
        val selectedImageList = listOf(
            R.drawable.selectedweight,
            R.drawable.selectedboost,
            R.drawable.selectedhealth,
            R.drawable.selectedcare,
            R.drawable.selectedevent,
            R.drawable.selectedother,
        )
        val textList= listOf("Weight\n"+"Management","Boost Self\n"+"Esteem","Health &\nFitness","Chronic illness\n"+"Care","Preparing for\n"+"an event",
            "Others")


        userName.text = "Hello ${sharedPreferences.getString(Constants.name,"")}, "

        getPreferences()
        return view
    }

    private fun getPreferences() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(),"")
        progressDialog.show()

        GetMethod(ApiURL.preferences,context).startMethod(object : ResponseData{
            override fun response(data: String?) {
                progressDialog.dismiss()
                Log.e("preferences",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    for(i in 0 until jsonObj.optJSONArray("data").length()){
                        val listData = jsonObj.optJSONArray("data").optJSONObject(i)
                        val preferencesModel = PreferencesModel()
                        preferencesModel.selectedImage = listData.optString("select_image")
                        preferencesModel.unselectedImage = listData.optString("image")
                        preferencesModel.name = listData.optString("name")
                        preferencesModel.id = listData.optString("id")

                        preferencesArrayList.add(preferencesModel)
                    }
                    val personalizedAdapter= PersonalizedAdapter(activity,preferencesArrayList)

                    recycler.adapter=personalizedAdapter
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