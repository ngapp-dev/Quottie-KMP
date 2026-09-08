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

package com.ngapp.quottie.authors.detail

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ngapp.quottie.authors.detail.fake.FakeAuthorRepository
import com.ngapp.quottie.authors.detail.fake.FakeQuoteRepository
import com.ngapp.quottie.authors.detail.state.AuthorDetailAction
import com.ngapp.quottie.authors.detail.state.AuthorDetailEvent
import com.ngapp.quottie.authors.detail.state.AuthorDetailUiState
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.domain.author.UpdateAuthorBookmarkUseCase
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.ShareManager
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
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
import kotlin.test.assertTrue

/**
 * Instrumented because [AuthorDetailViewModel] takes a real [ShareManager], which needs an
 * Android [android.content.Context] to construct. The "share" actions themselves (which would
 * launch a real system share sheet) are intentionally not exercised here.
 */
@RunWith(AndroidJUnit4::class)
class AuthorDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authorRepository: FakeAuthorRepository
    private lateinit var quoteRepository: FakeQuoteRepository
    private lateinit var shareManager: ShareManager

    private val author = AuthorResource("a1", "bio", "description", "https://en.wikipedia.org/wiki/Plato", "name", "slug", 3)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        UiAndroidPlatformContextProvider.setContext(ApplicationProvider.getApplicationContext())
        shareManager = ShareManager()
        authorRepository = FakeAuthorRepository()
        quoteRepository = FakeQuoteRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(authorId: String = "a1"): AuthorDetailViewModel = AuthorDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("id" to authorId)),
        authorRepository = authorRepository,
        quoteRepository = quoteRepository,
        updateQuoteBookmark = UpdateQuoteBookmarkUseCase(quoteRepository),
        updateAuthorBookmark = UpdateAuthorBookmarkUseCase(authorRepository),
        shareManager = shareManager,
    )

    @Test
    fun uiState_whenLocallyBookmarked_usesLocalCopyAndMarksBookmarked() = runTest(testDispatcher) {
        authorRepository.localBookmark = author
        val viewModel = viewModel()

        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = assertIs<AuthorDetailUiState.Success>(awaitItem())
            assertEquals("a1", state.author.id)
            assertTrue(state.author.isBookmarked)
        }
    }

    @Test
    fun uiState_whenNotBookmarked_fetchesFromNetwork() = runTest(testDispatcher) {
        authorRepository.authorDetailResult = Result.Success(author)
        val viewModel = viewModel()

        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = assertIs<AuthorDetailUiState.Success>(awaitItem())
            assertEquals("a1", state.author.id)
        }
    }

    @Test
    fun uiState_networkError_emitsErrorStateAndEvent() = runTest(testDispatcher) {
        authorRepository.authorDetailResult =
            Result.Error(DataError.Network.SERVER_ERROR, Exception("boom"))
        val viewModel = viewModel()
        val events = mutableListOf<AuthorDetailEvent>()
        backgroundScope.launch { viewModel.events.toList(events) }

        // uiState is only driven by an active subscriber (WhileSubscribed sharing), so it must
        // be collected for the error path (and the event it sends) to run at all.
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertIs<AuthorDetailUiState.Error>(awaitItem())
        }

        assertIs<AuthorDetailEvent.Error>(events.single())
    }

    @Test
    fun updateAuthorBookmark_removingBookmark_emitsMessageEvent() = runTest(testDispatcher) {
        authorRepository.authorDetailResult = Result.Success(author)
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.triggerAction(AuthorDetailAction.UpdateAuthorBookmark(author, isBookmarked = false))
            assertEquals(AuthorDetailEvent.Message, awaitItem())
        }
    }

    @Test
    fun updateQuoteBookmark_removingBookmark_emitsMessageEvent() = runTest(testDispatcher) {
        authorRepository.authorDetailResult = Result.Success(author)
        val viewModel = viewModel()
        val quote = QuoteResource("q1", "content", "author", 5, emptyList())

        viewModel.events.test {
            viewModel.triggerAction(AuthorDetailAction.UpdateQuoteBookmark(quote, isBookmarked = false))
            assertEquals(AuthorDetailEvent.Message, awaitItem())
        }
    }
}
