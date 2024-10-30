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

import com.ngapp.quottie.core.model.wiki.NormalizedResource
import com.ngapp.quottie.core.model.wiki.ThumbnailResource
import com.ngapp.quottie.core.model.wiki.WikiPageResource
import com.ngapp.quottie.core.model.wiki.WikiQueryResource
import com.ngapp.quottie.core.model.wiki.WikiResource
import com.ngapp.quottie.core.network.model.wiki.NetworkNormalized
import com.ngapp.quottie.core.network.model.wiki.NetworkThumbnail
import com.ngapp.quottie.core.network.model.wiki.NetworkWikiPage
import com.ngapp.quottie.core.network.model.wiki.NetworkWikiQuery
import com.ngapp.quottie.core.network.model.wiki.NetworkWiki

fun NetworkWiki.asResource() = WikiResource(
    batchComplete = batchComplete,
    query = query.asResource()
)

fun NetworkWikiQuery.asResource() = WikiQueryResource(
    normalizedResource = normalized?.map { it.asResource() },
    pages = pages.mapKeys { it.key }.mapValues { it.value.asResource() }
)

fun NetworkNormalized.asResource() = NormalizedResource(
    from = from,
    to = to
)

fun NetworkWikiPage.asResource() = WikiPageResource(
    pageId = pageId,
    ns = ns,
    title = title,
    thumbnailResource = thumbnail?.asResource(),
    pageImage = pageImage
)

fun NetworkThumbnail.asResource() = ThumbnailResource(
    source = source,
    width = width,
    height = height
)
