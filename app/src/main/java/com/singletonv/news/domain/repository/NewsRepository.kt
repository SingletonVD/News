package com.singletonv.news.domain.repository

import com.singletonv.news.domain.entity.Article
import com.singletonv.news.domain.entity.Language
import com.singletonv.news.domain.entity.RefreshConfig
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getAllSubscriptions(): Flow<List<String>>

    suspend fun addSubscription(topic: String)

    suspend fun updateArticlesForTopic(topic: String, language: Language): Boolean

    suspend fun removeSubscriptions(topic: String)

    suspend fun updateArticlesForAllSubscriptions(language: Language): List<String>

    fun getArticlesByTopics(topics: List<String>): Flow<List<Article>>

    suspend fun clearAllArticles(topics: List<String>)

    fun startBackgroundRefresh(refreshConfig: RefreshConfig)
}