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

package com.ngapp.quottie.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ngapp.quottie.ConsentState
import com.ngapp.quottie.MainActivityUiState
import com.ngapp.quottie.MainActivityUiState.Loading
import com.ngapp.quottie.MainActivityUiState.Success
import com.ngapp.quottie.MainActivityViewModel
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.desingsystem.component.QuottieHomeTopAppBar
import com.ngapp.quottie.core.desingsystem.component.QuottieNavigationSuiteScaffold
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.component.QuottieTopAppBar
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.ui.ads.LoadBannerAd
import com.ngapp.quottie.home.navigation.HomeNavigationRoute
import com.ngapp.quottie.navigation.QuottieNavHost
import com.ngapp.quottie.navigation.TopLevelDestination
import com.ngapp.quottie.onboarding.navigation.OnboardingNavigationRoute
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

@Composable
fun QuottieApp(
    appState: QuottieAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    viewModel: MainActivityViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val consentState by viewModel.consentState.collectAsStateWithLifecycle()

    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxSize(),
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            val snackbarHostState = remember { SnackbarHostState() }
            QuottieApp(
                appState = appState,
                startDestination = getStartDestination(uiState),
                snackbarHostState = snackbarHostState,
                consentState = consentState,
                windowAdaptiveInfo = windowAdaptiveInfo,
            )
        }
    }
}

@Composable
internal fun QuottieApp(
    appState: QuottieAppState,
    startDestination: KClass<*>,
    snackbarHostState: SnackbarHostState,
    consentState: ConsentState,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val currentDestination: NavDestination? = appState.currentDestination
    val currentTopLevelDestination: TopLevelDestination? = appState.currentTopLevelDestination

    if (currentTopLevelDestination != null) {
        QuottieNavigationSuiteScaffold(
            navigationSuiteItems = {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isRouteInHierarchy(destination.route)
                    item(
                        selected = selected,
                        onClick = { appState.navigateToTopLevelDestination(destination) },
                        icon = {
                            Icon(
                                painter = painterResource(destination.unselectedIcon),
                                contentDescription = null,
                            )
                        },
                        selectedIcon = {
                            Icon(
                                painter = painterResource(destination.selectedIcon),
                                contentDescription = null,
                            )
                        },
                        label = { QuottieText(text = stringResource(destination.iconTextId)) },
                        modifier = Modifier.testTag("QuottieNavItem")
                    )
                }
            },
            windowAdaptiveInfo = windowAdaptiveInfo,
            adsContent = {
                if (consentState.canShowAds) {
                    LoadBannerAd()
                }
            }
        ) {
            DestinationScaffold(appState, startDestination, snackbarHostState)
        }
    } else {
        DestinationScaffold(appState, startDestination, snackbarHostState)
    }
}

@Composable
private fun DestinationScaffold(
    appState: QuottieAppState,
    startDestination: KClass<*>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            val destination = appState.currentTopLevelDestination
            var shouldShowTopAppBar = false

            if (destination != null) {
                shouldShowTopAppBar = true
                if (destination == TopLevelDestination.HOME) {
                    QuottieHomeTopAppBar(
                        actionIcon = QuottieIcons.SearchBorder,
                        actionIconContentDescription = stringResource(
                            SharedRes.strings.destination_search,
                        ),
                        onActionClick = { appState.navigateToSearch() },
                    )
                } else {
                    QuottieTopAppBar(
                        title = stringResource(destination.titleTextId),
                        actionIcon = if (destination != TopLevelDestination.SETTINGS) QuottieIcons.SearchBorder else null,
                        actionIconContentDescription = stringResource(
                            SharedRes.strings.destination_search,
                        ),
                        onActionClick = { appState.navigateToSearch() },
                    )
                }
            }
            Box(
                // Workaround for https://issuetracker.google.com/338478720
                modifier = Modifier.consumeWindowInsets(
                    if (shouldShowTopAppBar) {
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    } else {
                        WindowInsets(0, 0, 0, 0)
                    },
                ),
            ) {
                QuottieNavHost(
                    appState = appState,
                    onShowSnackbar = { message, action ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = action,
                            duration = Short,
                        ) == ActionPerformed
                    },
                    startDestination = startDestination,
                )
            }
        }
    }
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun getStartDestination(uiState: MainActivityUiState): KClass<*> =
    when (uiState) {
        Loading -> HomeNavigationRoute::class
        is Success -> when (uiState.userData.shouldHideOnboarding) {
            true -> HomeNavigationRoute::class
            else -> OnboardingNavigationRoute::class
        }
    }

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false


