package co.com.mypt.model

data class GetPlansResponse(
    val status: Boolean,
    val data: List<PlanDetail>,
    val msg: String
)

data class PlanDetail(
    val id: Int,
    val name: String,
    val remaining_sessions: Int,
    val validity_days: Int,
    val remaining_days: Int,
    val type: String,
    val sessions: Int,
    val amount: String,
    val isUpgrade: Boolean,
    val isShow: Boolean,
    val renew_new: Boolean,
    val image: String,
    val end_date: String,
    val msg: String
)
