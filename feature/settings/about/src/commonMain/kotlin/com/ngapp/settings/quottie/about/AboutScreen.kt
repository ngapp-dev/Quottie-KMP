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

package com.ngapp.settings.quottie.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieCenterAlignedTopAppBar
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.ui.DetailHeaderImage
import com.ngapp.quottie.core.ui.ErrorView
import com.ngapp.settings.quottie.about.state.AboutEvent
import com.ngapp.settings.quottie.about.state.AboutUiState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AboutRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: AboutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var errorMessage by remember { mutableStateOf<StringResource?>(null) }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is AboutEvent.Error -> errorMessage = event.error
            }
        }
    }
    errorMessage?.let { error ->
        val message = stringResource(error)
        LaunchedEffect(message) {
            onShowSnackbar(message, null)
            errorMessage = null
        }
    }

    AboutScreen(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
    )
}

@Composable
private fun AboutScreen(
    modifier: Modifier,
    uiState: AboutUiState,
    onBackClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxSize()) {
        QuottieCenterAlignedTopAppBar(
            title = stringResource(SharedRes.strings.destination_about),
            navigationIcon = QuottieIcons.ArrowBack,
            navigationIconContentDescription = stringResource(SharedRes.strings.destination_navigate_back),
            onNavigationClick = onBackClick,
        )
        when (uiState) {
            is AboutUiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is AboutUiState.Error -> {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorView(
                        message = stringResource(uiState.error),
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
            }

            is AboutUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(16.dp))
                    DetailHeaderImage(
                        imageUrl = uiState.githubUser.avatarUrl,
                        contentDescription = uiState.githubUser.name,
                        clipShape = CircleShape,
                    )
                    Spacer(Modifier.height(18.dp))
                    QuottieText(
                        text = stringResource(SharedRes.strings.about_text_developed_by),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    QuottieText(
                        text = uiState.githubUser.name,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    QuottieText(
                        text = stringResource(SharedRes.strings.about_text_android_developer),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    QuottieText(
                        text = uiState.githubUser.htmlUrl,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable { uriHandler.openUri(uiState.githubUser.htmlUrl) }
                    )
                    Spacer(Modifier.height(18.dp))
                    QuottieText(
                        text = stringResource(SharedRes.strings.about_text_copyright_explanation),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "AboutScreen")
}