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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.ShareManager
import com.ngapp.quottie.core.ui.UiAndroidPlatformContextProvider
import com.ngapp.quottie.home.fake.FakeQuoteRepository
import com.ngapp.quottie.home.state.HomeAction
import com.ngapp.quottie.home.state.HomeEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Instrumented because [HomeViewModel] takes a real [ShareManager], which needs an Android
 * [android.content.Context] to construct. The "share a quote" action itself (which would launch a
 * real system share sheet) is intentionally not exercised here.
 */
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quoteRepository: FakeQuoteRepository
    private lateinit var shareManager: ShareManager

    private val quoteOfTheDay = QuoteResource("qotd", "quote of the day", "author", 5, emptyList())
    private val randomQuote = QuoteResource("q1", "random quote", "author1", 5, emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        UiAndroidPlatformContextProvider.setContext(ApplicationProvider.getApplicationContext())
        shareManager = ShareManager()
        quoteRepository = FakeQuoteRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): HomeViewModel = HomeViewModel(
        quoteRepository = quoteRepository,
        updateQuoteBookmark = UpdateQuoteBookmarkUseCase(quoteRepository),
        shareManager = shareManager,
    )

    @Test
    fun init_success_populatesQuoteOfTheDayAndRandomQuotes() = runTest(testDispatcher) {
        quoteRepository.quoteOfTheDayResult = Result.Success(quoteOfTheDay)
        quoteRepository.randomQuotesResult = Result.Success(listOf(randomQuote))

        val viewModel = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(quoteOfTheDay, state.quoteOfTheDay)
            assertEquals(setOf(randomQuote), state.quotes)
            assertFalseLoading(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun init_quoteOfTheDayError_setsErrorAndEmitsErrorEvent() = runTest(testDispatcher) {
        quoteRepository.quoteOfTheDayResult =
            Result.Error(DataError.Network.SERVER_ERROR, Exception("boom"))
        quoteRepository.randomQuotesResult = Result.Success(emptyList())

        val viewModel = viewModel()

        viewModel.events.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val event = awaitItem()
            assertTrue(event is HomeEvent.Error)
        }
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.error != null)
    }

    @Test
    fun updateQuoteBookmark_removingBookmark_emitsMessageEvent() = runTest(testDispatcher) {
        quoteRepository.quoteOfTheDayResult = Result.Success(quoteOfTheDay)
        quoteRepository.randomQuotesResult = Result.Success(emptyList())
        val viewModel = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.triggerAction(HomeAction.UpdateQuoteBookmark(randomQuote, isBookmarked = false))
            assertEquals(HomeEvent.Message, awaitItem())
        }
        assertNull(quoteRepository.savedBookmarks.find { it.id == randomQuote.id })
    }

    @Test
    fun updateQuoteBookmark_addingBookmark_savesBookmarkWithoutMessageEvent() = runTest(testDispatcher) {
        quoteRepository.quoteOfTheDayResult = Result.Success(quoteOfTheDay)
        quoteRepository.randomQuotesResult = Result.Success(emptyList())
        val viewModel = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.triggerAction(HomeAction.UpdateQuoteBookmark(randomQuote, isBookmarked = true))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, quoteRepository.savedBookmarks.single { it.id == randomQuote.id }.isBookmarked)
    }

    @Test
    fun refreshHomePage_setsLoadingThenReloadsData() = runTest(testDispatcher) {
        quoteRepository.quoteOfTheDayResult = Result.Success(quoteOfTheDay)
        quoteRepository.randomQuotesResult = Result.Success(listOf(randomQuote))
        val viewModel = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.triggerAction(HomeAction.RefreshHomePage)
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()
        assertFalseLoading(viewModel.uiState.value.isLoading)
        assertEquals(quoteOfTheDay, viewModel.uiState.value.quoteOfTheDay)
    }

    private fun assertFalseLoading(isLoading: Boolean) = assertEquals(false, isLoading)
}
