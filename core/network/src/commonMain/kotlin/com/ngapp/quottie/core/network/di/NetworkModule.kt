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

package com.ngapp.quottie.core.network.di

import com.ngapp.quottie.core.network.GithubNetworkDataSource
import com.ngapp.quottie.core.network.QuottieNetworkDataSource
import com.ngapp.quottie.core.network.WikipediaNetworkDataSource
import com.ngapp.quottie.core.network.ktor.GithubNetwork
import com.ngapp.quottie.core.network.ktor.QuottieNetwork
import com.ngapp.quottie.core.network.ktor.WikipediaNetwork
import com.ngapp.quottie.core.network.providers.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule =
    module {
        single {
            HttpClient(engine = httpClientEngine()) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.ALL
                }

                install(ContentNegotiation) {
                    json(json = Json { ignoreUnknownKeys = true })
                }
            }
        }
        single<QuottieNetworkDataSource> {
            QuottieNetwork(
                "https://api.quotable.io",
                httpClient = get()
            )
        }
        single<WikipediaNetworkDataSource> {
            WikipediaNetwork(
                baseUrl = "https://en.wikipedia.org",
                httpClient = get()
            )
        }
        single<GithubNetworkDataSource> {
            GithubNetwork(
                baseUrl = "https://api.github.com",
                httpClient = get()
            )
        }
    }
