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

package com.ngapp.quottie.core.network.model.wiki

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of [WikiResource]
 */
@Serializable
data class NetworkWiki(
    val batchComplete: String = "",
    val query: NetworkWikiQuery
)
/**
 * Network representation of [WikiQuery]
 */
@Serializable
data class NetworkWikiQuery(
    val normalized: List<NetworkNormalized>? = emptyList(),
    val pages: Map<String, NetworkWikiPage>
)

/**
 * Network representation of [WikiPage]
 */
@Serializable
data class NetworkWikiPage(
    @SerialName(value = "pageid") val pageId: Int = 0,
    val ns: Int = 0,
    val title: String,
    val thumbnail: NetworkThumbnail? = null,
    @SerialName(value = "pageimage") val pageImage: String? = null
)

/**
 * Network representation of [Normalized]
 */
@Serializable
data class NetworkNormalized(
    val from: String,
    val to: String
)

/**
 * Network representation of [Thumbnail]
 */
@Serializable
data class NetworkThumbnail(
    val source: String,
    val width: Int,
    val height: Int
)
