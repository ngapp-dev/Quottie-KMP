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

package com.ngapp.quottie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.ngapp.quottie.authors.detail.navigation.authorDetailScreen
import com.ngapp.quottie.authors.detail.navigation.navigateToAuthorDetail
import com.ngapp.quottie.authors.list.navigation.authorsScreen
import com.ngapp.quottie.authors.list.navigation.navigateToAuthors
import com.ngapp.quottie.bookmarks.navigation.bookmarksScreen
import com.ngapp.quottie.home.navigation.homeScreen
import com.ngapp.quottie.home.navigation.navigateToHome
import com.ngapp.quottie.onboarding.navigation.onboardingScreen
import com.ngapp.quottie.search.navigation.navigateToSearch
import com.ngapp.quottie.search.navigation.searchScreen
import com.ngapp.quottie.settings.navigation.settingsScreen
import com.ngapp.quottie.ui.QuottieAppState
import com.ngapp.settings.quottie.about.navigation.aboutScreen
import com.ngapp.settings.quottie.about.navigation.navigateToAbout
import com.ngapp.settings.quottie.privacypolicy.navigation.navigateToPrivacyPolicy
import com.ngapp.settings.quottie.privacypolicy.navigation.privacyPolicyScreen
import com.ngapp.settings.quottie.softwarelicense.navigation.navigateToSoftwareLicense
import com.ngapp.settings.quottie.softwarelicense.navigation.softwareLicenseScreen
import com.ngapp.settings.quottie.termsandconditions.navigation.navigateToTermsAndConditions
import com.ngapp.settings.quottie.termsandconditions.navigation.termsAndConditionsScreen
import kotlin.reflect.KClass

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun QuottieNavHost(
    appState: QuottieAppState,
    startDestination: KClass<*>,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        onboardingScreen(appState::navigateFromOnboardingToHomeScreen)
        homeScreen(navController::navigateToSearch, onShowSnackbar)
        authorsScreen(navController::navigateToAuthorDetail)
        authorDetailScreen(
            navController::navigateToSearch,
            navController::navigateUp,
            onShowSnackbar,
        )
        bookmarksScreen(
            navController::navigateToSearch,
            navController::navigateToAuthorDetail,
            appState::navigateToQuotesFromBookmarks,
            appState::navigateToAuthorsFromBookmarks,
            onShowSnackbar,
        )
        settingsScreen(
            navController::navigateToTermsAndConditions,
            navController::navigateToPrivacyPolicy,
            navController::navigateToSoftwareLicense,
            navController::navigateToAbout,
        )
        searchScreen(
            navController::navigateToAuthorDetail,
            navController::navigateUp,
            onShowSnackbar,
        )
        aboutScreen(navController::navigateUp, onShowSnackbar)
        termsAndConditionsScreen(navController::navigateUp)
        privacyPolicyScreen(navController::navigateUp)
        softwareLicenseScreen(navController::navigateUp)
    }
}
