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

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import java.util.Properties

plugins {
    id("kmp-library-plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("koin-plugin-setup")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.secrets)
    alias(libs.plugins.buildkonfig.plugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
            api(projects.core.common)
            api(projects.core.model)
            implementation(libs.bundles.ktor)
            api(libs.kotlinx.serialization.json)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
        }

        commonTest.dependencies {
            implementation(libs.bundles.test.multiplatform)
            implementation(libs.ktor.client.mock)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }

        androidUnitTest.dependencies {
            implementation(libs.bundles.test)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

// Base URLs are read from secrets.properties (gitignored, local to each machine) so they can be
// overridden per-developer without touching source, and fall back to the real backends so the
// project still builds for anyone without that file. Exposed via BuildKonfig (unlike the
// Android-only `secrets` plugin BuildConfig above) so commonMain code - including iOS - can see it.
val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use { load(it) }
    }
}

buildkonfig {
    packageName = "com.ngapp.quottie.core.network"
    defaultConfigs {
        buildConfigField(
            type = Type.STRING,
            name = "QUOTABLE_API_BASE_URL",
            value = secretsProperties.getProperty(
                "QUOTABLE_API",
                "https://quotable-api.ngapps-developer.workers.dev",
            ),
        )
        buildConfigField(
            type = Type.STRING,
            name = "WIKIPEDIA_API_BASE_URL",
            value = secretsProperties.getProperty("WIKIPEDIA_API", "https://en.wikipedia.org"),
        )
        buildConfigField(
            type = Type.STRING,
            name = "GITHUB_API_BASE_URL",
            value = secretsProperties.getProperty("GITHUB_API", "https://api.github.com"),
        )
    }
}