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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.cash.paging.PagingData
import com.ngapp.quottie.authors.detail.navigation.AuthorDetailNavigationRoute
import com.ngapp.quottie.authors.detail.state.AuthorDetailAction
import com.ngapp.quottie.authors.detail.state.AuthorDetailEvent
import com.ngapp.quottie.authors.detail.state.AuthorDetailUiState
import com.ngapp.quottie.authors.detail.state.AuthorDetailUiState.Loading
import com.ngapp.quottie.authors.detail.state.AuthorDetailUiState.Success
import com.ngapp.quottie.core.common.base.StateBaseViewModel
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Error
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.data.repository.quote.QuoteRepository
import com.ngapp.quottie.core.domain.author.UpdateAuthorBookmarkUseCase
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.ui.ShareManager
import com.ngapp.quottie.core.ui.model.uitext.asStringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AuthorDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val authorRepository: AuthorRepository,
    private val quoteRepository: QuoteRepository,
    private val updateQuoteBookmark: UpdateQuoteBookmarkUseCase,
    private val updateAuthorBookmark: UpdateAuthorBookmarkUseCase,
    private val shareManager: ShareManager,
) : StateBaseViewModel<AuthorDetailUiState, AuthorDetailAction, AuthorDetailEvent>() {

    private val authorId = savedStateHandle.toRoute<AuthorDetailNavigationRoute>().id

    val uiState: StateFlow<AuthorDetailUiState> = authorDetailUiState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading
        )

    override fun triggerAction(action: AuthorDetailAction) {
        when (action) {
            is AuthorDetailAction.ShareAuthor -> onShareAuthor(action.author)
            is AuthorDetailAction.ShareQuote -> onShareQuote(action.quote)
            is AuthorDetailAction.UpdateQuoteBookmark ->
                onUpdateQuoteBookmark(action.quote, action.isBookmarked)

            is AuthorDetailAction.UpdateAuthorBookmark ->
                onUpdateAuthorBookmark(action.author, action.isBookmarked)
        }
    }

    private fun authorDetailUiState(): Flow<AuthorDetailUiState> = flow {
        val localResult = authorRepository.getAuthorBookmark(authorId)
        if (localResult != null) {
            val authorBookmarked = localResult.copy(isBookmarked = true)
            val authorWiki = getAuthorWiki(authorBookmarked)
            val authorQuotesFlow = getAuthorQuotes(authorBookmarked)
            emit(Success(author = authorWiki, authorQuotes = authorQuotesFlow))
        } else {
            when (val result = authorRepository.getAuthorDetail(authorId)) {
                is Result.Success -> {
                    val author = result.data.let { author ->
                        val isBookmark = authorRepository.getAuthorBookmark(author.id)?.isBookmarked
                        author.copy(isBookmarked = isBookmark != null)
                    }
                    val authorWiki = getAuthorWiki(author)
                    val authorQuotesFlow = getAuthorQuotes(author)
                    emit(Success(author = authorWiki, authorQuotes = authorQuotesFlow))
                }

                is Result.Error -> {
                    handleCommonError(result)
                    emit(AuthorDetailUiState.Error(result.error.asStringResource()))
                }
            }
        }
    }

    private suspend fun getAuthorWiki(author: AuthorResource): AuthorResource {
        var authorWiki = author
        val authorTitle = author.link.substringAfterLast("/wiki/")
        authorRepository.getAuthorsWiki(authorTitle).collectLatest { wikiResult ->
            when (wikiResult) {
                is Result.Success -> {
                    val authorsWiki = wikiResult.data
                    val normalizedTitle = authorsWiki.query.normalizedResource
                        ?.find { it.from == author.link.substringAfterLast("/wiki/") }
                        ?.to ?: author.name

                    val wikiPage =
                        authorsWiki.query.pages.values.find { it.title == normalizedTitle }
                    authorWiki = author.copy(image = wikiPage?.thumbnailResource?.source.orEmpty())
                }

                is Result.Error -> wikiResult.exception
            }
        }
        return authorWiki
    }

    private suspend fun getAuthorQuotes(author: AuthorResource): Flow<PagingData<QuoteResource>> {
        return quoteRepository.getQuotesPaging(
            filter = ResultFilter(),
            slug = listOf(author.slug),
            pageSize = 20
        )
    }

    private suspend fun <T> handleCommonError(result: Result.Error<T, Error>) {
        val errorMessage = result.error as DataError.Network
        eventChannel.send(AuthorDetailEvent.Error(errorMessage.asStringResource()))
    }

    private fun onUpdateQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean) = safeLaunch {
        updateQuoteBookmark(quote, isBookmarked)
        if (!isBookmarked) {
            eventChannel.send(AuthorDetailEvent.Message)
        }
    }

    private fun onUpdateAuthorBookmark(author: AuthorResource, isBookmarked: Boolean) = safeLaunch {
        updateAuthorBookmark(author, isBookmarked)
        if (!isBookmarked) {
            eventChannel.send(AuthorDetailEvent.Message)
        }
    }

    private fun onShareAuthor(author: AuthorResource) {
        shareManager.shareAuthor(author)
    }

    private fun onShareQuote(quote: QuoteResource) {
        shareManager.shareQuote(quote)
    }
}