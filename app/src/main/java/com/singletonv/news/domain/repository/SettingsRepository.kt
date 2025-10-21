package com.singletonv.news.domain.repository

import com.singletonv.news.domain.entity.Language
import com.singletonv.news.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSettings(): Flow<Settings>

    suspend fun updateLanguage(language: Language)

    suspend fun updateInterval(interval: Int)

    suspend fun updateNotificationsEnabled(notificationsEnabled: Boolean)

    suspend fun updateWifiOnly(wifiOnly: Boolean)
}