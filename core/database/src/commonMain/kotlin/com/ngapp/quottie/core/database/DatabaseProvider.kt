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

@file:Suppress("MatchingDeclarationName")

package com.ngapp.quottie.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.ngapp.quottie.core.database.converter.InstantConverter
import com.ngapp.quottie.core.database.converter.StringListConverter
import com.ngapp.quottie.core.database.dao.AuthorBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteOfTheDayDao
import com.ngapp.quottie.core.database.dao.RecentSearchQueryDao
import com.ngapp.quottie.core.database.model.author.AuthorResourceEntity
import com.ngapp.quottie.core.database.model.quote.QuoteOfTheDayEntity
import com.ngapp.quottie.core.database.model.quote.QuoteResourceEntity
import com.ngapp.quottie.core.database.model.recentsearchquery.RecentSearchQueryEntity

@Database(
    entities = [
        QuoteResourceEntity::class,
        QuoteOfTheDayEntity::class,
        AuthorResourceEntity::class,
        RecentSearchQueryEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    StringListConverter::class,
    InstantConverter::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun QuoteOfTheDayDao(): QuoteOfTheDayDao
    abstract fun QuoteBookmarksDao(): QuoteBookmarksDao
    abstract fun AuthorBookmarksDao(): AuthorBookmarksDao
    abstract fun RecentSearchQueryDao(): RecentSearchQueryDao
}

@Suppress("TopLevelPropertyNaming")
expect fun databaseInstance(): AppDatabase

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Suppress("TopLevelPropertyNaming")
internal const val dbFileName = "quottieDBApp.db"
