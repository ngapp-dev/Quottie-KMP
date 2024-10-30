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

import com.android.build.gradle.BaseExtension
import config.ConfigurationKeys
import config.Versioning

internal fun BaseExtension.configureCompileOptions() {
    compileOptions {
        sourceCompatibility = ConfigurationKeys.javaConfiguration.version
        targetCompatibility = ConfigurationKeys.javaConfiguration.version
    }
}

internal fun BaseExtension.configureTestOptions() {
    testOptions {
        unitTests.isReturnDefaultValues = ConfigurationKeys.HAS_UNIT_TESTS_DEFAULT_VALUES
    }
}

internal fun BaseExtension.configureDefaultConfig() {
    defaultConfig {
        minSdk = ConfigurationKeys.sdkConfiguration.minSdk
        targetSdk = ConfigurationKeys.sdkConfiguration.targetSdk
        versionCode = Versioning.versionCode
        versionName = Versioning.versionName
    }
}