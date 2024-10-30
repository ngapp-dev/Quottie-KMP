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

package com.ngapp.quottie.search.state

import app.cash.paging.PagingData
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.common.base.BaseUiState
import com.ngapp.quottie.core.data.model.recentsearchquery.RecentSearchQuery
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SearchUiState(
    val isError: Boolean = false,
) : BaseUiState

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     */
    data object EmptyQuery : SearchResultUiState

    data class Success(
        val authors: Flow<PagingData<AuthorResource>> = emptyFlow(),
        val quotes: Flow<PagingData<QuoteResource>> = emptyFlow(),
    ) : SearchResultUiState
}

sealed interface RecentSearchQueriesUiState {
    data object Loading : RecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<RecentSearchQuery> = emptyList(),
    ) : RecentSearchQueriesUiState
}

enum class SearchTabs(val titleResId: StringResource) {
    AUTHORS(SharedRes.strings.destination_authors),
    QUOTES(SharedRes.strings.destination_quotes),
}