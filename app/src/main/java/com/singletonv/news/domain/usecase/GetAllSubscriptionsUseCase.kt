package com.singletonv.news.domain.usecase

import com.singletonv.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSubscriptionsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(): Flow<List<String>> = newsRepository.getAllSubscriptions()
}