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

fun <T> handleError(exception: Exception): Result<T, DataError.Network> {
    return when (exception) {
        is UnresolvedAddressException -> Result.Error(DataError.Network.NO_INTERNET, exception)
        is SerializationException -> Result.Error(DataError.Network.SERIALIZATION, exception)
        is SocketTimeoutException -> Result.Error(DataError.Network.SERVER_ERROR, exception)
        is IOException -> Result.Error(DataError.Network.SERVER_ERROR, exception)
        else -> Result.Error(DataError.Network.UNKNOWN, exception)
    }
}
