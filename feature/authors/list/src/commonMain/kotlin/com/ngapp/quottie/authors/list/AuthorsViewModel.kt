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

package com.ngapp.quottie.authors.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.cachedIn
import com.ngapp.quottie.authors.list.state.AuthorsUiState
import com.ngapp.quottie.authors.list.state.AuthorsUiState.Loading
import com.ngapp.quottie.authors.list.state.AuthorsUiState.Success
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.model.resultfilter.ResultFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AuthorsViewModel(
    private val authorRepository: AuthorRepository,
) : ViewModel() {

    var firstVisibleItemIndex: Int = 0
        private set

    var firstVisibleItemScrollOffset: Int = 0
        private set

    private val authors = flow {
        emitAll(
            authorRepository.getAuthorsPaging(
                filter = ResultFilter(),
                slug = emptyList(),
                pageSize = 20
            )
        )
    }.cachedIn(viewModelScope)

    val uiState: StateFlow<AuthorsUiState> = authorsUiState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading
        )

    private fun authorsUiState(): Flow<AuthorsUiState> = flow {
        emit(Success(authors = authors))
    }

    fun onScrollPositionChanged(
        firstVisibleItemIndex: Int,
        firstVisibleItemScrollOffset: Int,
    ) {
        this.firstVisibleItemIndex = firstVisibleItemIndex
        this.firstVisibleItemScrollOffset = firstVisibleItemScrollOffset
    }
}

