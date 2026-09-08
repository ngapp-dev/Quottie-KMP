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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ngapp.quottie.search

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ngapp.quottie.core.analytics.NoOpAnalyticsHelper
import com.ngapp.quottie.core.data.model.recentsearchquery.RecentSearchQuery
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.domain.recentsearchquery.GetRecentSearchQueriesUseCase
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.search.fake.FakeAuthorRepository
import com.ngapp.quottie.search.fake.FakeQuoteRepository
import com.ngapp.quottie.search.fake.FakeSearchRepository
import com.ngapp.quottie.search.state.RecentSearchQueriesUiState
import com.ngapp.quottie.search.state.SearchAction
import com.ngapp.quottie.search.state.SearchEvent
import com.ngapp.quottie.search.state.SearchResultUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authorRepository = FakeAuthorRepository()
    private val quoteRepository = FakeQuoteRepository()
    private val searchRepository = FakeSearchRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(initialQuery: String = ""): SearchViewModel = SearchViewModel(
        recentSearchQueriesUseCase = GetRecentSearchQueriesUseCase(searchRepository),
        authorRepository = authorRepository,
        quoteRepository = quoteRepository,
        searchRepository = searchRepository,
        savedStateHandle = SavedStateHandle(mapOf("searchQuery" to initialQuery)),
        updateQuoteBookmark = UpdateQuoteBookmarkUseCase(quoteRepository),
        analyticsHelper = NoOpAnalyticsHelper(),
    )

    @Test
    fun searchQuery_startsAtValuePassedThroughNavigationRoute() = runTest(testDispatcher) {
        val viewModel = viewModel(initialQuery = "plato")

        assertEquals("plato", viewModel.searchQuery.value)
    }

    @Test
    fun searchQueryChanged_updatesSearchQuery() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.triggerAction(SearchAction.SearchQueryChanged("aristotle"))

        assertEquals("aristotle", viewModel.searchQuery.value)
    }

    @Test
    fun searchResultUiState_withShortQuery_isEmptyQuery() = runTest(testDispatcher) {
        val viewModel = viewModel(initialQuery = "a")

        viewModel.searchResultUiState.test {
            skipItems(1) // Loading
            assertEquals(SearchResultUiState.EmptyQuery, awaitItem())
        }
    }

    @Test
    fun searchResultUiState_withLongEnoughQuery_becomesSuccess() = runTest(testDispatcher) {
        val viewModel = viewModel(initialQuery = "plato")

        viewModel.searchResultUiState.test {
            skipItems(1) // Loading
            assertIs<SearchResultUiState.Success>(awaitItem())
        }
    }

    @Test
    fun recentSearchQueriesUiState_reflectsRepository() = runTest(testDispatcher) {
        searchRepository.setQueries(listOf(RecentSearchQuery("plato")))
        val viewModel = viewModel()

        viewModel.recentSearchQueriesUiState.test {
            skipItems(1) // Loading
            val state = awaitItem()
            assertIs<RecentSearchQueriesUiState.Success>(state)
            assertEquals(listOf("plato"), state.recentQueries.map { it.query })
        }
    }

    @Test
    fun clearRecentSearches_clearsRepository() = runTest(testDispatcher) {
        searchRepository.setQueries(listOf(RecentSearchQuery("plato")))
        val viewModel = viewModel()

        viewModel.triggerAction(SearchAction.ClearRecentSearches)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(searchRepository.cleared)
    }

    @Test
    fun searchTriggered_recordsQueryInRepository() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.triggerAction(SearchAction.SearchTriggered("plato"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(searchRepository.insertedQueries.contains("plato"))
    }

    @Test
    fun updateQuoteBookmark_removingBookmark_emitsMessageEvent() = runTest(testDispatcher) {
        val viewModel = viewModel()
        val quote = QuoteResource("q1", "content", "author", 5, emptyList())

        viewModel.events.test {
            viewModel.triggerAction(SearchAction.UpdateQuoteBookmark(quote, isBookmarked = false))
            assertEquals(SearchEvent.Message, awaitItem())
        }
    }
}
