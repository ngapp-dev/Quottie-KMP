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

package com.ngapp.quottie.core.domain.quote

import com.ngapp.quottie.core.domain.fake.FakeQuoteRepository
import com.ngapp.quottie.core.model.quote.QuoteResource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UpdateQuoteBookmarkUseCaseTest {

    private val repository = FakeQuoteRepository()
    private val useCase = UpdateQuoteBookmarkUseCase(repository)
    private val quote = QuoteResource("q1", "content", "author", 7, listOf("life"))

    @Test
    fun invoke_withBookmarkedTrue_savesBookmark() = runTest {
        useCase(quote, isBookmarked = true)

        assertEquals(quote, repository.savedQuote)
        assertEquals(true, repository.savedIsBookmarked)
        assertNull(repository.deletedQuoteId)
    }

    @Test
    fun invoke_withBookmarkedFalse_deletesBookmark() = runTest {
        useCase(quote, isBookmarked = false)

        assertEquals("q1", repository.deletedQuoteId)
        assertNull(repository.savedQuote)
    }

    @Test
    fun invoke_defaultsToDeletingBookmark() = runTest {
        useCase(quote)

        assertEquals("q1", repository.deletedQuoteId)
    }
}
