package co.com.mypt.model

import co.com.mypt.model.HomeContent.Data.Content

data class HomeContent(
    val data: List<Content?>?,
    val msg: String?,
    val status: Boolean?
) {
    data class Data(
        val contents: List<Content?>?,
        val offer: Offer?
    ) {
        data class Content(
            val description: String?,
            val id: Int?,
            val image: String?,
            val key: String?,
            val name: String?,
            val order: Int?
        )

        data class Offer(
            val code: String?,
            val description: String?,
            val show: Boolean?,
            val subtitle: String?,
            val title: String?
        )
    }
}