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

package com.ngapp.quottie.core.data.model.githubuser

import com.ngapp.quottie.core.model.githubuser.GithubUserResource
import com.ngapp.quottie.core.network.model.githubuser.NetworkGithubUser

fun NetworkGithubUser.asResource() = GithubUserResource(
    id = id,
    login = login.orEmpty(),
    avatarUrl = avatarUrl.orEmpty(),
    url = url.orEmpty(),
    htmlUrl = htmlUrl.orEmpty(),
    name = name.orEmpty(),
    company = company.orEmpty(),
    blog = blog.orEmpty(),
    location = location,
    email = email.orEmpty(),
    bio = bio.orEmpty(),
    twitterUsername = twitterUsername.orEmpty()
)
