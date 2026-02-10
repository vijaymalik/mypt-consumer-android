package co.com.mypt.model

data class GetStoriesList(
    val data: Data?,
    val msg: String?,
    val status: Boolean?
) {
    data class Data(
        val data: List<StoryList?>?,
        val pagination: Pagination?
    ) {
        data class StoryList(
            val category_description: String?,
            val category_icon: Any?,
            val category_id: Int?,
            val category_image: Any?,
            val category_name: String?,
            val category_order: Int?,
            val stories: List<Story?>?,
            val stories_count: Int?
        ) {
            data class Story(
                val caption: String?,
                val cta_text: String?,
                val cta_url: String?,
                val expires_at: String?,
                val id: Int?,
                val media_path: String?,
                val published_at: String?,
                val thumb_path: String?,
                val title: String?,
                val type: String?
            )
        }

        data class Pagination(
            val current_page: Int?,
            val from: Int?,
            val has_more_pages: Boolean?,
            val last_page: Int?,
            val per_page: Int?,
            val to: Int?,
            val total: Int?
        )
    }
}