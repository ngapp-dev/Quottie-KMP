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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.quottie.MainActivityUiState
import com.ngapp.quottie.MainActivityUiState.Loading
import com.ngapp.quottie.MainActivityUiState.Success
import com.ngapp.quottie.MainActivityViewModel
import com.ngapp.quottie.core.desingsystem.theme.QuottieTheme
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.ui.QuottieApp
import com.ngapp.quottie.ui.rememberQuottieAppState
import org.koin.compose.viewmodel.koinViewModel

fun MainViewController() = ComposeUIViewController(
    configure = { enforceStrictPlistSanityCheck = false }
) {
    IosQuottieApp()
}

@Composable
private fun IosQuottieApp(
    viewModel: MainActivityViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)

    QuottieTheme(darkTheme = darkTheme) {
        QuottieApp(rememberQuottieAppState())
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    Loading -> isSystemInDarkTheme()
    is Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}
