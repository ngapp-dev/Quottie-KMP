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

package com.ngapp.quottie.di

import com.ngapp.quottie.authors.detail.di.featureAuthorDetailModule
import com.ngapp.quottie.authors.list.di.featureAuthorsModule
import com.ngapp.quottie.bookmarks.di.featureBookmarksModule
import com.ngapp.quottie.core.analytics.di.analyticsModule
import com.ngapp.quottie.core.data.di.dataModule
import com.ngapp.quottie.core.database.di.databaseModule
import com.ngapp.quottie.core.datastore.di.datastoreModule
import com.ngapp.quottie.core.domain.di.domainModule
import com.ngapp.quottie.core.network.di.networkModule
import com.ngapp.quottie.core.ui.di.uiModule
import com.ngapp.quottie.home.di.featureHomeModule
import com.ngapp.quottie.onboarding.di.featureOnboardingModule
import com.ngapp.quottie.search.di.featureSearchModule
import com.ngapp.quottie.settings.di.featureSettingsModule
import com.ngapp.settings.quottie.about.di.featureAboutModule
import com.ngapp.settings.quottie.privacypolicy.di.featurePrivacyPolicyModule

val appModules = listOf(
    analyticsModule(),
    dataModule,
    databaseModule,
    datastoreModule,
    domainModule,
    networkModule,
    uiModule,
)

val featureModules = listOf(
    mainActivityModule,
    featureOnboardingModule,
    featureHomeModule,
    featureAuthorsModule,
    featureAuthorDetailModule,
    featureBookmarksModule,
    featureSearchModule,
    featureSettingsModule,
    featurePrivacyPolicyModule,
    featureAboutModule,
)
