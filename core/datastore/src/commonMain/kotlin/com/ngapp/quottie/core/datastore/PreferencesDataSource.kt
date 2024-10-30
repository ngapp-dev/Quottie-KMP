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

package com.ngapp.quottie.core.datastore

import androidx.datastore.core.DataStore
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.core.model.UserData
import kotlinx.coroutines.flow.map

class PreferencesDataSource(private val userPreferences: DataStore<UserPreferences> = getDataStore()) {
    val userData = userPreferences.data
        .map {
            UserData(
                shouldHideOnboarding = it.should_hide_onboarding,
                darkThemeConfig = when (it.dark_theme_config) {
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        -> DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                isReviewShown = it.is_review_shown,
                totalUsageTime = it.total_usage_time,
            )
        }


    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData { data ->
            data.copy(should_hide_onboarding = shouldHideOnboarding)
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData { data ->
            data.copy(
                dark_theme_config = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            )
        }
    }

    suspend fun updateTotalUsageTime(totalUsageTime: Long) {
        userPreferences.updateData { data ->
            data.copy(total_usage_time = totalUsageTime)
        }
    }

    suspend fun setReviewShown(isReviewShown: Boolean) {
        userPreferences.updateData { data ->
            data.copy(is_review_shown = isReviewShown)
        }
    }
}
