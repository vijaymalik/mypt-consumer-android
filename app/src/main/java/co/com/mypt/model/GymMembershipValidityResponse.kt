package co.com.mypt.model

data class GymMembershipValidityResponse(
    val status: Boolean,
    val data: GymMembershipPackageData,
    val msg: String
)

data class GymMembershipPackageData(
    val studio: Studio,
    val packageDetail: PackageDetail
)

data class Studio(
    val id: Int,
    val name: String,
    val profile: String,
    val tags: List<String>,
    val avg_rating: String,
    val total_rating: String
)

data class PackageDetail(
    val name: String,
    val price: Int,
    val validity: String,
    val image: String,
    val special_msg: String?,
    val save_price: String?
)