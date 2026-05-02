package co.com.mypt.model
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubscriptionSlotsResponse(
    val status: Boolean,
    val data: Data,
    val msg: String
): Parcelable
@Parcelize
data class Data(
    @SerializedName("is_group")
    val isGroup: Boolean,

    val type: String,

    @SerializedName("available_types")
    val availableTypes: List<String>,

    @SerializedName("remaining_sessions")
    val remainingSessions: Int,

    val group: Group?,

    val date: String,

    @SerializedName("date_formatted")
    val dateFormatted: String,

    val trainers: List<Trainer>?,

    val message: String?
): Parcelable
@Parcelize
data class Group(
    val id: Int,
    val name: String,
    val type: String,
    val image: String,
    val msg: String
): Parcelable
@Parcelize
data class Trainer(
    val id: Int,
    val name: String,
    val profile: String,
    val badge: String,
    val slots: List<Slot>,

    @SerializedName("slots_count")
    val slotsCount: Int
): Parcelable
@Parcelize
data class Slot(
    val id: Int,
    val time: String,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("end_time")
    val endTime: String
): Parcelable