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

package com.ngapp.quottie.core.network.util

import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HandleResponseTest {

    private fun clientRespondingWith(status: HttpStatusCode, body: String = "{}"): HttpClient =
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

    @Test
    fun successStatus_parsesBodyAsSuccess() = runTest {
        val client = clientRespondingWith(HttpStatusCode.OK, """{"content":"hello"}""")
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<Map<String, String>>() }

        assertIs<Result.Success<Map<String, String>, DataError.Network>>(result)
        assertEquals("hello", result.data["content"])
    }

    @Test
    fun parseFailure_onSuccessStatus_mapsToUnknown() = runTest {
        val client = clientRespondingWith(HttpStatusCode.OK, "not json")
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<Map<String, String>>() }

        assertIs<Result.Error<Map<String, String>, DataError.Network>>(result)
        assertEquals(DataError.Network.UNKNOWN, result.error)
    }

    @Test
    fun unauthorized_mapsToAuthorizationError() = runTest {
        val client = clientRespondingWith(HttpStatusCode.Unauthorized)
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<String>() }

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.AUTHORIZATION_ERROR, result.error)
    }

    @Test
    fun conflict_mapsToConflictError() = runTest {
        val client = clientRespondingWith(HttpStatusCode.Conflict)
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<String>() }

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.CONFLICT, result.error)
    }

    @Test
    fun requestTimeout_mapsToRequestTimeoutError() = runTest {
        val client = clientRespondingWith(HttpStatusCode.RequestTimeout)
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<String>() }

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.REQUEST_TIMEOUT, result.error)
    }

    @Test
    fun payloadTooLarge_mapsToPayloadTooLargeError() = runTest {
        val client = clientRespondingWith(HttpStatusCode.PayloadTooLarge)
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<String>() }

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.PAYLOAD_TOO_LARGE, result.error)
    }

    @Test
    fun serverError_mapsToServerError() = runTest {
        val client = clientRespondingWith(HttpStatusCode.InternalServerError)
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<String>() }

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
    }

    @Test
    fun unmappedStatus_mapsToUnknown() = runTest {
        val client = clientRespondingWith(HttpStatusCode.NotFound)
        val response = client.get("https://example.com")

        val result = handleResponse(response) { it.body<String>() }

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.UNKNOWN, result.error)
    }
}
