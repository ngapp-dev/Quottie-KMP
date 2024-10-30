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

package com.ngapp.quottie.bookmarks

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.bookmarks.state.BookmarksAction
import com.ngapp.quottie.bookmarks.state.BookmarksEvent
import com.ngapp.quottie.bookmarks.state.BookmarksTabs
import com.ngapp.quottie.bookmarks.state.BookmarksTabs.AUTHORS
import com.ngapp.quottie.bookmarks.state.BookmarksTabs.QUOTES
import com.ngapp.quottie.bookmarks.state.BookmarksUiState
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieTabRow
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.quottie.core.desingsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.quottie.core.desingsystem.component.scrollbar.scrollbarState
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.AuthorResourceCard
import com.ngapp.quottie.core.ui.QuoteResourceCard
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun BookmarksRoute(
    onNavigateSearch: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateAuthors: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bookmarkMessage = stringResource(SharedRes.strings.bookmark_removed)
    val undoText = stringResource(SharedRes.strings.bookmark_undo)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is BookmarksEvent.AuthorMessage -> {
                    val snackBarResult = onShowSnackbar(bookmarkMessage, undoText)
                    if (snackBarResult) {
                        viewModel.triggerAction(BookmarksAction.UndoAuthorBookmarkRemoval)
                    }
                }

                is BookmarksEvent.QuoteMessage -> {
                    val snackBarResult = onShowSnackbar(bookmarkMessage, undoText)
                    if (snackBarResult) {
                        viewModel.triggerAction(BookmarksAction.UndoQuoteBookmarkRemoval)
                    }
                }
            }
        }
    }

    BookmarksScreen(
        modifier = modifier,
        uiState = uiState,
        onAuthorClick = onAuthorClick,
        onNavigateAuthors = onNavigateAuthors,
        onNavigateHome = onNavigateHome,
        onTagClick = onNavigateSearch,
        onAction = viewModel::triggerAction,
    )
}

@Composable
private fun BookmarksScreen(
    modifier: Modifier,
    uiState: BookmarksUiState,
    onAuthorClick: (String) -> Unit,
    onNavigateAuthors: () -> Unit,
    onNavigateHome: () -> Unit,
    onTagClick: (String) -> Unit,
    onAction: (BookmarksAction) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(AUTHORS) }
    val pagerState = rememberPagerState(
        pageCount = { BookmarksTabs.entries.size },
        initialPage = selectedTab.ordinal
    )
    val coroutineScope = rememberCoroutineScope()

    when (uiState) {
        is BookmarksUiState.Loading -> Unit
        is BookmarksUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                val tabsName = remember { BookmarksTabs.entries.map { it.titleResId } }
                QuottieTabRow(selectedTabIndex = pagerState.currentPage) {
                    tabsName.forEachIndexed { index, titleResId ->
                        val tab = if (titleResId == AUTHORS.titleResId) AUTHORS else QUOTES
                        Tab(
                            modifier = Modifier.wrapContentWidth(),
                            selected = index == pagerState.currentPage,
                            onClick = {
                                selectedTab = tab
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                QuottieText(
                                    text = stringResource(titleResId),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (index == pagerState.currentPage) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top,
                ) { page ->

                    Box(modifier = Modifier.fillMaxSize()) {
                        val state = rememberLazyGridState()
                        LazyVerticalGrid(
                            state = state,
                            modifier = modifier
                                .fillMaxSize()
                                .animateContentSize(),
                            columns = GridCells.Adaptive(300.dp),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            when (page) {
                                AUTHORS.ordinal -> authorsPage(
                                    authors = uiState.authors,
                                    onNavigateAuthors = onNavigateAuthors,
                                    onClick = onAuthorClick
                                )

                                QUOTES.ordinal -> quotesPage(
                                    quotes = uiState.quotes,
                                    onTagClick = onTagClick,
                                    onNavigateHome = onNavigateHome,
                                    onAction = onAction,
                                )
                            }
                        }
                        val itemsAvailable =
                            if (selectedTab == AUTHORS) uiState.authors.size else uiState.quotes.size
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

                LaunchedEffect(pagerState.currentPage) {
                    selectedTab = if (pagerState.currentPage == 0) AUTHORS else QUOTES
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "BookmarksScreen")
}

private fun LazyGridScope.authorsPage(
    authors: List<AuthorResource>,
    onNavigateAuthors: () -> Unit,
    onClick: (String) -> Unit,
) {
    if (authors.isEmpty()) {
        item {
            val addBookmarksString = buildAnnotatedString {
                append(stringResource(SharedRes.strings.bookmark_empty))
                append("\n")
                append(stringResource(SharedRes.strings.bookmark_no_content_start))
                append(" ")
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "",
                        linkInteractionListener = { onNavigateAuthors() },
                    ),
                ) {
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(stringResource(SharedRes.strings.destination_authors))
                    }
                }

                append(" ")
                append(stringResource(SharedRes.strings.bookmark_no_content_end))
            }
            QuottieText(
                annotatedString = addBookmarksString,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        items(authors, key = { it.id }) { author ->
            var isBookmark by rememberSaveable { mutableStateOf(author.isBookmarked) }
            LaunchedEffect(author.isBookmarked) {
                isBookmark = author.isBookmarked
            }
            AuthorResourceCard(
                author = author,
                onClick = { onClick(author.id) },
            )
        }
    }
}

private fun LazyGridScope.quotesPage(
    quotes: List<QuoteResource>,
    onTagClick: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onAction: (BookmarksAction) -> Unit,
) {
    if (quotes.isEmpty()) {
        item {
            val addBookmarksString = buildAnnotatedString {
                append(stringResource(SharedRes.strings.bookmark_empty))
                append("\n")
                append(stringResource(SharedRes.strings.bookmark_no_content_start))
                append(" ")
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "",
                        linkInteractionListener = { onNavigateHome() },
                    ),
                ) {
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(stringResource(SharedRes.strings.destination_quotes))
                    }
                }

                append(" ")
                append(stringResource(SharedRes.strings.bookmark_no_content_end))
            }
            QuottieText(
                annotatedString = addBookmarksString,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        items(quotes, key = { it.id }) { quote ->
            var isBookmarked by rememberSaveable { mutableStateOf(quote.isBookmarked) }
            LaunchedEffect(quote.isBookmarked) {
                isBookmarked = quote.isBookmarked
            }
            QuoteResourceCard(
                quote = quote,
                isBookmarked = isBookmarked,
                onToggleBookmark = {
                    isBookmarked = it
                    onAction(BookmarksAction.UpdateQuoteBookmark(quote, it))
                },
                onShareClick = { onAction(BookmarksAction.ShareQuote(quote)) },
                onTagClick = onTagClick
            )
        }
    }
}

