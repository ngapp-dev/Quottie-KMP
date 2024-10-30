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

package com.ngapp.settings.quottie.about

import androidx.lifecycle.viewModelScope
import com.ngapp.quottie.core.common.base.StateBaseViewModel
import com.ngapp.quottie.core.common.result.Result
import com.ngapp.quottie.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.quottie.core.ui.model.uitext.asStringResource
import com.ngapp.settings.quottie.about.state.AboutAction
import com.ngapp.settings.quottie.about.state.AboutEvent
import com.ngapp.settings.quottie.about.state.AboutUiState
import com.ngapp.settings.quottie.about.state.AboutUiState.Error
import com.ngapp.settings.quottie.about.state.AboutUiState.Loading
import com.ngapp.settings.quottie.about.state.AboutUiState.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AboutViewModel(
    private val githubUserRepository: GithubUserRepository,
) : StateBaseViewModel<AboutUiState, AboutAction, AboutEvent>() {

    val uiState: StateFlow<AboutUiState> = aboutUiState()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    override fun triggerAction(action: AboutAction) {}

    private fun aboutUiState(): Flow<AboutUiState> = flow {
        when (val result = githubUserRepository.getGithubUser()) {
            is Result.Success -> emit(Success(githubUser = result.data))
            is Result.Error -> {
                eventChannel.send(AboutEvent.Error(result.error.asStringResource()))
                emit(Error(result.error.asStringResource()))
            }
        }
    }
}

