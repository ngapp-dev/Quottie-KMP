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

package com.ngapp.quottie.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.analytics.TrackScreenViewEvent
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.ErrorView
import com.ngapp.quottie.core.ui.HorizontalDividerWithQuoteIcon
import com.ngapp.quottie.core.ui.PagerIndicator
import com.ngapp.quottie.core.ui.QuoteResourceCard
import com.ngapp.quottie.home.state.HomeAction
import com.ngapp.quottie.home.state.HomeEvent
import com.ngapp.quottie.home.state.HomeUiState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeRoute(
    onNavigateSearch: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<StringResource?>(null) }
    val bookmarkMessage = stringResource(SharedRes.strings.bookmark_removed)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.Error -> errorMessage = event.error
                is HomeEvent.Message -> onShowSnackbar(bookmarkMessage, null)
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

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        onTagClick = onNavigateSearch,
        onAction = viewModel::triggerAction
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier,
    uiState: HomeUiState,
    onTagClick: (String) -> Unit,
    onAction: (HomeAction) -> Unit,
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        state = gridState,
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(300.dp),
    ) {
        when {
            uiState.isLoading -> {
                item(key = "loading", span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            uiState.error != null -> {
                item(key = "error", span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorView(
                            message = stringResource(uiState.error),
                            canRetry = true,
                            onClickRetry = { onAction(HomeAction.RefreshHomePage) },
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                    }
                }
            }

            else -> {
                if (uiState.quoteOfTheDay != null) {
                    item("quoteOfTheDay") {
                        val rainbowColors = listOf(
                            Color(0xFFFF0000), // Red
                            Color(0xFFFF7F00), // Orange
                            Color(0xFFFFFF00), // Yellow
                            Color(0xFF00FF00), // Green
                            Color(0xFF0000FF), // Blue
                            Color(0xFF1F4F98), // Indigo
                            Color(0xFF8B00FF)  // Violet
                        )

                        Column {
                            val quote = uiState.quoteOfTheDay
                            var isBookmark by rememberSaveable { mutableStateOf(quote.isBookmarked) }
                            Spacer(Modifier.height(12.dp))
                            QuottieText(
                                text = stringResource(SharedRes.strings.title_quote_of_the_day),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Box(modifier = Modifier.padding(16.dp)) {
                                QuoteResourceCard(
                                    quote = quote,
                                    isBookmarked = isBookmark,
                                    borderStroke = BorderStroke(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(rainbowColors),
                                    ),
                                    onToggleBookmark = {
                                        isBookmark = it
                                        onAction(HomeAction.UpdateQuoteBookmark(quote, it))
                                    },
                                    onTagClick = onTagClick,
                                    onShareClick = { onAction(HomeAction.ShareQuote(quote)) },
                                )
                            }
                            HorizontalDividerWithQuoteIcon(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                item("randomQuotes") {
                    QuotePager(
                        quotes = uiState.quotes,
                        onAction = onAction,
                        onTagClick = onTagClick,
                    )
                }
                item("safeDrawing") {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "HomeScreen")
}

@Composable
private fun QuotePager(
    quotes: Set<QuoteResource>,
    onAction: (HomeAction) -> Unit,
    onTagClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = { quotes.size },
        initialPage = 0,
    )
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= quotes.size - 5) {
            onAction(HomeAction.GetRandomQuotes(quotes, 10))
        }
    }
    Column {
        Spacer(Modifier.height(12.dp))
        QuottieText(
            text = stringResource(SharedRes.strings.title_random_quotes),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val quote = quotes.toList()[page]
            var isBookmarked by remember { mutableStateOf(quote.isBookmarked) }
            Box(modifier = Modifier.padding(16.dp)) {
                QuoteResourceCard(
                    quote = quote,
                    isBookmarked = isBookmarked,
                    onToggleBookmark = {
                        isBookmarked = it
                        onAction(HomeAction.UpdateQuoteBookmark(quote, it))
                    },
                    onShareClick = { onAction(HomeAction.ShareQuote(quote)) },
                    onTagClick = onTagClick
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            val currentIndex = pagerState.currentPage
            repeat(5) { index ->
                val isSelected =
                    (currentIndex < 5 && index == currentIndex) || (currentIndex >= 5 && index == 4)
                PagerIndicator(isSelected = isSelected)
            }
        }
    }
}