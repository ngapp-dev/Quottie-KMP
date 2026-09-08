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

package com.ngapp.quottie.core.data.repository.search

import com.ngapp.quottie.core.data.fake.FakeRecentSearchQueryDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultSearchRepositoryTest {

    private val dao = FakeRecentSearchQueryDao()
    private val repository = DefaultSearchRepository(dao)

    @Test
    fun insertOrReplaceRecentSearch_thenGetRecentSearchQueries_returnsInsertedQuery() = runTest {
        repository.insertOrReplaceRecentSearch("plato")

        val queries = repository.getRecentSearchQueries(limit = 10).first()

        assertEquals(listOf("plato"), queries.map { it.query })
    }

    @Test
    fun insertOrReplaceRecentSearch_withSameQuery_doesNotDuplicate() = runTest {
        repository.insertOrReplaceRecentSearch("plato")
        repository.insertOrReplaceRecentSearch("plato")

        val queries = repository.getRecentSearchQueries(limit = 10).first()

        assertEquals(1, queries.size)
    }

    @Test
    fun getRecentSearchQueries_respectsLimit() = runTest {
        repository.insertOrReplaceRecentSearch("a")
        repository.insertOrReplaceRecentSearch("b")
        repository.insertOrReplaceRecentSearch("c")

        val queries = repository.getRecentSearchQueries(limit = 2).first()

        assertEquals(2, queries.size)
    }

    @Test
    fun clearRecentSearches_removesAllQueries() = runTest {
        repository.insertOrReplaceRecentSearch("plato")

        repository.clearRecentSearches()

        assertTrue(repository.getRecentSearchQueries(limit = 10).first().isEmpty())
    }
}
