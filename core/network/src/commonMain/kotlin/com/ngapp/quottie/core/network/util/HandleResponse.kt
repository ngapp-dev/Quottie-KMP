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

import io.ktor.client.statement.HttpResponse
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result

suspend fun <T> handleResponse(
    response: HttpResponse,
    parse: suspend (HttpResponse) -> T
): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                val result = parse(response)
                Result.Success(result)
            } catch (e: Exception) {
                Result.Error(DataError.Network.UNKNOWN, e)
            }
        }
        401 -> Result.Error(DataError.Network.AUTHORIZATION_ERROR, Exception("Authorization error"))
        409 -> Result.Error(DataError.Network.CONFLICT, Exception("Conflict error"))
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT, Exception("Request timeout"))
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE, Exception("Payload too large"))
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR, Exception("Server error"))
        else -> Result.Error(DataError.Network.UNKNOWN, Exception("Unknown error"))
    }
}
