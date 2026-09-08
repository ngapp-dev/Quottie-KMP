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

package com.ngapp.quottie.core.data.fake

import com.ngapp.quottie.core.database.dao.AuthorBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteOfTheDayDao
import com.ngapp.quottie.core.database.dao.RecentSearchQueryDao
import com.ngapp.quottie.core.database.model.author.AuthorResourceEntity
import com.ngapp.quottie.core.database.model.quote.QuoteOfTheDayEntity
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import com.ngapp.quottie.core.database.model.recentsearchquery.RecentSearchQueryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory fake standing in for the real Room-generated DAO in repository unit tests. */
class FakeQuoteBookmarksDao : QuoteBookmarksDao {

    private val bookmarks = MutableStateFlow<Map<String, QuoteResourceEntity>>(emptyMap())

    override suspend fun saveQuoteBookmark(quote: QuoteResourceEntity) {
        bookmarks.value = bookmarks.value + (quote.id to quote)
    }

    override suspend fun getQuoteBookmark(quoteId: String): QuoteResourceEntity? =
        bookmarks.value[quoteId]

    override fun getQuoteBookmarkListFlow() = bookmarks.map { it.values.toList() }

    override suspend fun deleteQuoteBookmark(quoteId: String) {
        bookmarks.value = bookmarks.value - quoteId
    }
}

class FakeQuoteOfTheDayDao : QuoteOfTheDayDao {

    private var current: QuoteOfTheDayEntity? = null

    override suspend fun insertQuoteOfTheDay(quote: QuoteOfTheDayEntity) {
        current = quote
    }

    override suspend fun deleteQuoteOfTheDay() {
        current = null
    }

    override suspend fun getQuoteOfTheDay(): QuoteOfTheDayEntity? = current
}

class FakeAuthorBookmarksDao : AuthorBookmarksDao {

    private val bookmarks = MutableStateFlow<Map<String, AuthorResourceEntity>>(emptyMap())

    override suspend fun saveAuthorBookmark(author: AuthorResourceEntity) {
        bookmarks.value = bookmarks.value + (author.id to author)
    }

    override suspend fun getAuthorBookmark(authorId: String): AuthorResourceEntity? =
        bookmarks.value[authorId]

    override fun getAuthorBookmarkListFlow() = bookmarks.map { it.values.toList() }

    override suspend fun deleteAuthorBookmark(authorId: String) {
        bookmarks.value = bookmarks.value - authorId
    }
}

class FakeRecentSearchQueryDao : RecentSearchQueryDao {

    private val queries = MutableStateFlow<List<RecentSearchQueryEntity>>(emptyList())

    override fun getRecentSearchQueryEntities(limit: Int) =
        queries.map { list -> list.sortedByDescending { it.queriedDate }.take(limit) }

    override suspend fun insertOrReplaceRecentSearchQuery(recentSearchQuery: RecentSearchQueryEntity) {
        queries.value = queries.value.filterNot { it.query == recentSearchQuery.query } + recentSearchQuery
    }

    override suspend fun clearRecentSearchQueries() {
        queries.value = emptyList()
    }
}
