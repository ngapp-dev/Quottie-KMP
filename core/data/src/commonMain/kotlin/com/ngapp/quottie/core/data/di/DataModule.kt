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

package com.ngapp.quottie.core.data.di

import com.ngapp.quottie.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.quottie.core.data.repository.author.AuthorRepository
import com.ngapp.quottie.core.data.repository.author.DefaultAuthorRepository
import com.ngapp.quottie.core.data.repository.githubuser.DefaultGithubUserRepository
import com.ngapp.quottie.core.data.repository.quote.DefaultQuoteRepository
import com.ngapp.quottie.core.data.repository.quote.QuoteRepository
import com.ngapp.quottie.core.data.repository.search.DefaultSearchRepository
import com.ngapp.quottie.core.data.repository.search.SearchRepository
import com.ngapp.quottie.core.data.repository.userdata.UserDataRepository
import com.ngapp.quottie.core.data.repository.userdata.UserDataRepositoryImpl
import org.koin.dsl.module

val dataModule = module {

    single<UserDataRepository> { UserDataRepositoryImpl(preferencesDataSource = get()) }
    single<AuthorRepository> {
        DefaultAuthorRepository(
            quottieNetwork = get(),
            wikipediaNetwork = get(),
            authorBookmarkDao = get()
        )
    }
    single<QuoteRepository> {
        DefaultQuoteRepository(
            quoteOfTheDayDao = get(),
            quoteBookmarksDao = get(),
            quottieNetwork = get()
        )
    }
    single<SearchRepository> {
        DefaultSearchRepository(recentSearchQueryDao = get())
    }
    single<GithubUserRepository> {
        DefaultGithubUserRepository(githubNetwork = get())
    }
}
