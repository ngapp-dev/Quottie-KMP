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

package com.ngapp.quottie.core.database.converter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringListConverterTest {

    private val converter = StringListConverter()

    @Test
    fun fromListOfStrings_thenToListOfStrings_roundTrips() {
        val original = listOf("wisdom", "life", "love")

        val serialized = converter.fromListOfStrings(original)
        val restored = converter.toListOfStrings(serialized)

        assertEquals(original, restored)
    }

    @Test
    fun fromListOfStrings_withNull_encodesEmptyList() {
        val serialized = converter.fromListOfStrings(null)

        assertEquals(emptyList(), converter.toListOfStrings(serialized))
    }

    @Test
    fun toListOfStrings_withInvalidJson_returnsNull() {
        assertNull(converter.toListOfStrings("not-json"))
    }

    @Test
    fun fromListOfStrings_withEmptyList_encodesEmptyArray() {
        assertEquals("[]", converter.fromListOfStrings(emptyList()))
    }
}
