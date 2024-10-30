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

package com.ngapp.settings.quottie.termsandconditions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieCenterAlignedTopAppBar
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import dev.icerock.moko.resources.compose.stringResource

private const val url = "https://quottie-kmp.web.app/termsandconditions.html"

@Composable
internal fun TermsAndConditionsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TermsAndConditionsScreen(
        modifier = modifier,
        onBackClick = onBackClick,
    )
}

@Composable
private fun TermsAndConditionsScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    val state = rememberWebViewState(url = url)
    state.webSettings.androidWebSettings.isAlgorithmicDarkeningAllowed = true

    Column(modifier = Modifier.fillMaxSize()) {
        QuottieCenterAlignedTopAppBar(
            title = stringResource(SharedRes.strings.settings_terms_and_conditions),
            navigationIcon = QuottieIcons.ArrowBack,
            navigationIconContentDescription = stringResource(SharedRes.strings.destination_navigate_back),
            onNavigationClick = onBackClick,
        )
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        WebView(state)
    }
    TrackScreenViewEvent(screenName = "TermsAndConditionsScreen")
}