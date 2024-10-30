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

package com.ngapp.quottie.core.desingsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Quottie navigation suite scaffold with item and content slots.
 * Wraps Material 3 [NavigationSuiteScaffold].
 *
 * @param modifier Modifier to be applied to the navigation suite scaffold.
 * @param navigationSuiteItems A slot to display multiple items via [QuottieNavigationSuiteScope].
 * @param windowAdaptiveInfo The window adaptive info.
 * @param content The app content inside the scaffold.
 */
@Composable
fun QuottieNavigationSuiteScaffold(
    navigationSuiteItems: QuottieNavigationSuiteScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    adsContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = QuottieNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = QuottieNavigationDefaults.navigationUnselectedItemColor(),
            selectedTextColor = QuottieNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = QuottieNavigationDefaults.navigationUnselectedItemColor(),
            indicatorColor = Color.Transparent,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = QuottieNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = QuottieNavigationDefaults.navigationUnselectedItemColor(),
            selectedTextColor = QuottieNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = QuottieNavigationDefaults.navigationUnselectedItemColor(),
            indicatorColor = Color.Transparent,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = QuottieNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = QuottieNavigationDefaults.navigationUnselectedItemColor(),
            selectedTextColor = QuottieNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = QuottieNavigationDefaults.navigationUnselectedItemColor(),
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            QuottieNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = QuottieNavigationDefaults.navigationContainerColor(),
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = QuottieNavigationDefaults.navigationContainerColor(),
            navigationRailContainerColor = QuottieNavigationDefaults.navigationContainerColor(),
        ),
        modifier = modifier,
    ) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                adsContent()
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceTint
            )
        }
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
class QuottieNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = { if (selected) selectedIcon() else icon() },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

/**
 * Quottie navigation default values.
 */
object QuottieNavigationDefaults {
    @Composable
    fun navigationContainerColor() = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationUnselectedItemColor() = MaterialTheme.colorScheme.primary
}
