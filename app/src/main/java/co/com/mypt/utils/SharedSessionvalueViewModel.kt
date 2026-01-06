package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedSessionvalueViewModel : ViewModel() {
    val data = MutableLiveData<String>()
}