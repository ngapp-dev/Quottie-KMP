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

package com.ngapp.quottie.core.domain.recentsearchquery

import com.ngapp.quottie.core.data.model.recentsearchquery.RecentSearchQuery
import com.ngapp.quottie.core.domain.fake.FakeSearchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRecentSearchQueriesUseCaseTest {

    private val repository = FakeSearchRepository()
    private val useCase = GetRecentSearchQueriesUseCase(repository)

    @Test
    fun invoke_returnsQueriesFromRepository() = runTest {
        repository.setQueries(listOf(RecentSearchQuery("plato"), RecentSearchQuery("aristotle")))

        val result = useCase().first()

        assertEquals(listOf("plato", "aristotle"), result.map { it.query })
    }

    @Test
    fun invoke_defaultLimit_isTen() = runTest {
        useCase().first()

        assertEquals(10, repository.recordedLimit)
    }

    @Test
    fun invoke_withCustomLimit_forwardsLimitToRepository() = runTest {
        useCase(limit = 3).first()

        assertEquals(3, repository.recordedLimit)
    }
}
