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

package com.ngapp.settings.quottie.termsandconditions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.ngapp.settings.quottie.termsandconditions.TermsAndConditionsRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToTermsAndConditions(navOptions: NavOptionsBuilder.() -> Unit = {}) =
    navigate(route = TermsAndConditionsNavigationRoute) { navOptions() }

fun NavGraphBuilder.termsAndConditionsScreen(
    onBackClick: () -> Unit,
) {
    composable<TermsAndConditionsNavigationRoute> {
        TermsAndConditionsRoute(onBackClick = onBackClick)
    }
}

@Serializable
data object TermsAndConditionsNavigationRoute