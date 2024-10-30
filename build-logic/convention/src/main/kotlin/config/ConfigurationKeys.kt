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

package config

import config.Versioning.ANDROID_COMPILE_SDK
import config.Versioning.ANDROID_MIN_SDK
import config.Versioning.ANDROID_TARGET_SDK
import model.JavaConfiguration
import model.SdkConfiguration
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal object ConfigurationKeys {

    const val APPLICATION_ID = "com.ngapp.quottie"
    const val APP_NAME = "ComposeApp"
    const val HAS_UNIT_TESTS_DEFAULT_VALUES = true

    val javaConfiguration = JavaConfiguration(
        javaVmTarget = JvmTarget.JVM_17,
        version = JavaVersion.VERSION_17
    )

    val sdkConfiguration = SdkConfiguration(
        minSdk = ANDROID_MIN_SDK,
        targetSdk = ANDROID_TARGET_SDK,
        compileSdk = ANDROID_COMPILE_SDK
    )

    val ELIGIBLE_MODULES_FOR_COVERAGE = listOf(
        ModuleKeys.FEATURE_ONBOARDING_MODULE,
        ModuleKeys.FEATURE_HOME_MODULE,
        ModuleKeys.FEATURE_QUOTES_MODULE,
        ModuleKeys.FEATURE_AUTHORS_MODULE,
        ModuleKeys.FEATURE_BOOKMARKS_MODULE,
        ModuleKeys.COMMON_MODULE,
        ModuleKeys.DATA_MODULE,
        ModuleKeys.DATASTORE_MODULE,
        ModuleKeys.DESIGNSYSTEM_MODULE,
        ModuleKeys.DOMAIN_MODULE,
        ModuleKeys.MODEL_MODULE,
        ModuleKeys.NAVIGATION_MODULE,
        ModuleKeys.NETWORK_MODULE,
        ModuleKeys.UI_MODULE,
        ModuleKeys.RESOURCES_MODULE,
    )
}
