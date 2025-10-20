package com.singletonv.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/everything?apiKey=3a211640eddb4671952f7b9fd1fcb90f")
    suspend fun loadArticles(
        @Query("q") topic: String
    ): NewsResponseDto
}