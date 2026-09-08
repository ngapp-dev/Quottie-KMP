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

package com.ngapp.quottie.core.data.repository.author

import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.fake.FakeAuthorBookmarksDao
import com.ngapp.quottie.core.data.fake.FakeQuottieNetworkDataSource
import com.ngapp.quottie.core.data.fake.FakeWikipediaNetworkDataSource
import com.ngapp.quottie.core.data.fake.mockHttpClient
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.model.wiki.WikiResource
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultAuthorRepositoryTest {

    private val authorBookmarksDao = FakeAuthorBookmarksDao()

    private fun repository(
        quottieStatus: HttpStatusCode,
        quottieBody: String,
        wikiStatus: HttpStatusCode = HttpStatusCode.OK,
        wikiBody: String = "{}",
    ) = DefaultAuthorRepository(
        quottieNetwork = FakeQuottieNetworkDataSource(mockHttpClient(quottieStatus, quottieBody)),
        wikipediaNetwork = FakeWikipediaNetworkDataSource(mockHttpClient(wikiStatus, wikiBody)),
        authorBookmarkDao = authorBookmarksDao,
    )

    @Test
    fun getAuthors_success_mapsPagedResultsToResources() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """{"totalCount":1,"results":[{"_id":"a1","bio":"bio","link":"link","name":"name","slug":"slug","quoteCount":3}]}""",
        )

        val result =
            repository.getAuthors(ResultFilter(), slug = emptyList(), pageSize = 10, page = 1).first()

        assertIs<Result.Success<List<AuthorResource>, DataError.Network>>(result)
        assertEquals("a1", result.data.single().id)
    }

    @Test
    fun getAuthors_serverError_mapsToNetworkError() = runTest {
        val repository = repository(HttpStatusCode.InternalServerError, "")

        val result =
            repository.getAuthors(ResultFilter(), slug = emptyList(), pageSize = 10, page = 1).first()

        assertIs<Result.Error<List<AuthorResource>, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
    }

    @Test
    fun getSearchAuthors_success_mapsPagedResultsToResources() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """{"totalCount":1,"results":[{"_id":"a2","bio":"bio","link":"link","name":"name","slug":"slug","quoteCount":1}]}""",
        )

        val result = repository.getSearchAuthors(
            ResultFilter(),
            slug = emptyList(),
            pageSize = 10,
            page = 1,
        ).first()

        assertIs<Result.Success<List<AuthorResource>, DataError.Network>>(result)
        assertEquals("a2", result.data.single().id)
    }

    @Test
    fun getAuthorsWiki_success_mapsToWikiResource() = runTest {
        val repository = repository(
            quottieStatus = HttpStatusCode.OK,
            quottieBody = "{}",
            wikiBody = """{"batchComplete":"","query":{"pages":{"1":{"pageid":1,"ns":0,"title":"Plato"}}}}""",
        )

        val result = repository.getAuthorsWiki("Plato").first()

        assertIs<Result.Success<WikiResource, DataError.Network>>(result)
        assertEquals("Plato", result.data.query.pages.getValue("1").title)
    }

    @Test
    fun getAuthorDetail_success_mapsToAuthorResource() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """{"_id":"a1","bio":"bio","link":"link","name":"name","slug":"slug","quoteCount":3}""",
        )

        val result = repository.getAuthorDetail("a1")

        assertIs<Result.Success<AuthorResource, DataError.Network>>(result)
        assertEquals("a1", result.data.id)
    }

    @Test
    fun getAuthorDetail_notFound_mapsToUnknownError() = runTest {
        val repository = repository(HttpStatusCode.NotFound, "")

        val result = repository.getAuthorDetail("missing")

        assertIs<Result.Error<AuthorResource, DataError.Network>>(result)
        assertEquals(DataError.Network.UNKNOWN, result.error)
    }

    private fun author(id: String) =
        AuthorResource(id, "bio", "description", "link", "name", "slug", 3)

    @Test
    fun saveAuthorBookmark_thenGetAuthorBookmark_returnsSavedBookmark() = runTest {
        val repository = repository(HttpStatusCode.OK, "{}")

        repository.saveAuthorBookmark(author("a1"), isBookmarked = true)

        assertEquals("a1", repository.getAuthorBookmark("a1")?.id)
    }

    @Test
    fun deleteAuthorBookmark_removesSavedBookmark() = runTest {
        val repository = repository(HttpStatusCode.OK, "{}")
        repository.saveAuthorBookmark(author("a1"), isBookmarked = true)

        repository.deleteAuthorBookmark("a1")

        assertEquals(null, repository.getAuthorBookmark("a1"))
    }

    @Test
    fun getAuthorBookmarkList_mapsEntitiesToExternalModels() = runTest {
        val repository = repository(HttpStatusCode.OK, "{}")
        repository.saveAuthorBookmark(author("a1"), true)

        val bookmarks = repository.getAuthorBookmarkList().first()

        assertEquals(listOf("a1"), bookmarks.map { it.id })
    }
}
