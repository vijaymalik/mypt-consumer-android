package co.com.mypt.retrofitApi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.com.mypt.fragments.viewModels.GuestUserViewModel


class UserViewModelFactory(
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(GuestUserViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return GuestUserViewModel() as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}
