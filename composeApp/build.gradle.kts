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

plugins {
    id("kmp-app-plugin")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.buildkonfig.plugin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
}

android {
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(compose.preview)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.google.play.app.update)
            implementation(libs.google.play.app.update.ktx)
            implementation(libs.google.play.review)
        }
        iosMain.dependencies {
            implementation(libs.koin.core)
        }
        commonMain.dependencies {
            implementation(projects.feature.authors.list)
            implementation(projects.feature.authors.detail)
            implementation(projects.feature.bookmarks)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.home)
            implementation(projects.feature.search)
            implementation(projects.feature.settings)
            implementation(projects.feature.settings.about)
            implementation(projects.feature.settings.softwarelicense)
            implementation(projects.feature.settings.privacypolicy)
            implementation(projects.feature.settings.termsandconditions)

            implementation(compose.runtime)
            implementation(libs.kotlinx.datetime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.material3.adaptive.navigation)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.material3.adaptive.layout)
            implementation(libs.compose.material3.adaptive.navigation.suite)
            implementation(libs.navigation.compose)

            implementation(libs.bundles.moko)
            implementation(libs.koin.core)

            implementation(projects.resources)
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.designsystem)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.ui)
            implementation(projects.core.analytics)
        }
    }
}

buildkonfig {
    packageName = "com.ngapp.quottie"
    defaultConfigs {}
}

secrets {
    propertiesFileName = "secrets.properties"
}
