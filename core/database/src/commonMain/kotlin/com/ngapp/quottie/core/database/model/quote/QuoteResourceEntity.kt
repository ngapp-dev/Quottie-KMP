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

package com.ngapp.quottie.core.database.model.quote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.quottie.core.model.quote.QuoteResource

@Entity(tableName = "quote_bookmarks")
data class QuoteResourceEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val author: String,
    val length: Int,
    val tags: List<String>,
    @ColumnInfo(name = "is_bookmarked")
    var isBookmarked: Boolean = false,
)

fun QuoteResource.asEntity() = QuoteResourceEntity(
    id = id,
    content = content,
    author = author,
    length = length,
    tags = tags,
    isBookmarked = isBookmarked
)

fun QuoteResourceEntity.asExternalModel() = QuoteResource(
    id = id,
    content = content,
    author = author,
    length = length,
    tags = tags,
    isBookmarked = isBookmarked,
)