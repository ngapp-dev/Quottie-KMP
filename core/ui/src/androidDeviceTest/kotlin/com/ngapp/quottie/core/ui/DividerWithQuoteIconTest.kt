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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DividerWithQuoteIconTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun horizontalDivider_showsQuoteIcon() {
        composeTestRule.setContent {
            HorizontalDividerWithQuoteIcon()
        }

        composeTestRule.onNodeWithContentDescription("Quotes").assertIsDisplayed()
    }

    @Test
    fun verticalDivider_showsQuoteIcon() {
        composeTestRule.setContent {
            VerticalDividerWithQuoteIcon()
        }

        composeTestRule.onNodeWithContentDescription("Quotes").assertIsDisplayed()
    }
}
