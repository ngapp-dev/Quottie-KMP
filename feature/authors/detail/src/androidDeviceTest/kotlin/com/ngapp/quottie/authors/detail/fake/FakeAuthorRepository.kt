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

package com.ngapp.quottie.authors.detail.fake

import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.model.wiki.NormalizedResource
import com.ngapp.quottie.core.model.wiki.WikiQueryResource
import com.ngapp.quottie.core.model.wiki.WikiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** Minimal in-memory fake covering exactly what [com.ngapp.quottie.authors.detail.AuthorDetailViewModel] uses. */
class FakeAuthorRepository : AuthorRepository {

    var localBookmark: AuthorResource? = null
    var authorDetailResult: Result<AuthorResource, DataError.Network> =
        Result.Error(DataError.Network.UNKNOWN, Exception("not configured"))
    var authorsWikiResult: Result<WikiResource, DataError.Network> =
        Result.Success(WikiResource(batchComplete = "", query = WikiQueryResource(emptyList(), emptyMap())))

    private val bookmarks = MutableStateFlow<Map<String, AuthorResource>>(emptyMap())
    val savedBookmarks: List<AuthorResource> get() = bookmarks.value.values.toList()

    override suspend fun getAuthorsPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<AuthorResource>> = error("Not used by this test")

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
        flowOf(authorsWikiResult)

    override suspend fun getAuthorDetail(authorId: String): Result<AuthorResource, DataError.Network> =
        authorDetailResult

    override suspend fun saveAuthorBookmark(author: AuthorResource, isBookmarked: Boolean) {
        bookmarks.value = bookmarks.value + (author.id to author.copy(isBookmarked = isBookmarked))
    }

    override suspend fun deleteAuthorBookmark(authorId: String) {
        bookmarks.value = bookmarks.value - authorId
    }

    override suspend fun getAuthorBookmark(authorId: String): AuthorResource? =
        bookmarks.value[authorId] ?: localBookmark?.takeIf { it.id == authorId }

    override fun getAuthorBookmarkList(): Flow<List<AuthorResource>> = bookmarks.map { it.values.toList() }
}
