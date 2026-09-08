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

package com.ngapp.quottie.settings.fake

import com.ngapp.quottie.core.data.repository.userdata.UserDataRepository
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Minimal in-memory fake covering exactly what [com.ngapp.quottie.settings.SettingsViewModel] uses. */
class FakeUserDataRepository : UserDataRepository {

    private val state = MutableStateFlow(
        UserData(
            shouldHideOnboarding = false,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            totalUsageTime = 0L,
            isReviewShown = false,
        ),
    )

    override val userData: Flow<UserData> = state

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) = error("Not used by this test")

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        state.value = state.value.copy(darkThemeConfig = darkThemeConfig)
    }

    override suspend fun updateTotalUsageTime(usageTime: Long) = error("Not used by this test")

    override suspend fun setReviewShown(shown: Boolean) = error("Not used by this test")
}
