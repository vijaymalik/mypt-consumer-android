package co.com.mypt.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainerStudiosResponse(
    val status: Boolean,
    val data: Data?,
    val msg: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val trainer: Trainer?,
        val studios: List<Studio>?
    ) : Parcelable {
        @Parcelize
        data class Trainer(
            val id: Int?,
            val name: String?,
            val profile: String?,
            val is_verified: Boolean?,
            val averageRating: Int?,
            val noOfRating: String?,
            val tags: List<Tag>?
        ) : Parcelable {
            @Parcelize
            data class Tag(
                val id: Int?,
                val name: String?
            ) : Parcelable
        }

        @Parcelize
        data class Studio(
            val id: Int?,
            val name: String?,
            val address: String?,
            val image: String?,
            val distance: String?
        ) : Parcelable
    }
}
