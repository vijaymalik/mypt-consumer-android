package co.com.mypt.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.com.mypt.R

class HomePlaceholderFragment : Fragment(R.layout.fragment_placeholder) {

    private var currentChildTag: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Optional: load default state if arguments exist
        val isActiveUser = arguments?.getBoolean("isActiveUser")

        if (isActiveUser != null) {
            updateHome(
                isActiveUser,
                arguments?.getString("name") ?: "",
                arguments?.getString("lat") ?: "",
                arguments?.getString("long") ?: "",
                arguments?.getString("chooseAddress") ?: ""
            )
        }
    }

    fun updateHome(
        isActiveUser: Boolean,
        name: String,
        lat: String,
        long: String,
        chooseAddress: String
    ) {

        val newTag = if (isActiveUser) "ACTIVE" else "GUEST"

        // 🚀 Prevent reloading same fragment again
        if (currentChildTag == newTag) return

        currentChildTag = newTag

        val fragment = if (isActiveUser) {
            ActiveUserHomeFragmentNew.newInstance(lat, long, chooseAddress)
        } else {
            GuestUserHomeFragmentNew.newInstance(lat, long, chooseAddress)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.home_container, fragment, newTag)
            .commit()
    }
}