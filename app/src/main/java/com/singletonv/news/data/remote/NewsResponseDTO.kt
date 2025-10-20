package com.singletonv.news.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponseDTO(
    @SerialName("articles")
    val articles: List<ArticleDTO> = listOf()
)