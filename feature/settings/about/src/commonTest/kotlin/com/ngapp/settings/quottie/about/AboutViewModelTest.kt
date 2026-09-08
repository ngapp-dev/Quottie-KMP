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

package com.ngapp.settings.quottie.about

import app.cash.turbine.test
import com.ngapp.quottie.core.common.result.DataError
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.model.githubuser.GithubUserResource
import com.ngapp.settings.quottie.about.fake.FakeGithubUserRepository
import com.ngapp.settings.quottie.about.state.AboutEvent
import com.ngapp.settings.quottie.about.state.AboutUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AboutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val githubUserRepository = FakeGithubUserRepository()

    private val githubUser = GithubUserResource(
        id = 1,
        login = "ngapp-dev",
        avatarUrl = "avatar",
        url = "url",
        htmlUrl = "html",
        name = "NGApps Dev",
        company = "",
        blog = "",
        location = "",
        email = "",
        bio = "",
        twitterUsername = "",
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_success_showsGithubUser() = runTest(testDispatcher) {
        githubUserRepository.result = Result.Success(githubUser)
        val viewModel = AboutViewModel(githubUserRepository)

        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = assertIs<AboutUiState.Success>(awaitItem())
            assertEquals("ngapp-dev", state.githubUser.login)
        }
    }

    @Test
    fun uiState_error_emitsErrorStateAndEvent() = runTest(testDispatcher) {
        githubUserRepository.result = Result.Error(DataError.Network.SERVER_ERROR, Exception("boom"))
        val viewModel = AboutViewModel(githubUserRepository)
        val events = mutableListOf<AboutEvent>()
        backgroundScope.launch { viewModel.events.toList(events) }

        viewModel.uiState.test {
            skipItems(1) // Loading
            assertIs<AboutUiState.Error>(awaitItem())
        }
        assertIs<AboutEvent.Error>(events.single())
    }
}
