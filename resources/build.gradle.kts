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

import dev.icerock.gradle.MRVisibility

plugins {
    id("kmp-library-plugin")
    alias(libs.plugins.moko.plugin)
}

android {
    namespace = "resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.moko.core)
            implementation(libs.moko.compose)
        }
        androidMain.get().dependsOn(commonMain.get())
        iosMain.get().dependsOn(commonMain.get())
    }
}

multiplatformResources {
    resourcesPackage.set("com.ngapp.quottie") // required
    resourcesClassName.set("SharedRes") // optional, default MR
    resourcesVisibility.set(MRVisibility.Public)
}
