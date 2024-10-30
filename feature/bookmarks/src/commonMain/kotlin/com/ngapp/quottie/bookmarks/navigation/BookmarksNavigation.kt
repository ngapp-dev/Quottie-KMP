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

package com.ngapp.quottie.bookmarks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ngapp.quottie.bookmarks.BookmarksRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToBookmarks(navOptions: NavOptions) =
    navigate(route = BookmarksNavigationRoute, navOptions)

fun NavGraphBuilder.bookmarksScreen(
    onNavigateSearch: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateAuthors: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<BookmarksNavigationRoute> {
        BookmarksRoute(
            onNavigateSearch = onNavigateSearch,
            onAuthorClick = onAuthorClick,
            onNavigateHome = onNavigateHome,
            onNavigateAuthors = onNavigateAuthors,
            onShowSnackbar = onShowSnackbar
        )
    }
}

@Serializable
data object BookmarksNavigationRoute