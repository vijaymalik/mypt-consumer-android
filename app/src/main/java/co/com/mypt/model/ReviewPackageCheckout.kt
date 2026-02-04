package co.com.mypt.model

data class ReviewPackageCheckout(
    val data: Data?,
    val msg: String?,
    val status: Boolean?
) {
    data class Data(
        val address: Address?,
        val applied_offer: AppliedOffer?,
        val available_promos: List<AvailablePromo?>?,
        val package_details: PackageDetails?,
        val payment_msg: String?,
        val trainer_detail: TrainerDetail?,
        val upgrade_plan: UpgradePlan?
    ) {
        data class Address(
            val building_name: String?,
            val city_id: Int?,
            val city_name: String?,
            val country_id: Int?,
            val country_name: String?,
            val id: Int?,
            val landmark: Any?,
            val lat: String?,
            val long: String?,
            val mobile_no: String?,
            val street: String?,
            val type: String?,
            val user_id: Int?
        )

        data class AppliedOffer(
            val benefit_type: String?,
            val code: String?,
            val description: String?,
            val discount_amount: Int?,
            val discount_type: String?,
            val discount_value: Int?,
            val final_price: Int?,
            val free_sessions: Int?,
            val id: Int?,
            val name: String?,
            val offer_mode: String?,
            val original_price: String?,
            val total_sessions: Int?
        )

        data class AvailablePromo(
            val benefit_type: String?,
            val description: String?,
            val discount_amount: Int?,
            val discount_type: String?,
            val discount_value: Int?,
            val free_sessions: Int?,
            val id: Int?,
            val is_applied: Boolean?,
            val name: String?,
            val offer_code: String?,
            val offer_details: String?,
            val offer_mode: String?,
            val product_type: Int?,
            val purchase_value: Int?
        )

        data class PackageDetails(
            val address_id: String?,
            val applied_offer_id: Int?,
            val best_plan_id: String?,
            val bonus_sessions: Int?,
            val is_best_plan: Boolean?,
            val main_price: Double?,
            val package_type: String?,
            val price: Int?,
            val price_per_session: String?,
            val sessions: String?,
            val studio_id: String?,
            val tax_amount: Double?,
            val tax_rate: Int?,
            val text_msg: String?,
            val total_sessions: Int?,
            val trainer_id: String?,
            val type: String?,
            val validity: String?,
            val validity_days: Int?
        )

        data class TrainerDetail(
            val is_group: Boolean?,
            val primary_trainer: PrimaryTrainer?,
            val secondary_trainers: List<Any?>?
        ) {
            data class PrimaryTrainer(
                val badge: Any?,
                val id: String?,
                val name: String?,
                val phone: String?,
                val profile: String?,
                val rating: String?
            )
        }

        data class UpgradePlan(
            val background_image: String?,
            val badge_text: String?,
            val currency: String?,
            val id: String?,
            val price: Int?,
            val price_per_session: Int?,
            val sessions: Int?,
            val special_msg: String?,
            val title: String?,
            val validity: String?
        )
    }
}