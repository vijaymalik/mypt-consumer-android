package co.com.mypt.model

data class BestPlanList(
    val data: List<BestPlanData?>?,
    val msg: String?,
    val status: Boolean?
) {
    data class BestPlanData(
        val background_image: String?,
        val badge_text: String?,
        val currency: String?,
        val features: List<String?>?,
        val id: String?,
        val period_type: String?,
        val price: String?,
        val price_per_session: String?,
        val sessions: String?,
        val title: String?,
        val validity_days: String?,
        val validity_months: String?,
        val validity_text: String?
    )
}