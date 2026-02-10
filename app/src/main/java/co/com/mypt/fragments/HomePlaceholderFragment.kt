package co.com.mypt.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import co.com.mypt.R

class HomePlaceholderFragment : Fragment(R.layout.fragment_placeholder) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isActiveUser = arguments?.getBoolean("isActiveUser") ?: false
        val name = arguments?.getString("name") ?: ""
        val lat = arguments?.getString("lat") ?: ""
        val long = arguments?.getString("long") ?: ""
        val chooseAddress = arguments?.getString("chooseAddress") ?: ""


        Handler(Looper.getMainLooper()).postDelayed({
            val frag = if (isActiveUser) {
                ActiveUserHomeFragmentNew.newInstance(name, lat, long)
            } else {
                GuestUserHomeFragmentNew.newInstance(lat, long, chooseAddress)
            }

            childFragmentManager.beginTransaction()
                .replace(R.id.home_container, frag)
                .commitNowAllowingStateLoss()

        }, 200)

    }
}
