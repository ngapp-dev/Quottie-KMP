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

package com.ngapp.quottie.core.ui.ads

import android.annotation.SuppressLint
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.ngapp.quottie.core.ui.UiAndroidPlatformContextProvider
import com.ngapp.quottie.core.ui.UiAndroidPlatformContextProvider.getActivity
import com.ngapp.quottiecommon.BuildConfig
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean


actual class ConsentHelper {
    private val context = requireNotNull(UiAndroidPlatformContextProvider.context?.getActivity())
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var showingForm = false
    private val _canShowAds = MutableStateFlow(false)
    actual val canShowAds: StateFlow<Boolean> = _canShowAds.asStateFlow()

    @SuppressLint("MissingPermission")
    actual fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        MobileAds.initialize(context)
    }

    actual fun isPrivacyOptionsRequired(): Boolean {
        val ci = UserMessagingPlatform.getConsentInformation(context)
        return ci.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    actual fun updateConsent() {
        UserMessagingPlatform.showPrivacyOptionsForm(context) { error ->
            val ci = UserMessagingPlatform.getConsentInformation(context)
            handleConsentResult(ci)
        }
    }

    actual fun obtainConsentAndShow() {
        val params = if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(context)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(BuildConfig.ADS_TEST_DEVICE_ID)
                .build()
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .setConsentDebugSettings(debugSettings)
                .build()
        } else {
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build()
        }

        val ci = UserMessagingPlatform.getConsentInformation(context)
        ci.requestConsentInfoUpdate(context, params, {
            if (isPrivacyOptionsRequired() && showingForm) return@requestConsentInfoUpdate
            showingForm = true
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(context) { error ->
                showingForm = false
                handleConsentResult(ci)
            }
        }, { error ->
            Napier.w("ConsentHelper: ${error.errorCode}: ${error.message}")
        })

        if (ci.canRequestAds()) {
            initializeMobileAdsSdk()
            _canShowAds.value = true
        }
    }

    actual fun revokeConsent() {
        val ci = UserMessagingPlatform.getConsentInformation(context)
        ci.reset()
        _canShowAds.value = false
    }

    private fun handleConsentResult(ci: ConsentInformation) {
        if (ci.canRequestAds()) {
            initializeMobileAdsSdk()
            _canShowAds.value = true
        } else {
            _canShowAds.value = false
        }
    }
}