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

package com.ngapp.quottie.core.data.pagingsource.quote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class QuotePagingSource(
    private val filter: ResultFilter,
    private val onGetQuotes: suspend (Int) -> Flow<Result<List<QuoteResource>, DataError.Network>>,
    private val onGetSearchQuotes: suspend (ResultFilter, Int) -> Flow<Result<List<QuoteResource>, DataError.Network>>,
    private val onGetQuoteBookmark: suspend (String) -> QuoteResourceEntity?
) : PagingSource<Int, QuoteResource>() {

    override fun getRefreshKey(state: PagingState<Int, QuoteResource>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteResource> {
        val page = params.key ?: 1
        return try {
            val response =
                when {
                    filter.searchQuery.isEmpty() -> onGetQuotes(page)
                    filter.searchQuery.toDoubleOrNull() != null -> {
                        val editFilter = filter.copy(searchQuery = "\" ${filter.searchQuery} \"")
                        onGetSearchQuotes(editFilter, page)
                    }

                    else -> onGetSearchQuotes(filter, page)
                }
            var quoteList = emptyList<QuoteResource>()
            response.collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        quoteList = result.data
                        quoteList.map { quote ->
                            val quoteBookmark = onGetQuoteBookmark(quote.id)?.isBookmarked
                            quote.copy(isBookmarked = quoteBookmark != null)
                        }
                    }

                    is Result.Error -> throw result.exception
                }
            }
            LoadResult.Page(
                data = quoteList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (quoteList.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}