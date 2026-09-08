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
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HandleErrorTest {

    @Test
    fun unresolvedAddressException_mapsToNoInternet() {
        val result = handleError<String>(UnresolvedAddressException())

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.NO_INTERNET, result.error)
    }

    @Test
    fun serializationException_mapsToSerializationError() {
        val result = handleError<String>(SerializationException("bad json"))

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.SERIALIZATION, result.error)
    }

    @Test
    fun socketTimeoutException_mapsToServerError() {
        val result = handleError<String>(SocketTimeoutException("timeout", null))

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
    }

    @Test
    fun ioException_mapsToServerError() {
        val result = handleError<String>(IOException("io failure"))

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
    }

    @Test
    fun unknownException_mapsToUnknown() {
        val result = handleError<String>(IllegalStateException("mystery"))

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.UNKNOWN, result.error)
    }

    @Test
    fun handleError_preservesOriginalException() {
        val original = IllegalStateException("mystery")

        val result = handleError<String>(original)

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(original, result.exception)
    }
}
