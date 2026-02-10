package co.com.mypt.model

data class TrainerListModelX(
    val `data`: Data?,
    val msg: String?,
    val status: Boolean?
) {
    data class Data(
        val tags: List<Tag?>?,
        val trainers: List<Trainer?>?,
        val type: String?
    ) {
        data class Tag(
            val description: String?,
            val icon: Any?,
            val id: Int?,
            val image: String?,
            val name: String?
        )

        data class Trainer(
            val averageRating: Any?,
            val distance: String?,
            val distance_sort: Double?,
            val encrypted_id: String?,
            val id: String?,
            val is_full: Boolean?,
            val is_group: Boolean?,
            val is_verified: Boolean?,
            val location: String?,
            val name: String?,
            val noOfRating: String?,
            val profile: String?,
            val slot: String?,
            val tags: List<Tag?>?,
            val total_available: Int?
        ) {
            data class Tag(
                val id: Int?,
                val name: String?,
                val pivot: Pivot?
            ) {
                data class Pivot(
                    val speciality_id: Int?,
                    val trainer_id: Int?
                )
            }
        }
    }
}