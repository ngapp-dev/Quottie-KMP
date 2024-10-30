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

package com.ngapp.quottie.core.data.repository.quote

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.model.quote.asQuoteOfTheDayEntity
import com.ngapp.quottie.core.data.model.quote.asResource
import com.ngapp.quottie.core.data.pagingsource.quote.QuotePagingSource
import com.ngapp.quottie.core.database.dao.QuoteBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteOfTheDayDao
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import com.ngapp.quottie.core.database.model.quote.asEntity
import com.ngapp.quottie.core.database.model.quote.asExternalModel
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.network.QuottieNetworkDataSource
import com.ngapp.quottie.core.network.model.quote.NetworkQuote
import com.ngapp.quottie.core.network.model.response.NetworkResponse
import com.ngapp.quottie.core.network.util.handleError
import com.ngapp.quottie.core.network.util.handleResponse
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DefaultQuoteRepository(
    private val quoteOfTheDayDao: QuoteOfTheDayDao,
    private val quoteBookmarksDao: QuoteBookmarksDao,
    private val quottieNetwork: QuottieNetworkDataSource,
) : QuoteRepository {

    override suspend fun getRandomQuotes(pageSize: Int): Flow<Result<List<QuoteResource>, DataError.Network>> =
        flow {
            try {
                val response = quottieNetwork.getRandomQuotes(pageSize = pageSize)
                val result = handleResponse(response) { it.body<List<NetworkQuote>>() }
                if (result is Result.Success) {
                    emit(Result.Success(result.data.map(NetworkQuote::asResource)))
                } else {
                    emit(Result.Error((result as Result.Error).error, result.exception))
                }
            } catch (e: Exception) {
                emit(handleError(e))
            }
        }


    override suspend fun getQuotesPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<QuoteResource>> =
        Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                QuotePagingSource(
                    filter = filter,
                    onGetQuotes = { page -> getQuotes(filter, slug, pageSize, page) },
                    onGetSearchQuotes = { filter, page ->
                        getSearchQuotes(filter, slug, pageSize, page)
                    },
                    onGetQuoteBookmark = { quoteId -> getQuoteBookmark(quoteId) }
                )
            }
        ).flow

    override suspend fun getQuotes(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<QuoteResource>, DataError.Network>> = flow {
        try {
            val response = quottieNetwork.getQuotes(
                sortBy = filter.sortField.field,
                order = filter.sortOrder.field,
                slug = slug,
                pageSize = pageSize,
                page = page
            )
            val result = handleResponse(response) { it.body<NetworkResponse<NetworkQuote>>() }
            if (result is Result.Success) {
                emit(Result.Success(result.data.results.map(NetworkQuote::asResource)))
            } else {
                emit(Result.Error((result as Result.Error).error, result.exception))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override suspend fun getSearchQuotes(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<QuoteResource>, DataError.Network>> = flow {
        try {
            val response = quottieNetwork.getSearchQuotes(
                query = filter.searchQuery,
                sortBy = filter.sortField.field,
                order = filter.sortOrder.field,
                slug = slug,
                pageSize = pageSize,
                page = page
            )
            val result: Result<NetworkResponse<NetworkQuote>, DataError.Network> =
                handleResponse(response) { it.body<NetworkResponse<NetworkQuote>>() }
            if (result is Result.Success) {
                emit(Result.Success(result.data.results.map(NetworkQuote::asResource)))
            } else {
                emit(Result.Error((result as Result.Error).error, result.exception))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override suspend fun getQuoteOfTheDay(): Flow<Result<QuoteResource, DataError.Network>> = flow {
        val currentDate =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val savedQuote = quoteOfTheDayDao.getQuoteOfTheDay()

        val isSameDate = savedQuote?.let {
            val lastFetchedDate =
                it.lastFetched.toLocalDateTime(TimeZone.currentSystemDefault()).date
            lastFetchedDate == currentDate
        } == true
        if (isSameDate) {
            emit(Result.Success(savedQuote.asExternalModel()))
        } else {
            try {
                val response = quottieNetwork.getRandomQuotes(pageSize = 1)
                val result =
                    handleResponse(response) { it.body<List<NetworkQuote>>() }
                if (result is Result.Success) {
                    val newQuote = result.data.first().asQuoteOfTheDayEntity()
                    quoteOfTheDayDao.replaceQuoteOfTheDay(
                        newQuote.copy(lastFetched = Clock.System.now())
                    )
                    emit(Result.Success(newQuote.asExternalModel()))
                } else {
                    emit(Result.Error((result as Result.Error).error, result.exception))
                }
            } catch (e: Exception) {
                emit(handleError(e))
            }
        }
    }

    override suspend fun saveQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean) =
        quoteBookmarksDao.saveQuoteBookmark(quote.copy(isBookmarked = isBookmarked).asEntity())

    override suspend fun deleteQuoteBookmark(quoteId: String) =
        quoteBookmarksDao.deleteQuoteBookmark(quoteId)

    override suspend fun getQuoteBookmark(quoteId: String): QuoteResourceEntity? =
        quoteBookmarksDao.getQuoteBookmark(quoteId)

    override fun getQuoteBookmarkList(): Flow<List<QuoteResource>> =
        quoteBookmarksDao.getQuoteBookmarkListFlow()
            .map { it.map(QuoteResourceEntity::asExternalModel) }

}