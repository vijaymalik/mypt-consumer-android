package co.com.mypt.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StreakModel(
    var completed: String = "",
    var date: String = "",
    var day: String = "",
    var dayNumber: String = "",
    var dayName: String = "",
    var status: String=""
) : Parcelable
