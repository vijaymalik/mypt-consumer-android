package co.com.mypt.onBoarding.personalize

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.GoalsAdapter
import co.com.mypt.model.GoalsModel
import co.com.mypt.onBoarding.PersonalizedActivity2
import com.android.volley.VolleyError
import org.json.JSONObject


class GoalsFragment : Fragment() {
    private var recycler: RecyclerView? = null


    companion object {
        fun newInstance(isFreshVisit: Boolean): GoalsFragment {
            val fragment = GoalsFragment()
            val args = Bundle()
            args.putBoolean("isFreshVisit", isFreshVisit)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)
        recycler = view.findViewById(R.id.recycler)

        /* val imageList = listOf(
             R.drawable.mental,
             R.drawable.weightgoals,
             R.drawable.muscle,
             R.drawable.cv,
             R.drawable.flexibilty,
             R.drawable.holistic,
             R.drawable.sports,
             R.drawable.goals_other,
             )
         val selectedImageList = listOf(
             R.drawable.selected_mental,
             R.drawable.selectedweight,
             R.drawable.selectedmuscle,
             R.drawable.selectedcv,
             R.drawable.selectedflexibilty,
             R.drawable.selectedholistic,
             R.drawable.selectedsports,
             R.drawable.selectedgoalsother,
         )
         var textList= listOf("Mental\nHealth","Weight\nLoss","Muscle\nBuilding","Improved CV\nEndurance","Flexibility\n& Mobility",
             "Holistic\nFitness","Sports\nConditioning","Others")*/

        getGoals()
        return view
    }

    private fun getGoals() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireContext(), "")
        progressDialog.show()

        GetMethod(ApiURL.goals, context).startMethod(object : ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()
                Log.e("Goalspreferences", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    val jsonArray = jsonObj.optJSONArray("data")
                    val goalsArrayList = ArrayList<GoalsModel>()
                    if (jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val listData = jsonArray.optJSONObject(i)
                            val goalsModel = GoalsModel()
                            goalsModel.selectedImage = listData.optString("select_image")
                            goalsModel.unselectedImage = listData.optString("image")
                            goalsModel.name = listData.optString("name")
                            goalsModel.id = listData.optString("id")
                            goalsArrayList.add(goalsModel)
                        }
                    }
                    val goalsAdapter = GoalsAdapter(activity, goalsArrayList)

                    recycler?.adapter = goalsAdapter

                } catch (e: Exception) {
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
        if (isVisible) {
            Log.e("selectedCountGoals", "${(context as PersonalizedActivity2).selectedCount}")
            if ((context as PersonalizedActivity2).selectedCount == 0) {
                getGoals()
            }
        }
    }
}