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

package com.ngapp.quottie.core.data.model.author

import com.ngapp.quottie.core.network.model.author.NetworkAuthor
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorResourceMapperTest {

    @Test
    fun networkAuthor_asResource_mapsAllFields() {
        val networkAuthor = NetworkAuthor(
            id = "a1",
            bio = "bio",
            description = "description",
            link = "link",
            name = "name",
            slug = "slug",
            quoteCount = 5,
        )

        val resource = networkAuthor.asResource()

        assertEquals("a1", resource.id)
        assertEquals("bio", resource.bio)
        assertEquals("description", resource.description)
        assertEquals("link", resource.link)
        assertEquals("name", resource.name)
        assertEquals("slug", resource.slug)
        assertEquals(5, resource.quoteCount)
    }

    @Test
    fun networkAuthor_asResource_withNullDescription_mapsToEmptyString() {
        val networkAuthor = NetworkAuthor(id = "a1", description = null)

        assertEquals("", networkAuthor.asResource().description)
    }
}
