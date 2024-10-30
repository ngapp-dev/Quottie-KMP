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

package com.ngapp.quottie.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieTabRow
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.quottie.core.desingsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.quottie.core.desingsystem.component.scrollbar.scrollbarState
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.AuthorResourceCard
import com.ngapp.quottie.core.ui.PagingGrid
import com.ngapp.quottie.core.ui.QuoteResourceCard
import com.ngapp.quottie.core.ui.QuottieSearchTextField
import com.ngapp.quottie.search.state.RecentSearchQueriesUiState
import com.ngapp.quottie.search.state.SearchAction
import com.ngapp.quottie.search.state.SearchEvent
import com.ngapp.quottie.search.state.SearchResultUiState
import com.ngapp.quottie.search.state.SearchTabs
import com.ngapp.quottie.search.state.SearchTabs.AUTHORS
import com.ngapp.quottie.search.state.SearchTabs.QUOTES
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SearchRoute(
    onAuthorClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val recentSearchQueriesUiState by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var errorMessage by remember { mutableStateOf<StringResource?>(null) }
    val bookmarkMessage = stringResource(SharedRes.strings.bookmark_removed)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is SearchEvent.Error -> errorMessage = event.error
                is SearchEvent.Message -> onShowSnackbar(bookmarkMessage, null)
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

    SearchScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        recentSearchesUiState = recentSearchQueriesUiState,
        searchResultUiState = searchResultUiState,
        onSearchQueryChanged = { viewModel.triggerAction(SearchAction.SearchQueryChanged(it)) },
        onSearchTriggered = { viewModel.triggerAction(SearchAction.SearchTriggered(it)) },
        onClearRecentSearches = { viewModel.triggerAction(SearchAction.ClearRecentSearches) },
        onAuthorClick = onAuthorClick,
        onBackClick = onBackClick,
        onAction = viewModel::triggerAction,
    )
}

@Composable
private fun SearchScreen(
    modifier: Modifier,
    searchQuery: String = "",
    recentSearchesUiState: RecentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
    searchResultUiState: SearchResultUiState = SearchResultUiState.Loading,
    onSearchQueryChanged: (String) -> Unit = {},
    onSearchTriggered: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onAuthorClick: (String) -> Unit,
    onAction: (SearchAction) -> Unit,
) {
    TrackScreenViewEvent(screenName = "SearchScreen")
    Column(modifier = modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        SearchToolbar(
            onBackClick = onBackClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
        when (searchResultUiState) {
            SearchResultUiState.Loading -> Unit
            SearchResultUiState.EmptyQuery -> {
                if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                    RecentSearchesBody(
                        onClearRecentSearches = onClearRecentSearches,
                        onRecentSearchClicked = {
                            onSearchQueryChanged(it)
                            onSearchTriggered(it)
                        },
                        recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                    )
                }
            }

            is SearchResultUiState.Success -> {
                val authorsPaging by rememberUpdatedState(searchResultUiState.authors.collectAsLazyPagingItems())
                val quotesPaging by rememberUpdatedState(searchResultUiState.quotes.collectAsLazyPagingItems())

                SearchResultBody(
                    modifier = modifier,
                    authorsPaging = authorsPaging,
                    quotesPaging = quotesPaging,
                    onAuthorClick = onAuthorClick,
                    onAction = onAction,
                )
            }
        }
    }
    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
}

@Composable
private fun SearchResultBody(
    modifier: Modifier,
    authorsPaging: LazyPagingItems<AuthorResource>,
    quotesPaging: LazyPagingItems<QuoteResource>,
    onAuthorClick: (String) -> Unit,
    onAction: (SearchAction) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(AUTHORS) }
    val pagerState = rememberPagerState(
        pageCount = { SearchTabs.entries.size },
        initialPage = selectedTab.ordinal
    )
    val coroutineScope = rememberCoroutineScope()
    val state = rememberLazyGridState()

    Column {
        val tabsName = rememberSaveable { SearchTabs.entries.map { it.titleResId } }
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
                PagingGrid(
                    modifier = modifier,
                    state = state,
                    lazyData = if (page == AUTHORS.ordinal) authorsPaging else quotesPaging,
                    lazyContent = { result ->
                        when (page) {
                            AUTHORS.ordinal -> {
                                result as AuthorResource
                                AuthorResourceCard(
                                    author = result,
                                    onClick = { onAuthorClick(result.id) },
                                )
                            }

                            QUOTES.ordinal -> {
                                result as QuoteResource
                                var isBookmark by rememberSaveable { mutableStateOf(result.isBookmarked) }
                                QuoteResourceCard(
                                    quote = result,
                                    isBookmarked = isBookmark,
                                    onToggleBookmark = {
                                        isBookmark = it
                                        onAction(SearchAction.UpdateQuoteBookmark(result, it))
                                    },
                                    onShareClick = {},
                                    onTagClick = {}
                                )
                            }
                        }
                    }
                )
                val itemsAvailable =
                    if (selectedTab == AUTHORS) authorsPaging.itemCount else quotesPaging.itemCount
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

            LaunchedEffect(pagerState.currentPage) {
                selectedTab = if (pagerState.currentPage == 0) AUTHORS else QUOTES
            }
        }
    }
    TrackScreenViewEvent(screenName = "SearchScreen")
}

@Composable
private fun RecentSearchesBody(
    recentSearchQueries: List<String>,
    onClearRecentSearches: () -> Unit,
    onRecentSearchClicked: (String) -> Unit,
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(SharedRes.strings.search_recent_searches))
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            if (recentSearchQueries.isNotEmpty()) {
                IconButton(
                    onClick = { onClearRecentSearches() },
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Icon(
                        painter = painterResource(QuottieIcons.Close),
                        contentDescription = stringResource(
                            SharedRes.strings.search_clear_recent_searches_content_desc,
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(recentSearchQueries) { recentSearch ->
                Text(
                    text = recentSearch,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clickable { onRecentSearchClicked(recentSearch) }
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                painter = painterResource(QuottieIcons.ArrowBack),
                contentDescription = stringResource(SharedRes.strings.destination_navigate_back),
            )
        }
        QuottieSearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
    }
}
