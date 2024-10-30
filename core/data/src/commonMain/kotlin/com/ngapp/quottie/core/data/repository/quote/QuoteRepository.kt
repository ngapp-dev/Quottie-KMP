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

import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {

    suspend fun getRandomQuotes(pageSize: Int): Flow<Result<List<QuoteResource>, DataError.Network>>

    suspend fun getQuotesPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<QuoteResource>>

    suspend fun getQuotes(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int,
    ): Flow<Result<List<QuoteResource>, DataError.Network>>

    suspend fun getSearchQuotes(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<QuoteResource>, DataError.Network>>

    suspend fun getQuoteOfTheDay(): Flow<Result<QuoteResource, DataError.Network>>

    suspend fun saveQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean)

    suspend fun deleteQuoteBookmark(quoteId: String)

    suspend fun getQuoteBookmark(quoteId: String): QuoteResourceEntity?

    fun getQuoteBookmarkList(): Flow<List<QuoteResource>>
}