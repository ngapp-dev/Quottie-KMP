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

import com.ngapp.quottie.libs
import gradle.kotlin.dsl.accessors._93bd701a7528465ddd1d441513f487f1.kotlin
import gradle.kotlin.dsl.accessors._93bd701a7528465ddd1d441513f487f1.ksp
import gradle.kotlin.dsl.accessors._93bd701a7528465ddd1d441513f487f1.sourceSets
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("com.google.devtools.ksp")
    id("kotlin-multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

            dependencies {
                api(libs.findLibrary("koin-core").get())
                api(libs.findLibrary("koin-annotations").get())
                api(libs.findLibrary("koin-compose-viewmodel").get())
                api(libs.findLibrary("koin-compose").get())
                api(libs.findLibrary("koin-coroutines").get())
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.findLibrary("koin-compiler").get())
}

// WORKAROUND: ADD this dependsOn("kspCommonMainKotlinMetadata") instead of above dependencies
tasks.withType<KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

afterEvaluate {
    tasks.filter {
        it.name.contains("SourcesJar", true)
    }.forEach {
        println("SourceJarTask====>${it.name}")
        it.dependsOn("kspCommonMainKotlinMetadata")
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "false")
}