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

package com.ngapp.quottie.core.data.repository.githubuser

import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.fake.FakeGithubNetworkDataSource
import com.ngapp.quottie.core.data.fake.mockHttpClient
import com.ngapp.quottie.core.model.githubuser.GithubUserResource
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultGithubUserRepositoryTest {

    private fun repository(status: HttpStatusCode, body: String) =
        DefaultGithubUserRepository(FakeGithubNetworkDataSource(mockHttpClient(status, body)))

    @Test
    fun getGithubUser_success_mapsToResource() = runTest {
        val repository = repository(
            HttpStatusCode.OK,
            """{"login":"ngapp-dev","id":49076456,"avatar_url":"avatar","url":"url","html_url":"html","name":"NGApps Dev","company":"","blog":"blog","location":"Poznan","email":"","bio":"","twitter_username":""}""",
        )

        val result = repository.getGithubUser()

        assertIs<Result.Success<GithubUserResource, DataError.Network>>(result)
        assertEquals("ngapp-dev", result.data.login)
        assertEquals(49076456, result.data.id)
        assertEquals("Poznan", result.data.location)
    }

    @Test
    fun getGithubUser_serverError_mapsToNetworkError() = runTest {
        val repository = repository(HttpStatusCode.InternalServerError, "")

        val result = repository.getGithubUser()

        assertIs<Result.Error<GithubUserResource, DataError.Network>>(result)
        assertEquals(DataError.Network.SERVER_ERROR, result.error)
    }

    @Test
    fun getGithubUser_unauthorized_mapsToAuthorizationError() = runTest {
        val repository = repository(HttpStatusCode.Unauthorized, "")

        val result = repository.getGithubUser()

        assertIs<Result.Error<GithubUserResource, DataError.Network>>(result)
        assertEquals(DataError.Network.AUTHORIZATION_ERROR, result.error)
    }
}
