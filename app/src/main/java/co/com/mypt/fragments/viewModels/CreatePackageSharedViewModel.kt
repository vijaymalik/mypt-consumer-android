package co.com.mypt.fragments.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class CreatePackageSharedViewModel: ViewModel() {

    var isMinMembersAdded = false
    var isMinBuddyAdded = false
    val addMinMembers = MutableSharedFlow<Boolean>()
}