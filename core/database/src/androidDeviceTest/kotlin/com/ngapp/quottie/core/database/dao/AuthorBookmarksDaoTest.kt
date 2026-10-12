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
import com.ngapp.quottie.core.database.model.author.AuthorResourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class AuthorBookmarksDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AuthorBookmarksDao

    private fun author(id: String = "a1") = AuthorResourceEntity(
        id = id,
        bio = "bio-$id",
        description = "description-$id",
        link = "link-$id",
        name = "name-$id",
        slug = "slug-$id",
        quoteCount = 3,
        image = "image-$id",
        isBookmarked = true,
    )

    @Before
    fun setUp() {
        database = createTestDatabase()
        dao = database.AuthorBookmarksDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveBookmark_thenGetBookmark_returnsSavedAuthor() = runTest {
        dao.saveAuthorBookmark(author())

        assertEquals(author(), dao.getAuthorBookmark("a1"))
    }

    @Test
    fun getBookmark_whenMissing_returnsNull() = runTest {
        assertNull(dao.getAuthorBookmark("missing"))
    }

    @Test
    fun deleteBookmark_removesAuthor() = runTest {
        dao.saveAuthorBookmark(author())

        dao.deleteAuthorBookmark("a1")

        assertNull(dao.getAuthorBookmark("a1"))
    }

    @Test
    fun getBookmarkListFlow_emitsAllSavedBookmarks() = runTest {
        dao.saveAuthorBookmark(author("a1"))
        dao.saveAuthorBookmark(author("a2"))

        val bookmarks = dao.getAuthorBookmarkListFlow().first()

        assertEquals(setOf("a1", "a2"), bookmarks.map { it.id }.toSet())
    }
}
