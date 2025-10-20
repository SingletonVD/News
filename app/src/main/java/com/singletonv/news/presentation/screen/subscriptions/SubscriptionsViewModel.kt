package com.singletonv.news.presentation.screen.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singletonv.news.domain.entity.Article
import com.singletonv.news.domain.usecase.AddSubscriptionUseCase
import com.singletonv.news.domain.usecase.ClearAllArticlesUseCase
import com.singletonv.news.domain.usecase.DeleteSubscriptionUseCase
import com.singletonv.news.domain.usecase.GetAllSubscriptionsUseCase
import com.singletonv.news.domain.usecase.GetArticlesByTopicsUseCase
import com.singletonv.news.domain.usecase.UpdateSubscribedArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val updateSubscribedArticlesUseCase: UpdateSubscribedArticlesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state
        get() = _state.asStateFlow()

    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    fun processCommand(command: SubscriptionsCommand) {
        when (command) {
            SubscriptionsCommand.ClearArticles -> {
                viewModelScope.launch {
                    val topics = state.value.selectedTopics
                    clearAllArticlesUseCase(topics)
                }
            }

            SubscriptionsCommand.ClickSubscribe -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        val topic = previousState.query.trim()
                        addSubscriptionUseCase(topic)
                        previousState.copy(query = "")
                    }
                }
            }

            is SubscriptionsCommand.InputTopic -> {
                _state.update { previousState ->
                    previousState.copy(query = command.query)
                }
            }

            SubscriptionsCommand.RefreshData -> {
                viewModelScope.launch {
                    updateSubscribedArticlesUseCase()
                }
            }

            is SubscriptionsCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    deleteSubscriptionUseCase(command.topic)
                }
            }

            is SubscriptionsCommand.ToggleTopicSelection -> {
                _state.update { previousState ->
                    val updatedSubscriptions = previousState.subscriptions.mapValues {
                        if (it.key == command.topic) !it.value
                        else it.value
                    }
                    previousState.copy(subscriptions = updatedSubscriptions)
                }
            }
        }
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase().onEach { subscriptions ->
            _state.update { previousState ->
                val updatedTopics = subscriptions.associateWith { topic ->
                    previousState.subscriptions[topic] ?: true
                }
                previousState.copy(subscriptions = updatedTopics)
            }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedTopics() {
        state.map { it.selectedTopics }
            .distinctUntilChanged()
            .flatMapLatest {
                getArticlesByTopicsUseCase(it)
            }
            .onEach {
                _state.update { previousState ->
                    previousState.copy(articles = it)
                }
            }.launchIn(viewModelScope)
    }
}

sealed interface SubscriptionsCommand {

    data class InputTopic(val query: String) : SubscriptionsCommand

    data object ClickSubscribe : SubscriptionsCommand

    data object RefreshData : SubscriptionsCommand

    data class ToggleTopicSelection(val topic: String) : SubscriptionsCommand

    data object ClearArticles : SubscriptionsCommand

    data class RemoveSubscription(val topic: String) : SubscriptionsCommand
}

data class SubscriptionsState(
    val query: String = "",
    val subscriptions: Map<String, Boolean> = mapOf(),
    val articles: List<Article> = listOf()
) {

    val subscribeButtonEnabled: Boolean
        get() = query.isNotBlank()

    val selectedTopics: List<String>
        get() = subscriptions.filter { it.value }.map { it.key }
}