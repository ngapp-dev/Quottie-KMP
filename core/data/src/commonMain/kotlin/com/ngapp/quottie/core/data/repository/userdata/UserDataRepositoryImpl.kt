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

package com.ngapp.quottie.core.data.repository.userdata

import com.ngapp.quottie.core.datastore.PreferencesDataSource
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.core.model.UserData
import kotlinx.coroutines.flow.Flow

internal class UserDataRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) =
        preferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun updateTotalUsageTime(usageTime: Long) {
        preferencesDataSource.updateTotalUsageTime(usageTime)
    }

    override suspend fun setReviewShown(shown: Boolean) {
        preferencesDataSource.setReviewShown(shown)
    }
}
