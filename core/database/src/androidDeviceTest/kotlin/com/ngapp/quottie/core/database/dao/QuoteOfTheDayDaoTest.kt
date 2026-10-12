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
import com.ngapp.quottie.core.database.model.quote.QuoteOfTheDayEntity
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class QuoteOfTheDayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: QuoteOfTheDayDao

    private fun quote(id: String = "qotd", fetched: Instant = Instant.fromEpochMilliseconds(0)) =
        QuoteOfTheDayEntity(
            id = id,
            content = "content-$id",
            author = "author-$id",
            length = 5,
            tags = emptyList(),
            lastFetched = fetched,
        )

    @Before
    fun setUp() {
        database = createTestDatabase()
        dao = database.QuoteOfTheDayDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getQuoteOfTheDay_whenEmpty_returnsNull() = runTest {
        assertNull(dao.getQuoteOfTheDay())
    }

    @Test
    fun insertQuoteOfTheDay_thenGet_returnsInsertedQuote() = runTest {
        dao.insertQuoteOfTheDay(quote())

        assertEquals(quote(), dao.getQuoteOfTheDay())
    }

    @Test
    fun replaceQuoteOfTheDay_swapsPreviousEntryForNewOne() = runTest {
        dao.insertQuoteOfTheDay(quote(id = "old"))

        dao.replaceQuoteOfTheDay(quote(id = "new", fetched = Instant.fromEpochMilliseconds(1_000)))

        assertEquals("new", dao.getQuoteOfTheDay()?.id)
    }

    @Test
    fun deleteQuoteOfTheDay_clearsTable() = runTest {
        dao.insertQuoteOfTheDay(quote())

        dao.deleteQuoteOfTheDay()

        assertNull(dao.getQuoteOfTheDay())
    }
}
