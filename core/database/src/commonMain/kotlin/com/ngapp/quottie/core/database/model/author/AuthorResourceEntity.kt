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

package com.ngapp.quottie.core.database.model.author

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.quottie.core.model.author.AuthorResource

@Entity(tableName = "author_bookmarks")
data class AuthorResourceEntity(
    @PrimaryKey
    val id: String,
    val bio: String,
    val description: String,
    val link: String,
    val name: String,
    val slug: String,
    @ColumnInfo(name = "quote_count")
    val quoteCount: Int,
    val image: String,
    @ColumnInfo(name = "is_bookmarked")
    var isBookmarked: Boolean = false
)

fun AuthorResource.asEntity() = AuthorResourceEntity(
    id = id,
    bio = bio,
    description = description,
    link = link,
    name = name,
    slug = slug,
    quoteCount = quoteCount,
    image = image,
    isBookmarked = isBookmarked,
)

fun AuthorResourceEntity.asExternalModel() = AuthorResource(
    id = id,
    bio = bio,
    description = description,
    link = link,
    name = name,
    slug = slug,
    quoteCount = quoteCount,
    image = image,
    isBookmarked = isBookmarked,
)