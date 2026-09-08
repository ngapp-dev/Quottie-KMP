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

import com.ngapp.quottie.core.network.GithubNetworkDataSource
import com.ngapp.quottie.core.network.QuottieNetworkDataSource
import com.ngapp.quottie.core.network.WikipediaNetworkDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Builds an [HttpClient] whose single [MockEngine] response is fixed for the lifetime of the
 * client, configured the same way the production Ktor client is (JSON content negotiation with
 * unknown keys ignored). Used to hand canned [HttpResponse]s to the fake network data sources
 * below, so repository tests can focus on mapping/caching logic instead of request building
 * (already covered by core/network's own tests).
 */
fun mockHttpClient(status: HttpStatusCode, body: String): HttpClient =
    HttpClient(MockEngine { _ ->
        respond(
            content = body,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

class FakeQuottieNetworkDataSource(private val client: HttpClient) : QuottieNetworkDataSource {
    private suspend fun response(): HttpResponse = client.get("https://test.invalid")

    override suspend fun getAuthors(
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse = response()

    override suspend fun getSearchAuthors(
        query: String,
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse = response()

    override suspend fun getRandomQuotes(pageSize: Int): HttpResponse = response()

    override suspend fun getAuthorDetail(authorId: String): HttpResponse = response()

    override suspend fun getQuotes(
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse = response()

    override suspend fun getSearchQuotes(
        query: String,
        sortBy: String,
        order: String,
        slug: List<String>?,
        pageSize: Int,
        page: Int,
    ): HttpResponse = response()
}

class FakeWikipediaNetworkDataSource(private val client: HttpClient) : WikipediaNetworkDataSource {
    override suspend fun getAuthorsWiki(authorTitle: String): HttpResponse =
        client.get("https://test.invalid")
}

class FakeGithubNetworkDataSource(private val client: HttpClient) : GithubNetworkDataSource {
    override suspend fun getGithubUser(): HttpResponse = client.get("https://test.invalid")
}
