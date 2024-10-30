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

package com.ngapp.quottie.home.state

import com.ngapp.quottie.core.common.base.BaseAction
import com.ngapp.quottie.core.model.quote.QuoteResource

sealed interface HomeAction : BaseAction {
    data class GetRandomQuotes(val currentQuotes: Set<QuoteResource>, val limit: Int) : HomeAction
    data class UpdateQuoteBookmark(val quote: QuoteResource, val isBookmarked: Boolean) : HomeAction
    data class ShareQuote(val quote: QuoteResource) : HomeAction
    data object RefreshHomePage : HomeAction
}