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

package com.ngapp.quottie.core.common.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResultTest {

    @Test
    fun success_holdsData() {
        val result: Result<String, DataError.Network> = Result.Success("payload")

        assertIs<Result.Success<String, DataError.Network>>(result)
        assertEquals("payload", result.data)
    }

    @Test
    fun error_holdsErrorAndException() {
        val exception = IllegalStateException("boom")
        val result: Result<String, DataError.Network> =
            Result.Error(DataError.Network.SERVER_ERROR, exception, "boom")

        assertIs<Result.Error<String, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
        assertEquals(exception, result.exception)
        assertEquals("boom", result.message)
    }

    @Test
    fun error_messageDefaultsToNull() {
        val result = Result.Error<String, DataError.Network>(DataError.Network.UNKNOWN, Exception("oops"))

        assertEquals(null, result.message)
    }

    @Test
    fun success_and_error_areMutuallyExclusive() {
        val successResult: Result<Int, DataError.Local> = Result.Success(1)
        val errorResult: Result<Int, DataError.Local> = Result.Error(DataError.Local.DISK_FULL, Exception())

        assertTrue(successResult is Result.Success)
        assertFalse(successResult is Result.Error<*, *>)
        assertTrue(errorResult is Result.Error)
        assertFalse(errorResult is Result.Success<*, *>)
    }

    @Test
    fun dataError_network_and_local_areDistinctErrorFamilies() {
        val networkError: DataError = DataError.Network.NO_INTERNET
        val localError: DataError = DataError.Local.DISK_FULL

        assertIs<DataError.Network>(networkError)
        assertIs<DataError.Local>(localError)
    }
}
