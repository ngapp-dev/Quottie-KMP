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
import com.ngapp.quottie.core.datastore.createDataStore
import com.ngapp.quottie.core.model.DarkThemeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserDataRepositoryImplTest {

    // See PreferencesDataSourceTest: DataStore rejects a second active instance per file path
    // within the same process, so every repository gets its own unique backing path.
    private fun repository(): UserDataRepositoryImpl {
        val path = "/user-data-repo-test-${Random.nextLong()}.pb".toPath()
        val dataSource = PreferencesDataSource(createDataStore(FakeFileSystem()) { path })
        return UserDataRepositoryImpl(dataSource)
    }

    @Test
    fun setShouldHideOnboarding_isReflectedInUserData() = runTest {
        val repository = repository()

        repository.setShouldHideOnboarding(true)

        assertTrue(repository.userData.first().shouldHideOnboarding)
    }

    @Test
    fun setDarkThemeConfig_isReflectedInUserData() = runTest {
        val repository = repository()

        repository.setDarkThemeConfig(DarkThemeConfig.DARK)

        assertEquals(DarkThemeConfig.DARK, repository.userData.first().darkThemeConfig)
    }

    @Test
    fun updateTotalUsageTime_isReflectedInUserData() = runTest {
        val repository = repository()

        repository.updateTotalUsageTime(1_000L)

        assertEquals(1_000L, repository.userData.first().totalUsageTime)
    }

    @Test
    fun setReviewShown_isReflectedInUserData() = runTest {
        val repository = repository()

        repository.setReviewShown(true)

        assertTrue(repository.userData.first().isReviewShown)
    }
}
