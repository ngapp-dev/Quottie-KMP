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

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import config.ConfigurationKeys
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configurePlatformTargets(project: Project) {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = ConfigurationKeys.APP_NAME
            isStatic = true
        }
    }
    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        namespace = ConfigurationKeys.APPLICATION_ID.plus(project.name)
        compileSdk = ConfigurationKeys.sdkConfiguration.compileSdk
        minSdk = ConfigurationKeys.sdkConfiguration.minSdk
        compilerOptions {
            jvmTarget.set(ConfigurationKeys.javaConfiguration.javaVmTarget)
        }
        // Unlike com.android.library, the KMP Android library target has no test compilations
        // unless they are enabled explicitly: host tests run commonTest on the JVM
        // (testAndroidHostTest), device tests run src/androidDeviceTest on a device.
        withHostTest {
            isReturnDefaultValues = ConfigurationKeys.HAS_UNIT_TESTS_DEFAULT_VALUES
            // Puts the R classes of dependencies on the test classpath: moko's SharedRes
            // references resources.R, which host tests can't load otherwise.
            isIncludeAndroidResources = true
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
}
