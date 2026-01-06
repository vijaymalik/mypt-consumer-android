package co.com.mypt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectExcerciseModel(
    var category: String = " ",
     var raps: String = " ",
     var video_path: String = " ",
     var video_type: String = " ",
     var calories: String = " ",
     var image: String = " ",
     var id: String = " ",
     var name: String = " ",
     var type: String = " ",
     var sets: String = " ",
    var duration: String=" ",
     var rest_duration: String = " ",

) : Parcelable

   

