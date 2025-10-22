package com.singletonv.news.presentation.screen.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.singletonv.news.R
import com.singletonv.news.presentation.mapper.toUiString

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            viewModel.processCommand(SettingsCommand.UpdateNotificationsEnabled(it))
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingsTopAppBar(
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        val state = viewModel.state.collectAsState()

        when (val currentState = state.value) {
            is SettingsState.Editing -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item {
                        Spacer(modifier = Modifier.padding(8.dp))
                        SettingsCard(
                            title = stringResource(R.string.search_language),
                            description = stringResource(R.string.select_language_for_news_search)
                        ) {
                            SettingsDropdown(
                                items = currentState.languages,
                                selectedItem = currentState.language,
                                onItemSelected = {
                                    viewModel.processCommand(SettingsCommand.UpdateLanguage(it))
                                },
                                itemAsString = { it.toUiString() }
                            )
                        }
                    }

                    item {
                        SettingsCard(
                            title = stringResource(R.string.update_interval),
                            description = stringResource(R.string.how_often_to_update_news),
                        ) {
                            SettingsDropdown(
                                items = currentState.intervals,
                                selectedItem = currentState.interval,
                                onItemSelected = {
                                    viewModel.processCommand(SettingsCommand.UpdateInterval(it))
                                },
                                itemAsString = { it.toUiString() }
                            )
                        }
                    }

                    item {
                        SettingsCard(
                            title = stringResource(R.string.notifications),
                            description = stringResource(R.string.show_notifications_about_new_articles)
                        ) {
                            Switch(
                                checked = currentState.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.processCommand(
                                            SettingsCommand.UpdateNotificationsEnabled(enabled)
                                        )
                                    }
                                }
                            )
                        }
                    }

                    item {
                        SettingsCard(
                            title = stringResource(R.string.update_only_via_wi_fi),
                            description = stringResource(R.string.save_mobile_data)
                        ) {
                            Switch(
                                checked = currentState.wifiOnly,
                                onCheckedChange = {
                                    viewModel.processCommand(
                                        SettingsCommand.UpdateWifiOnly(it)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            SettingsState.Initial -> {}
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.settings)
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onBackClick() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
    )
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    settingPicker: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            settingPicker()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SettingsDropdown(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemAsString: @Composable (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
            value = itemAsString(selectedItem),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = itemAsString(item))
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}