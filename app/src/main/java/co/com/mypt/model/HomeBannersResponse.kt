package co.com.mypt.model

import com.google.gson.annotations.SerializedName

data class HomeBannersResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("data")
    val data: List<BannerItem>?,

    @SerializedName("msg")
    val message: String
)

data class BannerItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("image")
    val imageUrl: String?
)