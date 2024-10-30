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

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.model.author.asResource
import com.ngapp.quottie.core.data.model.wiki.asResource
import com.ngapp.quottie.core.data.pagingsource.author.AuthorPagingSource
import com.ngapp.quottie.core.database.dao.AuthorBookmarksDao
import com.ngapp.quottie.core.database.model.author.AuthorResourceEntity
import com.ngapp.quottie.core.database.model.author.asEntity
import com.ngapp.quottie.core.database.model.author.asExternalModel
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.model.wiki.WikiResource
import com.ngapp.quottie.core.network.QuottieNetworkDataSource
import com.ngapp.quottie.core.network.WikipediaNetworkDataSource
import com.ngapp.quottie.core.network.model.author.NetworkAuthor
import com.ngapp.quottie.core.network.model.response.NetworkResponse
import com.ngapp.quottie.core.network.model.wiki.NetworkWiki
import com.ngapp.quottie.core.network.util.handleError
import com.ngapp.quottie.core.network.util.handleResponse
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DefaultAuthorRepository(
    private val quottieNetwork: QuottieNetworkDataSource,
    private val wikipediaNetwork: WikipediaNetworkDataSource,
    private val authorBookmarkDao: AuthorBookmarksDao,
) : AuthorRepository {

    override suspend fun getAuthorsPaging(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
    ): Flow<PagingData<AuthorResource>> = Pager(
        config = PagingConfig(pageSize = pageSize),
        pagingSourceFactory = {
            AuthorPagingSource(
                filter = filter,
                onGetAuthors = { page -> getAuthors(filter, slug, pageSize, page) },
                onGetSearchAuthors = { filter, page ->
                    getSearchAuthors(filter, slug, pageSize, page)
                },
                onGetAuthorsWiki = { authorTitle -> getAuthorsWiki(authorTitle) }
            )
        }
    ).flow

    override suspend fun getAuthors(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<AuthorResource>, DataError.Network>> = flow {
        try {
            val response = quottieNetwork.getAuthors(
                sortBy = filter.sortField.field,
                order = filter.sortOrder.field,
                slug = slug,
                pageSize = pageSize,
                page = page
            )
            val result = handleResponse(response) { it.body<NetworkResponse<NetworkAuthor>>() }
            if (result is Result.Success) {
                emit(Result.Success(result.data.results.map(NetworkAuthor::asResource)))
            } else {
                emit(Result.Error((result as Result.Error).error, result.exception))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override suspend fun getSearchAuthors(
        filter: ResultFilter,
        slug: List<String>,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<AuthorResource>, DataError.Network>> = flow {
        try {
            val response = quottieNetwork.getSearchAuthors(
                query = filter.searchQuery,
                sortBy = filter.sortField.field,
                order = filter.sortOrder.field,
                slug = slug,
                pageSize = pageSize,
                page = page
            )
            val result = handleResponse(response) { it.body<NetworkResponse<NetworkAuthor>>() }
            if (result is Result.Success) {
                emit(Result.Success(result.data.results.map(NetworkAuthor::asResource)))
            } else {
                emit(Result.Error((result as Result.Error).error, result.exception))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override suspend fun getAuthorsWiki(authorTitle: String): Flow<Result<WikiResource, DataError.Network>> =
        flow {
            try {
                val response = wikipediaNetwork.getAuthorsWiki(authorTitle)
                val result = handleResponse(response) { it.body<NetworkWiki>() }
                if (result is Result.Success) {
                    emit(Result.Success(result.data.asResource()))
                } else {
                    emit(Result.Error((result as Result.Error).error, result.exception))
                }
            } catch (e: Exception) {
                emit(handleError(e))
            }
        }

    override suspend fun getAuthorDetail(authorId: String): Result<AuthorResource, DataError.Network> =
            try {
                val response = quottieNetwork.getAuthorDetail(authorId)
                val result = handleResponse(response) { it.body<NetworkAuthor>() }
                if (result is Result.Success) {
                    Result.Success(result.data.asResource())
                } else {
                    Result.Error((result as Result.Error).error, result.exception)
                }
            } catch (e: Exception) {
                handleError(e)
            }

    override suspend fun saveAuthorBookmark(author: AuthorResource, isBookmarked: Boolean) =
        authorBookmarkDao.saveAuthorBookmark(author.copy(isBookmarked = isBookmarked).asEntity())

    override suspend fun deleteAuthorBookmark(authorId: String) =
        authorBookmarkDao.deleteAuthorBookmark(authorId)

    override suspend fun getAuthorBookmark(authorId: String): AuthorResource? =
        authorBookmarkDao.getAuthorBookmark(authorId)?.asExternalModel()

    override fun getAuthorBookmarkList(): Flow<List<AuthorResource>> =
        authorBookmarkDao.getAuthorBookmarkListFlow()
            .map { it.map(AuthorResourceEntity::asExternalModel) }
}