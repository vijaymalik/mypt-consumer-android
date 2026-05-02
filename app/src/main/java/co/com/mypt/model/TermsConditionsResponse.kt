package co.com.mypt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TermsConditionsResponse(
    val status: Boolean,
    val data: TermsData?,
    val msg: String
): Parcelable
@Parcelize
data class TermsData(
    val id: Int,
    val type: String?,
    val name: String?,
    val title_en: String?,
    val title_ar: String?,
    val content_en: String?,
    val content_ar: String?
): Parcelable