package com.singletonv.news.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDTO(
    @SerialName("description")
    val description: String? = "",
    @SerialName("publishedAt")
    val publishedAt: String = "",
    @SerialName("source")
    val source: SourceDTO = SourceDTO(),
    @SerialName("title")
    val title: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("urlToImage")
    val urlToImage: String? = ""
)