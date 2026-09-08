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

package com.ngapp.quottie.settings

import app.cash.turbine.test
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.settings.fake.FakeUserDataRepository
import com.ngapp.quottie.settings.state.SettingsAction
import com.ngapp.quottie.settings.state.SettingsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SettingsViewModelTest {

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
    fun settingsUiState_reflectsCurrentDarkThemeConfig() = runTest(testDispatcher) {
        val viewModel = SettingsViewModel(userDataRepository)

        viewModel.settingsUiState.test {
            skipItems(1) // Loading
            val state = assertIs<SettingsUiState.Success>(awaitItem())
            assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, state.darkThemeConfig)
        }
    }

    @Test
    fun updateDarkThemeConfig_updatesUiState() = runTest(testDispatcher) {
        val viewModel = SettingsViewModel(userDataRepository)

        viewModel.settingsUiState.test {
            skipItems(2) // Loading, initial Success
            viewModel.triggerAction(SettingsAction.UpdateDarkThemeConfig(DarkThemeConfig.DARK))
            val state = assertIs<SettingsUiState.Success>(awaitItem())
            assertEquals(DarkThemeConfig.DARK, state.darkThemeConfig)
        }
    }
}
