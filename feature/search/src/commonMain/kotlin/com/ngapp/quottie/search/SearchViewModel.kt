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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ngapp.quottie.core.analytics.AnalyticsEvent
import com.ngapp.quottie.core.analytics.AnalyticsEvent.Param
import com.ngapp.quottie.core.analytics.AnalyticsHelper
import com.ngapp.quottie.core.common.base.StateBaseViewModel
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.data.repository.quote.QuoteRepository
import com.ngapp.quottie.core.data.repository.search.SearchRepository
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.domain.recentsearchquery.GetRecentSearchQueriesUseCase
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.search.navigation.SearchNavigationRoute
import com.ngapp.quottie.search.state.RecentSearchQueriesUiState
import com.ngapp.quottie.search.state.SearchAction
import com.ngapp.quottie.search.state.SearchEvent
import com.ngapp.quottie.search.state.SearchResultUiState
import com.ngapp.quottie.search.state.SearchUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(
    recentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    authorRepository: AuthorRepository,
    quoteRepository: QuoteRepository,
    private val searchRepository: SearchRepository,
    private val savedStateHandle: SavedStateHandle,
    private val updateQuoteBookmark: UpdateQuoteBookmarkUseCase,
    private val analyticsHelper: AnalyticsHelper,
) : StateBaseViewModel<SearchUiState, SearchAction, SearchEvent>() {

    private val navSearchQuery = savedStateHandle.toRoute<SearchNavigationRoute>().searchQuery
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = navSearchQuery)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultUiState: StateFlow<SearchResultUiState> = searchQuery
        .flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                searchResultUiState(authorRepository, quoteRepository)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading,
        )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchQueriesUseCase()
            .map(RecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecentSearchQueriesUiState.Loading,
            )

    override fun triggerAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateQuoteBookmark ->
                onUpdateQuoteBookmark(action.quote, action.isBookmarked)

            is SearchAction.SearchQueryChanged -> onSearchQueryChanged(action.query)
            is SearchAction.SearchTriggered -> onSearchTriggered(action.query)
            is SearchAction.ClearRecentSearches -> onClearRecentSearches()
        }
    }

    private fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    /**
     * Called when the search action is explicitly triggered by the user. For example, when the
     * search icon is tapped in the IME or when the enter key is pressed in the search text field.
     *
     * The search results are displayed on the fly as the user types, but to explicitly save the
     * search query in the search text field, defining this method.
     */
    private fun onSearchTriggered(query: String) = safeLaunch {
        searchRepository.insertOrReplaceRecentSearch(searchQuery = query)
        analyticsHelper.logEventSearchTriggered(query = query)
    }

    private fun onClearRecentSearches() = safeLaunch {
        searchRepository.clearRecentSearches()
    }

    private fun onUpdateQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean) = safeLaunch {
        updateQuoteBookmark(quote, isBookmarked)
        if (!isBookmarked) {
            eventChannel.send(SearchEvent.Message)
        }
    }

    @OptIn(FlowPreview::class)
    private fun searchResultUiState(
        authorRepository: AuthorRepository,
        quoteRepository: QuoteRepository,
    ): Flow<SearchResultUiState> = flow {

        val searchAuthors = authorRepository.getAuthorsPaging(
            filter = ResultFilter(searchQuery = searchQuery.value),
            slug = emptyList(),
            pageSize = 20
        )
            .distinctUntilChanged()
            .debounce(500)
            .onEach { onSearchTriggered(searchQuery.value) }

        val searchQuotes = quoteRepository.getQuotesPaging(
            filter = ResultFilter(searchQuery = searchQuery.value),
            slug = emptyList(),
            pageSize = 20
        )
            .distinctUntilChanged()
            .debounce(500)
            .onEach { onSearchTriggered(searchQuery.value) }

        emit(SearchResultUiState.Success(searchAuthors, searchQuotes))
    }
}

private fun AnalyticsHelper.logEventSearchTriggered(query: String) =
    logEvent(
        event = AnalyticsEvent(
            type = SEARCH_QUERY,
            extras = listOf(element = Param(key = SEARCH_QUERY, value = query)),
        ),
    )


/** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
private const val SEARCH_QUERY_MIN_LENGTH = 2
private const val SEARCH_QUERY = "searchQuery"
