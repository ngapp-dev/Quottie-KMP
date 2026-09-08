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

package com.ngapp.quottie.onboarding

import app.cash.turbine.test
import com.ngapp.quottie.onboarding.fake.FakeUserDataRepository
import com.ngapp.quottie.onboarding.state.OnboardingAction
import com.ngapp.quottie.onboarding.state.OnboardingUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userDataRepository = FakeUserDataRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_whenOnboardingNotHidden_isShown() = runTest(testDispatcher) {
        val viewModel = OnboardingViewModel(userDataRepository)

        viewModel.uiState.test {
            skipItems(1) // Loading
            assertIs<OnboardingUiState.Shown>(awaitItem())
        }
    }

    @Test
    fun dismissOnboarding_hidesOnboarding() = runTest(testDispatcher) {
        val viewModel = OnboardingViewModel(userDataRepository)

        viewModel.uiState.test {
            skipItems(2) // Loading, Shown
            viewModel.triggerAction(OnboardingAction.DismissOnboarding)
            assertIs<OnboardingUiState.NotShown>(awaitItem())
        }
    }
}
