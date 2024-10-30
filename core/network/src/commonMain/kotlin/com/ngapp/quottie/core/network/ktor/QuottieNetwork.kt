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

package com.ngapp.quottie.core.network.ktor

import com.ngapp.quottie.core.network.QuottieNetworkDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

internal class QuottieNetwork(
    private val baseUrl: String,
    private val httpClient: HttpClient
) : QuottieNetworkDataSource {

    override suspend fun getAuthors(
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse =
        httpClient.get("$baseUrl/authors") {
            parameter("sortBy", sortBy)
            parameter("order", order)
            slug?.let { parameter("slug", it.joinToString(",")) }
            parameter("limit", pageSize)
            parameter("page", page)
        }

    override suspend fun getSearchAuthors(
        query: String,
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse =
        httpClient.get("$baseUrl/search/authors") {
            parameter("query", query)
            parameter("sortBy", sortBy)
            parameter("order", order)
            slug?.let { parameter("slug", it.joinToString(",")) }
            parameter("limit", pageSize)
            parameter("page", page)
        }

    override suspend fun getRandomQuotes(pageSize: Int): HttpResponse =
        httpClient.get("$baseUrl/quotes/random") {
            parameter("limit", pageSize)
        }

    override suspend fun getAuthorDetail(authorId: String): HttpResponse =
        httpClient.get("$baseUrl/authors/$authorId")

    override suspend fun getQuotes(
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse =
        httpClient.get("$baseUrl/quotes") {
            parameter("sortBy", sortBy)
            parameter("order", order)
            slug?.let { parameter("author", it.joinToString(",")) }
            parameter("limit", pageSize)
            parameter("page", page)
        }

    override suspend fun getSearchQuotes(
        query: String,
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse =
        httpClient.get("$baseUrl/search/quotes") {
            parameter("query", query)
            parameter("sortBy", sortBy)
            parameter("order", order)
            slug?.let { parameter("slug", it.joinToString(",")) }
            parameter("limit", pageSize)
            parameter("page", page)
        }
}
