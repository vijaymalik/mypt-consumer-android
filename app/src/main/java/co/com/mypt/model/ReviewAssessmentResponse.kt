package co.com.mypt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewAssessmentResponse(
    val status: Boolean,
    val data: AssessmentData,
    val msg: String
): Parcelable

@Parcelize
data class AssessmentData(
    val trainer_name: String,
    val date: String,
    val timing: String,
    val type: String,
    val location_name: String,
    val remaining_session: Int
): Parcelable
