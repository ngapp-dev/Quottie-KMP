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

package com.ngapp.quottie.core.analytics

//import cocoapods.FirebaseAnalytics.FIRAnalytics
import io.github.aakira.napier.Napier

private const val TAG = "FirebaseAnalyticsHelperIos"

class FirebaseAnalyticsHelper : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
//        FIRAnalytics.logEventWithName(
//            name = event.type.take(40),
//            parameters = event.extras.associate {
//                // Truncate parameter keys and values according to firebase maximum length values.
//                it.key.take(40) to it.value.take(100)
//            },
//        )

        Napier.d(message = "Received analytics event: $event", tag = TAG)
    }

    override fun setUserProperty(name: String, value: String) {
//        FIRAnalytics.setUserPropertyString(value = value, forName = name)
        Napier.d(message = "Set user property: $name = $value", tag = TAG)
    }
}
