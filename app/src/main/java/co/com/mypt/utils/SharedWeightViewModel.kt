package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedWeightViewModel : ViewModel() {
    val data = MutableLiveData<String>()
    val unit = MutableLiveData<String>()
}