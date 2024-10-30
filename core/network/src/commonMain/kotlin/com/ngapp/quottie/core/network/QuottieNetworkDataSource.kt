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

package com.ngapp.quottie.core.network

import io.ktor.client.statement.HttpResponse

/**
 * Interface representing network calls to the Quottie backend
 */
interface QuottieNetworkDataSource {

    suspend fun getAuthors(
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse

    suspend fun getSearchAuthors(
        query: String,
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse

    suspend fun getRandomQuotes(pageSize: Int): HttpResponse

    suspend fun getAuthorDetail(authorId: String): HttpResponse

    suspend fun getQuotes(
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse

    suspend fun getSearchQuotes(
        query: String,
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse
}
