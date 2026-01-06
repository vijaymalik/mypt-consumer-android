package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedGenderViewModel : ViewModel() {
    val data = MutableLiveData<String>()
}