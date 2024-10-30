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

package com.ngapp.settings.quottie.softwarelicense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieCenterAlignedTopAppBar
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun SoftwareLicenseRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SoftwareLicenseScreen(
        modifier = modifier,
        onBackClick = onBackClick,
    )
}

@Composable
private fun SoftwareLicenseScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val googleAnalyticsLink = stringResource(SharedRes.strings.settings_text_google_analytics_link)
    val googleAdmobLink = stringResource(SharedRes.strings.settings_text_google_admob_link)
    val apacheLink = stringResource(SharedRes.strings.settings_text_apache_link)

    Column(modifier = modifier.fillMaxSize()) {
        QuottieCenterAlignedTopAppBar(
            title = stringResource(SharedRes.strings.settings_software_license),
            navigationIcon = QuottieIcons.ArrowBack,
            navigationIconContentDescription = stringResource(SharedRes.strings.destination_navigate_back),
            onNavigationClick = onBackClick,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))
            QuottieText(
                text = stringResource(SharedRes.strings.settings_text_confirmation),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(6.dp))
            QuottieText(
                text = stringResource(SharedRes.strings.settings_text_application_uses),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(12.dp))
            QuottieText(
                text = stringResource(SharedRes.strings.settings_text_google_analytics),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(6.dp))
            QuottieText(
                text = googleAnalyticsLink,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { uriHandler.openUri(googleAnalyticsLink) }
            )
            Spacer(Modifier.height(12.dp))
            QuottieText(
                text = stringResource(SharedRes.strings.settings_text_google_admob),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(6.dp))
            QuottieText(
                text = googleAdmobLink,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { uriHandler.openUri(googleAdmobLink) }
            )
            Spacer(Modifier.height(12.dp))
            QuottieText(
                text = stringResource(SharedRes.strings.settings_text_apache),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(6.dp))
            QuottieText(
                text = apacheLink,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { uriHandler.openUri(apacheLink) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
    TrackScreenViewEvent(screenName = "SoftwareLicenseScreen")
}