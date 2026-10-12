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

package com.ngapp.quottie.core.common.util

actual val isDebug: Boolean
    get() = AndroidBuildType.isDebug

/**
 * KMP Android library modules have no debug/release variants (and no BuildConfig) under AGP 9,
 * so the app module reports its own BuildConfig.DEBUG here before anything reads [isDebug].
 */
object AndroidBuildType {
    var isDebug: Boolean = false
}