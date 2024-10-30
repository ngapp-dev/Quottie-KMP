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

import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager {

    actual fun shareAuthor(author: AuthorResource) {
        val message =
            "Author's quotes by Quottie:\n" +
                    "${author.name}\n" +
                    "${author.description}\n" +
                    "Total quotes: ${author.quoteCount}\n\n" +
                    "Share via Quottie\n" +
                    "Get more: https://apps.apple.com/app/id"
                        .trimIndent()

        val activityViewController = UIActivityViewController(
            activityItems = listOf(author.name + " - Share via Quottie", message),
            applicationActivities = null
        )

        val currentViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        currentViewController?.presentViewController(activityViewController, true, null)
    }

    actual fun shareQuote(quote: QuoteResource) {
        val message = "\"${quote.content}\"\n" +
                "- ${quote.author}\n\n" +
                "Share via Quottie\n" +
                "Get more: Get more: https://apps.apple.com/app/id"
                    .trimIndent()

        val activityViewController = UIActivityViewController(
            activityItems = listOf(message),
            applicationActivities = null
        )

        val currentViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        currentViewController?.presentViewController(activityViewController, true, null)
    }
}