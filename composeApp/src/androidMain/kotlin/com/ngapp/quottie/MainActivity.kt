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

package com.ngapp.quottie

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.review.ReviewManagerFactory
import com.ngapp.quottie.MainActivityUiState.Loading
import com.ngapp.quottie.MainActivityUiState.Success
import com.ngapp.quottie.core.analytics.AnalyticsInjected
import com.ngapp.quottie.core.analytics.LocalAnalyticsHelper
import com.ngapp.quottie.core.datastore.DatastoreAndroidPlatformContextProvider
import com.ngapp.quottie.core.desingsystem.theme.QuottieTheme
import com.ngapp.quottie.core.model.DarkThemeConfig
import com.ngapp.quottie.core.ui.UiAndroidPlatformContextProvider
import com.ngapp.quottie.ui.QuottieApp
import com.ngapp.quottie.ui.rememberQuottieAppState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    private val analyticsHelper = AnalyticsInjected().analyticsHelper
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE

    private val activityForResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Toast.makeText(
                    this,
                    "Update flow failed! Result code: ${result.resultCode}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdateManager)
        }
        checkForAppUpdates()
        var uiState: MainActivityUiState by mutableStateOf(Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                is Success -> false
            }
        }

        DatastoreAndroidPlatformContextProvider.setContext(this)
        UiAndroidPlatformContextProvider.setContext(this)

        enableEdgeToEdge()
        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)
            UpdateUserConfig(uiState, onReviewShown = { viewModel::setReviewShown })
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        statusBarLightScrim,
                        statusBarDarkScrim,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        navigationBarLightScrim,
                        navigationBarDarkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }
            CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
                QuottieTheme(darkTheme = darkTheme) {
                    QuottieApp(rememberQuottieAppState())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityForResultLauncher,
                        AppUpdateOptions.newBuilder(updateType).build()
                    )
                }
            }
        }
    }

    private val installStateUpdateManager = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds",
                Toast.LENGTH_SHORT
            ).show()
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> appUpdateInfo.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> appUpdateInfo.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityForResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdateManager)
        }
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

@Composable
private fun UpdateUserConfig(uiState: MainActivityUiState, onReviewShown: () -> Unit) {
    when (uiState) {
        Loading -> Unit
        is Success -> {
            val context = LocalContext.current
            val isReviewShown = uiState.userData.isReviewShown
            val showInAppReviewPrompt = uiState.userData.totalUsageTime > 300_000
            LaunchedEffect(key1 = showInAppReviewPrompt, key2 = isReviewShown) {
                if (showInAppReviewPrompt && !isReviewShown) {
                    launchInAppReview(context, onReviewShown)
                }
            }
        }
    }
}

private fun launchInAppReview(context: Context, onReviewShown: () -> Unit) {
    val reviewManager = ReviewManagerFactory.create(context as Activity)
    val requestReviewFlow = reviewManager.requestReviewFlow()
    requestReviewFlow.addOnCompleteListener { request ->
        if (request.isSuccessful) {
            val reviewInfo = request.result
            reviewManager.launchReviewFlow(context, reviewInfo)
            onReviewShown()
        }
    }
}


private val statusBarLightScrim = Color.argb(0xFF, 0xF7, 0xFA, 0xFA)
private val statusBarDarkScrim = Color.argb(0xFF, 0x17, 0x16, 0x13)
private val navigationBarLightScrim = Color.argb(0xe6, 0xF7, 0xFA, 0xFA)
private val navigationBarDarkScrim = Color.argb(0x80, 0x28, 0x24, 0x1C)