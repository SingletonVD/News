package com.singletonv.news.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceDTO(
    @SerialName("name")
    val name: String = ""
)