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
import kotlinx.coroutines.flow.first
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
        viewModelScope.launch {
            _state.value = getSettingsUseCase().first().run {
                SettingsState.Editing(
                    language = language,
                    interval = interval,
                    notificationsEnabled = notificationsEnabled,
                    wifiOnly = wifiOnly
                )
            }
        }
    }

    fun processCommand(command: SettingsCommand) {
        when (command) {
            SettingsCommand.Back -> {
                _state.value = SettingsState.Finished
            }

            SettingsCommand.SwitchNotificationsEnabled -> {
                _state.update { previousState ->
                    if (previousState is SettingsState.Editing) {
                        val updatedNotificationsEnabled = !previousState.notificationsEnabled
                        viewModelScope.launch {
                            updateNotificationsEnabledUseCase(updatedNotificationsEnabled)
                        }
                        previousState.copy(notificationsEnabled = updatedNotificationsEnabled)
                    } else {
                        previousState
                    }
                }
            }

            SettingsCommand.SwitchWifiOnly -> {
                _state.update { previousState ->
                    if (previousState is SettingsState.Editing) {
                        val updatedWifiOnly = !previousState.wifiOnly
                        viewModelScope.launch {
                            updateWifiOnlyUseCase(updatedWifiOnly)
                        }
                        previousState.copy(wifiOnly = updatedWifiOnly)
                    } else {
                        previousState
                    }
                }
            }

            is SettingsCommand.UpdateInterval -> {
                _state.update { previousState ->
                    if (previousState is SettingsState.Editing) {
                        viewModelScope.launch {
                            updateIntervalUseCase(command.interval)
                        }
                        previousState.copy(interval = command.interval)
                    } else {
                        previousState
                    }
                }
            }

            is SettingsCommand.UpdateLanguage -> {
                _state.update { previousState ->
                    if (previousState is SettingsState.Editing) {
                        viewModelScope.launch {
                            updateLanguageUseCase(command.language)
                        }
                        previousState.copy(language = command.language)
                    } else {
                        previousState
                    }
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
        val wifiOnly: Boolean
    ) : SettingsState

    data object Finished : SettingsState
}

sealed interface SettingsCommand {

    data class UpdateLanguage(val language: Language) : SettingsCommand

    data class UpdateInterval(val interval: Interval) : SettingsCommand

    data object SwitchNotificationsEnabled : SettingsCommand

    data object SwitchWifiOnly : SettingsCommand

    data object Back : SettingsCommand
}