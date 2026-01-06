package co.com.mypt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDuringSessionViewModel : ViewModel(){

    var setstart_dates=MutableLiveData<String>()
    var setstart_days=MutableLiveData<String>()
    var setend_dates=MutableLiveData<String>()
    var setend_days=MutableLiveData<String>()

    var apistart_date=MutableLiveData<String>()
    var apiend_date=MutableLiveData<String>()
}