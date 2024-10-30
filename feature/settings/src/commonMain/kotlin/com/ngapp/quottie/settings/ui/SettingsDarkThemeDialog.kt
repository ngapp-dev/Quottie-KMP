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

package com.ngapp.quottie.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.core.model.DarkThemeConfig.DARK
import com.ngapp.quottie.core.model.DarkThemeConfig.FOLLOW_SYSTEM
import com.ngapp.quottie.core.model.DarkThemeConfig.LIGHT
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun SettingsDarkThemeDialog(
    darkThemeConfig: DarkThemeConfig,
    onDismiss: () -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * usePlatformDefaultWidth = false is use as a temporary fix to allow
     * height recalculation during recomposition. This, however, causes
     * Dialog's to occupy full width in Compact mode. Therefore max width
     * is configured below. This should be removed when there's fix to
     * https://issuetracker.google.com/issues/221643630
     */
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.widthIn(max = 300.dp),
        onDismissRequest = { onDismiss() },
        title = {
            QuottieText(
                text = stringResource(SharedRes.strings.settings_dark_mode),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceTint
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .selectableGroup()
                ) {
                    SettingsDarkThemeDialogChooserRow(
                        text = stringResource(SharedRes.strings.settings_dark_mode_config_system_default),
                        selected = darkThemeConfig == FOLLOW_SYSTEM,
                        onClick = { onChangeDarkThemeConfig(FOLLOW_SYSTEM) },
                    )
                    SettingsDarkThemeDialogChooserRow(
                        text = stringResource(SharedRes.strings.settings_dark_mode_config_light),
                        selected = darkThemeConfig == LIGHT,
                        onClick = { onChangeDarkThemeConfig(LIGHT) },
                    )
                    SettingsDarkThemeDialogChooserRow(
                        text = stringResource(SharedRes.strings.settings_dark_mode_config_dark),
                        selected = darkThemeConfig == DARK,
                        onClick = { onChangeDarkThemeConfig(DARK) },
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@Composable
private fun SettingsDarkThemeDialogChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}