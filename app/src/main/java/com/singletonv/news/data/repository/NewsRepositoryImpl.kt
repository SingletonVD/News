package com.singletonv.news.data.repository

import android.util.Log
import com.singletonv.news.data.local.ArticleDbModel
import com.singletonv.news.data.local.NewsDao
import com.singletonv.news.data.local.SubscriptionDbModel
import com.singletonv.news.data.mapper.toDbModel
import com.singletonv.news.data.mapper.toEntities
import com.singletonv.news.data.remote.NewsApiService
import com.singletonv.news.domain.entity.Article
import com.singletonv.news.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService
) : NewsRepository {

    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map { it.topic }
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String) {
        val articles = loadArticles(topic)
        newsDao.addArticles(articles)
    }

    override suspend fun removeSubscriptions(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForAllSubscriptions() {
        val subscriptions = newsDao.getAllSubscriptions().first()
        coroutineScope {
            subscriptions.forEach {
                launch {
                    updateArticlesForTopic(it.topic)
                }
            }
        }
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getAllArticlesByTopics(topics).map { it.toEntities() }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics)
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            newsApiService.loadArticles(topic).toDbModel(topic)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            listOf()
        }
    }
}