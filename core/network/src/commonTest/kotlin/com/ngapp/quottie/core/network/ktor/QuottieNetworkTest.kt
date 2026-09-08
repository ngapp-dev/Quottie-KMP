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

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val BASE_URL = "https://api.quotable.io"

class QuottieNetworkTest {

    private fun networkWithCapturedRequest(): Pair<QuottieNetwork, () -> HttpRequestData> {
        var captured: HttpRequestData? = null
        val client = HttpClient(MockEngine { request ->
            captured = request
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        })
        return QuottieNetwork(BASE_URL, client) to { checkNotNull(captured) }
    }

    @Test
    fun getAuthors_buildsExpectedUrlAndParameters() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getAuthors(
            sortBy = "name",
            order = "asc",
            slug = listOf("plato", "aristotle"),
            pageSize = 20,
            page = 2,
        )

        val url = request().url
        assertEquals("$BASE_URL/authors", url.toString().substringBefore("?"))
        assertEquals("name", url.parameters["sortBy"])
        assertEquals("asc", url.parameters["order"])
        assertEquals("plato,aristotle", url.parameters["slug"])
        assertEquals("20", url.parameters["limit"])
        assertEquals("2", url.parameters["page"])
    }

    @Test
    fun getAuthors_withNullSlug_omitsSlugParameter() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getAuthors(sortBy = "name", order = "asc", slug = null, pageSize = 10, page = 1)

        assertNull(request().url.parameters["slug"])
    }

    @Test
    fun getSearchAuthors_buildsExpectedUrlAndParameters() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getSearchAuthors(
            query = "plato",
            sortBy = "name",
            order = "asc",
            slug = listOf("plato"),
            pageSize = 5,
            page = 1,
        )

        val url = request().url
        assertEquals("$BASE_URL/search/authors", url.toString().substringBefore("?"))
        assertEquals("plato", url.parameters["query"])
        assertEquals("plato", url.parameters["slug"])
    }

    @Test
    fun getRandomQuotes_buildsExpectedUrlAndParameters() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getRandomQuotes(pageSize = 15)

        val url = request().url
        assertEquals("$BASE_URL/quotes/random", url.toString().substringBefore("?"))
        assertEquals("15", url.parameters["limit"])
    }

    @Test
    fun getAuthorDetail_buildsExpectedUrl() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getAuthorDetail("author-123")

        assertEquals("$BASE_URL/authors/author-123", request().url.toString())
    }

    @Test
    fun getQuotes_buildsExpectedUrlAndParameters() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getQuotes(
            sortBy = "content",
            order = "desc",
            slug = listOf("plato"),
            pageSize = 30,
            page = 3,
        )

        val url = request().url
        assertEquals("$BASE_URL/quotes", url.toString().substringBefore("?"))
        assertEquals("content", url.parameters["sortBy"])
        assertEquals("desc", url.parameters["order"])
        assertEquals("plato", url.parameters["author"])
        assertEquals("30", url.parameters["limit"])
        assertEquals("3", url.parameters["page"])
    }

    @Test
    fun getSearchQuotes_buildsExpectedUrlAndParameters() = runTest {
        val (network, request) = networkWithCapturedRequest()

        network.getSearchQuotes(
            query = "wisdom",
            sortBy = "content",
            order = "asc",
            slug = null,
            pageSize = 25,
            page = 1,
        )

        val url = request().url
        assertEquals("$BASE_URL/search/quotes", url.toString().substringBefore("?"))
        assertEquals("wisdom", url.parameters["query"])
        assertNull(url.parameters["slug"])
    }
}
