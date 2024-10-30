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

@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Quottie"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

    include(":composeApp")
    include(":resources")

    include(":core:analytics")
    include(":core:common")
    include(":core:data")
    include(":core:database")
    include(":core:datastore")
    include(":core:designsystem")
    include(":core:domain")
    include(":core:model")
    include(":core:network")
    include(":core:ui")

    include(":feature:authors:list")
    include(":feature:authors:detail")
    include(":feature:bookmarks")
    include(":feature:onboarding")
    include(":feature:home")
    include(":feature:search")
    include(":feature:settings")
    include(":feature:settings:about")
    include(":feature:settings:softwarelicense")
    include(":feature:settings:privacypolicy")
    include(":feature:settings:termsandconditions")
