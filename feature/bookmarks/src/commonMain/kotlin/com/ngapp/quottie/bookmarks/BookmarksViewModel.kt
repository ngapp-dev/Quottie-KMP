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

import androidx.lifecycle.viewModelScope
import com.ngapp.quottie.bookmarks.state.BookmarksAction
import com.ngapp.quottie.bookmarks.state.BookmarksEvent
import com.ngapp.quottie.bookmarks.state.BookmarksUiState
import com.ngapp.quottie.bookmarks.state.BookmarksUiState.Loading
import com.ngapp.quottie.bookmarks.state.BookmarksUiState.Success
import com.ngapp.quottie.core.common.base.StateBaseViewModel
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.data.repository.quote.QuoteRepository
import com.ngapp.quottie.core.domain.author.UpdateAuthorBookmarkUseCase
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.ShareManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BookmarksViewModel(
    quoteRepository: QuoteRepository,
    authorRepository: AuthorRepository,
    private val updateQuoteBookmark: UpdateQuoteBookmarkUseCase,
    private val updateAuthorBookmark: UpdateAuthorBookmarkUseCase,
    private val shareManager: ShareManager,
) : StateBaseViewModel<BookmarksUiState, BookmarksAction, BookmarksEvent>() {

    private var lastRemovedQuoteBookmark: QuoteResource? = null
    private var lastRemovedAuthorBookmark: AuthorResource? = null

    val uiState: StateFlow<BookmarksUiState> = combine(
        quoteRepository.getQuoteBookmarkList(),
        authorRepository.getAuthorBookmarkList(),
    ) { quotes, authors ->
        Success(authors = authors, quotes = quotes)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading
        )

    override fun triggerAction(action: BookmarksAction) {
        when (action) {
            is BookmarksAction.UpdateQuoteBookmark -> onUpdateQuoteBookmark(action.quote, action.isBookmarked)
            is BookmarksAction.UndoQuoteBookmarkRemoval -> onUndoQuoteBookmarkRemoval()
            is BookmarksAction.UpdateAuthorBookmark -> onUpdateAuthorBookmark(action.author, action.isBookmarked)
            is BookmarksAction.UndoAuthorBookmarkRemoval -> onUndoAuthorBookmarkRemoval()
            is BookmarksAction.ShareQuote -> onShareQuote(action.quote)
        }
    }

    private fun onUpdateQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean) = safeLaunch {
        updateQuoteBookmark(quote, isBookmarked)
        lastRemovedQuoteBookmark = quote
        eventChannel.send(BookmarksEvent.QuoteMessage)
    }

    private fun onUndoQuoteBookmarkRemoval() = safeLaunch {
        lastRemovedQuoteBookmark?.let { updateQuoteBookmark(it, isBookmarked = true) }
        lastRemovedQuoteBookmark = null
    }

    private fun onUpdateAuthorBookmark(author: AuthorResource, isBookmarked: Boolean) = safeLaunch {
        lastRemovedAuthorBookmark = author
        updateAuthorBookmark(author, isBookmarked)
        eventChannel.send(BookmarksEvent.AuthorMessage)
    }

    private fun onUndoAuthorBookmarkRemoval() = safeLaunch {
        lastRemovedAuthorBookmark?.let { updateAuthorBookmark(it, isBookmarked = true) }
        lastRemovedAuthorBookmark = null
    }

    private fun onShareQuote(quote: QuoteResource) {
        shareManager.shareQuote(quote)
    }
}
