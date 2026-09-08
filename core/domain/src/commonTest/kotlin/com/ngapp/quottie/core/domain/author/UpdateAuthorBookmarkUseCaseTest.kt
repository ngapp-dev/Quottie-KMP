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

package com.ngapp.quottie.core.domain.author

import com.ngapp.quottie.core.domain.fake.FakeAuthorRepository
import com.ngapp.quottie.core.model.author.AuthorResource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UpdateAuthorBookmarkUseCaseTest {

    private val repository = FakeAuthorRepository()
    private val useCase = UpdateAuthorBookmarkUseCase(repository)
    private val author = AuthorResource("a1", "bio", "description", "link", "name", "slug", 3)

    @Test
    fun invoke_withBookmarkedTrue_savesBookmark() = runTest {
        useCase(author, isBookmarked = true)

        assertEquals(author, repository.savedAuthor)
        assertEquals(true, repository.savedIsBookmarked)
        assertNull(repository.deletedAuthorId)
    }

    @Test
    fun invoke_withBookmarkedFalse_deletesBookmark() = runTest {
        useCase(author, isBookmarked = false)

        assertEquals("a1", repository.deletedAuthorId)
        assertNull(repository.savedAuthor)
    }
}
