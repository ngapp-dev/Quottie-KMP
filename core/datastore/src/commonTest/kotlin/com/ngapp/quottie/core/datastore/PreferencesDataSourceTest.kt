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

import com.ngapp.quottie.core.model.DarkThemeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Exercises [PreferencesDataSource] against a real (but in-memory, via Okio's [FakeFileSystem])
 * datastore file, without needing a platform [android.content.Context].
 */
class PreferencesDataSourceTest {

    // DataStore guards against more than one active instance per file path within the same
    // process, so each test gets its own unique path even though the backing filesystem is
    // already isolated per call.
    private fun newDataSource(): PreferencesDataSource {
        val fileSystem = FakeFileSystem()
        val path = "/test-preferences-${Random.nextLong()}.pb".toPath()
        return PreferencesDataSource(createDataStore(fileSystem) { path })
    }

    @Test
    fun userData_defaultsMatchProtoDefaults() = runTest {
        val dataSource = newDataSource()

        val userData = dataSource.userData.first()

        assertFalse(userData.shouldHideOnboarding)
        assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, userData.darkThemeConfig)
        assertFalse(userData.isReviewShown)
        assertEquals(0L, userData.totalUsageTime)
    }

    @Test
    fun setShouldHideOnboarding_updatesUserData() = runTest {
        val dataSource = newDataSource()

        dataSource.setShouldHideOnboarding(true)

        assertTrue(dataSource.userData.first().shouldHideOnboarding)
    }

    @Test
    fun setDarkThemeConfig_updatesUserData() = runTest {
        val dataSource = newDataSource()

        dataSource.setDarkThemeConfig(DarkThemeConfig.DARK)

        assertEquals(DarkThemeConfig.DARK, dataSource.userData.first().darkThemeConfig)
    }

    @Test
    fun updateTotalUsageTime_updatesUserData() = runTest {
        val dataSource = newDataSource()

        dataSource.updateTotalUsageTime(12_345L)

        assertEquals(12_345L, dataSource.userData.first().totalUsageTime)
    }

    @Test
    fun setReviewShown_updatesUserData() = runTest {
        val dataSource = newDataSource()

        dataSource.setReviewShown(true)

        assertTrue(dataSource.userData.first().isReviewShown)
    }

    @Test
    fun updates_areIndependentOfEachOther() = runTest {
        val dataSource = newDataSource()

        dataSource.setShouldHideOnboarding(true)
        dataSource.updateTotalUsageTime(500L)

        val userData = dataSource.userData.first()
        assertTrue(userData.shouldHideOnboarding)
        assertEquals(500L, userData.totalUsageTime)
        assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, userData.darkThemeConfig)
    }
}
