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
import com.ngapp.quottie.core.data.model.githubuser.asResource
import com.ngapp.quottie.core.model.githubuser.GithubUserResource
import com.ngapp.quottie.core.network.GithubNetworkDataSource
import com.ngapp.quottie.core.network.model.githubuser.NetworkGithubUser
import com.ngapp.quottie.core.network.util.handleError
import com.ngapp.quottie.core.network.util.handleResponse
import io.ktor.client.call.body

class DefaultGithubUserRepository(
    val githubNetwork: GithubNetworkDataSource,
) : GithubUserRepository {

    override suspend fun getGithubUser(): Result<GithubUserResource, DataError.Network> =
        try {
            val response = githubNetwork.getGithubUser()
            val result = handleResponse(response) { it.body<NetworkGithubUser>() }
            if (result is Result.Success) {
                Result.Success(result.data.asResource())
            } else {
                Result.Error((result as Result.Error).error, result.exception)
            }
        } catch (e: Exception) {
            handleError(e)
        }

}