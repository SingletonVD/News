package com.singletonv.news.domain.usecase

import com.singletonv.news.domain.entity.Settings
import com.singletonv.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<Settings> {
        return settingsRepository.getSettings()
    }
}