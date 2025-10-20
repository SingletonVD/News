package com.singletonv.news.presentation.screen.subscriptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.singletonv.news.R
import com.singletonv.news.domain.entity.Article
import com.singletonv.news.presentation.ui.theme.CustomIcons
import com.singletonv.news.presentation.utils.formatDate

@Composable
fun SubscriptionsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SubscriptionsTopBar(
                onRefreshDataClick = {
                    viewModel.processCommand(SubscriptionsCommand.RefreshData)
                },
                onSettingsClick = onNavigateToSettings,
                onClearArticlesClick = {
                    viewModel.processCommand(SubscriptionsCommand.ClearArticles)
                }
            )
        }
    ) { innerPadding ->
        val state by viewModel.state.collectAsState()
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Subscriptions(
                    subscriptions = state.subscriptions,
                    query = state.query,
                    isSubscribeButtonEnabled = state.subscribeButtonEnabled,
                    onQueryChanged = { query ->
                        viewModel.processCommand(SubscriptionsCommand.InputTopic(query))
                    },
                    onTopicClick = { topic ->
                        viewModel.processCommand(SubscriptionsCommand.ToggleTopicSelection(topic))
                    },
                    onDeleteSubscription = { topic ->
                        viewModel.processCommand(SubscriptionsCommand.RemoveSubscription(topic))
                    },
                    onSubscriptionButtonClick = {
                        viewModel.processCommand(SubscriptionsCommand.ClickSubscribe)
                    },
                )
            }

            if (state.articles.isNotEmpty()) {
                item {
                    HorizontalDivider()
                }

                item {
                    Text(
                        text = stringResource(R.string.articles_label, state.articles.size),
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    HorizontalDivider()
                }

                items(
                    items = state.articles,
                    key = { it.url }
                ) {
                    ArticleCard(article = it)
                }
            } else if (state.subscriptions.isNotEmpty()) {
                item {
                    HorizontalDivider()
                }

                item {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.no_articles_for_selected_subscriptions),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionsTopBar(
    modifier: Modifier = Modifier,
    onRefreshDataClick: () -> Unit,
    onClearArticlesClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.subscriptions_title)
            )
        },
        actions = {
            Icon(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        onRefreshDataClick()
                    }
                    .padding(8.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.update_articles)
            )

            Icon(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        onClearArticlesClick()
                    }
                    .padding(8.dp),
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.clear_articles)
            )

            Icon(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        onSettingsClick()
                    }
                    .padding(8.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_screen)
            )
        }
    )
}

@Composable
private fun SubscriptionChip(
    modifier: Modifier = Modifier,
    topic: String,
    isSelected: Boolean,
    onSubscriptionClick: (String) -> Unit,
    onDeleteSubscriptionClick: (String) -> Unit
) {
    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = {
            onSubscriptionClick(topic)
        },
        label = {
            Text(topic)
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        onDeleteSubscriptionClick(topic)
                    },
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.remove_subscription)
            )
        }
    )
}

@Composable
private fun Subscriptions(
    modifier: Modifier = Modifier,
    subscriptions: Map<String, Boolean>,
    query: String,
    isSubscribeButtonEnabled: Boolean,
    onQueryChanged: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeleteSubscription: (String) -> Unit,
    onSubscriptionButtonClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChanged,
            label = {
                Text(text = stringResource(R.string.what_interests_you))
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSubscriptionButtonClick,
            enabled = isSubscribeButtonEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_subscription)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.add_subscription_button)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (subscriptions.isNotEmpty()) {
            Text(
                text = stringResource(R.string.subscriptions_label, subscriptions.size),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subscriptions.forEach { topic, isSelected ->
                    item(key = topic) {
                        SubscriptionChip(
                            topic = topic,
                            isSelected = isSelected,
                            onSubscriptionClick = onTopicClick,
                            onDeleteSubscriptionClick = onDeleteSubscription
                        )
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.no_subscriptions),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        article.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.image_for_article_label, article.title),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = article.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )

        if (article.description.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = article.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = article.sourceName,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = article.publishedAt.formatDate(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Icon(
                    imageVector = CustomIcons.OpenInNew,
                    contentDescription = stringResource(R.string.read_article_label, article.title)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.read))
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share_article_label, article.title)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.share))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}