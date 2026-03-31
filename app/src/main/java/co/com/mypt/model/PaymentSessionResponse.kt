package co.com.mypt.model

data class PaymentSessionResponse(
    val status: Boolean,
    val data: PaymentSessionData,
    val msg: String
) {
    data class PaymentSessionData(
        val gateway: String,
        val payment_id: Int,
        val order_ref: String,
        val html: String,
        val success_url: String,
        val failure_url: String,
        val deeplink_scheme: String
    )
}
