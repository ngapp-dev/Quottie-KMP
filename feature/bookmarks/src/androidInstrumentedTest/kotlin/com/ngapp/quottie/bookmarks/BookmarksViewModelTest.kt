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

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ngapp.quottie.bookmarks

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ngapp.quottie.bookmarks.fake.FakeAuthorRepository
import com.ngapp.quottie.bookmarks.fake.FakeQuoteRepository
import com.ngapp.quottie.bookmarks.state.BookmarksAction
import com.ngapp.quottie.bookmarks.state.BookmarksEvent
import com.ngapp.quottie.bookmarks.state.BookmarksUiState
import com.ngapp.quottie.core.domain.author.UpdateAuthorBookmarkUseCase
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.ShareManager
import com.ngapp.quottie.core.ui.UiAndroidPlatformContextProvider
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
import kotlin.test.assertIs

/**
 * Instrumented because [BookmarksViewModel] takes a real [ShareManager], which needs an Android
 * [android.content.Context] to construct. The "share a quote" action itself (which would launch a
 * real system share sheet) is intentionally not exercised here.
 */
@RunWith(AndroidJUnit4::class)
class BookmarksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quoteRepository: FakeQuoteRepository
    private lateinit var authorRepository: FakeAuthorRepository
    private lateinit var shareManager: ShareManager

    private val quote = QuoteResource("q1", "content", "author", 5, emptyList())
    private val author = AuthorResource("a1", "bio", "description", "link", "name", "slug", 3)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        UiAndroidPlatformContextProvider.setContext(ApplicationProvider.getApplicationContext())
        shareManager = ShareManager()
        quoteRepository = FakeQuoteRepository()
        authorRepository = FakeAuthorRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): BookmarksViewModel = BookmarksViewModel(
        quoteRepository = quoteRepository,
        authorRepository = authorRepository,
        updateQuoteBookmark = UpdateQuoteBookmarkUseCase(quoteRepository),
        updateAuthorBookmark = UpdateAuthorBookmarkUseCase(authorRepository),
        shareManager = shareManager,
    )

    @Test
    fun uiState_reflectsSeededBookmarks() = runTest(testDispatcher) {
        quoteRepository.seedBookmark(quote)
        authorRepository.seedBookmark(author)
        val viewModel = viewModel()

        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = assertIs<BookmarksUiState.Success>(awaitItem())
            assertEquals(listOf("q1"), state.quotes.map { it.id })
            assertEquals(listOf("a1"), state.authors.map { it.id })
        }
    }

    @Test
    fun updateQuoteBookmark_removingBookmark_emitsQuoteMessageEvent() = runTest(testDispatcher) {
        quoteRepository.seedBookmark(quote)
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.triggerAction(BookmarksAction.UpdateQuoteBookmark(quote, isBookmarked = false))
            assertEquals(BookmarksEvent.QuoteMessage, awaitItem())
        }
    }

    @Test
    fun undoQuoteBookmarkRemoval_restoresLastRemovedBookmark() = runTest(testDispatcher) {
        quoteRepository.seedBookmark(quote)
        val viewModel = viewModel()
        viewModel.triggerAction(BookmarksAction.UpdateQuoteBookmark(quote, isBookmarked = false))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.triggerAction(BookmarksAction.UndoQuoteBookmarkRemoval)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // Loading, from this fresh subscription
            val state = assertIs<BookmarksUiState.Success>(awaitItem())
            assertEquals(listOf("q1"), state.quotes.map { it.id })
        }
    }

    @Test
    fun updateAuthorBookmark_removingBookmark_emitsAuthorMessageEvent() = runTest(testDispatcher) {
        authorRepository.seedBookmark(author)
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.triggerAction(BookmarksAction.UpdateAuthorBookmark(author, isBookmarked = false))
            assertEquals(BookmarksEvent.AuthorMessage, awaitItem())
        }
    }
}
