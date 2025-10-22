package com.singletonv.news.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singletonv.news.domain.entity.Interval
import com.singletonv.news.domain.entity.Language
import com.singletonv.news.domain.usecase.GetSettingsUseCase
import com.singletonv.news.domain.usecase.UpdateIntervalUseCase
import com.singletonv.news.domain.usecase.UpdateLanguageUseCase
import com.singletonv.news.domain.usecase.UpdateNotificationsEnabledUseCase
import com.singletonv.news.domain.usecase.UpdateWifiOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateIntervalUseCase: UpdateIntervalUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateWifiOnlyUseCase: UpdateWifiOnlyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Initial)
    val state
        get() = _state.asStateFlow()

    init {
        getSettingsUseCase().onEach { settings ->
            _state.update {
                SettingsState.Editing(
                    language = settings.language,
                    interval = settings.interval,
                    notificationsEnabled = settings.notificationsEnabled,
                    wifiOnly = settings.wifiOnly
                )
            }
        }.launchIn(viewModelScope)
    }

    fun processCommand(command: SettingsCommand) {
        when (command) {

            is SettingsCommand.UpdateNotificationsEnabled -> {
                viewModelScope.launch {
                    updateNotificationsEnabledUseCase(command.enabled)
                }
            }

            is SettingsCommand.UpdateWifiOnly -> {
                viewModelScope.launch {
                    updateWifiOnlyUseCase(command.enabled)
                }
            }

            is SettingsCommand.UpdateInterval -> {
                viewModelScope.launch {
                    updateIntervalUseCase(command.interval)
                }
            }

            is SettingsCommand.UpdateLanguage -> {
                viewModelScope.launch {
                    updateLanguageUseCase(command.language)
                }
            }
        }
    }
}

sealed interface SettingsState {

    data object Initial : SettingsState

    data class Editing(
        val language: Language,
        val interval: Interval,
        val notificationsEnabled: Boolean,
        val wifiOnly: Boolean,
        val languages: List<Language> = Language.entries,
        val intervals: List<Interval> = Interval.entries
    ) : SettingsState
}

sealed interface SettingsCommand {

    data class UpdateLanguage(val language: Language) : SettingsCommand

    data class UpdateInterval(val interval: Interval) : SettingsCommand

    data class UpdateNotificationsEnabled(val enabled: Boolean) : SettingsCommand

    data class UpdateWifiOnly(val enabled: Boolean) : SettingsCommand
}