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

package com.ngapp.settings.quottie.privacypolicy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieCenterAlignedTopAppBar
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.settings.quottie.privacypolicy.state.PrivacyPolicyAction
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val url = "https://quottie-kmp.web.app/privacypolicy.html"

@Composable
internal fun PrivacyPolicyRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacyPolicyViewModel = koinViewModel(),
) {
    val isPrivacyOptionsRequired by viewModel.isPrivacyOptionsRequired.collectAsStateWithLifecycle()

    PrivacyPolicyScreen(
        modifier = modifier,
        isPrivacyOptionsRequired = isPrivacyOptionsRequired,
        onBackClick = onBackClick,
        onUpdateConsent = { viewModel.triggerAction(PrivacyPolicyAction.UpdateConsent) },
    )
}

@Composable
private fun PrivacyPolicyScreen(
    modifier: Modifier,
    isPrivacyOptionsRequired: Boolean,
    onBackClick: () -> Unit,
    onUpdateConsent: () -> Unit,
) {
    val state = rememberWebViewState(url = url)
    state.webSettings.androidWebSettings.isAlgorithmicDarkeningAllowed = true

    Column(modifier = Modifier.fillMaxSize()) {
        QuottieCenterAlignedTopAppBar(
            title = stringResource(SharedRes.strings.settings_privacy_policy),
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
        if (isPrivacyOptionsRequired) {
            ConsentChangeLink(onUpdateConsent)
        }
        WebView(state)
    }
    TrackScreenViewEvent(screenName = "PrivacyPolicyScreen")
}

@Composable
private fun ConsentChangeLink(onUpdateConsent: () -> Unit) {
    Text(
        buildAnnotatedString {
            append(stringResource(SharedRes.strings.change_consent))
            withLink(
                LinkAnnotation.Clickable(
                    linkInteractionListener = { onUpdateConsent() },
                    styles = TextLinkStyles(
                        SpanStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp,
                            fontFamily = fontFamilyResource(SharedRes.fonts.manrope_regular),
                            color = MaterialTheme.colorScheme.primary
                        )
                    ),
                    tag = ""
                )
            ) {
                append(stringResource(SharedRes.strings.here))
            }
        },
        modifier = Modifier.padding(horizontal = 24.dp),
        style = MaterialTheme.typography.bodyMedium,
    )
}