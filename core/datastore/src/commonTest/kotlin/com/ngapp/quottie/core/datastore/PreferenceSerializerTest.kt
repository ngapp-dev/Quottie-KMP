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

import kotlinx.coroutines.test.runTest
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferenceSerializerTest {

    @Test
    fun defaultValue_isEmptyUserPreferences() {
        assertEquals(UserPreferences(), PreferenceSerializer.defaultValue)
    }

    @Test
    fun writeTo_thenReadFrom_roundTripsPreferences() = runTest {
        val preferences = UserPreferences(
            should_hide_onboarding = true,
            dark_theme_config = DarkThemeConfigProto.DARK_THEME_CONFIG_DARK,
            total_usage_time = 42_000L,
            is_review_shown = true,
        )
        val buffer = Buffer()

        PreferenceSerializer.writeTo(preferences, buffer)
        val restored = PreferenceSerializer.readFrom(buffer)

        assertEquals(preferences, restored)
    }

    @Test
    fun readFrom_emptyBuffer_returnsDefaultInstance() = runTest {
        val restored = PreferenceSerializer.readFrom(Buffer())

        assertEquals(UserPreferences(), restored)
    }
}
