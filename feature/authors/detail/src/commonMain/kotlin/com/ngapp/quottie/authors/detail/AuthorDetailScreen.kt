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

package com.ngapp.quottie.authors.detail

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.authors.detail.state.AuthorDetailAction
import com.ngapp.quottie.authors.detail.state.AuthorDetailEvent
import com.ngapp.quottie.authors.detail.state.AuthorDetailUiState
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieCenterAlignedTopAppBar
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.quottie.core.desingsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.quottie.core.desingsystem.component.scrollbar.scrollbarState
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.ui.DetailHeaderImage
import com.ngapp.quottie.core.ui.ErrorView
import com.ngapp.quottie.core.ui.HorizontalDividerWithQuoteIcon
import com.ngapp.quottie.core.ui.PagingGrid
import com.ngapp.quottie.core.ui.QuoteResourceCard
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthorDetailRoute(
    onNavigateSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: AuthorDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<StringResource?>(null) }
    val bookmarkMessage = stringResource(SharedRes.strings.bookmark_removed)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthorDetailEvent.Error -> errorMessage = event.error
                is AuthorDetailEvent.Message -> onShowSnackbar(bookmarkMessage, null)
            }
        }
    }
    errorMessage?.let { error ->
        val message = stringResource(error)
        LaunchedEffect(message) {
            onShowSnackbar(message, null)
            errorMessage = null
        }
    }

    AuthorDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onTagClick = onNavigateSearch,
        onAction = viewModel::triggerAction,
    )
}

@Composable
private fun AuthorDetailScreen(
    modifier: Modifier,
    uiState: AuthorDetailUiState,
    onBackClick: () -> Unit,
    onTagClick: (String) -> Unit,
    onAction: (AuthorDetailAction) -> Unit,
) {
    when (uiState) {
        is AuthorDetailUiState.Loading -> Unit
        is AuthorDetailUiState.Error -> {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 56.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorView(
                    message = stringResource(uiState.error),
                    modifier = Modifier.fillMaxWidth(1f)
                )
            }
        }

        is AuthorDetailUiState.Success -> {
            val quotesPaging by rememberUpdatedState(uiState.authorQuotes.collectAsLazyPagingItems())
            val state = rememberLazyGridState()
            val author = uiState.author
            var isAuthorBookmarked by rememberSaveable { mutableStateOf(author.isBookmarked) }

            Column(modifier = Modifier.fillMaxSize()) {
                QuottieCenterAlignedTopAppBar(
                    title = stringResource(SharedRes.strings.destination_author_detail),
                    navigationIcon = QuottieIcons.ArrowBack,
                    navigationIconContentDescription = stringResource(SharedRes.strings.destination_navigate_back),
                    actionIcon = QuottieIcons.Share,
                    actionIconContentDescription = stringResource(SharedRes.strings.destination_share),
                    hasBookmarkAction = true,
                    isBookmarked = isAuthorBookmarked,
                    onToggleBookmark = {
                        isAuthorBookmarked = it
                        onAction(AuthorDetailAction.UpdateAuthorBookmark(author, it))
                    },
                    onActionClick = { onAction(AuthorDetailAction.ShareAuthor(author)) },
                    onNavigationClick = onBackClick,
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    PagingGrid(
                        modifier = modifier,
                        state = state,
                        lazyData = quotesPaging,
                        staticContent = {
                            item("headerImage") {
                                DetailHeaderImage(
                                    imageUrl = author.image,
                                    contentDescription = author.name,
                                )
                            }
                            item("content") {
                                Column {
                                    SelectionContainer {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            QuottieText(
                                                text = author.name,
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            QuottieText(
                                                text = author.description,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(Modifier.height(16.dp))
                                            QuottieText(
                                                text = author.bio,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                }
                            }
                            item("divider", span = { GridItemSpan(maxLineSpan) }) {
                                HorizontalDividerWithQuoteIcon()
                            }
                        },
                        lazyContent = { quote ->
                            var isBookmarked by rememberSaveable { mutableStateOf(quote.isBookmarked) }
                            QuoteResourceCard(
                                quote = quote,
                                isBookmarked = isBookmarked,
                                onToggleBookmark = {
                                    isBookmarked = it
                                    onAction(AuthorDetailAction.UpdateQuoteBookmark(quote, it))
                                },
                                onShareClick = { onAction(AuthorDetailAction.ShareQuote(quote)) },
                                onTagClick = onTagClick,
                            )
                        }
                    )
                    val itemsAvailable = quotesPaging.itemCount + 3
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
    }
    TrackScreenViewEvent(screenName = "AuthorDetailScreen")
}

