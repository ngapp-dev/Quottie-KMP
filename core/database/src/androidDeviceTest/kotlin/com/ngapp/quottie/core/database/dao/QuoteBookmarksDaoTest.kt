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
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Instrumented test for [QuoteBookmarksDao], running against the real Android SQLite framework
 * on a device/emulator.
 */
@RunWith(AndroidJUnit4::class)
class QuoteBookmarksDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: QuoteBookmarksDao

    private fun quote(id: String = "q1") = QuoteResourceEntity(
        id = id,
        content = "content-$id",
        author = "author-$id",
        length = 10,
        tags = listOf("wisdom"),
        isBookmarked = true,
    )

    @Before
    fun setUp() {
        database = createTestDatabase()
        dao = database.QuoteBookmarksDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveBookmark_thenGetBookmark_returnsSavedQuote() = runTest {
        dao.saveQuoteBookmark(quote())

        assertEquals(quote(), dao.getQuoteBookmark("q1"))
    }

    @Test
    fun getBookmark_whenMissing_returnsNull() = runTest {
        assertNull(dao.getQuoteBookmark("missing"))
    }

    @Test
    fun saveBookmark_withSameId_replacesExistingEntry() = runTest {
        dao.saveQuoteBookmark(quote().copy(content = "first"))
        dao.saveQuoteBookmark(quote().copy(content = "second"))

        assertEquals("second", dao.getQuoteBookmark("q1")?.content)
    }

    @Test
    fun deleteBookmark_removesQuote() = runTest {
        dao.saveQuoteBookmark(quote())

        dao.deleteQuoteBookmark("q1")

        assertNull(dao.getQuoteBookmark("q1"))
    }

    @Test
    fun getBookmarkListFlow_emitsAllSavedBookmarks() = runTest {
        dao.saveQuoteBookmark(quote("q1"))
        dao.saveQuoteBookmark(quote("q2"))

        val bookmarks = dao.getQuoteBookmarkListFlow().first()

        assertEquals(2, bookmarks.size)
        assertTrue(bookmarks.map { it.id }.containsAll(listOf("q1", "q2")))
    }
}
