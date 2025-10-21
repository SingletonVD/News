package com.singletonv.news.domain.usecase

import com.singletonv.news.data.mapper.toRefreshConfig
import com.singletonv.news.domain.repository.NewsRepository
import com.singletonv.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StartRefreshDataUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke() {
        settingsRepository.getSettings()
            .map { it.toRefreshConfig() }
            .distinctUntilChanged()
            .onEach {
                newsRepository.startBackgroundRefresh(it)
            }.collect()
    }
}