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

package com.ngapp.quottie.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieButton
import com.ngapp.quottie.core.desingsystem.component.QuottieOnboardingTopAppBar
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.ui.PagerIndicator
import com.ngapp.quottie.onboarding.state.OnboardingAction
import com.ngapp.quottie.onboarding.state.OnboardingUiState
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingRoute(
    onDismissOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState) {
        OnboardingUiState.NotShown -> onDismissOnboarding()
        else -> {
            OnboardingScreen(
                modifier = modifier,
                onAction = viewModel::triggerAction,
            )
        }
    }
}

@Composable
private fun OnboardingScreen(
    modifier: Modifier,
    onAction: (OnboardingAction) -> Unit,
) {
    val pages = listOf(
        Pair(QuottieIcons.OnboardingLight1, QuottieIcons.OnboardingDark1),
        Pair(QuottieIcons.OnboardingLight2, QuottieIcons.OnboardingDark2),
        Pair(QuottieIcons.OnboardingLight3, QuottieIcons.OnboardingDark3),
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })

    Column(modifier = modifier.fillMaxSize()) {
        QuottieOnboardingTopAppBar(
            navigationIcon = QuottieIcons.ArrowBack,
            navigationIconContentDescription = stringResource(SharedRes.strings.destination_navigate_back),
            actionIconContentDescription = stringResource(SharedRes.strings.onboarding_button_button_skip),
            onNavigationClick = {
                if (pagerState.currentPage > 0) {
                    scope.launch { pagerState.scrollToPage(pagerState.currentPage - 1) }
                }
            },
            onSkipActionClick = {
                scope.launch { pagerState.scrollToPage(pages.lastIndex) }
            }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val onboarding = if (isSystemInDarkTheme()) pages[page].second else pages[page].first
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(onboarding),
                    contentDescription = stringResource(SharedRes.strings.onboarding_description_page_img),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                )
            }
        }
        BottomSection(size = pages.size, index = pagerState.currentPage) {
            if (pagerState.currentPage < pages.lastIndex) {
                scope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
            } else {
                onAction(OnboardingAction.DismissOnboarding)
            }
        }

        TrackScreenViewEvent(screenName = "OnboardingScreen")
    }
}

@Composable
private fun BottomSection(
    size: Int,
    index: Int,
    onButtonClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(size) { PagerIndicator(isSelected = it == index) }
        }

        QuottieButton(
            onClick = onButtonClick,
            modifier = Modifier.height(56.dp)
        ) {
            AnimatedVisibility(visible = index != 2) {
                Icon(
                    painter = painterResource(QuottieIcons.KeyboardArrowRight),
                    contentDescription = stringResource(SharedRes.strings.onboarding_description_next_icon)
                )
            }
            AnimatedVisibility(visible = index == 2) {
                Text(
                    text = stringResource(SharedRes.strings.onboarding_button_get_started),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 64.dp)
                )
            }
        }
    }
}

