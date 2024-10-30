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

import android.content.Intent
import com.ngapp.quottie.core.model.author.AuthorResource
import com.ngapp.quottie.core.model.quote.QuoteResource

actual class ShareManager {

    private val context = requireNotNull(UiAndroidPlatformContextProvider.context)

    actual fun shareAuthor(author: AuthorResource) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, "${author.name} - Share via Quottie")
            putExtra(
                Intent.EXTRA_TEXT,
                "Author's quotes by Quottie:\n" +
                        "${author.name}\n" +
                        "${author.description}\n" +
                        "Total quotes: ${author.quoteCount}\n\n" +
                        "Share via Quottie\n" +
                        "Get more: https://play.google.com/store/apps/details?id=com.ngapp.quottie"
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    actual fun shareQuote(quote: QuoteResource) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, "${quote.author} - Share via Quottie")
            putExtra(
                Intent.EXTRA_TEXT,
                "\"${quote.content}\"\n" +
                        "- ${quote.author}\n\n" +
                        "Share via Quottie\n" +
                        "Get more: https://play.google.com/store/apps/details?id=com.ngapp.quottie"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }
}

