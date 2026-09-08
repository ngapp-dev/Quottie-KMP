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

package com.ngapp.quottie.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ngapp.quottie.core.database.AppDatabase
import com.ngapp.quottie.core.database.createTestDatabase
import com.ngapp.quottie.core.database.model.recentsearchquery.RecentSearchQueryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class RecentSearchQueryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: RecentSearchQueryDao

    private fun query(value: String, queriedAtMillis: Long) = RecentSearchQueryEntity(
        query = value,
        queriedDate = Instant.fromEpochMilliseconds(queriedAtMillis),
    )

    @Before
    fun setUp() {
        database = createTestDatabase()
        dao = database.RecentSearchQueryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getRecentSearchQueryEntities_ordersByQueriedDateDescending() = runTest {
        dao.insertOrReplaceRecentSearchQuery(query("oldest", 1_000))
        dao.insertOrReplaceRecentSearchQuery(query("newest", 3_000))
        dao.insertOrReplaceRecentSearchQuery(query("middle", 2_000))

        val result = dao.getRecentSearchQueryEntities(limit = 10).first()

        assertEquals(listOf("newest", "middle", "oldest"), result.map { it.query })
    }

    @Test
    fun getRecentSearchQueryEntities_respectsLimit() = runTest {
        dao.insertOrReplaceRecentSearchQuery(query("first", 1_000))
        dao.insertOrReplaceRecentSearchQuery(query("second", 2_000))
        dao.insertOrReplaceRecentSearchQuery(query("third", 3_000))

        val result = dao.getRecentSearchQueryEntities(limit = 2).first()

        assertEquals(2, result.size)
        assertEquals("third", result.first().query)
    }

    @Test
    fun insertOrReplace_withSameQuery_upsertsInsteadOfDuplicating() = runTest {
        dao.insertOrReplaceRecentSearchQuery(query("repeat", 1_000))
        dao.insertOrReplaceRecentSearchQuery(query("repeat", 5_000))

        val result = dao.getRecentSearchQueryEntities(limit = 10).first()

        assertEquals(1, result.size)
        assertEquals(5_000, result.first().queriedDate.toEpochMilliseconds())
    }

    @Test
    fun clearRecentSearchQueries_removesAllEntries() = runTest {
        dao.insertOrReplaceRecentSearchQuery(query("a", 1_000))
        dao.insertOrReplaceRecentSearchQuery(query("b", 2_000))

        dao.clearRecentSearchQueries()

        assertTrue(dao.getRecentSearchQueryEntities(limit = 10).first().isEmpty())
    }
}
