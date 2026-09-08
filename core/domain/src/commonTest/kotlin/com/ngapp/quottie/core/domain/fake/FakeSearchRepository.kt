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

package com.ngapp.quottie.core.domain.fake

import com.ngapp.quottie.core.data.model.recentsearchquery.RecentSearchQuery
import com.ngapp.quottie.core.data.repository.search.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSearchRepository : SearchRepository {

    private val queries = MutableStateFlow<List<RecentSearchQuery>>(emptyList())

    fun setQueries(value: List<RecentSearchQuery>) {
        queries.value = value
    }

    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> {
        recordedLimit = limit
        return queries
    }

    var recordedLimit: Int? = null
        private set

    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) = error("Not used by this test")

    override suspend fun clearRecentSearches() = error("Not used by this test")
}
