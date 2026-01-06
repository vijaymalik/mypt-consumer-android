package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedLocationViewModel : ViewModel() {
    val data = MutableLiveData<String>()
    val refreshLocation = MutableLiveData<Boolean>()
}