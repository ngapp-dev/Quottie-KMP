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

package com.ngapp.quottie.core.domain.onboarding

import com.ngapp.quottie.core.domain.fake.FakeUserDataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DismissOnboardingUseCaseTest {

    private val repository = FakeUserDataRepository()
    private val useCase = DismissOnboardingUseCase(repository)

    @Test
    fun invoke_true_setsShouldHideOnboardingToTrue() = runTest {
        useCase(shouldHideOnboarding = true)

        assertTrue(repository.userData.first().shouldHideOnboarding)
    }

    @Test
    fun invoke_false_setsShouldHideOnboardingToFalse() = runTest {
        useCase(shouldHideOnboarding = true)
        useCase(shouldHideOnboarding = false)

        assertFalse(repository.userData.first().shouldHideOnboarding)
    }
}
