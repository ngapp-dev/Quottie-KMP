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

package com.ngapp.quottie.core.model.githubuser

data class GithubUserResource(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val url: String,
    val htmlUrl: String,
    val name: String,
    val company: String,
    val blog: String,
    val location: String,
    val email: String,
    val bio: String,
    val twitterUsername: String,
) {
    companion object {
        fun init() = GithubUserResource(
            id = 49076456,
            login = "ngapp-dev",
            avatarUrl = "https://avatars.githubusercontent.com/u/49076456?v=4",
            url = "https://api.github.com/users/ngapp-dev",
            htmlUrl = "https://github.com/ngapp-dev",
            name = "NGApps Dev",
            company = "",
            blog = "https://metan.by/",
            location = "Poznan",
            email = "",
            bio = "",
            twitterUsername = "",
        )
    }
}