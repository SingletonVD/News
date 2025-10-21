package com.singletonv.news.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.singletonv.news.domain.entity.Interval
import com.singletonv.news.domain.entity.Language
import com.singletonv.news.domain.entity.Settings
import com.singletonv.news.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("language")
    private val intervalKey = intPreferencesKey("interval")
    private val notificationsEnabledKey = booleanPreferencesKey("notificationsEnabled")
    private val wifiOnlyKey = booleanPreferencesKey("wifiOnly")

    override fun getSettings(): Flow<Settings> {
        return context.dataStore.data.map { preferences ->
            val language = preferences[languageKey]?.let(Language::valueOf)
                ?: Settings.DEFAULT_LANGUAGE
            val interval = preferences[intervalKey]
                ?.let { intervalMinutes ->
                    Interval.entries.find { it.minutes == intervalMinutes }
                } ?: Settings.DEFAULT_INTERVAL
            val notificationsEnabled = preferences[notificationsEnabledKey]
                ?: Settings.DEFAULT_NOTIFICATIONS_ENABLED
            val wifiOnly = preferences[wifiOnlyKey]
                ?: Settings.DEFAULT_WIFI_ONLY

            Settings(
                language = language,
                interval = interval,
                notificationsEnabled = notificationsEnabled,
                wifiOnly = wifiOnly
            )
        }
    }

    override suspend fun updateLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language.name
        }
    }

    override suspend fun updateInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[intervalKey] = interval
        }
    }

    override suspend fun updateNotificationsEnabled(notificationsEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsEnabledKey] = notificationsEnabled
        }
    }

    override suspend fun updateWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[wifiOnlyKey] = wifiOnly
        }
    }
}