package co.com.mypt.fragments

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.GymWorkout.withTrainer.GymListActivity
import co.com.mypt.More.MyTrainerActivity
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.activities.HomeGymTrainerActivity
import co.com.mypt.activities.MainActivity
import co.com.mypt.curvedBottomNavigation.setUnderlineClickableText
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
    lateinit var linearLogout: LinearLayout
    lateinit var linearBookings: LinearLayout
    lateinit var linearFindTrainer: LinearLayout
    lateinit var linearFindGym: LinearLayout
    lateinit var linearTrainer: LinearLayout


    lateinit var tvDeleteAccount: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var deleteBottomSheetDialog: BottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_more, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        linearLogout = view.findViewById(R.id.linearLogout)
        linearBookings = view.findViewById(R.id.linearBookings)

        linearFindTrainer = view.findViewById(R.id.linearFindTrainer)
        linearFindGym = view.findViewById(R.id.linearFindGym)
        linearTrainer = view.findViewById(R.id.linearTrainer)

        tvDeleteAccount = view.findViewById(R.id.tvDeleteAccount)

        linearFindTrainer.setOnClickListener {
            var intent = Intent(activity, HomeGymTrainerActivity::class.java)
            startActivity(intent)
        }

        linearLogout.setOnClickListener {
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
                    GraphRequest(
                        AccessToken.getCurrentAccessToken(), "/me/permissions/",
                        null, HttpMethod.DELETE,
                        { response: GraphResponse? ->
                            LoginManager.getInstance().logOut()
                        }).executeAsync()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val intent = Intent(context, PhoneNumberScreenActivity::class.java)
            TaskStackBuilder.create(requireContext()).addNextIntentWithParentStack(intent)
                .startActivities()
            (context as MainActivity).finish()
        }
        linearFindGym.setOnClickListener {
            var intent = Intent(activity, GymListActivity::class.java)
            sharedPreferences.edit().putString("typeWorkout", "work").apply()
            startActivity(intent)
        }

        linearBookings.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            (context as MainActivity).navController.navigate(R.id.bookingFragment)
            fragmentManager.beginTransaction().replace(R.id.fragment_container, BookingFragment())
                .addToBackStack("fragment1").commit()
        }

        linearTrainer.setOnClickListener {
            var intent = Intent(activity, MyTrainerActivity::class.java)
            startActivity(intent)
        }

       tvDeleteAccount.setUnderlineClickableText("To delete your data permanently, close your account.","close your account."){
           deleteBottomSheetDialog.show()
       }
        deleteAccountBottomSheet()
        return view
    }

    private fun setCloseAccountSpannable(){
        val fullText = "To delete your data permanently, close your account."
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("close your account")
        val end = start + "close your account".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                deleteBottomSheetDialog.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(requireContext(), R.color.light_gray_delete)
            }
        }
        spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            clickableSpan,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvDeleteAccount.text = spannable
        tvDeleteAccount.movementMethod = LinkMovementMethod.getInstance()
        tvDeleteAccount.highlightColor = Color.TRANSPARENT
    }

    private fun deleteAccountBottomSheet() {

        deleteBottomSheetDialog =
            BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        deleteBottomSheetDialog.setCancelable(false)
        val bottomSheet = layoutInflater.inflate(R.layout.delete_bottomsheet_dialog, null)
        deleteBottomSheetDialog.setContentView(bottomSheet)


        var imclose = bottomSheet.findViewById<ImageView>(R.id.imclose)
        var tvCancel = bottomSheet.findViewById<TextView>(R.id.tvCancel)
        val linearDelete = bottomSheet.findViewById<LinearLayout>(R.id.linearDelete)

        imclose.setOnClickListener {
            deleteBottomSheetDialog.dismiss()
        }
        tvCancel.setOnClickListener {
            deleteBottomSheetDialog.dismiss()
        }
        linearDelete.setOnClickListener {
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
        val progressDialog: Dialog = ProgressDialog.progressDialog(requireActivity(), "")
        progressDialog.show()

        var api = ApiURL.accountdelete

        Log.e("DeleteAccountUrl", api)
        GetMethod(
            api, requireActivity()
        ).startMethod(object :
            ResponseData {
            override fun response(data: String?) {
                progressDialog.dismiss()

                Log.e("DeleteAccountResponse", data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")) {
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(context, PhoneNumberScreenActivity::class.java)
                        TaskStackBuilder.create(requireContext())
                            .addNextIntentWithParentStack(intent).startActivities()
                        (context as MainActivity).finish()
                    }


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
}