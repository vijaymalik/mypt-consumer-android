package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDOBViewModel : ViewModel() {
    val data = MutableLiveData<String>()
}