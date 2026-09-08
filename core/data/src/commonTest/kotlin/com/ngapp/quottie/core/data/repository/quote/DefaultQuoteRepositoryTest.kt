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

import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.fake.FakeQuoteBookmarksDao
import com.ngapp.quottie.core.data.fake.FakeQuoteOfTheDayDao
import com.ngapp.quottie.core.data.fake.FakeQuottieNetworkDataSource
import com.ngapp.quottie.core.data.fake.mockHttpClient
import com.ngapp.quottie.core.database.model.quote.QuoteOfTheDayEntity
import com.ngapp.quottie.core.model.quote.QuoteResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultQuoteRepositoryTest {

    private val quoteOfTheDayDao = FakeQuoteOfTheDayDao()
    private val quoteBookmarksDao = FakeQuoteBookmarksDao()

    private fun repository(status: HttpStatusCode, body: String) = DefaultQuoteRepository(
        quoteOfTheDayDao = quoteOfTheDayDao,
        quoteBookmarksDao = quoteBookmarksDao,
        quottieNetwork = FakeQuottieNetworkDataSource(mockHttpClient(status, body)),
    )

    @Test
    fun getRandomQuotes_success_mapsNetworkQuotesToResources() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """[{"_id":"q1","content":"content","author":"author","length":7,"tags":["life"]}]""",
        )

        val result = repository.getRandomQuotes(pageSize = 1).first()

        assertIs<Result.Success<List<QuoteResource>, DataError.Network>>(result)
        assertEquals(
            listOf(QuoteResource("q1", "content", "author", 7, listOf("life"))),
            result.data,
        )
    }

    @Test
    fun getRandomQuotes_serverError_mapsToNetworkError() = runTest {
        val repository = repository(HttpStatusCode.InternalServerError, "")

        val result = repository.getRandomQuotes(pageSize = 1).first()

        assertIs<Result.Error<List<QuoteResource>, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
    }

    @Test
    fun getQuotes_success_mapsPagedResultsToResources() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """{"totalCount":1,"results":[{"_id":"q1","content":"content","author":"author","length":7,"tags":[]}]}""",
        )

        val result =
            repository.getQuotes(ResultFilter(), slug = emptyList(), pageSize = 10, page = 1).first()

        assertIs<Result.Success<List<QuoteResource>, DataError.Network>>(result)
        assertEquals("q1", result.data.single().id)
    }

    @Test
    fun getSearchQuotes_success_mapsPagedResultsToResources() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """{"totalCount":1,"results":[{"_id":"q2","content":"content","author":"author","length":3,"tags":[]}]}""",
        )

        val result = repository.getSearchQuotes(
            ResultFilter(),
            slug = emptyList(),
            pageSize = 10,
            page = 1,
        ).first()

        assertIs<Result.Success<List<QuoteResource>, DataError.Network>>(result)
        assertEquals("q2", result.data.single().id)
    }

    @Test
    fun getQuoteOfTheDay_whenCachedForToday_returnsCacheWithoutOverwritingIt() = runTest {
        quoteOfTheDayDao.insertQuoteOfTheDay(
            QuoteOfTheDayEntity(
                id = "cached",
                content = "cached content",
                author = "cached author",
                length = 1,
                tags = emptyList(),
                lastFetched = Clock.System.now(),
            ),
        )
        // A network response that would produce a different quote if it were fetched.
        val repository = repository(
            HttpStatusCode.OK,
            """[{"_id":"fresh","content":"fresh content","author":"fresh author","length":1,"tags":[]}]""",
        )

        val result = repository.getQuoteOfTheDay().first()

        assertIs<Result.Success<QuoteResource, DataError.Network>>(result)
        assertEquals("cached", result.data.id)
        assertEquals("cached", quoteOfTheDayDao.getQuoteOfTheDay()?.id)
    }

    @Test
    fun getQuoteOfTheDay_whenNoCache_fetchesAndPersistsFromNetwork() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """[{"_id":"fresh","content":"fresh content","author":"fresh author","length":1,"tags":[]}]""",
        )

        val result = repository.getQuoteOfTheDay().first()

        assertIs<Result.Success<QuoteResource, DataError.Network>>(result)
        assertEquals("fresh", result.data.id)
        assertEquals("fresh", quoteOfTheDayDao.getQuoteOfTheDay()?.id)
    }

    @Test
    fun saveQuoteBookmark_thenGetQuoteBookmark_returnsSavedBookmark() = runTest {
        val repository = repository(HttpStatusCode.OK, "{}")
        val quote = QuoteResource("q1", "content", "author", 7, listOf("life"))

        repository.saveQuoteBookmark(quote, isBookmarked = true)

        assertEquals("q1", repository.getQuoteBookmark("q1")?.id)
        assertEquals(true, repository.getQuoteBookmark("q1")?.isBookmarked)
    }

    @Test
    fun deleteQuoteBookmark_removesSavedBookmark() = runTest {
        val repository = repository(HttpStatusCode.OK, "{}")
        val quote = QuoteResource("q1", "content", "author", 7, listOf("life"))
        repository.saveQuoteBookmark(quote, isBookmarked = true)

        repository.deleteQuoteBookmark("q1")

        assertEquals(null, repository.getQuoteBookmark("q1"))
    }

    @Test
    fun getQuoteBookmarkList_mapsEntitiesToExternalModels() = runTest {
        val repository = repository(HttpStatusCode.OK, "{}")
        repository.saveQuoteBookmark(QuoteResource("q1", "content", "author", 7, emptyList()), true)

        val bookmarks = repository.getQuoteBookmarkList().first()

        assertEquals(listOf("q1"), bookmarks.map { it.id })
    }
}
