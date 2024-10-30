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

package com.ngapp.quottie.authors.list

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import com.ngapp.quottie.authors.list.state.AuthorsUiState
import com.ngapp.quottie.core.analytics.LocalAnalyticsHelper
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.analytics.logAuthorResourceOpened
import com.ngapp.quottie.core.desingsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.quottie.core.desingsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.quottie.core.desingsystem.component.scrollbar.scrollbarState
import com.ngapp.quottie.core.ui.AuthorResourceCard
import com.ngapp.quottie.core.ui.PagingGrid
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthorsRoute(
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthorsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthorsScreen(
        modifier = modifier,
        uiState = uiState,
        onAuthorClick = onAuthorClick,
    )
}

@Composable
private fun AuthorsScreen(
    modifier: Modifier,
    uiState: AuthorsUiState,
    onAuthorClick: (String) -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current

    when (uiState) {
        is AuthorsUiState.Loading -> {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is AuthorsUiState.Success -> {
            val authorsPaging by rememberUpdatedState(uiState.authors.collectAsLazyPagingItems())
            val state = rememberLazyGridState()

            Box(modifier = Modifier.fillMaxSize()) {
                PagingGrid(
                    modifier = modifier,
                    state = state,
                    lazyData = authorsPaging,
                    lazyContent = { author ->
                        AuthorResourceCard(
                            author = author,
                            onClick = {
                                analyticsHelper.logAuthorResourceOpened(
                                    authorId = author.id,
                                    authorName = author.name,
                                )
                                onAuthorClick(author.id)
                            },
                        )
                    }
                )
                val itemsAvailable = authorsPaging.itemCount
                val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)
                state.DraggableScrollbar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterEnd),
                    state = scrollbarState,
                    orientation = Orientation.Vertical,
                    onThumbMoved = state.rememberDraggableScroller(
                        itemsAvailable = itemsAvailable,
                    ),
                )
            }
        }
    }
    TrackScreenViewEvent(screenName = "AuthorsScreen")
}
