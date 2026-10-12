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

import java.util.Properties

plugins {
    id("android-app-plugin")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.secrets)
}

val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use { load(it) }
    }
}

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        // The secrets plugin only fills manifest placeholders of real variants, not of the unit
        // test component, so the placeholder the manifest uses is mirrored here as well. Falls
        // back to Google's public test AdMob app id when secrets.properties is missing.
        manifestPlaceholders["ADS_ID_KEY"] = secretsProperties.getProperty(
            "ADS_ID_KEY",
            "ca-app-pub-3940256099942544~3347511713",
        )
    }
}

secrets {
    propertiesFileName = "secrets.properties"
}

dependencies {
    implementation(projects.composeApp)
    implementation(projects.core.analytics)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.android)
    implementation(libs.google.play.app.update)
    implementation(libs.google.play.app.update.ktx)
    implementation(libs.google.play.review)
}
