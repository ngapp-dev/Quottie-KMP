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

package com.ngapp.quottie.search.fake

import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.model.recentsearchquery.RecentSearchQuery
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.data.repository.quote.QuoteRepository
import com.ngapp.quottie.core.data.repository.search.SearchRepository
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.model.wiki.WikiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/** Minimal in-memory fake covering exactly what [com.ngapp.quottie.search.SearchViewModel] uses. */
class FakeAuthorRepository : AuthorRepository {

    override suspend fun getAuthorsPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<AuthorResource>> = flowOf(PagingData.empty())

    override suspend fun getAuthors(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int,
    ): Flow<Result<List<AuthorResource>, DataError.Network>> = error("Not used by this test")

    override suspend fun getSearchAuthors(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int,
    ): Flow<Result<List<AuthorResource>, DataError.Network>> = error("Not used by this test")

    override suspend fun getAuthorsWiki(authorTitle: String): Flow<Result<WikiResource, DataError.Network>> =
        error("Not used by this test")

    override suspend fun getAuthorDetail(authorId: String): Result<AuthorResource, DataError.Network> =
        error("Not used by this test")

    override suspend fun saveAuthorBookmark(author: AuthorResource, isBookmarked: Boolean) =
        error("Not used by this test")

    override suspend fun deleteAuthorBookmark(authorId: String) = error("Not used by this test")

    override suspend fun getAuthorBookmark(authorId: String): AuthorResource? =
        error("Not used by this test")

    override fun getAuthorBookmarkList(): Flow<List<AuthorResource>> = error("Not used by this test")
}

/** Minimal in-memory fake covering exactly what [com.ngapp.quottie.search.SearchViewModel] uses. */
class FakeQuoteRepository : QuoteRepository {

    private val bookmarks = MutableStateFlow<Map<String, QuoteResourceEntity>>(emptyMap())
    val savedBookmarks: List<QuoteResourceEntity> get() = bookmarks.value.values.toList()

    override suspend fun getRandomQuotes(pageSize: Int): Flow<Result<List<QuoteResource>, DataError.Network>> =
        error("Not used by this test")

    override suspend fun getQuotesPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<QuoteResource>> = flowOf(PagingData.empty())

    override suspend fun getQuotes(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int,
    ): Flow<Result<List<QuoteResource>, DataError.Network>> = error("Not used by this test")

    override suspend fun getSearchQuotes(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int,
    ): Flow<Result<List<QuoteResource>, DataError.Network>> = error("Not used by this test")

    override suspend fun getQuoteOfTheDay(): Flow<Result<QuoteResource, DataError.Network>> =
        error("Not used by this test")

    override suspend fun saveQuoteBookmark(quote: QuoteResource, isBookmarked: Boolean) {
        bookmarks.value = bookmarks.value +
            (quote.id to QuoteResourceEntity(quote.id, quote.content, quote.author, quote.length, quote.tags, isBookmarked))
    }

    override suspend fun deleteQuoteBookmark(quoteId: String) {
        bookmarks.value = bookmarks.value - quoteId
    }

    override suspend fun getQuoteBookmark(quoteId: String): QuoteResourceEntity? = bookmarks.value[quoteId]

    override fun getQuoteBookmarkList(): Flow<List<QuoteResource>> = error("Not used by this test")
}

/** Minimal in-memory fake covering exactly what [com.ngapp.quottie.search.SearchViewModel] uses. */
class FakeSearchRepository : SearchRepository {

    val insertedQueries = mutableListOf<String>()
    var cleared = false
        private set

    private val queries = MutableStateFlow<List<RecentSearchQuery>>(emptyList())

    fun setQueries(value: List<RecentSearchQuery>) {
        queries.value = value
    }

    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> = queries

    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) {
        insertedQueries.add(searchQuery)
    }

    override suspend fun clearRecentSearches() {
        cleared = true
        queries.value = emptyList()
    }
}
