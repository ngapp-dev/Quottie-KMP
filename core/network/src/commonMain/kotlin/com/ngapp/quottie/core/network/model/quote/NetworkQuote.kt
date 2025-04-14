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

package com.ngapp.quottie.core.network.model.quote

import com.ngapp.quottie.core.network.model.author.NetworkAuthor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of [QuoteResource]
 */
@Serializable
data class NetworkQuote(
    @SerialName(value = "id") val id: String = "",
    val content: String = "",
    val author: NetworkAuthor? = null,
    val length: Int = 0,
    val tags: List<NetworkTag> = emptyList(),
)

/**
 * Network representation of [TagResource]
 */
@Serializable
data class NetworkTag(
    @SerialName(value = "id") val id: String = "",
    val name: String = "",
)