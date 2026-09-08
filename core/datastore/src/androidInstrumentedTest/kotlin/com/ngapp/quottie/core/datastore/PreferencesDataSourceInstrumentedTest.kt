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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ngapp.quottie.core.model.DarkThemeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toPath
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Exercises [PreferencesDataSource] against a real, file-backed DataStore stored under the
 * instrumentation app's private files directory on a device/emulator.
 */
@RunWith(AndroidJUnit4::class)
class PreferencesDataSourceInstrumentedTest {

    // DataStore guards against more than one active instance per file path within the same
    // process, so each test writes to its own uniquely named file.
    private val testFileName = "instrumented-test-preferences-${System.nanoTime()}.pb"
    private lateinit var dataSource: PreferencesDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val path = context.filesDir.resolve(testFileName).absolutePath.toPath()
        dataSource = PreferencesDataSource(createDataStore(FileSystem.SYSTEM) { path })
    }

    @After
    fun tearDown() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.filesDir.resolve(testFileName).delete()
    }

    @Test
    fun setShouldHideOnboarding_persistsToRealFile() = runTest {
        dataSource.setShouldHideOnboarding(true)

        assertTrue(dataSource.userData.first().shouldHideOnboarding)
    }

    @Test
    fun setDarkThemeConfig_persistsToRealFile() = runTest {
        dataSource.setDarkThemeConfig(DarkThemeConfig.LIGHT)

        assertEquals(DarkThemeConfig.LIGHT, dataSource.userData.first().darkThemeConfig)
    }

    @Test
    fun updateTotalUsageTime_persistsToRealFile() = runTest {
        dataSource.updateTotalUsageTime(999L)

        assertEquals(999L, dataSource.userData.first().totalUsageTime)
    }

    @Test
    fun setReviewShown_persistsToRealFile() = runTest {
        dataSource.setReviewShown(true)

        assertTrue(dataSource.userData.first().isReviewShown)
    }
}
