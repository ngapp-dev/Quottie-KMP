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

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.ngapp.quottie.core.analytics.CrashlyticsAntilog
import com.ngapp.quottie.core.common.util.isDebug
import com.ngapp.quottie.di.appModules
import com.ngapp.quottie.di.featureModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.lazyModules

class QuottieApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        val enabled = isDebug.not()
        Firebase.analytics.setAnalyticsCollectionEnabled(enabled)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = enabled

        // Init Napier
        val antilog = if (isDebug) DebugAntilog() else CrashlyticsAntilog()
        Napier.base(antilog)

        startKoin {
            androidContext(this@QuottieApplication)
            modules(appModules)
            lazyModules(featureModules)
        }
    }
}
