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

import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.model.wiki.WikiResource
import kotlinx.coroutines.flow.Flow

interface AuthorRepository {

    suspend fun getAuthorsPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<AuthorResource>>

    suspend fun getAuthors(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<AuthorResource>, DataError.Network>>

    suspend fun getSearchAuthors(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<AuthorResource>, DataError.Network>>

    suspend fun getAuthorsWiki(authorTitle: String): Flow<Result<WikiResource, DataError.Network>>

    suspend fun getAuthorDetail(authorId: String): Result<AuthorResource, DataError.Network>

    suspend fun saveAuthorBookmark(author: AuthorResource, isBookmarked: Boolean)

    suspend fun deleteAuthorBookmark(authorId: String)

    suspend fun getAuthorBookmark(authorId: String): AuthorResource?

    fun getAuthorBookmarkList(): Flow<List<AuthorResource>>
}