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

package com.ngapp.quottie.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ngapp.quottie.settings.SettingsRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToSettings(navOptions: NavOptions) =
    navigate(route = SettingsNavigationRoute, navOptions)

fun NavGraphBuilder.settingsScreen(
    onNavigateTermsAndConditions: () -> Unit,
    onNavigatePrivacyPolicy: () -> Unit,
    onNavigateSoftwareLicense: () -> Unit,
    onNavigateAbout: () -> Unit,
) {
    composable<SettingsNavigationRoute> {
        SettingsRoute(
            onNavigateTermsAndConditions = onNavigateTermsAndConditions,
            onNavigatePrivacyPolicy = onNavigatePrivacyPolicy,
            onNavigateSoftwareLicense = onNavigateSoftwareLicense,
            onNavigateAbout = onNavigateAbout
        )
    }
}

@Serializable
data object SettingsNavigationRoute