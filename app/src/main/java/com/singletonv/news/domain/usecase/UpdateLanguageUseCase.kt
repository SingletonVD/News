package com.singletonv.news.domain.usecase

import com.singletonv.news.domain.entity.Interval
import com.singletonv.news.domain.entity.Language
import com.singletonv.news.domain.entity.Settings
import com.singletonv.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(language: Language) {
        settingsRepository.updateLanguage(language)
    }
}