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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ngapp.quottie.authors.list.navigation.AuthorsNavigationRoute
import com.ngapp.quottie.authors.list.navigation.navigateToAuthors
import com.ngapp.quottie.bookmarks.navigation.navigateToBookmarks
import com.ngapp.quottie.home.navigation.HomeNavigationRoute
import com.ngapp.quottie.home.navigation.navigateToHome
import com.ngapp.quottie.navigation.TopLevelDestination
import com.ngapp.quottie.navigation.TopLevelDestination.AUTHORS
import com.ngapp.quottie.navigation.TopLevelDestination.BOOKMARKS
import com.ngapp.quottie.navigation.TopLevelDestination.HOME
import com.ngapp.quottie.navigation.TopLevelDestination.SETTINGS
import com.ngapp.quottie.onboarding.navigation.OnboardingNavigationRoute
import com.ngapp.quottie.search.navigation.navigateToSearch
import com.ngapp.quottie.settings.navigation.navigateToSettings

@Composable
fun rememberQuottieAppState(
    navController: NavHostController = rememberNavController(),
): QuottieAppState {
    return remember(navController) {
        QuottieAppState(navController)
    }
}

@Stable
class QuottieAppState(val navController: NavHostController) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) ?: false
            }
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            HOME -> navController.navigateToHome(topLevelNavOptions)
            AUTHORS -> navController.navigateToAuthors(topLevelNavOptions)
            BOOKMARKS -> navController.navigateToBookmarks(topLevelNavOptions)
            SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
        }
    }

    fun navigateToAuthorsFromBookmarks() = navController.navigate(AuthorsNavigationRoute) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    fun navigateToQuotesFromBookmarks() = navController.navigate(HomeNavigationRoute) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    fun navigateFromOnboardingToHomeScreen() = navController.navigate(HomeNavigationRoute) {
        popUpTo(OnboardingNavigationRoute) { inclusive = true }
    }

    fun navigateToSearch() = navController.navigateToSearch("")
}