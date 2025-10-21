package com.singletonv.news.domain.usecase

import com.singletonv.news.domain.entity.Interval
import com.singletonv.news.domain.entity.Settings
import com.singletonv.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(interval: Interval) {
        settingsRepository.updateInterval(interval.minutes)
    }
}