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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.ngapp.quottie.core.desingsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuottieCenterAlignedTopAppBar(
    title: String,
    navigationIcon: ImageResource? = null,
    navigationIconContentDescription: String = "",
    actionIcon: ImageResource? = null,
    actionIconContentDescription: String = "",
    hasBookmarkAction: Boolean = false,
    isBookmarked: Boolean = false,
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    onToggleBookmark: (Boolean) -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { QuottieText(text = title) },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        painter = painterResource(navigationIcon),
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        actions = {
            if (hasBookmarkAction) {
                QuottieIconToggleButton(
                    checked = isBookmarked,
                    onCheckedChange = onToggleBookmark,
                    modifier = modifier,
                    icon = {
                        Icon(
                            painter = painterResource(QuottieIcons.BookmarkBorder),
                            contentDescription = "",
                        )
                    },
                    checkedIcon = {
                        Icon(
                            painter = painterResource(QuottieIcons.Bookmark),
                            contentDescription = "",
                        )
                    },
                )
            }
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        painter = painterResource(actionIcon),
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = QuottieTopAppBarDefaults.topAppBarContainerColor()
        ),
        modifier = modifier.testTag("QuottieCenterAlignedTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuottieTopAppBar(
    title: String,
    actionIcon: ImageResource? = null,
    actionIconContentDescription: String = "",
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            QuottieText(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        painter = painterResource(actionIcon),
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = QuottieTopAppBarDefaults.topAppBarContainerColor()
        ),
        modifier = modifier.testTag("QuottieTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuottieHomeTopAppBar(
    actionIcon: ImageResource? = null,
    actionIconContentDescription: String = "",
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                Image(
                    painter = painterResource(QuottieIcons.LogoLong),
                    contentDescription = stringResource(SharedRes.strings.title_quottie),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        },
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        painter = painterResource(actionIcon),
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = QuottieTopAppBarDefaults.topAppBarContainerColor()
        ),
        modifier = modifier.testTag("QuottieTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuottieOnboardingTopAppBar(
    navigationIcon: ImageResource,
    navigationIconContentDescription: String,
    actionIconContentDescription: String,
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit,
    onSkipActionClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    painter = painterResource(navigationIcon),
                    contentDescription = navigationIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            TextButton(onClick = onSkipActionClick) {
                Text(
                    text = actionIconContentDescription,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = QuottieTopAppBarDefaults.topAppBarContainerColor()
        ),
        modifier = modifier.testTag("QuottieOnboardingTopAppBar"),
    )
}

/**
 * Quottie top app bar default values.
 */
object QuottieTopAppBarDefaults {
    @Composable
    fun topAppBarContainerColor() = MaterialTheme.colorScheme.surface
}