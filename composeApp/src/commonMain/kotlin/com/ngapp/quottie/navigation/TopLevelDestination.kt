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

import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.authors.list.navigation.AuthorsNavigationRoute
import com.ngapp.quottie.bookmarks.navigation.BookmarksNavigationRoute
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.home.navigation.HomeNavigationRoute
import com.ngapp.quottie.settings.navigation.SettingsNavigationRoute
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageResource,
    val unselectedIcon: ImageResource,
    val iconTextId: StringResource,
    val titleTextId: StringResource,
    val route: KClass<*>,
) {
    HOME(
        selectedIcon = QuottieIcons.FormatQuote,
        unselectedIcon = QuottieIcons.FormatQuoteBorder,
        iconTextId = SharedRes.strings.destination_quotes,
        titleTextId = SharedRes.strings.title_quote_of_the_day,
        route = HomeNavigationRoute::class,
    ),
    AUTHORS(
        selectedIcon = QuottieIcons.Group,
        unselectedIcon = QuottieIcons.GroupBorder,
        iconTextId = SharedRes.strings.destination_authors,
        titleTextId = SharedRes.strings.destination_authors,
        route = AuthorsNavigationRoute::class,
    ),
    BOOKMARKS(
        selectedIcon = QuottieIcons.Bookmark,
        unselectedIcon = QuottieIcons.BookmarkBorder,
        iconTextId = SharedRes.strings.destination_bookmarks,
        titleTextId = SharedRes.strings.destination_bookmarks,
        route = BookmarksNavigationRoute::class,
    ),
    SETTINGS(
        selectedIcon = QuottieIcons.Settings,
        unselectedIcon = QuottieIcons.SettingsBorder,
        iconTextId = SharedRes.strings.destination_settings,
        titleTextId = SharedRes.strings.destination_settings,
        route = SettingsNavigationRoute::class,
    ),
}