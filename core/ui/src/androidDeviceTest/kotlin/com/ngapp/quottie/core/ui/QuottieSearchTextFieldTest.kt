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

package com.ngapp.quottie.core.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val CLEAR_CONTENT_DESCRIPTION = "Clear search text"

@RunWith(AndroidJUnit4::class)
class QuottieSearchTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTheGivenSearchQuery() {
        composeTestRule.setContent {
            QuottieSearchTextField(
                searchQuery = "plato",
                onSearchQueryChanged = {},
                onSearchTriggered = {},
            )
        }

        composeTestRule.onNodeWithTag("searchTextField").assertTextEquals("plato")
    }

    @Test
    fun typing_invokesOnSearchQueryChanged() {
        var lastQuery: String? = null
        composeTestRule.setContent {
            QuottieSearchTextField(
                searchQuery = "",
                onSearchQueryChanged = { lastQuery = it },
                onSearchTriggered = {},
            )
        }

        composeTestRule.onNodeWithTag("searchTextField").performTextInput("aristotle")

        composeTestRule.waitForIdle()
        assert(lastQuery == "aristotle") { "Expected 'aristotle' but was '$lastQuery'" }
    }

    @Test
    fun clearButton_hiddenWhenQueryIsEmpty() {
        composeTestRule.setContent {
            QuottieSearchTextField(
                searchQuery = "",
                onSearchQueryChanged = {},
                onSearchTriggered = {},
            )
        }

        composeTestRule.onAllNodesWithContentDescription(CLEAR_CONTENT_DESCRIPTION).assertCountEquals(0)
    }

    @Test
    fun clearButton_whenQueryNotEmpty_clearsQueryOnClick() {
        var cleared = false
        composeTestRule.setContent {
            QuottieSearchTextField(
                searchQuery = "plato",
                onSearchQueryChanged = { if (it.isEmpty()) cleared = true },
                onSearchTriggered = {},
            )
        }

        composeTestRule.onNodeWithContentDescription(CLEAR_CONTENT_DESCRIPTION).performClick()

        composeTestRule.waitForIdle()
        assert(cleared) { "Expected onSearchQueryChanged(\"\") to have been called" }
    }
}
