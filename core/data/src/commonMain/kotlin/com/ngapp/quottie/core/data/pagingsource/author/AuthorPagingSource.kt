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

package com.ngapp.quottie.core.data.pagingsource.author

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import com.ngapp.quottie.core.model.wiki.WikiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class AuthorPagingSource(
    private val filter: ResultFilter,
    private val onGetAuthors: suspend (Int) -> Flow<Result<List<AuthorResource>, DataError.Network>>,
    private val onGetSearchAuthors: suspend (ResultFilter, Int) -> Flow<Result<List<AuthorResource>, DataError.Network>>,
    private val onGetAuthorsWiki: suspend (String) -> Flow<Result<WikiResource, DataError.Network>>
) : PagingSource<Int, AuthorResource>() {

    override fun getRefreshKey(state: PagingState<Int, AuthorResource>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AuthorResource> {
        val page = params.key ?: 1
        return try {
            val response = when {
                filter.searchQuery.isEmpty() -> onGetAuthors(page)
                filter.searchQuery.toDoubleOrNull() != null -> {
                    val editFilter = filter.copy(searchQuery = "\" ${filter.searchQuery} \"")
                    onGetSearchAuthors(editFilter, page)
                }

                else -> onGetSearchAuthors(filter, page)
            }
            var authorList = emptyList<AuthorResource>()
            response.collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val authorResult = result.data
                        if (authorResult.isNotEmpty()) {
                            val authorTitles = authorResult.joinToString("|") { author ->
                                author.link.substringAfterLast("/wiki/")
                            }
                            onGetAuthorsWiki(authorTitles).collectLatest { wikiResult ->
                                when (wikiResult) {
                                    is Result.Success -> {
                                        val authorsWiki = wikiResult.data
                                        authorList = authorResult.map { author ->
                                            val normalizedTitle =
                                                authorsWiki.query.normalizedResource
                                                    ?.find {
                                                        it.from == author.link.substringAfterLast("/wiki/")
                                                    }
                                                    ?.to ?: author.name

                                            val wikiPage =
                                                authorsWiki.query.pages.values.find { it.title == normalizedTitle }
                                            author.copy(image = wikiPage?.thumbnailResource?.source.orEmpty())
                                        }
                                    }

                                    is Result.Error -> throw wikiResult.exception
                                }
                            }
                        }
                    }

                    is Result.Error -> throw result.exception
                }
            }
            LoadResult.Page(
                data = authorList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (authorList.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}