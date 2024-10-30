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

package com.ngapp.quottie.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.quottie.core.data.repository.userdata.UserDataRepository
import com.ngapp.quottie.onboarding.state.OnboardingAction
import com.ngapp.quottie.onboarding.state.OnboardingUiState.Loading
import com.ngapp.quottie.onboarding.state.OnboardingUiState.NotShown
import com.ngapp.quottie.onboarding.state.OnboardingUiState.Shown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val shouldShowOnboarding: Flow<Boolean> =
        userDataRepository.userData.map { !it.shouldHideOnboarding }

    val uiState = shouldShowOnboarding.map { shouldShowOnboarding ->
        if (shouldShowOnboarding) Shown else NotShown
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun triggerAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.DismissOnboarding -> onDismissOnboarding()
        }
    }

    private fun onDismissOnboarding() = viewModelScope.launch {
        userDataRepository.setShouldHideOnboarding(true)
    }
}
