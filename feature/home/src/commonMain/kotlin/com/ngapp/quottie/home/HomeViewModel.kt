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

import com.ngapp.quottie.core.common.base.BaseViewModel
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Error
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.repository.quote.QuoteRepository
import com.ngapp.quottie.core.domain.quote.UpdateQuoteBookmarkUseCase
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.ui.ShareManager
import com.ngapp.quottie.core.ui.model.uitext.asStringResource
import com.ngapp.quottie.home.state.HomeAction
import com.ngapp.quottie.home.state.HomeEvent
import com.ngapp.quottie.home.state.HomeUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val quoteRepository: QuoteRepository,
    private val updateQuoteBookmark: UpdateQuoteBookmarkUseCase,
    private val shareManager: ShareManager,
) : BaseViewModel<HomeUiState, HomeAction, HomeEvent>() {

    override fun provideUiState(): HomeUiState = HomeUiState()

    override fun triggerAction(action: HomeAction) {
        when (action) {
            is HomeAction.GetRandomQuotes -> onGetRandomQuotes(action.currentQuotes, action.limit)
            is HomeAction.UpdateQuoteBookmark -> onUpdateQuoteBookmark(action.quote, action.isBookmarked)
            is HomeAction.ShareQuote -> onShareQuote(action.quote)
            is HomeAction.RefreshHomePage -> onRefreshHomePage()
        }
    }

    init {
        initQuotes()
    }

    private fun onGetRandomQuotes(currentQuotes: Set<QuoteResource>, limit: Int) = safeLaunch {
        quoteRepository.getRandomQuotes(limit).collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    val newQuotes = result.data.toSet()
                    _uiState.update { it.copy(quotes = currentQuotes.plus(newQuotes)) }
                }

                is Result.Error -> handleCommonError(result)
            }
        }
    }

    private fun initQuotes() = safeLaunch {
        combine(
            quoteRepository.getQuoteOfTheDay(),
            quoteRepository.getRandomQuotes(10),
            quoteRepository.getQuoteBookmarkList(),
            ::Triple
        ).collect { (quoteOfTheDayResult, randomQuotesResult, quoteBookmarks) ->
            when {
                quoteOfTheDayResult is Result.Success && randomQuotesResult is Result.Success -> {
                    val quoteBookmarkIds = quoteBookmarks.map { it.id }
                    val updatedQuoteOfTheDay = quoteOfTheDayResult.data.let { quoteOfTheDay ->
                        val isBookmark = quoteBookmarkIds.contains(quoteOfTheDay.id)
                        quoteOfTheDay.copy(isBookmarked = isBookmark)
                    }

                    val updatedRandomQuotes = randomQuotesResult.data.map { quote ->
                        val isBookmarked = quoteBookmarkIds.contains(quote.id)
                        quote.copy(isBookmarked = isBookmarked)
                    }.toSet()

                    _uiState.update {
                        it.copy(
                            quoteOfTheDay = updatedQuoteOfTheDay,
                            quotes = updatedRandomQuotes,
                            isLoading = false,
                            error = null,
                        )
                    }
                }

                quoteOfTheDayResult is Result.Error -> handleCommonError(quoteOfTheDayResult)
                randomQuotesResult is Result.Error -> handleCommonError(randomQuotesResult)
            }
        }
    }

    private fun onUpdateQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean) = safeLaunch {
        updateQuoteBookmark(quote, isBookmarked)
        if (!isBookmarked) {
            eventChannel.send(HomeEvent.Message)
        }
    }

    private fun onShareQuote(quote: QuoteResource) {
        shareManager.shareQuote(quote)
    }

    private suspend fun <T> handleCommonError(result: Result.Error<T, Error>) {
        val errorMessage = result.error as DataError.Network
        _uiState.update { it.copy(error = errorMessage.asStringResource(), isLoading = false) }
        eventChannel.send(HomeEvent.Error(errorMessage.asStringResource()))
    }

    private fun onRefreshHomePage() {
        _uiState.update { it.copy(isLoading = true) }
        initQuotes()
    }
}

