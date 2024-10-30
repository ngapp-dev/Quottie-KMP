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

package com.ngapp.quottie.core.database.di

import com.ngapp.quottie.core.database.dao.AuthorBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteBookmarksDao
import com.ngapp.quottie.core.database.dao.QuoteOfTheDayDao
import com.ngapp.quottie.core.database.dao.RecentSearchQueryDao
import com.ngapp.quottie.core.database.databaseInstance
import org.koin.dsl.module

val databaseModule = module {

    single<QuoteOfTheDayDao> {
        databaseInstance().QuoteOfTheDayDao()
    }

    single<QuoteBookmarksDao> {
        databaseInstance().QuoteBookmarksDao()
    }

    single<AuthorBookmarksDao> {
        databaseInstance().AuthorBookmarksDao()
    }

    single<RecentSearchQueryDao> {
        databaseInstance().RecentSearchQueryDao()
    }
}
