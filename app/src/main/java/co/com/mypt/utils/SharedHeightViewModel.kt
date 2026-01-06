package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedHeightViewModel : ViewModel() {
    val data = MutableLiveData<String>()
}