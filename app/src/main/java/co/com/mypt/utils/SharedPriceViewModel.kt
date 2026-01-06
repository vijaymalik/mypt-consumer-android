package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPriceViewModel : ViewModel() {
    val data = MutableLiveData<String>()
    val days = MutableLiveData<String>()
}