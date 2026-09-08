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

package com.ngapp.quottie.core.data.model.wiki

import com.ngapp.quottie.core.network.model.wiki.NetworkNormalized
import com.ngapp.quottie.core.network.model.wiki.NetworkThumbnail
import com.ngapp.quottie.core.network.model.wiki.NetworkWiki
import com.ngapp.quottie.core.network.model.wiki.NetworkWikiPage
import com.ngapp.quottie.core.network.model.wiki.NetworkWikiQuery
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WikiResourceMapperTest {

    @Test
    fun networkWiki_asResource_mapsNestedPagesAndThumbnail() {
        val networkWiki = NetworkWiki(
            batchComplete = "yes",
            query = NetworkWikiQuery(
                normalized = listOf(NetworkNormalized(from = "plato", to = "Plato")),
                pages = mapOf(
                    "1" to NetworkWikiPage(
                        pageId = 1,
                        ns = 0,
                        title = "Plato",
                        thumbnail = NetworkThumbnail(source = "thumb.jpg", width = 100, height = 200),
                        pageImage = "image.jpg",
                    ),
                ),
            ),
        )

        val resource = networkWiki.asResource()

        assertEquals("yes", resource.batchComplete)
        assertEquals(listOf("plato" to "Plato"), resource.query.normalizedResource?.map { it.from to it.to })
        val page = resource.query.pages.getValue("1")
        assertEquals("Plato", page.title)
        assertEquals("thumb.jpg", page.thumbnailResource?.source)
        assertEquals(100, page.thumbnailResource?.width)
        assertEquals("image.jpg", page.pageImage)
    }

    @Test
    fun networkWikiPage_asResource_withoutThumbnail_mapsToNullThumbnail() {
        val page = NetworkWikiPage(pageId = 2, ns = 0, title = "No Thumbnail")

        assertNull(page.asResource().thumbnailResource)
    }
}
