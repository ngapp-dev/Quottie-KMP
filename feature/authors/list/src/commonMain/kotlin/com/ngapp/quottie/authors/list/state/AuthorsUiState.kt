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

package com.ngapp.quottie.authors.list.state

import app.cash.paging.PagingData
import com.ngapp.quottie.core.common.base.BaseUiState
import com.ngapp.quottie.core.model.author.AuthorResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

sealed interface AuthorsUiState : BaseUiState {
    data object Loading : AuthorsUiState
    data class Success(
        val authors: Flow<PagingData<AuthorResource>> = emptyFlow(),
    ) : AuthorsUiState
}