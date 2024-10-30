/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ngapp.quottie.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.core.model.DarkThemeConfig.DARK
import com.ngapp.quottie.core.model.DarkThemeConfig.LIGHT
import com.ngapp.quottie.core.model.DarkThemeConfig.FOLLOW_SYSTEM
import com.ngapp.quottie.settings.state.SettingsAction
import com.ngapp.quottie.settings.state.SettingsUiState
import com.ngapp.quottie.settings.ui.SettingsDarkThemeDialog
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsRoute(
    onNavigateTermsAndConditions: () -> Unit,
    onNavigatePrivacyPolicy: () -> Unit,
    onNavigateSoftwareLicense: () -> Unit,
    onNavigateAbout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        modifier = modifier,
        uiState = uiState,
        onNavigateTermsAndConditions = onNavigateTermsAndConditions,
        onNavigatePrivacyPolicy = onNavigatePrivacyPolicy,
        onNavigateSoftwareLicense = onNavigateSoftwareLicense,
        onNavigateAbout = onNavigateAbout,
        onAction = viewModel::triggerAction,
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier,
    uiState: SettingsUiState,
    onNavigateTermsAndConditions: () -> Unit,
    onNavigatePrivacyPolicy: () -> Unit,
    onNavigateSoftwareLicense: () -> Unit,
    onNavigateAbout: () -> Unit,
    onAction: (SettingsAction) -> Unit,
) {
    var showDarkThemeDialog by rememberSaveable { mutableStateOf(false) }

    when (uiState) {
        is SettingsUiState.Loading -> {}
        is SettingsUiState.Success -> {
            if (showDarkThemeDialog) {
                SettingsDarkThemeDialog(
                    darkThemeConfig = uiState.darkThemeConfig,
                    onDismiss = { showDarkThemeDialog = false },
                    onChangeDarkThemeConfig = {
                        onAction(SettingsAction.UpdateDarkThemeConfig(it))
                        showDarkThemeDialog = false
                    }
                )
            }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsSectionTitle(text = stringResource(SharedRes.strings.settings_appearance))
                SettingsDarkThemeRow(
                    text = stringResource(SharedRes.strings.settings_dark_mode),
                    darkThemeConfig = uiState.darkThemeConfig,
                    onClick = { showDarkThemeDialog = true }
                )
                SettingsSectionTitle(text = stringResource(SharedRes.strings.settings_legal))
                SettingsSectionItem(
                    text = stringResource(SharedRes.strings.settings_terms_and_conditions),
                    onClick = onNavigateTermsAndConditions
                )
                SettingsSectionItem(
                    text = stringResource(SharedRes.strings.settings_privacy_policy),
                    onClick = onNavigatePrivacyPolicy
                )
                SettingsSectionItem(
                    text = stringResource(SharedRes.strings.settings_software_license),
                    onClick = onNavigateSoftwareLicense
                )
                SettingsSectionTitle(text = stringResource(SharedRes.strings.settings_support))
                SettingsSectionItem(
                    text = stringResource(SharedRes.strings.settings_about),
                    onClick = onNavigateAbout
                )
            }
        }
    }
    TrackScreenViewEvent(screenName = "SettingsScreen")
}

@Composable
private fun SettingsSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    QuottieText(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(16.dp)
    )
}

@Composable
private fun SettingsDarkThemeRow(
    text: String,
    darkThemeConfig: DarkThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeName = when (darkThemeConfig) {
        FOLLOW_SYSTEM -> SharedRes.strings.settings_dark_mode_config_system_default
        LIGHT -> SharedRes.strings.settings_dark_mode_config_light
        DARK -> SharedRes.strings.settings_dark_mode_config_dark
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuottieText(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        QuottieText(
            text = stringResource(themeName),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SettingsSectionItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuottieText(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Icon(
            painter = painterResource(QuottieIcons.ArrowForward),
            contentDescription = "Arrow"
        )
    }
}
