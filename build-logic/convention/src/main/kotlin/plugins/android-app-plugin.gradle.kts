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

import com.ngapp.quottie.configureBuildTypes
import com.ngapp.quottie.configureCompileOptions
import com.ngapp.quottie.configureDefaultConfig
import com.ngapp.quottie.configureTestOptions
import config.ConfigurationKeys

// Android entry point of the app. AGP 9 compiles Kotlin itself (built-in Kotlin), so no Kotlin
// plugin is applied here; the shared code lives in the :composeApp KMP library.
plugins {
    id("com.android.application")
    id("kover-app-plugin")
}

android {
    namespace = ConfigurationKeys.APPLICATION_ID

    defaultConfig {
        applicationId = ConfigurationKeys.APPLICATION_ID
    }

    configureDefaultConfig()
    configureCompileOptions()
    configureTestOptions()
    configureBuildTypes()
}

kotlin {
    compilerOptions {
        jvmTarget.set(ConfigurationKeys.javaConfiguration.javaVmTarget)
    }
}
