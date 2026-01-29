package co.com.mypt.model

data class TrainerGroupDetail(
    val data: Data?,
    val msg: String?,
    val status: Boolean?
) {
    data class Data(
        val group: Group?,
        val primary_trainer: PrimaryTrainer?,
        val secondary_trainers: List<SecondaryTrainer?>?,
        val secondary_trainers_note: String?
    ) {
        data class Group(
            val description: String?,
            val id: String?,
            val image: Any?,
            val name: String?,
            val type: String?
        )

        data class PrimaryTrainer(
            val badge: String?,
            val id: String?,
            val name: String?,
            val phone: String?,
            val profile: String?,
            val rating: Int?,
            val tags: List<String?>?
        )

        data class SecondaryTrainer(
            val badge: String?,
            val id: String?,
            val name: String?,
            val phone: String?,
            val profile: String?,
            val rating: Int?,
            val tags: List<String?>?
        )
    }
}