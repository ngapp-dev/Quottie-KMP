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

import com.ngapp.quottie.core.network.model.githubuser.NetworkGithubUser
import kotlin.test.Test
import kotlin.test.assertEquals

class GithubUserResourceMapperTest {

    @Test
    fun networkGithubUser_asResource_mapsAllFields() {
        val networkUser = NetworkGithubUser(
            login = "ngapp-dev",
            id = 49076456,
            avatarUrl = "avatar",
            url = "url",
            htmlUrl = "html",
            name = "NGApps Dev",
            company = "company",
            blog = "blog",
            location = "Poznan",
            email = "email",
            bio = "bio",
            twitterUsername = "twitter",
        )

        val resource = networkUser.asResource()

        assertEquals(49076456, resource.id)
        assertEquals("ngapp-dev", resource.login)
        assertEquals("avatar", resource.avatarUrl)
        assertEquals("Poznan", resource.location)
        assertEquals("twitter", resource.twitterUsername)
    }

    @Test
    fun networkGithubUser_asResource_withNullableFieldsNull_mapsToEmptyStrings() {
        val networkUser = NetworkGithubUser(
            login = null,
            avatarUrl = null,
            url = null,
            htmlUrl = null,
            name = null,
            company = null,
            blog = null,
            email = null,
            bio = null,
            twitterUsername = null,
        )

        val resource = networkUser.asResource()

        assertEquals("", resource.login)
        assertEquals("", resource.avatarUrl)
        assertEquals("", resource.url)
        assertEquals("", resource.htmlUrl)
        assertEquals("", resource.name)
        assertEquals("", resource.company)
        assertEquals("", resource.blog)
        assertEquals("", resource.email)
        assertEquals("", resource.bio)
        assertEquals("", resource.twitterUsername)
    }
}
