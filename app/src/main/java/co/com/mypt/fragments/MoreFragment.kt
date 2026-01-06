package co.com.mypt.fragments

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.Goals.MyGoalsActivity
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.More.FavouriteWorkoutActivity
import co.com.mypt.More.HealthStatusActivity
import co.com.mypt.More.HelpSupportActivity
import co.com.mypt.More.MyMealsActivity
import co.com.mypt.More.MyMilestoneActivity
import co.com.mypt.More.MyOrdersActivity
import co.com.mypt.More.MyTrainerActivity
import co.com.mypt.More.PaymentHistoryActivity
import co.com.mypt.More.Setting.ChatsActivity
import co.com.mypt.More.SettingsActivity
import co.com.mypt.Profile.NewUserProfileActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.Shop.CartActivity
import co.com.mypt.Shop.ShopActivity
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.onBoarding.PhoneNumberScreenActivity
import com.android.volley.VolleyError
import com.facebook.AccessToken
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject


class MoreFragment : Fragment() {
    lateinit var linearGoals:LinearLayout
    lateinit var linearLogout:LinearLayout
    lateinit var linearBookings:LinearLayout
    lateinit var linearMeal:LinearLayout
    lateinit var linearWorkoutLibrary:LinearLayout
    lateinit var linearFavouriteWorkout:LinearLayout
    lateinit var linearSetting:LinearLayout
    lateinit var linearShop:LinearLayout
    lateinit var linearCart:LinearLayout
    lateinit var linearChat:LinearLayout
    lateinit var linearFindTrainer:LinearLayout
    lateinit var linearFindGym:LinearLayout
    lateinit var linearTrainer:LinearLayout
    lateinit var linearHealth:LinearLayout
    lateinit var linearMilestone:LinearLayout
    lateinit var linearProfile:LinearLayout
    lateinit var linearPayment:LinearLayout
    lateinit var linearHelp:LinearLayout
    lateinit var linearOrder:LinearLayout
    lateinit var tvDeleteAccount:TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var deleteBottomSheetDialog:BottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       var view= inflater.inflate(R.layout.fragment_more, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        linearGoals=view.findViewById(R.id.linearGoals)
        linearOrder=view.findViewById(R.id.linearOrder)
        linearLogout=view.findViewById(R.id.linearLogout)
        linearBookings=view.findViewById(R.id.linearBookings)
        linearMeal=view.findViewById(R.id.linearMeal)
        linearPayment=view.findViewById(R.id.linearPayment)
        linearMilestone=view.findViewById(R.id.linearMilestone)
        linearChat=view.findViewById(R.id.linearChat)
        linearHealth=view.findViewById(R.id.linearHealth)
        linearWorkoutLibrary=view.findViewById(R.id.linearWorkoutLibrary)
        linearFavouriteWorkout=view.findViewById(R.id.linearFavouriteWorkout)
        linearSetting=view.findViewById(R.id.linearSetting)
        linearShop=view.findViewById(R.id.linearShop)
        linearCart=view.findViewById(R.id.linearCart)
        linearFindTrainer=view.findViewById(R.id.linearFindTrainer)
        linearFindGym=view.findViewById(R.id.linearFindGym)
        linearTrainer=view.findViewById(R.id.linearTrainer)
        linearHelp=view.findViewById(R.id.linearHelp)
        linearProfile=view.findViewById(R.id.linearProfile)
        tvDeleteAccount=view.findViewById(R.id.tvDeleteAccount)
        linearFindTrainer.setOnClickListener{
            var intent=Intent(activity,HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }
        linearHelp.setOnClickListener{
            var intent=Intent(activity,HelpSupportActivity::class.java)
            startActivity(intent)
        }
        linearMilestone.setOnClickListener{
            var intent=Intent(activity,MyMilestoneActivity::class.java)
            startActivity(intent)
        }
        linearLogout.setOnClickListener{
            sharedPreferences.edit().clear().apply()
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                sdkInitialize(requireActivity())
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                        null, HttpMethod.DELETE,
                        { response: GraphResponse? ->
                            LoginManager.getInstance().logOut()
                        }).executeAsync()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val intent= Intent(context, PhoneNumberScreenActivity::class.java)
            TaskStackBuilder.create(requireContext()).addNextIntentWithParentStack(intent).startActivities()
            (context as MainActivity).finish()
        }
        linearFindGym.setOnClickListener{
            var intent=Intent(activity,GymListActivity::class.java)
            sharedPreferences.edit().putString("typeWorkout","work").apply()
            startActivity(intent)
        }
        linearPayment.setOnClickListener{
            var intent=Intent(activity,PaymentHistoryActivity::class.java)
            startActivity(intent)
        }
        linearGoals.setOnClickListener{
            var intent=Intent(activity,MyGoalsActivity::class.java)
            startActivity(intent)
        }
        linearBookings.setOnClickListener{
            var fragmentManager= requireActivity().supportFragmentManager
            (context as MainActivity).navController.navigate(R.id.bookingFragment)
            fragmentManager.beginTransaction().replace(R.id.fragment_container,BookingFragment()).addToBackStack("fragment1").commit()
        }
        linearMeal.setOnClickListener{
            var intent=Intent(activity,MyMealsActivity::class.java)
            startActivity(intent)
        }
        linearSetting.setOnClickListener{
            var intent=Intent(activity,SettingsActivity::class.java)
            startActivity(intent)
        }
        linearFavouriteWorkout.setOnClickListener{
            var intent=Intent(activity,FavouriteWorkoutActivity::class.java)
            startActivity(intent)
        }
        linearChat.setOnClickListener{
            var intent=Intent(activity,ChatsActivity::class.java)
            startActivity(intent)
        }
        linearShop.setOnClickListener{
            var intent=Intent(activity,ShopActivity::class.java)
            startActivity(intent)
        }
        linearWorkoutLibrary.setOnClickListener{
            var fragmentManager= requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container,LibraryFragment()).addToBackStack("fragment1").commit()
            (context as MainActivity).navController.navigate(R.id.libraryFragment)
        }
        linearCart.setOnClickListener{
            var intent=Intent(activity,CartActivity::class.java)
            startActivity(intent)
        }
        linearTrainer.setOnClickListener{
            var intent=Intent(activity, MyTrainerActivity::class.java)
            startActivity(intent)
        }
        linearHealth.setOnClickListener{
            var intent=Intent(activity, HealthStatusActivity::class.java)
            intent.putExtra("selectedType","1")
            startActivity(intent)
        }
        linearProfile.setOnClickListener{
            var intent=Intent(activity, NewUserProfileActivity::class.java)
            startActivity(intent)
        }
        linearOrder.setOnClickListener{
            var intent=Intent(activity, MyOrdersActivity::class.java)
            startActivity(intent)
        }
        tvDeleteAccount.setOnClickListener{
           deleteBottomSheetDialog.show()
        }
        deleteAccountBottomSheet()
        return view
    }

    private fun deleteAccountBottomSheet() {

        deleteBottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        deleteBottomSheetDialog.setCancelable(false)
        val bottomSheet = layoutInflater.inflate(R.layout.delete_bottomsheet_dialog, null)
        deleteBottomSheetDialog.setContentView(bottomSheet)


        var imclose =bottomSheet.findViewById<ImageView>(R.id.imclose)
        var tvCancel =bottomSheet.findViewById<TextView>(R.id.tvCancel)
        val linearDelete =bottomSheet.findViewById<LinearLayout>(R.id.linearDelete)

        imclose.setOnClickListener{
            deleteBottomSheetDialog.dismiss()
        }
        tvCancel.setOnClickListener{
            deleteBottomSheetDialog.dismiss()
        }
        linearDelete.setOnClickListener{
           sendDeleteData()
        }

        val window = deleteBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(deleteBottomSheetDialog)
        /*  addCalorieBottomSheetDialog.setOnShowListener {
              val bottomSheet = addCalorieBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
              bottomSheet?.layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
              bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
          }*/
            deleteBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }
    private fun sendDeleteData() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(),"")
        progressDialog.show()

        var api=ApiURL.accountdelete

        Log.e("DeleteAccountUrl",api)
        GetMethod(api
            ,requireActivity()).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("DeleteAccountResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        sharedPreferences.edit().clear().apply()
                        val intent= Intent(context, PhoneNumberScreenActivity::class.java)
                        TaskStackBuilder.create(requireContext()).addNextIntentWithParentStack(intent).startActivities()
                        (context as MainActivity).finish()
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